package cofh.asmhooks;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cofh.asmhooks.event.ModPopulateChunkEvent;
import cofh.core.CoFHProps;
import cofh.core.item.IEqualityOverrideItem;

public class HooksCore {

	// { Forge hooks

	public static void preGenerateWorld(World world, int chunkX, int chunkZ) {

		MinecraftForge.EVENT_BUS.post(new ModPopulateChunkEvent.Pre(world, chunkX, chunkZ));
	}

	public static void postGenerateWorld(World world, int chunkX, int chunkZ) {

		MinecraftForge.EVENT_BUS.post(new ModPopulateChunkEvent.Post(world, chunkX, chunkZ));
	}

	// }

	// { Vanilla hooks
	public static boolean areItemsEqualHook(ItemStack held, ItemStack lastHeld) {

		if (held.getItem() != lastHeld.getItem()) {
			return false;
		}
		Item item = held.getItem();
		if (item instanceof IEqualityOverrideItem && ((IEqualityOverrideItem) item).isLastHeldItemEqual(held, lastHeld)) {
			return true;
		}
		if (held.isItemStackDamageable() && held.getItemDamage() != lastHeld.getItemDamage()) {
			return false;
		}

		return ItemStack.areItemStackTagsEqual(held, lastHeld);
	}

	@SideOnly(Side.CLIENT)
	public static void tickTextures(ITickable obj) {

		if (CoFHProps.enableAnimatedTextures) {
			obj.tick();
		}
	}

	public static boolean paneConnectsTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {

		IBlockState block = world.getBlockState(pos);
		return block.isOpaqueCube() || block.getMaterial() == Material.GLASS || block instanceof BlockPane
				|| world.isSideSolid(pos, dir.getOpposite(), false);
	}
	// }

}
