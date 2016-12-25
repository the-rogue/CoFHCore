package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.canGenerateInBlock;
import static cofh.lib.world.WorldGenMinableCluster.generateBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import cofh.lib.util.WeightedRandomBlock;

public class WorldGenStalactite extends WorldGenStalagmite {

	public WorldGenStalactite(List<WeightedRandomBlock> resource, List<WeightedRandomBlock> block, List<WeightedRandomBlock> gblock) {

		super(resource, block, gblock);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {

		int end = world.getActualHeight();
		while (world.isAirBlock(pos) && pos.getY() < end) {
			pos.add(0, 1, 0);
		}

		if (!canGenerateInBlock(world, pos.add(0, -1, 0), baseBlock)) {
			return false;
		}

		int maxHeight = rand.nextInt(heightVariance) + minHeight;

		int size = genSize > 0 ? genSize : maxHeight / heightMod + rand.nextInt(sizeVariance);
		boolean r = false;
		for (int x = -size; x <= size; ++x) {
			for (int z = -size; z <= size; ++z) {
				if (!canGenerateInBlock(world, new BlockPos(pos).add(x, 1, z), baseBlock)) {
					continue;
				}
				int height = getHeight(x, z, size, rand, maxHeight);
				for (int y = 0; y < height; ++y) {
					r |= generateBlock(world, new BlockPos(pos).add(x, -y, z), genBlock, cluster);
				}
			}
		}
		return r;
	}
}
