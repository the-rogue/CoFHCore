package cofh.lib.util;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import cofh.lib.util.helpers.ItemHelper;

/**
 * Don't instantiate this or call these methods in any way. Use the methods in {@link ItemHelper}.
 *
 * @author King Lemming
 *
 */

public class OreDictionaryProxy {

	public ItemStack getOre(String oreName) {

		if (!oreNameExists(oreName)) {
			return null;
		}
		return ItemHelper.cloneStack(OreDictionary.getOres(oreName).get(0), 1);
	}

	public int[] getOreID(ItemStack stack) {

		return OreDictionary.getOreIDs(stack);
	}

	public int getOreID(String oreName) {

		return OreDictionary.getOreID(oreName);
	}

	public String[] getOreName(ItemStack stack) {
		
		ArrayList<String> oreNames = new ArrayList<String>();
		for (int i : OreDictionary.getOreIDs(stack)) {
			String s = OreDictionary.getOreName(i);
			oreNames.add(s);
		}
		return (String[]) oreNames.toArray();
	}

	public String getOreName(int oreID) {

		return OreDictionary.getOreName(oreID);
	}

	public boolean isOreIDEqual(ItemStack stack, int[] oreID) {

		return OreDictionary.getOreIDs(stack) == oreID;
	}

	public boolean isOreNameEqual(ItemStack stack, String[] oreName) {

		return getOreName(stack).equals(oreName);
	}

	public boolean oreNameExists(String oreName) {

		return OreDictionary.doesOreNameExist(oreName);
	}

}
