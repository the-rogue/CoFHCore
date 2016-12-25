package cofh.core.command;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.DimensionManager;
import cofh.lib.util.helpers.EntityHelper;

public class CommandTPX implements ISubCommand {

	public static CommandTPX instance = new CommandTPX();

	@Override
	public String getCommandName() {

		return "tpx";
	}

	@Override
	public int getPermissionLevel() {

		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException {

		// TODO: allow selector commands to target anything (single player, all players[@a], specific entities [@e], etc.)
		// where it makes sense to allow it (e.g., not allowing teleporting a single thing to many things)

		switch (arguments.length) {

		case 0: // () ???? how did we get here again?
		case 1: // (tpx) invalid command
			sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
			throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
		case 2: // (tpx {<player>|<dimension>}) teleporting player to self, or self to dimension
			EntityPlayerMP playerSender = CommandBase.getCommandSenderAsPlayer(sender);
			try {
				EntityPlayerMP player = CommandBase.getPlayer(server, sender, arguments[1]);
				if (!player.equals(playerSender)) {
					player.dismountRidingEntity();
					if (playerSender.dimension == player.dimension) {
						player.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
						CommandHandler.logAdminCommand(sender, this, "info.cofh.command.tpx.otherToSelf", player.getGameProfile().getName(), player.posX,
								player.posY, player.posZ);
					} else {
						EntityHelper.transferPlayerToDimension(player, playerSender.dimension, playerSender.mcServer.getPlayerList());
						player.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
						CommandHandler.logAdminCommand(sender, this, "info.cofh.command.tpx.dimensionOtherToSelf", player.getGameProfile().getName(),
								player.worldObj.provider.getDimensionType().getName(), player.posX, player.posY, player.posZ);
					}
				} else {
					sender.addChatMessage(new TextComponentTranslation("info.cofh.command.tpx.snark.0"));
				}
				break;
			} catch (PlayerNotFoundException t) {
				int dimension = 0;
				try {
					dimension = CommandBase.parseInt(arguments[1]);
				} catch (CommandException p) { // not a number, assume they wanted a player
					sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
					sender.addChatMessage(new TextComponentTranslation("info.cofh.command." + getCommandName() + ".syntax"));
					throw t;
				}
				if (!DimensionManager.isDimensionRegistered(dimension)) {
					throw new CommandException("info.cofh.command.world.notFound");
				}
				playerSender.dismountRidingEntity();
				if (playerSender.dimension != dimension) {
					EntityHelper.transferPlayerToDimension(playerSender, dimension, playerSender.mcServer.getPlayerList());
				}
				playerSender.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
				CommandHandler.logAdminCommand(sender, this, "info.cofh.command.tpx.dimensionSelf", playerSender.worldObj.provider.getDimensionType().getName(),
						playerSender.posX, playerSender.posY, playerSender.posZ);
			}
			break;
		case 3: // (tpx <player> {<player>|<dimension>}) teleporting player to player or player to dimension
			EntityPlayerMP player = CommandBase.getPlayer(server, sender, arguments[1]);
			try {
				EntityPlayerMP otherPlayer = CommandBase.getPlayer(server, sender, arguments[2]);
				if (!player.equals(otherPlayer)) {
					player.dismountRidingEntity();
					if (otherPlayer.dimension == player.dimension) {
						player.setPositionAndUpdate(otherPlayer.posX, otherPlayer.posY, otherPlayer.posZ);
						CommandHandler.logAdminCommand(sender, this, "info.cofh.command.tpx.otherTo", player.getGameProfile().getName(),
								otherPlayer.getGameProfile().getName(), player.posX, player.posY, player.posZ);
					} else {
						EntityHelper.transferPlayerToDimension(player, otherPlayer.dimension, otherPlayer.mcServer.getPlayerList());
						player.setPositionAndUpdate(otherPlayer.posX, otherPlayer.posY, otherPlayer.posZ);
						CommandHandler.logAdminCommand(sender, this, "info.cofh.command.tpx.dimensionOtherTo", player.getGameProfile().getName(),
								otherPlayer.getGameProfile().getName(), player.worldObj.provider.getDimensionType().getName(), player.posX, player.posY, player.posZ);
					}
				} else {
					sender.addChatMessage(new TextComponentTranslation("info.cofh.command.tpx.snark.1", arguments[1]));
				}
				break;
			} catch (PlayerNotFoundException t) {
				int dimension = 0;
				try {
					dimension = CommandBase.parseInt(arguments[2]);
				} catch (CommandException p) { // not a number, assume they wanted a player
					sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
					sender.addChatMessage(new TextComponentTranslation("info.cofh.command." + getCommandName() + ".syntax"));
					throw t;
				}
				if (!DimensionManager.isDimensionRegistered(dimension)) {
					throw new CommandException("info.cofh.command.world.notFound");
				}
				player.dismountRidingEntity();
				if (player.dimension != dimension) {
					EntityHelper.transferPlayerToDimension(player, dimension, player.mcServer.getPlayerList());
				}
				player.setPositionAndUpdate(player.posX, player.posY, player.posZ);
				CommandHandler.logAdminCommand(sender, this, "info.cofh.command.tpx.dimensionOther", player.getGameProfile().getName(),
						player.worldObj.provider.getDimensionType().getName(), player.posX, player.posY, player.posZ);
			}
			break;
		case 4: // (tpx <x> <y> <z>) teleporting self within dimension
			playerSender = CommandBase.getCommandSenderAsPlayer(sender);
			playerSender.setPositionAndUpdate(CommandBase.parseCoordinate(playerSender.posX, arguments[1], true).getAmount(),
					CommandBase.parseCoordinate(playerSender.posY, arguments[2], true).getAmount(),
					CommandBase.parseCoordinate(playerSender.posZ, arguments[3], true).getAmount());
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.tpx.self", playerSender.posX, playerSender.posY, playerSender.posZ);
			break;
		case 5: // (tpx {<player> <x> <y> <z> | <x> <y> <z> <dimension>}) teleporting player within player's dimension orself to dimension
			try {
				player = CommandBase.getPlayer(server, sender, arguments[1]);
				player.dismountRidingEntity();
				player.setPositionAndUpdate(CommandBase.parseCoordinate(player.posX, arguments[2], true).getAmount(),
						CommandBase.parseCoordinate(player.posY, arguments[3], true).getAmount(), CommandBase.parseCoordinate(player.posZ, arguments[4], true).getAmount());
				CommandHandler.logAdminCommand(sender, this, "info.cofh.command.tpx.other", player.getGameProfile().getName(), player.posX, player.posY,
						player.posZ);
			} catch (PlayerNotFoundException t) {
				int dimension;
				try {
					dimension = CommandBase.parseInt(arguments[4]);
				} catch (CommandException p) {
					sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
					sender.addChatMessage(new TextComponentTranslation("info.cofh.command." + getCommandName() + ".syntax"));
					throw t;
				}
				playerSender = CommandBase.getCommandSenderAsPlayer(sender);
				if (!DimensionManager.isDimensionRegistered(dimension)) {
					throw new CommandException("info.cofh.command.world.notFound");
				}
				playerSender.dismountRidingEntity();
				if (playerSender.dimension != dimension) {
					EntityHelper.transferPlayerToDimension(playerSender, dimension, playerSender.mcServer.getPlayerList());
				}
				playerSender.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
				CommandHandler.logAdminCommand(sender, this, "info.cofh.command.tpx.dimensionSelf", playerSender.worldObj.provider.getDimensionType().getName(),
						playerSender.posX, playerSender.posY, playerSender.posZ);
			}
			break;
		case 6: // (tpx <player> <x> <y> <z> <dimension>) teleporting player to dimension and location
		default: // ignore excess tokens. warn?
			player = CommandBase.getPlayer(server, sender, arguments[1]);
			int dimension = CommandBase.parseInt(arguments[5]);

			if (!DimensionManager.isDimensionRegistered(dimension)) {
				throw new CommandException("info.cofh.command.world.notFound");
			}
			player.dismountRidingEntity();
			if (player.dimension != dimension) {
				EntityHelper.transferPlayerToDimension(player, dimension, player.mcServer.getPlayerList());
			}
			player.setPositionAndUpdate(CommandBase.parseCoordinate(player.posX, arguments[2], true).getAmount(),
					CommandBase.parseCoordinate(player.posY, arguments[3], true).getAmount(), CommandBase.parseCoordinate(player.posZ, arguments[4], true).getAmount());
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.tpx.dimensionOther", player.getGameProfile().getName(),
					player.worldObj.provider.getDimensionType().getName(), player.posX, player.posY, player.posZ);
			break;
		}
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {

		if (args.length == 2 || args.length == 3) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
		} else if (args.length >= 6) {
			Integer[] ids = DimensionManager.getIDs();
			String[] strings = new String[ids.length];

			for (int i = 0; i < ids.length; i++) {
				strings[i] = ids[i].toString();
			}
			return CommandBase.getListOfStringsMatchingLastWord(args, strings);
		}

		return new ArrayList<String>();

	}

}
