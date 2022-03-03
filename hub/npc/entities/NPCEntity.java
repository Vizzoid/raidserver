package org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities;

import com.mojang.authlib.GameProfile;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.vizzoid.raidserver.raidserver.minecraft.commands.SkinCommand;

import java.util.UUID;

public interface NPCEntity {

  default UUID uuid() {
    return UUID.randomUUID();
  }

  TextComponent name();

  Location loc();

  default GameProfile profile() {
    GameProfile profile = new GameProfile(uuid(), name().content());
    profile.getProperties().put("textures", SkinCommand.deserialize(name().content().toLowerCase()));
    return profile;
  }

}
