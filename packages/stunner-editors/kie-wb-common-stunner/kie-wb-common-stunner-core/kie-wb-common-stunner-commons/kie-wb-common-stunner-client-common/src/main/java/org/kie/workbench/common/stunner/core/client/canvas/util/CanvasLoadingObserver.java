/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;

public class CanvasLoadingObserver {

    private static Logger LOGGER = Logger.getLogger(CanvasLoadingObserver.class.getName());

    private static final int TIMEOUT_DURATION = 5000;

    public interface Callback {

        void onLoadingStarted();

        void onLoadingCompleted();
    }

    private Callback callback;
    private Timer timeout;
    private int duration = TIMEOUT_DURATION;

    public void setLoadingObserverCallback(final Callback callback) {
        this.callback = callback;
    }

    public void setTimeoutDuration(final int duration) {
        this.duration = duration;
    }

    public void loadingStarted() {
        if (null != callback
                && null != timeout
                && !timeout.isRunning()) {
            callback.onLoadingStarted();
            log("Starting timeout...");
            this.timeout = new Timer() {
                @Override
                public void run() {
                    logWarn("Loading timeout timer fired after " + duration + "sec... something went wrong?");
                    loadingCompleted();
                }
            };
            timeout.schedule(duration);
        }
    }

    public void loadingCompleted() {
        clearTimeout();
        if (null != callback) {
            callback.onLoadingCompleted();
        }
    }

    private void clearTimeout() {
        if (null != this.timeout) {
            log("Clearing timeout...");
            if (this.timeout.isRunning()) {
                this.timeout.cancel();
            }
            this.timeout = null;
        }
    }

    private static void log(final String message) {
        log(Level.FINE,
            message);
    }

    private static void logWarn(final String message) {
        log(Level.WARNING,
            message);
    }

    private static void log(final Level level,
                            final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
