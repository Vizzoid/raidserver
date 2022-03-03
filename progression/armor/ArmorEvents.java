package org.vizzoid.raidserver.raidserver.minecraft.progression.armor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import org.vizzoid.raidserver.raidserver.minecraft.mechanics.pvp.PVP;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.miner.Miner;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.paladin.Paladin;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.rider.Rider;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.swordsman.Swordmaster;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorPiece;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Plan is to have base game:
 * <p>
 * ------------|-Spd-|-Dmg-|-Def-|
 * Swordsman - |  ↑  |  ↓  |  ~  | from Wither Skeleton
 * Beserker  - |  ~  |  ↑  |  ↓  | from Ghast
 * Paladin   - |  ↓  |  ~  |  ↑  | from Magma Cube
 * ------------|-----|-----|-----|
 * <p>
 * Ideas:
 * <p>
 * Enderman
 * Chef from butcher
 * Villager upgrade by trading(?)
 * Witch
 * Archer
 */
public class ArmorEvents extends MinecraftListener {

  public static final Map<UUID, Armor> armorSets = new HashMap<>();

  private static void handle(Event e, Player p) {
    if (p.getGameMode() != GameMode.SPECTATOR) {
      UUID uuid = p.getUniqueId();
      if (armorSets.containsKey(uuid)) {
        Armor armor = armorSets.get(uuid);
        armor.passive1(e)
          .passive2(e)
          .passive3(e)
          .passive4(e)
          .active1(e)
          .active2(e);

        if (e instanceof EntityDamageByEntityEvent event && event.getDamager() instanceof Player)
          armorSets.get(uuid).weaponIsIdeal(event);
      }
    }
  }

  /**
   * @param player changing armor
   * @return key of class, null is none matches
   */
  @Nullable
  public static String check(Player player) {
    PlayerInventory inv = player.getInventory();
    ItemStack[] armor = inv.getArmorContents();
    if (!Arrays.asList(armor).contains(null)) {
      if (Arrays.stream(armor).allMatch(i -> new Data(i.getItemMeta()).has(ArmorUtils.KEY))) {
        String s = new Data(armor[0].getItemMeta()).get(ArmorUtils.KEY);
        if (Arrays.stream(armor).allMatch(i -> new Data(i.getItemMeta()).get(ArmorUtils.KEY).equals(s))) {
          return s;
        }
      }
    }
    return null;
  }

  @EventHandler
  public void onArmorWear(PlayerArmorChangeEvent e) {

    Player p = e.getPlayer();
    UUID uuid = p.getUniqueId();
    String s = check(p);
    if (s != null) {
      ArmorSet set = ArmorSet.valueOf(s);
      Armor armor = set.init(p);
      armorSets.put(uuid, armor);
      award(p, "misc", armor.name().toLowerCase(Locale.ROOT));
    } else if (armorSets.containsKey(uuid)) {
      armorSets.get(uuid).clear();
      armorSets.remove(uuid);
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    handle(e, e.getPlayer());
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) {
    handle(e, e.getPlayer());
  }

  @EventHandler
  public void onEntityShootBow(EntityShootBowEvent e) {
    if (e.getEntity() instanceof Player p) {
      handle(e, p);
    }
  }

  // TODO add documentation
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof TNTPrimed t) {
      Data d = new Data(t);
      if (d.has(Miner.BOMB_KEY)) {
        if (d.get(Miner.BOMB_KEY).equals("BUFF_BOMB")) {
          e.setCancelled(true);
        }

        if (e.getEntity() instanceof Player p1 && t.getSource() instanceof Player p) {
          if (PVP.getManager(p).isOn() && PVP.getManager(p1).isOn()) {
            e.setDamage(e.getDamage() * 0.66);
          } else {
            e.setCancelled(true);
          }
          if (d.get(Miner.BOMB_KEY).equals("BUFF_BOMB")) {
            e.setCancelled(true);
            p1.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 1));
            p1.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10 * 20, 1));
            p1.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10 * 20, 1));
          }
        } else if (e.getEntity() instanceof Item) {
          e.setCancelled(true);
        } else {
          e.setDamage(e.getDamage() * 0.66);
        }
      }
      // This isn't simplifiable. Don't ask me why
    } else if (e.getDamager() instanceof Arrow a) {

      if (new Data(a).has(Rider.RICOCHET_KEY) && e.getEntity() instanceof LivingEntity entity) {
        if (a.getShooter() instanceof Player p) {
          boolean cont = true;
          LivingEntity transmitter = entity;
          List<LivingEntity> toRemove = new ArrayList<>();
          while (cont) {
            List<LivingEntity> targets = new ArrayList<>(transmitter.getLocation().getNearbyLivingEntities(2.5));
            if (PVP.getManager(p).isOff()) {
              targets.removeIf(l -> l instanceof Player);
            }
            targets.remove(transmitter);
            targets.removeIf(toRemove::contains);
            targets.removeIf(l -> !(l instanceof Monster));

            if (targets.size() > 0) {
              LivingEntity target = targets.get(new Random().nextInt(targets.size()));
              target.damage(e.getDamage());
              target.setVelocity(target.getLocation().subtract(transmitter.getLocation()).toVector().normalize().multiply(0.33));
              toRemove.add(transmitter);
              transmitter = target;
            } else {
              cont = false;
            }
          }
        }
      }
    } else if (e.getDamager() instanceof Player p) {
      handle(e, p);
    } else if (e.getEntity() instanceof Player p) {
      handle(e, p);
    }
  }

  @EventHandler
  public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
    handle(e, e.getPlayer());
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    handle(e, e.getPlayer());
  }

  @EventHandler
  public void onPlayerHurt(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player p) handle(e, p);
  }

  @EventHandler
  public void onFireballExplode(EntityExplodeEvent e) {
    Data d = new Data(e.getEntity());
    if (d.has(Swordmaster.KEY)) {
      e.setCancelled(true);
      e.getLocation().getWorld().createExplosion(e.getLocation(), 2);
    } else if (d.has(Miner.BOMB_KEY)) {
      if (!d.get(Miner.BOMB_KEY).equals("NORMAL_BOMB")) {
        e.blockList().clear();
      } else {
        e.setYield(100);
      }
    }
  }

  @EventHandler
  public void onPlayerTargeted(EntityTargetLivingEntityEvent e) {
    if (e.getTarget() instanceof Player p) {
      handle(e, p);

      if (armorSets.containsKey(p.getUniqueId()) && (armorSets.get(p.getUniqueId()).set() != ArmorSet.KNIGHT
        && armorSets.get(p.getUniqueId()).set() != ArmorSet.PALADIN)) {

        List<Player> nearby = new ArrayList<>(p.getLocation().getNearbyPlayers(10));
        nearby.removeIf(n -> !armorSets.containsKey(n.getUniqueId()));
        nearby.removeIf(n -> armorSets.get(n.getUniqueId()).set() != ArmorSet.KNIGHT
          && armorSets.get(n.getUniqueId()).set() != ArmorSet.PALADIN);

        if (!nearby.isEmpty()) {
          Random r = new Random();
          if (r.nextInt(3) == 0) {
            e.setTarget(nearby.get(r.nextInt(nearby.size())));
          }
        }
      }
    }
  }

  @EventHandler
  public void onPlayerRide(EntityMountEvent e) {
    if (e.getEntity() instanceof Player p) {
      handle(e, p);
    }
  }

  @EventHandler
  public void onPlayerRideOff(EntityDismountEvent e) {
    if (e.getEntity() instanceof Player p) {
      handle(e, p);
    }
  }

  @EventHandler
  public void onSummonDeath(EntityDeathEvent e) {
    if (new Data(e.getEntity()).has(Paladin.SUMMON_KEY)) {
      e.getDrops().clear();
      e.setDroppedExp(0);
    }
  }

  @EventHandler
  public void prepareSmith(PrepareSmithingEvent e) {
    ItemStack input = e.getInventory().getInputEquipment();
    if (input == null || e.getInventory().getResult() == null || e.getInventory().getInputMineral() == null) {
      e.setResult(null);
    } else {
      Data data = new Data(input.getItemMeta());
      if (data.has(ArmorUtils.KEY)) {
        Armor set = ArmorSet.valueOf(data.get(ArmorUtils.KEY)).init();
        if (set.isSmith() || set.relatedSet() == null) {
          e.setResult(null);
        } else {
          Armor setOut = Objects.requireNonNull(set.relatedSet()).init();
          if (e.getInventory().getInputMineral().getType() == setOut.material().getType()) {
            ItemStack result = ArmorUtils.item(ArmorPiece.find(input.getType()), setOut);
            input.getEnchantments().forEach(result::addUnsafeEnchantment);

            e.setResult(result);
          } else {
            e.setResult(null);
          }
        }
      } else {
        if (!e.getInventory().getInputMineral().getType().equals(Material.NETHERITE_INGOT)) {
          e.setResult(null);
        }
      }
    }
  }

}
