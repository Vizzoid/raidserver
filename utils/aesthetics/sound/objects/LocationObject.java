package org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;

public class LocationObject extends SoundObject {
  private final Location location;

  public LocationObject(Sound.Type type, Location location, float volume, float pitch, SoundCategory category) {
    super(type, null, volume, pitch, category, Sound.Source.LOCATION);
    this.location = location;
  }

  public Location location() {
    return location;
  }
}
