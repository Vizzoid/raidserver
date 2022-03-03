package org.vizzoid.raidserver.raidserver.minecraft.hub.npc.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.hub.npc.NPC;
import org.vizzoid.raidserver.raidserver.minecraft.hub.npc.NPCUtils;
import org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities.AIEntity;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NPCCommand extends MinecraftCommand {
  @Override
  public String name() {
    return "npc";
  }

  @Override
  public String description() {
    return "Adds NPC to world";
  }

  @Override
  public TabCompleter tabCompleter() {
    return new NPCCommandTab();
  }

  /**
   * 0       1     2     3    4     5     6
   * Order for List is: locWorld, locX, locY, locZ, yaw, pitch, say
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender instanceof Player p && p.isOp() && args.length >= 2) {
      switch (args[0].toLowerCase(Locale.ROOT)) {
        case "remove" -> {
          NPC npc = NPCUtils.find(args[1]);
          if (npc != null) {
            npc.destroy();
            Map<String, List<String>> map = getConfig().getSectionMap("npcs");
            map.remove(npc.entity().name().content());
          }
        }
        case "add" -> {
          Location l = p.getLocation();
          Location l1 = p.getEyeLocation();

          Map<String, List<String>> map = getConfig().getSectionMap("npcs");
          List<String> values = new ArrayList<>();
          // We get npc from registry to ensure that the npc is updated after the delay
          NPC.create(AIEntity.create(args[1], l));

          values.add(l.getWorld().getName());
          values.add(String.valueOf(l.getX()));
          values.add(String.valueOf(l.getY()));
          values.add(String.valueOf(l.getZ()));

          values.add(String.valueOf(l1.getYaw()));
          values.add(String.valueOf(l1.getPitch()));

          map.put(args[1], values);
          getConfig().setSection("npcs", map);
        }
        case "say" -> {
          if (args.length >= 3) {
            Map<String, List<String>> map = getConfig().getSectionMap("npcs");
            List<String> list = map.get(args[1]);
            List<String> args1 = new ArrayList<>(List.of(args));
            args1.remove(1);
            args1.remove(0);
            String msg = String.join(" ", args1);
            if (list.size() > 6) {
              list.set(6, msg);
            } else list.add(msg);

            if (NPCUtils.find(args[1]).entity() instanceof AIEntity ai) {
              ai.approach(msg);
              sender.sendMessage(ai.approach());
            }
            map.put(args[1], list);
            getConfig().setSection("npcs", map);
          }
        }
      }
    }
    return true;
  }
}
