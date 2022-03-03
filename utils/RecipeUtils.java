package org.vizzoid.raidserver.raidserver.minecraft.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.summons.SummonUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeUtils {

  private static final Map<ArmorSet, List<NamespacedKey>> armorRecipes = new HashMap<>();

  public static Map<ArmorSet, List<NamespacedKey>> armorRecipes() {
    return armorRecipes;
  }

  public static List<NamespacedKey> getArmorRecipes(ArmorSet set) {
    return armorRecipes().get(set);
  }

  public static void addArmorRecipes(ArmorSet set, Key key) {
    if (armorRecipes().containsKey(set)) {
      armorRecipes().get(set).add(key.to());
    } else armorRecipes().put(set, new ArrayList<>(List.of(key.to())));
  }

  public static void grant(ArmorSet set, Player p) {
    p.discoverRecipes(getArmorRecipes(set));
  }

  public static void addRecipes() {
    ArmorUtils.recipe();
    SummonUtils.recipe();
  }

}
