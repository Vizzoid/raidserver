package org.vizzoid.raidserver.raidserver.minecraft.admin.armor;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.ArmorUtils;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.armors.Armor;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorPiece;
import org.vizzoid.raidserver.raidserver.minecraft.progression.armor.enums.ArmorSet;
import org.vizzoid.raidserver.raidserver.minecraft.utils.minecraft.MinecraftCommand;

public class ArmorCommand extends MinecraftCommand {
  @Override
  public String name() {
    return "armor";
  }

  @Override
  public String description() {
    return "Give a set of armor";
  }

  @Override
  public String usage() {
    return "/armor <name>";
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender instanceof Player p && p.getGameMode() == GameMode.CREATIVE && args.length > 0) {
      try {
        ArmorSet set = ArmorSet.valueOf(args[0]);
        Armor a = set.init();
        p.getInventory().addItem(
          ArmorUtils.item(ArmorPiece.BOOTS, a),
          ArmorUtils.item(ArmorPiece.LEGGINGS, a),
          ArmorUtils.item(ArmorPiece.CHESTPLATE, a),
          ArmorUtils.item(ArmorPiece.HELMET, a));
      } catch (IllegalArgumentException ignored) {
        p.sendMessage(Component.text("Not a real armor!"));
      }
    }
    return true;
  }

  @Override
  public TabCompleter tabCompleter() {
    return new ArmorCommandTab();
  }
}
