/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.widgets.views;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;

@ApplicationScoped
public class LoadingBox {

    private static Logger LOGGER = Logger.getLogger(LoadingBox.class.getName());
    private static final int TIMEOUT = 30000; // 30s.

    public interface View {

        View show();

        View hide();
    }

    View view;

    private Timer timer;

    protected LoadingBox() {
        this(null);
    }

    @Inject
    public LoadingBox(final View view) {
        this.view = view;
    }

    public void show() {
        if (null != this.timer && this.timer.isRunning()) {
            return;
        }
        startTimer();
        view.show();
    }

    public void hide() {
        stopTimer();
        view.hide();
    }

    private void startTimer() {
        stopTimer();
        this.timer = new Timer() {
            @Override
            public void run() {
                log(Level.WARNING,
                    "Loading box - Timeout exceeded!");
                hide();
            }
        };
        timer.schedule(TIMEOUT);
    }

    private void stopTimer() {
        if (null != this.timer) {
            if (this.timer.isRunning()) {
                this.timer.cancel();
            }
            this.timer = null;
        }
    }

    /*public void onCanvasProcessingStarted(@Observes CanvasProcessingStartedEvent canvasProcessingStartedEvent) {
        checkNotNull("canvasProcessingStartedEvent", canvasProcessingStartedEvent);
        show();
    }

    public void onCanvasProcessingCompleted(@Observes CanvasProcessingCompletedEvent canvasProcessingCompletedEvent) {
        checkNotNull("canvasProcessingCompletedEvent", canvasProcessingCompletedEvent);
        hide();
    }

    public void onWidgetProcessingStarted(@Observes WidgetProcessingStartedEvent widgetProcessingStartedEvent) {
        checkNotNull("widgetProcessingStartedEvent", widgetProcessingStartedEvent);
        show();
    }

    public void onWidgetProcessingCompleted(@Observes WidgetProcessingCompletedEvent widgetProcessingCompletedEvent) {
        checkNotNull("widgetProcessingCompletedEvent", widgetProcessingCompletedEvent);
        hide();
    }*/

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
