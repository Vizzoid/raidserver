package org.vizzoid.raidserver.raidserver.minecraft.mechanics.pvp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;

import java.util.HashMap;
import java.util.Map;

// TODO add more pvp checking into actions
public class PVP extends Utility {

  private static final Map<Player, PVPManager> managerMap = new HashMap<>();

  /**
   * Check if event is possible event, if it is it will check if the damager is somehow a player and the victim is as well.
   * If this return true, it will alert the damager and cancel the event
   */
  // p is damager/cause, p1 is victim
  public static void passEvent(Event event) {
    if (event instanceof EntityDamageByEntityEvent e) {
      if (e.getEntity() instanceof Player p1) {
        if (e.getDamager() instanceof Player p) {
          check(p, p1, e);
        } else if (e.getDamager() instanceof TNTPrimed tnt) {
          if (tnt.getSource() instanceof Player p) {
            check(p, p1, e);
          }
        } else if (e.getDamager() instanceof Projectile projectile) {
          if (projectile.getShooter() instanceof Player p) {
            check(p, p1, e);
          }
        }
      }
    }
  }

  private static void check(Player cause, Player victim, Cancellable e) {
    PVPManager manager;
    if ((manager = getManager(cause)).isOn()) manager.cancel(e, true);
    if ((manager = getManager(victim)).isOn()) manager.cancel(e, false);
  }

  public static PVPManager getManager(Player p) {
    return managerMap.get(p);
  }

  /**
   * A 'has' check could be done, but its frankly unnecessary
   */
  static void createManager(@NotNull Player p) {
    managerMap.put(p, new PVPManagerImpl(p));
  }

  static void destroyManager(@NotNull Player p) {
    managerMap.remove(p);
  }

  public static TextComponent text(String s, TextColor color) {
    return Component.text(s, color).decoration(TextDecoration.BOLD, true);
  }

  public static void sendUpdate(CommandSender sender, boolean newValue) {
    if (newValue) deactivated(sender);
    else activated(sender);
  }

  public static void activated(CommandSender p) {
    p.sendMessage(text("PVP on!", Color.RED));
    Sound.failure(p);
  }

  public static void deactivated(CommandSender p) {
    p.sendMessage(text("PVP off!", Color.GREEN));
    Sound.success(p);
  }
}
