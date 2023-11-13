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

import java.util.Objects;
import java.util.function.Supplier;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.AbstractDiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AlertsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * A generic session's viewer instance.
 * It aggregates a custom diagram viewer type which provides binds the viewer's diagram instance
 * to be displayed with the one for the given session.
 */
@Dependent
public class SessionViewerImpl<S extends ViewerSession>
        extends AbstractSessionViewer<S>
        implements SessionDiagramViewer<S> {

    private final AbstractDiagramViewer<Diagram, AbstractCanvasHandler> diagramViewer;
    private final StunnerPreferencesRegistries preferencesRegistries;
    private final ScrollableLienzoPanel canvasPanel;
    private Supplier<Diagram> diagramSupplier;

    @Inject
    public SessionViewerImpl(final WidgetWrapperView view,
                             final ScrollableLienzoPanel canvasPanel,
                             final StunnerPreferencesRegistries preferencesRegistries) {
        this.canvasPanel = canvasPanel;
        this.preferencesRegistries = preferencesRegistries;
        this.diagramViewer = new SessionDiagramViewer(view);
        this.diagramSupplier = () -> null != getSessionHandler() ?
                getSessionHandler().getDiagram() :
                null;
    }

    public SessionViewerImpl<S> setDiagramSupplier(final Supplier<Diagram> diagramSupplier) {
        this.diagramSupplier = diagramSupplier;
        return this;
    }

    @Override
    protected DiagramViewer<Diagram, AbstractCanvasHandler> getDiagramViewer() {
        return diagramViewer;
    }

    @Override
    protected Diagram getDiagram() {
        return Objects.nonNull(diagramSupplier) ? diagramSupplier.get() : null;
    }

    @Override
    protected void onAfterCanvasInitialized() {
        super.onAfterCanvasInitialized();
        canvasPanel.getView().onResize();
    }

    @Override
    public void destroy() {
        super.destroy();
        diagramSupplier = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MediatorsControl<AbstractCanvas> getMediatorsControl() {
        return getSession().getMediatorsControl();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AlertsControl<AbstractCanvas> getAlertsControl() {
        return getSession().getAlertsControl();
    }

    private S getSession() {
        return getInstance();
    }

    /**
     * The internal diagram viewer custom type that binds the diagram(handler to be presented to
     * the one for the current active sessino.
     */
    private class SessionDiagramViewer
            extends AbstractDiagramViewer<Diagram, AbstractCanvasHandler> {

        public SessionDiagramViewer(final WidgetWrapperView view) {
            super(view);
        }

        @Override
        protected void onOpen(final Diagram diagram) {
            // The control lifecycle for this diagram editor instance are handled by the session itself.
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
        protected void destroyControls() {
            // The control lifecycle for this diagram editor instance are handled by the session itself.
        }

        @Override
        protected AbstractCanvas getCanvas() {
            return getSession().getCanvas();
        }

        @Override
        public CanvasPanel getCanvasPanel() {
            return canvasPanel;
        }

        @Override
        protected StunnerPreferencesRegistries getPreferencesRegistry() {
            return preferencesRegistries;
        }

        @Override
        @SuppressWarnings("unchecked")
        public AbstractCanvasHandler getHandler() {
            return getSession().getCanvasHandler();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C extends Canvas> MediatorsControl<C> getMediatorsControl() {
            return (MediatorsControl<C>) getSession().getMediatorsControl();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C extends Canvas> AlertsControl<C> getAlertsControl() {
            return (AlertsControl<C>) getSession().getAlertsControl();
        }

        @Override
        @SuppressWarnings("unchecked")
        public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
            return getSession().getSelectionControl();
        }
    }
}
