package cofh.core.item.tool;

import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import cofh.CoFHCore;
import cofh.api.core.IInitializer;
import cofh.lib.util.helpers.ItemHelper;

public class ItemHoeAdv extends ItemHoe implements IInitializer {
	
	protected String modname;
	protected String name;
	
	public String repairIngot = "";
	protected boolean showInCreative = true;

	public ItemHoeAdv(Item.ToolMaterial toolMaterial, String modname, String name) {

		super(toolMaterial);
		this.modname = modname;
		this.name = name;
		setUnlocalizedName(modname + ":" + name);
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
		String shortName = name.substring(name.indexOf(".") + 1);
		String material = shortName.substring(0, shortName.indexOf("."));
		String type = shortName.substring(shortName.indexOf(".") + 1);
		ModelBakery.registerItemVariants(this, new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type));
		return true;
	}	
	
	@Override
	public boolean initialize() {
		String shortName = name.substring(name.indexOf(".") + 1);
		String material = shortName.substring(0, shortName.indexOf("."));
		String type = shortName.substring(shortName.indexOf(".") + 1);
		CoFHCore.log.info(new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type), "inventory"));
		return true;
	}
	
	@Override
	public boolean postInit()
	{
		return true;
	}

}
