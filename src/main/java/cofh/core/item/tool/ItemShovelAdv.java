package cofh.core.item.tool;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemShovelAdv extends ItemToolAdv {

	public ItemShovelAdv(ToolMaterial toolMaterial, String modid, String name) {

		super(1.0F, 1.0F, toolMaterial);
		addToolClass("shovel");
		setUnlocalizedName(modid + ":" + name);
		setRegistryName(name);

		effectiveBlocks.addAll(new ItemShovelFake().geteffectiveblocks());
		effectiveMaterials.add(Material.GROUND);
		effectiveMaterials.add(Material.GRASS);
		effectiveMaterials.add(Material.SAND);
		effectiveMaterials.add(Material.SNOW);
		effectiveMaterials.add(Material.CRAFTED_SNOW);
		effectiveMaterials.add(Material.CLAY);
	}
	
	@Override
	public boolean preInit()
	{
		GameRegistry.register(this);
		return true;
	}
	
	@Override
	public boolean initialize() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(this.getUnlocalizedName().substring(5), "inventory"));
		return true;
	}

	@Override
	public boolean postInit()
	{
		return true;
	}
	
	public class ItemShovelFake extends ItemSpade {

		public ItemShovelFake()
		{
			super(Item.ToolMaterial.WOOD);
		}
		public Set<Block> geteffectiveblocks(){
			return effectiveBlocks;
		}
	}
}
