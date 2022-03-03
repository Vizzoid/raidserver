package org.vizzoid.raidserver.raidserver.minecraft.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.minecraft.commands.tipsBook.TipsUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.utils.Comp;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.WorldUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.time.Duration;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PlayerEvents extends MinecraftListener {

  // What a mess...
  // Who would've known a respawn counter would be so hard...
  @EventHandler
  public void onPlayerKillPlayer(PlayerRespawnEvent e) {

    Player player = e.getPlayer();
    final int d;
    if (player.hasMetadata("deathTimer")) {
      d = player.getMetadata("deathTimer").get(0).asInt();
    } else {
      d = 5;
    }
    player.setGameMode(GameMode.SPECTATOR);

    for (int i = 0; i < d; i++) {
      int finalI = i;
      Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
        player.showTitle(Title.title(
          Component.text("You will respawn in " + (d - finalI) + " seconds").color(Color.RED),
          Component.text(""),
          Title.Times.of(Duration.ofMillis(150), Duration.ofMillis(750), Duration.ofMillis(150))));
        Sound.failure(player);
      }, 21 + (i * 21L));
    } // Respawn Event

    Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
      if (d < 40) {
        player.setMetadata("deathTimer", new FixedMetadataValue(getPlugin(), d * 2));
      } else {
        player.setMetadata("deathTimer", new FixedMetadataValue(getPlugin(), 40));
      }
      player.setGameMode(GameMode.SURVIVAL);
      player.teleport(e.getRespawnLocation(), TeleportCause.PLUGIN);
      Sound.success(player);
      Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> player.removeMetadata("deathTimer", getPlugin()), 1200);
    }, 21 + (d * 21L));
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCounterDie(PlayerQuitEvent e) {
    if (e.getPlayer().hasMetadata("deathTimer")) {
      e.getPlayer().setGameMode(GameMode.SURVIVAL);
      if (e.getPlayer().getBedSpawnLocation() != null) {
        e.getPlayer().teleport(e.getPlayer().getBedSpawnLocation());
      } else e.getPlayer().teleport(WorldUtils.world().getSpawnLocation());
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerJoin(PlayerJoinEvent e) {
    new Scheduler().delay("ALERT_DELAY", () -> Comp.msg(e.getPlayer(), Comp.getAlerts(e.getPlayer())), 3 * 20);
  }

  @EventHandler
  public void onAdvancementDone(PlayerAdvancementDoneEvent e) {
    if (isUtility(e.getAdvancement().getKey())) {
      TipsUtils.addAmount(e.getPlayer());

      int amount = TipsUtils.getAmount(e.getPlayer());
      if (amount == 9 || amount == 18 || amount == 54)
        e.getPlayer().sendMessage(Component.text("You got a tip! Use '/tips' to see your tips!").color(Color.GREEN));
    }
  }

  private boolean isUtility(NamespacedKey key) {
    return !key.getKey().startsWith("recipes") && !key.getNamespace().equalsIgnoreCase("mystical");
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onLukeAttackDan(EntityDamageByEntityEvent e) {
    if (e.getEntity().getName().equals("Monke_2077") &&
      e.getDamager().getName().equals("DanimalCrackers")) {
      e.setDamage(99999);
    }
  }

  @EventHandler
  public void onPlayerBecomeCreative(PlayerGameModeChangeEvent e) {
    if (e.getNewGameMode() == GameMode.CREATIVE) {
      Player p = e.getPlayer();

      for (ArmorSet set : ArmorSet.values()) {
        Armor a = set.init();
        p.sendMessage(Component.text(a.name()).color(Color.LIGHT_PURPLE).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/armor " + set.name())));
      }
    }
  }

}
