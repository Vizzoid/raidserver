package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.skeletalSpirit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.paladin.Paladin;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.BossType;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.pet.WranglerUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.Particles;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;

import java.util.*;

public class SkeletonBoss extends Boss {

  public SkeletonHorseEntity ride;
  private short arrowDelay = -1;

  public static List<Location> minionLocs(Location l) {
    List<Location> list = new ArrayList<>();
    list.add(l.clone().add(1, 0, 1));
    list.add(l.clone().add(1, 0, -1));
    list.add(l.clone().add(-1, 0, 0));
    return list;
  }

  // Special made method for summoning minions as Paladin Class
  public static List<Skeleton> summonMinions(Player p) {
    List<Location> list = minionLocs(p.getLocation());
    List<Skeleton> summons = new ArrayList<>();

    int id2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> {
      for (Location l : list) {
        Particles.spawn(l.clone().subtract(0, 0.25, 0), Particle.BLOCK_DUST, l.clone().subtract(0, 1, 0).getBlock().getType(), 3);
      }
    }, 1, 1);

    int id3 = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> {
      for (Location l : list) {
        Sound.location(Sound.Effect.GRASS_DIG, l);
      }
    }, 0, 4);

    Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
      Bukkit.getScheduler().cancelTask(id2);
      Bukkit.getScheduler().cancelTask(id3);
      for (Location l : list) {

        Sound.location(Sound.Effect.GRASS_BREAK, l);
        Monster s = new MinionSkeleton(l.clone().subtract(0, 1, 0), p).getBukkitMonster();
        new Data(s).set(Paladin.SUMMON_KEY, p.getName());

        s.getEquipment().setHelmet(item(Material.IRON_HELMET));
        s.getEquipment().setChestplate(item(Material.IRON_CHESTPLATE));
        s.getEquipment().setItemInMainHand(item(Material.IRON_SWORD));

        Vector v = l.clone().add(0, 1, 0).toVector().subtract(s.getLocation().toVector());
        s.setVelocity(v.normalize().multiply(0.5));
        summons.add((Skeleton) s);

        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
          if (!s.isDead()) {
            s.damage(9999);
          }
        }, 45 * 20);
      }
    }, 20);
    return summons;
  }

  @Override
  public float MAX_HEALTH() {
    float health = 250 * get().getLocation().getNearbyPlayers(50).size();
    if (health == 0) health = 250;
    return health;
  }

  public void spawn(Location l, BossType type, @Nullable LivingEntity ignored) {
    super.spawn(l, type, new SkeletonBossEntity(l).getBukkitLivingEntity());

    l.getWorld().strikeLightningEffect(l);

    ride = new SkeletonHorseEntity(l, get());
    Entity craftRide = ride.getBukkitEntity();

    boss.setPersistent(false);
    ride.persist = false;

    craftRide.addPassenger(boss);
    new Data(craftRide).set(MINION_KEY, "RIDE");

    Objects.requireNonNull(get().getEquipment()).setHelmet(item(Material.IRON_HELMET));
    get().getEquipment().setChestplate(item(Material.IRON_CHESTPLATE));
    get().getEquipment().setLeggings(item(Material.IRON_LEGGINGS));
    get().getEquipment().setBoots(item(Material.IRON_BOOTS));

    get().getEquipment().setItemInMainHand(item(Material.BOW));
    get().registerAttribute(Attribute.GENERIC_MAX_HEALTH);
    Objects.requireNonNull(get().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(MAX_HEALTH());
    get().setHealth(MAX_HEALTH());

    new Data(boss).set(KEY, "SKELETON_BOSS");
    // Boss bar check, Stop skeleton from not becoming passenger(? Don't know why it happens)
    bar = Bukkit.createBossBar(type.get(), BarColor.WHITE, BarStyle.SEGMENTED_12, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY);
    scheduler.repeat("BAR_UPDATE", () -> {
      bar.removeAll();
      Collection<Player> nearbyNow = boss.getLocation().getNearbyPlayers(50);
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
      if (!ride.getBukkitEntity().getPassengers().contains(boss)) {
        ride.getBukkitEntity().addPassenger(boss);
      }
    }, 0, 20);

    scheduler.repeat("CHARGE_OR_LEAP_TIMER", () -> {
      List<Player> players = new ArrayList<>(craftRide.getLocation().getNearbyPlayers(20));
      if (!players.isEmpty()) {
        players.removeIf(p -> !p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE));
        if (players.size() > 0) {
          Player player = players.get(new Random().nextInt(players.size()));
          ride.stand(true, player);
          Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            ride.stand(false, player);
            ride.charge(player);
          }, 60);
        }
      }
    }, 15 * 20, 16 * 20);
  }

  @Override
  public void phase2() {
    scheduler.repeat("SUMMON_SKELETONS", () -> {
      if (minions.size() < 9) {
        summonMinions(false);
      }
    }, 0, 10 * 20);
  }

  @Override
  public void phase3() {
    ItemStack bow = item(Material.BOW);
    bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
    Objects.requireNonNull(get().getEquipment()).setItemInMainHand(bow);

    arrowDelay = 3;
  }

  @Override
  public void phase4() {
    scheduler.cancel("SUMMON_SKELETONS");
    scheduler.repeat("SUMMON_WITHER_SKELETONS", () -> {
      if (minions.size() < 9) {
        summonMinions(true);
      }
    }, 0, 10 * 20);
  }

  @Override
  public Sound.Music music() {
    return Sound.Music.WARD;
  }

  @Override
  public void death(EntityDeathEvent e) {

    Particles.spawn(get().getLocation(), Particle.EXPLOSION_HUGE, null);
    Sound.location(Sound.Effect.TNT, get().getLocation());
    e.setDeathSound(org.bukkit.Sound.ENTITY_IRON_GOLEM_DEATH);
    e.setDeathSoundVolume(3);
    remove();
    bar.setVisible(false);
    ride.setInvulnerable(false);

    e.getDrops().clear();
    e.getDrops().add(mainDrop());
    e.getDrops().add(subDrop());

    e.setDroppedExp(500);
  }

  @Override
  public ItemStack mainDrop() {
    ItemStack item = new ItemStack(Material.BONE);
    ItemMeta meta = item.getItemMeta();

    List<Component> lore = new ArrayList<>();
    lore.add(Component.text(""));
    lore.add(Component.text("Smith your armor with this to enhance it").decoration(TextDecoration.ITALIC, false));
    lore.add(Component.text("Please don't feed it to your dog").decoration(TextDecoration.ITALIC, true));

    meta.lore(lore);
    meta.displayName(Component.text("Ghastly Bone").decoration(TextDecoration.ITALIC, false));

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

  @Override
  public void event(EntityEvent e) {
    Class<? extends Event> clazz = e.getClass();
    if (EntityShootBowEvent.class.equals(clazz)) {
      if (arrowDelay != -1) {
        arrowDelay--;
        if (arrowDelay == 0) {
          arrowDelay = 3;
          ((Arrow) ((EntityShootBowEvent) e).getProjectile()).setBasePotionData(new PotionData(PotionType.POISON));
        }
      }
    }
  }

  @Override
  public Skeleton get() {
    return ((Skeleton) super.get());
  }

  public void summonMinions(boolean witherSkeleton) {
    List<Location> list = minionLocs(this.get().getLocation());

    int id2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> {
      for (Location l : list) {
        Particles.spawn(l.clone().subtract(0, 0.25, 0), Particle.BLOCK_DUST, l.clone().subtract(0, 1, 0).getBlock().getType(), 3);
      }
    }, 1, 1);

    int id3 = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> {
      for (Location l : list) {
        Sound.location(Sound.Effect.GRASS_DIG, l);
      }
    }, 0, 4);

    Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
      Bukkit.getScheduler().cancelTask(id2);
      Bukkit.getScheduler().cancelTask(id3);

      for (Location l : list) {
        Sound.location(Sound.Effect.GRASS_BREAK, l);
        Monster s;
        if (!witherSkeleton) {
          s = new MinionSkeleton(l.clone().subtract(0, 1, 0), get()).getBukkitMonster();
        } else {
          s = new MinionWitherSkeleton(l.clone().subtract(0, 1, 0), get()).getBukkitMonster();
        }
        new Data(s).set(MINION_KEY, "MINION");
        minions.add(s);

        s.getEquipment().setHelmet(item(Material.IRON_HELMET));
        s.getEquipment().setChestplate(item(Material.IRON_CHESTPLATE));
        s.getEquipment().setItemInMainHand(item(Material.IRON_SWORD));
        Vector v = l.clone().add(0, 1, 0).toVector().subtract(s.getLocation().toVector());
        s.setVelocity(v.normalize().multiply(0.5));

        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
          if (!s.isDead()) {
            s.damage(9999);
          }
        }, 30 * 20);
      }
    }, 20);
  }

}
