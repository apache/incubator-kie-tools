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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.DataEvent;

import static org.jbpm.quarkus.devui.runtime.rpc.events.JBPMDevUIEventPublisher.JOB_EVENT;
import static org.jbpm.quarkus.devui.runtime.rpc.events.JBPMDevUIEventPublisher.PROCESS_INSTANCE_STATE_DATA_EVENT;
import static org.jbpm.quarkus.devui.runtime.rpc.events.JBPMDevUIEventPublisher.USER_TASK_INSTANCE_STATE_DATA_EVENT;

import static org.mockito.Mockito.*;

public class JBPMDevUIEventPublisherTest {

    private Runnable processEventListener;
    private Runnable taskEventListener;
    private Runnable jobEventListener;

    private JBPMDevUIEventPublisher eventsPublisher;

    @BeforeEach
    public void init() {
        processEventListener = mock(Runnable.class);
        taskEventListener = mock(Runnable.class);
        jobEventListener = mock(Runnable.class);

        eventsPublisher = new JBPMDevUIEventPublisher();
        eventsPublisher.registerListener(PROCESS_INSTANCE_STATE_DATA_EVENT, processEventListener);
        eventsPublisher.registerListener(USER_TASK_INSTANCE_STATE_DATA_EVENT, taskEventListener);
        eventsPublisher.registerListener(JOB_EVENT, jobEventListener);
    }

    @Test
    public void onProcessStateEvent() {
        eventsPublisher.publish(mockKogitoDataEvent(PROCESS_INSTANCE_STATE_DATA_EVENT));
        verify(processEventListener, times(1)).run();
        verify(taskEventListener, never()).run();
        verify(jobEventListener, never()).run();
    }

    @Test
    public void onTaskStateEvent() {
        eventsPublisher.publish(mockKogitoDataEvent(USER_TASK_INSTANCE_STATE_DATA_EVENT));
        verify(processEventListener, never()).run();
        verify(taskEventListener, times(1)).run();
        verify(jobEventListener, never()).run();
    }

    @Test
    public void onJobEvent() {
        eventsPublisher.publish(mockKogitoDataEvent(JOB_EVENT));
        verify(processEventListener, never()).run();
        verify(taskEventListener, never()).run();
        verify(jobEventListener, times(1)).run();
    }

    private DataEvent<?> mockKogitoDataEvent(String eventType) {
        DataEvent<?> event = mock(DataEvent.class);
        when(event.getType()).thenReturn(eventType);
        return event;
    }
}
