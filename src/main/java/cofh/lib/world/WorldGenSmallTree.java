package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.canGenerateInBlock;
import static cofh.lib.world.WorldGenMinableCluster.generateBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import cofh.lib.util.WeightedRandomBlock;

public class WorldGenSmallTree extends WorldGenerator {

	private final List<WeightedRandomBlock> leaves;
	private final List<WeightedRandomBlock> trunk;
	private final WeightedRandomBlock[] genBlock;

	public WeightedRandomBlock[] genSurface = null;
	public int minHeight = 5;
	public int heightVariance = 3;
	public boolean treeChecks = true;
	public boolean leafVariance = true;
	public boolean relaxedGrowth = false;
	public boolean waterLoving = false;

	public WorldGenSmallTree(List<WeightedRandomBlock> resource, List<WeightedRandomBlock> leaf, List<WeightedRandomBlock> block) {

		trunk = resource;
		leaves = leaf;
		genBlock = block.toArray(new WeightedRandomBlock[block.size()]);
	}

	protected int getLeafRadius(int height, int level, boolean check) {

		if (check) {
			if (level >= 1 + height - 2) {
				return 2;
			} else {
				return relaxedGrowth ? 0 : 1;
			}
		}

		if (level >= 1 + height - 4) {
			return 1 - ((level - height) / 2);
		} else {
			return 0;
		}
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {

		int treeHeight = (heightVariance <= 1 ? 0 : rand.nextInt(heightVariance)) + minHeight;
		int worldHeight = world.getHeight();
		IBlockState blockstate;
		Block block;

		if (pos.getY() + treeHeight + 1 <= worldHeight) {
			int xOffset;
			int yOffset;
			int zOffset;

			if (!canGenerateInBlock(world, new BlockPos(pos).add(0, -1, 0), genSurface)) {
				return false;
			}

			if (pos.getY() < worldHeight - treeHeight - 1) {
				if (treeChecks) {
					for (yOffset = pos.getY(); yOffset <= pos.getY() + 1 + treeHeight; ++yOffset) {

						int radius = getLeafRadius(treeHeight, yOffset - pos.getY(), true);

						if (yOffset >= 0 & yOffset < worldHeight) {
							if (radius == 0) {
								blockstate = world.getBlockState(new BlockPos(pos.getX(), yOffset, pos.getZ()));
								block = blockstate.getBlock();
								if (!(block.isLeaves(blockstate, world, new BlockPos(pos.getX(), yOffset, pos.getZ())) || block.isAir(blockstate, world, new BlockPos(pos.getX(), yOffset, pos.getZ())) || block.isReplaceable(world, new BlockPos(pos.getX(), yOffset, pos.getZ()))
										|| block.canBeReplacedByLeaves(blockstate, world, new BlockPos(pos.getX(), yOffset, pos.getZ())) || canGenerateInBlock(world, new BlockPos(pos.getX(), yOffset, pos.getZ()), genBlock))) {
									return false;
								}

								if (!waterLoving && yOffset >= pos.getY() + 1) {
									radius = 1;
									for (xOffset = pos.getX() - radius; xOffset <= pos.getX() + radius; ++xOffset) {
										for (zOffset = pos.getZ() - radius; zOffset <= pos.getZ() + radius; ++zOffset) {
											blockstate = world.getBlockState(new BlockPos(xOffset, yOffset, zOffset));
											block = blockstate.getBlock();

											if (blockstate.getMaterial().isLiquid()) {
												return false;
											}
										}
									}
								}
							} else {
								for (xOffset = pos.getX() - radius; xOffset <= pos.getX() + radius; ++xOffset) {
									for (zOffset = pos.getZ() - radius; zOffset <= pos.getZ() + radius; ++zOffset) {
										blockstate = world.getBlockState(new BlockPos(xOffset, yOffset, zOffset));
										block = blockstate.getBlock();

										if (!(block.isLeaves(blockstate, world, new BlockPos(xOffset, yOffset, zOffset))) || block.isAir(blockstate, world, new BlockPos(xOffset, yOffset, zOffset))
												|| block.canBeReplacedByLeaves(blockstate, world, new BlockPos(xOffset, yOffset, zOffset)) || canGenerateInBlock(world, new BlockPos(xOffset, yOffset,
													zOffset), genBlock)) {
											return false;
										}
									}
								}
							}
						} else {
							return false;
						}
					}

					if (genSurface != null && !canGenerateInBlock(world, new BlockPos(pos).add(0, -1, 0), genSurface)) {
						return false;
					}
					blockstate = world.getBlockState(new BlockPos(pos).add(0, -1, 0));
					block = blockstate.getBlock();
					block.onPlantGrow(blockstate, world, new BlockPos(pos).add(0, -1, 0), pos);
				}

				boolean r = false;

				for (yOffset = pos.getY(); yOffset <= pos.getY() + treeHeight; ++yOffset) {

					int var12 = yOffset - (pos.getY() + treeHeight);
					int radius = getLeafRadius(treeHeight, yOffset - pos.getY(), false);
					if (radius <= 0) {
						continue;
					}

					for (xOffset = pos.getX() - radius; xOffset <= pos.getX() + radius; ++xOffset) {
						int xPos = xOffset - pos.getX(), t;
						xPos = (xPos + (t = xPos >> 31)) ^ t;

						for (zOffset = pos.getZ() - radius; zOffset <= pos.getZ() + radius; ++zOffset) {
							int zPos = zOffset - pos.getZ();
							zPos = (zPos + (t = zPos >> 31)) ^ t;

							blockstate = world.getBlockState(new BlockPos(xOffset, yOffset, zOffset));
							block = blockstate.getBlock();

							if (((xPos != radius | zPos != radius) || (!leafVariance || (rand.nextInt(2) != 0 && var12 != 0)))
									&& ((treeChecks ? block.isLeaves(blockstate, world, new BlockPos(xOffset, yOffset, zOffset)) || block.isAir(blockstate, world, new BlockPos(xOffset, yOffset, zOffset))
											|| block.canBeReplacedByLeaves(blockstate, world, new BlockPos(xOffset, yOffset, zOffset)) : false) || canGenerateInBlock(world, new BlockPos(xOffset,
												yOffset, zOffset), genBlock))) {
								r |= generateBlock(world, new BlockPos(xOffset, yOffset, zOffset), leaves);
							}
						}
					}
				}

				for (yOffset = 0; yOffset < treeHeight; ++yOffset) {
					blockstate = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + yOffset, pos.getZ()));
					block = blockstate.getBlock();

					if ((treeChecks ? block.isAir(blockstate, world, new BlockPos(pos.getX(), pos.getY() + yOffset, pos.getZ())) || block.isLeaves(blockstate, world, new BlockPos(pos.getX(), pos.getY() + yOffset, pos.getZ()))
							|| block.isReplaceable(world, new BlockPos(pos.getX(), pos.getY() + yOffset, pos.getZ())) : false)
							|| canGenerateInBlock(world, new BlockPos(pos.getX(), yOffset + pos.getY(), pos.getZ()), genBlock)) {
						r |= generateBlock(world, new BlockPos(pos.getX(), yOffset + pos.getY(), pos.getZ()), trunk);
					}
				}

				return r;
			}
		}
		return false;
	}

}
