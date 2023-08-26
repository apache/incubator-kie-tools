/*
 * Copyright © 2019 The GWT Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtproject.core.client.impl;

import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.DomGlobal.SetIntervalCallbackFn;
import elemental2.dom.DomGlobal.SetTimeoutCallbackFn;
import elemental2.promise.Promise;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Any;
import jsinterop.base.Js;
import org.gwtproject.core.client.Duration;
import org.gwtproject.core.client.Scheduler;

/** This is used by Scheduler to collaborate with Impl in order to have FinallyCommands executed. */
public class SchedulerImpl extends Scheduler {
    /**
     * Metadata bag for command objects. It's a JSO so that a lightweight JsArray can be used instead
     * of a Collections type.
     */
    @JsType(isNative = true, name = "Array", namespace = JsPackage.GLOBAL)
    static final class Task extends JsArray<Any> {
        @JsOverlay
        public static Task create(RepeatingCommand cmd) {
            return ((Task) new JsArray<Any>(Js.asAny(cmd), Js.asAny(true)));
        }

        @JsOverlay
        public static Task create(ScheduledCommand cmd) {
            return ((Task) new JsArray<Any>(Js.asAny(cmd), Js.asAny(false)));
        }

        @JsOverlay
        public boolean executeRepeating() {
            return getRepeating().execute();
        }

        @JsOverlay
        public void executeScheduled() {
            getScheduled().execute();
        }

        @JsOverlay
        public RepeatingCommand getRepeating() {
            return getAt(0).uncheckedCast();
        }

        @JsOverlay
        public ScheduledCommand getScheduled() {
            return getAt(0).uncheckedCast();
        }

        @JsOverlay
        public boolean isRepeating() {
            return getAt(1).asBoolean();
        }
    }

    /** Calls {@link org.gwtproject.core.client.impl.SchedulerImpl#flushPostEventPumpCommands()}. */
    private final class Flusher implements RepeatingCommand {
        public boolean execute() {
            flushRunning = true;
            flushPostEventPumpCommands();
            /*
             * No finally here, we want this to be clear only on a normal exit. An
             * abnormal exit would indicate that an exception isn't being caught
             * correctly or that a slow script warning canceled the timer.
             */
            flushRunning = false;
            return shouldBeRunning = isWorkQueued();
        }
    }

    /** Keeps {@link Flusher} running. */
    private final class Rescuer implements RepeatingCommand {
        public boolean execute() {
            if (flushRunning) {
                /*
                 * Since JS is single-threaded, if we're here, then than means that
                 * FLUSHER.execute() started, but did not finish. Reschedule FLUSHER.
                 */
                scheduleFixedDelay(flusher, FLUSHER_DELAY);
            }
            return shouldBeRunning;
        }
    }

    /** Use a GWT.create() here to make it simple to hijack the default implementation. */
    public static final SchedulerImpl INSTANCE = new SchedulerImpl();

    /**
     * The delay between flushing the task queues. Due to browser implementations the actual delay may
     * be longer.
     */
    private static final int FLUSHER_DELAY = 1;

    /** The delay between checking up on SSW problems. */
    private static final int RESCUE_DELAY = 50;

    /**
     * The amount of time that we're willing to spend executing IncrementalCommands. 16ms allows
     * control to be returned to the browser 60 times a second making it possible to keep the frame
     * rate at 60fps.
     */
    private static final double TIME_SLICE = 16;

    /** Extract boilerplate code. */
    private static JsArray<Task> createQueue() {
        return new JsArray<>();
    }

    /** Called from scheduledFixedInterval to give $entry a static function. */
    private static boolean execute(RepeatingCommand cmd) {
        return cmd.execute();
    }

    /** Provides lazy-init pattern for the task queues. */
    private static JsArray<Task> push(JsArray<Task> queue, Task task) {
        if (queue == null) {
            queue = createQueue();
        }
        queue.push(task);
        return queue;
    }

    /**
     * Execute a list of Tasks that hold both ScheduledCommands and RepeatingCommands. Any
     * RepeatingCommands in the <code>tasks</code> queue that want to repeat will be pushed onto the
     * <code>rescheduled</code> queue. The contents of <code>tasks</code> may not be altered while
     * this method is executing.
     *
     * @return <code>rescheduled</code> or a newly-allocated array if <code>rescheduled</code> is
     *     null.
     */
    private static JsArray<Task> runScheduledTasks(JsArray<Task> tasks, JsArray<Task> rescheduled) {
        assert tasks != null : "tasks";

        for (int i = 0, j = tasks.length; i < j; i++) {
            assert tasks.length == j : "Working array length changed " + tasks.length + " != " + j;
            Task t = tasks.getAt(i);

            try {
                // Move repeating commands to incremental commands queue
                if (t.isRepeating()) {
                    if (t.executeRepeating()) {
                        rescheduled = push(rescheduled, t);
                    }
                } else {
                    t.executeScheduled();
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return rescheduled;
    }

    private static void scheduleFixedDelayImpl(RepeatingCommand cmd, int delayMs) {
        DomGlobal.setTimeout(
                new SetTimeoutCallbackFn() {
                    @Override
                    public void onInvoke(Object... p0) {
                        if (execute(cmd)) {
                            DomGlobal.setTimeout(this, delayMs);
                        }
                    }
                },
                delayMs);
    }

    private static void scheduleFixedPeriodImpl(RepeatingCommand cmd, int delayMs) {
        new SetIntervalCallbackFn() {
            double intervalId;

            // initializer exists to let us get the return value from scheduling
            {
                intervalId = DomGlobal.setInterval(this, delayMs);
            }

            @Override
            public void onInvoke(Object... p0) {
                if (!execute(cmd)) {
                    // Either canceled or threw an exception
                    DomGlobal.clearInterval(intervalId);
                }
            }
        };
    }

    /**
     * A RepeatingCommand that calls flushPostEventPumpCommands(). It repeats if there are any
     * outstanding deferred or incremental commands.
     */
    Flusher flusher;

    /**
     * This provides some backup for the main flusher task in case it gets shut down by a slow-script
     * warning.
     */
    Rescuer rescue;

    /*
     * Work queues. Timers store their state on the function, so we don't need to
     * track them. They are not final so that we don't have to shorten them.
     * Processing the values in the queues is a one-shot, and then the array is
     * discarded.
     */
    JsArray<Task> deferredCommands;
    //    JsArray<Task> entryCommands;
    JsArray<Task> finallyCommands;
    JsArray<Task> incrementalCommands;

    /*
     * These two flags are used to control the state of the flusher and rescuer
     * commands.
     */
    private boolean flushRunning = false;
    private boolean shouldBeRunning = false;

    /** Unused, since we have no $entry used on every JS call */
    //    public void flushEntryCommands() {
    //        if (entryCommands != null) {
    //            JsArray<Task> rescheduled = null;
    //            // This do-while loop handles commands scheduling commands
    //            do {
    //                JsArray<Task> oldQueue = entryCommands;
    //                entryCommands = null;
    //                rescheduled = runScheduledTasks(oldQueue, rescheduled);
    //            } while (entryCommands != null);
    //            entryCommands = rescheduled;
    //        }
    //    }

    public void flushFinallyCommands() {
        if (finallyCommands != null) {
            JsArray<Task> rescheduled = null;
            // This do-while loop handles commands scheduling commands
            do {
                JsArray<Task> oldQueue = finallyCommands;
                finallyCommands = null;
                rescheduled = runScheduledTasks(oldQueue, rescheduled);
            } while (finallyCommands != null);
            finallyCommands = rescheduled;
        }
    }

    @Override
    public void scheduleDeferred(ScheduledCommand cmd) {
        deferredCommands = push(deferredCommands, Task.create(cmd));
        maybeSchedulePostEventPumpCommands();
    }

    @Override
    @Deprecated
    public void scheduleEntry(RepeatingCommand cmd) {
        // TODO possibly push to the old scheduler, if present
    }

    @Override
    @Deprecated
    public void scheduleEntry(ScheduledCommand cmd) {
        // TODO possibly push to the old scheduler, if present
    }

    @Override
    public void scheduleFinally(RepeatingCommand cmd) {
        if (finallyCommands == null) {
            Promise.resolve((Object) null)
                    .then(
                            ignore -> {
                                flushFinallyCommands();
                                return null;
                            });
        }
        finallyCommands = push(finallyCommands, Task.create(cmd));
    }

    @Override
    public void scheduleFinally(ScheduledCommand cmd) {
        if (finallyCommands == null) {
            Promise.resolve((Object) null)
                    .then(
                            ignore -> {
                                flushFinallyCommands();
                                return null;
                            });
        }
        finallyCommands = push(finallyCommands, Task.create(cmd));
    }

    @Override
    public void scheduleFixedDelay(RepeatingCommand cmd, int delayMs) {
        scheduleFixedDelayImpl(cmd, delayMs);
    }

    @Override
    public void scheduleFixedPeriod(RepeatingCommand cmd, int delayMs) {
        scheduleFixedPeriodImpl(cmd, delayMs);
    }

    @Override
    public void scheduleIncremental(RepeatingCommand cmd) {
        // Push repeating commands onto the same initial queue for relative order
        deferredCommands = push(deferredCommands, Task.create(cmd));
        maybeSchedulePostEventPumpCommands();
    }

    /** there for testing */
    Duration createDuration() {
        return new Duration();
    }

    /** Called by Flusher. */
    void flushPostEventPumpCommands() {
        if (deferredCommands != null) {
            JsArray<Task> oldDeferred = deferredCommands;
            deferredCommands = null;

            /* We might not have any incremental commands queued. */
            if (incrementalCommands == null) {
                incrementalCommands = createQueue();
            }
            runScheduledTasks(oldDeferred, incrementalCommands);
        }

        if (incrementalCommands != null) {
            incrementalCommands = runRepeatingTasks(incrementalCommands);
        }
    }

    boolean isWorkQueued() {
        return deferredCommands != null || incrementalCommands != null;
    }

    private void maybeSchedulePostEventPumpCommands() {
        if (!shouldBeRunning) {
            shouldBeRunning = true;

            if (flusher == null) {
                flusher = new Flusher();
            }
            scheduleFixedDelayImpl(flusher, FLUSHER_DELAY);

            if (rescue == null) {
                rescue = new Rescuer();
            }
            scheduleFixedDelayImpl(rescue, RESCUE_DELAY);
        }
    }

    /**
     * Execute a list of Tasks that hold RepeatingCommands.
     *
     * @return A replacement array that is possibly a shorter copy of <code>tasks</code>
     */
    private JsArray<Task> runRepeatingTasks(JsArray<Task> tasks) {
        assert tasks != null : "tasks";

        int length = tasks.length;
        if (length == 0) {
            return null;
        }

        boolean canceledSomeTasks = false;

        Duration duration = createDuration();
        while (duration.elapsedMillis() < TIME_SLICE) {
            boolean executedSomeTask = false;
            for (int i = 0; i < length; i++) {
                assert tasks.length == length
                        : "Working array length changed " + tasks.length + " != " + length;
                Task t = tasks.getAt(i);
                if (t == null) {
                    continue;
                }
                executedSomeTask = true;

                assert t.isRepeating() : "Found a non-repeating Task";

                if (!t.executeRepeating()) {
                    tasks.setAt(i, null);
                    canceledSomeTasks = true;
                }
            }
            if (!executedSomeTask) {
                // no work left to do, break to avoid busy waiting until TIME_SLICE is reached
                break;
            }
        }

        if (canceledSomeTasks) {
            JsArray<Task> newTasks = createQueue();
            // Remove tombstones
            for (int i = 0; i < length; i++) {
                if (tasks.getAt(i) != null) {
                    newTasks.push(tasks.getAt(i));
                }
            }
            assert newTasks.length < length;
            return newTasks.length == 0 ? null : newTasks;
        } else {
            return tasks;
        }
    }
}
