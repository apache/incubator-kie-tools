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


package org.kie.workbench.common.stunner.core.client.canvas.controls.select;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public final class MapSelectionControl<H extends AbstractCanvasHandler>
        extends AbstractCanvasHandlerControl<H>
        implements SelectionControl<H, Element>,
                   CanvasRegistrationControl<H, Element> {

    private final Consumer<CanvasSelectionEvent> selectionEventConsumer;
    private final Consumer<CanvasClearSelectionEvent> clearSelectionEventConsumer;
    private MouseClickHandler layerClickHandler;
    private final Map<String, Boolean> items = new HashMap<>();
    private boolean readonly;

    public static <H extends AbstractCanvasHandler> MapSelectionControl<H> build(final Consumer<CanvasSelectionEvent> selectionEventConsumer,
                                                                                 final Consumer<CanvasClearSelectionEvent> clearSelectionEventConsumer) {
        return new MapSelectionControl<>(selectionEventConsumer,
                                         clearSelectionEventConsumer);
    }

    // Do not expose by default to the IOC container.
    MapSelectionControl(final Consumer<CanvasSelectionEvent> selectionEventConsumer,
                        final Consumer<CanvasClearSelectionEvent> clearSelectionEventConsumer) {
        this.selectionEventConsumer = selectionEventConsumer;
        this.clearSelectionEventConsumer = clearSelectionEventConsumer;
    }

    @Override
    protected void doInit() {
        // Click handler for the canvas area - cleans current selection, if any.
        final MouseClickHandler clickHandler = new MouseClickHandler() {
            @Override
            public void handle(final MouseClickEvent event) {
                if (event.isButtonLeft() || event.isButtonRight()) {
                    clearSelection(false);
                    final String canvasRootUUID = getRootUUID();
                    fireCanvasClear();
                    if (null != canvasRootUUID) {
                        selectionEventConsumer.accept(new CanvasSelectionEvent(canvasHandler,
                                                                               canvasRootUUID));
                    }
                }
            }
        };
        getCanvas().addHandler(ViewEventType.MOUSE_CLICK,
                               clickHandler);
        this.layerClickHandler = clickHandler;
    }

    @Override
    public void clear() {
        clearSelection(true);
    }

    @Override
    public void register(final Element element) {
        /** Conditions to be met:
         * - element is not registered
         * - element has visible representation
         */
        if (!itemsRegistered().test(element.getUUID())
                && element.getContent() instanceof View) {
            items.put(element.getUUID(),
                      false);
        }
    }

    @Override
    public void deregister(final Element element) {
        final String uuid = element.getUUID();
        deselect(uuid);
        items.remove(uuid);
    }

    @Override
    public SelectionControl<H, Element> select(final String uuid) {
        return select(Collections.singletonList(uuid));
    }

    @Override
    public SelectionControl<H, Element> addSelection(String uuid) {
        return addSelection(Collections.singletonList(uuid));
    }

    @Override
    public SelectionControl<H, Element> deselect(final String uuid) {
        return deselect(Collections.singletonList(uuid));
    }

    @Override
    public boolean isSelected(final Element element) {
        return isSelected(element.getUUID());
    }

    @Override
    public Collection<String> getSelectedItems() {
        return items.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Collection<String> getUnselectedItems() {
        return items.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public SelectionControl<H, Element> clearSelection() {
        return clearSelection(true);
    }

    @Override
    public Optional<Object> getSelectedItemDefinition() {
        String selectedItemUUID = null;
        final Collection<String> selectedItems = getSelectedItems();
        if (null != selectedItems && !selectedItems.isEmpty()) {
            selectedItemUUID = selectedItems.iterator().next();
        }
        if (Objects.isNull(selectedItemUUID)) {
            final AbstractCanvasHandler canvasHandler = getCanvasHandler();
            if (!Objects.isNull(canvasHandler)) {
                final Diagram<?, ?> diagram = getCanvasHandler().getDiagram();
                if (!Objects.isNull(diagram)) {
                    final String cRoot = diagram.getMetadata().getCanvasRootUUID();
                    // Check if there exist any canvas root element.
                    if (!StringUtils.isEmpty(cRoot)) {
                        selectedItemUUID = cRoot;
                    }
                }
            }
        }
        if (!Objects.isNull(selectedItemUUID)) {
            final Element<? extends Definition<?>> element = CanvasLayoutUtils.getElement(getCanvasHandler(),
                                                                                          selectedItemUUID);
            return Optional.ofNullable(element);
        } else {
            return Optional.empty();
        }
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public SelectionControl<H, Element> select(final Collection<String> uuids) {
        addSelection(uuids);
        fireSelectedItemsEvent();
        return this;
    }

    public SelectionControl<H, Element> addSelection(final Collection<String> uuids) {
        uuids.stream()
                .filter(itemsRegistered())
                .forEach(uuid -> items.put(uuid, true));
        updateViewShapesState(getSelectedItems());
        return this;
    }

    public SelectionControl<H, Element> deselect(final Collection<String> uuids) {
        uuids.stream()
                .filter(itemsRegistered())
                .forEach(uuid -> items.put(uuid, false));
        updateViewShapesState(getUnselectedItems());
        return this;
    }

    public boolean isSelected(final String uuid) {
        return itemsRegistered().test(uuid) && items.get(uuid);
    }

    private SelectionControl<H, Element> clearSelection(final boolean fireEvent) {
        deselect(getSelectedItems());
        if (fireEvent) {
            fireCanvasClear();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private void updateViewShapesState(Collection<String> uuids) {
        uuids.stream()
                .map(uuid -> getCanvas().getShape(uuid))
                .filter(Objects::nonNull)
                .forEach(shape -> {
                    final boolean isSelected = isSelected(shape.getUUID());
                    if (isSelected && isReadonly()) {
                        shape.applyState(ShapeState.HIGHLIGHT);
                    } else if (isSelected) {
                        shape.applyState(ShapeState.SELECTED);
                    } else {
                        shape.applyState(ShapeState.NONE);
                    }
                });
        getCanvas().focus();
    }

    @Override
    protected void doDestroy() {
        if (null != layerClickHandler) {
            getCanvas().removeHandler(layerClickHandler);
            this.layerClickHandler = null;
        }
        items.clear();
    }

    public void onShapeRemoved(final CanvasShapeRemovedEvent shapeRemovedEvent) {
        checkNotNull("shapeRemovedEvent", shapeRemovedEvent);
        if (null == canvasHandler) {
            return;
        }
        if (getCanvas().equals(shapeRemovedEvent.getCanvas())) {
            items.remove(shapeRemovedEvent.getShape().getUUID());
        }
    }

    void onCanvasElementSelected(final CanvasSelectionEvent event) {
        checkNotNull("event", event);
        if (null == canvasHandler) {
            return;
        }

        final boolean isSameCtxt = canvasHandler.equals(event.getCanvasHandler());
        final boolean isSingleSelection = event.getIdentifiers().size() == 1;
        final boolean isCanvasRoot = isSingleSelection &&
                event.getIdentifiers().iterator().next().equals(getRootUUID());
        final boolean equals = items.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .anyMatch(uuid -> event.getIdentifiers().contains(uuid));
        if (isSameCtxt && !isCanvasRoot && !equals) {
            this.clearSelection(false);
            select(event.getIdentifiers());
        }
    }

    public void onCanvasClearSelection(final CanvasClearSelectionEvent event) {
        checkNotNull("event", event);
        if (null != canvasHandler
                && canvasHandler.equals(event.getCanvasHandler())
                && !getSelectedItems().isEmpty()) {
            this.clearSelection(false);
        }
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    public Predicate<String> itemsRegistered() {
        return items::containsKey;
    }

    public AbstractCanvas getCanvas() {
        return canvasHandler.getAbstractCanvas();
    }

    protected String getRootUUID() {
        return canvasHandler.getDiagram().getMetadata().getCanvasRootUUID();
    }

    private void fireSelectedItemsEvent() {
        final Collection<String> selectedItems = getSelectedItems();
        if (!selectedItems.isEmpty()) {
            selectionEventConsumer.accept(new CanvasSelectionEvent(canvasHandler,
                                                                   selectedItems));
        }
    }

    private void fireCanvasClear() {
        clearSelectionEventConsumer.accept(new CanvasClearSelectionEvent(canvasHandler));
    }
}
