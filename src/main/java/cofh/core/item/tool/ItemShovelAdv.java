package cofh.core.item.tool;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemSpade;

public class ItemShovelAdv extends ItemToolAdv {

	@SuppressWarnings("unchecked")
	public ItemShovelAdv(ToolMaterial toolMaterial) {

		super(1.0F, toolMaterial);
		addToolClass("shovel");

		effectiveBlocks.addAll(ItemSpade.effectiveBlocks);
		effectiveMaterials.add(Material.GROUND);
		effectiveMaterials.add(Material.GRASS);
		effectiveMaterials.add(Material.SAND);
		effectiveMaterials.add(Material.SNOW);
		effectiveMaterials.add(Material.CRAFTED_SNOW);
		effectiveMaterials.add(Material.CLAY);
	}

}
