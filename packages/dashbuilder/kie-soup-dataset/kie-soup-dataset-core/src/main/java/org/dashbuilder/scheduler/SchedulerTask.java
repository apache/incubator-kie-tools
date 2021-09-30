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

import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A scheduler task is a job to be executed in the future.
 */
public abstract class SchedulerTask implements Runnable {

    protected ScheduledFuture future = null;
    protected boolean running = false;
    protected boolean fixedDelay = false;
    protected long fixedDelaySeconds = -1;

    public abstract String getKey();
    public abstract String getDescription();
    public abstract void execute();

    public boolean isRunning() {
        return running;
    }

    public boolean isFixedDelay() {
        return fixedDelay;
    }

    public long getFixedDelaySeconds() {
        return fixedDelaySeconds;
    }

    public void setFixedDelaySeconds(long fixedDelaySeconds) {
        this.fixedDelaySeconds = fixedDelaySeconds;
    }

    public boolean isCancelled() {
        return future != null && future.isCancelled();
    }

    public boolean isDone() {
        return future != null && future.isDone();
    }

    public boolean isMisfired() {
        return !running && future != null && future.getDelay(TimeUnit.MILLISECONDS) < 0;
    }

    public void run() {
        try {
            running = true;
            execute();
        } finally {
            running = false;
        }
    }

    public void cancel() {
        if (future != null) {
            future.cancel(false);
        }
    }

    public String printTimeToFire() {
        if (future != null) {
            long millis = future.getDelay(TimeUnit.MILLISECONDS);
            return formatTime(millis);
        }
        return null;
    }

    public long getMillisTimeToFire() {
        if (future != null) {
            return future.getDelay(TimeUnit.MILLISECONDS);
        }
        return 0;
    }

    public Date getFireDate() {
        long delay = getMillisTimeToFire();
        if (delay < 0) return null;
        return new Date(System.currentTimeMillis() + delay);
    }

    public static String formatTime(long millis) {
         long milliseconds = millis;
         long seconds = milliseconds / 1000; milliseconds %= 1000;
         long minutes = seconds / 60; seconds %= 60;
         long hours = minutes / 60; minutes %= 60;
         long days = hours / 24; hours %= 24;
         long weeks = days / 7; days %= 7;

         String pattern = "{2}h {1}m {0}s";
         if (days > 0) pattern = "{3}d {2}h {1}m {0}s";
         if (weeks > 0) pattern = "{4} sem. {3}d {2}h {1}m {0}s";
         return MessageFormat.format(pattern, new Long[] {new Long(seconds), new Long(minutes), new Long(hours), new Long(days), new Long(weeks)});
     }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("key=").append(getKey()).append(", ");
        buf.append(getDescription());
        return buf.toString();
    }
}