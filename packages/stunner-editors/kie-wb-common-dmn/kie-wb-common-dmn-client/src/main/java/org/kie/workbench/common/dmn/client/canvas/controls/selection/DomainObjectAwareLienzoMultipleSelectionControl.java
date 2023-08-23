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

package org.kie.workbench.common.dmn.client.canvas.controls.selection;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoMultipleSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeXOfEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeYOfEvent;

/**
 * Specializes {@link LienzoMultipleSelectionControl} to also support selection of a single {@link DomainObject}.
 * Selection of an {@link Element}, clearance of the canvas or destruction of the underlying session also deselects
 * any {@link DomainObject}.
 * @param <H> {@link AbstractCanvasHandler}
 */
@Dependent
@MultipleSelection
@DMNEditor
public class DomainObjectAwareLienzoMultipleSelectionControl<H extends AbstractCanvasHandler> extends LienzoMultipleSelectionControl<H> {

    private Optional<DomainObject> selectedDomainObject = Optional.empty();
    private final DRDContextMenu drdContextMenu;
    private HandlerRegistration handlerRegistration;

    @Inject
    public DomainObjectAwareLienzoMultipleSelectionControl(final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                                           final Event<CanvasClearSelectionEvent> clearSelectionEvent,
                                                           final DRDContextMenu drdContextMenu) {
        super(canvasSelectionEvent,
              clearSelectionEvent);
        this.drdContextMenu = drdContextMenu;
    }

    @Override
    protected void onEnable(final H canvasHandler) {
        super.onEnable(canvasHandler);

        handlerRegistration = canvasHandler
                .getAbstractCanvas()
                .getView()
                .asWidget()
                .addDomHandler(event -> {
                    event.preventDefault();
                    event.stopPropagation();

                    final boolean selectionIsMultiple = getSelectedItems().size() > 1;
                    final boolean aSelectedShapeHasBeenClicked = isClickedOnShape(canvasHandler, getRelativeXOfEvent(event), getRelativeYOfEvent(event));

                    if (selectionIsMultiple && aSelectedShapeHasBeenClicked) {
                        drdContextMenu.appendContextMenuToTheDOM(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
                        drdContextMenu.show(getSelectedNodes(canvasHandler));
                    }
                }, ContextMenuEvent.getType());
    }

    protected boolean isClickedOnShape(final H canvasHandler, final int canvasX, final int canvasY) {
        return getSelectedNodesStream(canvasHandler)
                .map(Element::getContent)
                .filter(content -> content instanceof View)
                .anyMatch(view -> {
                    final Bounds bounds = ((View) view).getBounds();
                    return canvasX >= bounds.getUpperLeft().getX() && canvasX <= bounds.getLowerRight().getX()
                            && canvasY >= bounds.getUpperLeft().getY() && canvasY <= bounds.getLowerRight().getY();
                });
    }

    protected List<Node<? extends Definition<?>, Edge>> getSelectedNodes(final H canvasHandler) {
        return getSelectedNodesStream(canvasHandler)
                .map(Element::asNode)
                .collect(Collectors.toList());
    }

    protected Stream<? extends Element<? extends Definition<?>>> getSelectedNodesStream(final H canvasHandler) {
        return getSelectedItems().stream()
                .map(uuid -> CanvasLayoutUtils.getElement(canvasHandler, uuid))
                .filter(element -> element instanceof Node);
    }

    @Override
    public Optional<Object> getSelectedItemDefinition() {
        if (selectedDomainObject.isPresent()) {
            return Optional.of(selectedDomainObject.get());
        } else {
            return super.getSelectedItemDefinition();
        }
    }

    @Override
    protected void onSelect(final Collection<String> uuids) {
        selectedDomainObject = Optional.empty();
        super.onSelect(uuids);
    }

    @Override
    public SelectionControl<H, Element> select(final String uuid) {
        selectedDomainObject = Optional.empty();
        return super.select(uuid);
    }

    @Override
    public void clear() {
        selectedDomainObject = Optional.empty();
        super.clear();
    }

    @Override
    protected void onClearSelection() {
        selectedDomainObject = Optional.empty();
        super.onClearSelection();
    }

    @Override
    public void destroy() {
        selectedDomainObject = Optional.empty();
        super.destroy();
    }

    @Override
    protected void onDestroy() {
        selectedDomainObject = Optional.empty();
        handlerRegistration.removeHandler();
        super.onDestroy();
    }

    @Override
    protected void handleCanvasElementSelectedEvent(final CanvasSelectionEvent event) {
        selectedDomainObject = Optional.empty();
        super.handleCanvasElementSelectedEvent(event);
    }

    @Override
    protected void handleCanvasClearSelectionEvent(final CanvasClearSelectionEvent event) {
        selectedDomainObject = Optional.empty();
        super.handleCanvasClearSelectionEvent(event);
        super.onClearSelection();
    }

    void handleDomainObjectSelectedEvent(final @Observes DomainObjectSelectionEvent event) {
        Objects.requireNonNull(event, "Parameter named 'event' should be not null!");
        if (Objects.equals(getCanvasHandler(), event.getCanvasHandler())) {
            selectedDomainObject = Optional.ofNullable(event.getDomainObject());
        }
    }

    private AbstractCanvasHandler getCanvasHandler() {
        return getSelectionControl().getCanvasHandler();
    }
}
