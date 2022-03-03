package org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.events;

import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects.PlayerObject;

public class PlayerSoundEvent extends SoundEvent {
  public PlayerSoundEvent(PlayerObject object, boolean async) {
    super(object, async);
  }
}
