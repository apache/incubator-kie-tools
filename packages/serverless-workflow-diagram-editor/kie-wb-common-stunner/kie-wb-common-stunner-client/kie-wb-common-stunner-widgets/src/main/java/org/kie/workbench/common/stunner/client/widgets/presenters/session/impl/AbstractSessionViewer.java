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


package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import io.crysknife.client.IsElement;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

public abstract class AbstractSessionViewer<S extends AbstractSession>
        implements SessionViewer<S, AbstractCanvasHandler, Diagram> {

    private S session;

    protected abstract DiagramViewer<Diagram, AbstractCanvasHandler> getDiagramViewer();

    protected abstract Diagram getDiagram();

    @Override
    public void open(final S item,
                     final SessionViewer.SessionViewerCallback<Diagram> callback) {
        this.session = item;
        if (null != getDiagram()) {
            onBeforeOpen();
            final SessionViewer.SessionViewerCallback<Diagram> c = new SessionViewerCallback<Diagram>() {
                @Override
                public void afterCanvasInitialized() {
                    onAfterCanvasInitialized();
                    callback.afterCanvasInitialized();
                }

                @Override
                public void onSuccess() {
                    onOpenSuccess();
                    callback.onSuccess();
                }

                @Override
                public void onError(final ClientRuntimeError error) {
                    callback.onError(error);
                }
            };
            getDiagramViewer().open(getDiagram(), c);
        } else {
            clear();
        }
    }

    @Override
    public void scale(final int width,
                      final int height) {
        if (null != getDiagramViewer()) {
            getDiagramViewer().scale(width,
                                     height);
        }
    }

    @Override
    public void clear() {
        if (null != getDiagramViewer()) {
            getDiagramViewer().clear();
        }
    }

    @Override
    public void destroy() {
        if (null != getDiagramViewer()) {
            getDiagramViewer().destroy();
        }
        session = null;
    }

    @Override
    public S getInstance() {
        return session;
    }

    @Override
    public AbstractCanvasHandler getHandler() {
        return null != getDiagramViewer() ? getDiagramViewer().getHandler() : null;
    }

    public AbstractCanvasHandler getSessionHandler() {
        return null != session ? (AbstractCanvasHandler) session.getCanvasHandler() : null;
    }

    public AbstractCanvas getCanvas() {
        return null != getHandler() ? (AbstractCanvas) getHandler().getCanvas() : null;
    }

    @Override
    public IsElement getView() {
        return getDiagramViewer().getView();
    }

    protected void onBeforeOpen() {
    }

    protected void onOpenSuccess() {
    }

    protected void onAfterCanvasInitialized() {
    }
}
