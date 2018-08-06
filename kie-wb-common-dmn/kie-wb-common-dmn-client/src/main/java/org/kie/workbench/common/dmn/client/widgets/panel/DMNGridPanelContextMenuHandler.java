/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.widgets.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

public class DMNGridPanelContextMenuHandler implements ContextMenuHandler {

    private DMNGridLayer gridLayer;
    private CellEditorControlsView.Presenter cellEditorControls;
    private DMNGridPanelCellSelectionHandler cellSelectionHandler;

    private class CandidateGridWidget {

        final Point2D ap;
        final int uiRowIndex;
        final int uiColumnIndex;
        final GridWidget gridWidget;
        final HasCellEditorControls hasCellEditorControls;

        public CandidateGridWidget(final Point2D ap,
                                   final int uiRowIndex,
                                   final int uiColumnIndex,
                                   final GridWidget gridWidget,
                                   final HasCellEditorControls hasCellEditorControls) {
            this.ap = ap;
            this.uiRowIndex = uiRowIndex;
            this.uiColumnIndex = uiColumnIndex;
            this.gridWidget = gridWidget;
            this.hasCellEditorControls = hasCellEditorControls;
        }
    }

    public DMNGridPanelContextMenuHandler(final DMNGridLayer gridLayer,
                                          final CellEditorControlsView.Presenter cellEditorControls,
                                          final DMNGridPanelCellSelectionHandler cellSelectionHandler) {
        this.gridLayer = gridLayer;
        this.cellEditorControls = cellEditorControls;
        this.cellSelectionHandler = cellSelectionHandler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onContextMenu(final ContextMenuEvent event) {
        event.preventDefault();
        event.stopPropagation();
        final int canvasX = getRelativeX(event);
        final int canvasY = getRelativeY(event);
        final boolean isShiftKeyDown = event.getNativeEvent().getShiftKey();
        final boolean isControlKeyDown = event.getNativeEvent().getCtrlKey();

        final List<CandidateGridWidget> candidateGridWidgets = new ArrayList<>();
        for (GridWidget gridWidget : gridLayer.getGridWidgets()) {
            final GridData gridModel = gridWidget.getModel();
            final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(gridWidget,
                                                                              new Point2D(canvasX,
                                                                                          canvasY));
            final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(gridWidget,
                                                                         ap.getY());
            final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex(gridWidget,
                                                                               ap.getX());

            if (uiRowIndex == null || uiColumnIndex == null) {
                continue;
            }
            final GridCell<?> cell = gridModel.getCell(uiRowIndex, uiColumnIndex);
            if (cell == null) {
                continue;
            }

            if (cell instanceof HasCellEditorControls) {
                final HasCellEditorControls hasControls = (HasCellEditorControls) cell;
                candidateGridWidgets.add(new CandidateGridWidget(ap,
                                                                 uiRowIndex,
                                                                 uiColumnIndex,
                                                                 gridWidget,
                                                                 hasControls));
            }
        }

        if (candidateGridWidgets.isEmpty()) {
            return;
        }

        //Candidate Grids are ordered bottom (least nested) to top (most nested). Therefore the last element is the more specific match.
        final CandidateGridWidget candidateGridWidget = candidateGridWidgets.get(candidateGridWidgets.size() - 1);
        final Point2D ap = candidateGridWidget.ap;
        final int uiRowIndex = candidateGridWidget.uiRowIndex;
        final int uiColumnIndex = candidateGridWidget.uiColumnIndex;
        final GridWidget gridWidget = candidateGridWidget.gridWidget;
        final HasCellEditorControls hasCellEditorControls = candidateGridWidget.hasCellEditorControls;
        final Optional<HasCellEditorControls.Editor> editor = hasCellEditorControls.getEditor();

        //First select cell, if required, as selections are examined by HasCellEditorControls.Editor#bind
        cellSelectionHandler.selectCellIfRequired(uiRowIndex,
                                                  uiColumnIndex,
                                                  gridWidget,
                                                  isShiftKeyDown,
                                                  isControlKeyDown);

        editor.ifPresent(e -> {
            e.bind(gridWidget,
                   uiRowIndex,
                   uiColumnIndex);
            cellEditorControls.show(e,
                                    (int) (ap.getX() + gridWidget.getAbsoluteX()),
                                    (int) (ap.getY() + gridWidget.getAbsoluteY()));
        });
    }

    private int getRelativeX(final ContextMenuEvent event) {
        final NativeEvent e = event.getNativeEvent();
        final Element target = event.getRelativeElement();
        return e.getClientX() - target.getAbsoluteLeft() + target.getScrollLeft() + target.getOwnerDocument().getScrollLeft();
    }

    private int getRelativeY(final ContextMenuEvent event) {
        final NativeEvent e = event.getNativeEvent();
        final Element target = event.getRelativeElement();
        return e.getClientY() - target.getAbsoluteTop() + target.getScrollTop() + target.getOwnerDocument().getScrollTop();
    }
}
