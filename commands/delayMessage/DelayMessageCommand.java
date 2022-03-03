package org.vizzoid.raidserver.raidserver.minecraft.commands.delayMessage;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.minecraft.utils.Comp;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.Deprecation;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;

import java.util.ArrayList;
import java.util.List;

public class DelayMessageCommand extends MinecraftCommand {
  private static void error(Audience p, String err) {
    p.sendMessage(Component.text(err).color(Color.RED));
    Sound.failure(p);
  }

  private static Component msg(CommandSender sender, String msg) {
    return Component.text(sender.getName() + " whispers to you: " + msg).decoration(TextDecoration.ITALIC, true).color(Color.GRAY);
  }

  @Override
  public String name() {
    return "dm";
  }

  @Override
  public List<String> aliases(String... toAdd) {
    return super.aliases("direct", "delay");
  }

  @Override
  public String usage() {
    return "/dm <player> <message>";
  }

  @Override
  public String description() {
    return "Message an offline player, which will show them on join";
  }

  @Override
  public TabCompleter tabCompleter() {
    return new DelayMessageCommandTab();
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (args.length >= 2) {
      OfflinePlayer receiver = Deprecation.getOfflinePlayer(args[0]);

      List<String> args1 = new ArrayList<>(List.of(args));
      args1.remove(0);
      if (receiver.hasPlayedBefore()) {
        Comp.queue(receiver, msg(sender, String.join(" ", args1)));
      } else error(sender, "Receiver is not valid player!");
    } else error(sender, "Command requires a player and a message!");
    return true;
  }
}
