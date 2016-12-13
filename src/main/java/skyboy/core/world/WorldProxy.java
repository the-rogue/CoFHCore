package skyboy.core.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public abstract class WorldProxy extends World {

	protected World proxiedWorld;

	public WorldProxy(World world) {

		super(world.getSaveHandler(), world.getWorldInfo(), world.provider, world.theProfiler, world.isRemote);
		this.proxiedWorld = world;
		// perWorldStorage = world.perWorldStorage; // final, set in super; requires reflection
		ReflectionHelper.setPrivateValue(World.class, this, world.getPerWorldStorage(), new String[] { "perWorldStorage" }); // forge-added, no reobf
		scheduledUpdatesAreImmediate = world.scheduledUpdatesAreImmediate;
		loadedEntityList = world.loadedEntityList;
		loadedTileEntityList = world.loadedTileEntityList;
		playerEntities = world.playerEntities;
		weatherEffects = world.weatherEffects;
		setSkylightSubtracted(world.getSkylightSubtracted());
		prevRainingStrength = world.prevRainingStrength;
		rainingStrength = world.rainingStrength;
		prevThunderingStrength = world.prevThunderingStrength;
		thunderingStrength = world.thunderingStrength;
		setLastLightningBolt(world.getLastLightningBolt());
		getWorldInfo().setDifficulty(world.getWorldInfo().getDifficulty());
		rand = world.rand;
		// provider = world.provider; // handled by super
		findingSpawnPoint = world.findingSpawnPoint;
		mapStorage = world.getMapStorage();
		villageCollectionObj = world.villageCollectionObj;
		// theProfiler = world.theProfiler; // handled by super
		isRemote = world.isRemote;
	}

	@Override
	public IChunkProvider createChunkProvider() {

		return null;
	}

	@Override
	public Entity getEntityByID(int id) {

		return null;
	}
}
