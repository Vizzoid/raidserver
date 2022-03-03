package org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.vizzoid.raidserver.raidserver.main.meta.Color;

public class NPCBartender extends AIEntity {
  protected NPCBartender(Location loc) {
    super(Component.text("Bartender", Color.GOLD), loc);
  }
}
