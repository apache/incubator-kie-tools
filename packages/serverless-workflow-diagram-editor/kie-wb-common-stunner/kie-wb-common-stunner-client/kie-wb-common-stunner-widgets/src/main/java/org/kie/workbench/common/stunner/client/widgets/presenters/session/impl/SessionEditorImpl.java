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
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.AbstractDiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AlertsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LineSpliceAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * A generic session's editor instance.
 * It aggregates a custom diagram editor type which provides binds the editors's diagram instance and the
 * different editors' controls with the diagram and controls for the given session.
 */
@Dependent
public class SessionEditorImpl<S extends EditorSession>
        extends AbstractSessionViewer<S>
        implements SessionDiagramEditor<S> {

    private final AbstractDiagramViewer<Diagram, AbstractCanvasHandler> diagramViewer;
    private final ScrollableLienzoPanel canvasPanel;
    private final StunnerPreferencesRegistries preferencesRegistries;

    private Supplier<Diagram> diagramSupplier;

    @Inject
    public SessionEditorImpl(final WidgetWrapperView view,
                             final ScrollableLienzoPanel canvasPanel,
                             final StunnerPreferencesRegistries preferencesRegistries) {
        this.canvasPanel = canvasPanel;
        this.preferencesRegistries = preferencesRegistries;
        this.diagramViewer = new SessionDiagramEditor(view);
        this.diagramSupplier = () -> null != getSessionHandler() ?
                getSessionHandler().getDiagram() :
                null;
    }

    public SessionEditorImpl<S> setDiagramSupplier(final Supplier<Diagram> diagramSupplier) {
        this.diagramSupplier = diagramSupplier;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return (CanvasCommandManager<AbstractCanvasHandler>) getSession().getCommandManager();
    }

    @SuppressWarnings("unchecked")
    public DiagramEditor<Diagram, AbstractCanvasHandler> getDiagramEditor() {
        return (DiagramEditor<Diagram, AbstractCanvasHandler>) diagramViewer;
    }

    @Override
    protected DiagramViewer<Diagram, AbstractCanvasHandler> getDiagramViewer() {
        return diagramViewer;
    }

    public ScrollableLienzoPanel getCanvasPanel() {
        return canvasPanel;
    }

    @Override
    protected Diagram getDiagram() {
        return Objects.nonNull(diagramSupplier) ? diagramSupplier.get() : null;
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

    @Override
    protected void onAfterCanvasInitialized() {
        super.onAfterCanvasInitialized();
        canvasPanel.getView().onResize();
        canvasPanel.beginScrollEventTrack();
    }

    @Override
    public void destroy() {
        super.destroy();
        ((WidgetWrapperView) getView()).clear();
        diagramSupplier = null;
    }

    /**
     * the custom internal diagram editor type which provides binds the editors's diagram instance and the
     * different editors' controls with the diagram and controls for the given session.
     */
    private class SessionDiagramEditor
            extends AbstractDiagramViewer<Diagram, AbstractCanvasHandler>
            implements DiagramEditor<Diagram, AbstractCanvasHandler> {

        public SessionDiagramEditor(final WidgetWrapperView view) {
            super(view);
        }

        @Override
        protected void onOpen(final Diagram diagram) {
        }

        @Override
        protected void destroyInstances() {
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
        public MediatorsControl<AbstractCanvas> getMediatorsControl() {
            return SessionEditorImpl.this.getMediatorsControl();
        }

        @Override
        @SuppressWarnings("unchecked")
        public AlertsControl<AbstractCanvas> getAlertsControl() {
            return SessionEditorImpl.this.getAlertsControl();
        }

        @Override
        @SuppressWarnings("unchecked")
        public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
            return getSession().getSelectionControl();
        }

        @Override
        @SuppressWarnings("unchecked")
        public CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
            return SessionEditorImpl.this.getCommandManager();
        }

        @Override
        @SuppressWarnings("unchecked")
        public ConnectionAcceptorControl<AbstractCanvasHandler> getConnectionAcceptorControl() {
            return getSession().getConnectionAcceptorControl();
        }

        @Override
        @SuppressWarnings("unchecked")
        public ContainmentAcceptorControl<AbstractCanvasHandler> getContainmentAcceptorControl() {
            return getSession().getContainmentAcceptorControl();
        }

        @Override
        @SuppressWarnings("unchecked")
        public DockingAcceptorControl<AbstractCanvasHandler> getDockingAcceptorControl() {
            return getSession().getDockingAcceptorControl();
        }

        @Override
        @SuppressWarnings("unchecked")
        public LineSpliceAcceptorControl<AbstractCanvasHandler> getLineSpliceAcceptorControl() {
            return getSession().getLineSpliceAcceptorControl();
        }
    }
}
