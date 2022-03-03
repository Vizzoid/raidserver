package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WranglerUtils extends Utility {

  public static Key KEY = new Key("IS_WRANGLER");
  public static Key LEVEL_KEY = new Key("WRANGLER_LEVEL");

  /**
   * @param level of wrangler
   * @return wrangler item
   */
  public static ItemStack wrangler(int level) {
    ItemStack item = new ItemStack(Material.LEAD);
    ItemMeta meta = item.getItemMeta();

    List<Component> lore = new ArrayList<>();
    lore.add(Component.text(""));
    lore.add(Component.text("Capture and befriend mobs (and humans!) with this").decoration(TextDecoration.ITALIC, false));
    lore.add(Component.text("The more bosses you beat, the level increases!").decoration(TextDecoration.ITALIC, false));
    lore.add(Component.text("").decoration(TextDecoration.ITALIC, true));

    lore.addAll(lore(level));
    new Data(meta).set(KEY, true);

    meta.lore(lore);
    meta.displayName(Component.text("Wrangler - Level " + level).decoration(TextDecoration.ITALIC, false));

    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(meta);

    item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    return item;
  }

  /**
   * @param level of wrangler
   * @return lore of level
   * throws IllegalStateException if level is not in between 0-5
   */
  private static List<Component> lore(int level) {
    List<Component> lore = new ArrayList<>();

    // LIMIT:                          "LEVEL, LEVEL, LEVEL, LEVEL, LEVEL, LEVEL, LEVEL, LEVEL, LEVEL, LEVEL"
    switch (level) {
      case 0 -> {
        lore.add(text("You can't use this yet!"));
        lore.add(text("Beat a boss to level this up!", true));
      }
      case 1 -> {
        lore.add(text("Level 1: All passive mobs except Axolotl, Cat, Horses, Fox,"));
        lore.add(text("         Parrot, Ocelot, Strider, Snow Golem, or Villagers"));
      }
      case 2 -> {
        lore.add(text("Level 2: All neutral mobs except Spiders, Endermen,"));
        lore.add(text("         Iron Golem, Wolf, and Piglins"));
        lore.add(text("         Now includes Snow Golem, Ocelot, and Fox."));
      }
      case 3 -> {
        lore.add(text("Level 3: All hostile mobs except Illagers, Elder Guardian,"));
        lore.add(text("         Piglin Brute, Wither Skeleton, Shulker, Ghast, and Creeper"));
        lore.add(text("         Now includes Piglins, Spiders and Endermen"));
      }
      case 4 -> {
        lore.add(text("Level 4: Every mob except Bosses"));
        lore.add(text("         You really want another level?", true));
      }
      default -> {
        lore.add(text("Level 5: EVERYTHING"));
        lore.add(text("         You sick bastard", true, false));
      }
    }
    return lore;
  }

  private static TextComponent text(String s) {
    return Component.text(s).decoration(TextDecoration.ITALIC, false).color(Color.RED);
  }

  private static TextComponent text(String s, boolean italics) {
    return text(s).decoration(TextDecoration.ITALIC, italics);
  }

  private static TextComponent text(String s, boolean italics, boolean color) {
    if (color) {
      return text(s, italics).color(Color.RED);
    } else {
      return Component.text(s).decoration(TextDecoration.ITALIC, italics);
    }
  }

  public static boolean isWrangler(ItemMeta meta) {
    return new Data(meta).has(KEY);
  }

  public static boolean isWrangler(@Nullable ItemStack item) {
    if (item != null && !item.getType().equals(Material.AIR)) {
      return isWrangler(item.getItemMeta());
    } else {
      return false;
    }
  }

  /**
   * @param p player
   * @return wrangler level of player
   */
  public static int level(Player p) {
    Data data = new Data(p);
    if (data.has(LEVEL_KEY)) {
      return Integer.parseInt(data.get(LEVEL_KEY));
    } else {
      return 0;
    }
  }

  /**
   * Adds player level by one
   *
   * @param p player
   */
  public static void addLevel(Player p) {
    new Data(p).set(LEVEL_KEY, String.valueOf(level(p) + 1));
  }

  /**
   * Sets player level, recommended for debug and nothing else
   *
   * @param p        player
   * @param newValue new level
   */
  public static void setLevel(Player p, int newValue) {
    new Data(p).set(LEVEL_KEY, String.valueOf(newValue));
  }

}
