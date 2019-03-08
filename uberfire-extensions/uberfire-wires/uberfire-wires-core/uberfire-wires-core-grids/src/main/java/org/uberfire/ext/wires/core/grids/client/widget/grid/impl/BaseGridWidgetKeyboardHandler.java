/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperation.TriStateBoolean;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * KeyDownHandler for keyboard operations. Keyboard operations supported:
 * <ul>
 * <li>Clearing cell content with the DELETE key</li>
 * <li>Edit a cell content with the ENTER key</li>
 * <li>Keyboard navigation with the CURSOR keys</li>
 * <li>Move to top-left cell with HOME key</li>
 * <li>Move to bottom-right cell with END key</li>
 * </ul>
 * All operations keep the selection origin in view, i.e. the @{link Viewport} is
 * translated to keep the cell in view if necessary.
 */
public class BaseGridWidgetKeyboardHandler implements KeyDownHandler {

    protected GridLayer gridLayer;

    private Set<KeyboardOperation> operations = new HashSet<>();

    public BaseGridWidgetKeyboardHandler(final GridLayer gridLayer) {
        this.gridLayer = Objects.requireNonNull(gridLayer, "gridLayer");
    }

    public void addOperation(final KeyboardOperation... operations) {
        for (KeyboardOperation operation : operations) {
            this.operations.add(Objects.requireNonNull(operation, "operation"));
        }
    }

    @Override
    public void onKeyDown(final KeyDownEvent event) {
        final GridWidget selectedGridWidget = getSelectedGridWidget();
        if (selectedGridWidget == null) {
            return;
        }

        final KeyboardOperation operation = getOperation(event);
        if (operation == null) {
            return;
        }

        if (!operation.isExecutable(selectedGridWidget)) {
            return;
        }

        flushDOMElements(selectedGridWidget);

        final boolean redraw = operation.perform(selectedGridWidget,
                                                 event.isShiftKeyDown(),
                                                 event.isControlKeyDown());

        event.preventDefault();
        event.stopPropagation();

        if (redraw) {
            gridLayer.draw();
        }
    }

    private KeyboardOperation getOperation(final KeyDownEvent event) {
        final int keyCode = event.getNativeKeyCode();
        final boolean isShiftKeyDown = event.isShiftKeyDown();
        final boolean isControlKeyDown = event.isControlKeyDown();
        for (KeyboardOperation operation : operations) {
            if (operation.getKeyCode() == keyCode) {
                if (keyDownStateMatches(isShiftKeyDown,
                                        operation.isShiftKeyDown()) && keyDownStateMatches(isControlKeyDown,
                                                                                           operation.isControlKeyDown())) {
                    return operation;
                }
            }
        }
        return null;
    }

    private boolean keyDownStateMatches(final boolean actualKeyDownState,
                                        final TriStateBoolean requiredKeyDownState) {
        if (actualKeyDownState && (requiredKeyDownState == TriStateBoolean.TRUE || requiredKeyDownState == TriStateBoolean.DONT_CARE)) {
            return true;
        }
        if (!actualKeyDownState && (requiredKeyDownState == TriStateBoolean.FALSE || requiredKeyDownState == TriStateBoolean.DONT_CARE)) {
            return true;
        }
        return false;
    }

    private void flushDOMElements(final GridWidget selectedGridWidget) {
        final GridData gridModel = selectedGridWidget.getModel();
        for (GridColumn<?> column : gridModel.getColumns()) {
            if (column instanceof HasSingletonDOMElementResource) {
                ((HasSingletonDOMElementResource) column).flush();
            }
            if (column instanceof HasDOMElementResources) {
                ((HasDOMElementResources) column).destroyResources();
            }
        }
    }

    private GridWidget getSelectedGridWidget() {
        for (GridWidget gridWidget : gridLayer.getGridWidgets()) {
            if (gridWidget.isSelected()) {
                return gridWidget;
            }
        }
        return null;
    }
}
