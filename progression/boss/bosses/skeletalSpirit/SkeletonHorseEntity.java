package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.skeletalSpirit;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.Skeleton;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.Vector;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.Particles;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.util.Collection;

public class SkeletonHorseEntity extends SkeletonHorse {

  public static double speed = 3;
  public static double multiplicityA = 1.5;
  public static double multiplicityB = 1;
  private final Skeleton entity;
  private final Scheduler scheduler;
  private boolean canReach;

  public SkeletonHorseEntity(Location loc, LivingEntity entity) {
    super(EntityType.SKELETON_HORSE, NMS.to(loc.getWorld()));
    this.entity = NMS.to((org.bukkit.entity.Skeleton) entity);

    this.setPos(loc.getX(), loc.getY(), loc.getZ());
    this.getBukkitMob().setInvulnerable(true);

    this.goalSelector.addGoal(1, new FloatGoal(this));
    NMS.to(loc.getWorld()).addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

    scheduler = new Scheduler();
  }

  public Skeleton getBoss() {
    return entity;
  }

  public void stand(boolean bool, Player player) {
    scheduler.cancelAll();
    this.setForceStanding(bool);
    this.setTarget(NMS.to(player), EntityTargetEvent.TargetReason.FORGOT_TARGET, false);
    entity.setTarget(NMS.to(player), EntityTargetEvent.TargetReason.FORGOT_TARGET, false);
    if (bool) {
      // Creates ghost entity that mimics bosses movement to location
      // If ghost reaches location, boss will follow path
      // Otherwise, boss will leap towards the player
      SkeletonHorse skeletonHorse = new GhostHorse(loc());
      skeletonHorse.getBukkitMob().setInvisible(true);
      skeletonHorse.getBukkitMob().setInvulnerable(true);

      Location l = player.getLocation();
      canReach = false;
      int id = scheduler.repeat("ATTEMPT_REACH_TARGET", () -> {
        skeletonHorse.getNavigation().moveTo(l.getX(), l.getY(), l.getZ(), speed);
        if (skeletonHorse.getBukkitEntity().getLocation().distanceSquared(l) <= 2) {
          canReach = true;
          scheduler.cancel("ATTEMPT_REACH_TARGET");
        }

      }, 0, 1);
      scheduler.cancelDelay(id, () -> skeletonHorse.getBukkitEntity().remove(), 3 * 20);
    }

  }

  public void charge(Player p) {
    if (!loc().getBlock().getType().equals(Material.WATER) && canReach) {
      Sound.location(Sound.Effect.ZOMBIE_VILLAGER_CURE, loc());

      int id = scheduler.repeat("CHARGE_AT_TARGET", () -> {
        Particles.spawn(loc(), Particle.LAVA, null);

        this.getNavigation().moveTo(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), speed);
        Collection<LivingEntity> touching = loc().getNearbyLivingEntities(1);
        touching.remove(this.getBukkitLivingEntity());
        touching.remove(getBoss().getBukkitLivingEntity());

        if (!touching.isEmpty()) {
          touching.forEach(e -> {
            e.damage(5);
            e.setVelocity(e.getLocation().toVector().subtract(loc().subtract(0, 1, 0).toVector()));
          });
        }
      }, 0, 1);
      scheduler.cancelDelay(id, 20);
    } else {
      Location l = p.getLocation();
      double x = (l.getX() + this.getX()) / 2;
      double y = (l.getY() + this.getY()) / 2;
      double z = (l.getZ() + this.getZ()) / 2;
      double distanceY = l.distance(loc()) / 2;

      Location pullTo = new Location(this.level.getWorld(), x, y + distanceY, z);
      Vector v = pullTo.toVector().subtract(loc().toVector());

      this.getBukkitMob().setVelocity(v.normalize().multiply(multiplicityA));
      Sound.location(Sound.Effect.ENDERDRAGON_FLAP, loc());

      int id = scheduler.repeat("SMASH", () -> {
        if (!loc().subtract(0, 0.0625, 0).getBlock().getType().equals(Material.AIR)) {
          Sound.location(Sound.Effect.IRON_GOLEM_BREAK, loc());
          Particles.spawn(loc(), Particle.EXPLOSION_LARGE, null);
          Collection<LivingEntity> players = loc().getNearbyLivingEntities(5);
          players.remove(this.getBukkitLivingEntity());
          players.remove(getBoss().getBukkitLivingEntity());

          for (LivingEntity player : players) {
            player.damage(5);
            player.setVelocity(player.getLocation().toVector().subtract(loc().subtract(0, 2, 0).toVector()).normalize().multiply(multiplicityB));
          }
          scheduler.cancel("SMASH");
        }
      }, 2, 1);
      scheduler.cancelDelay(id, 5 * 20);
    }
  }

  private Location loc() {
    return this.getBukkitMob().getLocation();
  }
}
