package org.vizzoid.raidserver.raidserver.minecraft.progression.armor;

import org.bukkit.attribute.Attribute;

import java.util.ArrayList;
import java.util.List;

// ToDo knockback related attributes not properly assigned
public class AttrUtils {

  public static List<AttributeData> spd(double scalar) {
    List<AttributeData> list = new ArrayList<>();
    list.add(new AttributeData(Attribute.GENERIC_ATTACK_SPEED, scalar * 0.075));
    list.add(new AttributeData(Attribute.GENERIC_MOVEMENT_SPEED, scalar * 0.0125));
    return list;
  }

  public static List<AttributeData> dmg(double scalar) {
    List<AttributeData> list = new ArrayList<>();
    list.add(new AttributeData(Attribute.GENERIC_ATTACK_DAMAGE, scalar * 0.5));
    list.add(new AttributeData(Attribute.GENERIC_ATTACK_KNOCKBACK, scalar * 0.0125));
    return list;
  }

  public static List<AttributeData> def(double scalar) {
    List<AttributeData> list = new ArrayList<>();
    list.add(new AttributeData(Attribute.GENERIC_MAX_HEALTH, scalar * 1.25));
    list.add(new AttributeData(Attribute.GENERIC_KNOCKBACK_RESISTANCE, scalar * 0.05));
    list.add(new AttributeData(Attribute.GENERIC_ARMOR, scalar + 1));
    list.add(new AttributeData(Attribute.GENERIC_ARMOR_TOUGHNESS, scalar));
    return list;
  }

  public static List<AttributeData> negSpd(double scalar) {
    List<AttributeData> list = new ArrayList<>();
    list.add(new AttributeData(Attribute.GENERIC_ATTACK_SPEED, scalar * -0.075));
    list.add(new AttributeData(Attribute.GENERIC_MOVEMENT_SPEED, scalar * -0.005));
    return list;
  }

  public static List<AttributeData> negDmg(double scalar) {
    List<AttributeData> list = new ArrayList<>();
    list.add(new AttributeData(Attribute.GENERIC_ATTACK_DAMAGE, scalar * -0.25));
    list.add(new AttributeData(Attribute.GENERIC_ATTACK_KNOCKBACK, scalar * -0.0125));
    return list;
  }

  public static List<AttributeData> negDef(double scalar) {
    List<AttributeData> list = new ArrayList<>();
    list.add(new AttributeData(Attribute.GENERIC_MAX_HEALTH, scalar * -0.5));
    list.add(new AttributeData(Attribute.GENERIC_KNOCKBACK_RESISTANCE, scalar * -0.05));
    list.add(new AttributeData(Attribute.GENERIC_ARMOR, scalar * -2d));
    list.add(new AttributeData(Attribute.GENERIC_ARMOR_TOUGHNESS, scalar * -1d));
    return list;
  }

  public static List<AttributeData> empty() {
    return new ArrayList<>();
  }

}
