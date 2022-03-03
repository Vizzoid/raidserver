package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.magmaOpus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.BossType;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.Phase;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet.WranglerUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.Particles;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound.Effect;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound.Music;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import java.util.*;

public class MagmaBoss extends Boss {

  public static final Key SPLIT_KEY = new Key("SPLIT_FROM_BOSS");
  private boolean slam = true;

  @Override
  public float MAX_HEALTH() {
    float health = 500 * get().getLocation().getNearbyPlayers(50).size();
    if (health == 0) health = 500;
    return health;
  }

  @Override
  public void spawn(Location l, BossType type, @Nullable LivingEntity ignored) {
    super.spawn(l, type, new MagmaBossEntity(l.add(0, 75, 0), this).getBukkitLivingEntity());

    boss.setPersistent(false);
    new Data(boss).set(KEY, "MAGMA_OPUS");

    get().registerAttribute(Attribute.GENERIC_MAX_HEALTH);
    Objects.requireNonNull(get().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(MAX_HEALTH());
    get().setHealth(MAX_HEALTH());

    bar = Bukkit.createBossBar(type.get(), BarColor.RED, BarStyle.SEGMENTED_12, BarFlag.DARKEN_SKY);
    scheduler.repeat("BAR_UPDATE", () -> {
      bar.removeAll();
      Collection<Player> nearbyNow = boss.getLocation().getNearbyPlayers(50, 100, 50);
      for (Player p : nearbyNow) {
        if (!nearby.containsKey(p)) {
          addMusic(p);
        }
      }
      for (Player p : new ArrayList<>(nearby.keySet())) {
        if (!nearbyNow.contains(p)) {
          removeMusic(p);
        }
      }
      for (Player player : nearbyNow) {
        bar.addPlayer(player);
      }
    }, 0, 20);

    scheduleSlam(false);
  }

  public void scheduleSlam(boolean isRush) {
    scheduler.repeat("SLAM_OR_RUSH_TIMER", () -> {
      List<Player> players = new ArrayList<>(boss.getLocation().getNearbyPlayers(20));
      if (!players.isEmpty()) {
        players.removeIf(p -> !p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE));
        if (players.size() > 0) {
          if (isRush) {
            if (slam) {
              boss().slam(true);
              slam = false;
            } else {
              boss().rush(phase == Phase.FOUR);
              slam = true;
            }
          } else {
            boss().slam(true);
          }
        }
      }
    }, 15 * 20, 15 * 20);
  }

  public void cancelSlam() {
    scheduler.cancel("SLAM_OR_RUSH_TIMER");
  }

  // Begins rush
  @Override
  public void phase2() {
    cancelSlam();
    scheduleSlam(true);
    boss().setSize(9);
    for (int j = 0; j < 4; j++) {
      minion(MagmaCube.class, boss.getLocation()).setSize(2);
    }
  }

  // Splits up
  @Override
  public void phase3() {
    boss().setSize(7);
    for (int j = 0; j < 4; j++) {
      minion(MagmaCube.class, boss.getLocation()).setSize(3);
    }
    split();
  }

  public void split() {
    scheduler.delay("SPLIT_TIMER", () -> {
      cancelSlam();
      boss().split();
    }, 20 * 20);
  }

  // Rush is longer
  @Override
  public void phase4() {
    boss().setSize(5);
    for (int j = 0; j < 4; j++) {
      minion(MagmaCube.class, boss.getLocation()).setSize(4);
    }
  }

  @Override
  public void death(EntityDeathEvent e) {

    Particles.spawn(get().getLocation(), Particle.EXPLOSION_HUGE, null);
    Sound.location(Effect.TNT, get().getLocation());
    e.setDeathSound(org.bukkit.Sound.ENTITY_ENDER_DRAGON_DEATH);
    e.setDeathSoundVolume(3);
    remove();

    e.getDrops().clear();
    e.getDrops().add(mainDrop());
    e.getDrops().add(subDrop());

    e.setDroppedExp(500);
  }

  @Override
  public Music music() {
    return Music.PIGSTEP;
  }

  @Override
  public void event(EntityEvent event) {

  }

  @Override
  public ItemStack mainDrop() {
    ItemStack item = new ItemStack(Material.SLIME_BALL);
    ItemMeta meta = item.getItemMeta();

    List<Component> lore = new ArrayList<>();
    lore.add(Component.text(""));
    lore.add(Component.text("Smith your armor with this to enhance it").decoration(TextDecoration.ITALIC, false));
    lore.add(Component.text("It's like jello! Wonder how it tastes...").decoration(TextDecoration.ITALIC, true));

    meta.lore(lore);
    meta.displayName(Component.text("Crystal Gelatin").decoration(TextDecoration.ITALIC, false));

    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(meta);

    item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    item.setAmount(4);
    return item;

  }

  public ItemStack subDrop() {
    Random r = new Random();
    if (r.nextBoolean()) {
      return WranglerUtils.wrangler(1);
    }
    return new ItemStack(Material.AIR);
  }

  public MagmaBossEntity boss() {
    return (MagmaBossEntity) NMS.to(boss);
  }
}
