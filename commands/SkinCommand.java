package org.vizzoid.raidserver.raidserver.minecraft.commands;

import com.mojang.authlib.properties.Property;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinCommand extends MinecraftCommand {
  public static void serialize(String name, Property property) {
    Map<String, List<String>> map = new HashMap<>();
    if (getConfig().hasSection("skin")) {
      map = getConfig().getSectionMap("skin");
    }
    List<String> list = new ArrayList<>();
    list.add(property.getName());
    list.add(property.getValue());
    list.add(property.getSignature());
    map.put(name, list);
    getConfig().setSection("skin", map);
  }

  public static Property deserialize(String name) {
    List<String> list = getConfig().getSectionMap("skin").get(name);
    if (list == null) return null;
    return new Property(list.get(0), list.get(1), list.get(2));
  }

  public static Property property(Player p) {
    return NMS.to(p.getPlayerProfile()).getProperties().get("textures").stream().findFirst().orElseThrow();
  }

  @Override
  public String name() {
    return "skin";
  }

  @Override
  public String description() {
    return "Copy your skin to file";
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender instanceof Player p && sender.isOp() && args.length == 1) {
      serialize(args[0], property(p));
      sender.sendMessage("Saved skin to '" + args[0] + "' pathway.");
    }
    return true;
  }

}
