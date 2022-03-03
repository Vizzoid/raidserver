package org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects;

import net.kyori.adventure.audience.Audience;
import org.bukkit.SoundCategory;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;

/**
 * These objects are for easier storage and debug
 * Not recommended using for playing sounds as these objects are not created at the same time as sound playing
 */
public abstract class SoundObject {
  private final Sound.Type type;
  private final float volume;
  private final float pitch;
  private final SoundCategory category;
  private final Sound.Source source;
  private Audience audience;

  public SoundObject(Sound.Type type, Audience audience, float volume, float pitch,
                     SoundCategory category,
                     Sound.Source source) {
    this.type = type;
    this.audience = audience;
    this.volume = volume;
    this.pitch = pitch;
    this.category = category;
    this.source = source;
  }

  public Sound.Type type() {
    return type;
  }

  public float volume() {
    return volume;
  }

  public float pitch() {
    return pitch;
  }

  public SoundCategory category() {
    return category;
  }

  public Sound.Source source() {
    return source;
  }

  /**
   * Returns audience for Sound Utility
   * Not recommended for non-PlayerObjects, as this is volatile
   *
   * @return audience
   */
  public Audience audience() {
    return audience;
  }

  /**
   * Sets audience for Sound Utility
   * Temporary storage and not recommended non-PlayerObjects
   *
   * @param audience receiver
   */
  public void audience(Audience audience) {
    this.audience = audience;
  }
}
