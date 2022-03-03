package org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.vizzoid.raidserver.raidserver.main.meta.Color;

public class NPCBandit extends AIEntity {
  protected NPCBandit(Location loc) {
    super(Component.text("Bandit", Color.RED), loc);
  }
}
