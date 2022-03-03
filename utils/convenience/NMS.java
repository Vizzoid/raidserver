package org.vizzoid.raidserver.raidserver.minecraft.utils.convenience;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.*;
import org.bukkit.craftbukkit.v1_18_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class NMS {

  public static ServerLevel to(World world) {
    return ((CraftWorld) world).getHandle();
  }

  public static DedicatedServer to(Server server) {
    return ((CraftServer) server).getServer();
  }

  public static ServerPlayer to(Player player) {
    return ((CraftPlayer) player).getHandle();
  }

  public static GameProfile to(PlayerProfile player) {
    return ((CraftPlayerProfile) player).getGameProfile();
  }

  public static GameProfile to(org.bukkit.profile.PlayerProfile player) {
    return ((CraftPlayerProfile) player).getGameProfile();
  }

  public static net.minecraft.world.scores.Scoreboard to(Scoreboard scoreboard) {
    return ((CraftScoreboard) scoreboard).getHandle();
  }

  public static LivingEntity to(org.bukkit.entity.LivingEntity entity) {
    return ((CraftLivingEntity) entity).getHandle();
  }

  public static Entity to(org.bukkit.entity.Entity entity) {
    return ((CraftEntity) entity).getHandle();
  }

  public static Skeleton to(org.bukkit.entity.Skeleton entity) {
    return ((CraftSkeleton) entity).getHandle();
  }

  public static Zombie to(org.bukkit.entity.Zombie entity) {
    return ((CraftZombie) entity).getHandle();
  }

  public static Pillager to(org.bukkit.entity.Pillager entity) {
    return ((CraftPillager) entity).getHandle();
  }

  /**
   * Gets NMS mob but upscales it to PathfinderMob (assumption of inheritance)
   *
   * @param entity to be handled
   * @return subclass container
   */
  public static PathfinderMob up(org.bukkit.entity.Mob entity) {
    return (PathfinderMob) to(entity);
  }

  public static PathfinderMob to(org.bukkit.entity.Creature entity) {
    return ((CraftCreature) entity).getHandle();
  }

  public static Mob to(org.bukkit.entity.Mob entity) {
    return ((CraftMob) entity).getHandle();
  }


}
