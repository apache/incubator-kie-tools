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


package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.util.Objects;

import javax.enterprise.event.Observes;

import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;

public abstract class AbstractSelectionAwareSessionCommand<S extends ClientSession> extends AbstractClientSessionCommand<S> {

    public AbstractSelectionAwareSessionCommand(final boolean enabled) {
        super(enabled);
    }

    protected void onCanvasSelectionEvent(final @Observes CanvasSelectionEvent event) {
        checkNotNull("event", event);
        if (checkEventContext(event)) {
            handleCanvasSelectionEvent(event);
        }
    }

    protected void onCanvasClearSelectionEvent(final @Observes CanvasClearSelectionEvent event) {
        checkNotNull("event", event);
        if (checkEventContext(event)) {
            handleCanvasClearSelectionEvent(event);
        }
    }

    protected void onCanvasElementsClearEvent(final @Observes CanvasElementsClearEvent event) {
        checkNotNull("event", event);
        if (checkEventContext(event)) {
            handleCanvasElementsClearEvent(event);
        }
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    protected abstract void handleCanvasSelectionEvent(final CanvasSelectionEvent event);

    protected abstract void handleCanvasClearSelectionEvent(final CanvasClearSelectionEvent event);

    protected abstract void handleCanvasElementsClearEvent(final CanvasElementsClearEvent event);

    protected boolean onlyCanvasRootSelected(final CanvasSelectionEvent event) {
        return event.getIdentifiers().size() == 1 &&
                getCanvasHandler().getDiagram().getMetadata().getCanvasRootUUID().equals(event.getIdentifiers().iterator().next());
    }

    protected void enable(boolean enable) {
        setEnabled(enable);
        fire();
    }
}