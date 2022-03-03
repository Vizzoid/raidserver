package org.vizzoid.raidserver.raidserver.minecraft.progression.quest;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.vizzoid.raidserver.raidserver.main.meta.Color;
import org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities.NPCEntity;

import java.util.List;

public class Conversation {

  private final ConversationHolder holder;

  public Conversation(ConversationHolder holder) {
    this(holder, holder.list());
  }

  public Conversation(ConversationHolder holder, List<Line> lines) {
    Preconditions.checkArgument(!lines.isEmpty());
    // Non-AI NPC cannot be used for AI interactions
    Preconditions.checkArgument(!(holder instanceof NPCEntity));

  }

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public Line next() {
    return null;
  }

  public static class Line {

    private TextComponent component;

    private Line(String content, TextColor color, TextDecoration... decors) {
      component(Component.text(content, color));
      for (TextDecoration decor : decors) {
        component(component().decoration(decor, true));
      }
    }

    public static Line of(String content, TextColor color, TextDecoration... decors) {
      return new Line(content, color, decors);
    }

    public static Line of(String content) {
      return of(content, Color.WHITE);
    }

    public Line add(Line line) {
      return add(line.component());
    }

    public Line add(Component component) {
      return component(component().append(component));
    }

    private Line component(TextComponent component) {
      this.component = component;
      return this;
    }

    private TextComponent component() {
      return this.component;
    }

  }

}
