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


package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import jakarta.enterprise.event.Observes;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.AbstractCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.graph.Element;

public abstract class AbstractToolboxControl
        implements ToolboxControl<AbstractCanvasHandler, Element> {

    private final ToolboxControlImpl<ActionsToolboxFactory> toolboxControl;
    private final SingleItemSelectedShowPredicate toolboxShowPredicate;

    // It makes the toolbox appear only if single selection.
    private static class SingleItemSelectedShowPredicate implements Predicate<String> {

        private String id;
        private int count;

        @Override
        public boolean test(String s) {
            return (null == id && count == 0) ||
                    (null != id && count == 1 && id.equals(s)) ||
                    (null != id && count > 1);
        }
    }

    protected abstract List<ActionsToolboxFactory> getFactories();

    public AbstractToolboxControl() {
        this.toolboxShowPredicate = new SingleItemSelectedShowPredicate();
        this.toolboxControl = new ToolboxControlImpl<>(this::getFactories,
                                                       toolboxShowPredicate);
    }

    AbstractToolboxControl(final ToolboxControlImpl<ActionsToolboxFactory> toolboxControl) {
        this.toolboxShowPredicate = new SingleItemSelectedShowPredicate();
        this.toolboxControl = toolboxControl;
    }

    @Override
    public void init(final AbstractCanvasHandler context) {
        toolboxControl.init(context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
        toolboxControl.register(element);
    }

    @Override
    public void deregister(final Element element) {
        toolboxControl.deregister(element);
    }

    @Override
    public Iterator<Toolbox<?>> getToolboxes(final Element element) {
        return toolboxControl.getToolboxes(element);
    }

    void onCanvasSelectionEvent(final @Observes CanvasSelectionEvent event) {
        checkNotNull("event", event);
        handleCanvasSelectionEvent(event);
    }

    void onCanvasClearSelectionEvent(final @Observes CanvasClearSelectionEvent event) {
        checkNotNull("event", event);
        handleCanvasClearSelectionEvent(event);
    }

    void onCanvasShapeRemovedEvent(final @Observes CanvasShapeRemovedEvent event) {
        checkNotNull("event", event);
        handleCanvasShapeRemovedEvent(event);
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    protected void handleCanvasSelectionEvent(final CanvasSelectionEvent event) {
        if (checkEventContext(event)) {
            if (1 == event.getIdentifiers().size()) {
                final String uuid = event.getIdentifiers().iterator().next();

                // Call show method without evaluating if it is a new single selection or same element
                // selection after multiple selection causes DOM element leaks in DMN editor
                if (!uuid.equals(toolboxShowPredicate.id) || toolboxShowPredicate.count > 1) {
                    show(uuid);
                }
            } else {
                showMultiple(event.getIdentifiers());
            }
        }
    }

    protected void handleCanvasClearSelectionEvent(final CanvasClearSelectionEvent event) {
        if (checkEventContext(event)) {
            toolboxControl.destroyToolboxes();
            clear();
        }
    }

    protected void handleCanvasShapeRemovedEvent(final CanvasShapeRemovedEvent event) {
        if (checkEventContext(event)) {
            clear();
        }
    }

    private void show(final String uuid) {
        clear();
        toolboxShowPredicate.id = uuid;
        toolboxShowPredicate.count = 1;
        toolboxControl.show(uuid);
    }

    private void showMultiple(final Collection<String> ids) {
        clear();
        toolboxShowPredicate.id = ids.iterator().next();
        toolboxShowPredicate.count = ids.size();
    }

    public void clear() {
        toolboxControl.hideAndDestroyToolboxes();
        toolboxShowPredicate.id = null;
        toolboxShowPredicate.count = 0;
    }

    public void destroy() {
        clear();
        toolboxControl.destroy();
    }

    private boolean checkEventContext(final AbstractCanvasHandlerEvent canvasHandlerEvent) {
        final CanvasHandler _canvasHandler = canvasHandlerEvent.getCanvasHandler();
        return toolboxControl.getCanvasHandler() != null
                && toolboxControl.getCanvasHandler().equals(_canvasHandler);
    }

    private boolean checkEventContext(final AbstractCanvasShapeEvent canvasShapeEvent) {
        return toolboxControl.getCanvasHandler() != null
                && toolboxControl.getCanvasHandler().getCanvas().equals(canvasShapeEvent.getCanvas())
                && toolboxControl.isActive(canvasShapeEvent.getShape().getUUID());
    }
}
