package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.skeletalSpirit;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.WitherSkeleton;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet.MinionUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;

public class MinionWitherSkeleton extends WitherSkeleton {

  public MinionWitherSkeleton(Location l, org.bukkit.entity.LivingEntity owner) {
    super(EntityType.WITHER_SKELETON, NMS.to(l.getWorld()));
    this.setPos(l.getX(), l.getY(), l.getZ());
    NMS.to(l.getWorld()).addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    MinionUtils.convert(this, l, owner);
  }

}
