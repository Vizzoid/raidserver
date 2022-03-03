package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.skeletalSpirit;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;

public class SkeletonBossEntity extends Skeleton {

  public SkeletonBossEntity(Location loc) {
    super(EntityType.SKELETON, NMS.to(loc.getWorld()));

    this.setPos(loc.getX(), loc.getY(), loc.getZ());
    NMS.to(loc.getWorld()).addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

    this.setCustomName(new TextComponent("Skeletal Spirit"));
    this.setCustomNameVisible(true);
  }

}
