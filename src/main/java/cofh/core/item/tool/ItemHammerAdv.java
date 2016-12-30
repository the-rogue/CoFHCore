package cofh.core.item.tool;

import java.util.Locale;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;
import cofh.lib.util.helpers.BlockHelper;

public class ItemHammerAdv extends ItemToolAdv {
	
	protected String modname;
	protected String name;
	
	public ItemHammerAdv(ToolMaterial toolMaterial, String modname, String name) {

		super(4.0F, 4.0F, toolMaterial);
		addToolClass("pickaxe");
		addToolClass("hammer");
		this.modname = modname;
		this.name = name;
		setUnlocalizedName(modname + ":" + name);
		setRegistryName(name);

		effectiveBlocks.addAll(new ItemPickaxeFake().geteffectiveblocks());
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
		IBlockState blockstate = world.getBlockState(pos);

		if (!canHarvestBlock(blockstate, stack)) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
			return false;
		}
		boolean used = false;

		float refStrength = ForgeHooks.blockStrength(blockstate, player, world, pos);
		if (refStrength != 0.0D && canHarvestBlock(blockstate, stack)) {
			RayTraceResult rtr = BlockHelper.getCurrentRayTraceResult(player, true);
			IBlockState adjBlock;
			float strength;

			int x2 = pos.getX();
			int y2 = pos.getY();
			int z2 = pos.getZ();

			switch (rtr.sideHit) {
			case UP:
			case DOWN:
				for (x2 = rtr.getBlockPos().getX() - 1; x2 <= rtr.getBlockPos().getX() + 1; x2++) {
					for (z2 = rtr.getBlockPos().getZ() - 1; z2 <= rtr.getBlockPos().getZ() + 1; z2++) {
						adjBlock = world.getBlockState(new BlockPos(x2, y2, z2));
						strength = ForgeHooks.blockStrength(adjBlock, player, world, new BlockPos(x2, y2, z2));
						if (strength > 0f && refStrength / strength <= 10f) {
							used |= harvestBlock(world, new BlockPos(x2, y2, z2), player);
						}
					}
				}
				break;
			case NORTH:
			case SOUTH:
				for (x2 = rtr.getBlockPos().getX() - 1; x2 <= rtr.getBlockPos().getX() + 1; x2++) {
					for (y2 = rtr.getBlockPos().getY() - 1; y2 <= rtr.getBlockPos().getY() + 1; y2++) {
						adjBlock = world.getBlockState(new BlockPos(x2, y2, z2));
						strength = ForgeHooks.blockStrength(adjBlock, player, world, new BlockPos(x2, y2, z2));
						if (strength > 0f && refStrength / strength <= 10f) {
							used |= harvestBlock(world, new BlockPos(x2, y2, z2), player);
						}
					}
				}
				break;
			default:
				for (y2 = rtr.getBlockPos().getY() - 1; y2 <= rtr.getBlockPos().getY() + 1; y2++) {
					for (z2 = rtr.getBlockPos().getZ() - 1; z2 <= rtr.getBlockPos().getZ() + 1; z2++) {
						adjBlock = world.getBlockState(new BlockPos(x2, y2, z2));
						strength = ForgeHooks.blockStrength(adjBlock, player, world, new BlockPos(x2, y2, z2));
						if (strength > 0f && refStrength / strength <= 10f) {
							used |= harvestBlock(world, new BlockPos(x2, y2, z2), player);
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
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(new ResourceLocation(modname, "tool/" + material.toLowerCase(Locale.US) + "/" + material + type), "inventory"));
		return true;
	}
	
	@Override
	public boolean postInit()
	{
		return true;
	}
	
	public class ItemPickaxeFake extends ItemPickaxe {

		public ItemPickaxeFake()
		{
			super(Item.ToolMaterial.WOOD);
		}
		public Set<Block> geteffectiveblocks(){
			return effectiveBlocks;
		}
	}

}
