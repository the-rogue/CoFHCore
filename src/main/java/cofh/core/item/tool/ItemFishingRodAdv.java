package cofh.core.item.tool;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import cofh.api.core.IInitializer;
import cofh.core.entity.EntityCoFHFishHook;
import cofh.lib.util.helpers.ItemHelper;

public class ItemFishingRodAdv extends ItemFishingRod implements IInitializer {

	public ArrayList<String> repairIngot = new ArrayList<String>();
	protected ToolMaterial toolMaterial;
	protected boolean showInCreative = true;
	protected int luckModifier = 0;
	protected int speedModifier = 0;

	public ItemFishingRodAdv(ToolMaterial toolMaterial, String modid, String name) {

		this.toolMaterial = toolMaterial;
		setUnlocalizedName(modid + ":" + name);
		setRegistryName(name);
		this.setMaxDamage(toolMaterial.getMaxUses());
	}

	public ItemFishingRodAdv setRepairIngot(String repairIngot) {

		this.repairIngot.add(repairIngot);
		return this;
	}

	public ItemFishingRodAdv setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	public ItemFishingRodAdv setLuckModifier(int luckMod) {

		luckModifier = luckMod;
		return this;
	}

	public ItemFishingRodAdv setSpeedModifier(int speedMod) {

		speedModifier = speedMod;
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

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {

		if (player.fishEntity != null) {
			int i = player.fishEntity.handleHookRetraction();
			stack.damageItem(i, player);
			player.swingArm(hand);
		} else {
			world.playSound(player, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote) {
				world.spawnEntityInWorld(new EntityCoFHFishHook(world, player, luckModifier, speedModifier));
			}
			player.swingArm(hand);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}
	
	@Override
	public boolean preInit()
	{
		GameRegistry.register(this);
		return true;
	}
	
	@Override
	public boolean initialize() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(this.getUnlocalizedName().substring(5), "inventory"));
		return true;
	}

	@Override
	public boolean postInit()
	{
		return true;
	}

}
