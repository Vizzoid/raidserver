package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.beserker;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttrUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttributeData;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Upgradeable;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.essence.Carrier;
import org.vizzoid.raidserver.raidserver.minecraft.progression.essence.EssenceUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Viking extends Upgradeable {

  public Viking(Player p) {
    super(ArmorSet.VIKING, p);
  }

  protected Viking(ArmorSet set, Player p) {
    super(set, p);
  }

  public Viking() {
    super(ArmorSet.VIKING);
  }

  protected Viking(ArmorSet set) {
    super(set);
  }

  @Override
  public Armor active1(Event event) {
    if (check(event, ArmorUtils.crossbow(), ArmorUtils.left())) {

      PlayerInteractEvent e = (PlayerInteractEvent) event;
      Player p = e.getPlayer();

      if (notCooldown(Material.CROSSBOW)) {
        Arrow arrow = p.launchProjectile(Arrow.class, p.getLocation().getDirection().normalize().multiply(3));
        arrow.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 40, 255), true);
        Sound.play(Sound.Effect.ARROW_SHOOT, p);
        setCooldown(ArmorUtils.crossbow(), 30);
      }
    }
    return this;
  }

  @Override
  public Armor passive1(Event event) {
    if (check(EntityDamageByEntityEvent.class, event, ArmorUtils.axes())) {
      EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

      Random r = new Random();
      if (!ignore(e.getEntity()) && !new Data(e.getEntity()).has(Boss.KEY) && equals(e.getDamager())) {
        Player p = (Player) e.getDamager();
        if (e.getEntity() instanceof LivingEntity l && p.getAttackCooldown() == 1) {
          if (r.nextInt(10) == 0) {
            Sound.play(Sound.Effect.IRON_GOLEM_DEATH, e.getDamager());
            NMS.to(l).kill();
          }
        }
      }
    }
    return this;
  }

  private boolean ignore(Entity e) {
    List<EntityType> ignore = new ArrayList<>();
    ignore.add(EntityType.PLAYER);
    ignore.add(EntityType.WITHER);
    ignore.add(EntityType.ENDER_DRAGON);
    return ignore.contains(e.getType());
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
    return "Viking";
  }

  public List<Component> lore() {
    return new ArrayList<>(Arrays.asList(
      ArmorUtils.text("10% chance to one-shot mobs (not players or bosses)"),
      ArmorUtils.text(""),
      ArmorUtils.text("Left click with a crossbow to fire powerful slowness arrow")));
  }

  public String additive() {
    return "CHAINMAIL";
  }

  @Override
  public List<Material> idealWeapon() {
    return ArmorUtils.axes();
  }

  public ItemStack material() {
    return EssenceUtils.essence(Carrier.GHAST);
  }

  public List<AttributeData> attributes() {
    List<AttributeData> attr = AttrUtils.dmg(1);
    attr.addAll(AttrUtils.negDef(1));
    return attr;
  }

  @Override
  public ArmorSet relatedSet() {
    return ArmorSet.BERSERKER;
  }

  public boolean isSmith() {
    return false;
  }

}
