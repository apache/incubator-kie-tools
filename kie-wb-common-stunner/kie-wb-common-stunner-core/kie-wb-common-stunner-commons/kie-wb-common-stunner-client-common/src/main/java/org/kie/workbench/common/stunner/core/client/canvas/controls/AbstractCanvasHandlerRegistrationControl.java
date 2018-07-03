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

package org.kie.workbench.common.stunner.core.client.canvas.controls;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.graph.Element;

public abstract class AbstractCanvasHandlerRegistrationControl<H extends AbstractCanvasHandler>
        extends AbstractCanvasHandlerControl<H>
        implements CanvasRegistrationControl<H, Element> {

    private static Logger LOGGER = Logger.getLogger(AbstractCanvasHandlerRegistrationControl.class.getName());

    private final Map<String, ViewHandler<?>> handlers = new HashMap<>();
    private final Map<String, ViewHandler<?>> disabledHandlers = new HashMap<>();

    public void update(final Element element) {
        // Do nothing by default.
    }

    protected void registerHandler(final String uuid,
                                   final ViewHandler<?> handler) {
        handlers.put(uuid,
                     handler);
    }

    @Override
    protected void doInit() {
        doOnAllHandlers(enableEventHandler());
    }

    @Override
    public void clear() {
        doClear();
    }

    private void doOnAllHandlers(Consumer<Shape> handlerFunction) {
        if (!handlers.isEmpty() && Objects.nonNull(canvasHandler)) {
            handlers.keySet().stream()
                    .filter(this::isRegistered)
                    .map(uuid -> canvasHandler.getCanvas().getShape(uuid))
                    .filter(Objects::nonNull)
                    .filter(shape -> shape.getShapeView() instanceof HasEventHandlers)
                    .forEach(handlerFunction);
        }
    }

    private Consumer<Shape> enableEventHandler() {
        return shape -> {
            ViewHandler<?> eventHandler = disabledHandlers.get(shape.getUUID());
            ((HasEventHandlers) shape.getShapeView()).addHandler(eventHandler.getType(), eventHandler);
            disabledHandlers.remove(shape.getUUID());
        };
    }

    protected void doClear() {
        new HashSet<>(handlers.keySet())
                .stream()
                .forEach(this::deregister);
        handlers.clear();
        new HashSet<>(disabledHandlers.keySet())
                .stream()
                .forEach(this::deregister);
        disabledHandlers.clear();
    }

    @Override
    protected void doDestroy() {
        doClear();
    }

    @Override
    public void deregister(final Element element) {
        deregister(element.getUUID());
    }

    public boolean isRegistered(final Element element) {
        return isRegistered(element.getUUID());
    }

    protected Set<String> getRegisteredElements() {
        return handlers.keySet();
    }

    protected boolean isRegistered(final String uuid) {
        return handlers.containsKey(uuid);
    }

    protected void deregister(final String uuid) {
        if (isRegistered(uuid)) {
            final Shape shape = canvasHandler.getCanvas().getShape(uuid);
            final ViewHandler<?> handler = handlers.get(uuid);
            doDeregisterHandler(shape,
                                handler);
        }
    }

    protected boolean checkNotRegistered(final Element element) {
        if (isRegistered(element)) {
            LOGGER.log(Level.WARNING,
                       "Trying to register element [" + element.getUUID() + "] again into " +
                               "the control for type [" + this.getClass().getName() + "]");
            return false;
        }
        return true;
    }

    protected boolean checkEventContext(final AbstractCanvasHandlerEvent canvasHandlerEvent) {
        final CanvasHandler _canvasHandler = canvasHandlerEvent.getCanvasHandler();
        return canvasHandler != null && canvasHandler.equals(_canvasHandler);
    }

    private void doDeregisterHandler(final Shape shape,
                                     final ViewHandler<?> handler) {
        if (null != shape && null != handler) {
            final HasEventHandlers hasEventHandlers = (HasEventHandlers) shape.getShapeView();
            hasEventHandlers.removeHandler(handler);
            handlers.remove(shape.getUUID());
            disabledHandlers.remove(shape.getUUID());
        }
    }
}