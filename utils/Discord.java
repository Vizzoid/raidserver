package org.vizzoid.raidserver.raidserver.minecraft.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;

import java.util.List;

public class Discord extends Utility {

  /**
   * @deprecated Resource demanding and not necessary while Member Chunking is active,
   * but can be replaced once chunking becomes more resource-intensive
   */
  @Deprecated
  public static List<Member> findByTag(String tag) {
    return getGuild().findMembers(m -> m.getUser().getAsTag().equals(tag)).get();
  }

  /**
   * @deprecated Resource demanding and not necessary while Member Chunking is active,
   * but can be replaced once chunking becomes more resource-intensive
   */
  @Deprecated
  public static List<Member> findById(String id) {
    return getGuild().findMembers(m -> m.getId().equals(id)).get();
  }

  /**
   * @deprecated Resource demanding and not necessary while Member Chunking is active,
   * but can be replaced once chunking becomes more resource-intensive
   */
  @Deprecated
  public static Member findFirstByTag(String tag) {
    List<Member> found = findByTag(tag);
    return found.size() > 0 ? found.get(0) : null;
  }

  /**
   * @deprecated Resource demanding and not necessary while Member Chunking is active,
   * but can be replaced once chunking becomes more resource-intensive
   */
  @Deprecated
  public static Member findFirst(String id) {
    List<Member> found = findById(id);
    return found.size() > 0 ? found.get(0) : null;
  }

  public static Member getByTag(String tag) {
    return getGuild().getMemberByTag(tag);
  }

  public static Member getById(String id) {
    return getGuild().getMemberById(id);
  }

  public static class Minecraft {

    // ToDo fix this up to not rely on name
    public static OfflinePlayer getPlayer(@Nullable User user) {
      return getName(user) != null ? Bukkit.getPlayerExact(getName(user)) : null;
    }

    public static String getName(@Nullable User user) {
      if (user != null && getConfig().has("discord." + user.getId())) {
        return getConfig().getString("discord." + user.getId());
      }
      return null;
    }

    public static OfflinePlayer getPlayer(@Nullable Member member) {
      if (member != null) {
        return getPlayer(member.getUser());
      }
      return null;
    }

    public static String getName(@Nullable Member member) {
      if (member != null) {
        return getName(member.getUser());
      }
      return null;
    }

    public static String getNameById(String id) {
      return getName(Discord.getById(id));
    }

  }


}
