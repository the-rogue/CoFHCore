package cofh.core.command;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import cofh.api.world.IFeatureGenerator;
import cofh.core.world.FeatureParser;
import cofh.core.world.WorldHandler;

import com.google.common.base.Throwables;

public class CommandReloadWorldgen implements ISubCommand {

	public static ISubCommand instance = new CommandReloadWorldgen();

	@Override
	public String getCommandName() {

		return "reloadworldgen";
	}

	@Override
	public int getPermissionLevel() {

		return 3;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {

		for (IFeatureGenerator g : FeatureParser.parsedFeatures) {
			WorldHandler.instance.removeFeature(g);
		}

		FeatureParser.parsedFeatures.clear();

		try {
			FeatureParser.parseGenerationFile();
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
		CommandHandler.logAdminCommand(sender, this, "info.cofh.command.reloadworldgen.success");
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		return new ArrayList<String>();
	}

}
