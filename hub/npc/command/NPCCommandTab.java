package org.vizzoid.raidserver.raidserver.minecraft.hub.npc.command;

import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class NPCCommandTab extends TabCompleter {
  @Override
  public List<String> args0() {
    return new ArrayList<>(List.of("Add", "Remove", "Say"));
  }

  @Override
  public @NotNull MinecraftCommand related() {
    return new NPCCommand();
  }
}
