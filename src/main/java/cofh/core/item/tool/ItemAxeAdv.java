package cofh.core.item.tool;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;

public class ItemAxeAdv extends ItemToolAdv {

	@SuppressWarnings("unchecked")
	public ItemAxeAdv(Item.ToolMaterial toolMaterial) {

		super(3.0F, toolMaterial);
		addToolClass("axe");

		effectiveBlocks.addAll(ItemAxe.effectiveBlocks);
		effectiveMaterials.add(Material.WOOD);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.CACTUS);
		effectiveMaterials.add(Material.GOURD);
	}

}
