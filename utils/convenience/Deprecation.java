package org.vizzoid.raidserver.raidserver.minecraft.utils.convenience;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;

/**
 * Used to circumvent deprecation warnings.
 */
@SuppressWarnings("deprecation")
public class Deprecation extends Utility {

  public static void hideEntity(Player p, Entity e) {
    p.hideEntity(getPlugin(), e);
  }

  public static void showEntity(Player p, Entity e) {
    p.showEntity(getPlugin(), e);
  }

  public static Advancement getAdvancement(String root, String path) {
    return getServer().getAdvancement(new NamespacedKey(root, path));
  }

  public static OfflinePlayer getOfflinePlayer(String name) {
    return getServer().getOfflinePlayer(name);
  }

  public static boolean canSee(Player viewer, @NotNull Entity target) {
    return viewer.canSee(target);
  }

}
