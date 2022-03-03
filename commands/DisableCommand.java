package org.vizzoid.raidserver.raidserver.minecraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;

public class DisableCommand extends MinecraftCommand {
  @Override
  public String name() {
    return "disable";
  }

  @Override
  public String description() {
    return "Disables RaidServer plugin";
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender.equals(getServer().getConsoleSender()) || sender.isOp()) {
      if (getController().isPluginEnabled(getPlugin())) {
        getController().disablePlugin(getPlugin());
      } else getController().enablePlugin(getPlugin());
    }
    return true;
  }
}
