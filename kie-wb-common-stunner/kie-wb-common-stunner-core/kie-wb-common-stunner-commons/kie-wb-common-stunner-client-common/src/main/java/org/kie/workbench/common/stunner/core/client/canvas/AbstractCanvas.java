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

package org.kie.workbench.common.stunner.core.client.canvas;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.HasCanvasListeners;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.util.UUID;

public abstract class AbstractCanvas<V extends AbstractCanvas.CanvasView>
        implements Canvas<Shape>,
                   HasEventHandlers<AbstractCanvas<V>, Shape<?>>,
                   HasCanvasListeners<CanvasShapeListener> {

    public enum Cursors {
        AUTO,
        MOVE,
        POINTER,
        TEXT,
        NOT_ALLOWED,
        WAIT,
        CROSSHAIR;
    }

    public interface CanvasView<C extends CanvasView> extends IsWidget {

        C initialize(CanvasPanel panel,
                     CanvasSettings canvasSettings);

        C add(ShapeView<?> shape);

        C delete(ShapeView<?> shape);

        C addChild(ShapeView<?> parent,
                   ShapeView<?> child);

        C deleteChild(final ShapeView<?> parent,
                      final ShapeView<?> child);

        C dock(final ShapeView<?> parent,
               final ShapeView<?> child);

        C undock(final ShapeView<?> childParent,
                 final ShapeView<?> child);

        C setGrid(final CanvasGrid grid);

        C setCursor(final Cursors cursor);

        C clear();

        Transform getTransform();

        Point2D getAbsoluteLocation();

        int getWidth();

        int getHeight();

        void destroy();
    }

    protected CanvasGrid grid;
    protected Event<CanvasClearEvent> canvasClearEvent;
    protected Event<CanvasShapeAddedEvent> canvasShapeAddedEvent;
    protected Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent;
    protected Event<CanvasDrawnEvent> canvasDrawnEvent;
    protected Event<CanvasFocusedEvent> canvasFocusedEvent;

    private final String uuid;
    protected final Map<String, Shape> shapes = new HashMap<>();
    protected final List<CanvasShapeListener> listeners = new LinkedList<>();

    protected AbstractCanvas(final Event<CanvasClearEvent> canvasClearEvent,
                             final Event<CanvasShapeAddedEvent> canvasShapeAddedEvent,
                             final Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent,
                             final Event<CanvasDrawnEvent> canvasDrawnEvent,
                             final Event<CanvasFocusedEvent> canvasFocusedEvent) {
        this.canvasClearEvent = canvasClearEvent;
        this.canvasShapeAddedEvent = canvasShapeAddedEvent;
        this.canvasShapeRemovedEvent = canvasShapeRemovedEvent;
        this.canvasDrawnEvent = canvasDrawnEvent;
        this.canvasFocusedEvent = canvasFocusedEvent;
        this.uuid = UUID.uuid();
    }

    public abstract V getView();

    public AbstractCanvas<V> initialize(final CanvasPanel panel,
                                        final CanvasSettings settings) {
        onAfterDraw(AbstractCanvas.this::afterDrawCanvas);
        getView().initialize(panel, settings);
        return this;
    }

    @Override
    public Collection<Shape> getShapes() {
        return shapes.values();
    }

    public Shape getShape(final String uuid) {
        return shapes.get(uuid);
    }

    protected abstract void addChild(final Shape shape);

    @SuppressWarnings("unchecked")
    public Canvas addChild(final Shape parent,
                           final Shape child) {
        getView().addChild(parent.getShapeView(),
                           child.getShapeView());
        return this;
    }

    protected abstract void deleteChild(final Shape shape);

    @SuppressWarnings("unchecked")
    public Canvas deleteChild(final Shape parent,
                              final Shape child) {
        getView().deleteChild(parent.getShapeView(),
                              child.getShapeView());
        return this;
    }

    @SuppressWarnings("unchecked")
    public Canvas dock(final Shape parent,
                       final Shape child) {
        getView().dock(parent.getShapeView(),
                       child.getShapeView());
        return this;
    }

    @SuppressWarnings("unchecked")
    public Canvas undock(final Shape target,
                         final Shape child) {
        getView().undock(target.getShapeView(),
                         child.getShapeView());
        return this;
    }

    @Override
    public Canvas addShape(final Shape shape) {
        shapes.computeIfAbsent(shape.getUUID(), (v) -> {
            addTransientShape(shape);
            fireCanvasShapeAdded(shape);
            canvasShapeAddedEvent.fire(new CanvasShapeAddedEvent(this, shape));
            return shape;
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    public Canvas addTransientShape(final Shape shape) {
        if (shape.getUUID() == null) {
            shape.setUUID(UUID.uuid());
        }
        shape.getShapeView().setUUID(shape.getUUID());
        getView().add(shape.getShapeView());
        return this;
    }

    @Override
    public Canvas deleteShape(final Shape shape) {
        deleteTransientShape(shape);
        fireCanvasShapeRemoved(shape);
        shapes.remove(shape.getUUID());
        canvasShapeRemovedEvent.fire(new CanvasShapeRemovedEvent(this,
                                                                 shape));
        return this;
    }

    @SuppressWarnings("unchecked")
    public Canvas deleteTransientShape(final Shape shape) {
        getView().delete(shape.getShapeView());
        shape.destroy();
        return this;
    }

    public AbstractCanvas clear() {
        return clear(true);
    }

    private AbstractCanvas clear(final boolean fireEvents) {
        clearShapes();

        fireCanvasClear();
        if (fireEvents) {
            canvasClearEvent.fire(new CanvasClearEvent(this));
        }
        getView().clear();
        return this;
    }

    protected void clearShapes() {
        if (!shapes.isEmpty()) {
            shapes.values().stream().collect(Collectors.toList()).forEach(this::deleteShape);
            shapes.clear();
        }
    }

    public Canvas setGrid(final CanvasGrid grid) {
        this.grid = grid;
        getView().setGrid(grid);
        return this;
    }

    public CanvasGrid getGrid() {
        return grid;
    }

    public Point2D getAbsoluteLocation() {
        return getView().getAbsoluteLocation();
    }

    @Override
    public Transform getTransform() {
        return getView().getTransform();
    }

    @Override
    public void destroy() {
        clear(false);
        listeners.clear();
        getView().destroy();
    }

    @Override
    public HasCanvasListeners<CanvasShapeListener> addRegistrationListener(final CanvasShapeListener instance) {
        listeners.add(instance);
        return this;
    }

    @Override
    public HasCanvasListeners<CanvasShapeListener> removeRegistrationListener(final CanvasShapeListener instance) {
        listeners.remove(instance);
        return this;
    }

    @Override
    public HasCanvasListeners<CanvasShapeListener> clearRegistrationListeners() {
        listeners.clear();
        return this;
    }

    protected void fireCanvasShapeAdded(final Shape shape) {
        for (final CanvasShapeListener instance : listeners) {
            instance.register(shape);
        }
    }

    protected void fireCanvasShapeRemoved(final Shape shape) {
        for (final CanvasShapeListener instance : listeners) {
            instance.deregister(shape);
        }
    }

    protected void fireCanvasClear() {
        for (final CanvasShapeListener instance : listeners) {
            instance.clear();
        }
    }

    protected void afterDrawCanvas() {
    }

    @Override
    public int getWidth() {
        return getView().getWidth();
    }

    @Override
    public int getHeight() {
        return getView().getHeight();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractCanvas)) {
            return false;
        }
        AbstractCanvas that = (AbstractCanvas) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid == null ? 0 : ~~uuid.hashCode();
    }
}
