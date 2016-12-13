package cofh.core.util.fluid;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class DispenserFilledBucketHandler extends BehaviorDefaultDispenseItem {

	private final BehaviorDefaultDispenseItem defaultDispenserItemBehavior = new BehaviorDefaultDispenseItem();

	/**
	 * Dispense the specified stack, play the dispense sound and spawn particles.
	 */
	@Override
	public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stackBucket) {

		IPosition ipos = BlockDispenser.getDispensePosition(blockSource);
		BlockPos pos = new BlockPos(ipos.getX(), ipos.getY(), ipos.getZ());
		World world = blockSource.getWorld();

		if (!world.isAirBlock(pos) && world.getBlockState(pos).getMaterial().isSolid()) {
			return stackBucket;
		}
		if (BucketHandler.emptyBucket(null, blockSource.getWorld(), pos, stackBucket)) {
			return new ItemStack(Items.BUCKET);
		}
		return defaultDispenserItemBehavior.dispense(blockSource, stackBucket);
	}

}
