package cofh.core.item;

import cofh.lib.util.helpers.ItemHelper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemArmorAdv extends ItemArmor {

	public String repairIngot = "";
	public String[] textures = new String[2];
	protected Multimap<String, AttributeModifier> properties = HashMultimap.create();
	protected boolean showInCreative = true;

	public ItemArmorAdv(ArmorMaterial material, EntityEquipmentSlot equipmentSlot) {

		super(material, 0, equipmentSlot);
	}

	public ItemArmorAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemArmorAdv setArmorTextures(String[] textures) {

		this.textures = textures;
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

		return ItemHelper.isOreNameEqual(stack, repairIngot);
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

}
