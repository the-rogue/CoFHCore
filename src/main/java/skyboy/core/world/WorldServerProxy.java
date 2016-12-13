package skyboy.core.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public abstract class WorldServerProxy extends WorldServer {

	protected WorldServer proxiedWorld;

	public WorldServerProxy(WorldServer world) {

		super(world.getMinecraftServer(), world.getSaveHandler(), world.getWorldInfo(), world.provider.getDimension(), world.theProfiler);
		this.proxiedWorld = world;
		// perWorldStorage = world.perWorldStorage; // final, set in super; requires reflection
		ReflectionHelper.setPrivateValue(World.class, this, world.getPerWorldStorage(), "perWorldStorage"); // forge-added, no reobf
		loadedEntityList = world.loadedEntityList;
		loadedTileEntityList = world.loadedTileEntityList;
		playerEntities = world.playerEntities;
		weatherEffects = world.weatherEffects;
		rand = world.rand;
		// provider = world.provider; // handled by super
		mapStorage = world.getMapStorage();
		villageCollectionObj = world.villageCollectionObj;
		// theProfiler = world.theProfiler; // handled by super
		isRemote = world.isRemote;
		customTeleporters = world.customTeleporters;
		cofh_updateProps();
	}

	protected void cofh_updateProps() {

		scheduledUpdatesAreImmediate = proxiedWorld.scheduledUpdatesAreImmediate;
		setSkylightSubtracted(proxiedWorld.getSkylightSubtracted());
		prevRainingStrength = proxiedWorld.prevRainingStrength;
		rainingStrength = proxiedWorld.rainingStrength;
		prevThunderingStrength = proxiedWorld.prevThunderingStrength;
		thunderingStrength = proxiedWorld.thunderingStrength;
		setLastLightningBolt(proxiedWorld.getLastLightningBolt());
		getWorldInfo().setDifficulty(proxiedWorld.getWorldInfo().getDifficulty());
		findingSpawnPoint = proxiedWorld.findingSpawnPoint;
		chunkProvider = proxiedWorld.getChunkProvider();
		allPlayersSleeping = proxiedWorld.allPlayersSleeping;
		disableLevelSaving = proxiedWorld.disableLevelSaving;
	}

}
