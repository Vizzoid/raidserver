package org.vizzoid.raidserver.raidserver.minecraft.admin.armor;

import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ArmorCommandTab extends TabCompleter {
  @Override
  public List<String> args0() {
    List<String> list = new ArrayList<>();
    for (ArmorSet set : ArmorSet.values()) {
      list.add(set.name());
    }
    return list;
  }

  @Override
  public @NotNull MinecraftCommand related() {
    return new ArmorCommand();
  }
}
