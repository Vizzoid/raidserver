package org.vizzoid.raidserver.raidserver.minecraft.events;

import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.blacksmith.Blacksmith;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.miner.Miner;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorPiece;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import java.util.Random;

public class CustomEntityEvents extends MinecraftListener {

  public static final Key MINER_KEY = new Key("IS_MINER");
  private static final Key BLACKSMITH_KEY = new Key("BLACKSMITH_AMOUNT");

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent e) {
    if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
      if (e.getEntityType() == EntityType.SKELETON || e.getEntityType() == EntityType.ZOMBIE) {
        if (e.getLocation().getY() < 0) {
          if (e.getEntity() instanceof Monster m && new Random().nextInt(5) == 0) {
            m.getEquipment().setHelmet(item(Material.LEATHER_HELMET));
            m.getEquipment().setChestplate(item(Material.LEATHER_CHESTPLATE));
            m.getEquipment().setLeggings(item(Material.LEATHER_LEGGINGS));
            m.getEquipment().setBoots(item(Material.LEATHER_BOOTS));

            m.getEquipment().setItemInMainHand(item(Material.IRON_PICKAXE));
            new Data(m).set(MINER_KEY, true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onMinerKill(EntityDeathEvent e) {
    if (new Data(e.getEntity()).has(MINER_KEY)) {
      Player p = e.getEntity().getKiller();
      if (p != null) {
        e.getDrops().add(drop());
      }
    }
  }

  private ItemStack item(Material m) {
    ItemStack item = new ItemStack(m);
    ItemMeta meta = item.getItemMeta();
    if (meta instanceof LeatherArmorMeta) {
      ((LeatherArmorMeta) meta).setColor(Color.GRAY);
    }
    meta.setUnbreakable(true);

    item.setItemMeta(meta);
    return item;
  }

  private ItemStack drop() {
    ArmorPiece piece = ArmorPiece.values()[new Random().nextInt(ArmorPiece.values().length)];
    return ArmorUtils.item(piece, new Miner());
  }

  private int get(Player p) {
    return has(p) ? new Data(p).getInt(BLACKSMITH_KEY) : 0;
  }

  private boolean has(Player p) {
    return new Data(p).has(BLACKSMITH_KEY);
  }

  private void add(Player p) {
    new Data(p).set(BLACKSMITH_KEY, get(p) + 1);
  }

  @EventHandler
  public void onPlayerTrade(PlayerTradeEvent e) {
    if (e.getVillager() instanceof Villager villager && blacksmith(villager.getProfession())) {
      Player p = e.getPlayer();
      add(p);

      if (get(e.getPlayer()) >= 250) {
        award(p, "misc", "capitalism");

        Blacksmith armor = new Blacksmith();

        p.getInventory().addItem(
          ArmorUtils.item(ArmorPiece.BOOTS, armor),
          ArmorUtils.item(ArmorPiece.LEGGINGS, armor),
          ArmorUtils.item(ArmorPiece.CHESTPLATE, armor),
          ArmorUtils.item(ArmorPiece.HELMET, armor)).forEach((k, v) -> {
          p.getWorld().dropItemNaturally(p.getLocation(), v);
        });
      }
    }
  }

  private boolean blacksmith(Villager.Profession job) {
    return job == Villager.Profession.ARMORER || job == Villager.Profession.TOOLSMITH || job == Villager.Profession.WEAPONSMITH;
  }

}
