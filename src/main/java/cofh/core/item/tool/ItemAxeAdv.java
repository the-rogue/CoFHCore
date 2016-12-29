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
import net.minecraft.item.ItemAxe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemAxeAdv extends ItemToolAdv {
	
	protected String modname;
	protected String name;
	
	public ItemAxeAdv(Item.ToolMaterial toolMaterial, String modname, String name) {
		
		super(3.0F, 3.0F, toolMaterial);
		this.modname = modname;
		this.name = name;
		addToolClass("axe");
		setUnlocalizedName(modname + ":" + name);
		setRegistryName(name);

		effectiveBlocks.addAll(new ItemAxeFake().geteffectiveblocks());
		effectiveMaterials.add(Material.WOOD);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.CACTUS);
		effectiveMaterials.add(Material.GOURD);
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
	
	public class ItemAxeFake extends ItemAxe {

		public ItemAxeFake()
		{
			super(Item.ToolMaterial.WOOD);
		}
		public Set<Block> geteffectiveblocks(){
			return effectiveBlocks;
		}
	}

}
