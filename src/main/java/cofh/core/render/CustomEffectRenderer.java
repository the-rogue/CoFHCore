package cofh.core.render;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomEffectRenderer extends EffectRenderer {

	public CustomEffectRenderer() {

		super(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().renderEngine);
	}

	@Override
	public void addEffect(EntityFX p) {

	}
}
