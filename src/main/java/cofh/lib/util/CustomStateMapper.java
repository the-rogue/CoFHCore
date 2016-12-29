package cofh.lib.util;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CustomStateMapper extends StateMapperBase implements ItemMeshDefinition {
	
	public final String stateFile;
	public final String variant;
	public final ModelResourceLocation location;

	public CustomStateMapper(String modid, String stateFile, String variant) {
		this.stateFile = stateFile;
		this.variant = variant;
		this.location = new ModelResourceLocation(new ResourceLocation(modid, stateFile), variant);
	}
	public CustomStateMapper(String stateFile, String variant) {
		this.stateFile = stateFile;
		this.variant = variant;
		this.location = new ModelResourceLocation(new ResourceLocation(stateFile), variant);
	}

	@Nonnull
	@Override
	protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
		return location;
	}

	@Nonnull
	@Override
	public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
		return location;
	}
}