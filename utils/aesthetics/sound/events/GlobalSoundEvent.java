package org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.events;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects.GlobalObject;

import java.util.List;

public class GlobalSoundEvent extends SoundEvent {

  public GlobalSoundEvent(GlobalObject object, boolean async) {
    super(object, async);
  }

  public List<? extends Audience> getAudiences() {
    return ((GlobalObject) object).audiencesByList();
  }

  public Location getOrigin() {
    return ((GlobalObject) object).origin();
  }

  public int getRadius() {
    return ((GlobalObject) object).radius();
  }

}
