package cofh.core.fluid;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fluids.Fluid;
import cofh.CoFHCore;
import cofh.core.util.IBakeable;
import cofh.lib.util.BlockWrapper;

public class BlockFluidInteractive extends BlockFluidCoFHBase implements IBakeable {

	protected final TMap<BlockWrapper, BlockWrapper> collisionMap = new THashMap<BlockWrapper, BlockWrapper>();

	public BlockFluidInteractive(Fluid fluid, Material material, String name) {

		super(fluid, material, name);
		CoFHCore.registerBakeable(this);
	}

	public BlockFluidInteractive(String modName, Fluid fluid, Material material, String name) {

		super(modName, fluid, material, name);
		CoFHCore.registerBakeable(this);
	}

	public boolean addInteraction(IBlockState prestate, IBlockState poststate) {

		if (prestate == null || poststate == null) {
			return false;
		}
		collisionMap.put(new BlockWrapper(prestate), new BlockWrapper(poststate));
		return true;
	}
	@SuppressWarnings("deprecation")
	public boolean addInteraction(Block preblock, int minMeta, int maxMeta, IBlockState postState) {
		boolean sucessful = false;
		for (int meta = minMeta; meta < maxMeta; meta++) {
			sucessful = addInteraction(preblock.getStateFromMeta(meta), postState);
		}
		return sucessful;
	}
	@SuppressWarnings("deprecation")
	public boolean addInteraction(Block preblock, int preMinMeta, int preMaxMeta, Block postblock, int postMeta) {
		boolean sucessful = false;
		for (int premeta = preMinMeta; premeta < preMaxMeta; premeta++) {
			sucessful = addInteraction(preblock.getStateFromMeta(premeta), postblock.getStateFromMeta(postMeta));
		}
		return sucessful;
	}


	public boolean hasInteraction(IBlockState state) {

		return collisionMap.containsKey(new BlockWrapper(state)) || collisionMap.containsKey(new BlockWrapper(state.getBlock().getDefaultState()));
	}

	public BlockWrapper getInteraction(IBlockState state) {

		if (collisionMap.containsKey(new BlockWrapper(state))) {
			return collisionMap.get(new BlockWrapper(state));
		}
		return collisionMap.get(new BlockWrapper(state.getBlock().getDefaultState()));
	}

	@Override
	public void bake() {

		TMap<BlockWrapper, BlockWrapper> temp = new THashMap<BlockWrapper, BlockWrapper>();
		temp.putAll(collisionMap);
		collisionMap.clear();
		collisionMap.putAll(temp);
	}

}
