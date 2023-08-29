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
package org.kie.workbench.common.dmn.client.widgets.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import org.kie.workbench.common.dmn.client.editors.expressions.util.DynamicReadOnlyUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.mvp.Command;

import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.convertDOMToGridCoordinate;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeXOfEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeYOfEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getUiColumnIndex;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getUiHeaderRowIndex;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getUiRowIndex;

public class DMNGridPanelContextMenuHandler implements ContextMenuHandler {

    private DMNGridLayer gridLayer;
    private CellEditorControlsView.Presenter cellEditorControls;
    private DMNGridPanelCellSelectionHandler cellSelectionHandler;

    private class CandidateGridWidget {

        final Point2D ap;
        final int uiRowIndex;
        final int uiColumnIndex;
        final Object binding;
        final HasCellEditorControls hasCellEditorControls;
        final Command ensureCellSelectedCommand;

        CandidateGridWidget(final Point2D ap,
                            final int uiRowIndex,
                            final int uiColumnIndex,
                            final Object binding,
                            final HasCellEditorControls hasCellEditorControls,
                            final Command ensureCellSelectedCommand) {
            this.ap = ap;
            this.uiRowIndex = uiRowIndex;
            this.uiColumnIndex = uiColumnIndex;
            this.binding = binding;
            this.hasCellEditorControls = hasCellEditorControls;
            this.ensureCellSelectedCommand = ensureCellSelectedCommand;
        }
    }

    DMNGridPanelContextMenuHandler(final DMNGridLayer gridLayer,
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
        final int canvasX = getRelativeXOfEvent(event);
        final int canvasY = getRelativeYOfEvent(event);
        final boolean isShiftKeyDown = event.getNativeEvent().getShiftKey();
        final boolean isControlKeyDown = event.getNativeEvent().getCtrlKey();

        final List<CandidateGridWidget> candidateGridWidgets = new ArrayList<>();
        for (GridWidget gridWidget : gridLayer.getGridWidgets()) {

            if (DynamicReadOnlyUtils.isOnlyVisualChangeAllowed(gridWidget)) {
                continue;
            }

            final GridData gridModel = gridWidget.getModel();
            final Point2D ap = convertDOMToGridCoordinate(gridWidget,
                                                          new Point2D(canvasX,
                                                                      canvasY));

            //Ascertain whether the right-click occurred in the Header or Body
            final Integer uiHeaderRowIndex = getUiHeaderRowIndex(gridWidget, ap);
            final Integer uiColumnIndex = getUiColumnIndex(gridWidget, ap.getX());
            if (Objects.nonNull(uiHeaderRowIndex) && Objects.nonNull(uiColumnIndex)) {
                final GridColumn.HeaderMetaData hasCellEditorControls = gridModel.getColumns().get(uiColumnIndex).getHeaderMetaData().get(uiHeaderRowIndex);
                registerCandidateGridWidget(hasCellEditorControls,
                                            gridWidget.getComputedLocation().add(ap),
                                            uiHeaderRowIndex,
                                            uiColumnIndex,
                                            hasCellEditorControls,
                                            candidateGridWidgets,
                                            () -> cellSelectionHandler.selectHeaderCellIfRequired(uiHeaderRowIndex,
                                                                                                  uiColumnIndex,
                                                                                                  gridWidget,
                                                                                                  isShiftKeyDown,
                                                                                                  isControlKeyDown));
            }

            final Integer uiRowIndex = getUiRowIndex(gridWidget, ap.getY());
            if (Objects.nonNull(uiRowIndex) && Objects.nonNull(uiColumnIndex)) {
                final GridCell<?> hasCellEditorControls = gridModel.getCell(uiRowIndex, uiColumnIndex);
                registerCandidateGridWidget(hasCellEditorControls,
                                            gridWidget.getComputedLocation().add(ap),
                                            uiRowIndex,
                                            uiColumnIndex,
                                            gridWidget,
                                            candidateGridWidgets,
                                            () -> cellSelectionHandler.selectCellIfRequired(uiRowIndex,
                                                                                            uiColumnIndex,
                                                                                            gridWidget,
                                                                                            isShiftKeyDown,
                                                                                            isControlKeyDown));
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
        final Object binding = candidateGridWidget.binding;
        final HasCellEditorControls hasCellEditorControls = candidateGridWidget.hasCellEditorControls;
        final Optional<HasCellEditorControls.Editor> editor = hasCellEditorControls.getEditor();

        editor.ifPresent(e -> {
            //First select cell, if required, as selections are examined by HasCellEditorControls.Editor#bind
            candidateGridWidget.ensureCellSelectedCommand.execute();

            e.bind(binding,
                   uiRowIndex,
                   uiColumnIndex);
            cellEditorControls.show(e,
                                    (int) (ap.getX()),
                                    (int) (ap.getY()));
        });
    }

    private void registerCandidateGridWidget(final Object hasCellEditorControls,
                                             final Point2D ap,
                                             final int uiRowIndex,
                                             final int uiColumnIndex,
                                             final Object binding,
                                             final List<CandidateGridWidget> candidateGridWidgets,
                                             final Command ensureCellSelectedCommand) {
        if (hasCellEditorControls == null) {
            return;
        }

        if (hasCellEditorControls instanceof HasCellEditorControls) {
            candidateGridWidgets.add(new CandidateGridWidget(ap,
                                                             uiRowIndex,
                                                             uiColumnIndex,
                                                             binding,
                                                             (HasCellEditorControls) hasCellEditorControls,
                                                             ensureCellSelectedCommand));
        }
    }
}
