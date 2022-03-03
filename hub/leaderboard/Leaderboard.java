package org.vizzoid.raidserver.raidserver.minecraft.hub.leaderboard;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scoreboard.Objective;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.main.meta.PluginHolder;
import org.vizzoid.raidserver.raidserver.minecraft.hub.npc.NPC;
import org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities.NPCLeaderboard;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.WorldUtils;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.util.*;

public class Leaderboard extends PluginHolder {

  private final Participation participation;
  private final Combat combat;
  private final Survival survival;

  private final List<OfflinePlayer> players = new ArrayList<>();
  private final List<NPC> npcs = new ArrayList<>();

  public Leaderboard() {
    Scheduler scheduler = new Scheduler();

    participation = Participation.values()[new Random().nextInt(Participation.values().length)];
    combat = Combat.values()[new Random().nextInt(Combat.values().length)];
    survival = Survival.values()[new Random().nextInt(Survival.values().length)];

    Location origin = new Location(WorldUtils.hub(), 999981.5, 127, 1000004.5);
    List<ArmorStand> holos = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      if (i != 0) origin.subtract(0, 0, 4);
      holos.addAll(origin.getNearbyEntitiesByType(ArmorStand.class, 0.5));
    }
    if (holos.size() >= 3) {
      holos.get(0).customName(Component.text(participation.get()).color(Color.YELLOW));
      holos.get(1).customName(Component.text(combat.get()).color(Color.RED));
      holos.get(2).customName(Component.text(survival.get()).color(Color.GREEN));
    }

    scheduler.repeat("UPDATE", this::update, 0, (10 * 60) * 20);
  }

  private void update() {
    players.add(clamp(participation.range(), getObjective(participation.scoreboard())));
    players.add(clamp(combat.range(), getObjective(combat.scoreboard())));
    players.add(clamp(survival.range(), getObjective(survival.scoreboard())));

    for (NPC npc : npcs) {
      npc.destroy();
    }

    npcs.add(NPC.create(new NPCLeaderboard(players.get(0), 0)));
    npcs.add(NPC.create(new NPCLeaderboard(players.get(1), 1)));
    npcs.add(NPC.create(new NPCLeaderboard(players.get(2), 2)));
  }

  private OfflinePlayer clamp(Range range, Objective obj) {
    Map<OfflinePlayer, Integer> scores = new HashMap<>();
    for (OfflinePlayer p : getServer().getOfflinePlayers()) {
      scores.put(p, obj.getScore(p).getScore());
    }
    List<Map.Entry<OfflinePlayer, Integer>> scoresList = new LinkedList<>(scores.entrySet());
    scoresList.sort(Map.Entry.comparingByValue());

    return range == Range.MIN ? scoresList.get(0).getKey() : scoresList.get(scoresList.size() - 1).getKey();
  }

  public enum Participation implements Criteria {
    MOST_DEATHS("Player with the most deaths", "Deaths", Range.MAX);

    private final String name;
    private final String scoreboard;
    private final Range range;

    Participation(String name, String scoreboard, Range range) {

      this.name = name;
      this.scoreboard = scoreboard;
      this.range = range;
    }

    @Override
    public String get() {
      return name;
    }

    @Override
    public String scoreboard() {
      return scoreboard;
    }

    @Override
    public Range range() {
      return range;
    }
  }

  public enum Combat implements Criteria {
    PLAYER_KILLS("Player with the most Player kills", "playerKills", Range.MAX),
    MOB_KILLS("Player with the most kills", "Kills", Range.MAX);

    private final String name;
    private final String scoreboard;
    private final Range range;

    Combat(String name, String scoreboard, Range range) {

      this.name = name;
      this.scoreboard = scoreboard;
      this.range = range;
    }

    @Override
    public String get() {
      return name;
    }

    @Override
    public String scoreboard() {
      return scoreboard;
    }

    @Override
    public Range range() {
      return range;
    }
  }

  public enum Survival implements Criteria {
    LEAST_DEATHS("Player with the least deaths", "Deaths", Range.MIN),
    LEVELS("Player with the most levels", "Levels", Range.MAX);

    private final String name;
    private final String scoreboard;
    private final Range range;

    Survival(String name, String scoreboard, Range range) {

      this.name = name;
      this.scoreboard = scoreboard;
      this.range = range;
    }

    @Override
    public String get() {
      return name;
    }

    @Override
    public String scoreboard() {
      return scoreboard;
    }

    @Override
    public Range range() {
      return range;
    }
  }

  private enum Range {
    MAX,
    MIN
  }

  public interface Criteria {
    String get();

    String scoreboard();

    Range range();
  }

}
