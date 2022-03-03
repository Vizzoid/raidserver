package org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.SoundStop;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.events.GlobalSoundEvent;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.events.LocationSoundEvent;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.events.PlayerSoundEvent;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects.GlobalObject;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects.LocationObject;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects.PlayerObject;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects.SoundObject;

import java.util.Collection;

/**
 * Plays sound and acts sound classes
 * Sound.Async# is REQUIRED FOR ASYNC OPERATIONS
 * IllegalStateException will throw. DON'T LET IT HAPPEN.
 */
@SuppressWarnings({"PatternValidation", "unused"})
public class Sound extends Utility {

  public static final SoundCategory GLOBAL = SoundCategory.MASTER;
  public static final SoundCategory MUSIC = SoundCategory.MUSIC;

  public static SoundCategory category(Type t) {
    return t instanceof Effect ? GLOBAL : MUSIC;
  }

  public static void play(Type t, Audience p) {
    play(t, p, 1);
  }

  public static void play(Type t, Audience p, float volume) {
    play(t, p, volume, 1);
  }

  public static void play(Type t, Audience p, float volume, float pitch) {
    play(t, p, volume, pitch, category(t));
  }

  public static void play(Type t, Audience p, float volume, float pitch, SoundCategory source) {
    play(new PlayerObject(t, p, volume, pitch, source), true);
  }

  private static void play(SoundObject object, boolean fireEvent) {
    play(object, fireEvent, false);
  }

  private static void play(SoundObject object, boolean fireEvent, boolean async) {
    if (fireEvent) {
      if (new PlayerSoundEvent((PlayerObject) object, async).callEvent()) {
        object.audience().playSound(net.kyori.adventure.sound.Sound.sound(Key.key(object.type().get()),
          object.category(), object.volume(), object.pitch()), net.kyori.adventure.sound.Sound.Emitter.self());
      }
    } else {
      object.audience().playSound(net.kyori.adventure.sound.Sound.sound(Key.key(object.type().get()),
        object.category(), object.volume(), object.pitch()), net.kyori.adventure.sound.Sound.Emitter.self());
    }
  }

  public static void stop(Type t, Audience p) {
    p.stopSound(SoundStop.named(Key.key(t.get())));
  }

  public static void stopAll(Player p) {
    p.stopAllSounds();
  }

  public static void location(Type t, Location l) {
    location(t, l, 1);
  }

  public static void location(Type t, Location l, float volume) {
    location(t, l, volume, 1);
  }

  public static void location(Type t, Location l, float volume, float pitch) {
    location(t, l, volume, pitch, category(t));
  }

  public static void location(Type t, Location l, float volume, float pitch, SoundCategory source) {
    location(new LocationObject(t, l, volume, pitch, source));
  }

  private static void location(LocationObject object) {
    location(object, false);
  }

  private static void location(LocationObject object, boolean async) {
    if (new LocationSoundEvent(object, async).callEvent()) {
      object.location().getWorld().playSound(
        object.location(), object.type().get(), object.category(), object.volume(), object.pitch());
    }
  }

  public static void global(int radius, Type t, Location l) {
    global(radius, t, l, 1);
  }

  public static void global(int radius, Type t, Location l, float volume) {
    global(radius, t, l, volume, 1);
  }

  public static void global(int radius, Type t, Location l, float volume, float pitch) {
    global(radius, t, l, volume, pitch, category(t));
  }

  private static void global(int radius, Type t, Location l, float volume, float pitch, SoundCategory source) {
    global(radius, t, l, volume, pitch, source, false);
  }

  private static void global(int radius, Type t, Location l, float volume, float pitch, SoundCategory source, boolean async) {
    Collection<? extends Audience> nearby = l.getNearbyPlayers(radius);
    GlobalObject global = new GlobalObject(t, nearby, volume, pitch, source, l, radius);
    if (new GlobalSoundEvent(global, async).callEvent()) {
      nearby.forEach(p -> play(global, false));
    }
  }

  public static void success(Audience player) {
    play(Effect.XP_ORB, player, 1, 0.5f);
  }

  public static void failure(Audience player) {
    play(Effect.XP_ORB, player, 1);
  }

  public enum Effect implements Type {
    XP_ORB("minecraft:entity.experience_orb.pickup"),
    XP_BOTTLE_THROW("minecraft:entity.experience_bottle.throw"),
    ANVIL_PLACE("minecraft:block.anvil.place"),
    ENDERMAN_HURT("minecraft:entity.enderman.hurt"),
    ENDERDRAGON_GROWL("minecraft:entity.ender_dragon.growl"),
    IRON_GOLEM_DEATH("minecraft:entity.iron_golem.death"),
    ARROW_SHOOT("minecraft:entity.arrow.shoot"),
    CROSSBOW_FINISH_LOAD("minecraft:item.crossbow.loading_end"),
    ANVIL_USE("minecraft:block.anvil.use"),
    BREW("minecraft:block.brewing_stand.brew"),
    ENDERDRAGON_FLAP("minecraft:entity.ender_dragon.flap"),
    ENDERDRAGON_SHOOT("minecraft:entity.ender_dragon.shoot"),
    ENDERMAN_TELEPORT("minecraft:entity.enderman.teleport"),
    TNT("minecraft:entity.generic.explode"),
    TNT_PRIME("minecraft:entity.tnt.primed"),
    GRASS_BREAK("minecraft:block.grass.break"),
    GRASS_DIG("minecraft:block.grass.hit"),
    SKELETON_DEATH("minecraft:entity.skeleton.death"),
    ZOMBIE_VILLAGER_CURE("entity.zombie_villager.cure"),
    SHIELD_BLOCK("minecraft:item.shield.block"),
    IRON_GOLEM_BREAK("minecraft:entity.iron_golem.damage"),
    GHAST_HURT("minecraft:entity.ghast.hurt");

    private final String s;

    Effect(String s) {
      this.s = s;
    }

    public String get() {
      return s;
    }
  }

  public enum Music implements Type {
    WARD("minecraft:music_disc.ward", 4, 16),
    PIGSTEP("minecraft:music_disc.pigstep", 2, 25),
    ELEVEN("minecraft:music_disc.11", 1, 11),
    THIRTEEN("minecraft:music_disc.13", 2, 57),
    BLOCKS("minecraft:music_disc.blocks", 5, 51),
    CAT("minecraft:music_disc.cat", 3, 7),
    CHIRP("minecraft:music_disc.chirp", 3, 7),
    FAR("minecraft:music_disc.far", 3, 12),
    MALL("minecraft:music_disc.mall", 3, 24),
    MELLOHI("minecraft:music_disc.mellohi", 1, 38),
    STAL("minecraft:music_disc.stal", 2, 32),
    STRAD("minecraft:music_disc.strad", 3, 10),
    WAIT("minecraft:music_disc.wait", 3, 54),

    // These have differing lengths, it's not recommended looping these
    CREATIVE("minecraft:music.creative", 10, 1),
    CREDITS("minecraft:music.credits", 10, 1),
    DRAGON("minecraft:music.dragon", 10, 1),
    END("minecraft:music.end", 10, 1),
    GAME("minecraft:music.game", 10, 1),
    MENU("minecraft:music.menu", 10, 1),
    BASALT_DELTAS("minecraft:music.nether.basalt_deltas", 10, 1),
    CRIMSON_FOREST("minecraft:music.nether.crimson_forest", 10, 1),
    NETHER_WASTES("minecraft:music.nether.nether_wastes", 5, 14),
    SOUL_SAND_VALLEY("minecraft:music.nether.soul_sand_valley", 10, 1),
    NETHER_WARPED_FOREST("minecraft:music.nether.warped_forest", 10, 1),
    UNDERWATER("minecraft:music.under_water", 10, 1);

    private final String s;
    private final long t;

    Music(String s, long min, long sec) {
      this.s = s;
      this.t = (min * 60) + sec;
    }

    public String get() {
      return s;
    }

    public long length() {
      return t * 20;
    }
  }

  public enum Source {
    GLOBAL,
    PLAYER,
    LOCATION
  }

  public interface Type {
    String get();
  }

  public static class Async {
    public static void play(Type t, Audience p) {
      play(t, p, 1);
    }

    public static void play(Type t, Audience p, float volume) {
      play(t, p, volume, 1);
    }

    public static void play(Type t, Audience p, float volume, float pitch) {
      play(t, p, volume, pitch, category(t));
    }

    public static void play(Type t, Audience p, float volume, float pitch, SoundCategory source) {
      play(new PlayerObject(t, p, volume, pitch, source));
    }

    private static void play(SoundObject object) {
      Sound.play(object, true, true);
    }

    public static void stop(Type t, Audience p) {
      p.stopSound(SoundStop.named(Key.key(t.get())));
    }

    public static void stopAll(Player p) {
      p.stopAllSounds();
    }

    public static void location(Type t, Location l) {
      location(t, l, 1);
    }

    public static void location(Type t, Location l, float volume) {
      location(t, l, volume, 1);
    }

    public static void location(Type t, Location l, float volume, float pitch) {
      location(t, l, volume, pitch, category(t));
    }

    public static void location(Type t, Location l, float volume, float pitch, SoundCategory source) {
      location(new LocationObject(t, l, volume, pitch, source));
    }

    private static void location(LocationObject object) {
      Sound.location(object, true);
    }

  }

}
