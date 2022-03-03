package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.blindWidow;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.magmaOpus.MagmaBossEntity;
import org.vizzoid.raidserver.raidserver.minecraft.utils.LocationUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.Particles;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

public class SpiderBossEntity extends Spider {

  public final Map<Entity, Web> explosives1 = new HashMap<>();
  public final Map<Block, Web> explosives2 = new HashMap<>();
  private final Scheduler scheduler;
  private final SpiderBoss boss;
  public boolean isHiding = false;
  private Location[] locs;

  public SpiderBossEntity(Location loc, SpiderBoss boss) {
    super(EntityType.SPIDER, NMS.to(loc.getWorld()));

    this.scheduler = new Scheduler();
    this.boss = boss;

    this.setCustomName(new TextComponent("Blind Widow"));
    this.setCustomNameVisible(true);

    this.goalSelector.removeGoal(new LookAtPlayerGoal(this, net.minecraft.world.entity.player.Player.class, 8.0F));

    Class<?> targetGoal = Spider.class.getDeclaredClasses()[1];
    Constructor<?> constructor = targetGoal.getDeclaredConstructors()[0];
    constructor.setAccessible(true);

    try {
      this.targetSelector.removeGoal((Goal) constructor.newInstance(this, net.minecraft.world.entity.player.Player.class));
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    this.setPos(loc.getX(), loc.getY(), loc.getZ());
    NMS.to(loc.getWorld()).addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

    startEffects();
  }

  private static Collection<LivingEntity> nearby(LivingEntity entity) {
    Collection<LivingEntity> nearby = nearby(entity.getLocation(), 20);
    nearby.remove(entity);
    return nearby;
  }

  private static Collection<LivingEntity> nearby(Location l, double radius) {
    return l.getNearbyLivingEntities(radius);
  }

  public void ghost(boolean ghost) {
    if (!ghost) {
      this.setSilent(false);
      //this.setInvisible(false);
      this.setInvulnerable(false);

      isHiding = false;

      mob().setVelocity(location().add(0, 1, 0).toVector().subtract(location().toVector()));
      //location().getNearbyPlayers(50).forEach(p -> Deprecation.showEntity(p, mob()));
    } else {
      this.setSilent(true);
      this.setInvulnerable(true);
      //mob().setInvisible(true);

      isHiding = true;
      //location().getNearbyPlayers(50).forEach(p -> Deprecation.hideEntity(p, mob()));
    }
  }

  public void dig() {
    mob().setVelocity(location().add(0, 1, 0).toVector().subtract(location().toVector()));
    scheduler.repeat("GROUND_CHECK", () -> {
      if (mob().isOnGround()) {
        scheduler.cancel("GROUND_CHECK");
        ghost(true);
        locs = new Location[]{location(), location()};

        scheduler.repeat("PARTICLES", () -> {
          Particles.spawn(locs[0].setDirection(new Vector(0, 1, 0)), Particle.BLOCK_DUST, Material.DIRT, 5);
          Sound.location(Sound.Effect.GRASS_DIG, locs[0]);
        }, 0, 5);
        if (!mob().isOnGround()) {
          mob().teleport(location().toHighestLocation().add(0, 1, 0));
        }
        scheduler.repeat("CHECK", () -> {
          while (location().subtract(0, 1, 0).getBlock().isPassable()) {
            mob().teleport(location().subtract(0, 1, 0));
          }
          nearby(mob()).forEach(l -> {
            if (canSense(l)) {
              advance(l);
            }
          });
          if (locs[0] != locs[1] || !LocationUtils.equals(location(), locs[0])) {
            locs[1] = locs[0];
            mob().teleport(locs[0]);
          }

          if (!locs[0].getNearbyPlayers(1).isEmpty()) {
            unDig();
          }
        }, 40, 5);
      }
    }, 10, 1);
  }

  public void advance(LivingEntity l) {
    Path path = this.getNavigation().createPath(NMS.to(l), 1);
    if (path != null) {
      Node n = getNextAdvance(path);
      path.advance();
      if (n != null) {
        Location loc = new Location(mob().getWorld(), n.x, n.y, n.z);
        while (loc.clone().subtract(0, 1, 0).getBlock().isPassable()) {
          loc.subtract(0, 1, 0);
        }
        locs[1] = locs[0];
        locs[0] = loc;
      }
    }
  }

  public void unDig() {
    ghost(false);
    scheduler.cancel("CHECK");
    scheduler.cancel("PARTICLES");

    createWeb(location().add(0, 1, 0), Web.EXPLOSIVE);
    for (int i = 0; i < Math.round((getHealth() * -0.0133) + 6); i++) {
      createWeb(location().add(0, 1, 0));
    }
  }

  private void createWeb(Location l, Web web) {
    FallingBlock webFall = mob().getWorld().spawnFallingBlock(l, Bukkit.createBlockData(Material.COBWEB));
    webFall.setVelocity(MagmaBossEntity.randVelocity(l, true).normalize().multiply(0.33));
    explosives1.put(webFall, web);
  }

  private void createWeb(Location l) {
    if (new Random().nextInt(10) < Math.round(getHealth() / 100)) {
      createWeb(l, Web.random());
    }
  }

  public boolean canSense(Player p) {
    return location().distanceSquared(p.getLocation()) <= 4 ||
      p.isSprinting() || p.isJumping() || p.getItemInUse() != null ||
      (p.getActiveItem() != null && p.getActiveItem().getType() != Material.AIR) || p.getAttackCooldown() < 1;
  }

  public boolean canSense(LivingEntity e) {
    return e instanceof Player p ? canSense(p) : (location().distanceSquared(e.getLocation()) <= 4 ||
      e.isJumping() || e.isClimbing() || e.isGliding() || e.isSwimming());
  }

  private Node getNext(Path path) {
    return path.getNextNodeIndex() > 0 ? (path.getNextNodeIndex() >= path.nodes.size() ? null : path.getNextNode()) : getNextAdvance(path);
  }

  private Node getNextAdvance(Path path) {
    path.advance();
    return getNext(path);
  }

  public org.bukkit.entity.Spider mob() {
    return (org.bukkit.entity.Spider) this.getBukkitMob();
  }

  public Location location() {
    return mob().getLocation();
  }

  public void startEffects() {
    scheduler.repeat("EFFECT_TIMER", () -> {
      explosives1.forEach((k, v) -> {
        if (v.particle() != null) {
          Particles.spawn(k.getLocation(), Particle.SPELL_MOB, null);
        }

        Location l = k.getLocation();
        if (nearby(l, 0.5).size() > 0) {
          v.broken(l, boss);
          v.placed(l, boss);
          k.remove();
        }
      });
      explosives2.forEach((k, v) -> {
        if (v.particle() != null) {
          Particles.spawn(k.getLocation(), Particle.SPELL_MOB, null);
        }
      });
    }, 5, 5);
  }

  public enum Web {

    EXPLOSIVE(((l, b) -> {
      Particles.spawn(l, Particle.EXPLOSION_HUGE, null);
      nearby(l, 5).forEach(e -> {
        e.damage(10);
        e.setVelocity(e.getLocation().toVector().subtract(l.toVector()));

        if (e instanceof Mob m && new Data(m).has(Boss.MINION_KEY)) {
          SpiderBoss.confuse(m, new Scheduler());
        }
      });
    }), ((l, b) -> {
    }), new PotionData(PotionType.STRENGTH)),

    POISON(((l, b) -> {
      Particles.spawn(l, Particle.SPELL_INSTANT, null);
      nearby(l, 5).forEach(e -> e.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10 * 20, 1, false, true)));
    }), ((l, b) -> {
      b.minion(CaveSpider.class, l);
    }), new PotionData(PotionType.POISON)),

    INVISIBLE(((l, b) -> {
    }), ((l, b) -> {
      LivingEntity s = b.minion(org.bukkit.entity.Spider.class, l);
      s.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 1, false, false));
      new Data(s).set(Boss.MINION_KEY, "MINION");
    }), new PotionData(PotionType.INVISIBILITY)),

    SPLITTER(((l, b) -> {
      Particles.spawn(l, Particle.EXPLOSION_LARGE, null);
      for (int i = 0; i < 3; i++) {
        b.minion(Silverfish.class, l);
      }
    }), ((l, b) -> {
      b.minion(org.bukkit.entity.Spider.class, l);
    }), new PotionData(PotionType.SLOWNESS)),

    NORMAL(((l, b) -> {
      nearby(l, 1).forEach(e -> e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2 * 20, 240, false, true)));
    }), ((l, b) -> {
    }), null);

    private final BiConsumer<Location, SpiderBoss> broken;
    private final BiConsumer<Location, SpiderBoss> placed;
    private final PotionData particle;

    Web(BiConsumer<Location, SpiderBoss> broken, BiConsumer<Location, SpiderBoss> placed, PotionData particle) {
      this.broken = broken;
      this.placed = placed;
      this.particle = particle;
    }

    public static Web random() {
      return values()[new Random().nextInt(values().length)];
    }

    public void broken(Location l, SpiderBoss boss) {
      broken.accept(l, boss);
    }

    public void placed(Location l, SpiderBoss boss) {
      placed.accept(l, boss);
    }

    public PotionData particle() {
      return particle;
    }
  }

}
