package cofh.core.entity;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetAddress;
import java.net.SocketAddress;

import javax.crypto.SecretKey;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetServerHandlerFake extends NetHandlerPlayServer {

	public static class NetworkManagerFake extends NetworkManager {

		public NetworkManagerFake() {

			super(EnumPacketDirection.CLIENTBOUND);
		}

		@Override
		public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {

		}

		@Override
		public void setConnectionState(EnumConnectionState p_150723_1_) {

		}

		@Override
		public void channelInactive(ChannelHandlerContext p_channelInactive_1_) {

		}

		@Override
		public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) {

		}

		@Override
		public void setNetHandler(INetHandler p_150719_1_) {

		}

		@SuppressWarnings("rawtypes")
		@Override
		public void sendPacket(Packet p_150725_1_) {

		}

		@Override
		public void processReceivedPackets() {

		}

		@Override
		public SocketAddress getRemoteAddress() {

			return null;
		}

		@Override
		public void closeChannel(ITextComponent p_150718_1_) {

		}

		@Override
		public boolean isLocalChannel() {

			return false;
		}

		@SideOnly(Side.CLIENT)
		public static NetworkManager provideLanClient(InetAddress p_150726_0_, int p_150726_1_) {

			return null;
		}

		@SideOnly(Side.CLIENT)
		public static NetworkManager provideLocalClient(SocketAddress p_150722_0_) {

			return null;
		}

		@Override
		public void enableEncryption(SecretKey p_150727_1_) {

		}

		@Override
		public boolean isChannelOpen() {

			return false;
		}

		@Override
		public INetHandler getNetHandler() {

			return null;
		}

		@Override
		public ITextComponent getExitMessage() {

			return null;
		}

		@Override
		public void disableAutoRead() {

		}

		@Override
		public Channel channel() {

			return null;
		}

	}

	public NetServerHandlerFake(MinecraftServer par1MinecraftServer, EntityPlayerMP par3EntityPlayerMP) {

		super(par1MinecraftServer, new NetworkManagerFake(), par3EntityPlayerMP);
	}

	@Override
	public void kickPlayerFromServer(String p_147360_1_) {

	}

	@Override
	public void processInput(CPacketInput p_147358_1_) {

	}

	@Override
	public void processPlayer(CPacketPlayer p_147347_1_) {

	}

	@Override
	public void setPlayerLocation(double p_147364_1_, double p_147364_3_, double p_147364_5_, float p_147364_7_, float p_147364_8_) {

	}

	@Override
	public void processPlayerDigging(CPacketPlayerDigging p_147345_1_) {

	}

	@Override
	public void onDisconnect(ITextComponent p_147231_1_) {

	}

	@Override
	public void sendPacket(final Packet<?> p_147359_1_) {

	}

	@Override
	public void processHeldItemChange(CPacketHeldItemChange p_147355_1_) {

	}

	@Override
	public void processChatMessage(CPacketChatMessage p_147354_1_) {

	}

	@Override
	public void processEntityAction(CPacketEntityAction p_147357_1_) {

	}

	@Override
	public void processUseEntity(CPacketUseEntity p_147340_1_) {

	}

	@Override
	public void processClientStatus(CPacketClientStatus p_147342_1_) {

	}

	@Override
	public void processCloseWindow(CPacketCloseWindow p_147356_1_) {

	}

	@Override
	public void processClickWindow(CPacketClickWindow p_147351_1_) {

	}

	@Override
	public void processEnchantItem(CPacketEnchantItem p_147338_1_) {

	}

	@Override
	public void processCreativeInventoryAction(CPacketCreativeInventoryAction p_147344_1_) {

	}

	@Override
	public void processConfirmTransaction(CPacketConfirmTransaction p_147339_1_) {

	}

	@Override
	public void processUpdateSign(CPacketUpdateSign p_147343_1_) {

	}

	@Override
	public void processKeepAlive(CPacketKeepAlive p_147353_1_) {

	}

	@Override
	public void processPlayerAbilities(CPacketPlayerAbilities p_147348_1_) {

	}

	@Override
	public void processTabComplete(CPacketTabComplete p_147341_1_) {

	}

	@Override
	public void processClientSettings(CPacketClientSettings p_147352_1_) {

	}
}
