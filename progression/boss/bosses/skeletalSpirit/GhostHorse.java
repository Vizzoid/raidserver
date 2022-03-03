package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.skeletalSpirit;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;

public class GhostHorse extends SkeletonHorse {
  public GhostHorse(Location loc) {
    super(EntityType.SKELETON_HORSE, NMS.to(loc.getWorld()));
    this.setPos(loc.getX(), loc.getY(), loc.getZ());

    this.goalSelector.removeAllGoals();
    this.targetSelector.removeAllGoals();
    NMS.to(loc.getWorld()).addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
  }
}
