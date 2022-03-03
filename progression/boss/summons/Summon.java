package org.vizzoid.raidserver.raidserver.minecraft.progression.boss.summons;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.vizzoid.raidserver.raidserver.minecraft.progression.essence.Carrier;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.WorldUtils;

public enum Summon {

  SKELETON_BOSS("Summon the Skeletal Spirit", Material.BONE_BLOCK, Carrier.MAGMA_CUBE, 1000000, 125, 1000000),
  MAGMA_BOSS("Summon the Magma Opus", Material.MAGMA_BLOCK, Carrier.GHAST, -1000000, 125, 1000000),
  SPIDER_BOSS("Summon the Blind Widow", Material.COBWEB, Carrier.WITHER_SKELETON, 1000000, 125, -1000000);

  private final String desc;
  private final Material material;
  private final Carrier carrier;
  private final Location loc;

  Summon(String desc, Material material, Carrier carrier, int x, int y, int z) {
    this.desc = desc;
    this.material = material;
    this.carrier = carrier;
    this.loc = new Location(WorldUtils.end(), x, y, z);
  }

  public Component get() {
    return Component.text("Summon").decoration(TextDecoration.OBFUSCATED, true);
  }

  public Material material() {
    return this.material;
  }

  public Location loc() {
    return this.loc;
  }

  public Carrier carrier() {
    return carrier;
  }

  public Component desc() {
    return Component.text(desc);
  }
}
