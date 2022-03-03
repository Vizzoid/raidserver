package org.vizzoid.raidserver.raidserver.minecraft.admin.whitelist;

import com.destroystokyo.paper.profile.PlayerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class WhitelistData {
  private final String discordId;
  private String minecraftName;
  private UUID minecraftUUID;

  public WhitelistData(String discordId, String minecraftName) {
    this(discordId, minecraftName, null);
  }

  public WhitelistData(String discordId, String minecraftName, UUID minecraftUUID) {
    this.discordId = discordId;
    this.minecraftName = minecraftName;
    this.minecraftUUID = minecraftUUID;
  }

  public WhitelistData(String discordId, List<String> values) {
    this.discordId = discordId;
    this.minecraftName = values.get(0);
    if (values.size() > 1) {
      this.minecraftUUID = UUID.fromString(values.get(1));
    } else this.minecraftUUID = null;
  }

  public String id() {
    return discordId;
  }

  public String name() {
    return minecraftName;
  }

  public void name(String name) {
    minecraftName = name;
  }

  public UUID uuid() {
    return minecraftUUID;
  }

  public String uuidString() {
    return minecraftUUID != null ? minecraftUUID.toString() : null;
  }

  public void uuid(UUID uuid) {
    minecraftUUID = uuid;
  }

  private List<String> list() {
    List<String> list = new ArrayList<>(List.of(name()));
    if (uuidString() != null) list.add(uuidString());
    return list;
  }

  /**
   * Checks if one of the parameters (Name or UUID) matches player. If both match then return true, if only one does, then update the other value. If none match return false
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof PlayerProfile p) {
      if (name().equals(p.getName())) {
        if (uuid() == null) {
          uuid(p.getId());
          return true;
        } else return uuid().equals(p.getId());
      } else if (uuid() != null && uuid().equals(p.getId())) {
        name(p.getName());
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    int result = discordId.hashCode();
    result = 31 * result + minecraftName.hashCode();
    result = 31 * result + (minecraftUUID != null ? minecraftUUID.hashCode() : 0);
    return result;
  }

  public List<String> serialize() {
    return list();
  }

}
