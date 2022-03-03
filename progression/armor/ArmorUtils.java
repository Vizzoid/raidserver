package org.vizzoid.raidserver.raidserver.minecraft.progression.armor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.miner.Miner;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorPiece;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.utils.RecipeUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ArmorUtils extends Utility {

  public static final Key KEY = new Key("CLASS_ARMOR");

  public static List<Material> swords() {
    List<Material> list = new ArrayList<>();
    list.add(Material.DIAMOND_SWORD);
    list.add(Material.GOLDEN_SWORD);
    list.add(Material.IRON_SWORD);
    list.add(Material.NETHERITE_SWORD);
    list.add(Material.STONE_SWORD);
    list.add(Material.WOODEN_SWORD);
    return list;
  }

  public static List<Material> axes() {
    List<Material> list = new ArrayList<>();
    list.add(Material.DIAMOND_AXE);
    list.add(Material.GOLDEN_AXE);
    list.add(Material.IRON_AXE);
    list.add(Material.NETHERITE_AXE);
    list.add(Material.STONE_AXE);
    list.add(Material.WOODEN_AXE);
    return list;
  }

  public static List<Material> pickaxes() {
    List<Material> list = new ArrayList<>();
    list.add(Material.DIAMOND_PICKAXE);
    list.add(Material.GOLDEN_PICKAXE);
    list.add(Material.IRON_PICKAXE);
    list.add(Material.NETHERITE_PICKAXE);
    list.add(Material.STONE_PICKAXE);
    list.add(Material.WOODEN_PICKAXE);
    return list;
  }

  public static List<Material> shovels() {
    List<Material> list = new ArrayList<>();
    list.add(Material.DIAMOND_SHOVEL);
    list.add(Material.GOLDEN_SHOVEL);
    list.add(Material.IRON_SHOVEL);
    list.add(Material.NETHERITE_SHOVEL);
    list.add(Material.STONE_SHOVEL);
    list.add(Material.WOODEN_SHOVEL);
    return list;
  }

  public static List<Material> hoes() {
    List<Material> list = new ArrayList<>();
    list.add(Material.DIAMOND_HOE);
    list.add(Material.GOLDEN_HOE);
    list.add(Material.IRON_HOE);
    list.add(Material.NETHERITE_HOE);
    list.add(Material.STONE_HOE);
    list.add(Material.WOODEN_HOE);
    return list;
  }

  public static List<Material> bow() {
    List<Material> list = new ArrayList<>();
    list.add(Material.BOW);
    return list;
  }

  public static List<Material> shield() {
    List<Material> list = new ArrayList<>();
    list.add(Material.SHIELD);
    return list;
  }

  public static List<Material> crossbow() {
    List<Material> list = new ArrayList<>();
    list.add(Material.CROSSBOW);
    return list;
  }

  public static List<Material> all() {
    List<Material> list = new ArrayList<>();
    list.addAll(swords());
    list.addAll(axes());
    list.addAll(hoes());
    list.addAll(pickaxes());
    list.addAll(shovels());
    list.addAll(bow());
    list.addAll(crossbow());
    list.addAll(shield());
    return list;
  }

  public static List<Action> right() {
    List<Action> list = new ArrayList<>();
    list.add(Action.RIGHT_CLICK_AIR);
    list.add(Action.RIGHT_CLICK_BLOCK);
    return list;
  }

  public static List<Action> left() {
    List<Action> list = new ArrayList<>();
    list.add(Action.LEFT_CLICK_AIR);
    list.add(Action.LEFT_CLICK_BLOCK);
    return list;
  }

  public static Component text(String s) {
    return Component.text(s).decoration(TextDecoration.ITALIC, false);
  }

  public static ItemStack item(ArmorPiece armor, Armor set) {
    ItemStack item = new ItemStack(set.result(armor));
    ItemMeta meta = item.getItemMeta();
    // ToDo streamline this
    if (set instanceof Miner m) m.color(meta);

    List<Component> singleClass = new ArrayList<>();
    singleClass.add(text(" "));
    singleClass.add(text("Wear the full set to get these bonuses:"));
    singleClass.add(text(" "));
    singleClass.addAll(set.lore());

    meta.displayName(text(set.name() + armor.get()));
    meta.lore(singleClass);
    new Data(meta).set(KEY, set.set().name());

    addAttributes(meta, armor, set);

    meta.setUnbreakable(true);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    item.setItemMeta(meta);
    item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    return item;
  }

  /**
   * Make sure to update ItemMeta after this method!
   *
   * @param meta  of item
   * @param armor slot of item
   * @param set   that it belongs to
   */
  public static void addAttributes(ItemMeta meta, ArmorPiece armor, Armor set) {
    meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(
      UUID.randomUUID(), "generic.armor.0", armor.armor(), AttributeModifier.Operation.ADD_NUMBER, armor.slot()));
    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(
      UUID.randomUUID(), "generic.armor_toughness.0", armor.armorToughness(set.isSmith()), AttributeModifier.Operation.ADD_NUMBER, armor.slot()));
    if (set.isSmith()) {
      meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(
        UUID.randomUUID(), "generic.knockback_resistance.0", armor.resistance(), AttributeModifier.Operation.ADD_NUMBER, armor.slot()));
    }

    for (AttributeData e : set.attributes()) {
      meta.addAttributeModifier(e.attribute(), new AttributeModifier(UUID.randomUUID(), "generic." + e.attribute().name(), e.amount(), AttributeModifier.Operation.ADD_NUMBER, armor.slot()));
    }
  }

  protected static void recipe(ArmorPiece piece, Armor set) {
    if (set.material() != null) {
      Key key = new Key(set.name() + "_" + piece.name());
      if (!set.isSmith()) {
        ShapedRecipe recipe = new ShapedRecipe(key.to(), item(piece, set))
          .shape(piece.shape());
        recipe.setIngredient('M', new RecipeChoice.ExactChoice(set.material()));

        getPlugin().getServer().addRecipe(recipe);
        RecipeUtils.addArmorRecipes(set.set(), key);
      } else {
        ItemStack result = item(piece, set);
        // ExactChoice isn't working smh
        RecipeChoice base = new RecipeChoice.MaterialChoice(item(piece, Objects.requireNonNull(set.relatedSet()).init()).getType());
        RecipeChoice addon = new RecipeChoice.MaterialChoice(set.material().getType());

        SmithingRecipe recipe = new SmithingRecipe(
          key.to(), result, base, addon);
        getPlugin().getServer().addRecipe(recipe);
        RecipeUtils.addArmorRecipes(set.set(), key);
      }
    }
  }

  public static void recipe() {
    for (ArmorSet set : ArmorSet.values()) {
      for (ArmorPiece p : ArmorPiece.values()) {
        recipe(p, set.init());

      }
    }
  }
}
