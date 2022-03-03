package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.main.meta.PluginHolder;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.BossType;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.Phase;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.Particles;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Boss extends PluginHolder {

  public static final Key KEY = new Key("IS_BOSS");
  public static final Key MINION_KEY = new Key("MINION_OR_BOSS");
  public static final Key ITEM_KEY = new Key("SPAWN_ITEM");

  public static final Map<Entity, Boss> bossInstances = new HashMap<>();
  public final Map<Player, Integer> nearby = new HashMap<>();
  public final List<LivingEntity> minions = new ArrayList<>();

  public World world;
  public Location origin;
  public Entity boss;
  public Phase phase = Phase.ONE;
  public BossBar bar;
  public BossType type;

  public Scheduler scheduler;

  public static void handle(EntityEvent e) {
    if (bossInstances.containsKey(e.getEntity())) {
      bossInstances.get(e.getEntity()).event(e);
      if (e.getClass().equals(EntityDamageByEntityEvent.class)) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        if (!new Data(event.getDamager()).has(MINION_KEY)) {
          damage(event);
        } else {
          event.setCancelled(true);
        }
      }
    }
  }

  public static void damage(EntityDamageByEntityEvent e) {
    Boss boss = bossInstances.get(e.getEntity());

    double max = boss.MAX_HEALTH();
    double health = ((LivingEntity) e.getEntity()).getHealth();
    if (boss.phase.equals(Phase.ONE) && health <= (max * 0.75)) {
      boss.phase2();
      boss.phase = Phase.TWO;
    } else if (boss.phase.equals(Phase.TWO) && health <= (max * 0.5)) {
      boss.phase3();
      boss.phase = Phase.THREE;
    } else if (boss.phase.equals(Phase.THREE) && health <= (max * 0.25)) {
      boss.phase4();
      boss.phase = Phase.FOUR;
    }
    boss.updateBossBar();
  }

  public static void handle(EntityDeathEvent e) {
    if (bossInstances.containsKey(e.getEntity())) {
      bossInstances.get(e.getEntity()).death(e);
    } else if (new Data(e.getEntity()).has(MINION_KEY)) {
      e.getDrops().clear();
      bossInstances.values().forEach(Boss::updateMinion);
    }
  }

  public static void create(PlayerInteractEvent e) {
    if (e.getItem() != null) {
      Data data = new Data(e.getItem().getItemMeta());
      if (data.has(ITEM_KEY)) {
        BossType type = BossType.valueOf(data.get(ITEM_KEY));
        create(type, e);
        if (e.getItem().getAmount() > 1) {
          e.getItem().setAmount(e.getItem().getAmount() - 1);
        } else {
          e.getItem().setType(Material.AIR);
        }
        Sound.play(Sound.Effect.GHAST_HURT, e.getPlayer(), 1, 0.5f);
        e.getPlayer().getServer().broadcast(Component.text("A " + type.get() + " has been Summoned!").color(Color.LIGHT_PURPLE));
      }
    }
  }

  public static void create(BossType type, PlayerInteractEvent e) {
    Block block = e.getPlayer().getTargetBlock(10);
    if (block != null) {
      Boss instance = type.init();
      assert instance != null;

      instance.spawn(block.getLocation().add(0, 1, 0), type, null);
    }
  }

  protected static ItemStack item(Material m) {
    ItemStack item = new ItemStack(m);
    item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setUnbreakable(true);
    item.setItemMeta(meta);
    return item;
  }

  public abstract float MAX_HEALTH();

  /**
   * Spawns the boss at the location
   *
   * @param l    location of spawn
   * @param type of boss
   * @param boss instance
   * @throws IllegalArgumentException if boss is null. Boss is handled as entity in subclasses, so if this error
   *                                  is thrown then check if there was a direct call on the method or a subclass is not properly creating the mob
   */
  public void spawn(Location l, BossType type, @Nullable LivingEntity boss) throws IllegalArgumentException {
    if (boss == null) throw new IllegalStateException("Boss cannot be null! Are you sure this is setup correctly?");

    this.scheduler = new Scheduler();

    this.boss = boss;
    this.type = type;
    this.world = boss.getWorld();
    this.origin = l;
    new Data(boss).set(MINION_KEY, "SPAWNER");

    bossInstances.put(boss, this);
  }

  public abstract void phase2();

  public abstract void phase3();

  public abstract void phase4();

  public abstract void death(EntityDeathEvent e);

  public LivingEntity get() {
    return (LivingEntity) boss;
  }

  public abstract Sound.Music music();

  public void remove() {
    scheduler.cancelAll();
    bossInstances.remove(this.boss);
    bar.setVisible(false);
    removeAllMusic();
    new ArrayList<>(minions).forEach(m -> {
      if (!m.isDead()) {
        Particles.spawn(m.getLocation(), Particle.EXPLOSION_LARGE, null);
        Sound.location(Sound.Effect.TNT, m.getLocation());
        m.damage(9999);
      }
    });
  }

  public void updateBossBar() {
    bar.setProgress(get().getHealth() / MAX_HEALTH());
  }

  public Entity hasTarget() {
    return get().getTargetEntity(30);
  }

  public <T extends Entity> T minion(Class<T> clazz, Location loc) {
    T entity = world.spawn(loc, clazz);
    entity.setPersistent(false);
    new Data(entity).set(MINION_KEY, "MINION");
    addMinion((LivingEntity) entity);
    return entity;
  }

  public <T extends net.minecraft.world.entity.Entity> T minion(Class<T> clazz, LivingEntity spawner, Location loc) {
    T entity = null;
    try {
      entity = clazz.getDeclaredConstructor(Location.class, LivingEntity.class).newInstance(loc, spawner);
      entity.persist = false;
      new Data(entity.getBukkitEntity()).set(MINION_KEY, "MINION");
      addMinion((LivingEntity) entity.getBukkitEntity());
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return entity;
  }

  public void addMinion(LivingEntity entity) {
    minions.add(entity);
    updateMinion();
  }

  public void addMusic(Player p) {
    int id = scheduler.repeat("MUSIC_REPEAT", () -> Sound.play(music(), p), 0, Math.toIntExact(music().length()));
    nearby.put(p, id);
  }

  public void removeMusic(Player p) {
    Sound.stop(music(), p);
    scheduler.cancel(nearby.remove(p));
  }

  public void removeAllMusic() {
    new HashMap<>(nearby).forEach((k, v) -> removeMusic(k));
  }

  private void updateMinion() {
    minions.removeIf(Entity::isDead);
  }

  public abstract void event(EntityEvent event);

  public abstract ItemStack mainDrop();


}
