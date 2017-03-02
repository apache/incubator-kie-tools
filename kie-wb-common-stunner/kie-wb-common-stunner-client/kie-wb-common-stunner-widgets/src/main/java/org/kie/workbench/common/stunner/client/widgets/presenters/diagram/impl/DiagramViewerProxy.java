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

package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import org.kie.workbench.common.stunner.client.widgets.canvas.wires.WiresCanvasPresenter;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerProxy;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * An abstract DiagramViewer type that proxies another canvas handler/diagram instance.
 * It has its own canvas instance and a canvas handler proxy type in order to
 * proxy most of the handler's public API. This allows to reproduce a canvas handler
 * instance in another canvas instances, and provide different managers and controls for each one.
 * @param <D> The diagram type.
 */
public abstract class DiagramViewerProxy<D extends Diagram>
        extends AbstractDiagramViewer<D, CanvasHandlerProxy> {

    private final DefinitionManager definitionManager;
    private final GraphUtils graphUtils;
    private final ShapeManager shapeManager;
    private final CanvasCommandFactory canvasCommandFactory;
    private final SelectionControl<CanvasHandlerProxy, ?> selectionControl;
    private CanvasHandlerProxy proxy;

    public DiagramViewerProxy(final DefinitionManager definitionManager,
                              final GraphUtils graphUtils,
                              final ShapeManager shapeManager,
                              final WidgetWrapperView view,
                              final CanvasCommandFactory canvasCommandFactory,
                              final SelectionControl<CanvasHandlerProxy, ?> selectionControl) {
        super(view);
        this.definitionManager = definitionManager;
        this.graphUtils = graphUtils;
        this.shapeManager = shapeManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.selectionControl = selectionControl;
    }

    protected abstract AbstractCanvasHandler getProxiedHandler();

    @Override
    public void open(final D item,
                     final int width,
                     final int height,
                     final DiagramViewerCallback<D> callback) {
        this.open(item,
                  width,
                  height,
                  false,
                  callback);
    }

    @Override
    public CanvasHandlerProxy getHandler() {
        final boolean isNewHandler = null == proxy && null != getProxiedHandler();
        final boolean isDifferentHandler = null != proxy && null != getProxiedHandler() && !isSameContext();
        if (isNewHandler || isDifferentHandler) {
            this.proxy = new DiagramCanvasHandlerProxy<D, AbstractCanvas>(definitionManager,
                                                                          graphUtils,
                                                                          shapeManager);
        } else if (null != proxy && null == getProxiedHandler()) {
            this.proxy.destroy();
            this.proxy = null;
        }
        return proxy;
    }

    @Override
    public SelectionControl<CanvasHandlerProxy, ?> getSelectionControl() {
        return selectionControl;
    }

    @Override
    protected void scalePanel(final int width,
                              final int height) {
        getWiresCanvasPresenter().getLienzoPanel().setPixelSize(width,
                                                                height);
    }

    private WiresCanvasPresenter getWiresCanvasPresenter() {
        return (WiresCanvasPresenter) getCanvas();
    }

    private class DiagramCanvasHandlerProxy<D extends Diagram, C extends AbstractCanvas> extends CanvasHandlerProxy<D, C> {

        public DiagramCanvasHandlerProxy(final DefinitionManager definitionManager,
                                         final GraphUtils graphUtils,
                                         final ShapeManager shapeManager) {
            super(definitionManager,
                  graphUtils,
                  shapeManager);
        }

        @Override
        protected void draw(final ParameterizedCommand<CommandResult<?>> loadCallback) {
            loadCallback.execute(canvasCommandFactory.draw().execute(this));
        }

        @Override
        public AbstractCanvasHandler getWrapped() {
            return DiagramViewerProxy.this.getProxiedHandler();
        }
    }

    private boolean isSameContext() {
        return null != proxy && proxy.getWrapped().equals(getProxiedHandler());
    }
}
