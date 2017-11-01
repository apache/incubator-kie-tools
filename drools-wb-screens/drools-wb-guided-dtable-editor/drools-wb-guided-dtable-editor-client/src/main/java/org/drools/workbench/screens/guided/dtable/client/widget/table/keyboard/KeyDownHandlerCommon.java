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
package org.drools.workbench.screens.guided.dtable.client.widget.table.keyboard;

import java.util.Optional;
import java.util.function.Function;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

public class KeyDownHandlerCommon implements KeyDownHandler {

    protected final GridLienzoPanel gridPanel;
    protected final GridLayer gridLayer;
    protected final GridWidget gridWidget;
    protected final HasSingletonDOMElementResource gridCell;
    protected final GridBodyCellRenderContext context;

    public KeyDownHandlerCommon(final GridLienzoPanel gridPanel,
                                final GridLayer gridLayer,
                                final GridWidget gridWidget,
                                final HasSingletonDOMElementResource gridCell,
                                final GridBodyCellRenderContext context) {
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.gridWidget = gridWidget;
        this.gridCell = gridCell;
        this.context = context;
    }

    @Override
    public void onKeyDown(final KeyDownEvent e) {
        final int keyCode = e.getNativeKeyCode();
        final boolean isShiftKeyDown = e.isShiftKeyDown();
        switch (keyCode) {
            case KeyCodes.KEY_TAB:
            case KeyCodes.KEY_ENTER:
                gridCell.flush();
                moveSelection(keyCode,
                              isShiftKeyDown);
                e.preventDefault();

            case KeyCodes.KEY_ESCAPE:
                gridCell.destroyResources();
                gridPanel.setFocus(true);
                gridLayer.batch();
        }

        e.stopPropagation();
    }

    protected void moveSelection(final int keyCode,
                                 final boolean isShiftKeyDown) {
        final Optional<Integer> dx = getDelta(keyCode,
                                              KeyCodes.KEY_TAB,
                                              isShiftKeyDown);
        final Optional<Integer> dy = getDelta(keyCode,
                                              KeyCodes.KEY_ENTER,
                                              isShiftKeyDown);

        if (dx.isPresent() || dy.isPresent()) {
            final Function<Integer, Integer> boundsX = getBoundsXCheck(isShiftKeyDown);
            final Function<Integer, Integer> boundsY = getBoundsYCheck(isShiftKeyDown);
            moveSelection(dx,
                          dy,
                          boundsX,
                          boundsY);
        }
    }

    private Optional<Integer> getDelta(final int keyCode,
                                       final int requiredKeyCode,
                                       final boolean isShiftKeyDown) {
        if (keyCode == requiredKeyCode) {
            return Optional.of(isShiftKeyDown ? -1 : 1);
        }
        return Optional.empty();
    }

    private Function<Integer, Integer> getBoundsXCheck(final boolean isShiftKeyDown) {
        if (isShiftKeyDown) {
            return (x) -> x < 0 ? 0 : x;
        }
        final int maxUiColumnIndex = gridWidget.getModel().getColumnCount() - 1;
        return (x) -> x > maxUiColumnIndex ? maxUiColumnIndex : x;
    }

    private Function<Integer, Integer> getBoundsYCheck(final boolean isShiftKeyDown) {
        if (isShiftKeyDown) {
            return (y) -> y < 0 ? 0 : y;
        }
        final int maxUiRowIndex = gridWidget.getModel().getRowCount() - 1;
        return (y) -> y > maxUiRowIndex ? maxUiRowIndex : y;
    }

    protected void moveSelection(final Optional<Integer> dx,
                                 final Optional<Integer> dy,
                                 final Function<Integer, Integer> boundsX,
                                 final Function<Integer, Integer> boundsY) {
        final int uiRowIndex = boundsY.apply(context.getRowIndex() + dy.orElse(0));
        final int uiColumnIndex = boundsX.apply(context.getColumnIndex() + dx.orElse(0));

        gridWidget.selectCell(uiRowIndex,
                              uiColumnIndex,
                              false,
                              false);
    }
}