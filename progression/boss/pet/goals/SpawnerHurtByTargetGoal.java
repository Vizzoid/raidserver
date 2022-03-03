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

public class SpawnerHurtByTargetGoal extends TargetGoal {

  private final UUID spawner;
  private LivingEntity ownerLastHurtBy;
  private int timestamp;

  public SpawnerHurtByTargetGoal(Mob minion, UUID spawner) {
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
      this.ownerLastHurtBy = spawner.getLastHurtByMob();
      int i = spawner.getLastHurtByMobTimestamp();

      return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
    }
  }

  @Override
  public void start() {
    this.mob.setTarget(this.ownerLastHurtBy, org.bukkit.event.entity.EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, false); // CraftBukkit - reason

    if (entity() != null) {
      this.timestamp = livingEntity().getLastHurtByMobTimestamp();
    }

    super.start();
  }
}
