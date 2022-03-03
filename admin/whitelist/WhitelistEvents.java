package org.vizzoid.raidserver.raidserver.minecraft.admin.whitelist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftListener;

public class WhitelistEvents extends MinecraftListener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerLogin(AsyncPlayerPreLoginEvent e) {
    if (!getWhitelist().onJoin(e.getPlayerProfile())) {
      e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
      e.kickMessage(Component.text("You are not whitelisted to this server! Use ").color(Color.RED)
        .append(Component.text("'/whitelist " + e.getPlayerProfile().getName() + "'").color(Color.BLUE).decoration(TextDecoration.BOLD, true))
        .append(Component.text(" in the Discord Server to gain access.").color(Color.RED)));
    }
  }

}
