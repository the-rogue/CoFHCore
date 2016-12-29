package cofh.core.fluid;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cofh.api.core.IInitializer;
import cofh.lib.render.particle.ParticleDrip;
import cofh.lib.util.CustomStateMapper;

/**
 * Required a blockstate file for all textures of BlockFluidCoFHBase's that extend this named fluid_block.json structured like this:
 * 	"variants": {
		"BlockFluidCoFHBaseName1": [{
			"custom": { "fluid": "fluid1name" }
		}],
		"BlockFluidCoFHBaseName2": [{
			"custom": { "fluid": "fluid2name" }
		}]
	}
 */
public abstract class BlockFluidCoFHBase extends BlockFluidClassic implements IInitializer {

	protected final String name;
	protected final String modName;
	protected float particleRed = 1.0F;
	protected float particleGreen = 1.0F;
	protected float particleBlue = 1.0F;
	protected boolean shouldDisplaceFluids = false;

	public BlockFluidCoFHBase(Fluid fluid, Material material, String name) {
		this("cofh", fluid, material, name);
	}

	public BlockFluidCoFHBase(String modName, Fluid fluid, Material material, String name) {
		
		super(fluid, material);

		this.name = name;
		this.modName = modName;
		
		setUnlocalizedName(modName + ":fluid" + name);
		setRegistryName("fluid" + name);
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

	@Override
	public boolean preInit() {

		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
		
		CustomStateMapper mapper = new CustomStateMapper(this.modName, "fluid_block", this.name);
		ModelLoader.registerItemVariants(Item.getItemFromBlock(this));
		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), mapper);
		ModelLoader.setCustomStateMapper(this, mapper);
		return true;
	}
	
	@Override
	public boolean initialize() {
		
		return true;
	}
	
	@Override
	public boolean postInit() {

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
