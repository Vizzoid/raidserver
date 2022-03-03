package org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

public class Particles {

  public static void spawn(Location loc, Particle p, Material m) {
    if (p.getDataType().equals(Void.class)) {
      loc.getWorld().spawnParticle(p, loc, 1);
    } else {
      loc.getWorld().spawnParticle(p, loc, 1, m.createBlockData());
    }
  }

  public static void spawn(Location loc, Particle p, Material m, int count) {
    if (p.getDataType().equals(Void.class)) {
      loc.getWorld().spawnParticle(p, loc, count);
    } else {
      loc.getWorld().spawnParticle(p, loc, count, m.createBlockData());
    }
  }

}
