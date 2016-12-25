package cofh.core.command;

import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import com.google.common.base.Throwables;

public class CommandReplaceBlock implements ISubCommand {

	public static ISubCommand instance = new CommandReplaceBlock();

	@Override
	public String getCommandName() {

		return "replaceblocks";
	}

	@Override
	public int getPermissionLevel() {

		return 3;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws NumberInvalidException, CommandException {

		if (args.length < 7) {
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
		int yS = CommandBase.parseInt(args[i++]), yL;
		int zS = CommandBase.parseInt(args[i++]), zL;
		int t = i + 1;

		try {
			xL = CommandBase.parseInt(args[i++]);
			yL = CommandBase.parseInt(args[i++]);
			zL = CommandBase.parseInt(args[i++]);
		} catch (Throwable e) {
			if (i > t || center == null) {
				throw Throwables.propagate(e);
			}
			--i;
			xL = xS;
			yL = yS;
			zL = zS;
		}

		if (center != null) {
			xS = center.getX() - xS;
			yS = center.getY() - yS;
			zS = center.getZ() - zS;

			xL = center.getX() + xL;
			yL = center.getY() + yL;
			zL = center.getZ() + zL;
		}

		yS &= ~yS >> 31; // max(yS, 0)
		yL &= ~yL >> 31; // max(yL, 0)

		if (xL < xS) {
			t = xS;
			xS = xL;
			xL = t;
		}
		if (yL < yS) {
			t = yS;
			yS = yL;
			yL = t;
		}
		if (zL < zS) {
			t = zS;
			zS = zL;
			zL = t;
		}

		if (yS > 255) {
			sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
			sender.addChatMessage(new TextComponentTranslation("info.cofh.command." + getCommandName() + ".syntax"));
			return;
		} else if (yL > 255) {
			yL = 255;
		}

		
		String blockReplRaw = args[i++];
		t = blockReplRaw.indexOf('#');
		if (t > 0) {
			blockReplRaw = blockReplRaw.substring(0, t);
		}
		Block replBlock = Block.getBlockFromName(blockReplRaw);
		/*
		{
			String blockRaw = args[i];
			t = blockRaw.indexOf('#');
			if (t > 0) {
				blockRaw = blockRaw.substring(0, t);
			}
			Block block = Block.getBlockFromName(blockRaw);
			if (block == Blocks.AIR) {
				sender.addChatMessage(new TextComponentTranslation("info.cofh.command.syntaxError"));
				sender.addChatMessage(new TextComponentTranslation("info.cofh.command." + getCommandName() + ".syntax"));
				// TODO: more descriptive error
				return;
			} 
		}
		*/
		long blockCounter = ((long) xL - xS) * ((long) yL - yS) * ((long) zL - zS);
		CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.start", blockCounter, xS, yS, zS, xL, yL, zL, blockReplRaw);

		THashSet<UpdateEntry> set = new THashSet<UpdateEntry>();

		blockCounter = 0;
		for (int e = args.length; i < e; ++i) {
			String blockRaw = args[i];
			if (blockRaw.charAt(0) == '*') {
				if (blockRaw.equals("*fluid")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								IBlockState blockstate = chunk.getBlockState(cX, y, cZ);
								if (blockstate.getMaterial().isLiquid()) {
									chunk.setBlockState(new BlockPos(cX, y, cZ), replBlock.getDefaultState());
									++blockCounter;
									set.add(new UpdateEntry(chunk, cX, y, cZ));
								}
							}
						}
					}
				} else if (blockRaw.equals("*tree")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								IBlockState blockstate = chunk.getBlockState(cX, y, cZ);
								if (blockstate.getBlock().isWood(world, new BlockPos(x, y, z)) || blockstate.getBlock().isLeaves(blockstate, world, new BlockPos(x, y, z))) {
									chunk.setBlockState(new BlockPos(cX, y, cZ), replBlock.getDefaultState());
									++blockCounter;
									set.add(new UpdateEntry(chunk, cX, y, cZ));
								}
							}
						}
					}
				} else if (blockRaw.startsWith("*repl")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								IBlockState blockstate = chunk.getBlockState(cX, y, cZ);
								if (blockstate.getBlock().isReplaceable(world, new BlockPos(x, y, z))) {
									chunk.setBlockState(new BlockPos(cX, y, cZ), replBlock.getDefaultState());
									++blockCounter;
									set.add(new UpdateEntry(chunk, cX, y, cZ));
								}
							}
						}
					}
				} else if (blockRaw.equals("*stone")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								IBlockState blockstate = chunk.getBlockState(cX, y, cZ);
								if (blockstate.getBlock().isReplaceableOreGen(blockstate, world, new BlockPos(x, y, z), BlockMatcher.forBlock(Blocks.STONE)) || blockstate.getBlock().isReplaceableOreGen(blockstate, world, new BlockPos(x, y, z), BlockMatcher.forBlock(Blocks.NETHERRACK))
										|| blockstate.getBlock().isReplaceableOreGen(blockstate, world, new BlockPos(x, y, z), BlockMatcher.forBlock(Blocks.END_STONE))) {
									chunk.setBlockState(new BlockPos(cX, y, cZ), replBlock.getDefaultState());
									++blockCounter;
									set.add(new UpdateEntry(chunk, cX, y, cZ));
								}
							}
						}
					}
				} else if (blockRaw.equals("*rock")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								IBlockState blockstate = chunk.getBlockState(cX, y, cZ);
								if (blockstate.getBlock().getMaterial(blockstate) == Material.ROCK) {
									chunk.setBlockState(new BlockPos(cX, y, cZ), replBlock.getDefaultState());
									++blockCounter;
									set.add(new UpdateEntry(chunk, cX, y, cZ));
								}
							}
						}
					}
				} else if (blockRaw.equals("*sand")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								IBlockState blockstate = chunk.getBlockState(cX, y, cZ);
								if (blockstate.getBlock().getMaterial(blockstate) == Material.SAND) {
									chunk.setBlockState(new BlockPos(cX, y, cZ), replBlock.getDefaultState());
									++blockCounter;
									set.add(new UpdateEntry(chunk, cX, y, cZ));
								}
							}
						}
					}
				} else if (blockRaw.equals("*dirt")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								IBlockState blockstate = chunk.getBlockState(cX, y, cZ);
								Material m = blockstate.getBlock().getMaterial(blockstate);
								if (m == Material.GRASS || m == Material.GROUND || m == Material.CLAY || m == Material.SNOW || m == Material.CRAFTED_SNOW
										|| m == Material.ICE || m == Material.PACKED_ICE) {
									chunk.setBlockState(new BlockPos(cX, y, cZ), replBlock.getDefaultState());
									++blockCounter;
									set.add(new UpdateEntry(chunk, cX, y, cZ));
								}
							}
						}
					}
				} else if (blockRaw.startsWith("*plant")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								IBlockState blockstate = chunk.getBlockState(cX, y, cZ);
								Material m = blockstate.getBlock().getMaterial(blockstate);
								if (m == Material.PLANTS || m == Material.VINE || m == Material.CACTUS || m == Material.LEAVES) {
									chunk.setBlockState(new BlockPos(cX, y, cZ), replBlock.getDefaultState());
									++blockCounter;
									set.add(new UpdateEntry(chunk, cX, y, cZ));
								}
							}
						}
					}
				} else if (blockRaw.equals("*fire")) {
					for (int x = xS; x <= xL; ++x) {
						for (int z = zS; z <= zL; ++z) {
							Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
							int cX = x & 15, cZ = z & 15;
							for (int y = yS; y <= yL; ++y) {
								IBlockState blockstate = chunk.getBlockState(cX, y, cZ);
								Material m = blockstate.getBlock().getMaterial(blockstate);
								if (m == Material.FIRE || m == Material.LAVA || blockstate.getBlock().isBurning(world, new BlockPos(x, y, z))) {
									chunk.setBlockState(new BlockPos(cX, y, cZ), replBlock.getDefaultState());
									++blockCounter;
									set.add(new UpdateEntry(chunk, cX, y, cZ));
								}
							}
						}
					}
				}
				continue;
			}
			int meta = -1;
			t = blockRaw.indexOf('#');
			if (t > 0) {
				meta = CommandBase.parseInt(blockRaw.substring(t + 1));
				blockRaw = blockRaw.substring(0, t);
			}
			Block block = Block.getBlockFromName(blockRaw);
			if (block == Blocks.AIR) {
				continue;
			}

			for (int x = xS; x <= xL; ++x) {
				for (int z = zS; z <= zL; ++z) {
					Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
					int cX = x & 15, cZ = z & 15;
					for (int y = yS; y <= yL; ++y) {
						boolean v = meta == -1 || chunk.getBlockState(new BlockPos(cX, y, cZ)).getBlock().getMetaFromState(chunk.getBlockState(new BlockPos(cX, y, cZ))) == meta;
						if (v && chunk.getBlockState(cX, y, cZ).getBlock() == block) {
							chunk.setBlockState(new BlockPos(cX, y, cZ), replBlock.getDefaultState());
							++blockCounter;
							set.add(new UpdateEntry(chunk, cX, y, cZ));
						}
					}
				}
			}
		}
		if (!set.isEmpty()) {
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.success", blockCounter, xS, yS, zS, xL, yL, zL, blockReplRaw);
		} else {
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.replaceblocks.failure");
		}

		if (world instanceof WorldServer) {
			TObjectHashIterator<UpdateEntry> c = set.iterator();
			for (int k = 0, e = set.size(); k < e; ++k) {
				UpdateEntry entry = c.next();
				PlayerChunkMap manager = ((WorldServer) world).getPlayerChunkMap();
				if (manager == null) {
					return;
				}
				PlayerChunkMapEntry watcher = manager.getEntry(entry.chunk.xPosition, entry.chunk.zPosition);
				if (watcher != null) {
					watcher.blockChanged(entry.x, entry.y, entry.z);
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
		if (args.length >= 2){
			try {
				Integer.parseInt(args[1]);
				if (args.length >= 8) {
					return CommandBase.getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
				}
			} catch (NumberFormatException e) {
				if (args.length >= 6) {
					return CommandBase.getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
				}
			}
		}
		return new ArrayList<String>();
	}
	public static class UpdateEntry {
		public final Chunk chunk;
		public final int x, y, z;
		
		public UpdateEntry(Chunk chunk, int x, int y, int z) {
			this.chunk = chunk;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

}
