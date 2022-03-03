package org.vizzoid.raidserver.raidserver.minecraft.utils;

import org.bukkit.Location;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.WorldUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocationUtils extends Utility {

  private static final String REGEX = "*";

  public static String to(Location loc, String name) {
    return loc.getWorld().getName() +
      REGEX + loc.getX() +
      REGEX + loc.getY() +
      REGEX + loc.getZ() +
      REGEX + loc.getYaw() +
      REGEX + loc.getPitch() +
      REGEX + name;
  }

  public static Map.Entry<Location, String> fromWithName(String string) {
    String[] s = string.split(REGEX);
    if (s.length == 7) {
      return new AbstractMap.SimpleEntry<>(new Location(
        getPlugin().getServer().getWorld(s[0]),
        Double.parseDouble(s[1]),
        Double.parseDouble(s[2]),
        Double.parseDouble(s[3]),
        Float.parseFloat(s[4]),
        Float.parseFloat(s[5])),
        s[6]);
    } else {
      throw new IndexOutOfBoundsException("Location String is in incorrect pattern! Use 'LocationUtils.to(<Location>, <String>)'! Violator: " + string);
    }
  }

  public static String to(Location loc) {
    return loc.getWorld().getName() +
      REGEX + loc.getX() +
      REGEX + loc.getY() +
      REGEX + loc.getZ() +
      REGEX + loc.getYaw() +
      REGEX + loc.getPitch();
  }

  public static Location from(String string) {
    String[] s = string.split(REGEX);
    if (s.length == 6) {
      return new Location(
        getPlugin().getServer().getWorld(s[0]),
        Double.parseDouble(s[1]),
        Double.parseDouble(s[2]),
        Double.parseDouble(s[3]),
        Float.parseFloat(s[4]),
        Float.parseFloat(s[5]));
    } else {
      throw new IndexOutOfBoundsException("Location String is in incorrect pattern! Use 'LocationUtils.to(<Location>, <String>)'! Violator: " + string);
    }
  }

  public static String toAll(List<Location> locs) {
    StringBuilder s = new StringBuilder(to(locs.get(0)));
    locs.remove(0);
    for (Location loc : locs) {
      s.append("*").append(to(loc));
    }
    return s.toString();
  }

  public static List<Location> fromAll(String s) {
    String[] locs = s.split(REGEX);
    List<Location> list = new ArrayList<>();
    for (String loc : locs) {
      list.add(from(loc));
    }
    return list;
  }

  public static boolean equals(Location l1, Location l2) {
    return l1.getX() == l2.getX() && l1.getY() == l2.getY() && l1.getZ() == l2.getZ();
  }

  public static boolean isSimilar(Location l1, Location l2) {
    return l1.distanceSquared(l2) < 2;
  }

  public static Location fromString(String world, String x, String y, String z, String yaw, String pitch) {
    return new Location(WorldUtils.getWorld(world),
      Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z),
      Float.parseFloat(yaw), Float.parseFloat(pitch));
  }

}
