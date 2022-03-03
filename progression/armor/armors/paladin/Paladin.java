package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.paladin;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttrUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttributeData;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.blindWidow.SpiderBoss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.skeletalSpirit.SkeletonBoss;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import java.util.*;

public class Paladin extends Knight {

  public static final Key SUMMON_KEY = new Key("SUMMONED_PALADIN");
  public List<Skeleton> summons = new ArrayList<>();
  public int id;

  public Paladin(Player p) {
    super(ArmorSet.PALADIN, p);
  }

  public Paladin() {
    super(ArmorSet.PALADIN);
  }

  @Override
  public Armor passive2(Event event) {
    if (check(EntityDamageByEntityEvent.class, event, ArmorUtils.pickaxes())) {
      EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

      if (e.isCritical() && e.getEntity() instanceof Mob m && !new Data(m).has(Boss.KEY)) {
        if (new Random().nextInt(5) == 0) {
          SpiderBoss.confuse(m, scheduler);
        }
      }
    }
    return this;
  }

  @Override
  public Armor active2(Event event) {
    if (check(event, ArmorUtils.pickaxes(), ArmorUtils.right())) {
      PlayerInteractEvent e = (PlayerInteractEvent) event;
      if (Objects.equals(e.getHand(), EquipmentSlot.HAND)) {
        Player p = e.getPlayer();

        if (notCooldown()) {
          if (!summons.isEmpty()) {
            summons.addAll(SkeletonBoss.summonMinions(p));
          } else {
            summons = SkeletonBoss.summonMinions(p);
          }
          setCooldown(ArmorUtils.pickaxes(), LAUNCH_DELAY);
        }
      }
    }
    return this;
  }

  public String name() {
    return "Paladin";
  }

  public List<Component> lore() {
    return new ArrayList<>(Arrays.asList(
      ArmorUtils.text("Enemies have a higher chance of targeting only you"),
      ArmorUtils.text("Critical hits with a pickaxe can confuse the mob"),
      ArmorUtils.text(""),
      ArmorUtils.text("Crouch with your shield to launch mobs (including arrows and tnt) backwards"),
      ArmorUtils.text("Right click with your pickaxe to summon minions")));
  }

  public String additive() {
    return "NETHERITE";
  }

  public ItemStack material() {
    return new SkeletonBoss().mainDrop();
  }

  public List<AttributeData> attributes() {
    List<AttributeData> attr = AttrUtils.def(2);
    attr.addAll(AttrUtils.negSpd(1));
    return attr;
  }

  public ArmorSet relatedSet() {
    return ArmorSet.KNIGHT;
  }

  public boolean isSmith() {
    return true;
  }

}
