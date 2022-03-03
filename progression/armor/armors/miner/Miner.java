package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.miner;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttrUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttributeData;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

import java.util.*;

public class Miner extends Armor {

  public static final Key BOMB_KEY = new Key("SPECIAL_BOMB");
  public static int BOMB_DELAY = 20;

  public Miner(Player p) {
    super(ArmorSet.MINER, p);
  }

  public Miner() {
    super(ArmorSet.MINER);
  }

  @Override
  public Armor passive1(Event event) {
    if (check(PlayerDeathEvent.class, event)) {
      PlayerDeathEvent e = (PlayerDeathEvent) event;
      Player p = e.getPlayer();

      TNTPrimed tnt = p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
      Location loc = tnt.getLocation();
      new Data(tnt)
        .set(BOMB_KEY, "DEATH_BOMB");

      tnt.setSource(p);
      scheduler.repeat("SEEKING_BOMB", () -> {
        tnt.getChunk().load();
        tnt.getLocation().getNearbyLivingEntities(5).forEach(l -> {
          if (l instanceof Mob m) {
            NMS.to(m).getNavigation().moveTo(loc.getX(), loc.getY(), loc.getZ(), 1);
          }
        });
      }, 0, 1);
      scheduler.cancelDelay("SEEKING_BOMB", 4 * 20);
    }
    return this;
  }

  @Override
  public Armor passive2(Event event) {
    if (check(BlockBreakEvent.class, event, ArmorUtils.pickaxes())) {
      BlockBreakEvent e = (BlockBreakEvent) event;
      Block b = e.getBlock();

      blocksToBreak(b, e.getPlayer().getLocation()).forEach(block -> {
        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
          block.getDrops().clear();
        }
        if (!indestructible(block)) {
          block.breakNaturally(e.getPlayer().getInventory().getItemInMainHand());
        }
      });
    }
    return this;
  }

  // ToDo tidy up this shit
  private List<Block> blocksToBreak(Block b, Location l) {
    List<Block> blocks = new ArrayList<>();
    switch (closerTo(l)) {

      case NORTH -> {
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ() - 1));
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ() - 2));
        //blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ() - 3));
        //blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ() - 4));
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 1));
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 2));
        // blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 3));
        // blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 4));
      }
      case SOUTH -> {
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ() + 1));
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ() + 2));
        //blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ() + 3));
        //blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ() + 4));
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() + 1));
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() + 2));
        //blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() + 3));
        //blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() + 4));
      }
      case EAST -> {
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX() + 1, b.getY() - 1, b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX() + 2, b.getY() - 1, b.getZ()));
        //blocks.add(b.getWorld().getBlockAt(b.getX() + 3, b.getY() - 1, b.getZ()));
        //blocks.add(b.getWorld().getBlockAt(b.getX() + 4, b.getY() - 1, b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX() + 1, b.getY(), b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX() + 2, b.getY(), b.getZ()));
        //blocks.add(b.getWorld().getBlockAt(b.getX() + 3, b.getY(), b.getZ()));
        //blocks.add(b.getWorld().getBlockAt(b.getX() + 4, b.getY(), b.getZ()));
      }
      case WEST -> {
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX() - 1, b.getY() - 1, b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX() - 2, b.getY() - 1, b.getZ()));
        //blocks.add(b.getWorld().getBlockAt(b.getX() - 3, b.getY() - 1, b.getZ()));
        //blocks.add(b.getWorld().getBlockAt(b.getX() - 4, b.getY() - 1, b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX() - 1, b.getY(), b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX() - 2, b.getY(), b.getZ()));
        //blocks.add(b.getWorld().getBlockAt(b.getX() - 3, b.getY(), b.getZ()));
        //blocks.add(b.getWorld().getBlockAt(b.getX() - 4, b.getY(), b.getZ()));
      }
      case UP -> {
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() + 1, b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() + 2, b.getZ()));
        //blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() + 3, b.getZ()));
        //blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() + 4, b.getZ()));
      }
      case DOWN -> {
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ()));
        blocks.add(b.getWorld().getBlockAt(b.getX(), b.getY() - 2, b.getZ()));
      }
    }
    return blocks;
  }

  private Direction closerTo(Location l) {
    Direction pitch = pitch(l.getPitch());
    if (pitch.equals(Direction.STRAIGHT)) {
      return yaw(l.getYaw());
    }
    return pitch;
  }

  public Direction yaw(double yaw) {
    Set<Direction> directions = new HashSet<>();

    directions.add(Math.abs(yaw - Direction.NORTH.value) < Math.abs(yaw - Direction.WEST.value)
      ? Direction.NORTH : Direction.WEST);

    Direction d1 = Math.abs(yaw - Direction.WEST.value) < Math.abs(yaw - Direction.SOUTH.value)
      ? Direction.WEST : Direction.SOUTH;
    if (!directions.add(d1)) {
      return d1;
    }

    Direction d2 = Math.abs(yaw - Direction.SOUTH.value) < Math.abs(yaw - Direction.EAST.value)
      ? Direction.SOUTH : Direction.EAST;
    if (!directions.add(d2)) {
      return d2;
    }

    Direction d3 = Math.abs(yaw - Direction.EAST.value) < Math.abs(yaw - Direction.NORTH_NEG.value)
      ? Direction.EAST : Direction.NORTH;
    if (!directions.add(d3)) {
      return d3;
    }

    return Direction.NORTH;
  }

  public Direction pitch(double pitch) {
    Set<Direction> directions = new HashSet<>();

    Direction d1 = Math.abs(pitch - Direction.UP.value) < Math.abs(pitch - Direction.STRAIGHT.value)
      ? Direction.UP : Direction.STRAIGHT;
    directions.add(d1);

    Direction d2 = Math.abs(pitch - Direction.STRAIGHT.value) < Math.abs(pitch - Direction.DOWN.value)
      ? Direction.STRAIGHT : Direction.DOWN;
    if (!directions.add(d2)) {
      return Direction.STRAIGHT;
    }

    if (d2 == Direction.DOWN) {
      return Direction.DOWN;
    }
    if (d1 == Direction.UP) {
      return Direction.UP;
    }

    return null;
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
  public Armor active1(Event event) {
    if (check(PlayerToggleSneakEvent.class, event, ArmorUtils.pickaxes())) {
      PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
      Player p = e.getPlayer();

      if (notCooldown()) {
        TNTPrimed tnt = p.getWorld().spawn(p.getEyeLocation(), TNTPrimed.class);
        Sound.play(Sound.Effect.TNT_PRIME, p);
        new Data(tnt).set(BOMB_KEY, "BUFF_BOMB");

        tnt.setSource(p);
        tnt.setVelocity(p.getEyeLocation().getDirection().normalize().multiply(0.5));

        int id = scheduler.repeat("BOMB_ON_CONTACT", () -> {
          Location loc = tnt.getLocation().subtract(0, 0.0625, 0);
          if (!loc.getBlock().getType().equals(Material.AIR)) {
            tnt.setFuseTicks(20);
            scheduler.cancel("BOMB_ON_CONTACT");
          }
        }, 1, 1);
        scheduler.cancelDelay(id, 4 * 20);
        setCooldown(ArmorUtils.pickaxes(), BOMB_DELAY);
      }
    }
    return this;
  }

  @Override
  public Armor active2(Event event) {
    if (check(event, ArmorUtils.pickaxes(), ArmorUtils.right())) {
      PlayerInteractEvent e = (PlayerInteractEvent) event;
      Player p = e.getPlayer();

      if (notCooldown()) {
        TNTPrimed tnt = p.getWorld().spawn(p.getEyeLocation(), TNTPrimed.class);
        Sound.play(Sound.Effect.TNT_PRIME, p);
        new Data(tnt).set(BOMB_KEY, "NORMAL_BOMB");

        tnt.setSource(p);
        tnt.setVelocity(p.getEyeLocation().getDirection().normalize().multiply(0.5));

        int id = scheduler.repeat("BOMB_ON_CONTACT", () -> {
          Location loc = tnt.getLocation().subtract(0, 0.0625, 0);
          if (!loc.getBlock().getType().equals(Material.AIR)) {
            tnt.setFuseTicks(20);
            scheduler.cancel("BOMB_ON_CONTACT");
          }
        }, 1, 1);
        scheduler.cancelDelay(id, 4 * 20);
        setCooldown(ArmorUtils.pickaxes(), BOMB_DELAY);
      }
    }
    return this;
  }

  private boolean indestructible(Block block) {

    Set<Material> blocks = new HashSet<>();
    blocks.add(Material.BEDROCK);
    blocks.add(Material.STRUCTURE_BLOCK);
    blocks.add(Material.COMMAND_BLOCK);
    blocks.add(Material.CHAIN_COMMAND_BLOCK);
    blocks.add(Material.REPEATING_COMMAND_BLOCK);
    blocks.add(Material.END_PORTAL_FRAME);
    blocks.add(Material.END_PORTAL);
    blocks.add(Material.END_GATEWAY);
    blocks.add(Material.BARRIER);
    blocks.add(Material.JIGSAW);
    blocks.add(Material.LIGHT);
    blocks.add(Material.NETHER_PORTAL);
    blocks.add(Material.ANCIENT_DEBRIS);
    blocks.add(Material.OBSIDIAN);
    blocks.add(Material.CRYING_OBSIDIAN);

    return blocks.contains(block.getType());
  }

  public String name() {
    return "Miner";
  }

  public void color(ItemMeta meta) {
    ((LeatherArmorMeta) meta).setColor(Color.GRAY);
  }

  public List<Component> lore() {
    return new ArrayList<>(Arrays.asList(
      ArmorUtils.text("Digs a tunnel with a pickaxe"),
      ArmorUtils.text("Drops attracting bomb on death"),
      ArmorUtils.text(""),
      ArmorUtils.text("Crouch with pickaxe to throw a buff bomb"),
      ArmorUtils.text("Right click with pickaxe to throw bomb")));
  }

  public String additive() {
    return "LEATHER";
  }

  @Override
  public List<Material> idealWeapon() {
    return ArmorUtils.shovels();
  }

  public ItemStack material() {
    return null;
  }

  public List<AttributeData> attributes() {
    List<AttributeData> attr = AttrUtils.negDmg(2);
    attr.addAll(AttrUtils.negDef(1.5));
    return attr;
  }

  public boolean isSmith() {
    return false;
  }

  enum Direction {
    NORTH(180),
    // Negative version of north as it goes from 180 to -180 on cardinals
    NORTH_NEG(-180),
    EAST(-90),
    WEST(90),
    SOUTH(0),
    UP(-90),
    DOWN(90),
    STRAIGHT(0);

    private final double value;

    Direction(double value) {
      this.value = value;
    }
  }

}
