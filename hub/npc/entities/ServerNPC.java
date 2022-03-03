package org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;

public class ServerNPC extends Pillager {
  public ServerNPC(Location l) {
    super(EntityType.PILLAGER, NMS.to(l.getWorld()));
    setPos(l.getX(), l.getY(), l.getZ());

    mob().setSilent(true);
    mob().setInvulnerable(true);
    mob().setInvisible(true);
    mob().setRemoveWhenFarAway(false);
    mob().setPersistent(true);

    mob().getEquipment().clear();

    goalSelector.removeAllGoals();
    targetSelector.removeAllGoals();
    this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
    this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

    NMS.to(l.getWorld()).addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
  }

  private Creature mob() {
    return getBukkitCreature();
  }
}
