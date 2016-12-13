package cofh.core.util.oredict;

import cofh.lib.util.OreDictionaryProxy;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.item.ItemStack;

/**
 * If CoFHCore is present, an instance of this class is initialized by the OreDictionaryArbiter and the functionality in ItemHelper is much improved.
 *
 * Translation: Don't touch.
 *
 * @author King Lemming
 *
 */
public class OreDictionaryArbiterProxy extends OreDictionaryProxy {

	@Override
	public final ItemStack getOre(String oreName) {

		if (OreDictionaryArbiter.getOres(oreName) == null) {
			return null;
		}
		return ItemHelper.cloneStack(OreDictionaryArbiter.getOres(oreName).get(0), 1);
	}

	@Override
	public final int[] getOreID(ItemStack stack) {

		int[] i = new int[0];
		i[0] = OreDictionaryArbiter.getOreID(stack);
		return i;
	}

	@Override
	public final int getOreID(String oreName) {

		return OreDictionaryArbiter.getOreID(oreName);
	}

	@Override
	public final String[] getOreName(ItemStack stack) {
		String[] s = new String[0];
		s[0] = OreDictionaryArbiter.getOreName(OreDictionaryArbiter.getOreID(stack));
		return s;
	}

	@Override
	public final String getOreName(int oreID) {

		return OreDictionaryArbiter.getOreName(oreID);
	}

	@Override
	public final boolean isOreIDEqual(ItemStack stack, int[] oreID) {

		return new int[]{OreDictionaryArbiter.getOreID(stack)} == oreID;
	}

	@Override
	public final boolean isOreNameEqual(ItemStack stack, String[] oreName) {

		return OreDictionaryArbiter.getOreName(OreDictionaryArbiter.getOreID(stack)).equals(oreName);
	}

	@Override
	public final boolean oreNameExists(String oreName) {

		return OreDictionaryArbiter.getOres(oreName) != null;
	}

}
