package org.vizzoid.raidserver.raidserver.minecraft.mechanics.pvp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;

public class PVPEvents extends MinecraftListener {

  /**
   * LOWEST priority on gameplay events is reserved for mainly hub-related events, LOW is used instead
   * This isn't the same if the event isn't gameplay (PlayerLogin, Chat, etc)
   */
  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerAttackPlayer(EntityDamageByEntityEvent e) {
    PVP.passEvent(e);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    PVP.createManager(e.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    PVP.destroyManager(e.getPlayer());
  }

}
