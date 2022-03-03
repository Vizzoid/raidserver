package org.vizzoid.raidserver.raidserver.minecraft.utils.objects;

import org.bukkit.NamespacedKey;
import org.vizzoid.raidserver.raidserver.main.meta.PluginHolder;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.util.Objects;

public class Key extends PluginHolder {

  private NamespacedKey namespacedKey;

  public Key(String key) {
    if (getPlugin() != null) {
      namespacedKey = new NamespacedKey(getPlugin(), key);
    } else {
      Scheduler scheduler = new Scheduler();
      scheduler.await("KEY_BUILD", Objects::nonNull, PluginHolder::getPlugin, () -> {
        if (getPlugin() != null) {
          namespacedKey = new NamespacedKey(getPlugin(), key);
          scheduler.cancel();
        }
      });
    }
  }

  private Key(NamespacedKey key) {
    namespacedKey = key;
  }

  public static Key from(NamespacedKey key) {
    return new Key(key);
  }

  public static Key minecraft(String key) {
    return new Key(NamespacedKey.minecraft(key));
  }

  public NamespacedKey to() {
    return namespacedKey;
  }

  @Override
  public String toString() {
    return "Key: " + namespacedKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Key key = (Key) o;

    return namespacedKey.equals(key.namespacedKey);
  }

  @Override
  public int hashCode() {
    return namespacedKey.hashCode();
  }
}
