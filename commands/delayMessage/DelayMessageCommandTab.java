package org.vizzoid.raidserver.raidserver.minecraft.commands.delayMessage;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DelayMessageCommandTab extends TabCompleter {

  @Override
  public List<String> args0() {
    List<String> names = new ArrayList<>();
    for (OfflinePlayer p : getServer().getOfflinePlayers()) {
      names.add(p.getName());
    }
    return names;
  }

  @Override
  public @NotNull MinecraftCommand related() {
    return new DelayMessageCommand();
  }
}
