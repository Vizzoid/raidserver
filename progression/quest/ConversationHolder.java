package org.vizzoid.raidserver.raidserver.minecraft.progression.quest;

import java.util.ArrayList;
import java.util.List;

public interface ConversationHolder {

  /**
   * @return List of Lines of conversation
   */
  default List<Conversation.Line> list(Conversation.Line... lines) {
    return new ArrayList<>(List.of(lines));
  }

}
