package org.vizzoid.raidserver.raidserver.minecraft.admin.cache;

import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.TabCompleter;

import java.util.List;

public class CacheCommandTab extends TabCompleter {

  @Override
  public List<String> args0() {
    return List.of("All", "Schedulers", "Bosses", "Minions", "Drops", "Mobs");
  }

  @Override
  public @NotNull MinecraftCommand related() {
    return new CacheCommand();
  }
}
