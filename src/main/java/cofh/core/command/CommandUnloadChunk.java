package cofh.core.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import cofh.repack.codechicken.lib.raytracer.RayTracer;

import com.google.common.base.Throwables;

public class CommandUnloadChunk implements ISubCommand {

	public static ISubCommand instance = new CommandUnloadChunk();

	@Override
	public String getCommandName() {

		return "unloadchunk";
	}

	@Override
	public int getPermissionLevel() {

		return 4;
	}

	Field chunksToUnload;

	@SuppressWarnings("unchecked")
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {

		if (!(sender instanceof EntityPlayerMP)) {
			return;
		}

		if (chunksToUnload == null) {
			chunksToUnload = ReflectionHelper.findField(ChunkProviderServer.class, "field_73248_b", "droppedChunksSet");
		}

		EntityPlayerMP player = (EntityPlayerMP) sender;
		RayTraceResult trace = RayTracer.retrace(player, 100);
		Chunk chunk = player.worldObj.getChunkFromBlockCoords(trace.getBlockPos());

		Set<Long> o;
		try {
			o = (Set<Long>) chunksToUnload.get(player.getServerWorld().getChunkProvider());
		} catch (IllegalAccessException e) {
			throw Throwables.propagate(e);
		}

		o.add(ChunkPos.chunkXZ2Int(chunk.xPosition, chunk.zPosition));
		CommandHandler.logAdminCommand(sender, this, "info.cofh.command.unloadchunk.success", chunk.xPosition, chunk.zPosition);
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {

		return new ArrayList<String>();
	}

}
