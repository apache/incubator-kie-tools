/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jbpm.quarkus.devui.runtime.rpc.events;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class JBPMDevUIEventPublisher implements EventPublisher {

    public static final String PROCESS_INSTANCE_STATE_DATA_EVENT = "ProcessInstanceStateDataEvent";
    public static final String USER_TASK_INSTANCE_STATE_DATA_EVENT = "UserTaskInstanceStateDataEvent";
    public static final String JOB_EVENT = "JobEvent";

    private final Map<String, Runnable> listeners = new HashMap<>();

    public void registerListener(String eventType, Runnable listener) {
        listeners.put(eventType, listener);
    }

    public void clear() {
        this.listeners.clear();
    }

    @Override
    public void publish(DataEvent<?> event) {
        ofNullable(listeners.get(event.getType())).ifPresent(Runnable::run);
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        events.forEach(this::publish);
    }
}
