package cofh.core.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;
import cofh.lib.util.helpers.StringHelper;

public abstract class GuiBaseAdv extends GuiBase {

	public static final String TEX_ARROW_LEFTs = GuiProps.PATH_ELEMENTS + "Progress_Arrow_Left.png";
	public static final String TEX_ARROW_RIGHTs = GuiProps.PATH_ELEMENTS + "Progress_Arrow_Right.png";
	public static final String TEX_DROP_LEFTs = GuiProps.PATH_ELEMENTS + "Progress_Fluid_Left.png";
	public static final String TEX_DROP_RIGHTs = GuiProps.PATH_ELEMENTS + "Progress_Fluid_Right.png";

	public static final String TEX_ALCHEMYs = GuiProps.PATH_ELEMENTS + "Scale_Alchemy.png";
	public static final String TEX_BUBBLEs = GuiProps.PATH_ELEMENTS + "Scale_Bubble.png";
	public static final String TEX_CRUSHs = GuiProps.PATH_ELEMENTS + "Scale_Crush.png";
	public static final String TEX_FLAMEs = GuiProps.PATH_ELEMENTS + "Scale_Flame.png";
	public static final String TEX_FLUXs = GuiProps.PATH_ELEMENTS + "Scale_Flux.png";
	public static final String TEX_SAWs = GuiProps.PATH_ELEMENTS + "Scale_Saw.png";
	public static final String TEX_SUNs = GuiProps.PATH_ELEMENTS + "Scale_Sun.png";
	public static final String TEX_SNOWFLAKEs = GuiProps.PATH_ELEMENTS + "Scale_Snowflake.png";

	public static final String TEX_INFO_ANGLEs = GuiProps.PATH_ELEMENTS + "Info_Angle.png";
	public static final String TEX_INFO_DISTANCEs = GuiProps.PATH_ELEMENTS + "Info_Distance.png";
	public static final String TEX_INFO_DURATIONs = GuiProps.PATH_ELEMENTS + "Info_Duration.png";
	public static final String TEX_INFO_FORCEs = GuiProps.PATH_ELEMENTS + "Info_Force.png";
	public static final String TEX_INFO_SIGNALs = GuiProps.PATH_ELEMENTS + "Info_Signal.png";

	public static final String TEX_TANK = GuiProps.PATH_ELEMENTS + "FluidTank.png";
	public static final String TEX_TANK_GREY = GuiProps.PATH_ELEMENTS + "FluidTank_Grey.png";

	public static final int PROGRESS = 24;
	public static final int SPEED = 16;

	protected String myInfo = "";

	public GuiBaseAdv(Container container) {

		super(container);
	}

	public GuiBaseAdv(Container container, ResourceLocation texture) {

		super(container, texture);
	}

	protected void generateInfo(String tileString, int lines) {

		myInfo = StringHelper.localize(tileString + "." + 0);
		for (int i = 1; i < lines; i++) {
			myInfo += "\n\n" + StringHelper.localize(tileString + "." + i);
		}
	}

	/* HELPERS */
	@Override
	public void drawButton(TextureAtlasSprite icon, int x, int y, int spriteSheet, int mode) {

		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		switch (mode) {
		case 0:
			drawIcon(map.getAtlasSprite("cofh:icons/Icon_Button"), x, y, 1);
			break;
		case 1:
			drawIcon(map.getAtlasSprite("cofh:icons/Icon_Button_Highlight"), x, y, 1);
			break;
		default:
			drawIcon(map.getAtlasSprite("cofh:icons/Icon_Button_Inactive"), x, y, 1);
			break;
		}
		drawIcon(icon, x, y, spriteSheet);
	}

	@Override
	public TextureAtlasSprite getIcon(String name) {
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(name);
	}

}
