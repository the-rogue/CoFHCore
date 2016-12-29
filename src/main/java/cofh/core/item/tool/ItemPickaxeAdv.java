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
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemPickaxeAdv extends ItemToolAdv {
	
	protected String modname;
	protected String name;
	
	public ItemPickaxeAdv(ToolMaterial toolMaterial, String modname, String name) {

		super(2.0F, 2.0F, toolMaterial);
		addToolClass("pickaxe");
		this.modname = modname;
		this.name = name;
		setUnlocalizedName(modname + ":" + name);
		setRegistryName(name);

		effectiveBlocks.addAll(new ItemPickaxeFake().geteffectiveblocks());
		effectiveMaterials.add(Material.IRON);
		effectiveMaterials.add(Material.ANVIL);
		effectiveMaterials.add(Material.ROCK);
		effectiveMaterials.add(Material.ICE);
		effectiveMaterials.add(Material.PACKED_ICE);
		effectiveMaterials.add(Material.GLASS);
		effectiveMaterials.add(Material.REDSTONE_LIGHT);
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
	
	public class ItemPickaxeFake extends ItemPickaxe {

		public ItemPickaxeFake()
		{
			super(Item.ToolMaterial.WOOD);
		}
		public Set<Block> geteffectiveblocks(){
			return effectiveBlocks;
		}
	}
}
