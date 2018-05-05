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

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
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
        doOpen(item,
               null,
               null,
               callback);
    }

    @Override
    public void open(final S item,
                     final int width,
                     final int height,
                     final SessionViewer.SessionViewerCallback<Diagram> callback) {
        doOpen(item,
               width,
               height,
               callback);
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
    public IsWidget getView() {
        return getDiagramViewer().getView();
    }

    /**
     * Implementation override this method can perform additional operations once opening the diagram viewer, by
     * using a custom <code>DiagramViewer.DiagramViewerCallback</code> instance.
     */
    protected DiagramViewer.DiagramViewerCallback<Diagram> buildCallback(final SessionViewer.SessionViewerCallback<Diagram> callback) {
        return callback;
    }

    private void doOpen(final S item,
                        final Integer width,
                        final Integer height,
                        final SessionViewer.SessionViewerCallback<Diagram> callback) {
        this.session = item;
        if (null != getDiagram()) {
            beforeOpen();
            final DiagramViewer.DiagramViewerCallback<Diagram> diagramViewerCallback = buildCallback(callback);
            if (null != width && null != height) {
                getDiagramViewer().open(getDiagram(),
                                        width,
                                        height,
                                        diagramViewerCallback);
            } else {
                getDiagramViewer().open(getDiagram(),
                                        diagramViewerCallback);
            }
        } else {
            clear();
        }
    }

    protected void beforeOpen() {
    }
}
