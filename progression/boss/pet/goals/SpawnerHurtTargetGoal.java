package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.Bukkit;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;

import java.util.EnumSet;
import java.util.UUID;

public class SpawnerHurtTargetGoal extends TargetGoal {

  private final UUID spawner;
  private LivingEntity ownerLastHurt;
  private int timestamp;

  public SpawnerHurtTargetGoal(Mob minion, UUID spawner) {
    super(minion, false);
    this.spawner = spawner;
    this.setFlags(EnumSet.of(Goal.Flag.TARGET));
  }

  private org.bukkit.entity.Entity entity() {
    return Bukkit.getEntity(spawner);
  }

  private LivingEntity livingEntity() {
    return (LivingEntity) NMS.to(entity());
  }

  @Override
  public boolean canUse() {

    if (entity() == null) {
      return false;
    } else {

      LivingEntity spawner = livingEntity();
      this.ownerLastHurt = spawner.getLastHurtMob();
      int i = spawner.getLastHurtMobTimestamp();

      return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT);
    }
  }

  @Override
  public void start() {
    this.mob.setTarget(this.ownerLastHurt, org.bukkit.event.entity.EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true); // CraftBukkit - reason

    if (livingEntity() != null) {
      this.timestamp = livingEntity().getLastHurtMobTimestamp();
    }

    super.start();
  }
}
