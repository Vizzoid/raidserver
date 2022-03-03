package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.summons;

import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.essence.EssenceUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.RecipeUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import java.util.ArrayList;
import java.util.List;

public class SummonUtils extends Utility {

  public static void recipe() {
    for (Summon summon : Summon.values()) {
      recipe(summon);
    }
  }

  public static void recipe(Summon summon) {
    ItemStack item = new ItemStack(summon.material());
    ItemMeta meta = item.getItemMeta();

    meta.displayName(summon.get());
    meta.lore(new ArrayList<>(List.of(Component.text(""), summon.desc())));
    new Data(meta).set(Boss.ITEM_KEY, summon.name());

    item.setItemMeta(meta);
    item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    item.addItemFlags(ItemFlag.HIDE_ENCHANTS);

    Key key = new Key(summon.name() + "_SUMMON");
    ShapedRecipe recipe = new ShapedRecipe(key.to(), item);
    recipe.shape("EEE", "ESE", "EEE");
    recipe.setIngredient('E', EssenceUtils.essence(summon.carrier()));
    recipe.setIngredient('S', summon.material());

    getServer().addRecipe(recipe);
    RecipeUtils.addArmorRecipes(summon.carrier().set(), key);
  }

}
