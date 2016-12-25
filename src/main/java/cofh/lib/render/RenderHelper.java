package cofh.lib.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

/**
 * Contains various helper functions to assist with rendering.
 *
 * @author King Lemming
 *
 */
public final class RenderHelper {

	public static final double RENDER_OFFSET = 1.0D / 1024.0D;
	public static final ResourceLocation MC_BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");
	public static final ResourceLocation MC_ITEM_SHEET = new ResourceLocation("textures/atlas/items.png");
	public static final ResourceLocation MC_FONT_DEFAULT = new ResourceLocation("textures/font/ascii.png");
	public static final ResourceLocation MC_FONT_ALTERNATE = new ResourceLocation("textures/font/ascii_sga.png");
	public static final ResourceLocation MC_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	private RenderHelper() {

	}

	public static final TextureManager engine() {

		return Minecraft.getMinecraft().getTextureManager();
	}

	public static final Tessellator tessellator() {

		return Tessellator.getInstance();
	}

	public static void setColor3ub(int color) {

		GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
	}

	public static void setColor4ub(int color) {

		GL11.glColor4ub((byte) (color >> 24 & 0xFF), (byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
	}

	public static void resetColor() {

		GL11.glColor4f(1F, 1F, 1F, 1F);
	}
/*
	public static void renderItemAsBlock(RenderBlocks renderer, ItemStack item, double translateX, double translateY, double translateZ) {

		renderTextureAsBlock(renderer, item.getIconIndex(), translateX, translateY, translateZ);
	}

	public static void renderTextureAsBlock(RenderManager renderer, IIcon texture, double translateX, double translateY, double translateZ) {

		Tessellator tessellator = Tessellator.getInstance();
		Block block = Blocks.STONE;

		if (texture == null) {
			return;
		}
		renderer.setRenderBoundsFromBlock(block);
		GL11.glTranslated(translateX, translateY, translateZ);
		tessellator.startDrawingQuads();

		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.draw();
	}

	public static void renderBlockFace(RenderBlocks renderer, IIcon texture, int face, double translateX, double translateY, double translateZ) {

		Tessellator tessellator = Tessellator.instance;
		Block block = Blocks.stone;

		if (texture == null || face < 0 || face > 5) {
			return;
		}
		renderer.setRenderBoundsFromBlock(block);
		GL11.glTranslated(translateX, translateY, translateZ);
		tessellator.startDrawingQuads();

		switch (face) {
		case 0:
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		case 1:
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		case 2:
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		case 3:
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		case 4:
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		case 5:
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		}
		tessellator.draw();
	}
*/

	public static void renderIcon(TextureAtlasSprite icon, double z) {

		Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		Tessellator.getInstance().getBuffer().pos(0, 16, z).tex(icon.getMinU(), icon.getMaxV()).endVertex();
		Tessellator.getInstance().getBuffer().pos(16, 16, z).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
		Tessellator.getInstance().getBuffer().pos(16, 0, z).tex(icon.getMaxU(), icon.getMinV()).endVertex();
		Tessellator.getInstance().getBuffer().pos(0, 0, z).tex(icon.getMinU(), icon.getMinV()).endVertex();
		Tessellator.getInstance().draw();
	}

	public static void renderIcon(double x, double y, double z, TextureAtlasSprite icon, int width, int height) {

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer worldrenderer = tessellator.getBuffer();
		worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		worldrenderer.pos(x, y + height, z).tex(icon.getMinU(), icon.getMaxV()).endVertex();
		worldrenderer.pos(x + width, y + height, z).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
		worldrenderer.pos(x + width, y, z).tex(icon.getMaxU(), icon.getMinV()).endVertex();
		worldrenderer.pos(x, y, z).tex(icon.getMinU(), icon.getMinV()).endVertex();
		tessellator.draw();
	}

	public static final ResourceLocation getFluidTexture(Fluid fluid) {

		if (fluid == null) {
			return FluidRegistry.LAVA.getStill();
		}
		return fluid.getStill();
	}

	public static final ResourceLocation getFluidTexture(FluidStack fluid) {

		if (fluid == null || fluid.getFluid() == null || fluid.getFluid().getStill(fluid) == null) {
			return FluidRegistry.LAVA.getStill();
		}
		return fluid.getFluid().getStill(fluid);
	}

	public static final void bindItemTexture(ItemStack stack) {

		engine().bindTexture(stack.getItem() instanceof ItemBlock ? MC_BLOCK_SHEET : MC_ITEM_SHEET);
	}

	public static final void bindTexture(ResourceLocation texture) {

		engine().bindTexture(texture);
	}

	public static final void setBlockTextureSheet() {

		bindTexture(MC_BLOCK_SHEET);
	}

	public static final void setItemTextureSheet() {

		bindTexture(MC_ITEM_SHEET);
	}

	public static final void setDefaultFontTextureSheet() {

		bindTexture(MC_FONT_DEFAULT);
	}

	public static final void setSGAFontTextureSheet() {

		bindTexture(MC_FONT_ALTERNATE);
	}

	public static final void enableGUIStandardItemLighting() {

		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
	}

	public static void enableStandardItemLighting() {

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
	}

}
