package org.vizzoid.raidserver.raidserver.minecraft.mechanics.quests;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.blindWidow.SpiderBoss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.magmaOpus.MagmaBoss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.skeletalSpirit.SkeletonBoss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.essence.Carrier;
import org.vizzoid.raidserver.raidserver.minecraft.progression.essence.EssenceUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;

import java.util.Random;

public class QuestEvents extends MinecraftListener {

  @EventHandler
  public void onTravelingTraderSpawn(CreatureSpawnEvent e) {
    if (e.getEntityType() == EntityType.WANDERING_TRADER) {
      WanderingTrader trader = (WanderingTrader) e.getEntity();
      {
        ItemStack recipe = new ItemStack(Material.EMERALD);
        recipe.setAmount(5);

        MerchantRecipe trade;
        switch (new Random().nextInt(3)) {
          case 0 -> trade = new MerchantRecipe(EssenceUtils.essence(Carrier.WITHER_SKELETON), 12);
          case 1 -> trade = new MerchantRecipe(EssenceUtils.essence(Carrier.MAGMA_CUBE), 12);
          default -> trade = new MerchantRecipe(EssenceUtils.essence(Carrier.GHAST), 12);
        }
        trade.addIngredient(recipe);
        trader.setRecipe(0, trade);
      }

      {
        ItemStack result = new ItemStack(Material.NETHERITE_INGOT);
        MerchantRecipe trade = new MerchantRecipe(result, 1);

        ItemStack recipe;
        switch (new Random().nextInt(3)) {
          case 0 -> recipe = new SkeletonBoss().mainDrop();
          case 1 -> recipe = new MagmaBoss().mainDrop();
          default -> recipe = new SpiderBoss().mainDrop();
        }
        recipe.setAmount(1);
        trade.addIngredient(recipe);
        trader.setRecipe(1, trade);
      }

    }
  }

}
