package cofh.core.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import cofh.core.block.BlockCoFHBase;


public class Render
{
	/**
	 * Useful Helper method to register the texture for each block, that all the blocks use
	 */
	public static void blockTexture(BlockCoFHBase block)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getUnlocalizedName().substring(5), "inventory"));
	}
	
	/**
	 * Useful Helper method to register the texture for each item, that all the items use
	 */
	public static void itemTexture(Item item)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getUnlocalizedName().substring(5), "inventory"));
	}
}
