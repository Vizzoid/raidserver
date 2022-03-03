package org.vizzoid.raidserver.raidserver.minecraft.utils.objects;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;

@SuppressWarnings("UnusedReturnValue")
public record Data(@NotNull PersistentDataHolder holder) {

  public static final String REGEX = ", ";
  public static final String SUB_REGEX = " : ";
  private static final PersistentDataType<String, String> STRING = PersistentDataType.STRING;

  public Data set(Key key, String input) {
    holder.getPersistentDataContainer().set(key.to(), STRING, input);
    return this;
  }

  public Data set(Key key, int input) {
    holder.getPersistentDataContainer().set(key.to(), STRING, String.valueOf(input));
    return this;
  }

  public Data set(Key key, boolean is) {
    set(key, is ? "true" : "false");
    return this;
  }

  public Data add(Key key, String input) {
    String s = has(key) ? get(key) + REGEX + input : input;
    set(key, s);
    return this;
  }

  public Data subtract(Key key, String input) {
    String s = contains(key, REGEX + input) ? get(key).replace(REGEX + input, "") : get(key).replace(input + REGEX, "");
    set(key, s);
    return this;
  }

  @CheckReturnValue
  public boolean contains(Key key, String input) {
    return get(key).contains(input);
  }

  public String[] split(Key key) {
    if (get(key) != null) {
      return get(key).split(REGEX);
    }
    return null;
  }

  @CheckReturnValue
  public boolean has(Key key) {
    return holder.getPersistentDataContainer().has(key.to());
  }

  @CheckReturnValue
  public String get(Key key) {
    return holder.getPersistentDataContainer().get(key.to(), STRING);
  }

  @CheckReturnValue
  public boolean getBool(Key key) {
    return get(key).equals("true");
  }

  @CheckReturnValue
  public int getInt(Key key) {
    return Integer.parseInt(get(key));
  }

  // Revamping: Replace has checks followed by sets with this
  public String getCheck(Key key) {
    if (has(key)) {
      return get(key);
    } else {
      return null;
    }
  }

  /**
   * Removes the key regardless
   */
  public Data remove(Key key) {
    holder.getPersistentDataContainer().remove(key.to());
    return this;
  }

  /**
   * Removes key only if the key's value is equal to the input
   */
  public Data remove(Key key, String value) {
    if (has(key) && get(key).equals(value)) {
      remove(key);
    }
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (NamespacedKey key : holder.getPersistentDataContainer().getKeys()) {
      builder.append(key).append(REGEX);
    }
    return builder.toString();
  }
}
