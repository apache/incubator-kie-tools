/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.AbstractDiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * A generic session's viewer instance.
 * It aggregates a custom diagram viewer type which provides binds the viewer's diagram instance
 * to be displayed with the one for the given session.
 */
public class SessionViewerImpl<S extends AbstractClientReadOnlySession, H extends AbstractCanvasHandler>
        extends AbstractSessionViewer<S, H>
        implements SessionDiagramViewer<S, H> {

    private final AbstractDiagramViewer<Diagram, H> diagramViewer;
    private final CanvasCommandManager<H> canvasCommandManager;

    SessionViewerImpl(final CanvasCommandManager<H> canvasCommandManager,
                      final WidgetWrapperView view) {
        this.diagramViewer = new SessionDiagramViewer(view);
        this.canvasCommandManager = canvasCommandManager;
    }

    @Override
    protected CanvasCommandManager<H> getCommandManager() {
        return canvasCommandManager;
    }

    @Override
    protected DiagramViewer<Diagram, H> getDiagramViewer() {
        return diagramViewer;
    }

    @Override
    protected Diagram getDiagram() {
        return null != getSessionHandler() ? getSessionHandler().getDiagram() : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ZoomControl<AbstractCanvas> getZoomControl() {
        return getSession().getZoomControl();
    }

    private S getSession() {
        return getInstance();
    }

    /**
     * The internal diagram viewer custom type that binds the diagram(handler to be presented to
     * the one for the current active sessino.
     */
    private class SessionDiagramViewer
            extends AbstractDiagramViewer<Diagram, H> {

        public SessionDiagramViewer(final WidgetWrapperView view) {
            super(view);
        }

        @Override
        public void open(final Diagram item,
                         final int width,
                         final int height,
                         final DiagramViewer.DiagramViewerCallback<Diagram> callback) {
            open(item,
                 width,
                 height,
                 false,
                 callback);
        }

        @Override
        protected void scalePanel(final int width,
                                  final int height) {
            scale(width,
                  height);
        }

        @Override
        protected void enableControls() {
            // The control lifecycle for this diagram editor instance are handled by the session itself.
        }

        @Override
        protected void disableControls() {
            // The control lifecycle for this diagram editor instance are handled by the session itself.
        }

        @Override
        protected void destroyControls() {
            // The control lifecycle for this diagram editor instance are handled by the session itself.
        }

        @Override
        protected AbstractCanvas getCanvas() {
            return getSession().getCanvas();
        }

        @Override
        @SuppressWarnings("unchecked")
        public H getHandler() {
            return (H) getSession().getCanvasHandler();
        }

        @Override
        @SuppressWarnings("unchecked")
        public ZoomControl<AbstractCanvas> getZoomControl() {
            return getSession().getZoomControl();
        }

        @Override
        @SuppressWarnings("unchecked")
        public SelectionControl<H, ?> getSelectionControl() {
            return (SelectionControl<H, ?>) getSession().getSelectionControl();
        }
    }
}
