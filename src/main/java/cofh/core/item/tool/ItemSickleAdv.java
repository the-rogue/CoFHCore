package cofh.core.item.tool;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import cofh.core.util.CoreUtils;

public class ItemSickleAdv extends ItemToolAdv {

	public int radius = 3;

	public ItemSickleAdv(Item.ToolMaterial toolMaterial) {

		super(3.0F, toolMaterial);
		addToolClass("sickle");

		effectiveMaterials.add(Material.LEAVES);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.WEB);
		effectiveBlocks.add(Blocks.WEB);
		effectiveBlocks.add(Blocks.VINE);
	}

	public ItemSickleAdv setRadius(int radius) {

		this.radius = radius;
		return this;
	}

	@Override
	protected boolean harvestBlock(World world, BlockPos pos, EntityPlayer player) {

		if (world.isAirBlock(pos)) {
			return false;
		}
		EntityPlayerMP playerMP = null;
		if (player instanceof EntityPlayerMP) {
			playerMP = (EntityPlayerMP) player;
		}
		// check if the block can be broken, since extra block breaks shouldn't instantly break stuff like obsidian
		// or precious ores you can't harvest while mining stone
		Block block = world.getBlockState(pos).getBlock();
		IBlockState blockstate = world.getBlockState(pos);
		// only effective materials
		if (!(getToolClasses(player.getHeldItemMainhand()).contains(block.getHarvestTool(blockstate)) || canHarvestBlock(block, player.getHeldItemMainhand()))) {
			return false;
		}
		if (!ForgeHooks.canHarvestBlock(block, player, world, pos)) {
			return false;
		}
		// send the blockbreak event
		int xp;
		if (playerMP != null) {
			xp = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);
			if (xp == -1) {
				return false;
			}
		}
		if (player.capabilities.isCreativeMode) {
			if (!world.isRemote) {
				block.onBlockHarvested(world, x, y, z, meta, player);
			}

			if (block.removedByPlayer(world, player, x, y, z, false)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
			}
			// send update to client
			if (!world.isRemote) {
				playerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
			} else {
				Minecraft.getMinecraft().getNetHandler()
						.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
			}
			return true;
		}
		if (!world.isRemote) {
			// serverside we reproduce ItemInWorldManager.tryHarvestBlock
			// ItemInWorldManager.removeBlock
			block.onBlockHarvested(world, x, y, z, meta, player);
			if (block.removedByPlayer(world, player, x, y, z, true)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
				block.harvestBlock(world, player, x, y, z, meta);
				if (block.equals(Blocks.VINE)) {
					CoreUtils.dropItemStackIntoWorldWithVelocity(new ItemStack(Blocks.VINE), world, x, y, z);
				}
				if (event != null) {
					block.dropXpOnBlockBreak(world, x, y, z, event.getExpToDrop());
				}
			}
			// always send block update to client
			playerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
		} else {
			// PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;
			// clientside we do a "this block has been clicked on long enough to be broken" call. This should not send any new packets
			// the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.
			// following code can be found in PlayerControllerMP.onPlayerDestroyBlock
			if (block.removedByPlayer(world, player, x, y, z, true)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
			}
			Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
		}
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {

		World world = player.worldObj;
		Block block = world.getBlock(x, y, z);

		if (!canHarvestBlock(block, stack)) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
			return false;
		}
		boolean used = false;

		world.playAuxSFXAtEntity(player, 2001, x, y, z, Block.getIdFromBlock(block) | (world.getBlockMetadata(x, y, z) << 12));

		for (int i = x - radius; i <= x + radius; i++) {
			for (int k = z - radius; k <= z + radius; k++) {
				used |= harvestBlock(world, i, y, k, player);
			}
		}
		if (used && !player.capabilities.isCreativeMode) {
			stack.damageItem(1, player);
		}
		return true;
	}

}
