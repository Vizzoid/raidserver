package org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft;

import org.vizzoid.raidserver.raidserver.main.meta.PluginHolder;

public abstract class MinecraftListener extends PluginHolder implements org.bukkit.event.Listener {

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
