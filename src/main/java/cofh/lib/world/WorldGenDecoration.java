package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.canGenerateInBlock;
import static cofh.lib.world.WorldGenMinableCluster.selectBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.position.BlockPosition;

public class WorldGenDecoration extends WorldGenerator {

	private final List<WeightedRandomBlock> cluster;
	private final WeightedRandomBlock[] genBlock;
	private final WeightedRandomBlock[] onBlock;
	private final int clusterSize;
	public boolean seeSky = true;
	public boolean checkStay = true;
	public int stackHeight = 1;
	public int xVar = 8;
	public int yVar = 4;
	public int zVar = 8;

	public WorldGenDecoration(List<WeightedRandomBlock> blocks, int count, List<WeightedRandomBlock> material, List<WeightedRandomBlock> on) {

		cluster = blocks;
		clusterSize = count;
		genBlock = material == null ? null : material.toArray(new WeightedRandomBlock[material.size()]);
		onBlock = on == null ? null : on.toArray(new WeightedRandomBlock[on.size()]);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {

		boolean r = false;
		for (int l = clusterSize; l-- > 0;) {
			BlockPos pos2 = new BlockPos(pos).add(rand.nextInt(xVar) - rand.nextInt(xVar), (yVar > 1 ? rand.nextInt(yVar) - rand.nextInt(yVar) : 0), rand.nextInt(zVar) - rand.nextInt(zVar));

			if (!BlockPosition.blockExists(world, pos2)) {
				++l;
				continue;
			}

			if ((!seeSky || world.canBlockSeeSky(pos2)) && canGenerateInBlock(world, new BlockPos(pos).add(0, -1, 0), onBlock)
					&& canGenerateInBlock(world, pos2, genBlock)) {

				WeightedRandomBlock block = selectBlock(world, cluster);
				int stack = stackHeight > 1 ? rand.nextInt(stackHeight) : 0;
				do {
					if (!checkStay || block.block.canPlaceBlockAt(world, pos2)) {
						r |= world.setBlockState(pos2, block.block.getStateFromMeta(block.metadata),2);
					} else {
						break;
					}
					pos2.add(0, 1, 0);
					if (!canGenerateInBlock(world, pos, genBlock)) {
						break;
					}
				} while (stack-- > 0);
			}
		}
		return r;
	}

}
