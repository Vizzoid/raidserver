package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.skeletalSpirit;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet.MinionUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;

public class MinionSkeleton extends Skeleton {

  public MinionSkeleton(Location l, org.bukkit.entity.LivingEntity owner) {
    super(EntityType.SKELETON, NMS.to(l.getWorld()));
    this.setPos(l.getX(), l.getY(), l.getZ());
    NMS.to(l.getWorld()).addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    MinionUtils.convert(this, l, owner);
  }

}
