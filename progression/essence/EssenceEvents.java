package org.vizzoid.raidserver.raidserver.minecraft.progression.essence;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.vizzoid.raidserver.raidserver.minecraft.utils.RecipeUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;

import java.util.Random;

public class EssenceEvents extends MinecraftListener {
  @EventHandler
  public void onCarrierKilled(EntityDeathEvent e) {
    if (new Data(e.getEntity()).has(EssenceUtils.ESSENCE_KEY)) {
      Carrier c = Carrier.get(e.getEntity());
      ItemStack essence = EssenceUtils.essence(c);
      Random r = new Random();
      essence.setAmount((int) Math.floor(r.nextInt(3) + 1 * c.dropRates()));
      e.getDrops().add(essence);
      e.setDroppedExp(Math.toIntExact(Math.round(e.getDroppedExp() * 1.1)));

      Player p = e.getEntity().getKiller();
      if (p != null) {
        RecipeUtils.grant(c.set(), p);
      }
    }
  }

  @EventHandler
  public void onPotentialCarrierSpawned(CreatureSpawnEvent e) {
    if (e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
      Carrier c = Carrier.get(e.getEntity());
      if (c != null) {
        Random r = new Random();

        // 10% chance of becoming carrier
        if (r.nextInt(c.carrierRates()) == 0) {
          EssenceUtils.essenceCarrier(e.getEntity());
        }
      }
    }
  }
}
