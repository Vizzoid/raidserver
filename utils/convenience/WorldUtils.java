package org.vizzoid.raidserver.raidserver.minecraft.utils.convenience;

import com.google.common.base.Preconditions;
import org.bukkit.World;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.hub.hub.HubUtils;

import java.util.List;

public final class WorldUtils extends Utility {

  public static World getWorld(String name) {
    if (name.equalsIgnoreCase("hub_world")) return hub();
    return getServer().getWorld(name);
  }

  public static List<World> getWorlds() {
    return getServer().getWorlds();
  }

  /**
   * @return default, original world
   */
  public static World world() {
    return worldNotNull("world");
  }

  /**
   * @return default, original nether
   */
  public static World nether() {
    return worldNotNull("world_nether");
  }

  /**
   * @return default, original end
   */
  public static World end() {
    return worldNotNull("world_the_end");
  }

  /**
   * @return default, original world
   */
  public static World test() {
    return worldNotNull("test");
  }

  /**
   * @return default, original nether
   */
  public static World hub() {
    World w = world("hub_world");
    return w != null ? w : HubUtils.hubWorld();
  }

  private static World worldNotNull(String s) {
    World world = world(s);
    Preconditions.checkArgument(world != null, "World is null! Make sure that the worlds are properly named!");
    return world;
  }

  private static World world(String s) {
    for (World world : getWorlds()) {
      if (world.getName().equalsIgnoreCase(s)) {
        return world;
      }
    }
    return null;
  }

}
