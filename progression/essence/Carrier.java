package org.vizzoid.raidserver.raidserver.minecraft.progression.essence;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;

public enum Carrier {
  WITHER_SKELETON(EntityType.WITHER_SKELETON, ArmorSet.SWORDSMAN, Material.COAL, "Wither", 2.5, 7),
  MAGMA_CUBE(EntityType.MAGMA_CUBE, ArmorSet.KNIGHT, Material.MAGMA_CREAM, "Magma", 1, 10),
  GHAST(EntityType.GHAST, ArmorSet.VIKING, Material.GHAST_TEAR, "Spectral", 3, 5);

  private final EntityType mob;
  private final ArmorSet set;
  private final Material essence;
  private final String name;
  private final double dropRates;
  private final int carrierRates;

  // Carrier rates: 1 / carrierRates           default: 10 so it'll be 10% or 1/10
  Carrier(EntityType mob, ArmorSet set, Material essence, String name, double dropRates, int carrierRates) {
    this.mob = mob;
    this.set = set;
    this.essence = essence;
    this.name = name;
    this.dropRates = dropRates;
    this.carrierRates = carrierRates;
  }

  public static Carrier get(Entity entity) {
    Carrier e = null;
    for (Carrier c : Carrier.values()) {
      if (entity.getType().equals(c.mob)) {
        e = c;
        break;
      }
    }
    return e;
  }

  public EntityType mob() {
    return mob;
  }

  public Material essence() {
    return essence;
  }

  public String getName() {
    return name;
  }

  public double dropRates() {
    return dropRates;
  }

  public int carrierRates() {
    return carrierRates;
  }

  public ArmorSet set() {
    return set;
  }
}
