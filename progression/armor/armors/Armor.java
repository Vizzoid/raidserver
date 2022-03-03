package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.vizzoid.raidserver.raidserver.main.meta.PluginHolder;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttributeData;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorPiece;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.util.List;
import java.util.UUID;

public abstract class Armor extends PluginHolder {

  protected final Scheduler scheduler;
  private final ArmorSet set;
  public UUID pUUID;

  public Armor(ArmorSet set, Player p) {
    this.set = set;
    this.pUUID = p.getUniqueId();
    scheduler = new Scheduler();
  }

  // Startup
  public Armor(ArmorSet set) {
    this.set = set;
    this.pUUID = null;
    scheduler = new Scheduler();
  }

  public ArmorSet set() {
    return set;
  }

  protected boolean check(Class<? extends Event> target, Event thrown) {
    return target.equals(thrown.getClass());
  }

  protected boolean check(Class<? extends Event> target, Event thrown, List<Material> tools) {
    boolean bool = (target.equals(thrown.getClass()) && tools.contains(player().getInventory().getItemInMainHand().getType()));
    if (!bool) {
      bool = (target.equals(thrown.getClass()) && tools.contains(player().getInventory().getItemInOffHand().getType()));
    }
    return bool;
  }

  protected boolean check(Event thrown, List<Material> tools, List<Action> actions) {
    return PlayerInteractEvent.class.equals(thrown.getClass()) && tools.contains(player().getInventory().getItemInMainHand().getType()) && actions.contains(((PlayerInteractEvent) thrown).getAction());
  }

  protected boolean check(Event thrown, List<Action> actions) {
    return PlayerInteractEvent.class.equals(thrown.getClass()) && actions.contains(((PlayerInteractEvent) thrown).getAction());
  }

  public Player player() {
    return Bukkit.getPlayer(pUUID);
  }

  public void clear() {
    pUUID = null;
  }

  public abstract Armor passive1(Event event);

  public abstract Armor passive2(Event event);

  // Implementation of two secret passive events to complement others
  // Ex: active1 summons buff bomb, passive3 allows bomb to buff players
  public abstract Armor passive3(Event event);

  public abstract Armor passive4(Event event);

  // Boosts damage when weapon is ideal (class's weapon) used as event catcher for easier implementation
  // Damage is not boosted when ideal tool is already a weapon
  public void weaponIsIdeal(EntityDamageByEntityEvent e) {
    if (this.idealWeapon().contains(((Player) e.getDamager()).getInventory().getItemInMainHand().getType())) {
      if (!e.isCritical()) {
        e.setDamage(e.getDamage() + this.damageBoost());
      } else {
        e.setDamage(e.getDamage() + (this.damageBoost() * 1.5));
      }
    }
  }

  public abstract Armor active1(Event event);

  public abstract Armor active2(Event event);

  public void setCooldown(List<Material> list, int timeInSeconds) {
    if (player().getGameMode().equals(GameMode.SURVIVAL) || player().getGameMode().equals(GameMode.ADVENTURE)) {
      list.forEach(m -> player().setCooldown(m, timeInSeconds * 20));
    }
  }

  public boolean notCooldown(Material m) {
    if (player().getGameMode().equals(GameMode.SURVIVAL) || player().getGameMode().equals(GameMode.ADVENTURE)) {
      return player().getCooldown(m) == 0;
    }
    return true;
  }

  public boolean notCooldown() {
    return notCooldown(player().getInventory().getItemInMainHand().getType());
  }

  public boolean notCooldownLeft() {
    return notCooldown(player().getInventory().getItemInOffHand().getType());
  }

  public boolean equals(Entity e) {
    return e.getUniqueId().equals(pUUID);
  }

  public ItemStack result(ArmorPiece piece) {
    return new ItemStack(piece.material(additive()));
  }

  /**
   * This method should be called when player attacks with idealWeapon();
   **/
  public double damageBoost() {
    return 0;
  }

  public ArmorSet relatedSet() {
    return null;
  }

  public abstract String name();

  public abstract List<Component> lore();

  public abstract String additive();

  public abstract List<Material> idealWeapon();

  public abstract ItemStack material();

  public abstract List<AttributeData> attributes();

  public abstract boolean isSmith();

}
