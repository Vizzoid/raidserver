package org.vizzoid.raidserver.raidserver.minecraft.mechanics.mystical;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Exploits ruined most of these, but they weren't that important,
 * ToDo fix exploit and find solutions
 */
public enum Mystical {

  BEETROOT("Gives great damange boost", p -> MysticalUtils.potion(p, PotionEffectType.INCREASE_DAMAGE, 2), PlayerItemConsumeEvent.class, Material.BEETROOT),
  /*
  CACTUS(p -> { // EXPLOIT
    Data data = new Data(p);
    MysticalUtils.potion(p, PotionEffectType.DAMAGE_RESISTANCE);
    data.set(MysticalUtils.KEY, "CACTUS");
    Scheduler.Sync.run(() -> data.remove(MysticalUtils.KEY, "CACTUS"), 45 * 20);
  }, PlayerInteractEvent.class, Material.CACTUS),*/
  CARROT("BOOOOOMMM!", p -> {
    // Instant saturation
    TNTPrimed tnt = p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
    tnt.setFuseTicks(0);
    tnt.setSource(p);
    new Data(tnt).set(MysticalUtils.KEY, "CARROT");
  }, PlayerItemConsumeEvent.class, Material.CARROT),
  /*
  COCOA("Confuses surrounding mobs", p -> {
    // Loses target from surrounding mobs
    MysticalUtils.potion(p, PotionEffectType.WEAKNESS);
    Collection<LivingEntity> nearby = p.getLocation().getNearbyLivingEntities(20);
    nearby.remove(p);
    nearby.forEach(l -> {
      if (l instanceof Monster m) {
        SpiderBoss.confuse(m, new Scheduler(), 15 * 20);
      }
    });
  }, PlayerInteractEvent.class, Material.COCOA_BEANS),
  FUNGUS(p -> {
    // Great nausea, great armor toughness
    MysticalUtils.potion(p, PotionEffectType.CONFUSION, 0, 20 * 45);
    MysticalUtils.potion(p, PotionEffectType.DAMAGE_RESISTANCE, 2, 20 * 45);
  }, PlayerInteractEvent.class, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS),
  MUSHROOM(p -> {
    // Great nausea, great area attack
    MysticalUtils.potion(p, PotionEffectType.CONFUSION, 0, 20 * 45);
    Data data = new Data(p);
    data.set(MysticalUtils.KEY, "MUSHROOM");
    Scheduler.Sync.run(() -> data.remove(MysticalUtils.KEY, "MUSHROOM"), 45 * 20);
  }, PlayerInteractEvent.class, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM),
  // Not to be confused with fungus, the sapling of the nether
  GLOW_BERRY(p -> {
    // Lights up all surrounding entities
    MysticalUtils.potion(p, PotionEffectType.GLOWING);
    Collection<LivingEntity> nearby = p.getLocation().getNearbyLivingEntities(50);
    nearby.remove(p);
    nearby.forEach(l -> {
      if (l instanceof Mob m) {
        MysticalUtils.potion(m, PotionEffectType.GLOWING);
      }
    });
  }, PlayerItemConsumeEvent.class, Material.GLOW_BERRIES),
  KELP(p -> {
    // DOLPHIN
    MysticalUtils.potion(p, PotionEffectType.DOLPHINS_GRACE);
  }, PlayerInteractEvent.class, Material.KELP),*/
  MELON("Great speed and haste", p -> {
    // Incredible speed (Haste and Swiftness(?))
    MysticalUtils.potion(p, PotionEffectType.SPEED, 2);
    MysticalUtils.potion(p, PotionEffectType.FAST_DIGGING, 2);
  }, PlayerItemConsumeEvent.class, Material.MELON_SLICE),
  /*
  NETHER_WART("Pushes all mobs away", p -> {
    // push all mobs away
    MysticalUtils.potion(p, PotionEffectType.SLOW);
    new Scheduler().cancelFuture("LOSE_TARGET", () -> {
      Collection<LivingEntity> nearby = p.getLocation().getNearbyLivingEntities(5);
      nearby.remove(p);
      nearby.forEach(l -> {
        if (l instanceof Mob m) {
          m.setVelocity(m.getLocation().toVector().subtract(p.getLocation().toVector()));
        }
      });
    }, 1, 1, 45 * 20);
  }, PlayerInteractEvent.class, Material.NETHER_WART),*/
  POTATO("Grants short flight", p -> {
    // Flying for a short time
    MysticalUtils.potion(p, PotionEffectType.SPEED, 0, 10);

    p.setAllowFlight(true);
    p.setFlying(true);
    new Scheduler().delay("FLIGHT", () -> {
      p.setAllowFlight(false);
      p.setFlying(false);
    }, 10 * 20);
  }, PlayerItemConsumeEvent.class, Material.POTATO, Material.POISONOUS_POTATO);
  /*
  PUMPKIN(p -> {
    // Confuses surrounding mobs
    MysticalUtils.potion(p, PotionEffectType.WEAKNESS);
    new Scheduler().cancelFuture("LOSE_TARGET", () -> {
      Collection<LivingEntity> nearby = p.getLocation().getNearbyLivingEntities(5);
      nearby.remove(p);
      nearby.forEach(l -> {
        if (l instanceof Mob m) {
          if (m.getTarget() != null && m.getTarget().getUniqueId().equals(p.getUniqueId())) {
            m.setTarget(null);
          }
        }
      });
    }, 1, 1, 45 * 20);
  }, PlayerInteractEvent.class, Material.PUMPKIN),*/
  /*
  SEA_PICKLE(p -> {
    // Great, long conduit power
    MysticalUtils.potion(p, PotionEffectType.CONDUIT_POWER, 3, 90);
  }, PlayerInteractEvent.class, Material.SEA_PICKLE),
  SUGAR_CANE(p -> {
    // JumpBoost
    MysticalUtils.potion(p, PotionEffectType.JUMP, 3, 90);
  }, PlayerInteractEvent.class, Material.SUGAR_CANE),
  SWEET_BERRY(p -> {
    // INSTANT SATURATION
  }, PlayerItemConsumeEvent.class, Material.SWEET_BERRIES),
  SAPLING(p -> {
    // Long lasting, slows hunger (maybe periodically give saturation)
    new Scheduler().cancelFuture("SLOW_SATURATION", () -> {
      MysticalUtils.potion(p, PotionEffectType.SATURATION, 0, 0.5f);
    }, 0, 20, 45 * 20);
  }, PlayerInteractEvent.class, Material.OAK_SAPLING, Material.BIRCH_SAPLING,
    Material.DARK_OAK_SAPLING, Material.JUNGLE_SAPLING,
    Material.SPRUCE_SAPLING, Material.ACACIA_SAPLING),
  WHEAT("All passive mobs and villagers will follow you", p -> {
    // All friendly mobs and humans will follow (maybe make this longer)
    MysticalUtils.potion(p, PotionEffectType.SLOW);
    new Scheduler().cancelFuture("LOSE_TARGET", () -> {
      Collection<LivingEntity> nearby = p.getLocation().getNearbyLivingEntities(10);
      nearby.remove(p);
      nearby.forEach(l -> {
        if (l instanceof Breedable m) {
          m.getPathfinder().moveTo(p);
        }
      });
    }, 1, 1, 90 * 20);
  }, PlayerInteractEvent.class, Material.WHEAT);*/

  private final String desc;
  private final Consumer<Player> run;
  private final Class<? extends Event> event;
  private final List<Material> viableDrops;

  Mystical(String desc, Consumer<Player> run, Class<? extends Event> event, Material... drops) {
    this.desc = desc;
    this.run = run;
    this.event = event;
    this.viableDrops = as(drops);
  }

  public static Mystical find(Material m) {
    for (Mystical mystical : values()) {
      for (Material m1 : mystical.viableDrops) {
        if (m1 == m) {
          return mystical;
        }
      }
    }
    return null;
  }

  private List<Material> as(Material... drops) {
    return new ArrayList<>(Arrays.asList(drops));
  }

  public boolean isSuccess(Event event) {
    return this.event.equals(event.getClass());
  }

  public void run(Player p) {
    run.accept(p);
  }

  public String desc() {
    return desc;
  }
}
