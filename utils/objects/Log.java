package org.vizzoid.raidserver.raidserver.minecraft.utils.objects;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Log extends LogRecord {
  /**
   * A simple reconstruction
   */
  public Log(Level level, String msg) {
    super(level, msg);
    this.setLoggerName("RaidServer");
  }

  public Log replace1(String newChar) {
    return replace("%s", newChar);
  }

  public Log replace(String oldChar, String newChar) {
    setMessage(getMessage().replace(oldChar, newChar));
    return this;
  }
}
