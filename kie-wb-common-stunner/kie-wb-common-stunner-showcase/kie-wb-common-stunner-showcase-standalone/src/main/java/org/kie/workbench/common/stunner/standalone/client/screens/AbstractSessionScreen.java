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

package org.kie.workbench.common.stunner.standalone.client.screens;

import javax.enterprise.event.Observes;

import org.kie.workbench.common.stunner.client.widgets.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.client.widgets.event.SessionFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public abstract class AbstractSessionScreen {

    private ClientSession<AbstractCanvas, AbstractCanvasHandler> session;

    protected abstract void doOpenDiagram();

    protected abstract void doCloseSession();

    protected abstract void doUpdateTitle(String title);

    public void open(final ClientSession<AbstractCanvas, AbstractCanvasHandler> session) {
        if (null != this.session) {
            close();
        }
        this.session = session;
        doOpenDiagram();
        updateTitle();
    }

    public void close() {
        doCloseSession();
        this.session = null;
    }

    @SuppressWarnings("unchecked")
    void onSessionDiagramOpenedEvent(@Observes SessionDiagramOpenedEvent sessionDiagramOpenedEvent) {
        checkNotNull("sessionDiagramOpenedEvent",
                     sessionDiagramOpenedEvent);
        open(sessionDiagramOpenedEvent.getSession());
    }

    @SuppressWarnings("unchecked")
    void onSessionFocusedEvent(@Observes SessionFocusedEvent focusedEvent) {
        open(focusedEvent.getSession());
    }

    void onCanvasSessionDestroyed(@Observes SessionDestroyedEvent sessionDestroyedEvent) {
        checkNotNull("sessionDestroyedEvent",
                     sessionDestroyedEvent);
        close();
    }

    protected AbstractCanvasHandler getCanvasHandler() {
        return null != session ? session.getCanvasHandler() : null;
    }

    protected AbstractCanvas getCanvas() {
        return null != session ? session.getCanvas() : null;
    }

    protected AbstractSession getSession() {
        return (AbstractSession) session;
    }

    private void updateTitle() {
        if (null != getCanvasHandler() && null != getCanvasHandler().getDiagram()) {
            final Diagram<?, ?> diagram = getCanvasHandler().getDiagram();
            doUpdateTitle(diagram.getMetadata().getTitle());
        }
    }
}
