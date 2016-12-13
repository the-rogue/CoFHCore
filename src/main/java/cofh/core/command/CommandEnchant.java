package cofh.core.command;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandEnchant implements ISubCommand {

	public static ISubCommand instance = new CommandEnchant();

	@Override
	public String getCommandName() {

		return "enchant";
	}

	@Override
	public int getPermissionLevel() {

		return 2;
	}

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws NumberInvalidException, CommandException {

		int l = args.length;
		int i = 1;
		EntityPlayerMP player = null;
		switch (l) {

		case 0:
		case 1:
			sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
			try
			{
				throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
			}
			catch (WrongUsageException e)
			{
			}
		default:
		case 4:
		case 3:
			try {
				player = CommandBase.getPlayer(server, sender, args[i++]);
			} catch (CommandException t) {
				if (l != 3) {
					sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
					sender.addChatMessage(new TextComponentTranslation("info.cofh.command." + getCommandName() + ".syntax"));
						throw t;
				}
				--i;
			}
		case 2:
			if (player == null) {
				try
				{
					player = CommandBase.getCommandSenderAsPlayer(sender);
				}
				catch (PlayerNotFoundException e)
				{
				}
			}
			int id = CommandBase.parseInt(args[i++]);
			int level = 1;
			ItemStack itemstack = player.inventory.getCurrentItem();

			if (itemstack == null) {
				throw new CommandException("commands.enchant.noItem", new Object[0]);
			} else {
				Enchantment enchantment = Enchantment.getEnchantmentByID(id);

				if (enchantment == null) {
					throw new NumberInvalidException("commands.enchant.notFound", new Object[] { Integer.valueOf(id) });
				}
				if (i < l) {
					level = CommandBase.parseInt(args[i++]);
				}

				itemstack.addEnchantment(enchantment, level);
				CommandHandler.logAdminCommand(sender, this, "commands.enchant.success");
			}
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
