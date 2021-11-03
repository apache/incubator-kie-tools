/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task scheduler component.
 * <p>It uses internally an instance of java.util.concurrent.ScheduledThreadPoolExecutor
 * which provides a thread pool and the delayed task execution capability.</p>
 */
public class Scheduler {

    private static final String TASK = "Task ";
    private Logger log = LoggerFactory.getLogger(Scheduler.class);

    protected PausableThreadPoolExecutor executor;
    protected ThreadFactory threadFactory;
    protected Map<String,SchedulerTask> scheduledTasks = Collections.synchronizedMap(new HashMap<String,SchedulerTask>());

    protected int maxThreadPoolSize;

    public void init(int maxThreadPoolSize) {
        this.maxThreadPoolSize = maxThreadPoolSize;
        this.threadFactory = new SchedulerThreadFactory();
        this.executor = new PausableThreadPoolExecutor(maxThreadPoolSize, threadFactory);
        this.executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        this.executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        log.debug("Scheduler started [pool size=" + maxThreadPoolSize + "].");
    }

    public void shutdown() {
        log.debug("Scheduler shutdown started.");
        executor.shutdown();
        log.debug("Scheduler shutdown completed.");
    }

    public int getMaxThreadPoolSize() {
        return maxThreadPoolSize;
    }

    public void setMaxThreadPoolSize(int maxThreadPoolSize) {
        this.maxThreadPoolSize = maxThreadPoolSize;
        if (executor != null) executor.setCorePoolSize(maxThreadPoolSize);
    }

    public int getThreadPoolSize() {
        return executor.getPoolSize();
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public int getNumberOfScheduledTasks() {
        return scheduledTasks.size();
    }

    public int getNumberOfScheduledTasksInQueue() {
        return executor.getQueue().size();
    }

    public SchedulerTask getTaskByKey(String key) {
        for (SchedulerTask task : scheduledTasks.values()) {
            if (task.getKey().equals(key)) {
                return task;
            }
        }
        return null;
    }

    public List<SchedulerTask> getScheduledTasks() {
        return new ArrayList<>(scheduledTasks.values());
    }

    public List<SchedulerTask> getRunningTasks() {
        List<SchedulerTask> result = new ArrayList<>();
        for (SchedulerTask task : scheduledTasks.values()) {
            if (task.isRunning()) result.add(task);
        }
        return result;
    }

    public List<SchedulerTask> getMisfiredTasks() {
        List<SchedulerTask> result = new ArrayList<>();
        for (SchedulerTask task : scheduledTasks.values()) {
            if (task.isMisfired()) result.add(task);
        }
        return result;
    }

    public List<SchedulerTask> getWaitingTasks() {
        List<SchedulerTask> result = new ArrayList<>();
        for (SchedulerTask task : scheduledTasks.values()) {
            if (!task.isDone() && !task.isRunning() && !task.isMisfired()) result.add(task);
        }
        return result;
    }

    // Transactional operations

    public void pause() {
        executor.pause();
    }

    public void resume() {
        executor.resume();
    }

    public boolean isPaused() {
        return executor.isPaused();
    }

    public synchronized void execute(SchedulerTask task) {
        try {
            _schedule(task, null);
        } catch (Exception e) {
            log.error("Execute call failed for task: " + task.getKey(), e);
        }
    }

    public synchronized void schedule(SchedulerTask task, Date date) {
        try {
            _schedule(task, date);
        } catch (Exception e) {
            log.error("Schedule call failed for task: " + task.getKey(), e);
        }
    }

    public synchronized void schedule(SchedulerTask task, long seconds) {
        try {
            _schedule(task, seconds);
        } catch (Exception e) {
            log.error("Schedule call failed for task: " + task.getKey(), e);
        }
    }

    public synchronized void unschedule(final String key) {
        try {
            _unschedule(key);
        } catch (Exception e) {
            log.error("Unschedule call failed for task: " + key, e);
        }
    }

    public synchronized void unscheduleAll() {
        try {
            _unscheduleAll();
        } catch (Exception e) {
            log.error("Unschedule all call failed.", e);
        }
    }

    public synchronized void fireTask(String key) {
        SchedulerTask task = scheduledTasks.get(key);
        if (task != null && !task.isDone() && !task.isRunning()) {
            try {
                task.run();
                log.debug(TASK + task + " executed.");
            } finally {
                scheduledTasks.remove(key);
                task.cancel();
                _purge();

                // Re-schedule fixed delay (repetitive) tasks after issue a fire.
                if (task.isFixedDelay()) {
                    schedule(task, task.getFixedDelaySeconds());
                }
            }
        }
    }

    // Scheduler business logic

    protected void _schedule(SchedulerTask task, Date date) {
        // Calculate the delay.
        // Never use a delay=0 in order to force the task to be launched in a separated thread.
        long delay = 10000;
        if (date != null) {
            Date now = new Date();
            delay = date.getTime() - now.getTime();
            if (delay <= 0) throw new IllegalArgumentException("Delay is negative. The task can not be scheduled [" + task.toString() + "] Date=" + date);
        }

        // Remove any old task (if any)
        _unschedule(task.getKey());

        // Register the new task.
        task.future = executor.schedule(task, delay, TimeUnit.MILLISECONDS);
        scheduledTasks.put(task.getKey(), task);
        if (date == null) log.debug(TASK + task + " execution requested.");
        else log.debug(TASK + task + " scheduled to: " + date);
    }

    protected void _schedule(SchedulerTask task, long seconds) {
        // Remove any old task (if any)
        _unschedule(task.getKey());

        // Register the new task.
        task.fixedDelay = true;
        task.fixedDelaySeconds = seconds;
        task.future = executor.scheduleWithFixedDelay(task, seconds, seconds, TimeUnit.SECONDS);
        scheduledTasks.put(task.getKey(), task);
        log.debug(TASK + task + " scheduled every " + seconds + " seconds.");
    }

    protected void _unschedule(String key) {
        SchedulerTask task = scheduledTasks.remove(key);
        if (task != null && !task.isDone() && !task.isRunning()) {
            task.cancel();
            _purge();
            log.debug(TASK + task + " unscheduled.");
        }
    }

    public void _unscheduleAll() {
        Collection<SchedulerTask> tasks = scheduledTasks.values();
        for (SchedulerTask task : tasks) {
            if (task != null && !task.isDone() && !task.isRunning()) {
                task.cancel();
            }
        }
        executor.purge();
        scheduledTasks.clear();
        log.debug("All tasks unscheduled.");
    }

    protected void _purge() {
        executor.purge();
        Iterator<SchedulerTask> it = scheduledTasks.values().iterator();
        while (it.hasNext()) {
            SchedulerTask task = it.next();
            if (task.isDone()) {
                it.remove();
                log.debug(TASK + task + " purged.");
            }
        }
    }

    public String printScheduledTasksReport() {
        Map<Object, SchedulerTask> temp = new HashMap<>(scheduledTasks);
        StringBuilder buf = new StringBuilder();
        buf.append("\n\n------------------ SCHEDULED TASKS=").append(temp.size())
                .append(" (Queue size=").append(executor.getQueue().size()).append(") -----------------------------\n");
        for (Map.Entry<Object, SchedulerTask> entry : temp.entrySet()) {
            SchedulerTask task = entry.getValue();
            buf.append("\n");
            if (task.isRunning()) buf.append("RUNNING - ");
            else if (task.isCancelled()) buf.append("CANCELL - ");
            else if (task.isDone()) buf.append("COMPLTD - ");
            else buf.append("WAITING - [Firing in ").append(task.printTimeToFire()).append("] - ");
            buf.append("[").append(task).append("]");
        }
        return buf.toString();
    }
}
