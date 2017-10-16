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

import java.lang.annotation.Annotation;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DiagramPreviewProxy;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A generic session's preview instance for subtypes of <code>AbstractClientSession</code>.
 * It aggregates a custom diagram preview type which provides binds the editors's diagram instance
 * with the diagram and controls for the given session. It also scales the view to the given
 * size for the preview.
 */
@Dependent
public class SessionPreviewImpl
        extends AbstractSessionViewer<AbstractClientSession, AbstractCanvasHandler>
        implements SessionDiagramPreview<AbstractClientSession> {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;

    private DiagramPreviewProxy<Diagram> diagramPreview;
    private AbstractCanvas canvas;
    private ZoomControl<AbstractCanvas> zoomControl;

    private DefinitionManager definitionManager;
    private ShapeManager shapeManager;
    private TextPropertyProviderFactory textPropertyProviderFactory;
    private CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    private DefinitionUtils definitionUtils;
    private GraphUtils graphUtils;
    private ManagedInstance<BaseCanvasHandler> canvasHandlerFactories;
    private ManagedInstance<CanvasCommandFactory> canvasCommandFactories;
    private BaseCanvasHandler canvasHandler;
    private CanvasCommandFactory canvasCommandFactory;

    @Inject
    @SuppressWarnings("unchecked")
    public SessionPreviewImpl(final DefinitionManager definitionManager,
                              final ShapeManager shapeManager,
                              final TextPropertyProviderFactory textPropertyProviderFactory,
                              final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                              final DefinitionUtils definitionUtils,
                              final GraphUtils graphUtils,
                              final @Any ManagedInstance<BaseCanvasHandler> canvasHandlerFactories,
                              final @Any ManagedInstance<CanvasCommandFactory> canvasCommandFactories,
                              final SelectionControl<AbstractCanvasHandler, ?> selectionControl,
                              final WidgetWrapperView view) {
        this.definitionManager = definitionManager;
        this.shapeManager = shapeManager;
        this.textPropertyProviderFactory = textPropertyProviderFactory;
        this.canvasCommandManager = canvasCommandManager;

        this.definitionUtils = definitionUtils;
        this.graphUtils = graphUtils;
        this.canvasHandlerFactories = canvasHandlerFactories;
        this.canvasCommandFactories = canvasCommandFactories;

        this.diagramPreview =
                new DiagramPreviewProxy<Diagram>(view,
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
                    protected CanvasCommandFactory getCanvasCommandFactory() {
                        return SessionPreviewImpl.this.getCanvasCommandFactory();
                    }

                    @Override
                    protected BaseCanvasHandler<Diagram, ?> getCanvasHandler() {
                        return SessionPreviewImpl.this.getCanvasHandler();
                    }

                    @Override
                    protected void enableControls() {
                        zoomControl.enable(canvas);
                        zoomControl.setMinScale(0);
                        zoomControl.setMaxScale(1);
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
    }

    BaseCanvasHandler getCanvasHandler() {
        if (canvasHandler == null) {
            BaseCanvasHandler handler;
            final String defSetId = getDiagram().getMetadata().getDefinitionSetId();
            final Annotation qualifier = definitionUtils.getQualifier(defSetId);
            final ManagedInstance<BaseCanvasHandler> customInstances = canvasHandlerFactories.select(qualifier);
            if (customInstances.isUnsatisfied()) {
                handler = canvasHandlerFactories.select(DefinitionManager.DEFAULT_QUALIFIER).get();
            } else {
                handler = customInstances.get();
            }
            canvasHandler = new SessionPreviewCanvasHandlerProxy(handler,
                                                                 definitionManager,
                                                                 graphUtils,
                                                                 shapeManager,
                                                                 textPropertyProviderFactory);
        }

        return canvasHandler;
    }

    CanvasCommandFactory getCanvasCommandFactory() {
        if (canvasCommandFactory == null) {
            final String defSetId = getDiagram().getMetadata().getDefinitionSetId();
            final Annotation qualifier = definitionUtils.getQualifier(defSetId);
            final ManagedInstance<CanvasCommandFactory> customInstances = canvasCommandFactories.select(qualifier);
            if (customInstances.isUnsatisfied()) {
                canvasCommandFactory = canvasCommandFactories.select(DefinitionManager.DEFAULT_QUALIFIER).get();
            } else {
                canvasCommandFactory = customInstances.get();
            }
        }
        return canvasCommandFactory;
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
    protected CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return canvasCommandManager;
    }

    @Override
    protected DiagramViewer<Diagram, AbstractCanvasHandler> getDiagramViewer() {
        return diagramPreview;
    }

    @Override
    protected Diagram getDiagram() {
        return null != getSessionHandler() ? getSessionHandler().getDiagram() : null;
    }

    void commandExecutedFired(@Observes CanvasCommandExecutedEvent commandExecutedEvent) {
        handleCanvasCommandExecutedEvent(commandExecutedEvent);
    }

    void commandUndoExecutedFired(@Observes CanvasUndoCommandExecutedEvent commandUndoExecutedEvent) {
        handleCanvasUndoCommandExecutedEvent(commandUndoExecutedEvent);
    }

    protected void handleCanvasCommandExecutedEvent(final CanvasCommandExecutedEvent event) {
        super.onCommandExecuted(event);
    }

    protected void handleCanvasUndoCommandExecutedEvent(final CanvasUndoCommandExecutedEvent event) {
        super.onCommandUndoExecuted(event);
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
        canvasView.setDecoratorStrokeWidth(2);
        canvasView.setDecoratorStrokeAlpha(0.8);
        canvasView.setDecoratorStrokeColor("#404040");
    }
}
