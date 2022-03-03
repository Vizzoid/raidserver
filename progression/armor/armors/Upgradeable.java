package org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors;

import org.bukkit.entity.Player;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;

public abstract class Upgradeable extends Armor {

  protected Upgradeable(ArmorSet set, Player p) {
    super(set, p);
  }

  public Upgradeable(ArmorSet set) {
    super(set);
  }
}
