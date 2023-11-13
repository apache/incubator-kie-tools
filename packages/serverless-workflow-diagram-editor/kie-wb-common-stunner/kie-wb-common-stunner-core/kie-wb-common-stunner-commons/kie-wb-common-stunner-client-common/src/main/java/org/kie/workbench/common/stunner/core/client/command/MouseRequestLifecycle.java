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


package org.kie.workbench.common.stunner.core.client.command;

import java.util.function.Supplier;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent;

@Dependent
public class MouseRequestLifecycle implements CommandRequestLifecycle {

    private Supplier<CommandRequestLifecycle> target;

    public void listen(Supplier<CommandRequestLifecycle> target) {
        this.target = target;
    }

    @Override
    public void start() {
        getTarget().start();
    }

    @Override
    public void rollback() {
        getTarget().rollback();
    }

    @Override
    public void complete() {
        getTarget().complete();
    }

    @PreDestroy
    public void destroy() {
        target = null;
    }

    void onMouseDown(final @Observes CanvasMouseDownEvent event) {
        start();
    }

    void onMouseUp(final @Observes CanvasMouseUpEvent event) {
        complete();
    }

    CommandRequestLifecycle getTarget() {
        return target.get();
    }
}
