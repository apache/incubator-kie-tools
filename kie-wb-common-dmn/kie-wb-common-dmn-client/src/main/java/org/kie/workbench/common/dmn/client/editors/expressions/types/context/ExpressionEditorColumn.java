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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.RequiresResize;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

public class ExpressionEditorColumn extends DMNGridColumn<Optional<GridWidget>> implements RequiresResize {

    public ExpressionEditorColumn(final HeaderMetaData headerMetaData,
                                  final GridWidget gridWidget) {
        this(Collections.singletonList(headerMetaData),
             gridWidget);
    }

    public ExpressionEditorColumn(final List<HeaderMetaData> headerMetaData,
                                  final GridWidget gridWidget) {
        super(headerMetaData,
              new ExpressionEditorColumnRenderer(),
              gridWidget);
        setMovable(false);
        setResizable(false);
    }

    @Override
    public Double getMinimumWidth() {
        double minimumWidth = super.getMinimumWidth();
        final GridData model = gridWidget.getModel();
        final int columnIndex = model.getColumns().indexOf(this);
        final int _columnIndex = model.getColumns().get(columnIndex).getIndex();

        for (GridRow row : model.getRows()) {
            final GridCell<?> cell = row.getCells().get(_columnIndex);
            if (cell != null) {
                final GridCellValue<?> value = cell.getValue();
                if (value instanceof DMNExpressionCellValue) {
                    final DMNExpressionCellValue ecv = (DMNExpressionCellValue) value;
                    final Optional<GridWidget> editor = ecv.getValue();
                    if (editor.isPresent()) {

                        // The minimum width of an embedded editor is the WIDTH of it's columns
                        // (other than the last) plus the MINIMUM WIDTH of the last column.
                        double editorWidth = DMNGridColumn.PADDING * 2;
                        final GridWidget editorWidget = editor.get();
                        final GridData editorModel = editorWidget.getModel();
                        final List<GridColumn<?>> editorColumns = editorModel.getColumns();
                        final int editorColumnCount = editorModel.getColumnCount();
                        for (int editorColumnIndex = 0; editorColumnIndex < editorColumnCount - 1; editorColumnIndex++) {
                            final GridColumn editorColumn = editorColumns.get(editorColumnIndex);
                            editorWidth = editorWidth + editorColumn.getWidth();
                        }
                        editorWidth = editorWidth + editorColumns.get(editorColumnCount - 1).getMinimumWidth();

                        minimumWidth = Math.max(minimumWidth,
                                                editorWidth);
                    }
                }
            }
        }
        return minimumWidth;
    }

    @Override
    public void setWidth(final double width) {
        super.setWidth(width);
        updateWidthOfChildren();
        updateWidthOfPeers();
    }

    @Override
    public void setWidthInternal(final double width) {
        super.setWidth(width);
        updateWidthOfChildren();
    }

    protected void updateWidthOfChildren() {
        final double columnWidth = getWidth();
        final GridData model = gridWidget.getModel();
        final int columnIndex = model.getColumns().indexOf(this);
        final int _columnIndex = model.getColumns().get(columnIndex).getIndex();

        for (GridRow row : model.getRows()) {
            final GridCell<?> cell = row.getCells().get(_columnIndex);
            if (cell != null) {
                final GridCellValue<?> value = cell.getValue();
                if (value instanceof DMNExpressionCellValue) {
                    final DMNExpressionCellValue ecv = (DMNExpressionCellValue) value;
                    final Optional<GridWidget> editor = ecv.getValue();
                    if (editor.isPresent()) {
                        final GridWidget gw = editor.get();
                        final List<GridColumn<?>> gwcs = gw.getModel().getColumns();
                        double targetGridWidth = columnWidth - DMNGridColumn.PADDING * 2;
                        for (GridColumn<?> gwc : gwcs) {
                            targetGridWidth = targetGridWidth - gwc.getWidth();
                        }

                        final GridColumn<?> lastColumn = gwcs.get(gwcs.size() - 1);
                        final double lastColumnWidth = lastColumn.getWidth();
                        ((DMNGridColumn) lastColumn).setWidthInternal(lastColumnWidth + targetGridWidth);
                    }
                }
            }
        }
    }

    @Override
    public void onResize() {
        final double currentColumnWidth = getWidth();
        final double requiredColumnWidth = getRequiredColumnWidth();
        if (currentColumnWidth != requiredColumnWidth) {
            setWidth(requiredColumnWidth);
        }
    }

    private double getRequiredColumnWidth() {
        double requiredColumnWidth = DEFAULT_WIDTH;
        final GridData model = gridWidget.getModel();
        final int columnIndex = model.getColumns().indexOf(this);
        final int _columnIndex = model.getColumns().get(columnIndex).getIndex();

        for (GridRow row : model.getRows()) {
            final GridCell<?> cell = row.getCells().get(_columnIndex);
            if (cell != null) {
                final GridCellValue<?> value = cell.getValue();
                if (value instanceof DMNExpressionCellValue) {
                    final DMNExpressionCellValue ecv = (DMNExpressionCellValue) value;
                    final Optional<GridWidget> editor = ecv.getValue();
                    if (editor.isPresent()) {
                        requiredColumnWidth = Math.max(requiredColumnWidth,
                                                       editor.get().getWidth() + DMNGridColumn.PADDING * 2);
                    }
                }
            }
        }
        return requiredColumnWidth;
    }
}
