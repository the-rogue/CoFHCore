package cofh.core.item.tool;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import cofh.lib.util.helpers.BlockHelper;

public class ItemHammerAdv extends ItemToolAdv {

	@SuppressWarnings("unchecked")
	public ItemHammerAdv(ToolMaterial toolMaterial) {

		super(4.0F, toolMaterial);
		addToolClass("pickaxe");
		addToolClass("hammer");

		effectiveBlocks.addAll(ItemPickaxe.effectiveBlocks);
		effectiveMaterials.add(Material.IRON);
		effectiveMaterials.add(Material.ANVIL);
		effectiveMaterials.add(Material.ROCK);
		effectiveMaterials.add(Material.ICE);
		effectiveMaterials.add(Material.PACKED_ICE);
		effectiveMaterials.add(Material.GLASS);
		effectiveMaterials.add(Material.REDSTONE_LIGHT);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.worldObj;
		IBlockState block = world.getBlockState(pos);

		if (!canHarvestBlock(block, stack)) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
			return false;
		}
		boolean used = false;

		float refStrength = ForgeHooks.blockStrength(block, player, world, pos);
		if (refStrength != 0.0D && canHarvestBlock(block, stack)) {
			RayTraceResult rtr = BlockHelper.getCurrentRayTraceResult(player, true);
			IBlockState adjBlock;
			float strength;

			int x2 = pos.getX();
			int y2 = pos.getY();
			int z2 = pos.getZ();

			switch (rtr.sideHit) {
			case UP:
			case DOWN:
				for (x2 = rtr.blockPos.getX() - 1; x2 <= rtr.blockPos.getX() + 1; x2++) {
					for (z2 = rtr.blockPos.getZ() - 1; z2 <= rtr.blockPos.getZ() + 1; z2++) {
						adjBlock = world.getBlockState(new BlockPos(x2, y2, z2));
						strength = ForgeHooks.blockStrength(adjBlock, player, world, new BlockPos(x2, y2, z2));
						if (strength > 0f && refStrength / strength <= 10f) {
							used |= harvestBlock(world, x2, y2, z2, player);
						}
					}
				}
				break;
			case NORTH:
			case SOUTH:
				for (x2 = rtr.blockPos.getX() - 1; x2 <= rtr.blockPos.getX() + 1; x2++) {
					for (y2 = rtr.blockPos.getY() - 1; y2 <= rtr.blockPos.getY() + 1; y2++) {
						adjBlock = world.getBlockState(new BlockPos(x2, y2, z2));
						strength = ForgeHooks.blockStrength(adjBlock, player, world, new BlockPos(x2, y2, z2));
						if (strength > 0f && refStrength / strength <= 10f) {
							used |= harvestBlock(world, x2, y2, z2, player);
						}
					}
				}
				break;
			default:
				for (y2 = rtr.blockPos.getY() - 1; y2 <= rtr.blockPos.getY() + 1; y2++) {
					for (z2 = rtr.blockPos.getZ() - 1; z2 <= rtr.blockPos.getZ() + 1; z2++) {
						adjBlock = world.getBlockState(new BlockPos(x2, y2, z2));
						strength = ForgeHooks.blockStrength(adjBlock, player, world, new BlockPos(x2, y2, z2));
						if (strength > 0f && refStrength / strength <= 10f) {
							used |= harvestBlock(world, x2, y2, z2, player);
						}
					}
				}
				break;
			}
			if (used && !player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
		}
		return true;
	}

}
