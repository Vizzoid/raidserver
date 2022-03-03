package org.vizzoid.raidserver.raidserver.minecraft.progression.essence;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.BossType;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EssenceUtils extends Utility {

  public static final Key ESSENCE_KEY = new Key("ESSENCE_CARRIER");

  public static void essenceCarrier(LivingEntity entity) {
    for (Attribute a : Attribute.values()) {
      entity.registerAttribute(a);
      Objects.requireNonNull(entity.getAttribute(a)).addModifier(new AttributeModifier("essence.buff." + a.name(), 0.1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    }
    entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10000000, 1));
    new Data(entity).set(ESSENCE_KEY, true);
  }

  public static ItemStack essence(Carrier c) {
    ItemStack item = new ItemStack(c.essence());
    ItemMeta meta = item.getItemMeta();

    List<Component> lore = new ArrayList<>();
    lore.add(Component.text(""));
    lore.add(Component.text("Use this to build armor").decoration(TextDecoration.ITALIC, false));
    lore.add(Component.text("It's sticky...").decoration(TextDecoration.ITALIC, true));

    meta.displayName(Component.text(c.getName() + " Essence").decoration(TextDecoration.ITALIC, false));
    meta.lore(lore);

    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(meta);

    item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    return item;
  }

  public static void recipe() {

  }

  public static void recipe(BossType type) {

  }

}
