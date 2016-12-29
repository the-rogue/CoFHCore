package cofh.core.item.tool;

import java.util.Locale;
import java.util.Set;

import cofh.CoFHCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemShovelAdv extends ItemToolAdv {
	
	protected String modname;
	protected String name;
	
	public ItemShovelAdv(ToolMaterial toolMaterial, String modname, String name) {

		super(1.0F, 1.0F, toolMaterial);
		addToolClass("shovel");
		this.modname = modname;
		this.name = name;
		setUnlocalizedName(modname + ":" + name);
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
		String shortName = name.substring(name.indexOf(".") + 1);
		String material = shortName.substring(0, shortName.indexOf("."));
		String type = shortName.substring(shortName.indexOf(".") + 1);
		ModelBakery.registerItemVariants(this, new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type));
		return true;
	}
	
	@Override
	public boolean initialize() {
		String shortName = name.substring(name.indexOf(".") + 1);
		String material = shortName.substring(0, shortName.indexOf("."));
		String type = shortName.substring(shortName.indexOf(".") + 1);
		CoFHCore.log.info(new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type), "inventory"));
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
