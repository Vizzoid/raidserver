package org.vizzoid.raidserver.raidserver.minecraft.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;

import java.util.*;

/**
 * TODO FUCK THIS DOESNT SAVE ON RESTART MAKE IT MAKE ITE |CONFIGS CONFIGS
 */
public class Comp extends Utility {

  private static final Map<UUID, List<Component>> playersToAlert = new HashMap<>();

  public static void addAlert(OfflinePlayer player, Component... string) {
    List<Component> alerts = getAlerts(player);
    alerts.addAll(Arrays.asList(string));
    playersToAlert.put(player.getUniqueId(), alerts);
  }

  public static List<Component> getAlerts(OfflinePlayer player) {
    if (playersToAlert.containsKey(player.getUniqueId())) {
      return playersToAlert.get(player.getUniqueId());
    } else {
      return new ArrayList<>();
    }
  }

  public static Map<UUID, List<Component>> get() {
    return playersToAlert;
  }

  public static void queue(OfflinePlayer player, Component c) {
    Player p = player.getPlayer();
    if (p != null) {
      p.sendMessage(c);
    } else {
      addAlert(player, c);
    }
  }

  public static void queue(OfflinePlayer player, String s) {
    Component c = Component.text(s);
    Player p = player.getPlayer();
    if (p != null) {
      p.sendMessage(c);
    } else {
      addAlert(player, c);
    }
  }

  public static void queueErr(OfflinePlayer player, String s) {
    Component c = Component.text(s).decorate(TextDecoration.BOLD).color(Color.RED);
    Player p = player.getPlayer();
    if (p != null) {
      p.sendMessage(c);
    } else {
      addAlert(player, c);
    }
  }

  public static void msg(Audience audience, List<Component> components) {
    for (Component c : components) {
      audience.sendMessage(c);
    }
  }

  public static String text(Component component) {
    return PlainTextComponentSerializer.plainText().serialize(component);
  }
}
