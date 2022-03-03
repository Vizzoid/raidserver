package org.vizzoid.raidserver.raidserver.minecraft.mechanics.mystical;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import java.util.List;

public class MysticalUtils extends Utility {

  public static final Key KEY = new Key("MYSTICAL_DROP");

  // Assumption of viability
  public static ItemStack item(@NotNull Mystical m1, Material m, Player p) {
    ItemStack item = new ItemStack(m);
    ItemMeta meta = item.getItemMeta();

    String name = m.name().replace("_", " ");
    TextComponent text = Component.text("Mystical " + WordUtils.capitalizeFully(name));
    text = text.color(Color.DARK_PURPLE);
    text = text.decoration(TextDecoration.ITALIC, false);

    new Data(meta).set(KEY, m1.name());

    meta.displayName(text);
    if (p != null && root(p, m1).isDone()) {
      meta.lore(lore(m1.desc()));
    } else meta.lore(lore("Eat it to find out it's effect!"));
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

    item.setItemMeta(meta);
    item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    return item;
  }

  /**
   * @return immutable list of lore contents
   */
  private static List<Component> lore(String desc) {
    return List.of(Component.text(desc));
  }

  public static AdvancementProgress root(Player p, Mystical m) {
    return getProgress(p, "mystical", m.name().toLowerCase());
  }

  public static boolean is(ItemStack item) {
    return new Data(item.getItemMeta()).has(KEY);
  }

  public static void potion(LivingEntity p, PotionEffectType type) {
    potion(p, type, 0);
  }

  public static void potion(LivingEntity p, PotionEffectType type, int amplifier) {
    potion(p, type, amplifier, 45);
  }

  public static void potion(LivingEntity p, PotionEffectType type, int amplifier, float durationInSeconds) {
    p.addPotionEffect(new PotionEffect(type, Math.round(durationInSeconds * 20), amplifier, false, true));
  }


}
