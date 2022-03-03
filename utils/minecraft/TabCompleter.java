package org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vizzoid.raidserver.raidserver.main.meta.PluginHolder;

import java.util.ArrayList;
import java.util.List;


public abstract class TabCompleter extends PluginHolder implements org.bukkit.command.TabCompleter {
  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
    if (command.getName().equalsIgnoreCase(related().name())) {
      if (args.length == 1) {
        return display(args, args0());
      } else if (args.length == 2) {
        return display(args, args1());
      } else if (args.length >= 3) {
        return display(args, args2());
      }
    }
    return null;
  }

  private List<String> display(String[] args, List<String> args0) {
    List<String> display = new ArrayList<>();
    args0.forEach(s -> {
      if (s.toLowerCase().startsWith(args[0].toLowerCase())) display.add(s);
    });
    return display;
  }

  public abstract List<String> args0();

  public List<String> args1() {
    return new ArrayList<>();
  }

  public List<String> args2() {
    return new ArrayList<>();
  }

  public abstract @NotNull MinecraftCommand related();

}
