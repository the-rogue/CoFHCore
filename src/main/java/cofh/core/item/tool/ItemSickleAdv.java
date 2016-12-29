package cofh.core.item.tool;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;
import cofh.CoFHCore;
import cofh.core.util.CoreUtils;

public class ItemSickleAdv extends ItemToolAdv {
	
	protected String modname;
	protected String name;
	
	public int radius = 3;

	public ItemSickleAdv(Item.ToolMaterial toolMaterial, String modname, String name) {

		super(3.0F, 3.0F, toolMaterial);
		addToolClass("sickle");
		this.modname = modname;
		this.name = name;
		setUnlocalizedName(modname + ":" + name);
		setRegistryName(name);

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
		if (!(getToolClasses(player.getHeldItemMainhand()).contains(block.getHarvestTool(blockstate)) || canHarvestBlock(blockstate, player.getHeldItemMainhand()))) {
			return false;
		}
		if (!ForgeHooks.canHarvestBlock(block, player, world, pos)) {
			return false;
		}
		// send the blockbreak event
		int xp = 0;
		if (playerMP != null) {
			xp = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);
			if (xp == -1) {
				return false;
			}
		}
		if (player.capabilities.isCreativeMode) {
			if (!world.isRemote) {
				block.onBlockHarvested(world, pos, blockstate, player);
			}

			if (block.removedByPlayer(blockstate, world, pos, player, false)) {
				block.onBlockDestroyedByPlayer(world, pos, blockstate);
			}
			// send update to client
			if (!world.isRemote) {
				playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
			} else {
				Minecraft.getMinecraft().getConnection()
						.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
			}
			return true;
		}
		if (!world.isRemote) {
			// serverside we reproduce ItemInWorldManager.tryHarvestBlock
			// ItemInWorldManager.removeBlock
			block.onBlockHarvested(world, pos, blockstate, player);
			if (block.removedByPlayer(blockstate, world, pos, player, true)) {
				block.onBlockDestroyedByPlayer(world, pos, blockstate);
				block.harvestBlock(world, player, pos, blockstate, null, null);
				if (block.equals(Blocks.VINE)) {
					CoreUtils.dropItemStackIntoWorldWithVelocity(new ItemStack(Blocks.VINE), world, pos.getX(), pos.getY(), pos.getZ());
				}
				if (xp == 0) {
					block.dropXpOnBlockBreak(world, pos, xp);
				}
			}
			// always send block update to client
			playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
		} else {
			// PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;
			// clientside we do a "this block has been clicked on long enough to be broken" call. This should not send any new packets
			// the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.
			// following code can be found in PlayerControllerMP.onPlayerDestroyBlock
			if (block.removedByPlayer(blockstate, world, pos, player, true)) {
				block.onBlockDestroyedByPlayer(world, pos, blockstate);
			}
			Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
		}
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.worldObj;
		IBlockState blockstate = world.getBlockState(pos);

		if (!canHarvestBlock(blockstate, stack)) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
			return false;
		}
		boolean used = false;

		world.playEvent(player, 2001, pos, Block.getIdFromBlock(blockstate.getBlock()) | (blockstate.getBlock().getMetaFromState(blockstate) << 12));

		for (int i = pos.getX() - radius; i <= pos.getX() + radius; i++) {
			for (int k = pos.getZ() - radius; k <= pos.getZ() + radius; k++) {
				used |= harvestBlock(world, new BlockPos(i, pos.getZ(), k), player);
			}
		}
		if (used && !player.capabilities.isCreativeMode) {
			stack.damageItem(1, player);
		}
		return true;
	}

	@Override
	public boolean preInit()
	{
		GameRegistry.register(this);
		String shortName = name.substring(name.indexOf(".") + 1);
		String material = shortName.substring(0, shortName.indexOf("."));
		String type = shortName.substring(shortName.indexOf(".") + 1);
		ModelBakery.registerItemVariants(this, new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type));
		return true;
	}
	
	@Override
	public boolean initialize() {
		String shortName = name.substring(name.indexOf(".") + 1);
		String material = shortName.substring(0, shortName.indexOf("."));
		String type = shortName.substring(shortName.indexOf(".") + 1);
		CoFHCore.log.info(new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type), "inventory"));
		return true;
	}

	@Override
	public boolean postInit()
	{
		return true;
	}

}
