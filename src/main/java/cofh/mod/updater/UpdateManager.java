package cofh.mod.updater;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cofh.core.CoFHProps;

import com.google.common.base.Strings;

public final class UpdateManager {

	private static transient int pollOffset = 0;
	private static final Style description = new Style();
	private static final Style version = new Style();
	private static final Style modname = new Style();
	private static final Style download = new Style();
	private static final Style white = new Style();
	static {

		description.setColor(TextFormatting.GRAY);
		version.setColor(TextFormatting.AQUA);
		modname.setColor(TextFormatting.GOLD);
		download.setColor(TextFormatting.GREEN);
		white.setColor(TextFormatting.WHITE);

		{
			Style tooltip = new Style();
			tooltip.setColor(TextFormatting.YELLOW);
			ITextComponent msg = new TextComponentTranslation("info.cofh.updater.tooltip").setStyle(tooltip);
			download.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, msg));
		}
	}

	public static void registerUpdater(UpdateManager manager) {

		MinecraftForge.EVENT_BUS.register(manager);
	}

	private boolean _notificationDisplayed;
	private final IUpdatableMod _mod;
	private final UpdateCheckThread _updateThread;
	private final String _downloadUrl;
	private int lastPoll = 400;

	public UpdateManager(IUpdatableMod mod) {

		this(mod, null);
	}

	public UpdateManager(IUpdatableMod mod, String releaseUrl) {

		this(mod, releaseUrl, null);
	}

	public UpdateManager(IUpdatableMod mod, String releaseUrl, String downloadUrl) {

		_mod = mod;
		_updateThread = new UpdateCheckThread(mod, releaseUrl, downloadUrl);
		_updateThread.start();
		_downloadUrl = downloadUrl;
		lastPoll += (pollOffset += 140);
	}

	@SubscribeEvent
	public void tickStart(PlayerTickEvent evt) {

		if (evt.phase != Phase.START) {
			return;
		}
		MinecraftServer theserver = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (theserver != null && theserver.isServerRunning()) {
			if (!theserver.getPlayerList().canSendCommands(evt.player.getGameProfile())) {
				return;
			}
		}
		if (lastPoll > 0) {
			--lastPoll;
			return;
		}
		lastPoll = 400;

		if (!_notificationDisplayed && _updateThread.checkComplete()) {
			_notificationDisplayed = true;
			MinecraftForge.EVENT_BUS.unregister(this);
			if (_updateThread.newVersionAvailable()) {
				if (!CoFHProps.enableUpdateNotice && !_updateThread.isCriticalUpdate()) {
					return;
				}
				ModVersion newVersion = _updateThread.newVersion();

				EntityPlayer player = evt.player;
				ITextComponent chat = new TextComponentString("");
				{
					Style data = modname.createShallowCopy();
					ITextComponent msg = new TextComponentString(newVersion.modVersion().toString()).setStyle(version);
					data.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, msg));
					chat.appendSibling(new TextComponentString("[" + _mod.getModName() + "] ").setStyle(data));
				}
				chat.appendSibling(new TextComponentTranslation("info.cofh.updater.version").setStyle(white));
				chat.appendText(TextFormatting.GOLD + ":");
				player.addChatMessage(chat);
				chat = new TextComponentString("");
				if (!Strings.isNullOrEmpty(_downloadUrl)) {
					chat.appendText(TextFormatting.WHITE + "[");
					Style data = download.createShallowCopy();
					data.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, _downloadUrl));
					chat.appendSibling(new TextComponentTranslation("info.cofh.updater.download").setStyle(data));
					chat.appendText(TextFormatting.WHITE + "] ");
				}
				chat.appendSibling(new TextComponentString(newVersion.description()).setStyle(description));
				player.addChatMessage(chat);
			}
		}
	}

}
