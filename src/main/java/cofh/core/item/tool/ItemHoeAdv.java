package cofh.core.item.tool;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import cofh.api.core.IInitializer;
import cofh.lib.util.helpers.ItemHelper;

public class ItemHoeAdv extends ItemHoe implements IInitializer {

	public String repairIngot = "";
	protected boolean showInCreative = true;

	public ItemHoeAdv(Item.ToolMaterial toolMaterial, String modid, String name) {

		super(toolMaterial);
		setUnlocalizedName(modid + ":" + name);
		setRegistryName(name);
	}

	public ItemHoeAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemHoeAdv setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		if (showInCreative) {
			list.add(new ItemStack(item, 1, 0));
		}
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, new String[]{repairIngot});
	}
	
	@Override
	public boolean preInit()
	{
		GameRegistry.register(this);
		return true;
	}	
	
	@Override
	public boolean initialize() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(this.getUnlocalizedName().substring(5), "inventory"));
		return true;
	}
	
	@Override
	public boolean postInit()
	{
		return true;
	}

}
