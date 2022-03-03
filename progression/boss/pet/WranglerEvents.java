package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;

public class WranglerEvents extends MinecraftListener {

  @EventHandler
  public void onPlayerBeatBoss(PlayerAdvancementDoneEvent e) {
    //if (e.getAdvancement().getRoot().getKey().equals(Advancement.ROOT_BOSS_KEY)) {
    WranglerUtils.addLevel(e.getPlayer());
    updateWrangler(e.getPlayer());
    //}
  }

  /**
   * Updates all instances of Wrangler in player's inventory
   *
   * @param p player being updated
   */
  private void updateWrangler(Player p) {
    if (p.getInventory().contains(Material.LEAD)) {
      p.getInventory().all(Material.LEAD).forEach((k, v) -> {
        if (WranglerUtils.isWrangler(v)) {
          updateWrangler(p, k);
        }
      });
    }
  }

  /**
   * Updates this instance of Wrangler in player's inventory
   *
   * @param p     player being updated
   * @param index of wrangler
   */
  private void updateWrangler(Player p, int index) {
    p.getInventory().setItem(index, WranglerUtils.wrangler(WranglerUtils.level(p)));
  }

  @EventHandler
  public void onPlayerPickupWrangler(EntityPickupItemEvent e) {
    if (e.getEntity() instanceof Player p) {
      if (WranglerUtils.isWrangler(e.getItem().getItemStack())) {
        e.getItem().setItemStack(updateWrangler(p, e.getItem().getItemStack()));
      }
    }
  }

  @EventHandler
  public void onPlayerMoveWrangler(InventoryClickEvent e) {
    if (e.getWhoClicked() instanceof Player p) {
      if (WranglerUtils.isWrangler(e.getCurrentItem())) {
        Inventory inv = e.getClickedInventory();
        if (inv != null && inv.getHolder() != null) {
          if (inv.getHolder().equals(e.getWhoClicked())) {
            e.setCurrentItem(updateWrangler(p, e.getCurrentItem()));
          }
        }
      }
    }
  }

  /**
   * @param p    actor
   * @param item old wrangler stack
   * @return updated wranglers
   */
  public ItemStack updateWrangler(Player p, ItemStack item) {
    ItemStack newItem = WranglerUtils.wrangler(WranglerUtils.level(p));
    newItem.setAmount(item.getAmount());
    return newItem;
  }

  @EventHandler
  public void playerGrindWrangler(PrepareResultEvent e) {
    if (e.getInventory() instanceof GrindstoneInventory inv) {

      boolean upper = inv.getUpperItem() != null && new Data(inv.getUpperItem().getItemMeta()).has(WranglerUtils.KEY);
      boolean lower = inv.getLowerItem() != null && new Data(inv.getLowerItem().getItemMeta()).has(WranglerUtils.KEY);
      if (upper) {
        ItemStack item = new ItemStack(Material.DIAMOND);
        if (lower) {
          item.setAmount(6);
        } else {
          item.setAmount(3);
        }
        inv.setResult(item);
      } else if (lower) {
        ItemStack item = new ItemStack(Material.DIAMOND);
        item.setAmount(3);
        inv.setResult(item);
      }
    }
  }

}
