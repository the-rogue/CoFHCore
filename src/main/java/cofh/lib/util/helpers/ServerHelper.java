package cofh.lib.util.helpers;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Contains various helper functions to assist with determining Server/Client status.
 *
 * @author King Lemming
 *
 */
public final class ServerHelper {

	private ServerHelper() {

	}

	public static final boolean isClientWorld(World world) {

		return world.isRemote;
	}

	public static final boolean isServerWorld(World world) {

		return !world.isRemote;
	}

	public static final boolean isSinglePlayerServer() {

		return FMLCommonHandler.instance().getMinecraftServerInstance() != null;
	}

	public static final boolean isMultiPlayerServer() {

		return FMLCommonHandler.instance().getMinecraftServerInstance() == null;
	}

	/**
	 * This function circumvents a miserable failing.
	 */
	public static final void sendItemUsePacket(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing hitSide, float hitX, float hitY,
			float hitZ) {

		if (isServerWorld(world)) {
			return;
		}
		NetHandlerPlayClient netClientHandler = (NetHandlerPlayClient) FMLClientHandler.instance().getClientPlayHandler();
		netClientHandler.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, hitSide, EnumHand.MAIN_HAND, hitX, hitY, hitZ));
	}

}
