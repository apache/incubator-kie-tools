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

package org.kie.workbench.common.stunner.core.client.canvas.controls.select;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Element;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public final class SelectionControlImpl<H extends AbstractCanvasHandler>
        extends AbstractCanvasHandlerRegistrationControl<H>
        implements SelectionControl<H, Element> {

    private final Event<CanvasElementSelectedEvent> elementSelectedEvent;
    private final Event<CanvasClearSelectionEvent> clearSelectionEvent;
    private MouseClickHandler layerClickHandler;
    private String selectedElementUUID;

    @Inject
    public SelectionControlImpl(final Event<CanvasElementSelectedEvent> elementSelectedEvent,
                                final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        this.elementSelectedEvent = elementSelectedEvent;
        this.clearSelectionEvent = clearSelectionEvent;
    }

    @Override
    public void enable(final H canvasHandler) {
        super.enable(canvasHandler);
        final Layer layer = canvasHandler.getCanvas().getLayer();
        // Click handler for the canvas area - cleans current selection, if any.
        final MouseClickHandler clickHandler = new MouseClickHandler() {
            @Override
            public void handle(final MouseClickEvent event) {
                if (event.isButtonLeft() || event.isButtonRight()) {
                    clearSelection(false);
                    final String canvasRootUUID = getRootUUID();
                    fireCanvasClear();
                    if (null != canvasRootUUID) {
                        elementSelectedEvent.fire(new CanvasElementSelectedEvent(canvasHandler,
                                                                                 canvasRootUUID));
                    }
                }
            }
        };
        layer.addHandler(ViewEventType.MOUSE_CLICK,
                         clickHandler);
        this.layerClickHandler = clickHandler;
    }

    @Override
    public void register(final Element element) {
        if (isEnabled()
                && checkNotRegistered(element)) {
            final Shape<?> shape = getCanvas().getShape(element.getUUID());
            if (null != shape) {
                final ShapeView shapeView = shape.getShapeView();
                if (shapeView instanceof HasEventHandlers) {
                    final HasEventHandlers hasEventHandlers = (HasEventHandlers) shapeView;
                    if (hasEventHandlers.supports(ViewEventType.MOUSE_CLICK)) {
                        final MouseClickHandler clickHandler = new MouseClickHandler() {
                            @Override
                            public void handle(final MouseClickEvent event) {
                                if (event.isButtonLeft() || event.isButtonRight()) {
                                    select(element);
                                }
                            }
                        };
                        hasEventHandlers.addHandler(ViewEventType.MOUSE_CLICK,
                                                    clickHandler);
                        registerHandler(shape.getUUID(),
                                        clickHandler);
                    }
                }
                registerHandler(shape.getUUID(),
                                layerClickHandler);
            }
        }
    }

    @Override
    public SelectionControl<H, Element> select(final Element element) {
        return select(element.getUUID());
    }

    private SelectionControl<H, Element> select(final String uuid) {
        if (null != selectedElementUUID
                && !isSelected(uuid)) {
            deselect(uuid);
            fireCanvasClear();
        }
        if (null == selectedElementUUID) {
            selectedElementUUID = uuid;
            updateViewShapesState();
            elementSelectedEvent.fire(new CanvasElementSelectedEvent(canvasHandler,
                                                                     uuid));
        }
        return this;
    }

    @Override
    public SelectionControl<H, Element> deselect(final Element element) {
        return deselect(element.getUUID());
    }

    private SelectionControl<H, Element> deselect(final String uuid) {
        if (isSelected(uuid)) {
            selectedElementUUID = null;
            updateViewShapesState();
        }
        return this;
    }

    @Override
    public boolean isSelected(final Element element) {
        return element.getUUID().equals(selectedElementUUID);
    }

    private boolean isSelected(final String uuid) {
        return null != selectedElementUUID && selectedElementUUID.equals(uuid);
    }

    @Override
    public Collection<String> getSelectedItems() {
        return Collections.singleton(selectedElementUUID);
    }

    @Override
    public SelectionControl<H, Element> clearSelection() {
        return clearSelection(true);
    }

    public boolean isEnabled() {
        return super.isEnabled() && null != canvasHandler.getCanvas();
    }

    @Override
    public void deregisterAll() {
        super.deregisterAll();
        clearSelection(false);
    }

    @Override
    public void deregister(final String uuid) {
        super.deregister(uuid);
        deselect(uuid);
    }

    @Override
    protected void doDisable() {
        super.doDisable();
        if (isEnabled()
                && null != layerClickHandler
                && null != getCanvas().getLayer()) {
            getCanvas().getLayer().removeHandler(layerClickHandler);
            this.layerClickHandler = null;
        }
    }

    void onShapeRemovedEvent(final @Observes CanvasShapeRemovedEvent shapeRemovedEvent) {
        checkNotNull("shapeRemovedEvent",
                     shapeRemovedEvent);
        if (isEnabled() && getCanvas().equals(shapeRemovedEvent.getCanvas())) {
            final Shape<?> shape = shapeRemovedEvent.getShape();
            deselect(shape.getUUID());
        }
    }

    void onCanvasElementSelectedEvent(final @Observes CanvasElementSelectedEvent event) {
        checkNotNull("event",
                     event);
        final String uuid = event.getElementUUID();
        final boolean isSameCtxt = null != canvasHandler
                && canvasHandler.equals(event.getCanvasHandler());
        final boolean isCanvasRoot = null != canvasHandler && uuid.equals(getRootUUID());
        if (isSameCtxt && !isCanvasRoot) {
            select(uuid);
        } else if (isSameCtxt) {
            clearSelection(false);
        }
    }

    void CanvasClearSelectionEvent(final @Observes CanvasClearSelectionEvent event) {
        checkNotNull("event",
                     event);
        if (null != canvasHandler && canvasHandler.equals(event.getCanvasHandler())) {
            this.clearSelection(false);
        }
    }

    private SelectionControl<H, Element> clearSelection(final boolean fireEvent) {
        deselect(selectedElementUUID);
        if (null != getCanvas()) {
            getCanvas().draw();
        }
        if (fireEvent) {
            fireCanvasClear();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private void updateViewShapesState() {
        if (isEnabled()) {
            final List<Shape> shapes = getCanvas().getShapes();
            for (final Shape shape : shapes) {
                final boolean isSelected = null != selectedElementUUID && selectedElementUUID.equals(shape.getUUID());
                if (isSelected) {
                    shape.applyState(ShapeState.SELECTED);
                } else {
                    shape.applyState(ShapeState.NONE);
                }
            }
            // Batch a show operation.
            getCanvas().draw();
        }
    }

    private void fireCanvasClear() {
        clearSelectionEvent.fire(new CanvasClearSelectionEvent(canvasHandler));
    }

    private Canvas getCanvas() {
        return canvasHandler.getCanvas();
    }

    private String getRootUUID() {
        return canvasHandler.getDiagram().getMetadata().getCanvasRootUUID();
    }
}
