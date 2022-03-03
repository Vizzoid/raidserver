package org.vizzoid.raidserver.raidserver.minecraft.admin.whitelist;

import net.dv8tion.jda.api.entities.Member;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.utils.Discord;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;

public class WhitelistMinecraftCommand extends MinecraftCommand {
  @Override
  public String name() {
    return "whitelist";
  }

  @Override
  public String description() {
    return "Add someone to external whitelist";
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender.isOp() && args.length >= 2) {
      Member member = Discord.getByTag(args[0]);
      getWhitelist().add(member.getId(), args[1]);
    }
    return true;
  }
}
