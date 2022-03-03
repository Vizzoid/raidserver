package org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects;

import net.kyori.adventure.audience.Audience;
import org.bukkit.SoundCategory;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;

public class PlayerObject extends SoundObject {

  public PlayerObject(Sound.Type type, Audience audience, float volume, float pitch, SoundCategory category) {
    super(type, audience, volume, pitch, category, Sound.Source.PLAYER);
  }

}
