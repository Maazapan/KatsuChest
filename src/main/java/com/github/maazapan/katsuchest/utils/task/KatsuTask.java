package com.github.maazapan.katsuchest.utils.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class KatsuTask {

    protected BukkitTask task;
    protected final Plugin plugin;

    private final int period;
    private int currentCount = 0;

    public KatsuTask(Plugin plugin, int period) {
        this.plugin = plugin;
        this.period = period;
    }

    public abstract void run();

    public void start() {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::run, 0, period);
    }

    /**
     * Create a count task
     * increases the count by 1 every time.
     *
     * @param count Initial count
     */
    public void countTask(int count) {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (currentCount == count) {
                task.cancel();
                return;
            }
            run();
            currentCount++;
        }, 0, period);
    }

    /**
     * Create a countdown task.
     * decreases the count by 1 every time
     *
     * @param count Initial count
     */
    public void countDownTask(int count) {
        this.currentCount = count;

        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (currentCount < 0) {
                task.cancel();
                return;
            }
            run();
            currentCount--;
        }, 0, period);
    }

    public BukkitTask getTask() {
        return task;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void cancel() {
        task.cancel();
    }
}
