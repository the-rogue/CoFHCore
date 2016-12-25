package cofh.lib.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import cofh.lib.util.WeightedRandomWorldGenerator;

public class WorldGenMulti extends WorldGenerator {

	private final List<WeightedRandomWorldGenerator> generators;

	public WorldGenMulti(ArrayList<WeightedRandomWorldGenerator> values) {

		generators = values;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos pos) {

		WeightedRandomWorldGenerator gen = (WeightedRandomWorldGenerator) WeightedRandom.getRandomItem(random, generators);
		return gen.generator.generate(world, random, pos);
	}

}
