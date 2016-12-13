package cofh.lib.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

/**
 * Slot that will only accept ItemStacks when the IInventory returns true from isItemValidForSlot.
 *
 * If an ISidedInventory, canInsertItem (from side 6 (UNKNOWN)) must also return true.
 */
public class SlotAcceptInsertable extends SlotAcceptValid {

	protected ISidedInventory sidedInv;

	public SlotAcceptInsertable(IInventory inventory, int index, int x, int y) {

		super(inventory, index, x, y);

		if (inventory instanceof ISidedInventory) {
			sidedInv = (ISidedInventory) inventory;
		} else {
			sidedInv = null;
		}
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		boolean valid = super.isItemValid(stack);
		EnumFacing side = null;
		for (EnumFacing sideiteration : EnumFacing.VALUES) 
		{
			int[] slots = sidedInv.getSlotsForFace(sideiteration);
			
			for (int slot : slots)
			{
				if (slotNumber == slot) {
					side = sideiteration;
				}
			}
		}
		if (!(side == null)) {
			return valid && sidedInv != null ? sidedInv.canInsertItem(slotNumber, stack, side) : valid;
		} 
		else
		{
			return false;
		}
	}

}
