package org.vizzoid.raidserver.raidserver.minecraft.mechanics.pvp;

import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class PVPCommandTab extends TabCompleter {

  @Override
  public List<String> args0() {
    return List.of("On", "Off");
  }

  @Override
  public @NotNull MinecraftCommand related() {
    return new PVPCommand();
  }

  @Override
  public List<String> args1() {
    List<String> names = new ArrayList<>();
    getServer().getOnlinePlayers().forEach(p -> names.add(p.getName()));
    return names;
  }
}
