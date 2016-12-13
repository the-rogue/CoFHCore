package cofh.api.transport;

import net.minecraft.util.math.BlockPos;

public interface IEnderDestination extends IEnderAttuned {

	public boolean isNotValid();

	public BlockPos pos();

	public int dimension();

	public int getDestination();

	public boolean setDestination(int frequency);

	public boolean clearDestination();

}
