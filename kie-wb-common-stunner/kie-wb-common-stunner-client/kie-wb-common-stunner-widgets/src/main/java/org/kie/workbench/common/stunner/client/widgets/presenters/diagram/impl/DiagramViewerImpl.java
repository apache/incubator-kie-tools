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

import org.jboss.errai.ioc.client.api.Disposer;
import org.kie.workbench.common.stunner.client.widgets.canvas.wires.WiresCanvasPresenter;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControlRegistrationHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * A generic DiagramViewer implementation.
 * It opens a diagram instance in a new canvas and handler instances for read-only purposes.,
 * It provides a zoom and selection control that third parties can interacti with, but it does not provide
 * any controls that allow the diagram's authoring.
 */
public class DiagramViewerImpl<D extends Diagram, H extends AbstractCanvasHandler, S extends ClientSession>
        extends AbstractDiagramViewer<D, H> {

    private final AbstractCanvas canvas;
    private final H canvasHandler;
    private final ZoomControl<AbstractCanvas> zoomControl;
    private final SelectionControl<H, Element> selectionControl;
    private final Disposer<CanvasControl> disposer;

    private CanvasControlRegistrationHandler<AbstractCanvas, H, S> registrationHandler;

    DiagramViewerImpl(final AbstractCanvas canvas,
                      final H canvasHandler,
                      final WidgetWrapperView view,
                      final ZoomControl<AbstractCanvas> zoomControl,
                      final SelectionControl<H, Element> selectionControl,
                      final Disposer<CanvasControl> disposer) {
        super(view);
        this.canvas = canvas;
        this.canvasHandler = canvasHandler;
        this.zoomControl = zoomControl;
        this.selectionControl = selectionControl;
        this.registrationHandler = null;
        this.disposer = disposer;
    }

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
    protected void enableControls() {
        registrationHandler =
                new CanvasControlRegistrationHandler<AbstractCanvas, H, S>(getHandler().getAbstractCanvas(),
                                                                           getHandler(),
                                                                           disposer);
        registerControls(registrationHandler);
        registrationHandler.enable();
    }

    protected void registerControls(final CanvasControlRegistrationHandler<AbstractCanvas, H, S> registrationHandler) {
        registrationHandler.registerCanvasControl(getZoomControl());
        registrationHandler.registerCanvasHandlerControl(getSelectionControl());
    }

    @Override
    protected void disableControls() {
        if (null != registrationHandler) {
            registrationHandler.disable();
            registrationHandler.clear();
            registrationHandler = null;
        }
    }

    @Override
    protected void destroyControls() {
        if (null != registrationHandler) {
            registrationHandler.disable();
            registrationHandler.destroy();
            registrationHandler = null;
        }
    }

    @Override
    public H getHandler() {
        return canvasHandler;
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

    public AbstractCanvas getCanvas() {
        return canvas;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ZoomControl<AbstractCanvas> getZoomControl() {
        return zoomControl;
    }

    @Override
    public SelectionControl<H, ?> getSelectionControl() {
        return selectionControl;
    }
}
