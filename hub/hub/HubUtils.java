package org.vizzoid.raidserver.raidserver.minecraft.hub.hub;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.hub.leaderboard.Leaderboard;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.WorldUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HubUtils extends Utility {

  private static final Leaderboard leaderboard = new Leaderboard();
  private static final Map<UUID, PlayerData> dataMap = new HashMap<>();

  public static Map<UUID, PlayerData> data() {
    return dataMap;
  }

  public static PlayerData get(UUID uuid) {
    return data().get(uuid);
  }

  public static void remove(UUID uuid) {
    data().remove(uuid);
  }

  public static void put(UUID uuid, PlayerData data) {
    data().put(uuid, data);

  }

  public static void put(UUID uuid, Player p) {
    put(uuid, new PlayerData(p));
  }

  public static Location hub() {
    return WorldUtils.hub().getSpawnLocation().subtract(0.5, 0, 0.5);
  }

  public static Leaderboard leaderboard() {
    return leaderboard;
  }

  public static World hubWorld() {
    World world = new WorldCreator("hub_world")
      .environment(World.Environment.THE_END)
      .generateStructures(false)
      .generator(new HubGenerator())
      .createWorld();
    if (world != null) {
      world.setSpawnLocation(1000001, 127, 1000001);
    }
    return world;
  }

}
