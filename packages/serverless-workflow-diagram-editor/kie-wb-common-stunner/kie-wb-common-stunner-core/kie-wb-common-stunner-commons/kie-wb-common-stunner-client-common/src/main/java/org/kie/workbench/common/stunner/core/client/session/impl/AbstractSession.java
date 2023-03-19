/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.session.impl;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.uberfire.mvp.Command;

public abstract class AbstractSession<C extends AbstractCanvas, H extends AbstractCanvasHandler>
        implements ClientSession<C, H> {

    public abstract void init(Metadata metadata,
                              Command callback);

    public abstract void open();

    public abstract void destroy();

    public abstract void close();

    public abstract CanvasCommandManager<AbstractCanvasHandler> getCommandManager();

    public abstract SelectionControl<AbstractCanvasHandler, Element> getSelectionControl();

    protected void onControlRegistered(final CanvasControl control) {
        onControlRegistered(control, this);
    }

    @SuppressWarnings("unchecked")
    public static void onControlRegistered(final CanvasControl control,
                                           final ClientSession session) {
        if (control instanceof CanvasControl.SessionAware) {
            ((CanvasControl.SessionAware) control).bind(session);
        }
    }

    @SuppressWarnings("unchecked")
    public static void onControlDestroyed(final CanvasControl control) {
        if (control instanceof RequiresCommandManager) {
            ((RequiresCommandManager) control).setCommandManagerProvider(null);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractSession)) {
            return false;
        }
        AbstractSession that = (AbstractSession) o;
        return getSessionUUID().equals(that.getSessionUUID());
    }

    @Override
    public int hashCode() {
        return getSessionUUID() == null ? 0 : ~~getSessionUUID().hashCode();
    }
}
