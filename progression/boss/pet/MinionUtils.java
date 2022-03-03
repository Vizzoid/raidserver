package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.skeletalSpirit.SkeletonBossEntity;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet.goals.FollowSpawnerGoal;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet.goals.SpawnerHurtByTargetGoal;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet.goals.SpawnerHurtTargetGoal;

import java.util.*;

public class MinionUtils {

  // UUID of player wrangler, mob being wrangled
  private static final Map<UUID, Mob> wrangledMobs = new HashMap<>();

  public static Mob mob(org.bukkit.entity.Player p) {
    Mob mob = wrangledMobs.get(p.getUniqueId());
    if (mob.isDead()) {
      return null;
    }
    return mob;
  }

  public static void mob(org.bukkit.entity.Player p, Mob mob) {
    wrangledMobs.put(p.getUniqueId(), mob);
  }

  public static void remove(org.bukkit.entity.Player p) {
    wrangledMobs.remove(p.getUniqueId());
  }

  public static boolean has(org.bukkit.entity.Player p) {
    return wrangledMobs.containsKey(p.getUniqueId()) && mob(p) != null;
  }

  public static void convert(PathfinderMob minion, Location l, org.bukkit.entity.LivingEntity owner) {
    UUID spawner = owner.getUniqueId();
    minion.persist = true;
    boolean isPlayerSummoned = owner instanceof org.bukkit.entity.Player;

    minion.goalSelector.addGoal(6, new FollowSpawnerGoal(minion, spawner, isPlayerSummoned));

    if (minion instanceof Enemy || minion instanceof NeutralMob) {

      GoalSelector selector = minion.targetSelector;
      selector.removeAllGoals();
      selector.addGoal(1, new SpawnerHurtByTargetGoal(minion, spawner));
      selector.addGoal(2, new SpawnerHurtTargetGoal(minion, spawner));
      if (isPlayerSummoned) {
        selector.addGoal(3, new HurtByTargetGoal(minion, Player.class));
      }

      hostiles(minion, isPlayerSummoned).forEach(h -> selector.addGoal(4, new NearestAttackableTargetGoal<>(minion, h, false)));
    }
  }

  public static void revert(PathfinderMob minion) {
    minion.goalSelector.removeAllGoals();

    if (minion instanceof Monster) {
      GoalSelector selector = minion.targetSelector;
      selector.removeAllGoals();
    }
  }

  private static Set<Class<? extends LivingEntity>> hostiles(PathfinderMob mob, boolean isPlayerSummoned) {
    Set<Class<? extends LivingEntity>> hostiles = new HashSet<>();
    if (isPlayerSummoned) {
      hostiles.add(WitherSkeleton.class);
      hostiles.add(Skeleton.class);
      hostiles.add(SkeletonBossEntity.class);
      hostiles.add(Blaze.class);
      hostiles.add(Creeper.class);
      hostiles.add(Drowned.class);
      hostiles.add(ElderGuardian.class);
      hostiles.add(Endermite.class);
      hostiles.add(EnderDragon.class);
      hostiles.add(WitherBoss.class);
      hostiles.add(Evoker.class);
      hostiles.add(Ghast.class);
      hostiles.add(Guardian.class);
      hostiles.add(Hoglin.class);
      hostiles.add(Husk.class);
      hostiles.add(Illusioner.class);
      hostiles.add(MagmaCube.class);
      hostiles.add(Phantom.class);
      hostiles.add(PiglinBrute.class);
      hostiles.add(Pillager.class);
      hostiles.add(Ravager.class);
      hostiles.add(Shulker.class);
      hostiles.add(Silverfish.class);
      hostiles.add(Slime.class);
      hostiles.add(Spider.class);
      hostiles.add(Stray.class);
      hostiles.add(Vex.class);
      hostiles.add(Vindicator.class);
      hostiles.add(Witch.class);
      hostiles.add(Zoglin.class);
      hostiles.add(Zombie.class);
      hostiles.add(ZombieVillager.class);
      hostiles.add(CaveSpider.class);
      hostiles.add(Piglin.class);
    } else {
      hostiles.add(Player.class);
    }
    hostiles.removeIf(c -> c.isAssignableFrom(mob.getClass()));
    return hostiles;
  }

  public static Set<EntityType> level(int level) {
    switch (level) {
      case 0 -> {
        return level0();
      }
      case 1 -> {
        return level1();
      }
      case 2 -> {
        return level2();
      }
      case 3 -> {
        return level3();
      }
      case 4 -> {
        return level4();
      }
      default -> {
        return level5();
      }
    }
  }

  private static Set<EntityType> level0() {
    return new HashSet<>();
  }

  private static Set<EntityType> level1() {
    Set<EntityType> types = new HashSet<>();
    types.add(EntityType.BAT);
    types.add(EntityType.CHICKEN);
    types.add(EntityType.COD);
    types.add(EntityType.COW);
    types.add(EntityType.GLOW_SQUID);
    types.add(EntityType.MUSHROOM_COW);
    types.add(EntityType.PIG);
    types.add(EntityType.PUFFERFISH);
    types.add(EntityType.RABBIT);
    types.add(EntityType.SALMON);
    types.add(EntityType.SHEEP);
    types.add(EntityType.SQUID);
    types.add(EntityType.TROPICAL_FISH);
    types.add(EntityType.TURTLE);
    return types;
  }

  private static Set<EntityType> level2() {
    Set<EntityType> types = new HashSet<>(level1());
    types.add(EntityType.SNOWMAN);
    types.add(EntityType.OCELOT);
    types.add(EntityType.FOX);
    types.add(EntityType.BEE);
    types.add(EntityType.DOLPHIN);
    types.add(EntityType.GOAT);
    types.add(EntityType.LLAMA);
    types.add(EntityType.TRADER_LLAMA);
    types.add(EntityType.PANDA);
    types.add(EntityType.POLAR_BEAR);
    return types;
  }

  private static Set<EntityType> level3() {
    Set<EntityType> types = new HashSet<>(level2());
    types.add(EntityType.PIGLIN);
    types.add(EntityType.ZOMBIFIED_PIGLIN);
    types.add(EntityType.SPIDER);
    types.add(EntityType.CAVE_SPIDER);
    types.add(EntityType.ENDERMAN);
    types.add(EntityType.BLAZE);
    types.add(EntityType.ZOMBIE);
    types.add(EntityType.DROWNED);
    types.add(EntityType.HUSK);
    types.add(EntityType.GUARDIAN);
    types.add(EntityType.ENDERMITE);
    types.add(EntityType.HOGLIN);
    types.add(EntityType.MAGMA_CUBE);
    types.add(EntityType.PHANTOM);
    types.add(EntityType.SILVERFISH);
    types.add(EntityType.SKELETON);
    types.add(EntityType.SLIME);
    types.add(EntityType.STRAY);
    types.add(EntityType.ZOGLIN);
    types.add(EntityType.ZOMBIE_VILLAGER);
    return types;
  }

  private static Set<EntityType> level4() {
    Set<EntityType> types = new HashSet<>(level3());
    types.add(EntityType.ILLUSIONER);
    types.add(EntityType.PILLAGER);
    types.add(EntityType.VINDICATOR);
    types.add(EntityType.VEX);
    types.add(EntityType.RAVAGER);
    types.add(EntityType.WITCH);
    types.add(EntityType.CREEPER);
    types.add(EntityType.GHAST);
    types.add(EntityType.ELDER_GUARDIAN);
    types.add(EntityType.PIGLIN_BRUTE);
    types.add(EntityType.SHULKER);
    types.add(EntityType.WITHER_SKELETON);

    types.add(EntityType.AXOLOTL);
    types.add(EntityType.PARROT);
    types.add(EntityType.CAT);
    types.add(EntityType.HORSE);
    types.add(EntityType.MULE);
    types.add(EntityType.DONKEY);
    types.add(EntityType.SKELETON_HORSE);
    types.add(EntityType.ZOMBIE_HORSE);
    types.add(EntityType.VILLAGER);
    types.add(EntityType.WANDERING_TRADER);
    types.add(EntityType.STRIDER);
    types.add(EntityType.WOLF);
    types.add(EntityType.IRON_GOLEM);
    return types;
  }

  private static Set<EntityType> level5() {
    Set<EntityType> types = new HashSet<>(level4());
    types.add(EntityType.WITHER);
    types.add(EntityType.ENDER_DRAGON);
    return types;
  }

}
