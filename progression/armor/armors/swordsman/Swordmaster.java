package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.swordsman;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttrUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttributeData;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.blindWidow.SpiderBoss;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Swordmaster extends Swordsman {

  public static final Key KEY = new Key("BREAK_BLOCKS");
  public static final Key ATTACK_KEY = new Key("ATTACK_NUMBER");
  public static int FIREBALL_DELAY = 20;

  public Swordmaster(Player p) {
    super(ArmorSet.SWORDMASTER, p);
  }

  public Swordmaster() {
    super(ArmorSet.SWORDMASTER);
  }

  @Override
  public Armor active2(Event event) {
    if (check(event, ArmorUtils.bow(), ArmorUtils.left())) {
      PlayerInteractEvent e = (PlayerInteractEvent) event;
      Player p = e.getPlayer();
      Location l = p.getEyeLocation().toVector().add(p.getEyeLocation().getDirection().multiply(2)).toLocation(p.getWorld());

      if (notCooldown(Material.BOW)) {
        Fireball fireball = p.getWorld().spawn(l, Fireball.class);
        fireball.setDirection(p.getLocation().getDirection().normalize().multiply(255));
        new Data(fireball).set(KEY, true);
        Sound.play(Sound.Effect.ENDERDRAGON_SHOOT, p);
        setCooldown(ArmorUtils.bow(), FIREBALL_DELAY);
      }
    }
    return this;
  }

  @Override
  public Armor passive2(Event event) {
    if (check(EntityDamageByEntityEvent.class, event, ArmorUtils.swords())) {
      EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
      if (equals(e.getDamager())) {
        Player p = (Player) e.getDamager();
        Data data = new Data(p);

        if (data.has(ATTACK_KEY)) {
          int i = Integer.parseInt(data.get(ATTACK_KEY));
          if (i != 1) {
            data.set(ATTACK_KEY, String.valueOf(i - 1));
          } else {
            data.set(ATTACK_KEY, "5");
            if (e.getEntity() instanceof LivingEntity l) {
              l.damage(e.getDamage(), p);
              e.setDamage(0);
            }
          }
        } else {
          data.set(ATTACK_KEY, "5");
        }
      }
    }
    return this;
  }

  public String name() {
    return "Swordmaster";
  }

  public List<Component> lore() {
    return new ArrayList<>(Arrays.asList(
      ArmorUtils.text("Only 10% of all fall damage is taken"),
      ArmorUtils.text("Every fifth sword hit pierces armor"),
      ArmorUtils.text(""),
      ArmorUtils.text("Right click while using a sword to leap"),
      ArmorUtils.text("Left click with your bow to send a fireball")));
  }

  public String additive() {
    return "GOLDEN";
  }

  public ItemStack material() {
    return new SpiderBoss().mainDrop();
  }

  public List<AttributeData> attributes() {
    List<AttributeData> attr = AttrUtils.spd(2);
    attr.addAll(AttrUtils.negDmg(1));
    return attr;
  }

  public ArmorSet relatedSet() {
    return ArmorSet.SWORDSMAN;
  }

  public boolean isSmith() {
    return true;
  }
}
