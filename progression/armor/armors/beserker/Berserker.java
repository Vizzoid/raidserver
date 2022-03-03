package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.beserker;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttrUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttributeData;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorPiece;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.magmaOpus.MagmaBoss;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;

import java.util.*;

public class Berserker extends Viking {

  // ItemStack is crossbow, integer is bukkitRunnable to load
  // Make sure you check that itemstack is still in inventory, that it hasn't been loaded, and stop previous runnable when a new one is put
  Map<ItemStack, Integer> crossbowAuto = new HashMap<>();

  public Berserker(Player p) {
    super(ArmorSet.BERSERKER, p);
  }

  public Berserker() {
    super(ArmorSet.BERSERKER);
  }

  /**
   * Responsible to automatically load crossbow and dispose projectile
   *
   * @param event thrown when player shoots bow
   * @return instance
   */
  @Override
  public Armor passive2(Event event) {
    if (check(EntityShootBowEvent.class, event, ArmorUtils.crossbow())) {
      EntityShootBowEvent e = (EntityShootBowEvent) event;
      Player p = (Player) e.getEntity();
      if (e.getBow() != null && e.getConsumable() != null) {
        int id = scheduler.delay("RELOAD_CROSSBOW", () -> {
          PlayerInventory inv = p.getInventory();
          CrossbowMeta meta = ((CrossbowMeta) e.getBow().getItemMeta());
          boolean offHand = inv.getItemInOffHand().getType().equals(e.getConsumable().getType());
          if (!meta.hasChargedProjectiles() && inv.contains(e.getBow()) && (inv.contains(e.getConsumable().getType()) || offHand)) {
            meta.addChargedProjectile(new ItemStack(e.getConsumable()));
            int index;
            ItemStack projectileStack;
            if (!offHand) {
              index = inv.first(e.getConsumable().getType());
              projectileStack = inv.getItem(index);
            } else {
              index = 45;
              projectileStack = inv.getItemInOffHand();
            }
            if (projectileStack != null) {
              if (projectileStack.getAmount() > 1) {
                projectileStack.setAmount(projectileStack.getAmount() - 1);
              } else {
                projectileStack.setType(Material.AIR);
              }
              inv.setItem(index, projectileStack);
              e.getBow().setItemMeta(meta);
              Sound.play(Sound.Effect.CROSSBOW_FINISH_LOAD, p);
            }
          }
        }, 100);
        if (crossbowAuto.containsKey(e.getBow())) {
          scheduler.cancel(crossbowAuto.get(e.getBow()));
        }
        crossbowAuto.put(e.getBow(), id);
      }
    }
    return this;
  }

  @Override
  public Armor active2(Event event) {
    if (check(event, ArmorUtils.axes(), ArmorUtils.right())) {
      PlayerInteractEvent e = (PlayerInteractEvent) event;
      Player p = e.getPlayer();

      if (notCooldown()) {
        Sound.play(Sound.Effect.ENDERDRAGON_GROWL, p);
        for (ItemStack i : p.getInventory().getArmorContents()) {
          assert i != null;
          // add Rage Properties
          ItemMeta regMeta = i.getItemMeta();
          for (AttributeData entry : AttrUtils.spd(3)) {
            regMeta.addAttributeModifier(entry.attribute(), new AttributeModifier(UUID.randomUUID(), "generic.temp." + entry.attribute().name(),
              entry.amount(), AttributeModifier.Operation.ADD_NUMBER, ArmorPiece.find(i.getType()).slot()));
          }
          i.setItemMeta(regMeta);

          scheduler.delay("RETURN_FROM_RAGE", () -> {
            ItemMeta meta = i.getItemMeta();
            meta.removeAttributeModifier(i.getType().getEquipmentSlot());

            ArmorUtils.addAttributes(meta, ArmorPiece.find(i.getType()), this);
            i.setItemMeta(meta);
          }, 100);
        }
        setCooldown(ArmorUtils.axes(), 180);
      }
    }
    return this;
  }

  public String name() {
    return "Berserker";
  }

  public List<Component> lore() {
    return new ArrayList<>(Arrays.asList(
      ArmorUtils.text("10% chance to one-shot mobs (not players or bosses)"),
      ArmorUtils.text("Crossbows load automatically"),
      ArmorUtils.text(""),
      ArmorUtils.text("Left click with a crossbow to fire powerful slowness arrow"),
      ArmorUtils.text("Right click your axe to RAGE")));
  }

  public String additive() {
    return "IRON";
  }

  public ItemStack material() {
    return new MagmaBoss().mainDrop();
  }

  public List<AttributeData> attributes() {
    List<AttributeData> attr = AttrUtils.dmg(2);
    attr.addAll(AttrUtils.negDef(1));
    return attr;
  }

  @Override
  public ArmorSet relatedSet() {
    return ArmorSet.VIKING;
  }

  public boolean isSmith() {
    return true;
  }

}
