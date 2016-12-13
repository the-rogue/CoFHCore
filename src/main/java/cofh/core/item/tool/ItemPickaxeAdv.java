package cofh.core.item.tool;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemPickaxe;

public class ItemPickaxeAdv extends ItemToolAdv {

	@SuppressWarnings("unchecked")
	public ItemPickaxeAdv(ToolMaterial toolMaterial) {

		super(2.0F, toolMaterial);
		addToolClass("pickaxe");

		effectiveBlocks.addAll(ItemPickaxe.effectiveBlocks);
		effectiveMaterials.add(Material.IRON);
		effectiveMaterials.add(Material.ANVIL);
		effectiveMaterials.add(Material.ROCK);
		effectiveMaterials.add(Material.ICE);
		effectiveMaterials.add(Material.PACKED_ICE);
		effectiveMaterials.add(Material.GLASS);
		effectiveMaterials.add(Material.REDSTONE_LIGHT);
	}

}
