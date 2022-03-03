package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;

import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;
// CraftBukkit end

public class FollowSpawnerGoal extends Goal {

  public static final int TELEPORT_WHEN_DISTANCE_IS = 12;
  private static final int MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 2;
  private static final int MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 3;
  private static final int MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 1;
  private final Mob minion;
  private final UUID owner;
  private final LevelReader level;
  private final PathNavigation navigation;
  private final boolean canTeleport;
  private final float stopDistance;
  private final float startDistance;
  private final boolean canFly;
  private int timeToRecalcPath;
  private float oldWaterCost;

  public FollowSpawnerGoal(Mob minion, UUID owner, boolean canTeleport) {
    this.minion = minion;
    this.owner = owner;
    this.level = minion.getLevel();
    this.navigation = minion.getNavigation();
    this.canTeleport = canTeleport;
    this.startDistance = 10;
    this.stopDistance = 12;
    this.canFly = true;
    this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  private org.bukkit.entity.Entity entity() {
    return Bukkit.getEntity(owner);
  }

  private LivingEntity livingEntity() {
    return (LivingEntity) NMS.to(entity());
  }

  @Override
  public boolean canUse() {
    if (entity() == null) {
      return false;
    } else if (livingEntity().isSpectator()) {
      return false;
    } else return !(this.minion.distanceToSqr(livingEntity()) < (double) (this.startDistance * this.startDistance));
  }

  @Override
  public boolean canContinueToUse() {
    return !this.navigation.isDone() && this.minion.distanceToSqr(livingEntity()) > (double) (this.stopDistance * this.stopDistance);
  }

  @Override
  public void start() {
    this.timeToRecalcPath = 0;
    this.oldWaterCost = this.minion.getPathfindingMalus(BlockPathTypes.WATER);
    this.minion.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
  }

  @Override
  public void stop() {
    this.navigation.stop();
    this.minion.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
  }

  @Override
  public void tick() {
    this.minion.getLookControl().setLookAt(livingEntity(), 10.0F, (float) this.minion.getMaxHeadXRot());
    if (--this.timeToRecalcPath <= 0) {
      this.timeToRecalcPath = this.adjustedTickDelay(10);
      if (!this.minion.isLeashed() && !this.minion.isPassenger()) {
        if (this.minion.distanceToSqr(livingEntity()) >= 144.0D) {
          this.teleportToOwner();
        } else {
          this.navigation.moveTo(livingEntity(), 1);
        }

      }
    }
  }

  private void teleportToOwner() {
    if (this.canTeleport) {
      BlockPos blockposition = livingEntity().blockPosition();

      for (int i = 0; i < 10; ++i) {
        int j = this.randomIntInclusive(-3, 3);
        int k = this.randomIntInclusive(-1, 1);
        int l = this.randomIntInclusive(-3, 3);
        boolean flag = this.maybeTeleportTo(blockposition.getX() + j, blockposition.getY() + k, blockposition.getZ() + l);

        if (flag) {
          return;
        }
      }
    }

  }

  private boolean maybeTeleportTo(int x, int y, int z) {
    if (Math.abs((double) x - livingEntity().getX()) < 2.0D && Math.abs((double) z - livingEntity().getZ()) < 2.0D) {
      return false;
    } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
      return false;
    } else {
      // CraftBukkit start
      CraftEntity entity = this.minion.getBukkitEntity();
      Location to = new Location(entity.getWorld(), (double) x + 0.5D, y, (double) z + 0.5D, this.minion.getYRot(), this.minion.getXRot());
      EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
      this.minion.level.getCraftServer().getPluginManager().callEvent(event);
      if (event.isCancelled()) {
        return false;
      }
      to = event.getTo();

      this.minion.moveTo(Objects.requireNonNull(to).getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
      // CraftBukkit end
      this.navigation.stop();
      return true;
    }
  }

  private boolean canTeleportTo(BlockPos pos) {
    BlockPathTypes pathtype = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());

    if (pathtype != BlockPathTypes.WALKABLE) {
      return false;
    } else {
      BlockState iblockdata = this.level.getBlockState(pos.below());

      if (!this.canFly && iblockdata.getBlock() instanceof LeavesBlock) {
        return false;
      } else {
        BlockPos blockposition1 = pos.subtract(this.minion.blockPosition());

        return this.level.noCollision(this.minion, this.minion.getBoundingBox().move(blockposition1));
      }
    }
  }

  private int randomIntInclusive(int min, int max) {
    return this.minion.getRandom().nextInt(max - min + 1) + min;
  }
}
