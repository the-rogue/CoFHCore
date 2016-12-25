package cofh.core.particles;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cofh.lib.util.position.BlockPosition;

public abstract class ParticleBase {
	public final static float BASE_GRAVITY = 0.04F;
	protected static final ResourceLocation MC_PARTICLES = new ResourceLocation("textures/particle/particles.png");
	public static final Random rand = new Random();
	public static double interpPosX;
	public static double interpPosY;
	public static double interpPosZ;
	public static float rX;
	public static float rZ;
	public static float rYZ;
	public static float rXY;
	public static float rXZ;

	public final ResourceLocation location;
	protected final Vec3d prev;
	protected final Vec3d pos;
	protected double motX, motY, motZ;
	protected float r = 1;
	protected float g = 1;
	protected float b = 1;
	protected float a = 1;
	protected float size = 0.2F;
	protected int life;
	protected int maxLife;
	protected boolean onGround;
	protected float gravity = BASE_GRAVITY;

	protected ParticleBase(double x, double y, double z, double motX, double motY, double motZ, float size, int life, ResourceLocation location) {
		pos = new Vec3d(x, y, z);
		prev = new Vec3d(x, y, z);
		this.motX = motX;
		this.motY = motY;
		this.motZ = motZ;
		this.size = 0.1F * size;
		this.life = 0;
		this.maxLife = life;
		this.location = location;
	}

	public final boolean advance() {
		copyVecValuesFrom(prev, pos);
		return checkLife() && handleMovement();
	}

	public boolean handleMovement() {
		motY -= gravity;
		if (!moveEntity(motX, motY, motZ)) return false;
		applyFriction();
		return true;
	}

	public void applyFriction() {
		motX *= 0.9800000190734863D;
		motY *= 0.9800000190734863D;
		motZ *= 0.9800000190734863D;
		if (this.onGround) {
			motX *= 0.699999988079071D;
			motZ *= 0.699999988079071D;
		}
	}

	public boolean checkLife() {
		return life++ < maxLife;
	}

	public void copyVecValuesFrom(Vec3d dst, Vec3d src) {
		dst = new Vec3d(src.xCoord, src.yCoord, src.zCoord);
	}

	protected boolean moveEntity(double motX, double motY, double motZ) {
		World worldObj = Minecraft.getMinecraft().theWorld;
		if (worldObj == null) return false;

		if (noClip()) {
			pos.addVector(motX, motY, motZ);
			return true;
		} else {
			if (motX == 0 && motY == 0 && motZ == 0) return true;

			pos.addVector(motX, motY, motZ);

			RayTraceResult rtr = worldObj.rayTraceBlocks(prev, pos);
			if (rtr != null) {
				if (!collide(rtr)) return false;

				this.motX = pos.xCoord - prev.xCoord;
				this.motY = pos.yCoord - prev.yCoord;
				this.motZ = pos.zCoord - prev.zCoord;

			}
			return true;
		}
	}

	private boolean collide(RayTraceResult rtr) {
		if (killOnCollide())
			return false;

		copyVecValuesFrom(pos, rtr.hitVec);

		onGround = this.motY < 0 && rtr.sideHit == EnumFacing.UP;
		return true;
	}

	protected boolean killOnCollide() {
		return true;
	}

	protected boolean noClip() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	protected int brightness(float partialTicks) {
		int x = MathHelper.floor_double(pos.xCoord);
		int z = MathHelper.floor_double(pos.zCoord);

		World worldObj = Minecraft.getMinecraft().theWorld;
		if (worldObj != null && BlockPosition.blockExists(worldObj, new BlockPos(x, 0, z))) {
			int y = MathHelper.floor_double(pos.yCoord);
			return worldObj.getCombinedLight(new BlockPos(x, y, z), 0);
		} else {
			return 0;
		}
	}

	@SideOnly(Side.CLIENT)
	public abstract void render(VertexBuffer worldRenderer, double partialTicks);

	@SideOnly(Side.CLIENT)
	public void renderParticle(VertexBuffer worldRenderer, double partialTicks, double size, double u0, double u1, double v0, double v1) {

		double x = pos.xCoord + (pos.xCoord - prev.xCoord) * partialTicks - interpPosX;
		double y = pos.yCoord + (pos.yCoord - prev.yCoord) * partialTicks - interpPosY;
		double z = pos.zCoord + (pos.zCoord - prev.zCoord) * partialTicks - interpPosZ;
        int i = this.brightness((float)partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        Vec3d[] avec3d = new Vec3d[] {
        		new Vec3d((double)(-rX * size - rXY * size), (double)(-rZ * size), (double)(-rYZ * size - rXZ * size)), 
        		new Vec3d((double)(-rX * size + rXY * size), (double)(rZ * size), (double)(-rYZ * size + rXZ * size)), 
        		new Vec3d((double)(rX * size + rXY * size), (double)(rZ * size), (double)(rYZ * size + rXZ * size)), 
        		new Vec3d((double)(rX * size - rXY * size), (double)(-rZ * size), (double)(rYZ * size - rXZ * size))
        		};

        worldRenderer.pos(x + avec3d[0].xCoord, y + avec3d[0].yCoord, z + avec3d[0].zCoord).tex(u1, v1).color(r, g, b, a).lightmap(j, k).endVertex();
        worldRenderer.pos(x + avec3d[1].xCoord, y + avec3d[1].yCoord, z + avec3d[1].zCoord).tex(u1, v0).color(r, g, b, a).lightmap(j, k).endVertex();
        worldRenderer.pos(x + avec3d[2].xCoord, y + avec3d[2].yCoord, z + avec3d[2].zCoord).tex(u0, v0).color(r, g, b, a).lightmap(j, k).endVertex();
        worldRenderer.pos(x + avec3d[3].xCoord, y + avec3d[3].yCoord, z + avec3d[3].zCoord).tex(u0, v1).color(r, g, b, a).lightmap(j, k).endVertex();
	}


	@SuppressWarnings("unchecked")
	public <T extends ParticleBase> T setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends ParticleBase> T setColor(int color) {
		this.r = (float) ((color & 16711680) >> 16) / 255.0F;
		this.g = (float) ((color & 65280) >> 8) / 255.0F;
		this.b = (float) ((color & 255)) / 255.0F;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends ParticleBase> T setAlpha(float a) {
		this.a = a;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends ParticleBase> T setSize(float size) {
		this.size = 0.1F * size;
		return (T) this;
	}
}
