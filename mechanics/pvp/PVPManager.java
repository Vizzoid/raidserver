package org.vizzoid.raidserver.raidserver.minecraft.mechanics.pvp;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.vizzoid.raidserver.raidserver.main.meta.Color;

@CanIgnoreReturnValue
public interface PVPManager {

  /**
   * @return value of pvp
   */
  boolean isOn();

  /**
   * No different from isOn() but easier to understand
   */
  default boolean is() {
    return isOn();
  }

  default boolean isOff() {
    return !isOn();
  }

  default PVPManager switchPVP() {
    setPVPAndAlert(isOff());
    return this;
  }

  default PVPManager setPVPOn() {
    setPVP(true);
    return this;
  }

  default PVPManager setPVPOff() {
    setPVP(false);
    return this;
  }

  PVPManager setPVPAndAlert(boolean newValue);

  PVPManager setPVP(boolean newValue);

  Player getPlayer();

  default PVPManager cancel(Cancellable e, boolean alert) {
    e.setCancelled(true);
    if (alert) {
      alert();
    }
    return this;
  }

  private PVPManager alert() {
    getPlayer().sendMessage(PVP.text("You can't attack other players with PVP off!", Color.RED));
    return this;
  }

}
