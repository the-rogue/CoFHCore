package cofh.core.command;

import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import com.google.common.base.Throwables;

public class CommandFixMojangsShit implements ISubCommand {

	public static ISubCommand instance = new CommandFixMojangsShit();

	@Override
	public String getCommandName() {

		return "updatechests";
	}

	@Override
	public int getPermissionLevel() {

		return 4;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws NumberInvalidException, CommandException {

		if (args.length < 3) {
			sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
			throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
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
				center = CommandBase.getPlayer(server ,sender, args[i - 1]).getPosition();
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
			xS = center.getX() - xS;
			zS = center.getZ() - zS;

			xL = center.getX() + xL;
			zL = center.getZ() + zL;
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

		int yS = 0, yL = 255;

		long blockCounter = ((long) xL - xS) * ((long) yL - yS) * ((long) zL - zS);
		CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.start", blockCounter, xS, yS, zS, xL, yL, zL, "chest");

		THashSet<Chunk> set = new THashSet<Chunk>();

		blockCounter = 0;
		Block block = Blocks.CHEST;
		try {
			for (int x = xS; x <= xL; ++x) {
				for (int z = zS; z <= zL; ++z) {
					Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
					int cX = x & 15, cZ = z & 15;
					for (int y = yS; y <= yL; ++y) {
						if (chunk.getBlockState(new BlockPos(cX, y, cZ)).getBlock().getMetaFromState(chunk.getBlockState(new BlockPos(cX, y, cZ))) < 2 && chunk.getBlockState(cX, y, cZ).getBlock() == block) {
							TileEntity tile = chunk.getTileEntity(new BlockPos(cX, y, cZ), Chunk.EnumCreateEntityType.IMMEDIATE);
							NBTTagCompound tag = new NBTTagCompound();
							tile.writeToNBT(tag);
							chunk.removeTileEntity(new BlockPos(cX, y, cZ));
							if (!(chunk.setBlockState(new BlockPos(cX, y, cZ), Blocks.AIR.getDefaultState())).equals(null)) {
								++blockCounter;
								chunk.getTileEntity(new BlockPos(cX, y, cZ), Chunk.EnumCreateEntityType.IMMEDIATE).readFromNBT(tag);
								set.add(chunk);
							}
						}
					}
				}
			}
		} catch (Throwable e) {
			Throwables.propagate(e);
		}
		if (!set.isEmpty()) {
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.success", blockCounter, xS, yS, zS, xL, yL, zL, "chest");
		} else {
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.failure");
		}

		if (world instanceof WorldServer) {
			TObjectHashIterator<Chunk> c = set.iterator();
			for (int k = 0, e = set.size(); k < e; ++k) {
				Chunk chunk = c.next();
				PlayerChunkMap manager = ((WorldServer) world).getPlayerChunkMap();
				if (manager == null) {
					return;
				}
				PlayerChunkMapEntry watcher = manager.getEntry(chunk.xPosition, chunk.zPosition);
				if (watcher != null) {
					watcher.sentToPlayers();
				}
			}
		}
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
		}
		return new ArrayList<String>();
	}
}
