package cofh.core.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBucket;
import cofh.api.core.IInitializer;

public abstract class ItemBucketCoFH extends ItemBucket implements IInitializer {

	public ItemBucketCoFH(Block containedBlock, String modname, String name, CreativeTabs tab)
	{
		super(containedBlock);
		setUnlocalizedName(modname + ":" + name);
		setCreativeTab(tab);
		setRegistryName(modname, name);
	}
}
