package org.vizzoid.raidserver.raidserver.minecraft.hub.hub;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.WorldUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;

import javax.annotation.CheckReturnValue;

/**
 * All event listeners are played first/last to ensure that no other events are called if cancelled
 * for example pvp messages when pvp is disabled in hub
 */
public class HubEvents extends MinecraftListener {

  @CheckReturnValue
  private static boolean isInHub(Location loc, @NotNull Player p) {
    return isInHub(loc) && p.getGameMode() != GameMode.CREATIVE;
  }

  @CheckReturnValue
  private static boolean isInHub(Location loc) {
    return (HubUtils.hubWorld().equals(loc.getWorld()));
  }

  public static void quit(Player p, boolean disable) {
    if (isInHub(p.getLocation(), p)) {
      PlayerData data = HubUtils.get(p.getUniqueId());
      HubUtils.remove(p.getUniqueId());
      if (data != null) {
        data.normalize(p, disable);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void playerBreakHub(BlockBreakEvent e) {
    if (isInHub(e.getBlock().getLocation(), e.getPlayer())) {
      e.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void explodeHub(BlockExplodeEvent e) {
    if (isInHub(e.getBlock().getLocation())) {
      e.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void entityExplodeHub(EntityExplodeEvent e) {
    if (isInHub(e.getEntity().getLocation())) {
      e.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void playerInteractHub(PlayerInteractEvent e) {
    if (e.getClickedBlock() == null) {
      if (isInHub(e.getPlayer().getLocation(), e.getPlayer())) {
        e.setCancelled(true);
      }
    } else if (!(e.getClickedBlock().getBlockData() instanceof Powerable && ArmorUtils.right().contains(e.getAction()))) {
      if (isInHub(e.getPlayer().getLocation(), e.getPlayer())) {
        e.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void playerDamageHub(EntityDamageEvent e) {
    if (isInHub(e.getEntity().getLocation())) {
      if (e.getEntity() instanceof Player p && e.getCause() == EntityDamageEvent.DamageCause.VOID)
        p.teleport(HubUtils.hub());

      e.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void playerFoodChange(FoodLevelChangeEvent e) {
    if (isInHub(e.getEntity().getLocation())) {
      e.setCancelled(true);
      e.getEntity().setExhaustion(20);
      e.getEntity().setSaturation(20);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerLeaveHub(PlayerPortalEvent e) {
    if (isInHub(e.getFrom(), e.getPlayer())) {
      Player p = e.getPlayer();
      PlayerData data = HubUtils.get(p.getUniqueId());
      HubUtils.remove(p.getUniqueId());
      if (data != null) {
        data.normalize(p, true);
      } else p.teleport(WorldUtils.world().getSpawnLocation());
      e.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    PlayerData data = new PlayerData(p);
    if (!isInHub(p.getLocation(), p)) {
      HubUtils.put(p.getUniqueId(), data);
      data.reduce(p, true);
    } else data.reduce(p, false);
    if (isInHub(p.getLocation())) {
      data.addMusic(p);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerQuit(PlayerQuitEvent e) {
    quit(e.getPlayer(), true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEndermanSpawn(CreatureSpawnEvent e) {
    if (e.getEntityType() == EntityType.ENDERMAN) {
      if (isInHub(e.getLocation())) {
        e.setCancelled(true);
        if (e.getEntity().isValid()) e.getEntity().remove();
      }
    }
  }

}
