package org.vizzoid.raidserver.raidserver.minecraft.progression.boss;

import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.Boss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.blindWidow.SpiderBoss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.magmaOpus.MagmaBoss;
import org.vizzoid.raidserver.raidserver.minecraft.progression.boss.bosses.skeletalSpirit.SkeletonBoss;

import java.lang.reflect.InvocationTargetException;

public enum BossType {
  SKELETON_BOSS(SkeletonBoss.class, "Skeletal Spirit"),
  MAGMA_BOSS(MagmaBoss.class, "Magma Opus"),
  SPIDER_BOSS(SpiderBoss.class, "Blind Widow");

  private final Class<? extends Boss> clazz;
  private final String name;

  BossType(Class<? extends Boss> clazz, String name) {
    this.clazz = clazz;
    this.name = name;
  }

  public Boss init() {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String get() {
    return name;
  }
}
