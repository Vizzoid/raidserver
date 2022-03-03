package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.magmaOpus;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings({"ConstantConditions"})
public class MagmaBossEntity extends MagmaCube {

  public final List<Entity> split = new ArrayList<>();
  private final Map<Location, Material> temporaryMagmaBlocks = new HashMap<>();
  private final Scheduler scheduler;
  private final MagmaBoss boss;
  protected Location splitLoc;

  public MagmaBossEntity(Location loc, MagmaBoss boss) {
    super(EntityType.MAGMA_CUBE, NMS.to(loc.getWorld()));
    this.scheduler = new Scheduler();
    this.boss = boss;

    this.setSize(11);

    this.setPos(loc.getX(), loc.getY(), loc.getZ());
    NMS.to(loc.getWorld()).addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

    this.setCustomName(new TextComponent("Magma Opus"));
    this.setCustomNameVisible(true);

    this.slam(false);
  }

  public static Vector randVelocity(Location loc, boolean posY) {
    Double[] locs = random(posY);
    return loc.clone().add(locs[0], locs[1], locs[2]).toVector().subtract(loc.toVector());
  }

  private static Double[] random(boolean posY) {
    Random r = new Random();

    Double[] locs = {0d, 0d, 0d};
    for (int i = 0; i < 3; i++) {
      int negPos = r.nextBoolean() ? 1 : -1;
      if (i == 1) {
        if (posY) {
          negPos = 1;
        }
      }
      locs[i] = r.nextDouble(2) * negPos;
    }
    return locs;
  }

  @SuppressWarnings({"unchecked", "JavaReflectionMemberAccess"})
  public void setSize(int size) {
    int j = Mth.clamp(size, 1, 127);

    try {
      // Name of obfuscated method
      Field id_size = Slime.class.getDeclaredField("bW");
      id_size.setAccessible(true);
      this.entityData.set((EntityDataAccessor<Integer>) id_size.get(this), j);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
    this.reapplyPosition();
    this.refreshDimensions();
    // Less health means more
    long l = Math.round((size * (-3.35)) + 51.55);
    this.getAttribute(Attributes.ARMOR).setBaseValue(l);
    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2F + 0.1F * (float) j);
    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(j);

    this.xpReward = j;
  }

  public void slam(boolean jump) {
    Vector v = mob().getLocation().add(0, 1, 0).toVector().subtract(mob().getLocation().toVector());
    if (jump) {
      mob().setVelocity(v.normalize().multiply(3));
    }

    String name = "CHECK_ON_GROUND";
    scheduler.repeat(name, () -> {
      if (mob().isOnGround()) {
        scheduler.cancel(name);
        Collection<LivingEntity> nearby = mob().getLocation().getNearbyLivingEntities(20);
        nearby.remove(mob());

        nearby.forEach(l -> {
          if (l instanceof Player && !l.getLocation().subtract(0, 0.0625, 0).getBlock().isPassable()) {
            l.setVelocity(v.normalize().multiply(2));
          } else if (l.isOnGround()) {
            l.setVelocity(v.normalize().multiply(2));
          }
        });
      }
    }, 1, 1);
  }

  public void rush(boolean isNextPhase) {
    List<Player> players = new ArrayList<>(mob().getLocation().getNearbyPlayers(20));
    players.removeIf(p -> p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR);

    Map<Player, Zombie> chasing = new HashMap<>();
    players.forEach(p -> chasing.put(p, minion()));

    String name = "RUSH";
    scheduler.repeat(name, () -> chasing.forEach((k, v) -> {
      if (k.isOnline() && !k.isDead()) {
        NMS.to(v).getNavigation().moveTo(NMS.to(k), 2);

        if (this.isOnGround()) {
          oneRadiiBlocks(v.getLocation().subtract(0, 0.0625, 0).getBlock()).forEach(b -> {
            temporaryMagmaBlocks.putIfAbsent(b.getLocation(), b.getType());
            b.setType(Material.MAGMA_BLOCK);
          });
        }
      }
    }), 0, 1);
    scheduler.delay("RESET_BLOCKS", () -> temporaryMagmaBlocks.forEach((l, d) -> l.getBlock().setType(d)), 30 * 20);
    scheduler.cancelDelay(name, () -> chasing.forEach((k, v) -> v.remove()), (isNextPhase ? 6 : 3) * 20);
  }

  private Zombie minion() {
    Location l = mob().getLocation();
    org.bukkit.entity.Zombie z = l.getWorld().spawn(l, org.bukkit.entity.Zombie.class);
    z.setAdult();

    z.registerAttribute(Attribute.GENERIC_MAX_HEALTH);
    z.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2048);
    z.setHealth(2048);

    z.getEquipment().setBoots(item(Material.LEATHER_BOOTS), true);
    z.getEquipment().setLeggings(item(Material.LEATHER_LEGGINGS), true);
    z.getEquipment().setChestplate(item(Material.LEATHER_CHESTPLATE), true);
    z.getEquipment().setHelmet(new ItemStack(Material.MAGMA_BLOCK), true);

    z.setPersistent(false);
    new Data(z).set(Boss.MINION_KEY, "MINION");
    boss.addMinion(z);

    return z;
  }

  private ItemStack item(Material m) {
    ItemStack item = new ItemStack(m);
    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

    meta.setColor(Color.BLACK);

    item.setItemMeta(meta);
    return item;
  }

  private Mob mob() {
    return this.getBukkitMob();
  }

  private List<Block> oneRadiiBlocks(Block origin) {
    List<Block> blocks = new ArrayList<>();
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        Block b = origin.getRelative(i, 0, j);
        if (b.isSolid()) {
          blocks.add(b);
        }
      }
    }
    return blocks;
  }

  public void split() {
    this.setInvisible(true);
    splitLoc = mob().getLocation();

    for (int i = 0; i < (this.getHealth() * -0.04) + 7; i++) {
      Blaze m = boss.minion(Blaze.class, mob().getLocation());
      Vector v1 = randVelocity(mob().getLocation(), true);
      m.setVelocity(v1);
      new Data(m)
        .set(MagmaBoss.SPLIT_KEY, true)
        .set(Boss.MINION_KEY, "MINION");
      split.add(m);
    }
    scheduler.repeat("SPLIT_INVISIBILITY", () -> {
      if (split.size() > 0) {
        mob().teleport(split.get(0).getLocation().add(0, 20, 0));
      } else {
        unSplit();
      }
    }, 1, 1);
    List<Entity> list = new ArrayList<>(split);
    scheduler.delay("UNSPLIT", () -> {
      for (Entity e : list) {
        if (!e.isDead() && e instanceof LivingEntity l) {
          l.damage(9999);
          split.remove(e);
        }
      }
    }, 60 * 20);
  }

  public void unSplit() {
    scheduler.cancel("SPLIT_INVISIBILITY");
    if (splitLoc != null) {
      this.setInvisible(false);
      mob().teleport(splitLoc.add(0, 20, 0));
      slam(false);
      splitLoc = null;
      boss.split();
    }
  }

}
