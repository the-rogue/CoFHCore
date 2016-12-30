package cofh.core.item;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import cofh.api.core.IInitializer;
import cofh.lib.util.helpers.ItemHelper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ItemArmorAdv extends ItemArmor implements IInitializer {

	public String repairIngot = "";
	protected Multimap<String, AttributeModifier> properties = HashMultimap.create();
	protected boolean showInCreative = true;
	protected String modname;
	protected String name;
	
	//Names should be in the format armour.material.type
	public ItemArmorAdv(ArmorMaterial material, EntityEquipmentSlot equipmentSlot, String modname, String name) {

		super(material, 0, equipmentSlot);
		this.modname = modname;
		this.name = name;
		setUnlocalizedName(modname + ":" + name);
		setRegistryName(name);
	}

	public ItemArmorAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemArmorAdv setShowInCreative(boolean showInCreative) {

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
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {

		Multimap<String, AttributeModifier> map = super.getItemAttributeModifiers(equipmentSlot);
		map.putAll(properties);
		return map;
	}

	public ItemArmorAdv putAttribute(String attribute, AttributeModifier modifier) {

		properties.put(attribute, modifier);
		return this;
	}

	public Collection<AttributeModifier> removeAttribute(String attribute) {

		return properties.removeAll(attribute);
	}
	
	@Override
	public boolean preInit()
	{
		GameRegistry.register(this);
		String shortName = name.substring(name.indexOf(".") + 1);
		String material = shortName.substring(0, shortName.indexOf("."));
		String type = shortName.substring(shortName.indexOf(".") + 1);
		ModelBakery.registerItemVariants(this, new ResourceLocation(modname, "armor/" + material.toLowerCase(Locale.US) + "/" + material + type));
		return true;
	}
	
	@Override
	public boolean initialize() {
		String shortName = name.substring(name.indexOf(".") + 1);
		String material = shortName.substring(0, shortName.indexOf("."));
		String type = shortName.substring(shortName.indexOf(".") + 1);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(new ResourceLocation(modname, "armor/" + material.toLowerCase(Locale.US) + "/" + material + type), "inventory"));
		return true;
	}

	@Override
	public boolean postInit()
	{
		return true;
	}
}
