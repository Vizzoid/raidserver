package org.vizzoid.raidserver.raidserver.minecraft.hub.npc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.vizzoid.raidserver.raidserver.minecraft.commands.SkinCommand;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;

public class NPCEvents extends MinecraftListener {

  @EventHandler(ignoreCancelled = true)
  public void saveSkinOnJoin(PlayerJoinEvent e) {
    SkinCommand.serialize("skin_save_" + e.getPlayer().getUniqueId(), SkinCommand.property(e.getPlayer()));
  }

}
