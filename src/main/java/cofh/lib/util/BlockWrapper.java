package cofh.lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/**
 * Wrapper for a Block/Metadata combination post 1.7. Quick and dirty, allows for Integer-based Hashes without collisions.
 *
 * @author King Lemming
 *
 */
public final class BlockWrapper {

	public IBlockState blockstate;

	public BlockWrapper(IBlockState blockstate) {

		this.blockstate = blockstate;
	}

	public BlockWrapper set(IBlockState blockstate) {

		if (blockstate != null) {
			this.blockstate = blockstate;
		} else {
			this.blockstate = null;
		}
		return this;
	}

	public boolean isEqual(BlockWrapper other) {

		if (other == null) {
			return false;
		}
		if (blockstate == other.blockstate) {
			return true;
		}
		if (blockstate != null && other.blockstate != null) {
			return blockstate.getBlock().delegate.get() == other.blockstate.getBlock().delegate.get();
		}
		return false;
	}

	final int getId() {

		return Block.getIdFromBlock(blockstate.getBlock());
	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof BlockWrapper)) {
			return false;
		}
		return isEqual((BlockWrapper) o);
	}

	@Override
	public int hashCode() {

		return blockstate.getBlock().getMetaFromState(blockstate) | getId() << 16;
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder(getClass().getName());
		b.append('@').append(System.identityHashCode(this)).append('{');
		b.append("m:").append(blockstate.getBlock().getMetaFromState(blockstate)).append(", i:").append(blockstate == null ? null : blockstate.getClass().getName());
		b.append('@').append(System.identityHashCode(blockstate)).append(", v:");
		b.append(getId()).append('}');
		return b.toString();
	}

}
