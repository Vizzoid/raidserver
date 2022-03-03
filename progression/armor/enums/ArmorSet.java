package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.AttributeData;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.beserker.Berserker;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.beserker.Viking;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.blacksmith.Blacksmith;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.miner.Miner;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.paladin.Knight;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.paladin.Paladin;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.rider.Rider;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.swordsman.Swordmaster;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.swordsman.Swordsman;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public enum ArmorSet {
  SWORDSMAN(Swordsman.class),
  SWORDMASTER(Swordmaster.class),
  VIKING(Viking.class),
  BERSERKER(Berserker.class),
  KNIGHT(Knight.class),
  PALADIN(Paladin.class),
  MINER(Miner.class),
  BLACKSMITH(Blacksmith.class),
  RIDER(Rider.class);

  private final Class<? extends Armor> clazz;

  ArmorSet(Class<? extends Armor> clazz) {
    this.clazz = clazz;
  }

  public Armor init(Player p) {
    try {
      return clazz.getDeclaredConstructor(Player.class).newInstance(p);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return anonymous();
  }

  public Armor init() {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return anonymous();
  }

  private Armor anonymous() {
    return new Armor(ArmorSet.BERSERKER) { // Anonymous class, if you see this there is a critical error ^ view above
      @Override
      public Armor passive1(Event event) {
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
        return this;
      }

      @Override
      public Armor active2(Event event) {
        return this;
      }

      @Override
      public String name() {
        return "";
      }

      @Override
      public List<Component> lore() {
        return List.of(Component.text(""));
      }

      @Override
      public String additive() {
        return "";
      }

      @Override
      public List<Material> idealWeapon() {
        return List.of(Material.AIR);
      }

      @Override
      public ItemStack material() {
        return new ItemStack(Material.AIR);
      }

      @Override
      public List<AttributeData> attributes() {
        return List.of(new AttributeData(Attribute.GENERIC_FLYING_SPEED, 1.0));
      }

      @Override
      public boolean isSmith() {
        return false;
      }
    };
  }

}
