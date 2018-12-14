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

package org.kie.workbench.common.dmn.client.widgets.grid.handlers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.types.Point2D;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderUtilities;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.util.RenderContextUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.DefaultGridWidgetEditCellMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class EditableHeaderGridWidgetEditCellMouseEventHandler extends DefaultGridWidgetEditCellMouseEventHandler {

    @Override
    public boolean handleHeaderCell(final GridWidget gridWidget,
                                    final Point2D relativeLocation,
                                    final int uiHeaderRowIndex,
                                    final int uiHeaderColumnIndex,
                                    final AbstractNodeMouseEvent event) {
        //Get column information
        final double cx = relativeLocation.getX();
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(cx);
        final GridColumn<?> column = ci.getColumn();
        if (column == null) {
            return false;
        }
        if (!EditableHeaderUtilities.hasEditableHeader(column)) {
            return false;
        }

        if (!EditableHeaderUtilities.isEditableHeader(column,
                                                      uiHeaderRowIndex)) {
            return false;
        }

        //Get rendering information
        final Point2D gridWidgetComputedLocation = gridWidget.getComputedLocation();
        final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
        final EditableHeaderMetaData headerMetaData = (EditableHeaderMetaData) column.getHeaderMetaData().get(uiHeaderRowIndex);
        final GridBodyCellEditContext context = RenderContextUtilities.makeRenderContext(gridWidget,
                                                                                         ri,
                                                                                         ci,
                                                                                         relativeLocation.add(gridWidgetComputedLocation),
                                                                                         uiHeaderRowIndex);

        if (isHeaderSelectionValid(gridWidget)) {
            if (Objects.equals(headerMetaData.getSupportedEditAction(), GridCellEditAction.getSupportedEditAction(event))) {
                headerMetaData.edit(context);
                return true;
            }
        }

        return false;
    }

    private boolean isHeaderSelectionValid(final GridWidget gridWidget) {
        final GridData gridData = gridWidget.getModel();
        final List<GridData.SelectedCell> selectedHeaderCells = gridData.getSelectedHeaderCells();

        //No selections (should not happen) then cannot edit the header cell
        if (selectedHeaderCells.isEmpty()) {
            return false;
        }

        //Single selection, then can edit header cell
        if (selectedHeaderCells.size() == 1) {
            return true;
        }

        //Check if all selected cell MetaData are equal
        final GridColumn.HeaderMetaData firstSelectedCellMetaData = getSelectedCellMetaData(gridData,
                                                                                            selectedHeaderCells.get(0));
        return selectedHeaderCells
                .stream()
                .map(selectedCell -> getSelectedCellMetaData(gridData, selectedCell))
                .collect(Collectors.toList())
                .stream()
                .allMatch(selectedCell -> Objects.equals(selectedCell, firstSelectedCellMetaData));
    }

    private GridColumn.HeaderMetaData getSelectedCellMetaData(final GridData gridData,
                                                              final GridData.SelectedCell selectedCell) {
        final int _headerColumnIndex = ColumnIndexUtilities.findUiColumnIndex(gridData.getColumns(),
                                                                              selectedCell.getColumnIndex());
        final GridColumn<?> gridColumn = gridData.getColumns().get(_headerColumnIndex);
        final List<GridColumn.HeaderMetaData> gridColumnMetaData = gridColumn.getHeaderMetaData();
        return gridColumnMetaData.get(selectedCell.getRowIndex());
    }
}
