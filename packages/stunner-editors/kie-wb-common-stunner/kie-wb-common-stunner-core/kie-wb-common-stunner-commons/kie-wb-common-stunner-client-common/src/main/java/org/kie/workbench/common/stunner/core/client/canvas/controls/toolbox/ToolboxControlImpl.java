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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;

public class ToolboxControlImpl<F extends ToolboxFactory<AbstractCanvasHandler, Element>>
        extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements ToolboxControl<AbstractCanvasHandler, Element> {

    private final Supplier<List<F>> toolboxFactories;
    private final Toolboxes toolboxes = new Toolboxes();
    private final Predicate<String> showToolboxPredicate;

    public static final Predicate<String> ALWAYS_SHOW_PREDICATE = id -> true;

    ToolboxControlImpl(final Supplier<List<F>> toolboxFactories) {
        this(toolboxFactories,
             ALWAYS_SHOW_PREDICATE);
    }

    ToolboxControlImpl(final Supplier<List<F>> toolboxFactories,
                       final Predicate<String> showToolboxPredicate) {
        this.toolboxFactories = toolboxFactories;
        this.showToolboxPredicate = showToolboxPredicate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
        if (checkNotRegistered(element)
                && supportsToolbox(element)) {
            final Shape shape = canvasHandler.getCanvas().getShape(element.getUUID());
            final HasEventHandlers hasEventHandlers = (HasEventHandlers) shape.getShapeView();
            if (hasEventHandlers.supports(ViewEventType.MOUSE_CLICK)) {
                final MouseClickHandler clickHandler = new MouseClickHandler() {
                    @Override
                    public void handle(final MouseClickEvent event) {
                        if (event.isButtonLeft()) {
                            show(element);
                        }
                    }
                };
                hasEventHandlers.addHandler(ViewEventType.MOUSE_CLICK,
                                            clickHandler);
                registerHandler(element.getUUID(),
                                clickHandler);
            }
        }
    }

    @Override
    public Iterator<Toolbox<?>> getToolboxes(final Element element) {
        load(element);
        return toolboxes.list.iterator();
    }

    @Override
    protected void doDestroy() {
        hideAndDestroyToolboxes();
        super.doDestroy();
    }

    public void destroyToolboxes() {
        toolboxes.destroy();
    }

    public void hideAndDestroyToolboxes() {
        toolboxes.hideAndDestroy();
    }

    @SuppressWarnings("unchecked")
    public ToolboxControl<AbstractCanvasHandler, Element> show(final String uuid) {
        if (showToolboxPredicate.test(uuid)) {
            final Node node = canvasHandler.getGraphIndex().getNode(uuid);
            // Only nodes have toolbox/es, discard processing for edges.
            if (null != node) {
                return show(node);
            }
        }
        return this;
    }

    public ToolboxControl<AbstractCanvasHandler, Element> show(final Element element) {
        if (showToolboxPredicate.test(element.getUUID())) {
            if (!toolboxes.isTheElement(element)) {
                load(element);
            }
            toolboxes.show();
        }
        return this;
    }

    public AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    public boolean isActive(final String uuid) {
        return null != toolboxes.uuid &&
                toolboxes.uuid.equals(uuid);
    }

    @SuppressWarnings("unchecked")
    private void load(final Element element) {
        if (isRegistered(element)) {
            // Actually only nodes are being registered.
            load((Node<?, Edge>) element);
        } else {
            destroyToolboxes();
        }
    }

    private void load(final Node<?, Edge> node) {
        this.toolboxes.load(node);
    }

    private boolean supportsToolbox(final Element element) {
        return element instanceof Node;
    }

    private class Toolboxes {

        private String uuid;
        private List<Toolbox<?>> list;

        public Toolboxes() {
            this.uuid = null;
            this.list = Collections.emptyList();
        }

        public Toolboxes load(final Node<?, Edge> element) {
            destroy();
            if (!isCanvasRoot(element)) {
                this.uuid = element.getUUID();
                this.list =
                        toolboxFactories.get().stream()
                                .map(factory -> factory.build(canvasHandler,
                                                              element))
                                .flatMap(factory -> factory.map(Stream::of)
                                        .orElseGet(Stream::empty))
                                .collect(Collectors.toList());
            }
            return this;
        }

        public void show() {
            list.forEach(Toolbox::show);
        }

        public void hide() {
            list.forEach(Toolbox::hide);
        }

        public void hideAndDestroy() {
            list.forEach(Toolbox::hideAndDestroy);
            clear();
        }

        public void destroy() {
            list.forEach(Toolbox::destroy);
            clear();
        }

        private void clear() {
            list.clear();
            uuid = null;
        }

        public boolean isTheElement(final String _uuid) {
            return null != uuid && uuid.equals(_uuid);
        }

        public boolean isTheElement(final Element<?> e) {
            return isTheElement(e.getUUID());
        }

        private boolean isCanvasRoot(final Element<?> element) {
            return element.getUUID().equals(canvasHandler.getDiagram().getMetadata().getCanvasRootUUID());
        }
    }
}
