package cofh.core.entity;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCoFHFishHook extends EntityFishHook {

	int luckModifier = 0;
	int speedModifier = 0;

	public EntityCoFHFishHook(World world) {

		super(world);
	}

	@SideOnly(Side.CLIENT)
	public EntityCoFHFishHook(World world, double x, double y, double z, EntityPlayer player) {

		super(world, x, y, z, player);

	}

	public EntityCoFHFishHook(World world, EntityPlayer player, int luckMod, int speedMod) {

		super(world, player);
		luckModifier = luckMod;
		speedModifier = speedMod;
	}

	@Override
	public void onUpdate() {

		onEntityUpdate();

		if (this.fishPosRotationIncrements > 0) {
			double d7 = this.posX + (this.fishX - this.posX) / this.fishPosRotationIncrements;
			double d8 = this.posY + (this.fishY - this.posY) / this.fishPosRotationIncrements;
			double d9 = this.posZ + (this.fishZ - this.posZ) / this.fishPosRotationIncrements;
			double d1 = MathHelper.wrapDegrees(this.fishYaw - this.rotationYaw);
			this.rotationYaw = (float) (this.rotationYaw + d1 / this.fishPosRotationIncrements);
			this.rotationPitch = (float) (this.rotationPitch + (this.fishPitch - this.rotationPitch) / this.fishPosRotationIncrements);
			--this.fishPosRotationIncrements;
			this.setPosition(d7, d8, d9);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		} else {
			if (!this.worldObj.isRemote) {
				ItemStack itemstack = this.angler.getHeldItemMainhand();
				if (this.angler.isDead || !this.angler.isEntityAlive() || itemstack == null || !(itemstack.getItem() instanceof ItemFishingRod)
						|| this.getDistanceSqToEntity(this.angler) > 1024.0D) {
					this.setDead();
					this.angler.fishEntity = null;
					return;
				}

				if (this.caughtEntity != null) {
					if (!this.caughtEntity.isDead) {
						this.posX = this.caughtEntity.posX;
						this.posY = this.caughtEntity.getEntityBoundingBox().minY + this.caughtEntity.height * 0.8D;
						this.posZ = this.caughtEntity.posZ;
						return;
					}

					this.caughtEntity = null;
				}
			}

			if (this.inGround) {
				if (this.worldObj.getBlockState(field_189740_d).getBlock() == this.inTile) {
					++this.ticksInGround;

					if (this.ticksInGround == 1200) {
						this.setDead();
					}

					return;
				}

				this.inGround = false;
				this.motionX *= this.rand.nextFloat() * 0.2F;
				this.motionY *= this.rand.nextFloat() * 0.2F;
				this.motionZ *= this.rand.nextFloat() * 0.2F;
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			} else {
				++this.ticksInAir;
			}

			Vec3d vec31 = new Vec3d(this.posX, this.posY, this.posZ);
			Vec3d vec3 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			RayTraceResult rayTraceResult = this.worldObj.rayTraceBlocks(vec31, vec3);
			vec31 = new Vec3d(this.posX, this.posY, this.posZ);
			vec3 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

			if (rayTraceResult != null) {
				vec3 = new Vec3d(rayTraceResult.hitVec.xCoord, rayTraceResult.hitVec.yCoord, rayTraceResult.hitVec.zCoord);
			}

			Entity entity = null;
			List<?> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ)
					.expand(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;
			double d2;

			for (int i = 0; i < list.size(); i++) {
				Entity entity1 = (Entity) list.get(i);

				if (entity1.canBeCollidedWith() && (entity1 != this.angler || this.ticksInAir >= 5)) {
					float f = 0.3F;
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f, f, f);
					RayTraceResult rayTraceResult1 = axisalignedbb.calculateIntercept(vec31, vec3);

					if (rayTraceResult1 != null) {
						d2 = vec31.distanceTo(rayTraceResult1.hitVec);

						if (d2 < d0 || d0 == 0.0D) {
							entity = entity1;
							d0 = d2;
						}
					}
				}
			}

			if (entity != null) {
				rayTraceResult = new RayTraceResult(entity);
			}

			if (rayTraceResult != null) {
				if (rayTraceResult.entityHit != null) {
					if (rayTraceResult.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.angler), 0.0F)) {
						this.caughtEntity = rayTraceResult.entityHit;
					}
				} else {
					this.inGround = true;
				}
			}

			if (!this.inGround) {
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				float f5 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
				this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

				for (this.rotationPitch = (float) (Math.atan2(this.motionY, f5) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
					;
				}

				while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
					this.prevRotationPitch += 360.0F;
				}

				while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
					this.prevRotationYaw -= 360.0F;
				}

				while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
					this.prevRotationYaw += 360.0F;
				}

				this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
				this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
				float f6 = 0.92F;

				if (this.onGround || this.isCollidedHorizontally) {
					f6 = 0.5F;
				}

				byte b0 = 5;
				double d10 = 0.0D;

				for (int j = 0; j < b0; ++j) {
					double d3 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (j + 0) / b0 - 0.125D + 0.125D;
					double d4 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (j + 1) / b0 - 0.125D + 0.125D;
					AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(this.getEntityBoundingBox().minX, d3, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().maxX, d4,
							this.getEntityBoundingBox().maxZ);

					if (this.worldObj.isAABBInMaterial(axisalignedbb1, Material.WATER)) {
						d10 += 1.0D / b0;
					}
				}

				if (!this.worldObj.isRemote && d10 > 0.0D) {
					WorldServer worldserver = (WorldServer) this.worldObj;
					int k = 1;
					BlockPos isatpos = new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) + 1, MathHelper.floor_double(this.posZ));

					if (this.rand.nextFloat() < 0.25F && this.worldObj.isRainingAt(isatpos)) {
						k = 2;
					}

					if (this.rand.nextFloat() < 0.5F && !this.worldObj.canSeeSky(isatpos)) {
						--k;
					}

					if (this.ticksCatchable > 0) {
						--this.ticksCatchable;

						if (this.ticksCatchable <= 0) {
							this.ticksCaughtDelay = 0;
							this.ticksCatchableDelay = 0;
						}
					} else {
						float f1;
						float f2;
						double d5;
						double d6;
						float f7;
						double d11;

						if (this.ticksCatchableDelay > 0) {
							this.ticksCatchableDelay -= k;

							if (this.ticksCatchableDelay <= 0) {
								this.motionY -= 0.20000000298023224D;
								this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("random.splash")), 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
								f1 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
								worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX, f1 + 1.0F, this.posZ, (int) (1.0F + this.width * 20.0F), this.width, 0.0D,
										this.width, 0.20000000298023224D);
								worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, this.posX, f1 + 1.0F, this.posZ, (int) (1.0F + this.width * 20.0F), this.width, 0.0D,
										this.width, 0.20000000298023224D);
								this.ticksCatchable = MathHelper.getRandomIntegerInRange(this.rand, 10, 30);
							} else {
								this.fishApproachAngle = (float) (this.fishApproachAngle + this.rand.nextGaussian() * 4.0D);
								f1 = this.fishApproachAngle * 0.017453292F;
								f7 = MathHelper.sin(f1);
								f2 = MathHelper.cos(f1);
								d11 = this.posX + f7 * this.ticksCatchableDelay * 0.1F;
								d5 = MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F;
								d6 = this.posZ + f2 * this.ticksCatchableDelay * 0.1F;

								if (this.rand.nextFloat() < 0.15F) {
									worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d11, d5 - 0.10000000149011612D, d6, 1, f7, 0.1D, f2, 0.0D);
								}

								float f3 = f7 * 0.04F;
								float f4 = f2 * 0.04F;
								worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d11, d5, d6, 0, f4, 0.01D, (-f3), 1.0D);
								worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d11, d5, d6, 0, (-f4), 0.01D, f3, 1.0D);
							}
						} else if (this.ticksCaughtDelay != 0) {
							this.ticksCaughtDelay -= k;
							f1 = 0.15F;

							if (this.ticksCaughtDelay < 20) {
								f1 = (float) (f1 + (20 - this.ticksCaughtDelay) * 0.05D);
							} else if (this.ticksCaughtDelay < 40) {
								f1 = (float) (f1 + (40 - this.ticksCaughtDelay) * 0.02D);
							} else if (this.ticksCaughtDelay < 60) {
								f1 = (float) (f1 + (60 - this.ticksCaughtDelay) * 0.01D);
							}

							if (this.rand.nextFloat() < f1) {
								f7 = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F) * 0.017453292F;
								f2 = MathHelper.randomFloatClamp(this.rand, 25.0F, 60.0F);
								d11 = this.posX + MathHelper.sin(f7) * f2 * 0.1F;
								d5 = MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F;
								d6 = this.posZ + MathHelper.cos(f7) * f2 * 0.1F;
								worldserver.spawnParticle(EnumParticleTypes.WATER_SPLASH, d11, d5, d6, 2 + this.rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D,
										0.0D);
							}
							if (this.ticksCaughtDelay <= 0) {
								this.fishApproachAngle = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F);
								this.ticksCatchableDelay = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
							}
						} else {
							this.ticksCaughtDelay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
							this.ticksCaughtDelay -= (EnchantmentHelper.getLuckOfSeaModifier(this.angler) + speedModifier) * 20 * 5;
						}
					}

					if (this.ticksCatchable > 0) {
						this.motionY -= this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat() * 0.2D;
					}
				}
				d2 = d10 * 2.0D - 1.0D;
				this.motionY += 0.03999999910593033D * d2;

				if (d10 > 0.0D) {
					f6 = (float) (f6 * 0.9D);
					this.motionY *= 0.8D;
				}
				this.motionX *= f6;
				this.motionY *= f6;
				this.motionZ *= f6;
				this.setPosition(this.posX, this.posY, this.posZ);
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {

		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {

		super.readEntityFromNBT(nbt);
	}

	@Override
	public int handleHookRetraction() {

		if (this.worldObj.isRemote) {
			return 0;
		} else {
			byte b0 = 0;

			if (this.caughtEntity != null) {
				double d0 = this.angler.posX - this.posX;
				double d2 = this.angler.posY - this.posY;
				double d4 = this.angler.posZ - this.posZ;
				double d6 = MathHelper.sqrt_double(d0 * d0 + d2 * d2 + d4 * d4);
				double d8 = 0.1D;
				this.caughtEntity.motionX += d0 * d8;
				this.caughtEntity.motionY += d2 * d8 + MathHelper.sqrt_double(d6) * 0.08D;
				this.caughtEntity.motionZ += d4 * d8;
				b0 = 3;
			} else if (this.ticksCatchable > 0) {
				LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer)this.worldObj);
                lootcontext$builder.withLuck((float)EnchantmentHelper.getLuckOfSeaModifier(this.angler) + this.angler.getLuck());

                for (ItemStack itemstack : this.worldObj.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(this.rand, lootcontext$builder.build()))
                {
                	EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, itemstack);
                	double d1 = this.angler.posX - this.posX;
                	double d3 = this.angler.posY - this.posY;
                	double d5 = this.angler.posZ - this.posZ;
                	double d7 = MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5);
                	double d9 = 0.1D;
                	entityitem.motionX = d1 * d9;
                	entityitem.motionY = d3 * d9 + MathHelper.sqrt_double(d7) * 0.08D;
                	entityitem.motionZ = d5 * d9;
                	this.worldObj.spawnEntityInWorld(entityitem);
                	this.angler.worldObj.spawnEntityInWorld(new EntityXPOrb(this.angler.worldObj, this.angler.posX,
                			this.angler.posY + 0.5D, this.angler.posZ + 0.5D, this.rand.nextInt(6) + 1));
                	b0 = 1;
                }
			}
			if (this.inGround) {
				b0 = 2;
			}
			this.setDead();
			this.angler.fishEntity = null;
			return b0;
		}
	}
}
