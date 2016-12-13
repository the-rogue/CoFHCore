package cofh.core.item;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
import cofh.core.render.CoFHFontRenderer;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;

public class ItemBase extends Item {

	public class ItemEntry {

		public String name;
		public int rarity = 0;
		public int maxDamage = 0;
		public boolean altName = false;

		public ItemEntry(String name, int rarity, int maxDamage) {

			this.name = name;
			this.rarity = rarity;
			this.maxDamage = maxDamage;
		}

		public ItemEntry(String name, int rarity) {

			this.name = name;
			this.rarity = rarity;
		}

		public ItemEntry(String name) {

			this.name = name;
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

	public ItemBase() {

		setHasSubtypes(true);
	}

	public ItemBase(String modName) {

		this.modName = modName;
		setHasSubtypes(true);
	}

	public ItemBase setHasTextures(boolean hasTextures) {

		this.hasTextures = hasTextures;
		return this;
	}

	public boolean addItem(int number, ItemEntry entry, boolean register) {

		if (itemMap.containsKey(Integer.valueOf(number))) {
			return false;
		}
		itemMap.put(Integer.valueOf(number), entry);
		itemList.add(Integer.valueOf(number));
		if (register) {
			GameRegistry.register(this, new ResourceLocation(entry.name));
		}
		return true;
	}

	public boolean addItem(int number, ItemEntry entry) {

		return addItem(number, entry, true);
	}

	public boolean addItem(int number, String name, int rarity, boolean register) {

		return addItem(number, new ItemEntry(name, rarity), register);
	}

	public boolean addItem(int number, String name, int rarity) {

		return addItem(number, name, rarity, true);
	}

	public boolean addItem(int number, String name) {

		return addItem(number, name, 0);
	}

	public boolean addOreDictItem(int number, String name, int rarity) {
		boolean registered = addItem(number, name, rarity);
		ItemStack stack = new ItemStack(this, 0, number);
		OreDictionary.registerOre(name, stack);

		return registered;
	}

	public boolean addOreDictItem(int number, String name) {

		boolean registered = addItem(number, name);
		ItemStack stack =  new ItemStack(this, 0, number);
		OreDictionary.registerOre(name, stack);

		return registered;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

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
			return new StringBuilder().append(getUnlocalizedName()).append('.').append(item.name).append("Alt").toString();
		}
		return new StringBuilder().append("item.").append(getUnlocalizedName()).append('.').append(item.name).toString();
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

		GameRegistry.register(this);
		name = modName + ":" + name;
		return super.setUnlocalizedName(name);
	}

	public void registertexture() {

		if (!hasTextures) {
			return;
		}
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(this.getUnlocalizedName().substring(5), "inventory"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack) {

		return CoFHFontRenderer.loadFontRendererStack(stack);
	}

}
