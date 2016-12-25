package cofh.core.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;

import com.google.common.base.Predicate;

public class EntityAINearestPlayerOrOther<T extends EntityCreature> extends EntityAITarget
{
	protected final Class<T> otherTargetClass;
    private final int targetChance;
    /** Instance of EntityAINearestAttackableTargetSorter. */
    protected final EntityAINearestPlayerOrOther.Sorter theNearestAttackableTargetSorter;
    protected final Predicate <EntityPlayer> targetPlayerSelector;
    protected final Predicate <T> targetOtherSelector;
    
    protected EntityLivingBase targetEntity;

    public EntityAINearestPlayerOrOther(EntityCreature creature, Class <T> otherTargetClass, boolean checkSight)
    {
        this(creature, otherTargetClass, checkSight, false);
    }

    public EntityAINearestPlayerOrOther(EntityCreature creature, Class <T> otherTargetClass, boolean checkSight, boolean onlyNearby)
    {
        this(creature, otherTargetClass, 10, checkSight, onlyNearby, (Predicate <EntityPlayer>)null, (Predicate <T>)null);
    }

    public EntityAINearestPlayerOrOther(EntityCreature creature, Class <T> otherTargetClass, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate <EntityPlayer> playerSelector, @Nullable final Predicate <T> otherSelector)
    {
        super(creature, checkSight, onlyNearby);
        this.otherTargetClass = otherTargetClass;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new EntityAINearestPlayerOrOther.Sorter(creature);
        this.setMutexBits(1);
        this.targetPlayerSelector = new Predicate<EntityPlayer>()
        {
            public boolean apply(@Nullable EntityPlayer p_apply_1_)
            {
                return p_apply_1_ == null ? false : (playerSelector != null && !playerSelector.apply(p_apply_1_) ? false : (!EntitySelectors.NOT_SPECTATING.apply(p_apply_1_) ? false : EntityAINearestPlayerOrOther.this.isSuitableTarget(p_apply_1_, false)));
            }
        };
        this.targetOtherSelector = new Predicate<T>()
        {
            public boolean apply(@Nullable T p_apply_1_)
            {
                return p_apply_1_ == null ? false : (otherSelector != null && !otherSelector.apply(p_apply_1_) ? false : (!EntitySelectors.NOT_SPECTATING.apply(p_apply_1_) ? false : EntityAINearestPlayerOrOther.this.isSuitableTarget(p_apply_1_, false)));
            }
        };
}

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	this.targetEntity = null;
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0)
        {
            return false;
        }
        else 
        {
        	this.targetEntity = this.taskOwner.worldObj.getNearestAttackablePlayer(this.taskOwner.posX, this.taskOwner.posY + (double)this.taskOwner.getEyeHeight(), this.taskOwner.posZ, this.getTargetDistance(), this.getTargetDistance(), null, this.targetPlayerSelector);
            if (this.targetEntity != null && this.taskOwner.canEntityBeSeen(this.targetEntity)) {
            	return true;
            }
            else
            {
            	List<T> list = this.taskOwner.worldObj.<T>getEntitiesWithinAABB(otherTargetClass, this.getTargetableArea(this.getTargetDistance()), this.targetOtherSelector);
            	if (list.isEmpty())
            	{
            		return false;
            	}
            	else
            	{
            		Collections.sort(list, this.theNearestAttackableTargetSorter);
            		this.targetEntity = list.get(0);
            		return true;
            	}
            }

        }
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance)
    {
        return this.taskOwner.getEntityBoundingBox().expand(targetDistance, 4.0D, targetDistance);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }

    public static class Sorter implements Comparator<Entity>
        {
            private final Entity theEntity;

            public Sorter(Entity theEntityIn)
            {
                this.theEntity = theEntityIn;
            }

            public int compare(Entity p_compare_1_, Entity p_compare_2_)
            {
                double d0 = this.theEntity.getDistanceSqToEntity(p_compare_1_);
                double d1 = this.theEntity.getDistanceSqToEntity(p_compare_2_);
                return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
            }
        }
}