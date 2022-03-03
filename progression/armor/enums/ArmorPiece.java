package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

public enum ArmorPiece {
  HELMET("_HELMET", " Helmet", 3, 2, EquipmentSlot.HEAD, "MMM", "M M", "   "),
  CHESTPLATE("_CHESTPLATE", " Chestplate", 8, 2, EquipmentSlot.CHEST, "M M", "MMM", "MMM"),
  LEGGINGS("_LEGGINGS", " Leggings", 6, 2, EquipmentSlot.LEGS, "MMM", "M M", "M M"),
  BOOTS("_BOOTS", " Boots", 3, 2, EquipmentSlot.FEET, "   ", "M M", "M M");

  private final String material;
  private final String name;
  private final double armor;
  private final double armorToughness;
  private final EquipmentSlot slot;
  private final String[] shape;

  ArmorPiece(String material, String name, double armor, double armorToughness, EquipmentSlot slot, String... shape) {
    this.material = material;
    this.name = name;
    this.armor = armor;
    this.armorToughness = armorToughness;
    this.slot = slot;
    this.shape = shape;
  }

  /**
   * @param m material
   * @return piece of armor the material is of
   * throws IllegalStateException if material isn't armor
   */
  public static ArmorPiece find(Material m) {
    String[] a = m.name().split("_");
    if (a.length > 1) {
      return ArmorPiece.valueOf(a[1]);
    }
    throw new IllegalStateException("Material is not armor!");
  }

  public Material material(String material) {
    return Material.valueOf(material + this.material);
  }

  public String get() {
    return name;
  }

  public String[] shape() {
    return shape;
  }

  public double armor() {
    return armor;
  }

  public double armorToughness(boolean smith) {
    if (smith) {
      return armorToughness + 1;
    } else {
      return armorToughness;
    }
  }

  public double resistance() {
    return 1;
  }

  public EquipmentSlot slot() {
    return slot;
  }

}
