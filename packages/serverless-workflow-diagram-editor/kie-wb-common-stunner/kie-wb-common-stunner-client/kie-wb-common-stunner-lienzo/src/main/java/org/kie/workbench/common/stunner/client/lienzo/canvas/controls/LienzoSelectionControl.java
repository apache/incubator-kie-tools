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


package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.AbstractSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MapSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SingleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.graph.Element;

@Dependent
@SingleSelection
@Default
public class LienzoSelectionControl<H extends AbstractCanvasHandler>
        extends AbstractSelectionControl<H> {

    private static Logger LOGGER = Logger.getLogger(LienzoSelectionControl.class.getName());

    private final Map<String, ViewHandler<?>> handlers = new HashMap<>();

    @Inject
    public LienzoSelectionControl(final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                  final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        super(canvasSelectionEvent,
              clearSelectionEvent);
    }

    LienzoSelectionControl(final MapSelectionControl<H> selectionControl,
                           final Event<CanvasSelectionEvent> canvasSelectionEvent,
                           final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        super(selectionControl,
              canvasSelectionEvent,
              clearSelectionEvent);
    }

    @Override
    protected void onRegister(final Element element) {
        super.onRegister(element);
        final Shape<?> shape = getSelectionControl().getCanvas().getShape(element.getUUID());
        if (null != shape) {
            final ShapeView shapeView = shape.getShapeView();
            if (shapeView instanceof HasEventHandlers) {
                final HasEventHandlers hasEventHandlers = (HasEventHandlers) shapeView;
                if (hasEventHandlers.supports(ViewEventType.MOUSE_CLICK)) {
                    final MouseClickHandler clickHandler = new MouseClickHandler() {
                        @Override
                        public void handle(final MouseClickEvent event) {
                            if (event.isButtonLeft() || event.isButtonRight()) {
                                singleSelect(element);
                            }
                        }
                    };
                    hasEventHandlers.addHandler(ViewEventType.MOUSE_CLICK,
                                                clickHandler);
                    registerHandler(shape.getUUID(),
                                    clickHandler);
                }
            }
        }
    }

    void singleSelect(final Element element) {
        if (!getSelectedItems().isEmpty()) {
            clearSelection();
        }
        select(element.getUUID());
    }

    @Override
    protected void onDeregister(Element element) {
        super.onDeregister(element);
        deregister(element.getUUID());
    }

    @Override
    public void clear() {
        super.clear();
        clearHandlers();
    }

    private void clearHandlers() {
        new HashSet<>(handlers.keySet())
                .forEach(this::deregister);
        handlers.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearHandlers();
    }

    private void registerHandler(final String uuid,
                                 final ViewHandler<?> handler) {
        handlers.put(uuid,
                     handler);
    }

    protected void deregister(final String uuid) {
        final Shape shape = getSelectionControl().getCanvas().getShape(uuid);
        final ViewHandler<?> handler = handlers.get(uuid);
        doDeregisterHandler(shape,
                            handler);
    }

    private void doDeregisterHandler(final Shape shape,
                                     final ViewHandler<?> handler) {
        if (null != shape && null != handler) {
            final HasEventHandlers hasEventHandlers = (HasEventHandlers) shape.getShapeView();
            hasEventHandlers.removeHandler(handler);
            handlers.remove(shape.getUUID());
        }
    }

    Map<String, ViewHandler<?>> getHandlers() {
        return handlers;
    }
}
