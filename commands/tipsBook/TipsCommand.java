package org.vizzoid.raidserver.raidserver.minecraft.commands.tipsBook;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;

import java.util.List;

public class TipsCommand extends MinecraftCommand {
  @Override
  public String name() {
    return "tips";
  }

  @Override
  public List<String> aliases(String... toAdd) {
    return super.aliases("tip");
  }

  @Override
  public String description() {
    return "Opens book of tips";
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender instanceof Player p) {
      TipsUtils.openBook(p);
    }
    return true;
  }
}
