package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.blacksmith;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttrUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttributeData;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorPiece;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;

import java.util.*;

public class Blacksmith extends Armor {

  public static int COOLDOWN_DELAY = 90;
  public static int HASTE_DELAY = 30;

  private int id = 0;

  public Blacksmith(Player p) {
    super(ArmorSet.BLACKSMITH, p);
    id = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> {
      Player player = player();
      if (player != null) {
        // Living Entity for testing
        Collection<LivingEntity> list = player.getLocation().getNearbyLivingEntities(15);
        list.remove(player());
        list.removeIf(l -> !metal().contains(l.getType()));

        for (ItemStack item : player.getInventory().getArmorContents()) {
          if (item != null) {
            ItemMeta meta = item.getItemMeta();
            meta.removeAttributeModifier(item.getType().getEquipmentSlot());
            ArmorUtils.addAttributes(meta, ArmorPiece.find(item.getType()), this);

            for (AttributeData e : attributes()) {
              meta.addAttributeModifier(e.attribute(), new AttributeModifier(UUID.randomUUID(), "generic." + e.attribute().name(), e.amount(), AttributeModifier.Operation.ADD_NUMBER, item.getType().getEquipmentSlot()));
            }
            for (AttributeData e : AttrUtils.def(0.33 * list.size())) {
              meta.addAttributeModifier(e.attribute(), new AttributeModifier(UUID.randomUUID(), "generic." + e.attribute().name(), e.amount(), AttributeModifier.Operation.ADD_NUMBER, item.getType().getEquipmentSlot()));
            }
            for (AttributeData e : AttrUtils.dmg(0.33 * list.size())) {
              meta.addAttributeModifier(e.attribute(), new AttributeModifier(UUID.randomUUID(), "generic." + e.attribute().name(), e.amount(), AttributeModifier.Operation.ADD_NUMBER, item.getType().getEquipmentSlot()));
            }
            item.setItemMeta(meta);
          } else {
            cancel();
          }
        }
      }
    }, 200, 200);
  }

  public Blacksmith() {
    super(ArmorSet.BLACKSMITH);
  }

  private List<EntityType> metal() {
    return new ArrayList<>(Arrays.asList(
      EntityType.IRON_GOLEM,
      EntityType.SNOWMAN,
      EntityType.MULE,
      EntityType.HORSE,
      EntityType.DONKEY,
      EntityType.VILLAGER,
      EntityType.WANDERING_TRADER,
      EntityType.PUFFERFISH,
      EntityType.PLAYER
    ));
  }

  public void cancel() {
    Bukkit.getScheduler().cancelTask(id);
  }

  @Override
  public Armor passive1(Event event) {
    if (check(EntityDamageByEntityEvent.class, event)) {
      EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
      if (equals(e.getDamager())) {
        e.getEntity().setFireTicks(80);
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
  public Armor active1(Event event) {
    if (check(event, ArmorUtils.hoes(), ArmorUtils.right())) {
      PlayerInteractEvent e = (PlayerInteractEvent) event;
      Player p = e.getPlayer();

      if (notCooldown()) {
        Collection<Player> players = p.getLocation().getNearbyPlayers(15);
        players.forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 10 * 20, 2)));
        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 10 * 20, 2));
        Sound.play(Sound.Effect.ANVIL_USE, p);
        setCooldown(ArmorUtils.hoes(), HASTE_DELAY);
      }
    }
    return this;
  }

  @Override
  public Armor active2(Event event) {
    if (check(PlayerToggleSneakEvent.class, event, ArmorUtils.shield())) {
      PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
      Player p = e.getPlayer();

      if (notCooldown(Material.SHIELD)) {
        Collection<Player> players = p.getLocation().getNearbyPlayers(5);
        players.remove(p);
        players.forEach(player -> ArmorUtils.all().forEach(m -> player.setCooldown(m, 0)));
        Sound.play(Sound.Effect.BREW, p);
        setCooldown(ArmorUtils.shield(), COOLDOWN_DELAY);
      }
    }
    return this;
  }

  public String name() {
    return "Blacksmith";
  }

  public List<Component> lore() {
    return new ArrayList<>(Arrays.asList(
      ArmorUtils.text("All attacks inflict fire"),
      ArmorUtils.text("The more metal-based mobs near you, the stronger you are"),
      ArmorUtils.text(""),
      ArmorUtils.text("Right click with a hoe to give all nearby players haste"),
      ArmorUtils.text("Crouch with shield to refresh the cooldowns of all surrounding players")));
  }

  public String additive() {
    return "GOLDEN";
  }

  @Override
  public List<Material> idealWeapon() {
    return ArmorUtils.hoes();
  }

  @Override
  public double damageBoost() {
    return 1.5;
  }

  public ItemStack material() {
    return new ItemStack(Material.COOKED_BEEF);
  }

  public List<AttributeData> attributes() {
    List<AttributeData> attr = AttrUtils.negDmg(1);
    attr.addAll(AttrUtils.negDef(1));
    return attr;
  }

  public boolean isSmith() {
    return false;
  }

}
