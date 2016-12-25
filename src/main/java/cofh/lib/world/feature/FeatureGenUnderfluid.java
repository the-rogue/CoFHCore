package cofh.lib.world.feature;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;

public class FeatureGenUnderfluid extends FeatureBase {

	final boolean water;
	final WorldGenerator worldGen;
	final int count;
	final List<WeightedRandomBlock> matList;
	final String[] fluidList;

	public FeatureGenUnderfluid(String name, WorldGenerator worldGen, List<WeightedRandomBlock> matList, int count, GenRestriction biomeRes, boolean regen,
			GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.matList = matList;
		water = true;
		fluidList = null;
	}

	public FeatureGenUnderfluid(String name, WorldGenerator worldGen, List<WeightedRandomBlock> matList, String[] fluidList, int count, GenRestriction biomeRes,
			boolean regen, GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.matList = matList;
		water = false;
		Arrays.sort(fluidList);
		this.fluidList = fluidList;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean generateFeature(Random random, int chunkX, int chunkZ, World world) {

		int blockX = chunkX * 16;
		int blockZ = chunkZ * 16;

		boolean generated = false;
		for (int i = 0; i < count; i++) {
			int x = blockX + random.nextInt(16);
			int z = blockZ + random.nextInt(16);
			if (!canGenerateInBiome(world, x, z, random)) {
				continue;
			}

			int y = BlockHelper.getSurfaceBlockY(world, x, z);
			l: do {
				IBlockState blockstate = world.getBlockState(new BlockPos(x, y, z));
				Block block = blockstate.getBlock();
				if (water) {
					if (blockstate.getMaterial() == Material.WATER) {
						continue;
					}
					if (world.getBlockState(new BlockPos(x, y + 1, z)).getMaterial() != Material.WATER) {
						continue;
					}
				} else {
					Fluid fluid = FluidHelper.lookupFluidForBlock(block);
					//TODO Replace this depreciated method!!!
					if (fluid != null && Arrays.binarySearch(fluidList, FluidRegistry.getFluidID(fluid.getName())) >= 0) {
						continue;
					}

					fluid = FluidHelper.lookupFluidForBlock(world.getBlockState(new BlockPos(x, y + 1, z)).getBlock());
					if (fluid == null || Arrays.binarySearch(fluidList, FluidRegistry.getFluidID(fluid.getName())) < 0) {
						continue;
					}
				}
				for (WeightedRandomBlock mat : matList) {
					if (block.isReplaceableOreGen(mat.block.getStateFromMeta(mat.metadata), world, new BlockPos(x, y, z), null)) {
						break l;
					}
				}
			} while (y-- > 1);

			if (y > 0) {
				generated |= worldGen.generate(world, random, new BlockPos(x, y, z));
			}
		}
		return generated;
	}

}
