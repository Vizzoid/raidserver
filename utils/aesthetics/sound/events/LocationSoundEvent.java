package org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.events;

import org.bukkit.Location;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects.LocationObject;

public class LocationSoundEvent extends SoundEvent {

  public LocationSoundEvent(LocationObject object, boolean async) {
    super(object, async);
  }

  public Location getLocation() {
    return ((LocationObject) object).location();
  }

}
