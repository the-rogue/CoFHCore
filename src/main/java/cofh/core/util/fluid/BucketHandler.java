package cofh.core.util.fluid;

import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import cofh.lib.util.BlockWrapper;
import cofh.lib.util.ItemWrapper;
import cofh.lib.util.helpers.ServerHelper;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

@SuppressWarnings("deprecation")
public class BucketHandler {

	public static BucketHandler instance = new BucketHandler();

	public static void initialize() {

	}

	private static BiMap<BlockWrapper, ItemWrapper> buckets = HashBiMap.create();

	private BucketHandler() {

		if (instance != null) {
			throw new IllegalArgumentException();
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPreBucketFill(FillBucketEvent event) {

		// perform global permissions checks
		onBucketFill(event, true);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onPostBucketFill(FillBucketEvent event) {

		// handle thusfar unhandled buckets
		onBucketFill(event, false);
	}

	private void onBucketFill(FillBucketEvent event, boolean pre) {

		if (ServerHelper.isClientWorld(event.getWorld()) | event.getFilledBucket() != null || event.getResult() != Result.DEFAULT) {
			return;
		}
		ItemStack current = event.getEmptyBucket();
		if (event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK) {
			return;
		}
		boolean fill = true;
		BlockPos pos = event.getTarget().getBlockPos();
		EnumFacing side = event.getTarget().sideHit;

		l: if (!current.getItem().equals(Items.BUCKET)) {
			if (FluidContainerRegistry.isBucket(current)) {
				IBlockState blockstate = event.getWorld().getBlockState(pos);
				pos.offset(side);
				if (!blockstate.getBlock().isReplaceable(event.getWorld(), pos) && blockstate.getMaterial().isSolid()) {
					pos.offset(side, -1);
				}
				fill = false;
				break l;
			}
			return;
		}

		if (pre) { // doing all of this in one pass will pre-empt other handlers. Split to two priorities.
			if (event.getEntityPlayer() != null) {
				if (!event.getWorld().canMineBlockBody(event.getEntityPlayer(), pos) || (fill && !event.getEntityPlayer().canPlayerEdit(pos, side, current))) {
					event.setCanceled(true);
				}
			}
			return;
		}
		ItemStack bucket = null;

		if (fill) {
			bucket = fillBucket(event.getWorld(), pos);
		} else if (emptyBucket(event.getEntityPlayer(), event.getWorld(), pos, current)) {
			bucket = new ItemStack(Items.BUCKET);
		}
		if (bucket == null) {
			return;
		}
		event.setFilledBucket(bucket);
		event.setResult(Result.ALLOW);
	}

	public static boolean registerBucket(IBlockState blockstate, ItemStack bucket) {

		if (blockstate == null || bucket == null || buckets.containsKey(new BlockWrapper(blockstate))) {
			return false;
		}
		buckets.put(new BlockWrapper(blockstate), new ItemWrapper(bucket));
		return true;
	}

	public static ItemStack fillBucket(World world, BlockPos pos) {

		IBlockState blockstate = world.getBlockState(pos);

		if (!buckets.containsKey(new BlockWrapper(blockstate))) {
			if (blockstate.equals(Blocks.WATER) || blockstate.equals(Blocks.FLOWING_WATER)) {
				if (world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)) == 0) {
					world.setBlockToAir(pos);
					return new ItemStack(Items.WATER_BUCKET);
				}
				return null;
			} else if (blockstate.equals(Blocks.LAVA) || blockstate.equals(Blocks.FLOWING_LAVA)) {
				if (world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)) == 0) {
					world.setBlockToAir(pos);
					return new ItemStack(Items.LAVA_BUCKET);
				}
				return null;
			}
			if (blockstate instanceof IFluidBlock) {
				IFluidBlock flBlock = (IFluidBlock) blockstate;

				if (flBlock.canDrain(world, pos)) {
					ItemStack stack = new ItemStack(Items.BUCKET);
					stack = FluidContainerRegistry.fillFluidContainer(flBlock.drain(world, pos, false), stack);

					if (stack != null) {
						flBlock.drain(world, pos, true);
						return stack;
					}
				}
			}
			return null;
		}
		if (!world.setBlockToAir(pos)) {
			return null;
		}
		ItemWrapper result = buckets.get(new BlockWrapper(blockstate));
		return new ItemStack(result.item, 1, result.metadata);
	}

	public static boolean emptyBucket(@Nullable EntityPlayer player,World world, BlockPos pos, ItemStack bucket) {

		boolean r = false;
		if (!buckets.inverse().containsKey(new ItemWrapper(bucket))) {
			if (bucket.getItem() instanceof ItemBucket) {
				IBlockState oldstate = world.getBlockState(pos);
				r = ((ItemBucket) bucket.getItem()).tryPlaceContainedLiquid(player, world, pos);
				world.notifyBlockUpdate(pos, oldstate, world.getBlockState(pos), 0);
			}
			return r;
		}
		BlockWrapper result = buckets.inverse().get(new ItemWrapper(bucket));

		Material material = world.getBlockState(pos).getMaterial();
		boolean solid = !material.isSolid();
		if (world.isAirBlock(pos) || solid) {
			IBlockState oldstate = world.getBlockState(pos);
			if (!world.isRemote && solid && !material.isLiquid()) {
				world.destroyBlock(pos, true);
			}
			r = world.setBlockState(pos, result.blockstate, 3); // this can fail
			world.notifyBlockUpdate(pos, oldstate, world.getBlockState(pos), 0);
		}
		return r;
	}

	public static void refreshMap() {

		BiMap<BlockWrapper, ItemWrapper> tempMap = HashBiMap.create(buckets.size());

		for (Entry<BlockWrapper, ItemWrapper> entry : buckets.entrySet()) {
			BlockWrapper tempBlock = new BlockWrapper(entry.getKey().blockstate);
			ItemWrapper tempItem = new ItemWrapper(entry.getValue().item, entry.getValue().metadata);
			tempMap.put(tempBlock, tempItem);
		}
		buckets.clear();
		buckets = tempMap;
	}

}
