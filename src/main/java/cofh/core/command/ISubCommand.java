package cofh.core.command;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public interface ISubCommand {

	public int getPermissionLevel();

	public String getCommandName();

	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] arguments) throws NumberInvalidException, CommandException;

	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos);

}
