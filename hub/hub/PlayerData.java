package org.vizzoid.raidserver.raidserver.minecraft.hub.hub;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.util.Collection;

public final class PlayerData extends Utility {

  private static final Scheduler scheduler = new Scheduler();
  private final Location loc;
  private final int level;
  private final float xp;
  private final float saturation;
  private final float exhaustion;
  private final int foodLevel;
  private final double health;
  private final ItemStack[] inv;
  private final Collection<PotionEffect> effects;

  public PlayerData(Player p) {
    this.loc = p.getLocation();
    this.level = p.getLevel();
    this.xp = p.getExp();
    this.inv = p.getInventory().getContents();
    this.saturation = p.getSaturation();
    this.exhaustion = p.getExhaustion();
    this.foodLevel = p.getFoodLevel();
    this.health = p.getHealth();
    this.effects = p.getActivePotionEffects();
  }

  public Location loc() {
    return loc;
  }

  public int level() {
    return level;
  }

  public float xp() {
    return xp;
  }

  public ItemStack[] inv() {
    return inv;
  }

  public void reduce(Player p, boolean clear) {
    p.setLevel(0);
    p.setExp(0);
    if (clear) {
      p.getInventory().clear();
    }
    p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
    p.setGameMode(GameMode.SURVIVAL);

    p.setSaturation(20);
    p.setHealth(20);
    p.setExhaustion(20);
    p.setFoodLevel(20);

    p.teleport(HubUtils.hub());
    getPlugin().addDisableLogic("NORMALIZE_" + p.getUniqueId(), () -> {
      if (p.isOnline()) {
        HubEvents.quit(p, false);
      }
    });
  }

  public void addMusic(Player p) {
    scheduler.repeat("MUSIC_REPEAT", () -> Sound.play(Sound.Music.STAL, p, 1, 1, SoundCategory.RECORDS), 0, Math.toIntExact(Sound.Music.STAL.length()));
  }

  public void removeMusic(Player p) {
    Sound.stop(Sound.Music.STAL, p);
    scheduler.cancel("MUSIC_REPEAT");
  }

  public void normalize(Player p, boolean disable) {
    p.teleport(loc());
    p.setLevel(level());
    p.setExp(xp());
    p.getInventory().setContents(inv());
    p.getActivePotionEffects().forEach(a -> p.removePotionEffect(a.getType()));
    p.addPotionEffects(effects());
    p.setGameMode(GameMode.SURVIVAL);

    p.setSaturation(saturation());
    p.setHealth(health());
    p.setExhaustion(exhaustion());
    p.setFoodLevel(foodLevel());

    removeMusic(p);
    if (disable) {
      getPlugin().removeDisableLogic("NORMALIZE_" + p.getUniqueId());
    }
  }

  public Collection<PotionEffect> effects() {
    return effects;
  }

  public float saturation() {
    return saturation;
  }

  public double health() {
    return health;
  }

  public float exhaustion() {
    return exhaustion;
  }

  public int foodLevel() {
    return foodLevel;
  }
}
