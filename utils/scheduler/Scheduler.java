package org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler;

import org.bukkit.Bukkit;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Allows scheduled events
 * Initialize this on initializing of using class (preferably)
 * Instance instead of utility for storing tasks
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Scheduler extends Utility {

  /**
   * Global is for global use -- plugin tasks,
   * It is also a tool to cancel a task from anywhere and is a proxy for debug
   */
  protected static GlobalScheduler global = null;
  protected TaskManager manager;

  public Scheduler() {
    manager = new TaskManager();
    global().add(this, manager);
  }

  protected Scheduler(boolean isGlobal) {
    manager = new TaskManager();
    if (!isGlobal) {
      global().add(this, manager);
    }
  }

  public static GlobalScheduler global() {
    if (global == null) global = new GlobalScheduler();
    return global;
  }

  public void add(int id, String name, TaskType type) {
    manager.add(id, name, type);
    global().add(id, name, type);
  }

  public void remove(int id) {
    manager.remove(id);
    global().remove(id);
  }

  // Contemplating remaking this...
  // In most cases delay shouldn't be logged as it isn't cancelled
  // But whatever I guess
  public int delay(String name, Runnable task, int delay) {
    int id = schedule(task, delay);
    add(id, name, TaskType.DELAY);

    schedule(() -> cancel(id, false), delay);
    return id;
  }

  public int repeat(String name, Runnable task, int delay, int repeat) {
    int id = schedule(task, delay, repeat);
    add(id, name, TaskType.REPEAT);
    return id;
  }

  private int schedule(Runnable task, int delay) {
    return Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), task, delay);
  }

  private int schedule(Runnable task, int delay, int repeat) {
    return Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), task, delay, repeat);
  }

  public boolean isScheduled(String name) {
    List<Integer> ids = manager.getByName(name);
    return !ids.isEmpty();
  }

  public boolean isScheduled(int id) {
    return manager.hasKey(id);
  }

  /**
   * A Supplier is used because variables can change as the scheduler runs
   */
  public <T> void await(String name, Predicate<? super T> filter, Supplier<T> test, Runnable onSuccess) {
    // New scheduler to avoid cancelling these tasks
    await(name, filter, test, onSuccess, new Log(Level.WARNING,
      "The task \"" + name + "\" has been awaiting for a long time! If warnings repeat, restart the server!"));
  }

  /**
   * A Supplier is used because variables can change as the scheduler runs
   */
  public <T> void await(String name, Predicate<? super T> filter, Supplier<T> test, Runnable onSuccess, Log warn) {
    String await = "AWAITING_" + name;
    String check = "ON_STILL_AWAITING_" + name;

    final int[] i = {0};
    // New scheduler to avoid cancelling these tasks
    Scheduler scheduler = new Scheduler();
    scheduler.repeat(await, () -> {
      if (filter.test(test.get())) {
        onSuccess.run();

        scheduler.cancel(await);
        scheduler.cancel(check);
      }
    }, 0, 10);
    scheduler.repeat(check, () -> {
      if (scheduler.isScheduled(await)) {
        ++i[0];
        if (i[0] >= 5) {
          getLogger().log(new Log(Level.SEVERE, "Task \"" + name + "\" failed to succeed! It's recommended that you restart"));

          scheduler.cancel(await);
          scheduler.cancel(check);
        } else getLogger().log(warn);
      }
    }, 10 * 20, 10 * 20);
  }

  /**
   * Cancels the newest running task
   *
   * @return true or false if task was cancelled
   */
  public boolean cancel() {
    int current = manager.current();
    manager.currentTask = 0;
    return cancel(current);
  }

  /**
   * Cancels all tasks with this name
   */
  public boolean cancel(String name) {
    List<Integer> ids = manager.getByName(name);
    ids.forEach(this::cancel);
    return !ids.isEmpty();
  }

  /**
   * Cancels and removes the task with this id
   */
  public boolean cancel(int id) {
    return cancel(id, true);
  }

  /**
   * Removes and can cancel task with this id
   */
  private boolean cancel(int id, boolean cancel) {
    if (manager.hasKey(id)) {
      if (cancel) {
        Bukkit.getScheduler().cancelTask(id);
      }
      remove(id);
      return true;
    }
    return false;
  }

  /**
   * Cancels all tasks with this name at a delay
   */
  // inlined delay functions meant for cancelling
  // boolean impossible on schedule
  public void cancelDelay(String name, int delay) {
    schedule(() -> cancel(name), delay);
  }

  /**
   * Cancel the task with this id at a delay
   */
  public void cancelDelay(int id, int delay) {
    schedule(() -> cancel(id), delay);
  }

  /**
   * Cancels all tasks with this name at a delay, running additional code with it
   */
  public void cancelDelay(String name, Runnable additional, int delay) {
    schedule(() -> cancel(name), delay);
    schedule(additional, delay);
  }

  /**
   * Cancel the task with this id at a delay, running additional code with it
   */
  public void cancelDelay(int id, Runnable additional, int delay) {
    schedule(() -> cancel(id), delay);
    schedule(additional, delay);
  }

  /**
   * Cancels all tasks
   */
  public void cancelAll() {
    manager.getAll().forEach((k, v) -> cancel(k));
  }

  /**
   * Runs repeating task and setups cancel for task
   */
  public void cancelFuture(String name, Runnable task, int delay, int repeat, int cancelDelay) {
    int id = repeat(name, task, delay, repeat);
    cancelDelay(id, cancelDelay);
  }

  @Override
  public String toString() {
    return "Scheduler: \n     " + manager.toString();
  }

  public enum TaskType {
    REPEAT,
    DELAY
  }

  public static class TaskManager {

    // integer of task id, string of name, type of task
    private final Map<Integer, TaskData> runningTasks = new HashMap<>();
    private int currentTask;

    public void current(int id) {
      currentTask = id;
    }

    public int current() {
      return currentTask;
    }

    public void add(int id, String name, TaskType type) {
      current(id);
      runningTasks.put(id, new TaskData(name, type));
    }

    public boolean hasKey(int id) {
      return runningTasks.containsKey(id);
    }

    /**
     * List is used instead of single element to count for duplicates
     *
     * @return list of ids with name
     */
    public List<Integer> getByName(String name) {
      List<Integer> ids = new ArrayList<>();
      runningTasks.forEach((k, v) -> {
        if (v.name().equals(name)) {
          ids.add(k);
        }
      });

      return ids;
    }

    public Map<Integer, TaskData> getAll() {
      return new HashMap<>(runningTasks);
    }

    public void remove(int id) {
      runningTasks.remove(id);
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder("TaskManager: ");
      getAll().forEach((k, v) -> builder.append("\n     Name: ")
        .append(v.name()).append(", Type: ")
        .append(v.type()).append(", ID: ")
        .append(k));
      return builder.toString();
    }
  }

  // Maybe implement the other methods(?) Not this specifically because this has no delay so it can't be logged
  public static class Async {

    public static void run(Runnable task) {
      Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), task);
    }

    public static void run(Runnable task, int delay) {
      Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), task, delay);
    }

  }

  public static class Sync {

    public static void run(Runnable task, int delay) {
      Bukkit.getScheduler().runTaskLater(getPlugin(), task, delay);
    }

    public static void run(Runnable task) {
      Bukkit.getScheduler().runTask(getPlugin(), task);
    }

  }

  public record TaskData(String name, Scheduler.TaskType type) {
  }

}
