package cofh.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.registry.GameData;

import org.apache.logging.log4j.core.helpers.Loader;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.core.entity.EntityLightningBoltFake;
import cofh.lib.util.position.BlockPosition;

public class CoreUtils {

	public static int entityId = 0;

	public static int getEntityId() {

		entityId++;
		return entityId;
	}

	/* MOD UTILS */
	public static String getModName(Item item) {

		@SuppressWarnings("deprecation")
		ResourceLocation r = GameData.getItemRegistry().getNameForObject(item);
		String s = r.toString();
		return s.substring(0, ( s).indexOf(':'));
	}

	/* PLAYER UTILS */
	public static EntityPlayer getClientPlayer() {

		return CoFHCore.proxy.getClientPlayer();
	}

	public static boolean isPlayer(EntityPlayer player) {

		return player instanceof EntityPlayerMP;
	}

	public static boolean isFakePlayer(EntityPlayer player) {

		return (player instanceof FakePlayer);
	}

	public static boolean isOp(EntityPlayer player) {

		return CoFHCore.proxy.isOp(player.getName());
	}

	public static boolean isOp(String playerName) {

		return CoFHCore.proxy.isOp(playerName);
	}

	/* SERVER UTILS */
	public static boolean isClient() {

		return CoFHCore.proxy.isClient();
	}

	public static boolean isServer() {

		return CoFHCore.proxy.isServer();
	}

	/* BLOCK UTILS */
	public static boolean isBlockUnbreakable(World world, BlockPos pos) {

		IBlockState b = world.getBlockState(pos);
		return b instanceof BlockLiquid || b.getBlockHardness(world, pos) < 0;
	}

	public static boolean isRedstonePowered(World world, BlockPos pos) {
		
		BlockPosition position = new BlockPosition(pos);
		
		if (world.isBlockIndirectlyGettingPowered(position.getPos()) > 0) {
			return true;
		}
		
		for (BlockPosition bp : position.getAdjacent(false)) {
			IBlockState block = world.getBlockState(bp.getPos());
			if (block.equals(Blocks.REDSTONE_WIRE) && block.getStrongPower(world, bp.getPos(), EnumFacing.DOWN) > 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean isRedstonePowered(TileEntity tile) {

		return isRedstonePowered(tile.getWorld(), tile.getPos());
	}

	public static void dismantleLog(String playerName, Block block, int metadata, double x, double y, double z) {

		if (CoFHProps.enableDismantleLogging) {
			CoFHCore.log.info("Player " + playerName + " dismantled " + " (" + block + ":" + metadata + ") at (" + x + "," + y + "," + z + ")");
		}
	}

	/* FILE UTILS */
	public static void copyFileUsingStream(String source, String dest) throws IOException {

		copyFileUsingStream(source, new File(dest));
	}

	public static void copyFileUsingStream(String source, File dest) throws IOException {

		InputStream is = null;
		OutputStream os = null;
		try {
				is = Loader.getResource(source, null).openStream();
				os = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	public static void copyFileUsingChannel(File source, File dest) throws IOException {

		FileInputStream sourceStream = null;
		FileChannel sourceChannel = null;
		FileOutputStream outputStream = null;
		try {
				sourceStream = new FileInputStream(source);
				sourceChannel = sourceStream.getChannel();
				outputStream = new FileOutputStream(dest);
			outputStream.getChannel().transferFrom(sourceChannel, 0, sourceChannel.size());
		} finally {
			sourceStream.close();
			sourceChannel.close();
			outputStream.close();
		}
	}

	/* SOUND UTILS */

	public static final float getSoundVolume(int category) {

		return CoFHCore.proxy.getSoundVolume(category);
	}
	public static final SoundEvent createSound(String modid, String soundpath) {
		soundpath = soundpath.replaceAll("/", ".");
		ResourceLocation location = new ResourceLocation(modid, soundpath);
		SoundEvent event = new SoundEvent(location);
		SoundEvent.REGISTRY.register(-1, location, event);
		return event;
	}

	/* ENTITY UTILS */
	public static boolean dropItemStackIntoWorld(ItemStack stack, World world, double x, double y, double z) {

		return dropItemStackIntoWorld(stack, world, x, y, z, false);
	}

	public static boolean dropItemStackIntoWorldWithVelocity(ItemStack stack, World world, double x, double y, double z) {

		return dropItemStackIntoWorld(stack, world, x, y, z, true);
	}

	public static boolean dropItemStackIntoWorld(ItemStack stack, World world, double x, double y, double z, boolean velocity) {

		if (stack == null) {
			return false;
		}
		float x2 = 0.5F;
		float y2 = 0.0F;
		float z2 = 0.5F;

		if (velocity) {
			x2 = world.rand.nextFloat() * 0.8F + 0.1F;
			y2 = world.rand.nextFloat() * 0.8F + 0.1F;
			z2 = world.rand.nextFloat() * 0.8F + 0.1F;
		}
		EntityItem entity = new EntityItem(world, x + x2, y + y2, z + z2, stack.copy());

		if (velocity) {
			entity.motionX = (float) world.rand.nextGaussian() * 0.05F;
			entity.motionY = (float) world.rand.nextGaussian() * 0.05F + 0.2F;
			entity.motionZ = (float) world.rand.nextGaussian() * 0.05F;
		} else {
			entity.motionY = -0.05F;
			entity.motionX = 0;
			entity.motionZ = 0;
		}
		world.spawnEntityInWorld(entity);

		return true;
	}

	public static void doFakeExplosion(World world, double x, double y, double z, boolean playSound) {

		world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y + 1, z, 0.0D, 0.0D, 0.0D);

		if (playSound) {
			world.playSound(x, y, z, SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.generic.explode")), SoundCategory.BLOCKS, 1.0F, 1.0F, true);
		}
	}

	public static void doFakeLightningBolt(World world, double x, double y, double z) {

		EntityLightningBoltFake bolt = new EntityLightningBoltFake(world, x, y, z);
		world.addWeatherEffect(bolt);
	}

	public static boolean teleportEntityTo(Entity entity, double x, double y, double z) {

		if (entity instanceof EntityLivingBase) {
			return teleportEntityTo((EntityLivingBase) entity, x, y, z);
		} else {
			entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			entity.worldObj.playSound(x, y, z, SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.endermen.teleport")), SoundCategory.AMBIENT, 1.0F, 1.0F, false);
		}
		return true;
	}

	public static boolean teleportEntityTo(EntityLivingBase entity, double x, double y, double z) {

		EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}

		entity.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
		entity.worldObj.playSound(x, y, z, SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.endermen.teleport")), SoundCategory.AMBIENT, 1.0F, 1.0F, false);

		return true;
	}

	public static boolean teleportEntityTo(EntityLivingBase entity, double x, double y, double z, boolean cooldown) {

		if (cooldown) {
			NBTTagCompound tag = entity.getEntityData();
			long time = entity.worldObj.getTotalWorldTime();
			if (tag.getLong("cofh:tD") > time) {
				return false;
			}
			tag.setLong("cofh:tD", time + 35);
		}

		return teleportEntityTo(entity, x, y, z);
	}

}
