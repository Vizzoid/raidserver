package org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GlobalObject extends SoundObject {

  private final Collection<? extends Audience> audiences;
  private final Location origin;
  private final int radius;

  public GlobalObject(Sound.Type type, Collection<? extends Audience> audiences, float volume, float pitch, SoundCategory category, Location origin, int radius) {
    super(type, null, volume, pitch, category, Sound.Source.GLOBAL);
    this.audiences = audiences;
    this.origin = origin;
    this.radius = radius;
  }

  public Collection<? extends Audience> audiences() {
    return audiences;
  }

  public List<? extends Audience> audiencesByList() {
    return new ArrayList<>(audiences());
  }

  public int radius() {
    return radius;
  }

  public Location origin() {
    return origin;
  }

  // Recipient for object -- used for passing through "play(soundObject)" methods
  public GlobalObject temp(Audience audience) {
    this.audience(audience);
    return this;
  }
}
