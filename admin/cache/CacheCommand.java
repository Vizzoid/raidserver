package org.vizzoid.raidserver.raidserver.minecraft.admin.cache;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Tameable;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;

import javax.annotation.CheckReturnValue;
import java.util.ArrayList;
import java.util.HashMap;

public class CacheCommand extends MinecraftCommand {

  public static void handle(Flag flag, CommandSender sender) {
    switch (flag) {
      case ALL -> sender.sendMessage("Cleared " + Cache.clearAll() + " objects.");
      case SCHEDULERS -> sender.sendMessage("Stopped " + Cache.clearSchedulers() + " tasks.");
      case BOSSES -> sender.sendMessage("Removed " + Cache.clearBosses() + " bosses.");
      case MINIONS -> sender.sendMessage("Removed " + Cache.clearMinions() + " minions.");
      case MOBS -> sender.sendMessage("Removed " + Cache.clearMobs() + " mobs.");
      case DROPS -> sender.sendMessage("Cleared " + Cache.clearDrops() + " drops.");
    }
  }

  @Override
  public String name() {
    return "cache";
  }

  @Override
  public String description() {
    return "Clears the cache of a category";
  }

  @Override
  public TabCompleter tabCompleter() {
    return new CacheCommandTab();
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender.isOp()) {

      if (args.length >= 1) {
        handle(Flag.find(args[0]), sender);
      } else {
        sender.sendMessage("Cache command requires predetermined args");
      }
    }
    return true;
  }

  public enum Flag {
    ALL,
    SCHEDULERS,
    BOSSES,
    MINIONS,
    MOBS,
    DROPS;

    public static Flag find(String name) {
      for (Flag flag : values()) {
        if (flag.name().toLowerCase().equals(name)) {
          return flag;
        }
      }
      return ALL;
    }
  }

  public static class Cache {

    /**
     * Clears all (omits chunk number as it inflates number and provides little performance)
     */
    @CheckReturnValue
    public static int clearAll() {
      return clearSchedulers() +
        clearBosses() +
        clearDrops() +
        clearMobs();
    }

    /**
     * Clears schedulers
     * Possibly add function to re-add schedulers initialized on startup
     */
    @CheckReturnValue
    public static int clearSchedulers() {
      int size = getPlugin().getScheduler().getTasks().size();
      getPlugin().getScheduler().cancelAll();
      return size;
    }

    /**
     * Clears bosses and minions
     */
    @CheckReturnValue
    public static int clearBosses() {
      int size = Boss.bossInstances.size();
      new HashMap<>(Boss.bossInstances).forEach((k, v) -> v.get().damage(9999));
      return size;
    }

    /**
     * Clears minions
     */
    @CheckReturnValue
    public static int clearMinions() {
      final int[] size = {0};
      new HashMap<>(Boss.bossInstances).forEach((k, v) -> new ArrayList<>(v.minions).forEach(l -> {
        if (new Data(l).get(Boss.MINION_KEY).equals("MINION")) {
          l.damage(9999);
          size[0]++;
        }
      }));
      return size[0];
    }

    /**
     * Clears items
     */
    @CheckReturnValue
    public static int clearDrops() {
      final int[] size = {0};
      getPlugin().getServer().getWorlds().forEach(world -> {
        new ArrayList<>(world.getEntitiesByClass(Item.class)).forEach(i -> {
          i.remove();
          size[0]++;
        });
        new ArrayList<>(world.getEntitiesByClass(ExperienceOrb.class)).forEach(e -> {
          e.remove();
          size[0]++;
        });
      });
      return size[0];
    }

    /**
     * Clears non-persistent mobs
     */
    @CheckReturnValue
    public static int clearMobs() {
      final int[] size = {0};
      getPlugin().getServer().getWorlds().forEach(world -> new ArrayList<>(world.getLivingEntities()).forEach(l -> {
        if (l instanceof Mob && !(l instanceof Tameable) && (!l.isPersistent() || l.getRemoveWhenFarAway() || l.customName() == null)) {
          l.remove();
          size[0]++;
        }
      }));
      return size[0];
    }
  }
}
