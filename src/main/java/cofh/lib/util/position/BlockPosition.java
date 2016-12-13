package cofh.lib.util.position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cofh.lib.util.helpers.BlockHelper;

public class BlockPosition implements Comparable<BlockPosition>, Serializable {

	private static final long serialVersionUID = 8671402745765780610L;

	private BlockPos pos;
	public EnumFacing orientation;

	public BlockPosition(BlockPos pos) {

		this.pos = pos;
	}

	public BlockPosition(BlockPos pos, EnumFacing orientation) {

		this.pos = pos;
		this.orientation = orientation;
	}

	public BlockPosition(BlockPosition p) {

		pos = p.pos;
		orientation = p.orientation;
	}

	public BlockPosition(NBTTagCompound tag) {

		pos = new BlockPos(tag.getInteger("bp_i"), tag.getInteger("bp_j"), tag.getInteger("bp_k"));

		if (tag.hasKey("bp_dir")) {
			orientation = EnumFacing.VALUES[tag.getByte("bp_dir")];
		}
	}

	public BlockPosition(TileEntity tile) {

		pos = tile.getPos();
		if (tile instanceof IRotateableTile) {
			orientation = ((IRotateableTile) tile).getDirectionFacing();
		}
	}
	
	public BlockPos getPos()
	{
		return pos;
	}

	public void setPos(BlockPos pos)
	{
		this.pos = pos;
	}

	public static <T extends TileEntity & IRotateableTile> BlockPosition fromRotateableTile(T te) {

		return new BlockPosition(te);
	}

	public BlockPosition copy() {

		return new BlockPosition(pos, orientation);
	}

	public BlockPosition copy(EnumFacing orientation) {

		return new BlockPosition(pos, orientation);
	}

	public BlockPosition setOrientation(EnumFacing o) {

		orientation = o;
		return this;
	}

	public BlockPosition step(int dir) {

		int[] d = BlockHelper.SIDE_COORD_MOD[dir];
		pos = new BlockPos(pos.getX() + d[0], pos.getY() + d[1], pos.getZ() + d[2]);
		return this;
	}

	public BlockPosition step(int dir, int dist) {

		int[] d = BlockHelper.SIDE_COORD_MOD[dir];
		pos = new BlockPos(pos.getX() + d[0] * dist, pos.getY() + d[1] * dist, pos.getZ() + d[2] * dist);
		return this;
	}

	public BlockPosition step(EnumFacing dir) {
		
		pos.offset(dir);
		return this;
	}

	public BlockPosition step(EnumFacing dir, int dist) {

		pos.offset(dir, dist);
		return this;
	}

	public BlockPosition moveForwards(int step) {

		switch (orientation) {
		case UP:
			pos = new BlockPos(pos.getX(), pos.getY() + step, pos.getZ());
			break;
		case DOWN:
			pos = new BlockPos(pos.getX(), pos.getY() - step, pos.getZ());
			break;
		case SOUTH:
			pos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + step);
			break;
		case NORTH:
			pos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - step);
			break;
		case EAST:
			pos = new BlockPos(pos.getX() + step, pos.getY(), pos.getZ());
			break;
		case WEST:
			pos = new BlockPos(pos.getX() - step, pos.getY(), pos.getZ());
			break;
		default:
		}
		return this;
	}

	public BlockPosition moveBackwards(int step) {

		return moveForwards(-step);
	}

	public BlockPosition moveRight(int step) {

		switch (orientation) {
		case UP:
		case SOUTH:
			pos = new BlockPos(pos.getX() - step, pos.getY(), pos.getZ());
			break;
		case DOWN:
		case NORTH:
			pos = new BlockPos(pos.getX() + step, pos.getY(), pos.getZ());
			break;
		case EAST:
			pos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + step);
			break;
		case WEST:
			pos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - step);
			break;
		default:
			break;
		}
		return this;
	}

	public BlockPosition moveLeft(int step) {

		return moveRight(-step);
	}

	public BlockPosition moveUp(int step) {

		switch (orientation) {
		case EAST:
		case WEST:
		case NORTH:
		case SOUTH:
			pos = new BlockPos(pos.getX(), pos.getY() + step, pos.getZ());
			break;
		case UP:
			pos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - step);
			break;
		case DOWN:
			pos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + step);
		default:
			break;
		}
		return this;
	}

	public BlockPosition moveDown(int step) {

		return moveUp(-step);
	}

	public void writeToNBT(NBTTagCompound tag) {

		tag.setInteger("bp_i", pos.getX());
		tag.setInteger("bp_j", pos.getY());
		tag.setInteger("bp_k", pos.getZ());
		tag.setByte("bp_dir", (byte) orientation.ordinal());
	}

	@Override
	public String toString() {

		if (orientation == null) {
			return "{" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "}";
		}
		return "{" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ";" + orientation.toString() + "}";
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof BlockPosition)) {
			return false;
		}
		BlockPosition bp = (BlockPosition) obj;
		return bp.pos.equals(pos) & bp.orientation == orientation;
	}

	// so compiler will optimize
	public boolean equals(BlockPosition bp) {

		return bp != null && bp.pos.equals(pos) & bp.orientation == orientation;
	}

	@Override
	public int hashCode() {

		return (pos.getX() & 0xFFF) | (pos.getY() & 0xFF << 8) | (pos.getZ() & 0xFFF << 12);
	}

	public BlockPosition min(BlockPosition p) {

		return new BlockPosition(new BlockPos(p.pos.getX() > pos.getX() ? pos.getX() : p.pos.getX(), p.pos.getY() > pos.getY() ? pos.getY() : p.pos.getY(), p.pos.getZ() > pos.getZ() ? pos.getZ() : p.pos.getZ()));
	}

	public BlockPosition max(BlockPosition p) {

		return new BlockPosition(new BlockPos(p.pos.getX() < pos.getX() ? pos.getX() : p.pos.getX(), p.pos.getY() < pos.getY() ? pos.getY() : p.pos.getY(), p.pos.getZ() < pos.getZ() ? pos.getZ() : p.pos.getZ()));
	}

	public List<BlockPosition> getAdjacent(boolean includeVertical) {

		List<BlockPosition> a = new ArrayList<BlockPosition>(4 + (includeVertical ? 2 : 0));
		a.add(copy(EnumFacing.EAST).moveForwards(1));
		a.add(copy(EnumFacing.WEST).moveForwards(1));
		a.add(copy(EnumFacing.SOUTH).moveForwards(1));
		a.add(copy(EnumFacing.NORTH).moveForwards(1));
		if (includeVertical) {
			a.add(copy(EnumFacing.UP).moveForwards(1));
			a.add(copy(EnumFacing.DOWN).moveForwards(1));
		}
		return a;
	}

	public boolean blockExists(World world) {

		return pos.getY() >= 0 && pos.getY() < 256 ? world.isBlockLoaded(pos) : false;
	}
	
	public static boolean blockExists(World world, BlockPos pos) {
		return pos.getY() >= 0 && pos.getY() < 256 ? world.isBlockLoaded(pos) : false;
	}

	public TileEntity getTileEntity(World world) {

		return world.getTileEntity(pos);
	}

	public IBlockState getBlock(World world) {

		return world.getBlockState(pos);
	}

	@SuppressWarnings("unchecked")
	public <T> T getTileEntity(World world, Class<T> targetClass) {

		TileEntity te = world.getTileEntity(pos);
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	public static EnumFacing getDirection(int xS, int yS, int zS, int x, int y, int z) {

		int dir = 0;
		if (y < yS) {
			dir |= 1;
		} else if (y != yS) {
			dir |= 2;
		}
		if (z < zS) {
			dir |= 4;
		} else if (z != zS) {
			dir |= 8;
		}
		if (x < xS) {
			dir |= 16;
		} else if (x != xS) {
			dir |= 32;
		}
		switch (dir) {
		case 2:
			return EnumFacing.UP;
		case 1:
			return EnumFacing.DOWN;
		case 4:
			return EnumFacing.WEST;
		case 8:
			return EnumFacing.EAST;
		case 16:
			return EnumFacing.NORTH;
		case 32:
			return EnumFacing.SOUTH;
		default:
			return null;
		}
	}

	public static TileEntity getTileEntityRaw(World world, BlockPos pos) {	

		if (!blockExists(world, pos)) {
			return null;
		}
        ChunkPos chunkposition = new ChunkPos(pos);
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        TileEntity tileentity = (TileEntity)chunk.getTileEntityMap().get(chunkposition);
		return tileentity == null || tileentity.isInvalid() ? null : tileentity;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getTileEntityRaw(World world, BlockPos pos, Class<T> targetClass) {

		TileEntity te = getTileEntityRaw(world, pos);
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	public static boolean blockExists(TileEntity start, EnumFacing dir) {

		final BlockPos pos = start.getPos().offset(dir);
		return blockExists(start.getWorld(), pos);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity start, EnumFacing dir) {

		final BlockPos pos = start.getPos().offset(dir);
		return getTileEntityRaw(start.getWorld(), pos);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAdjacentTileEntity(TileEntity start, EnumFacing direction, Class<T> targetClass) {

		TileEntity te = getAdjacentTileEntity(start, direction);
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	/* Comparable */
	@Override
	public int compareTo(BlockPosition other) {

		return this.pos.compareTo(other.pos);
	}

}
