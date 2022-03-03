package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.blindWidow;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.BossType;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet.WranglerUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.Deprecation;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.util.*;

public class SpiderBoss extends Boss {

  public static final Key WEB_KEY = new Key("WEB_EFFECT");

  public static void confuse(Mob m, Scheduler scheduler) {
    confuse(m, scheduler, 5);
  }

  public static void confuse(Mob m, Scheduler scheduler, int delayInSeconds) {
    m.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 0, 255, false, true));

    scheduler.repeat("CONFUSE_ATTACK", () -> {
      List<LivingEntity> targets = new ArrayList<>(m.getLocation().getNearbyLivingEntities(5, 5, 5));
      if (!targets.isEmpty()) {
        m.setTarget(targets.get(new Random().nextInt(targets.size())));
      }
    }, 0, 20);
    scheduler.cancelDelay("CONFUSE_ATTACK", () -> m.setTarget(null), delayInSeconds * 20);
  }

  @Override
  public float MAX_HEALTH() {
    float health = 400 * get().getLocation().getNearbyPlayers(50).size();
    if (health == 0) health = 400;
    return health;
  }

  @Override
  public void spawn(Location l, BossType type, @Nullable LivingEntity ignored) {
    super.spawn(l, type, new SpiderBossEntity(l, this).getBukkitLivingEntity());

    boss.setPersistent(false);
    new Data(boss).set(KEY, "BLIND_WIDOW");

    get().registerAttribute(Attribute.GENERIC_MAX_HEALTH);
    Objects.requireNonNull(get().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(MAX_HEALTH());
    get().setHealth(MAX_HEALTH());

    bar = Bukkit.createBossBar(type.get(), BarColor.RED, BarStyle.SEGMENTED_12, BarFlag.DARKEN_SKY);
    scheduler.repeat("BAR_UPDATE", () -> {
      bar.removeAll();
      Collection<Player> nearbyNow = boss.getLocation().getNearbyPlayers(50, 50, 50);
      for (Player p : nearbyNow) {
        if (!nearby.containsKey(p)) {
          addMusic(p);
          p.setPlayerTime(18000, false);

          if (boss().isHiding) {
            Deprecation.hideEntity(p, boss);
          }
        }
      }
      for (Player p : new ArrayList<>(nearby.keySet())) {
        if (!nearbyNow.contains(p)) {
          removeMusic(p);
          p.resetPlayerTime();

          Deprecation.showEntity(p, boss);
        }
      }
      for (Player player : nearbyNow) {
        bar.addPlayer(player);
      }
    }, 0, 20);

    scheduler.repeat("BLIND_PHASE_ONE", () ->
      nearby.keySet().forEach(p ->
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, true, false))), 0, 20);
  }

  @Override
  public void phase2() {
    scheduler.cancel("BLIND_PHASE_ONE");
    boss().dig();
  }

  public void dig() {
    scheduler.delay("DIG_TIMER", this::dig, 5 * 20);
  }

  @Override
  public void phase3() {

  }

  @Override
  public void phase4() {

  }

  @Override
  public void death(EntityDeathEvent e) {
    remove();
  }

  @Override
  public Sound.Music music() {
    return Sound.Music.NETHER_WASTES;
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

  public SpiderBossEntity boss() {
    return (SpiderBossEntity) NMS.to(boss);
  }

}
