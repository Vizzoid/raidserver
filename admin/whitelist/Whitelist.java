package org.vizzoid.raidserver.raidserver.minecraft.admin.whitelist;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.dv8tion.jda.api.entities.User;
import org.vizzoid.raidserver.raidserver.main.meta.PluginHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Whitelist extends PluginHolder {

  private final String KEY = "whitelist";
  // ID of user, whitelist data on user
  private final Map<String, WhitelistData> data = new HashMap<>();

  public Whitelist() {
    // We DON'T want whitelist. Whitelist doesn't allow adding of players who haven't joined, but players cannot join without whitelist
    // We'll be using our own custom whitelist instead.
    getServer().setWhitelist(false);
    getServer().setWhitelistEnforced(false);

    deserialize();
    getPlugin().addDisableLogic("WHITELIST_SERIALIZATION", this::serialize);
  }

  public void add(String id, String name) {
    data.put(id, new WhitelistData(id, name));
  }

  public boolean onJoin(PlayerProfile p) {
    for (WhitelistData data : this.data.values()) {
      if (data.equals(p)) {
        return true;
      }
    }
    return false;
  }

  public void serialize() {
    Map<String, List<String>> map = new HashMap<>();
    data.forEach((k, v) -> map.put(k, v.serialize()));
    getConfig().setSection(KEY, map);
  }

  private void deserialize() {
    if (getConfig().hasSection(KEY)) {
      getConfig().getSectionMap(KEY).forEach((k, l) -> data.put(k, new WhitelistData(k, l)));
    }
  }

  public String getName(User user) {
    return data.get(user.getId()).name();
  }

  public boolean containsUser(User user) {
    return data.containsKey(user.getId());
  }

  public boolean containsName(String name) {
    for (WhitelistData data : this.data.values()) {
      if (data.name().equals(name)) {
        return true;
      }
    }
    return false;
  }

}
