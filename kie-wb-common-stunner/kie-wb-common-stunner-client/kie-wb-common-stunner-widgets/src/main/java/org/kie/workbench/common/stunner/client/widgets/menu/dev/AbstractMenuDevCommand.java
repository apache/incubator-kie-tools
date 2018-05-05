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
package org.kie.workbench.common.stunner.client.widgets.menu.dev;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;

public abstract class AbstractMenuDevCommand
        implements MenuDevCommand {

    private final SessionManager sessionManager;

    protected AbstractMenuDevCommand(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public IconType getIcon() {
        return IconType.PRINT;
    }

    protected AbstractSession getSession() {
        return sessionManager.getCurrentSession();
    }

    protected AbstractCanvasHandler getCanvasHandler() {
        return null != getSession() ? (AbstractCanvasHandler) getSession().getCanvasHandler() : null;
    }

    protected Diagram getDiagram() {
        return null != getCanvasHandler() ? getCanvasHandler().getDiagram() : null;
    }

    protected Graph getGraph() {
        return null != getDiagram() ? getDiagram().getGraph() : null;
    }
}
