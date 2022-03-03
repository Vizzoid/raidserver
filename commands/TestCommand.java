package org.vizzoid.raidserver.raidserver.minecraft.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;

public class TestCommand extends MinecraftCommand {
  @Override
  public String name() {
    return "test";
  }

  @Override
  public String description() {
    return "Teleports you to Test World (Admin)";
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender instanceof Player p && p.isOp()) {
      World test = new WorldCreator("test").createWorld();
      if (test != null) {
        p.teleport(test.getSpawnLocation());
      } else p.sendMessage(Component.text("Error occurred"));
    }
    return true;
  }
}
