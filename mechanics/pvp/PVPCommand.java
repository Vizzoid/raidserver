package org.vizzoid.raidserver.raidserver.minecraft.mechanics.pvp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;

public class PVPCommand extends MinecraftCommand {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (args.length >= 1) {
      boolean pvp = get(args[0]);

      if (args.length >= 2 && sender.isOp()) {
        Player p = Bukkit.getPlayerExact(args[1]);
        if (p != null) {
          PVP.getManager(p).setPVPAndAlert(pvp);
          PVP.sendUpdate(sender, pvp);
        }
      } else if (sender instanceof Player p) {
        PVP.getManager(p).setPVPAndAlert(pvp);
      }
    } else if (sender instanceof Player p) {
      PVP.getManager(p).switchPVP();
    }
    return true;
  }

  private boolean get(String io) {
    return io.equalsIgnoreCase("on");
  }

  @Override
  public String name() {
    return "pvp";
  }

  @Override
  public String description() {
    return "Disables or enables PVP";
  }

  @Override
  public String usage() {
    return "/pvp [On/Off]";
  }

  @Override
  public TabCompleter tabCompleter() {
    return new PVPCommandTab();
  }
}
