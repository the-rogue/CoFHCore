package cofh.core.item.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import cofh.CoFHCore;
import cofh.api.core.IInitializer;
import cofh.core.enchantment.CoFHEnchantment;
import cofh.lib.util.helpers.ItemHelper;

public class ItemBowAdv extends ItemBow implements IInitializer {
	
	protected String modname;
	protected String name;
	
	protected ToolMaterial toolMaterial;

	public ArrayList<String> repairIngot = new ArrayList<String>();
	public float arrowSpeedMultiplier = 2.0F;
	public float arrowDamageMultiplier = 1.25F;
	protected boolean showInCreative = true;

	public ItemBowAdv(Item.ToolMaterial toolMaterial, String modname, String name) {

		super();
		this.toolMaterial = toolMaterial;
		this.modname = modname;
		this.name = name;
		setUnlocalizedName(modname + ":" + name);
		setRegistryName(name);
		setMaxDamage(toolMaterial.getMaxUses());
	}

	public int cofh_canEnchantApply(ItemStack stack, Enchantment ench) {

		if (ench == Enchantment.REGISTRY.getObject(new ResourceLocation("looting"))) {
			return 1;
		}
		if (ench.type == EnumEnchantmentType.BOW) {
			return 1;
		}
		return -1;
	}

	public ItemBowAdv setRepairIngot(String repairIngot) {

		this.repairIngot.add(repairIngot);
		return this;
	}

	public ItemBowAdv setArrowSpeed(float multiplier) {

		this.arrowSpeedMultiplier = multiplier;
		return this;
	}

	public ItemBowAdv setArrowDamage(float multiplier) {

		arrowDamageMultiplier = multiplier;
		return this;
	}

	public ItemBowAdv setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		if (showInCreative) {
			list.add(new ItemStack(item, 1, 0));
		}
	}

	@Override
	public int getItemEnchantability() {

		return toolMaterial.getEnchantability();
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, (String[])repairIngot.toArray());
	}

	// TODO: This will need a custom render or something
	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        boolean flag = this.findAmmo(playerIn) != null;

        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemStackIn, worldIn, playerIn, hand, flag || EnchantmentHelper.getEnchantmentLevel(Enchantment.REGISTRY.getObject(new ResourceLocation("infinity")), itemStackIn) > 0);
        if (ret != null) return ret;

        if (!playerIn.capabilities.isCreativeMode && !flag && EnchantmentHelper.getEnchantmentLevel(Enchantment.REGISTRY.getObject(new ResourceLocation("infinity")), itemStackIn) <= 0)
        {
            return !flag ? new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn) : new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
        }
        else
        {
            playerIn.setActiveHand(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
        }
    }

    protected ItemStack findAmmo(EntityPlayer player)
    {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
        {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
        {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else
        {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack))
                {
                    return itemstack;
                }
            }

            return null;
        }
    }
	
	@Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemstack = this.findAmmo(entityplayer);
            
            int enchantMultishot = EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.multishot, stack);
            enchantMultishot++;
            
            int drawStrength = this.getMaxItemUseDuration(stack) - timeLeft;
            drawStrength = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, (EntityPlayer)entityLiving, drawStrength, itemstack != null || flag);
            if (drawStrength < 0) return;

            if (itemstack != null || flag)
            {
                if (itemstack == null)
                {
                    itemstack = new ItemStack(Items.ARROW, enchantMultishot);
                }

                float f = getArrowVelocity(drawStrength);

                if ((double)f >= 0.1D)
                {
                    boolean flag1 = entityplayer.capabilities.isCreativeMode || (itemstack.getItem() instanceof ItemArrow ? ((ItemArrow)itemstack.getItem()).isInfinite(itemstack, stack, entityplayer) : false);
                    
                    if (enchantMultishot < 1) {
                    	enchantMultishot = 1;
                    }
                    
                    if (!worldIn.isRemote)
                    {
                    	if (itemstack.stackSize < enchantMultishot) {
                    		enchantMultishot = itemstack.stackSize;
                    	}
                    	for (int i = 0; i < enchantMultishot; i++) {
                    		ItemArrow itemarrow = (ItemArrow)((ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW));
                    		EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
                    		if (enchantMultishot == 1) {
                    			entityarrow.setAim(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);
                    		} else {
                    			entityarrow.setAim(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, 1.5f * drawStrength * arrowSpeedMultiplier, 3.0F);
                    		}

                    		if (f == 1.0F)
                    		{
                    			entityarrow.setIsCritical(true);
                    		}

                    		int enchantPower = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

                    		if (enchantPower > 0)
                    		{
                    			entityarrow.setDamage(entityarrow.getDamage() + (double)enchantPower * 0.5D + 0.5D);
                    		}
                    		
                    		int enchantPunch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                    		
                    		if (enchantPunch > 0)
                    		{
                    			entityarrow.setKnockbackStrength(enchantPunch);
                    		}
                    		
                    		if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
                    		{
                    			entityarrow.setFire(100);
                    		}

                    		if (!entityplayer.capabilities.isCreativeMode) {
                    			stack.damageItem(1, entityplayer);
                    		}
                    		if (flag1)
                    		{
                    			entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                    		}
                    		
                    		worldIn.spawnEntityInWorld(entityarrow);
                    	}
                    }

                    worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                    if (!flag1)
                    {
                    	for (int i = 0; i < enchantMultishot; i++) {
                    		--itemstack.stackSize;
                    	}

                        if (itemstack.stackSize == 0)
                        {
                            entityplayer.inventory.deleteStack(itemstack);
                        }
                    }

                    entityplayer.addStat(StatList.getObjectUseStats(this));
                }
            }
        }
    }
	
	@Override
	public boolean preInit()
	{
		GameRegistry.register(this);
		String shortName = name.substring(name.indexOf(".") + 1);
		String material = shortName.substring(0, shortName.indexOf("."));
		String type = shortName.substring(shortName.indexOf(".") + 1);
		ModelBakery.registerItemVariants(this, new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type));
		return true;
	}
	
	@Override
	public boolean initialize() {
		String shortName = name.substring(name.indexOf(".") + 1);
		String material = shortName.substring(0, shortName.indexOf("."));
		String type = shortName.substring(shortName.indexOf(".") + 1);
		CoFHCore.log.info(new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type), "inventory"));
		return true;
	}

	@Override
	public boolean postInit()
	{
		return true;
	}

}
