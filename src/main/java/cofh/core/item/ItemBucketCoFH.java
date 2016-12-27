package cofh.core.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import cofh.api.core.IInitializer;

@SuppressWarnings("deprecation")
public class ItemBucketCoFH extends ItemBucket implements IInitializer {

	protected Fluid containedFluid;
	public ItemBucketCoFH(Block containedBlock, Fluid containedFluid, String modname, String name, CreativeTabs tab)
	{
		super(containedBlock);
		this.containedFluid = containedFluid;
		setUnlocalizedName(modname + ":" + name);
		setCreativeTab(tab);
		setRegistryName(modname, name);
	}
	
	@Override
	public boolean preInit()
	{
		GameRegistry.register(this);
		return true;
	}

	@Override
	public boolean initialize()
	{
		FluidContainerRegistry.registerFluidContainer(containedFluid, new ItemStack(this), FluidContainerRegistry.EMPTY_BUCKET);
		return true;
	}

	@Override
	public boolean postInit()
	{
		return true;
	}
}
