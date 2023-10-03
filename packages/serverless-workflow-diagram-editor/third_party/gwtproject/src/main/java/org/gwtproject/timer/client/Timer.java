/*
 * Copyright © 2019 The GWT Project Authors
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
package org.gwtproject.timer.client;

import elemental2.dom.DomGlobal;

/**
 * A simplified, browser-safe timer class. This class serves the same purpose as java.util.Timer,
 * but is simplified because of the single-threaded environment.
 *
 * <p>To schedule a timer, simply create a subclass of it (overriding {@link #run}) and call {@link
 * #schedule} or {@link #scheduleRepeating}.
 *
 * <p>NOTE: If you are using a timer to schedule a UI animation, use
 * <b>org.gwtproject.animation.client.AnimationScheduler</b> or
 * <b>DomGlobal#requestAnimationFrame</b> instead. The browser can optimize your animation for
 * maximum performance.
 */
public abstract class Timer {

    private final GWTTimer timer = new JRETimer();

    /**
     * Returns {@code true} if the timer is running. Timer is running if and only if it is scheduled
     * but it is not expired or cancelled.
     *
     * @return boolean
     */
    public final boolean isRunning() {
        return timer.isRunning();
    }

    /** Cancels this timer. If the timer is not running, this is a no-op. */
    public void cancel() {
        timer.cancel();
    }

    /** This method will be called when a timer fires. Override it to implement the timer's logic. */
    public abstract void run();

    /**
     * Schedules a timer to elapse in the future. If the timer is already running then it will be
     * first canceled before re-scheduling.
     *
     * @param delayMillis how long to wait before the timer elapses, in milliseconds
     */
    public void schedule(int delayMillis) {
        timer.schedule(delayMillis);
    }

    /**
     * Schedules a timer that elapses repeatedly. If the timer is already running then it will be
     * first canceled before re-scheduling.
     *
     * @param periodMillis how long to wait before the timer elapses, in milliseconds, between each
     *     repetition
     */
    public void scheduleRepeating(int periodMillis) {
        timer.scheduleRepeating(periodMillis);
    }

    private class GWTTimer {
        private boolean isRepeating;

        private Double timerId = null;

        public final boolean isRunning() {
            return timerId != null;
        }

        public void cancel() {
            if (!isRunning()) {
                return;
            }

            if (isRepeating) {
                DomGlobal.clearInterval(timerId);
            } else {
                DomGlobal.clearTimeout(timerId);
            }
            timerId = null;
        }

        public void schedule(int delayMillis) {
            if (delayMillis < 0) {
                throw new IllegalArgumentException("must be non-negative");
            }
            if (isRunning()) {
                cancel();
            }
            isRepeating = false;
            timerId = DomGlobal.setTimeout(createTimeoutCallback(this), delayMillis);
        }

        /**
         * Schedules a timer that elapses repeatedly. If the timer is already running then it will be
         * first canceled before re-scheduling.
         *
         * @param periodMillis how long to wait before the timer elapses, in milliseconds, between each
         *     repetition
         */
        public void scheduleRepeating(int periodMillis) {
            if (periodMillis <= 0) {
                throw new IllegalArgumentException("must be positive");
            }
            if (isRunning()) {
                cancel();
            }
            isRepeating = true;
            timerId = DomGlobal.setInterval(createIntervalCallback(this), periodMillis);
        }

        /*
         * Called by native code when this timer fires.
         *
         * Only call run() if cancelCounter has not changed since the timer was scheduled.
         */
        final void fire() {

            if (!isRepeating) {
                timerId = null;
            }

            // Run the timer's code.
            Timer.this.run();
        }

        private DomGlobal.SetTimeoutCallbackFn createTimeoutCallback(GWTTimer timer) {
            return callback -> timer.fire();
        }

        private DomGlobal.SetIntervalCallbackFn createIntervalCallback(GWTTimer timer) {
            return callback -> timer.fire();
        }
    }

    private class JRETimer extends GWTTimer {

        @GwtIncompatible
        private final java.util.Timer timer = new java.util.Timer();

        @GwtIncompatible
        private java.util.TimerTask repeatingTask;

        @GwtIncompatible
        @Override
        public void schedule(int periodMillis) {
            java.util.TimerTask task = new java.util.TimerTask() {
                @Override
                public void run() {
                    Timer.this.run();
                }
            };

            // Schedule the timer to run once after 5000 milliseconds (5 seconds).
            timer.schedule(task, periodMillis);
        }

        @GwtIncompatible
        @Override
        public void scheduleRepeating(int periodMillis) {
            if (repeatingTask != null) {
                repeatingTask.cancel(); // Cancel the previous task if exists.
            }

            repeatingTask = new java.util.TimerTask() {
                @Override
                public void run() {
                    Timer.this.run();
                }
            };

            // Schedule the timer to run repeatedly every periodMillis milliseconds.
            timer.scheduleAtFixedRate(repeatingTask, 0, periodMillis);
        }

        @GwtIncompatible
        @Override
        public void cancel() {
            timer.cancel();
        }
    }
}
