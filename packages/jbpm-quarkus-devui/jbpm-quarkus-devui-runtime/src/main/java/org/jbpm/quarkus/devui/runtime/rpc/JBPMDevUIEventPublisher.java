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

package org.jbpm.quarkus.devui.runtime.rpc;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.profile.IfBuildProfile;

import java.util.Collection;
import java.util.Objects;
@ApplicationScoped
@IfBuildProfile("dev")
public class JBPMDevUIEventPublisher implements EventPublisher {

    private Runnable onProcessEvent;
    private Runnable onTaskEvent;
    private Runnable onJobEvent;

    @Override
    public void publish(DataEvent<?> event) {
        switch (event.getType()) {
            case "ProcessInstanceStateDataEvent":
                maybeRun(onProcessEvent);
                break;
            case "UserTaskInstanceStateDataEvent":
                maybeRun(onTaskEvent);
                break;
            case "JobEvent":
                maybeRun(onJobEvent);
                break;
        }
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        events.forEach(this::publish);
    }

    private void maybeRun(Runnable runnable) {
        if (Objects.nonNull(runnable)) {
            runnable.run();
        }
    }

    public void setOnProcessEventListener(Runnable onProcessEvent) {
        this.onProcessEvent = onProcessEvent;
    }

    public void setOnTaskEventListener(Runnable onTaskEvent) {
        this.onTaskEvent = onTaskEvent;
    }

    public void setOnJobEventListener(Runnable onJobEvent) {
        this.onJobEvent = onJobEvent;
    }
}