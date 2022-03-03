package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.rider;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttrUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttributeData;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rider extends Armor {

  public static final Key RICOCHET_KEY = new Key("PROJECTILE_RICOCHET");

  public static int CYCLE_DELAY = 25;
  public static int HEAL_DELAY = 45;

  public LivingEntity vehicle = null;
  public float maxHealth = 30;

  private PotionEffectType currentType = PotionEffectType.SPEED;

  public Rider(Player p) {
    super(ArmorSet.RIDER, p);
    if (p.getVehicle() != null) {
      vehicle = (LivingEntity) p.getVehicle();
    }
  }

  public Rider() {
    super(ArmorSet.RIDER);
  }

  public static void arrow(LivingEntity e, Vector direction) {
    Arrow arrow = e.launchProjectile(Arrow.class, direction.normalize().multiply(3));
    new Data(arrow).set(RICOCHET_KEY, true);
  }

  @Override
  public Armor passive1(Event event) {
    if (check(EntityShootBowEvent.class, event, ArmorUtils.crossbow())) {
      EntityShootBowEvent e = (EntityShootBowEvent) event;
      new Data(e.getProjectile()).set(RICOCHET_KEY, true);
    }
    return this;
  }

  private double hitModifier() {
    if (vehicle != null) {
      AttributeInstance health = vehicle.getAttribute(Attribute.GENERIC_MAX_HEALTH);
      return health != null ? (maxHealth / (health.getValue())) : 1;
    } else {
      return 1;
    }
  }

  @Override
  public Armor passive2(Event event) {
    if (check(EntityDamageByEntityEvent.class, event)) {
      EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
      if (equals(e.getDamager())) {
        e.setDamage(e.getDamage() * hitModifier());
      }
    }
    return this;
  }

  @Override
  public Armor passive3(Event event) {
    if (check(EntityMountEvent.class, event)) {
      EntityMountEvent e = (EntityMountEvent) event;
      vehicle = (LivingEntity) e.getMount();
    }
    return this;
  }

  @Override
  public Armor passive4(Event event) {
    if (check(EntityDismountEvent.class, event)) {
      vehicle = null;
    }
    return this;
  }

  @Override
  public Armor active1(Event event) {
    if (check(event, ArmorUtils.crossbow(), ArmorUtils.left())) {
      PlayerInteractEvent e = (PlayerInteractEvent) event;
      Player p = e.getPlayer();

      if (notCooldown(Material.CROSSBOW) && checkValidity()) {
        effect(p);
        effect(vehicle);
        cycle();

        p.sendActionBar(Component.text("Next effect is: " + StringUtils.capitalize(currentType.getName())).color(Color.GREEN));
        Sound.success(p);
        setCooldown(ArmorUtils.crossbow(), CYCLE_DELAY);
      }
    }
    return this;
  }

  private void effect(LivingEntity l) {
    l.addPotionEffect(new PotionEffect(currentType, 10 * 20, 2));
  }

  /**
   * @throws IllegalStateException if cycle is not of the 3 effects
   */
  private void cycle() {
    if (currentType == PotionEffectType.SPEED) currentType = PotionEffectType.INCREASE_DAMAGE;
    else if (currentType == PotionEffectType.INCREASE_DAMAGE) currentType = PotionEffectType.DAMAGE_RESISTANCE;
    else if (currentType == PotionEffectType.DAMAGE_RESISTANCE) currentType = PotionEffectType.SPEED;
    else throw new IllegalStateException("Cycle type is not [Speed, Strength, Resistance], only viable types!");
  }

  public boolean checkValidity(LivingEntity vehicle) {
    return this.vehicle != null && vehicle != null && vehicle.isValid() && vehicle == this.vehicle;
  }

  public boolean checkValidity() {
    return checkValidity(vehicle);
  }

  @Override
  public Armor active2(Event event) {
    if (check(event, ArmorUtils.swords(), ArmorUtils.right())) {

      if (notCooldown() && checkValidity()) {
        player().getLocation().getNearbyLivingEntities(10).forEach(l -> {
          if (!(l instanceof Monster)) {
            l.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1));
            Sound.play(Sound.Effect.BREW, l);
          }
        });
        setCooldown(ArmorUtils.swords(), HEAL_DELAY);
      }
    }
    return this;
  }

  public String name() {
    return "Rider";
  }

  public List<Component> lore() {
    return new ArrayList<>(Arrays.asList(
      ArmorUtils.text("All crossbow shots ricochet off of mobs"),
      ArmorUtils.text("The weaker the ride, the more damage you'll deal"),
      ArmorUtils.text(""),
      ArmorUtils.text("Left click with crossbow to use an effect"),
      ArmorUtils.text("Right click with sword to heal others")));
  }

  public String additive() {
    return "LEATHER";
  }

  public List<Material> idealWeapon() {
    return ArmorUtils.swords();
  }

  public ItemStack material() {
    return null;
  }

  public List<AttributeData> attributes() {
    List<AttributeData> attr = AttrUtils.negDmg(1);
    attr.addAll(AttrUtils.negDef(1));
    return attr;
  }

  public ArmorSet relatedSet() {
    return ArmorSet.SWORDMASTER;
  }

  public boolean isSmith() {
    return false;
  }

}
