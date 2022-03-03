package org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.events;

import org.bukkit.SoundCategory;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.Sound;
import org.vizzoid.raidserver.raidserver.minecraft.utils.aesthetics.sound.objects.SoundObject;

public class SoundEvent extends Event implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  protected final SoundObject object;
  private boolean cancel = false;

  public SoundEvent(SoundObject object, boolean async) {
    // Declaring Async is important. If it is run opposite to what is declared an error will be thrown
    // Remind me to make an async copy
    super(async);
    this.object = object;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }

  public Sound.Type getType() {
    return object.type();
  }

  public float getVolume() {
    return object.volume();
  }

  public float getPitch() {
    return object.pitch();
  }

  public SoundCategory getCategory() {
    return object.category();
  }

  public Sound.Source getSource() {
    return object.source();
  }

  @Override
  public boolean isCancelled() {
    return cancel;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancel = cancel;
  }

  @Override
  public String toString() {
    return this.getEventName();
  }
}
