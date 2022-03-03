package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.swordsman;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttrUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttributeData;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Upgradeable;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.progression.essence.Carrier;
import org.vizzoid.raidserver.raidserver.minecraft.progression.essence.EssenceUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Swordsman extends Upgradeable {

  public static int LEAP_DELAY = 5;

  public Swordsman(Player p) {
    super(ArmorSet.SWORDSMAN, p);
  }

  protected Swordsman(ArmorSet set, Player p) {
    super(set, p);
  }

  public Swordsman() {
    super(ArmorSet.SWORDSMAN);
  }

  protected Swordsman(ArmorSet set) {
    super(set);
  }

  @Override
  public Armor passive1(Event event) {
    if (check(EntityDamageEvent.class, event)) {
      EntityDamageEvent e = ((EntityDamageEvent) event);
      if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
        e.setDamage(e.getDamage() * 0.1);
      }
    }
    return this;
  }

  @Override
  public Armor active1(Event event) {
    if (check(event, ArmorUtils.swords(), ArmorUtils.right())) {
      PlayerInteractEvent e = (PlayerInteractEvent) event;
      Player p = e.getPlayer();
      if (notCooldown()) {
        Vector v = p.getLocation().getDirection().normalize().multiply(2);
        p.setVelocity(v);
        Sound.play(Sound.Effect.ENDERDRAGON_FLAP, p);
        setCooldown(ArmorUtils.swords(), LEAP_DELAY);
      }
    }
    return this;
  }

  @Override
  public Armor passive2(Event event) {
    return this;
  }

  @Override
  public Armor passive3(Event event) {
    return this;
  }

  @Override
  public Armor passive4(Event event) {
    return this;
  }

  @Override
  public Armor active2(Event event) {
    return this;
  }

  public String name() {
    return "Swordsman";
  }

  public List<Component> lore() {
    return new ArrayList<>(Arrays.asList(
      ArmorUtils.text("Only 10% of all fall damage is taken"),
      ArmorUtils.text(""),
      ArmorUtils.text("Right click while using a sword to leap")));
  }

  public String additive() {
    return "IRON";
  }

  public List<Material> idealWeapon() {
    return ArmorUtils.swords();
  }

  public ItemStack material() {
    return EssenceUtils.essence(Carrier.WITHER_SKELETON);
  }

  public List<AttributeData> attributes() {
    List<AttributeData> attr = AttrUtils.spd(1);
    attr.addAll(AttrUtils.negDmg(1));
    return attr;
  }

  public ArmorSet relatedSet() {
    return ArmorSet.SWORDMASTER;
  }

  public boolean isSmith() {
    return false;
  }
}
