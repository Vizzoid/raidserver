package org.vizzoid.raidserver.raidserver.minecraft.admin.whitelist;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.discord.MessageUtils;
import org.vizzoid.raidserver.raidserver.discord.command.meta.ButtonUtils;
import org.vizzoid.raidserver.raidserver.discord.command.meta.DiscordCommand;
import org.vizzoid.raidserver.raidserver.discord.gui.GuiUtils;

import java.util.List;

public class WhitelistDiscordCommand extends DiscordCommand {

  private Message message;
  private String name;

  @Override
  public String getDescription() {
    return "Type in your Minecraft name to whitelist yourself to the minecraft server!";
  }

  @Override
  public String getName() {
    return "whitelist";
  }

  @Override
  public CommandData getData() {
    return super.getData()
      .addOption(OptionType.STRING, "username", "Minecraft Username", true);
  }

  @Override
  public List<CommandPrivilege> getPrivileges() {
    List<CommandPrivilege> privileges = super.getPrivileges();
    // Disables @everyone
    privileges.add(CommandPrivilege.disable(getGuild().getPublicRole()));
    // Disables @Newcomers
    privileges.add(CommandPrivilege.disable(role("934263023940534372")));
    return privileges;
  }

  public Role role(String id, @NotNull Role substitute) {
    Role role = getGuild().getRoleById(id);
    return role != null ? role : substitute;
  }

  public Role role(String id) {
    return role(id, getGuild().getPublicRole());
  }

  @Override
  public void execute(SlashCommandEvent e) {
    e.deferReply(false).queue();

    name = e.getOptionsByName("username").get(0).getAsString();
    if (handle(e)) {
      if (!getWhitelist().containsUser(e.getUser())) {
        success(e);
      } else {
        e.getHook().sendMessageEmbeds(
            GuiUtils.embed("This will override your current whitelist: \"" + getWhitelist().getName(e.getUser()) + "\"",
              "Are you sure?"))
          .addActionRow(ButtonUtils.confirm(this))
          .setEphemeral(false).queue(
            m -> message = m
          );
      }
    }
  }

  private void success(Interaction e) {
    e.getHook().sendMessageEmbeds(GuiUtils.embed("\"" + name + "\",", "You've been whitelisted! Join via 'thebar.ddns.net'.")).queue();
    whitelist(e.getUser());
  }

  @Override
  public void executeButton(ButtonClickEvent e, int buttonNum) {
    e.deferReply(false).queue();
    switch (buttonNum) {
      case 0 -> success(e);
      case 1 -> message.delete().queue();
      default -> e.reply(MessageUtils.INTERNAL_ERROR).queue();
    }
  }

  private boolean handle(SlashCommandEvent e) {
    if (!name.contains(" ")) {
      if (!getWhitelist().containsName(name)) {
        return true;
      } else {
        e.getHook().sendMessageEmbeds(GuiUtils.embed("This person is already whitelisted!", "Please try a different username!")).queue();
      }
    } else {
      e.getHook().sendMessageEmbeds(
        GuiUtils.embed("\"" + name + "\" is not a valid username!",
          "Please try a different username!")).queue();
    }
    return false;
  }

  private void whitelist(User user) {
    getWhitelist().add(user.getId(), name);
  }

}
