package org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.vizzoid.raidserver.raidserver.main.meta.Color;

import java.util.Locale;

public class AIEntity implements NPCEntity {
  private final TextComponent name;
  private final Location loc;
  private TextComponent approachText;

  private AIEntity(String name, Location loc) {
    this.name = Component.text(name);
    this.loc = loc;
  }

  protected AIEntity(TextComponent name, Location loc) {
    this.name = name;
    this.loc = loc;
  }

  public static AIEntity create(String name, Location loc) {
    switch (name.toLowerCase(Locale.ROOT)) {
      case "bartender" -> {
        return new NPCBartender(loc);
      }
      case "bandit" -> {
        return new NPCBandit(loc);
      }
    }
    return new AIEntity(name, loc);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AIEntity aiEntity = (AIEntity) o;

    return name.equals(aiEntity.name);
  }

  public TextComponent name() {
    return name;
  }

  public Location loc() {
    return loc;
  }

  public void approach(String approachText) {
    System.out.println(approachText);
    this.approachText = Component.text(" [" + name().content() + "] ", name().color()).append(Component.text(approachText, Color.WHITE));
  }

  public TextComponent approach() {
    return approachText;
  }

}
