package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.paladin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.vizzoid.raidserver.raidserver.minecraft.mechanics.pvp.PVP;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttrUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttributeData;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Upgradeable;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.essence.Carrier;
import org.vizzoid.raidserver.raidserver.minecraft.progression.essence.EssenceUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.Particles;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Knight extends Upgradeable {

  public static int LAUNCH_DELAY = 60;

  public Knight(Player p) {
    super(ArmorSet.KNIGHT, p);
  }

  protected Knight(ArmorSet set, Player p) {
    super(set, p);
  }

  public Knight() {
    super(ArmorSet.KNIGHT);
  }

  protected Knight(ArmorSet set) {
    super(set);
  }

  @Override
  public Armor passive1(Event event) {
    return this;
  }

  @Override
  public Armor active1(Event event) {
    if (check(PlayerToggleSneakEvent.class, event, ArmorUtils.shield())) {
      PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
      Player p = e.getPlayer();

      if (notCooldown(Material.SHIELD)) {
        Location loc1 = p.getLocation().subtract(0, 3, 0);

        List<Entity> l = p.getNearbyEntities(5, 5, 5);
        if (PVP.getManager(p).isOff()) {
          l.removeIf(entity -> entity.getType() == EntityType.PLAYER);
        } else {
          l.removeIf(entity -> {
            if (entity instanceof Player player) {
              return PVP.getManager(player).isOff();
            }
            return false;
          });
        }
        l.removeIf(entity -> new Data(entity).has(Boss.KEY));
        if (!l.isEmpty()) {
          List<Integer> ids = new ArrayList<>();
          for (Entity entity : l) {
            Location loc2 = entity.getLocation();
            Vector v = loc2.toVector().subtract(loc1.toVector());
            entity.setVelocity(v.normalize().multiply(2.5));
            int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> Particles.spawn(entity.getLocation(), Particle.FALLING_DUST, Material.WHITE_WOOL), 1, 5);
            ids.add(id);
          }
          Sound.play(Sound.Effect.ENDERDRAGON_FLAP, p);
          Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> ids.forEach(id -> Bukkit.getScheduler().cancelTask(id)), 40);
          setCooldown(ArmorUtils.shield(), LAUNCH_DELAY);
        }
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
    return "Knight";
  }

  public List<Component> lore() {
    return new ArrayList<>(Arrays.asList(
      ArmorUtils.text("Enemies have a higher chance of targeting only you"),
      ArmorUtils.text(""),
      ArmorUtils.text("Crouch with your shield to launch mobs (including arrows and tnt) backwards")));
  }

  public String additive() {
    return "DIAMOND";
  }

  public List<Material> idealWeapon() {
    return ArmorUtils.pickaxes();
  }

  @Override
  public double damageBoost() {
    return 2;
  }

  public ItemStack material() {
    return EssenceUtils.essence(Carrier.MAGMA_CUBE);
  }

  public List<AttributeData> attributes() {
    List<AttributeData> attr = AttrUtils.def(1);
    attr.addAll(AttrUtils.negSpd(1));
    return attr;
  }

  public ArmorSet relatedSet() {
    return ArmorSet.PALADIN;
  }

  public boolean isSmith() {
    return false;
  }

}
