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


package org.kie.workbench.common.stunner.core.client.canvas.controls;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    private final Map<String, Set<ViewHandler<?>>> handlers;
    private final Map<String, Set<ViewHandler<?>>> disabledHandlers;

    public AbstractCanvasHandlerRegistrationControl() {
        handlers = new HashMap<>();
        disabledHandlers = new HashMap<>();
    }

    public void update(final Element element) {
        // Do nothing by default.
    }

    protected void registerHandler(final String uuid,
                                   final ViewHandler<?> handler) {
        handlers.putIfAbsent(uuid, new HashSet<>());
        handlers.get(uuid).add(handler);
    }

    @Override
    protected void doInit() {
        enableAllHandlers();
    }

    @Override
    public void clear() {
        doClear();
    }

    private void enableAllHandlers() {
        if (!handlers.isEmpty() && Objects.nonNull(canvasHandler)) {
            handlers.keySet().stream()
                    .filter(this::isRegistered)
                    .map(uuid -> canvasHandler.getCanvas().getShape(uuid))
                    .filter(Objects::nonNull)
                    .filter(shape -> shape.getShapeView() instanceof HasEventHandlers)
                    .forEach(shape -> disabledHandlers.get(shape.getUUID())
                            .stream()
                            .forEach(eventHandler -> {
                                ((HasEventHandlers) shape.getShapeView()).addHandler(eventHandler.getType(), eventHandler);
                                handlers.get(shape.getUUID()).add(eventHandler);
                            })
                    );

            disabledHandlers.values().stream().forEach(Set::clear);
            disabledHandlers.clear();
        }
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
        return handlers.entrySet()
                .stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .filter(entry -> !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    protected boolean isRegistered(final String uuid) {
        return handlers.containsKey(uuid);
    }

    protected void deregister(final String uuid) {
        if (isRegistered(uuid)) {
            final Shape shape = canvasHandler.getCanvas().getShape(uuid);
            handlers.get(uuid).stream().forEach(handler -> doDeregisterHandler(shape, handler));
            handlers.get(uuid).clear();
            handlers.remove(uuid);
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
            disabledHandlers.putIfAbsent(shape.getUUID(), new HashSet<>());
            disabledHandlers.get(shape.getUUID()).add(handler);
        }
    }
}