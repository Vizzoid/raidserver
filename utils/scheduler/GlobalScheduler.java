package org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler;

import java.util.HashMap;
import java.util.Map;

public class GlobalScheduler extends Scheduler {

  private final Map<Scheduler, TaskManager> schedulers = new HashMap<>();

  protected GlobalScheduler() {
    super(true);
    global = this;
  }

  public void add(Scheduler s, TaskManager t) {
    schedulers.put(s, t);
  }

  // Probably not needed -- even for disposing
  public void remove(Scheduler s) {
    schedulers.remove(s);
    s.cancelAll();
  }

  public Map<Scheduler, TaskManager> get() {
    return schedulers;
  }

  public Map<Integer, TaskData> getTasks() {
    return manager.getAll();
  }

  @Override
  public void add(int id, String name, TaskType type) {
    manager.add(id, name, type);
  }

  @Override
  public void remove(int id) {
    manager.remove(id);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("Global: \n     ");
    get().forEach((s, t) -> builder.append(s.toString()));
    return builder.toString();
  }
}
