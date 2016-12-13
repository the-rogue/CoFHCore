package cofh.core.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cofh.api.block.IBlockDebug;
import cofh.api.block.IBlockInfo;
import cofh.api.block.IDismantleable;
import cofh.api.core.IInitializer;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.inventory.IInventoryRetainer;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.ISecurable;
import cofh.api.tileentity.ITileInfo;
import cofh.core.render.Render;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.lib.util.position.BlockPosition;

import com.mojang.authlib.GameProfile;

public abstract class BlockCoFHBase extends Block implements ITileEntityProvider, IBlockDebug, IBlockInfo, IDismantleable, IInitializer {

	public static int renderPass = 0;
	public static final ArrayList<ItemStack> NO_DROP = new ArrayList<ItemStack>();

	public BlockCoFHBase(Material material) {

		super(material);
		setSoundType(SoundType.STONE);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		return createNewTileEntity(world, state.getBlock().getMetaFromState(state));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileCoFHBase) {
			TileCoFHBase theTile = (TileCoFHBase) tile;
			theTile.blockBroken();
		}
		if (tile instanceof IInventoryRetainer) {
			// do nothing
		} else if (tile instanceof IInventory) {
			IInventory inv = (IInventory) tile;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				CoreUtils.dropItemStackIntoWorldWithVelocity(inv.getStackInSlot(i), world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		if (tile != null) {
			world.removeTileEntity(pos);
		}
	}

	protected AxisAlignedBB getBoundingBox(World world, int x, int y, int z) {
		return FULL_BLOCK_AABB;
	}

	protected AxisAlignedBB getStatelessBoundingBox(World world, int x, int y, int z) {

		return FULL_BLOCK_AABB;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {

		if (!player.capabilities.isCreativeMode) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

		TileEntity tile = world.getTileEntity(pos);

		if (ServerHelper.isServerWorld(world) && tile instanceof ISecurable) {
			if (SecurityHelper.isSecure(stack)) {
				GameProfile stackOwner = SecurityHelper.getOwner(stack);

				if (((ISecurable) tile).setOwner(stackOwner)) {
					; // cool, set the owner
				} else if (placer instanceof ICommandSender) {
					((ISecurable) tile).setOwnerName(EntityList.getEntityString(placer));
				}
				((ISecurable) tile).setAccess(SecurityHelper.getAccess(stack));
			}
		}
		if (tile instanceof IRedstoneControl) {
			if (RedstoneControlHelper.hasRSControl(stack)) {
				((IRedstoneControl) tile).setControl(RedstoneControlHelper.getControl(stack));
			}
		}
		if (tile instanceof IReconfigurableFacing) {
			IReconfigurableFacing reconfig = (IReconfigurableFacing) tile;
			int quadrant = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

			if (reconfig.allowYAxisFacing()) {
				quadrant = placer.rotationPitch > 60 ? 4 : placer.rotationPitch < -60 ? 5 : quadrant;
			}
			switch (quadrant) {
			case 0:
				reconfig.setFacing(2);
				break;
			case 1:
				reconfig.setFacing(5);
				break;
			case 2:
				reconfig.setFacing(3);
				break;
			case 3:
				reconfig.setFacing(4);
				break;
			case 4:
				reconfig.setFacing(1);
				break;
			case 5:
				reconfig.setFacing(0);
				break;
			}
		}
		if (tile instanceof TileCoFHBase) {
			((TileCoFHBase) tile).onNeighborBlockChange();
			((TileCoFHBase) tile).blockPlaced();
		}
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {

		TileEntity tile;
		if (world instanceof World) {
			tile = BlockPosition.getTileEntityRaw((World) world, pos);
		} else {
			tile = world.getTileEntity(pos);
		}

		if (tile instanceof TileCoFHBase) {
			((TileCoFHBase) tile).onNeighborTileChange(neighbor);
			((TileCoFHBase) tile).onNeighborBlockChange();
		}
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof ISecurable && !((ISecurable) tile).canPlayerAccess(player)) {
			return -1;
		}
		return ForgeHooks.blockStrength(state, player, world, pos);
	}

	@Override
	public int damageDropped(IBlockState state) {

		return state.getBlock().getMetaFromState(state);
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);
		return tile instanceof TileCoFHBase ? ((TileCoFHBase) tile).getComparatorInput(blockState) : 0;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileCoFHBase && tile.getWorld() != null) {
			return ((TileCoFHBase) tile).getLightValue();
		}
		return 0;
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {

		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {

		TileEntity tile = world.getTileEntity(pos);

		return tile instanceof IReconfigurableFacing ? ((IReconfigurableFacing) tile).rotateBlock() : false;
	}

	@SideOnly(Side.CLIENT)
	public void registertexture()
	{
		Render.blockTexture(this);
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		if (world instanceof World) {
			return dismantleBlock(null, state,(World) world, pos, true);
		}
		return null;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {

		Item item = Item.getItemFromBlock(this);

		if (item == null) {
			return null;
		}
		int bMeta = state.getBlock().getMetaFromState(state);
		ItemStack pickBlock = new ItemStack(item, 1, bMeta);
		pickBlock.setTagCompound(getItemStackTag(world, pos));

		return pickBlock;
	}

	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		return null;
	}

	public abstract ArrayList<ItemStack> dismantleBlock(EntityPlayer player, NBTTagCompound nbt, World world, int x, int y, int z, boolean returnDrops,
			boolean simulate);

	/* IBlockDebug */
	@Override
	public void debugBlock(IBlockAccess world, int x, int y, int z, EnumFacing side, EntityPlayer player) {

	}

	/* IBlockInfo */
	@Override
	public void getBlockInfo(IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player, List<ITextComponent> info, boolean debug) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof ITileInfo) {
			((ITileInfo) tile).getTileInfo(info, side, player, debug);
		} else {
			if (tile instanceof IEnergyReceiver) {
				IEnergyReceiver eReceiver = (IEnergyReceiver) tile;
				if (eReceiver.getMaxEnergyStored(side) <= 0) {
					return;
				}
				info.add(new TextComponentString(StringHelper.localize("info.cofh.energy") + ": " + eReceiver.getEnergyStored(side) + "/"
						+ eReceiver.getMaxEnergyStored(side) + " RF."));
			}
		}
	}

	/* IDismantleable */
	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, IBlockState state, World world, BlockPos pos, boolean returnDropss) {
		
		this.breakBlock(world, pos, state);
		return NO_DROP;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof ISecurable) {
			return ((ISecurable) tile).canPlayerAccess(player);
		} else if (tile instanceof TileCoFHBase) {
			return ((TileCoFHBase) tile).canPlayerDismantle(player);
		}
		return true;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		return true;
	}

}
