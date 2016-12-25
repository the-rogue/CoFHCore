package cofh.core.fluid;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cofh.lib.render.particle.ParticleDrip;
import cofh.lib.util.helpers.StringHelper;

public abstract class BlockFluidCoFHBase extends BlockFluidClassic {

	String name = "";
	String modName = "cofh";
	protected float particleRed = 1.0F;
	protected float particleGreen = 1.0F;
	protected float particleBlue = 1.0F;
	protected boolean shouldDisplaceFluids = false;

	public BlockFluidCoFHBase(Fluid fluid, Material material, String name) {

		super(fluid, material);
		this.name = StringHelper.titleCase(name);
		
		setUnlocalizedName(modName + ":fluid" + name);
		displacements.put(this, false);
	}

	public BlockFluidCoFHBase(String modName, Fluid fluid, Material material, String name) {

		super(fluid, material);

		this.name = StringHelper.titleCase(name);
		this.modName = modName;
		
		setUnlocalizedName(modName + ":fluid" + name);
		displacements.put(this, false);
	}

	public BlockFluidCoFHBase setParticleColor(int c) {

		return setParticleColor(((c >> 16) & 255) / 255f, ((c >> 8) & 255) / 255f, ((c >> 0) & 255) / 255f);
	}

	public BlockFluidCoFHBase setParticleColor(float particleRed, float particleGreen, float particleBlue) {

		this.particleRed = particleRed;
		this.particleGreen = particleGreen;
		this.particleBlue = particleBlue;

		return this;
	}

	public BlockFluidCoFHBase setDisplaceFluids(boolean a) {

		this.shouldDisplaceFluids = a;
		return this;
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {

		return false;
	}

	public boolean preInit() {

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {

		super.randomDisplayTick(state, world, pos, rand);

		double py = pos.getY() - 1.05D;

		if (density < 0) {
			py = pos.getY() + 2.10D;
		}
		if (rand.nextInt(20) == 0 && world.isSideSolid(new BlockPos(pos).add(0, densityDir, 0), densityDir == -1 ? EnumFacing.UP : EnumFacing.DOWN)
				&& !world.getBlockState(new BlockPos(pos).add(0, 2 * densityDir, 0)).getMaterial().blocksMovement()) {
			;
			Particle fx = new ParticleDrip(world, pos.getX() + rand.nextFloat(), py, pos.getZ() + rand.nextFloat(), particleRed, particleGreen, particleBlue, densityDir);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {

		if (!shouldDisplaceFluids && world.getBlockState(pos).getMaterial().isLiquid()) {
			return false;
		}
		return super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {

		if (!shouldDisplaceFluids && world.getBlockState(pos).getMaterial().isLiquid()) {
			return false;
		}
		return super.displaceIfPossible(world, pos);
	}

}
