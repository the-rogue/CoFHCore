package cofh.core.item;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import cofh.api.core.IInitializer;
import cofh.core.render.CoFHFontRenderer;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;

public class ItemBase extends Item implements IInitializer {

	public class ItemEntry {

		public String name;
		public String modname;
		public ItemMeshDefinition mapper = new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation(ItemBase.this.modName + ":" + ItemBase.this.name + "/" + name, "inventory");
			}
		};
		public int rarity = 0;
		public int maxDamage = 0;
		public boolean altName = false;

		public ItemEntry(String modname, String name, int rarity, int maxDamage) {

			this.name = name;
			this.modname = modname;
			this.rarity = rarity;
			this.maxDamage = maxDamage;
		}

		public ItemEntry(String modname, String name, int rarity) {

			this.name = name;
			this.modname = modname;
			this.rarity = rarity;
		}

		public ItemEntry(String modname, String name) {

			this.name = name;
			this.modname = modname;
		}

		public ItemEntry useAltName(boolean altName) {

			this.altName = altName;
			return this;
		}
	}

	public TMap<Integer, ItemEntry> itemMap = new THashMap<Integer, ItemEntry>();
	public ArrayList<Integer> itemList = new ArrayList<Integer>(); // This is actually more memory efficient than a LinkedHashMap

	public boolean hasTextures = true;
	public String modName = "cofh";
	public String name = "";

	//Constructor must be called in preinit
	public ItemBase(@Nullable String modName) {this(modName, "itembase");}
	public ItemBase(@Nullable String modName, String name) {

		if (modName != null) {
			this.modName = modName;
		}
		this.name = name;
		setUnlocalizedName(name);
		setRegistryName(name);
		setHasSubtypes(true);
		GameRegistry.register(this);
	}

	public ItemBase setHasTextures(boolean hasTextures) {

		this.hasTextures = hasTextures;
		return this;
	}

	public ItemStack addItem(int number, ItemEntry entry) {

		if (itemMap.containsKey(Integer.valueOf(number))) {
			return null;
		}
		itemMap.put(Integer.valueOf(number), entry);
		itemList.add(Integer.valueOf(number));
		return new ItemStack(this, 1, number);
	}

	public ItemStack addItem(int number, String modname, String name, int rarity) {

		return addItem(number, new ItemEntry(modname, name, rarity));
	}

	public ItemStack addItem(int number, String modname, String name) {

		return addItem(number, modname, name, 0);
	}

	public ItemStack addOreDictItem(int number, String modname, String name, int rarity) {
		addItem(number, modname, name, rarity);
		ItemStack stack = new ItemStack(this, 1, number);
		OreDictionary.registerOre(name, stack);

		return stack;
	}

	public ItemStack addOreDictItem(int number, String modname, String name) {

		addItem(number, modname, name);
		ItemStack stack =  new ItemStack(this, 1, number);
		OreDictionary.registerOre(name, stack);

		return stack;
	}

	public String getRawName(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return "invalid";
		}
		return itemMap.get(i).name;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		int i = stack.getItemDamage();
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return EnumRarity.COMMON;
		}
		return EnumRarity.values()[itemMap.get(stack.getItemDamage()).rarity];
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {

		for (int i = 0; i < itemList.size(); i++) {
			list.add(new ItemStack(item, 1, itemList.get(i)));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return "item.invalid";
		}
		ItemEntry item = itemMap.get(i);

		if (item.altName) {
			return new StringBuilder().append(getUnlocalizedName().substring(0, getUnlocalizedName().indexOf(":") + 1)).append(item.name).append("Alt").toString();
		}
		return new StringBuilder().append(getUnlocalizedName().substring(0, getUnlocalizedName().indexOf(":") + 1)).append(item.name).toString();
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {

		return SecurityHelper.isSecure(stack);
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack stack) {

		if (SecurityHelper.isSecure(stack)) {
			location.setEntityInvulnerable(true);
			((EntityItem) location).lifespan = Integer.MAX_VALUE;
		}
		return null;
	}

	@Override
	public Item setUnlocalizedName(String name) {
		return super.setUnlocalizedName(modName + ":" + name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack) {

		return CoFHFontRenderer.loadFontRendererStack(stack);
	}
	
	@Override
	public boolean preInit()
	{
		for (int i : itemList) {
			ModelBakery.registerItemVariants(this, new ResourceLocation(modName, name + "/" + itemMap.get(i).name));
		} 
		return true;
	}
	
	@Override
	public boolean initialize() 
	{
		for (int i : itemList) {
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, i, new ModelResourceLocation(new ResourceLocation(modName, name + "/" + itemMap.get(i).name), "inventory"));
		}
		return true;
	}

	@Override
	public boolean postInit()
	{
		return true;
	}

}
