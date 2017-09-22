/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.event.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

/**
 * Observes events {@link ScreenMaximizedEvent}, {@link ScreenMinimizedEvent} and trigger callbacks registered to
 * events. This class is used when it is necessary to observe events on non CDI managed bean classes.
 */
@Dependent
public class ScreenResizeEventObserver {

    private Map<Class<? extends ScreenResizeEvent>, List<Consumer<? extends ScreenResizeEvent>>> consumers;

    public ScreenResizeEventObserver(){
        init();
    }

    private void init() {
        consumers = new HashMap<>();
    }

    public void onEventReceived(@Observes ScreenMaximizedEvent event) {
        consumers.get(event.getClass()).forEach(consumer -> ((Consumer<ScreenMaximizedEvent>) consumer).accept(event));
    }

    public void onEventReceived(@Observes ScreenMinimizedEvent event) {
        consumers.get(event.getClass()).forEach(consumer -> ((Consumer<ScreenMinimizedEvent>) consumer).accept(event));
    }

    public <T extends ScreenResizeEvent> void registerEventCallback(Class<T> eventClass,
                                                                    Consumer<T> callback) {
        if (!this.consumers.containsKey(eventClass)) {
            consumers.put(eventClass,
                          new ArrayList<>());
        }
        this.consumers.get(eventClass).add(callback);
    }
}
