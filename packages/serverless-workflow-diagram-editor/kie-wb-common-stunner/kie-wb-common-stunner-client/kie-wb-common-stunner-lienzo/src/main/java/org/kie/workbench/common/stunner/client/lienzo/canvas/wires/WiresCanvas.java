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


package org.kie.workbench.common.stunner.client.lienzo.canvas.wires;

import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.wires.WiresManagerFactory;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;

@Dependent
public class WiresCanvas extends LienzoCanvas<WiresCanvasView> {

    public static final String WIRES_CANVAS_GROUP_ID = "stnner.wiresCanvas";

    private final WiresManagerFactory wiresManagerFactory;
    private WiresCanvasView view;
    private WiresManager wiresManager;

    @Inject
    public WiresCanvas(final Event<CanvasClearEvent> canvasClearEvent,
                       final Event<CanvasShapeAddedEvent> canvasShapeAddedEvent,
                       final Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent,
                       final Event<CanvasDrawnEvent> canvasDrawnEvent,
                       final Event<CanvasFocusedEvent> canvasFocusedEvent,
                       final @Default WiresManagerFactory wiresManagerFactory,
                       final WiresCanvasView view) {
        super(canvasClearEvent,
              canvasShapeAddedEvent,
              canvasShapeRemovedEvent,
              canvasDrawnEvent,
              canvasFocusedEvent);
        this.view = view;
        this.wiresManagerFactory = wiresManagerFactory;
    }

    @Override
    public AbstractCanvas<WiresCanvasView> initialize(final CanvasPanel panel,
                                                      final CanvasSettings settings) {
        super.initialize(panel, settings);
        final WiresLayer layer = getView().getLayer();
        wiresManager = wiresManagerFactory.newWiresManager(layer.getLienzoLayer());
        wiresManager.setSpliceEnabled(false);
        // Set the default NONE acceptors for wires capabilities.
        // Each of these ones is being handled by each of the canvas controls associated
        wiresManager.setLocationAcceptor(ILocationAcceptor.NONE);
        wiresManager.setContainmentAcceptor(IContainmentAcceptor.NONE);
        wiresManager.setDockingAcceptor(IDockingAcceptor.NONE);
        wiresManager.setConnectionAcceptor(IConnectionAcceptor.NONE);
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.NONE);
        view.use(wiresManager);
        return this;
    }

    @Override
    protected void addChild(final Shape shape) {
        getView().addRoot(shape.getShapeView());
    }

    @Override
    protected void deleteChild(final Shape shape) {
        getView().deleteRoot(shape.getShapeView());
    }

    @Override
    public WiresCanvasView getView() {
        return view;
    }

    public WiresManager getWiresManager() {
        return wiresManager;
    }
}
