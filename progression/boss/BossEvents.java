package org.vizzoid.raidserver.raidserver.minecraft.progression.boss;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.blindWidow.SpiderBoss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.blindWidow.SpiderBossEntity;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.magmaOpus.MagmaBoss;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;

import java.util.function.BiConsumer;

public class BossEvents extends MinecraftListener {

  @EventHandler
  public void onBossOrMinionAttacked(EntityDamageEvent e) {
    Data data = new Data(e.getEntity());
    if (data.has(Boss.MINION_KEY)) {
      switch (e.getCause()) {
        case ENTITY_ATTACK, FIRE, PROJECTILE, ENTITY_SWEEP_ATTACK,
          FIRE_TICK, THORNS, FALL, BLOCK_EXPLOSION,
          ENTITY_EXPLOSION, POISON, MAGIC, CUSTOM -> {
          Boss.handle(e);
          if (data.get(Boss.MINION_KEY).equals("SPAWNER") && e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            e.setCancelled(true);
          }
        }

        case CONTACT, SUFFOCATION, LAVA, MELTING, DROWNING, VOID, LIGHTNING,
          WITHER, SUICIDE, STARVATION, FALLING_BLOCK, DRAGON_BREATH, FLY_INTO_WALL,
          HOT_FLOOR, CRAMMING, DRYOUT, FREEZE -> e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onSpiderBossWeb(EntityBlockFormEvent e) {
    iterate((k, v) -> {
      if (v instanceof SpiderBoss s) {
        if (s.boss().explosives1.containsKey(e.getEntity())) {
          SpiderBossEntity.Web web = s.boss().explosives1.get(e.getEntity());
          s.boss().explosives2.put(e.getBlock(), web);
          web.placed(e.getBlock().getLocation(), s);
          s.boss().explosives1.remove(e.getEntity());
          if (web != SpiderBossEntity.Web.EXPLOSIVE) {
            e.setCancelled(true);
            e.getEntity().remove();
          }
        }
      }
    });
  }

  @EventHandler
  public void onBossDeath(EntityDeathEvent e) {
    Boss.handle(e);

    if (new Data(e.getEntity()).has(MagmaBoss.SPLIT_KEY)) {
      for (Player nearbyPlayer : e.getEntity().getLocation().getNearbyPlayers(4)) {
        nearbyPlayer.damage(5);
        nearbyPlayer.setVelocity(e.getEntity().getLocation().toVector().subtract(nearbyPlayer.getLocation().toVector()).normalize().multiply(0.1));
      }
      iterate((k, v) -> {
        if (v instanceof MagmaBoss m) {
          if (m.boss().split.remove(e.getEntity())) {
            if (m.boss().split.isEmpty()) {
              m.boss().unSplit();
              m.scheduleSlam(true);
            }
          }
        }
      });
    }
  }

  @EventHandler
  public void onBossSummon(PlayerInteractEvent e) {
    if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
      Boss.create(e);
    }
  }

  @EventHandler
  public void onBowShot(EntityShootBowEvent e) {
    Boss.handle(e);
  }

  @EventHandler
  public void onBossPlayerBreakBlock(BlockBreakEvent e) {
    player(e.getPlayer(), () -> {
      e.setCancelled(true);
      e.getPlayer().sendMessage(Component.text("You can't break blocks during a boss fight!").decoration(TextDecoration.BOLD, true).color(Color.RED));
    });
  }

  @EventHandler
  public void onBossPlayerPlaceBlock(BlockPlaceEvent e) {
    player(e.getPlayer(), () -> {
      e.setCancelled(true);
      e.getPlayer().sendMessage(Component.text("You can't place blocks during a boss fight!").decoration(TextDecoration.BOLD, true).color(Color.RED));
    });
  }

  @EventHandler
  public void onPlayerDieInBoss(PlayerDeathEvent e) {
    player(e.getPlayer(), () -> {
      e.setKeepLevel(true);
      e.setKeepInventory(true);

      e.getDrops().clear();
      e.setDroppedExp(0);
    });
  }

  @EventHandler
  public void onTNTExplode(EntityExplodeEvent e) {
    iterate((k, v) -> {
      if (v.world.equals(e.getLocation().getWorld()) && e.getLocation().distanceSquared(v.get().getLocation()) <= 2500) {
        e.blockList().clear();
      }
    });
  }

  @EventHandler
  public void onBossEnterPortal(EntityPortalEvent e) {
    if (new Data(e.getEntity()).has(Boss.MINION_KEY)) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onMinionTarget(EntityTargetLivingEntityEvent e) {
    if (e.getTarget() != null) {
      if (new Data(e.getTarget()).has(Boss.MINION_KEY) || new Data(e.getTarget()).has(Boss.MINION_KEY)) {
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onMinionSplit(SlimeSplitEvent e) {
    if (new Data(e.getEntity()).has(Boss.MINION_KEY)) {
      e.setCancelled(true);
    }
  }

  // Entity, Boss
  private void iterate(BiConsumer<? super Entity, ? super Boss> runnable) {
    Boss.bossInstances.forEach(runnable);
  }

  private void player(Player player, Runnable runnable) {
    final boolean[] repeat = {true};
    iterate((k, v) -> {
      if (v.nearby.containsKey(player) && repeat[0]) {
        runnable.run();
        repeat[0] = false;
      }
    });

  }

}
