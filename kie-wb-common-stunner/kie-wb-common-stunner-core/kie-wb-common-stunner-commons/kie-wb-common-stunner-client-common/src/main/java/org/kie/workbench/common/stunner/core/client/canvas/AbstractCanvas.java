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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.HasCanvasListeners;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLoadingObserver;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.util.UUID;

/**
 * For Lienzo's based Canvas.
 */
public abstract class AbstractCanvas<V extends AbstractCanvas.View>
        implements Canvas<Shape>,
                   HasCanvasListeners<CanvasShapeListener> {

    private static Logger LOGGER = Logger.getLogger(AbstractCanvas.class.getName());

    public enum Cursors {
        AUTO,
        MOVE,
        POINTER,
        TEXT,
        NOT_ALLOWED,
        WAIT,
        CROSSHAIR;
    }

    public interface View<P> extends IsWidget {

        View show(final P panel,
                  final int width,
                  final int height,
                  final Layer layer);

        View add(final IsWidget widget);

        View remove(final IsWidget widget);

        View addShape(final ShapeView<?> shapeView);

        View removeShape(final ShapeView<?> shapeView);

        View addChildShape(final ShapeView<?> parent,
                           final ShapeView<?> child);

        View removeChildShape(final ShapeView<?> parent,
                              final ShapeView<?> child);

        View dock(final ShapeView<?> parent,
                  final ShapeView<?> child);

        View undock(final ShapeView<?> childParent,
                    final ShapeView<?> child);

        double getAbsoluteX();

        double getAbsoluteY();

        int getWidth();

        int getHeight();

        View setGrid(final CanvasGrid grid);

        View setCursor(final Cursors cursor);

        View setDecoratorStrokeWidth(final double width);

        View setDecoratorStrokeAlpha(final double alpha);

        View setDecoratorStrokeColor(final String color);

        Layer getLayer();

        View clear();

        void destroy();
    }

    protected Layer layer;
    protected V view;
    protected CanvasGrid grid;
    protected Event<CanvasClearEvent> canvasClearEvent;
    protected Event<CanvasShapeAddedEvent> canvasShapeAddedEvent;
    protected Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent;
    protected Event<CanvasDrawnEvent> canvasDrawnEvent;
    protected Event<CanvasFocusedEvent> canvasFocusedEvent;

    protected final Map<String, Shape> shapes = new HashMap<>();
    protected final List<CanvasShapeListener> listeners = new LinkedList<>();
    private final CanvasLoadingObserver loadingObserver = new CanvasLoadingObserver();
    private final String uuid;

    protected AbstractCanvas(final Event<CanvasClearEvent> canvasClearEvent,
                             final Event<CanvasShapeAddedEvent> canvasShapeAddedEvent,
                             final Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent,
                             final Event<CanvasDrawnEvent> canvasDrawnEvent,
                             final Event<CanvasFocusedEvent> canvasFocusedEvent,
                             final Layer layer,
                             final V view) {
        this.canvasClearEvent = canvasClearEvent;
        this.canvasShapeAddedEvent = canvasShapeAddedEvent;
        this.canvasShapeRemovedEvent = canvasShapeRemovedEvent;
        this.canvasDrawnEvent = canvasDrawnEvent;
        this.canvasFocusedEvent = canvasFocusedEvent;
        this.layer = layer;
        this.view = view;
        this.uuid = UUID.uuid();
    }

    @SuppressWarnings("unchecked")
    protected <P> void show(final P panel,
                            final int width,
                            final int height,
                            final Layer layer) {
        // Show the canvas layer on using the given panel instance.
        view.show(panel,
                  width,
                  height,
                  layer);
        // TODO: Review this.
        //       If adding this handler, the SelectionControl for this layer never fires,
        //       so it seems it's not registering fine more than one click event handler.
        /*final MouseClickHandler clickHandler = new MouseClickHandler() {

            @Override
            public void handle( final MouseClickEvent event ) {
                canvasFocusedEvent.fire( new CanvasFocusedEvent( AbstractCanvas.this ) );

            }

        };
        layer.addHandler( ViewEventType.MOUSE_CLICK, clickHandler );*/
    }

    public abstract void addControl(final IsWidget controlView);

    public abstract void deleteControl(final IsWidget controlView);

    @Override
    public Collection<Shape> getShapes() {
        return shapes.values();
    }

    public Shape getShape(final String uuid) {
        return shapes.get(uuid);
    }

    @SuppressWarnings("unchecked")
    public Canvas addChildShape(final Shape parent,
                                final Shape child) {
        getView().addChildShape(parent.getShapeView(),
                                child.getShapeView());
        log(Level.FINE,
            "Adding child [" + child.getUUID() + "] into parent [" + parent.getUUID() + "]");
        return this;
    }

    @SuppressWarnings("unchecked")
    public Canvas deleteChildShape(final Shape parent,
                                   final Shape child) {
        getView().removeChildShape(parent.getShapeView(),
                                   child.getShapeView());
        log(Level.FINE,
            "Deleting child [" + child.getUUID() + "] from parent [" + parent.getUUID() + "]");
        return this;
    }

    @SuppressWarnings("unchecked")
    public Canvas dock(final Shape parent,
                       final Shape child) {
        getView().dock(parent.getShapeView(),
                       child.getShapeView());
        log(Level.FINE,
            "Docking child [" + child.getUUID() + "] into parent [" + parent.getUUID() + "]");
        return this;
    }

    @SuppressWarnings("unchecked")
    public Canvas undock(final Shape target,
                         final Shape child) {
        getView().undock(target.getShapeView(),
                         child.getShapeView());
        log(Level.FINE,
            "Undocking child [" + child.getUUID() + "] from parent [" + target.getUUID() + "]");
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
        view.addShape(shape.getShapeView());
        return this;
    }

    public double getAbsoluteX() {
        return view.getAbsoluteX();
    }

    public double getAbsoluteY() {
        return view.getAbsoluteY();
    }

    public Canvas setGrid(final CanvasGrid grid) {
        this.grid = grid;
        view.setGrid(grid);
        return this;
    }

    public CanvasGrid getGrid() {
        return grid;
    }

    @Override
    public Layer getLayer() {
        return layer;
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
        view.removeShape(shape.getShapeView());
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
        view.clear();
        return this;
    }

    protected void clearShapes() {
        if (!shapes.isEmpty()) {
            shapes.values().stream().collect(Collectors.toList()).forEach(this::deleteShape);
            shapes.clear();
        }
    }

    @Override
    public void destroy() {
        clear(false);
        listeners.clear();
        view.destroy();
        layer.destroy();
        layer = null;
        view = null;
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
        return view.getWidth();
    }

    @Override
    public int getHeight() {
        return view.getHeight();
    }

    public V getView() {
        return view;
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

    public void setLoadingObserverCallback(final CanvasLoadingObserver.Callback loadingObserverCallback) {
        this.loadingObserver.setLoadingObserverCallback(loadingObserverCallback);
    }

    public void loadingStarted() {
        this.loadingObserver.loadingStarted();
    }

    public void loadingCompleted() {
        this.loadingObserver.loadingCompleted();
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
