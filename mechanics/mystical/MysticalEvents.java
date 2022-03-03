package org.vizzoid.raidserver.raidserver.minecraft.mechanics.mystical;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Location;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Container;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;

import javax.annotation.CheckReturnValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class MysticalEvents extends MinecraftListener {

  @EventHandler
  public void onCropBreak(BlockBreakEvent e) {
    if (handle(e.getPlayer(), e.getBlock().getLocation(), e.getBlock().getDrops(
      e.getPlayer().getInventory().getItemInMainHand()))) {
      e.setDropItems(false);
    }
  }

  /**
   * @deprecated Blocks displayed through here act strangely
   * ToDo find solution
   */
  @Deprecated
  public void onCropHarvest(PlayerHarvestBlockEvent e) {
    // lmao
    if (e.getHarvestedBlock().getState() instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {
      if (handle(e.getPlayer(), e.getHarvestedBlock().getLocation(), e.getHarvestedBlock().getDrops(
        e.getPlayer().getInventory().getItemInMainHand()))) {
        e.getItemsHarvested().clear();
      }
    }
  }

  @EventHandler
  public void onBlockBreakCrop(BlockBreakBlockEvent e) {
    if (e.getBlock().getState() instanceof Ageable crop) {
      if (crop.getAge() == crop.getMaximumAge()) {
        if (handle(null, e.getBlock().getLocation(), e.getDrops())) {
          e.getDrops().clear();
        }
      }
    } else {
      if (handle(null, e.getBlock().getLocation(), e.getDrops())) {
        e.getDrops().clear();
      }
    }
  }

  @CheckReturnValue
  private boolean handle(Player p, Location l, Collection<ItemStack> drops) {
    if (l.getBlock().getState() instanceof Container) return false;

    List<ItemStack> cloned = new ArrayList<>(drops);

    for (ItemStack d : new ArrayList<>(drops)) {
      if (new Random().nextInt(10) == 0) {
        Mystical m = Mystical.find(d.getType());
        if (m != null) {
          if (d.getAmount() == 1) {
            cloned.remove(d);
          } else {
            ItemStack item = cloned.get(cloned.indexOf(d));
            item.setAmount(item.getAmount() - 1);
          }
          cloned.add(MysticalUtils.item(m, d.getType(), p));

          for (ItemStack drop : cloned) {
            l.getWorld().dropItemNaturally(l, drop);
          }
          return true;
        }
      }
    }
    return false;
  }

  @EventHandler
  public void onFarmableDeath(EntityDeathEvent e) {
    if (e.getEntity() instanceof Animals) {
      if (handle(e.getEntity().getKiller(), e.getEntity().getLocation(), e.getDrops())) {
        e.getDrops().clear();
      }
    }
  }

  @EventHandler
  public void onPlayerEatMystical(PlayerItemConsumeEvent e) {
    handleEffect(e, e.getItem());
  }

  // ToDo item isn't being used when on 1 amount
  @EventHandler
  public void onPlayerUseMystical(PlayerInteractEvent e) {
    if (e.getItem() != null) {
      if (handleEffect(e, e.getItem())) {
        if (e.getItem().getAmount() > 1) {
          e.getItem().setAmount(e.getItem().getAmount() - 1);
        }
      }
    }
  }

  private boolean handleEffect(PlayerEvent e, ItemStack item) {
    if (MysticalUtils.is(item)) {
      Mystical m = Mystical.valueOf(new Data(item.getItemMeta()).get(MysticalUtils.KEY));
      if (m.isSuccess(e)) {
        m.run(e.getPlayer());
        AdvancementProgress progress = MysticalUtils.root(e.getPlayer(), m);
        progress.getRemainingCriteria().forEach(progress::awardCriteria);
        return true;
      }
    }
    return false;
  }

  @EventHandler
  public void onPlayerHitWhileCactus(EntityDamageByEntityEvent e) {/*
    if (e.getDamager() instanceof TNTPrimed t) {
      if (new Data(t).has(MysticalUtils.KEY)) {
        if (t.getSource() != null && t.getSource().equals(e.getEntity())) {
          e.setCancelled(true);
        }
      }
    }*/

    if (e.getEntity() instanceof Player p) {
      if (e.getDamager() instanceof LivingEntity l) {
        String output = new Data(p).getCheck(MysticalUtils.KEY);
        if (output != null) {
          if ("CACTUS".equals(output)) {
            l.damage(3 * e.getDamage());
          }
        }
      }
    } else if (e.getDamager() instanceof Player p && e.getEntity() instanceof LivingEntity l && p.getAttackCooldown() == 1) {
      String output = new Data(p).getCheck(MysticalUtils.KEY);
      if (output != null) {
        if ("MUSHROOM".equals(output)) {
          Collection<LivingEntity> nearby = l.getLocation().getNearbyLivingEntities(2);
          nearby.remove(p);
          nearby.remove(l);

          nearby.forEach(n -> n.damage(e.getDamage()));
        }
      }
    }
  }

}
