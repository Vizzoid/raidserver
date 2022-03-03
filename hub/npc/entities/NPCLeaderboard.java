package org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.vizzoid.raidserver.raidserver.minecraft.commands.SkinCommand;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.WorldUtils;

public record NPCLeaderboard(OfflinePlayer p, int num) implements NPCEntity {

  @Override
  public TextComponent name() {
    Preconditions.checkNotNull(p.getName());
    return Component.text(p.getName());
  }

  /**
   * Clones player profile but not uuid to allow for said player still joining
   */
  @Override
  public GameProfile profile() {
    GameProfile profile = new GameProfile(uuid(), name().content());
    Property property = SkinCommand.deserialize("skin_save_" + p.getUniqueId());
    if (property == null) return null;
    profile.getProperties().put("textures", property);
    return profile;
  }

  @Override
  public Location loc() {
    return new Location(WorldUtils.hub(), 999979.5, 128, 1000004.5 - (4 * num), -90, 0);
  }

}
