package cofh.repack.codechicken.lib.vec.uv;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import cofh.repack.codechicken.lib.render.CCRenderState;
import cofh.repack.codechicken.lib.vec.IrreversibleTransformationException;

public class IconTransformation extends UVTransformation {
    public TextureAtlasSprite icon;

    public IconTransformation(TextureAtlasSprite icon) {
        this.icon = icon;
    }

    @Override
    public void operate(CCRenderState state) {
        super.operate(state);
        state.sprite = icon;
    }

    @Override
    public void apply(UV uv) {
        uv.u = icon.getInterpolatedU(uv.u * 16);
        uv.v = icon.getInterpolatedV(uv.v * 16);
    }

    @Override
    public UVTransformation inverse() {
        throw new IrreversibleTransformationException(this);
    }
}
