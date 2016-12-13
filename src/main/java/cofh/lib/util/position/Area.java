package cofh.lib.util.position;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class Area {
	
	private BlockPosition minPos;
	private BlockPosition maxPos;

	private BlockPosition origin;

	public Area(BlockPos minPos, BlockPos maxPos) {
		
		this.minPos = new BlockPosition(minPos);
		this.maxPos = new BlockPosition(maxPos);
	}

	public Area(BlockPosition center, int radius, int yNegOffset, int yPosOffset) {

		minPos = new BlockPosition(new BlockPos(center.getPos().getX() - radius, center.getPos().getY() - yNegOffset, center.getPos().getZ() - radius));
		maxPos = new BlockPosition(new BlockPos(center.getPos().getX() + radius, center.getPos().getY() + yPosOffset, center.getPos().getZ() + radius));
		origin = center;
	}

	public BlockPosition getMin() {

		return minPos;
	}

	public BlockPosition getMax() {

		return maxPos;
	}

	public boolean contains(BlockPosition pos) {

		return pos.getPos().getX() >= minPos.getPos().getX() & pos.getPos().getX() <= maxPos.getPos().getX() & pos.getPos().getY() >= minPos.getPos().getY() & pos.getPos().getY() <= maxPos.getPos().getY() & pos.getPos().getZ() >= minPos.getPos().getZ() & pos.getPos().getZ() <= maxPos.getPos().getZ();
	}

	public List<BlockPosition> getPositionsTopFirst() {

		ArrayList<BlockPosition> positions = new ArrayList<BlockPosition>();
		for (int y = maxPos.getPos().getY(); y >= minPos.getPos().getY(); y--) {
			for (int x = minPos.getPos().getX(); x <= maxPos.getPos().getX(); x++) {
				for (int z = minPos.getPos().getZ(); z <= maxPos.getPos().getZ(); z++) {
					positions.add(new BlockPosition(new BlockPos(x, y, z)));
				}
			}
		}
		return positions;
	}

	public List<BlockPosition> getPositionsBottomFirst() {

		ArrayList<BlockPosition> positions = new ArrayList<BlockPosition>();
		for (int y = minPos.getPos().getY(); y <= maxPos.getPos().getY(); y++) {
			for (int x = minPos.getPos().getX(); x <= maxPos.getPos().getX(); x++) {
				for (int z = minPos.getPos().getZ(); z <= maxPos.getPos().getZ(); z++) {
					positions.add(new BlockPosition(new BlockPos(x, y, z)));
				}
			}
		}
		return positions;
	}

	public BlockPosition getOrigin() {

		return origin.copy();
	}

	public AxisAlignedBB toAxisAlignedBB() {

		return new AxisAlignedBB(minPos.getPos(), maxPos.getPos().add(1, 1, 1));
	}

}
