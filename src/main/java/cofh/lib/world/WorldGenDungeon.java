package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.canGenerateInBlock;
import static cofh.lib.world.WorldGenMinableCluster.generateBlock;
import static java.lang.Math.abs;

import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.WeightedRandomNBTTag;

public class WorldGenDungeon extends WorldGenerator {

	private final List<WeightedRandomBlock> walls;
	private final WeightedRandomBlock[] genBlock;
	private final List<WeightedRandomNBTTag> spawners;
	public int minWidthX = 2, maxWidthX = 3;
	public int minWidthZ = 2, maxWidthZ = 3;
	public int minHeight = 3, maxHeight = 3;
	public int minHoles = 1, maxHoles = 5;
	public int maxChests = 2, maxChestTries = 3;
	public ResourceLocation lootTable;
	public List<WeightedRandomBlock> floor;

	public WorldGenDungeon(List<WeightedRandomBlock> blocks, List<WeightedRandomBlock> material, List<WeightedRandomNBTTag> mobs) {

		walls = blocks;
		floor = walls;
		spawners = mobs;
		genBlock = material.toArray(new WeightedRandomBlock[material.size()]);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {

		if (pos.getY() <= 2) {
			return false;
		}

		int height = nextInt(rand, maxHeight - minHeight + 1) + minHeight;
		int xWidth = nextInt(rand, maxWidthX - minWidthX + 1) + minWidthX;
		int zWidth = nextInt(rand, maxWidthZ - minWidthZ + 1) + minWidthZ;
		int holes = 0;
		int x, y, z;

		int floor = pos.getY() - 1, ceiling = pos.getY() + height + 1;

		for (x = pos.getX() - xWidth - 1; x <= pos.getX() + xWidth + 1; ++x) {
			for (z = pos.getZ() - zWidth - 1; z <= pos.getZ() + zWidth + 1; ++z) {
				for (y = floor; y <= ceiling; ++y) {

					if (y == floor && !canGenerateInBlock(world, new BlockPos(x, y, z), genBlock)) {
						return false;
					}

					if (y == ceiling && !canGenerateInBlock(world, new BlockPos(x, y, z), genBlock)) {
						return false;
					}

					if ((abs(x - pos.getX()) == xWidth + 1 || abs(z - pos.getZ()) == zWidth + 1) && y == pos.getY() && world.isAirBlock(new BlockPos(x, y, z))
							&& world.isAirBlock(new BlockPos(x, y + 1, z))) {
						++holes;
					}
				}
			}
		}

		if (holes < minHoles || holes > maxHoles) {
			return false;
		}

		NBTTagCompound tag = (NBTTagCompound) ((WeightedRandomNBTTag) WeightedRandom.getRandomItem(rand, spawners)).tag;

		for (x = pos.getX() - xWidth - 1; x <= pos.getX() + xWidth + 1; ++x) {
			for (z = pos.getZ() - zWidth - 1; z <= pos.getZ() + zWidth + 1; ++z) {
				for (y = pos.getY() + height; y >= floor; --y) {

					l: if (y != floor) {
						if ((abs(x - pos.getX()) != xWidth + 1 && abs(z - pos.getZ()) != zWidth + 1)) {
							world.setBlockToAir(new BlockPos(x, y, z));
						} else if (y >= 0 && !canGenerateInBlock(world, new BlockPos(x, y - 1, z), genBlock)) {
							world.setBlockToAir(new BlockPos(x, y, z));
						} else {
							break l;
						}
						continue;
					}
					if (canGenerateInBlock(world, new BlockPos(x, y, z), genBlock)) {
						if (y == floor) {
							generateBlock(world, new BlockPos(x, y, z), this.floor);
						} else {
							generateBlock(world, new BlockPos(x, y, z), walls);
						}
					}
				}
			}
		}

		for (int i = maxChests; i-- > 0;) {
			for (int j = maxChestTries; j-- > 0;) {
				x = pos.getX() + nextInt(rand, xWidth * 2 + 1) - xWidth;
				z = pos.getZ() + nextInt(rand, zWidth * 2 + 1) - zWidth;

				if (world.isAirBlock(new BlockPos(x, pos.getY(), z))) {
					int walls = 0;

					if (isWall(world, x - 1, pos.getY(), z)) {
						++walls;
					}

					if (isWall(world, x + 1, pos.getY(), z)) {
						++walls;
					}

					if (isWall(world, x, pos.getY(), z - 1)) {
						++walls;
					}

					if (isWall(world, x, pos.getY(), z + 1)) {
						++walls;
					}

					if (walls >= 1 && walls <= 2) {
						world.setBlockState(new BlockPos(x, pos.getY(), z), Blocks.CHEST.getDefaultState(), 2);
						TileEntityChest chest = (TileEntityChest) world.getTileEntity(new BlockPos(x, pos.getY(), z));

						if (chest != null) {
							chest.setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
						}

						break;
					}
				}
			}
		}

		world.setBlockState(pos, Blocks.MOB_SPAWNER.getDefaultState(), 2);
		TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(pos);

		if (spawner != null) {
			spawner.getSpawnerBaseLogic().readFromNBT(tag);
		} else {
			System.err.println("Failed to fetch mob spawner entity at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");
		}

		return true;
	}

	private static int nextInt(Random rand, int v) {

		if (v <= 1) {
			return 0;
		}
		return rand.nextInt(v);
	}

	private boolean isWall(World world, int x, int y, int z) {

		IBlockState blockstate = world.getBlockState(new BlockPos(x, y, z));
		return WeightedRandomBlock.isBlockContained(blockstate.getBlock(), blockstate.getBlock().getMetaFromState(blockstate), walls);
	}

}
