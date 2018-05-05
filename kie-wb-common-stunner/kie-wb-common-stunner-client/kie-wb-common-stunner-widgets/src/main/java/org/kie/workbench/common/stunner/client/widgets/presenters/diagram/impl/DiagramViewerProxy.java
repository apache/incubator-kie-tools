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

package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import org.kie.workbench.common.stunner.client.widgets.canvas.wires.WiresCanvasPresenter;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * An abstract DiagramViewer type that proxies another canvas handler/diagram instance.
 * It has its own canvas instance and a canvas handler proxy type in order to
 * proxy most of the handler's public API. This allows to reproduce a canvas handler
 * instance in another canvas instances, and provide different managers and controls for each one.
 * @param <D> The diagram type.
 */
public abstract class DiagramViewerProxy<D extends Diagram>
        extends AbstractDiagramViewer<D, AbstractCanvasHandler> {

    public DiagramViewerProxy(final WidgetWrapperView view) {
        super(view);
    }

    protected abstract BaseCanvasHandler<D, ?> getCanvasHandler();

    protected abstract CanvasCommandFactory<AbstractCanvasHandler> getCanvasCommandFactory();

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
    public AbstractCanvasHandler getHandler() {
        return getCanvasHandler();
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
}
