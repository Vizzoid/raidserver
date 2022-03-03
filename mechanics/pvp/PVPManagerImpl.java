package org.vizzoid.raidserver.raidserver.minecraft.mechanics.pvp;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

final class PVPManagerImpl implements PVPManager {

  private final Key KEY = new Key("PVP_ON");

  private Player p;
  private boolean pvp = false;

  PVPManagerImpl(Player p) {
    update(p);
  }

  private void update(Player p) {
    this.p = p;
    Data data = new Data(p);
    if (data.has(KEY)) {
      this.pvp = data.getBool(KEY);
    } else setPVPOff();
  }

  @Override
  public boolean isOn() {
    return pvp;
  }

  @Override
  public PVPManager setPVPAndAlert(boolean newValue) {
    setPVP(newValue);
    PVP.sendUpdate(p, pvp);
    return this;
  }

  @Override
  public PVPManager setPVP(boolean newValue) {
    nullPlayer();
    pvp = newValue;
    new Data(p).set(KEY, pvp);
    return this;
  }

  @Override
  public Player getPlayer() {
    nullPlayer();
    return p;
  }

  /**
   * Throws error if player is null -- empty PVPManagers cannot be changed before assigned
   */
  public void nullPlayer() {
    Preconditions.checkNotNull(p, "Manager has been updated before assignment!");
  }
}
