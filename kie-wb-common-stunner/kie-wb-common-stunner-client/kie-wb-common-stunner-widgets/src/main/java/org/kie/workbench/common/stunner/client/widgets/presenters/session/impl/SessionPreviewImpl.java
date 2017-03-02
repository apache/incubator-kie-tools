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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DiagramPreviewProxy;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerProxy;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A generic session's preview instance for subtypes of <code>AbstractClientSession</code>.
 * It aggregates a custom diagram preview type which provides binds the editors's diagram instance
 * with the diagram and controls for the given session. It also scales the view to the given
 * size for the preview.
 */
@Dependent
public class SessionPreviewImpl
        extends AbstractSessionViewer<AbstractClientSession, CanvasHandlerProxy>
        implements SessionDiagramPreview<AbstractClientSession> {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;

    private final DiagramPreviewProxy<Diagram> diagramPreview;
    private final CanvasCommandManager<CanvasHandlerProxy> canvasCommandManager;
    private final ShapeManager shapeManager;
    private AbstractCanvas canvas;
    private ZoomControl<AbstractCanvas> zoomControl;

    @Inject
    @SuppressWarnings("unchecked")
    public SessionPreviewImpl(final DefinitionManager definitionManager,
                              final GraphUtils graphUtils,
                              final ShapeManager shapeManager,
                              final CanvasCommandFactory canvasCommandFactory,
                              final SelectionControl<CanvasHandlerProxy, ?> selectionControl,
                              final CanvasCommandManager<CanvasHandlerProxy> canvasCommandManager,
                              final WidgetWrapperView view) {
        this.diagramPreview =
                new DiagramPreviewProxy<Diagram>(definitionManager,
                                                 graphUtils,
                                                 shapeManager,
                                                 view,
                                                 canvasCommandFactory,
                                                 selectionControl) {
                    @Override
                    public <C extends Canvas> ZoomControl<C> getZoomControl() {
                        return (ZoomControl<C>) zoomControl;
                    }

                    @Override
                    protected int getWidth() {
                        return DEFAULT_WIDTH;
                    }

                    @Override
                    protected int getHeight() {
                        return DEFAULT_HEIGHT;
                    }

                    @Override
                    protected AbstractCanvas getCanvas() {
                        return canvas;
                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    protected AbstractCanvasHandler getProxiedHandler() {
                        return SessionPreviewImpl.this.getSessionHandler();
                    }

                    @Override
                    protected void enableControls() {
                        zoomControl.enable(canvas);
                        zoomControl.setMinScale(0).setMaxScale(1);
                    }

                    @Override
                    protected void disableControls() {
                        zoomControl.disable();
                    }

                    @Override
                    protected void destroyControls() {
                        zoomControl.disable();
                    }
                };
        this.canvas = null;
        this.zoomControl = null;
        this.shapeManager = shapeManager;
        this.canvasCommandManager = canvasCommandManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void beforeOpen() {
        super.beforeOpen();
        final CanvasFactory<Canvas, CanvasHandler> canvasFactory = shapeManager.getCanvasFactory(getDiagram());
        canvas = (AbstractCanvas) canvasFactory.newCanvas();
        zoomControl = canvasFactory.newControl(ZoomControl.class);
    }

    @Override
    protected CanvasCommandManager<CanvasHandlerProxy> getCommandManager() {
        return canvasCommandManager;
    }

    @Override
    protected DiagramViewer<Diagram, CanvasHandlerProxy> getDiagramViewer() {
        return diagramPreview;
    }

    @Override
    protected Diagram getDiagram() {
        return null != getSessionHandler() ? getSessionHandler().getDiagram() : null;
    }

    void commandExecutedFired(@Observes CanvasCommandExecutedEvent commandExecutedEvent) {
        super.onCommandExecuted(commandExecutedEvent);
    }

    void commandUndoExecutedFired(@Observes CanvasUndoCommandExecutedEvent commandUndoExecutedEvent) {
        super.onCommandUndoExecuted(commandUndoExecutedEvent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ZoomControl<AbstractCanvas> getZoomControl() {
        return zoomControl;
    }

    /**
     * For preview purposes, make more visible the decorator for the canvas, so update it once
     * the canvas has been initialized.
     */
    @Override
    protected DiagramViewer.DiagramViewerCallback<Diagram> buildCallback(final SessionViewerCallback<AbstractClientSession, Diagram> callback) {
        return new DiagramViewer.DiagramViewerCallback<Diagram>() {
            @Override
            public void afterCanvasInitialized() {
                checkNotNull("canvas",
                             canvas);
                updateCanvasDecorator(canvas.getView());
                callback.afterCanvasInitialized();
            }

            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                callback.onError(error);
            }
        };
    }

    /**
     * Updates the canvas decorator for preview purposes using
     * a higher width and darker color for the line stroke.
     */
    private void updateCanvasDecorator(final AbstractCanvas.View canvasView) {
        canvasView
                .setDecoratorStrokeWidth(2)
                .setDecoratorStrokeAlpha(0.8)
                .setDecoratorStrokeColor("#404040");
    }
}
