package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.Particles;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;

public class MinionEvents extends MinecraftListener {

  @EventHandler
  public void onPlayerLeadInteract(EntityDamageByEntityEvent e) {
    Entity target = e.getEntity();
    if (target instanceof Mob mob && e.getDamager() instanceof Player p) {
      if (isHolding(p)) {
        e.setCancelled(true);
        if (MinionUtils.level(WranglerUtils.level(p)).contains(target.getType())) {
          if (MinionUtils.has(p)) {
            if (target != MinionUtils.mob(p)) return;

            MinionUtils.revert(NMS.up(MinionUtils.mob(p)));
          }
          MinionUtils.convert(NMS.up(mob), target.getLocation(), p);
          MinionUtils.mob(p, mob);
          Particles.spawn(target.getLocation().add(0, 1, 0), Particle.HEART, null, 10);
          Sound.success(p);
        } else {
          p.sendMessage(Component.text("You aren't a high enough level to wrangle that!")
            .color(Color.RED).decoration(TextDecoration.BOLD, true));
          Sound.play(Sound.Effect.ENDERMAN_TELEPORT, p);
        }
      }
    }
  }

  @EventHandler
  public void onPlayerAttemptLeash(PlayerLeashEntityEvent e) {
    if (isHolding(e.getPlayer())) {
      e.setCancelled(true);
    }
  }

  private boolean isHolding(Player p) {
    return WranglerUtils.isWrangler(p.getInventory().getItemInMainHand()) || WranglerUtils.isWrangler(p.getInventory().getItemInOffHand());
  }

}
