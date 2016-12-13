package cofh.core.command;

import java.util.ArrayDeque;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import cofh.core.world.TickHandlerWorld;
import cofh.lib.util.position.ChunkCoord;

import com.google.common.base.Throwables;

public class CommandPregen implements ISubCommand {

	public static ISubCommand instance = new CommandPregen();

	@Override
	public String getCommandName() {

		return "pregen";
	}

	@Override
	public int getPermissionLevel() {

		return 4;
	}

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws NumberInvalidException {

		if (args.length < 4) {
			sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
			try
			{
				throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
			}
			catch (WrongUsageException e)
			{
			}
		}
		World world = sender.getEntityWorld();
		if (world.isRemote) {
			return;
		}

		BlockPos center = null;
		int i = 1;
		int xS, xL;
		if ("@".equals(args[i])) {
			center = sender.getPosition();
			++i;
			xS = CommandBase.parseInt(args[i++]);
		} else {
			try {
				xS = CommandBase.parseInt(args[i++]);
			} catch (Throwable t) {
				try
				{
					center = CommandBase.getPlayer(server, sender, args[i - 1]).getPosition();
				}
				catch (PlayerNotFoundException e)
				{
				}
				xS = CommandBase.parseInt(args[i++]);
			}
		}
		int zS = CommandBase.parseInt(args[i++]), zL;
		int t = i + 1;

		try {
			xL = CommandBase.parseInt(args[i++]);
			zL = CommandBase.parseInt(args[i++]);
		} catch (Throwable e) {
			if (i > t || center == null) {
				throw Throwables.propagate(e);
			}
			--i;
			xL = xS;
			zL = zS;
		}

		if (center != null) {
			xS = (center.getX() / 16) - xS;
			zS = (center.getZ() / 16) - zS;

			xL = (center.getX() / 16) + xL;
			zL = (center.getZ() / 16) + zL;
		}

		if (xL < xS) {
			t = xS;
			xS = xL;
			xL = t;
		}
		if (zL < zS) {
			t = zS;
			zS = zL;
			zL = t;
		}

		synchronized (TickHandlerWorld.chunksToPreGen) {
			ArrayDeque<ChunkCoord> chunks = TickHandlerWorld.chunksToPreGen.get(world.provider.getDimension());
			if (chunks == null) {
				chunks = new ArrayDeque<ChunkCoord>();
			}

			for (int x = xS; x <= xL; ++x) {
				for (int z = zS; z <= zL; ++z) {
					chunks.addLast(new ChunkCoord(x, z));
				}
			}
			TickHandlerWorld.chunksToPreGen.put(world.provider.getDimension(), chunks);
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.pregen.start", (xL - xS) * (zL - zS), xS, zS, xL, zL);
		}
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
		}
		return null;
	}

}
