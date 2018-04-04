/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.canvas.wires;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoLayerUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;

/**
 * Lienzo based Canvas for Lienzo layer types.
 * Provides a Lienzo canvas with a single layer as main layer for representing the diagram
 * and supports both primitives/shapes and wires shapes/connectors as well.
 */
public abstract class WiresCanvas extends AbstractCanvas<WiresCanvas.View> {

    public static final String WIRES_CANVAS_GROUP_ID = "stnner.wiresCanvas";

    private static Logger LOGGER = Logger.getLogger(WiresCanvas.class.getName());

    public interface View extends AbstractCanvas.View<LienzoPanel> {

        View setConnectionAcceptor(final IConnectionAcceptor connectionAcceptor);

        View setContainmentAcceptor(final IContainmentAcceptor containmentAcceptor);

        View setDockingAcceptor(final IDockingAcceptor dockingAcceptor);

        WiresManager getWiresManager();

        com.ait.lienzo.client.core.shape.Layer getTopLayer();
    }

    protected WiresCanvas(final Event<CanvasClearEvent> canvasClearEvent,
                          final Event<CanvasShapeAddedEvent> canvasShapeAddedEvent,
                          final Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent,
                          final Event<CanvasDrawnEvent> canvasDrawnEvent,
                          final Event<CanvasFocusedEvent> canvasFocusedEvent,
                          final Layer layer,
                          final View view) {
        super(canvasClearEvent,
              canvasShapeAddedEvent,
              canvasShapeRemovedEvent,
              canvasDrawnEvent,
              canvasFocusedEvent,
              layer,
              view);
    }

    @Override
    public Canvas initialize(final int width,
                             final int height) {
        return this;
    }

    public WiresManager getWiresManager() {
        return view.getWiresManager();
    }

    @Override
    public Optional<Shape> getShapeAt(double x,
                                      double y) {
        if (x > -1 && y > -1) {
            //Layer is guaranteed to be LienzoLayer. Look at the constructor injection.
            final LienzoLayer lienzoLayer = (LienzoLayer) getLayer();
            final String uuid = LienzoLayerUtils.getUUID_At(lienzoLayer,
                                                            x,
                                                            y);
            return Optional.ofNullable(getShape(uuid));
        }
        return Optional.empty();
    }

    protected void log(final Level level,
                       final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
