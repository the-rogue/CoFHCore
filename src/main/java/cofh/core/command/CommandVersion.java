package cofh.core.command;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import cofh.CoFHCore;
import cofh.lib.util.helpers.StringHelper;

public class CommandVersion implements ISubCommand {

	public static CommandVersion instance = new CommandVersion();

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "version";
	}

	@Override
	public int getPermissionLevel() {

		return -1;
	}

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] arguments) {

		sender.addChatMessage(new TextComponentString(StringHelper.localize("info.cofh.command.version.0") + " " + CoFHCore.version + "."));
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {

		return null;
	}

}
