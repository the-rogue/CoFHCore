package cofh.core.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import cofh.CoFHCore;

public class CoFHEnchantment {

	private CoFHEnchantment() {

	}

	public static void postInit() {

		int enchantId = CoFHCore.configCore.get("Enchantment", "Holding", 100);
		EntityEquipmentSlot[] equipslots = new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
		holding = new EnchantmentHolding(Rarity.VERY_RARE, equipslots);
		
		for (int i = enchantId; i < 256; i++) {
			try {
				Enchantment.REGISTRY.register(i, new ResourceLocation("cofh", "holding"), holding);
				break;
			} catch (IllegalArgumentException e) {

			}
		}
		CoFHCore.configCore.set("Enchantment", "Holding", Enchantment.getEnchantmentID(holding));

		enchantId = CoFHCore.configCore.get("Enchantment", "Multishot", 101);
		multishot = new EnchantmentMultishot(Rarity.VERY_RARE, equipslots);
		
		for (int i = enchantId; i < 256; i++) {
			try {
				Enchantment.REGISTRY.register(i, new ResourceLocation("cofh", "multishot"), multishot);
				break;
			} catch (IllegalArgumentException e) {

			}
		}
		CoFHCore.configCore.set("Enchantment", "Multishot", Enchantment.getEnchantmentID(multishot));
	}

	public static NBTTagList getEnchantmentTagList(NBTTagCompound nbt) {

		return nbt == null ? null : nbt.getTagList("ench", 10);
	}

	public static void addEnchantment(NBTTagCompound nbt, int id, int level) {

		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		NBTTagList list = getEnchantmentTagList(nbt);

		if (list == null) {
			list = new NBTTagList();
		}
		boolean found = false;
		for (int i = 0; i < list.tagCount() && !found; i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if (tag.getShort("id") == id) {
				tag.setShort("id", (short) id);
				tag.setShort("lvl", (short) level);
				found = true;
			}
		}
		if (!found) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setShort("id", (short) id);
			tag.setShort("lvl", (short) level);
			list.appendTag(tag);
		}
		nbt.setTag("ench", list);
	}

	public static void addEnchantment(ItemStack stack, int id, int level) {

		addEnchantment(stack.getTagCompound(), id, level);
	}

	public static Enchantment holding;
	public static Enchantment multishot;

}
