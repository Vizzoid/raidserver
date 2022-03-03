package org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.vizzoid.raidserver.raidserver.main.meta.PluginHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class MinecraftCommand extends PluginHolder implements CommandExecutor {

  public abstract String name();

  public abstract String description();

  public String usage() {
    return "/<command>";
  }

  public TabCompleter tabCompleter() {
    return null;
  }

  public List<String> aliases(String... toAdd) {
    List<String> aliases = new ArrayList<>();
    aliases.add(name());
    aliases.addAll(List.of(toAdd));
    return aliases;
  }

  public final void executor() {
    PluginCommand cmd = getPlugin().getCommand(name());
    assert cmd != null;
    cmd.setDescription(description());
    cmd.setAliases(aliases());
    cmd.setUsage(usage());

    if (tabCompleter() != null) cmd.setTabCompleter(tabCompleter());
    cmd.setExecutor(this);
  }

}
