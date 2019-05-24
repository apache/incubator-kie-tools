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
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.BaseGridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridWidgetRegistry;

public class ExpressionEditorColumn extends DMNGridColumn<BaseGrid<? extends Expression>, Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>>
        implements HasDOMElementResources {

    public ExpressionEditorColumn(final GridWidgetRegistry registry,
                                  final HeaderMetaData headerMetaData,
                                  final double width,
                                  final BaseGrid<? extends Expression> gridWidget) {
        this(registry,
             Collections.singletonList(headerMetaData),
             width,
             gridWidget);
    }

    public ExpressionEditorColumn(final GridWidgetRegistry registry,
                                  final List<HeaderMetaData> headerMetaData,
                                  final double width,
                                  final BaseGrid<? extends Expression> gridWidget) {
        this(headerMetaData,
             new ExpressionEditorColumnRenderer(registry),
             width,
             gridWidget);
    }

    protected ExpressionEditorColumn(final List<HeaderMetaData> headerMetaData,
                                     final BaseGridColumnRenderer<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> renderer,
                                     final double width,
                                     final BaseGrid<? extends Expression> gridWidget) {
        super(headerMetaData,
              renderer,
              width,
              gridWidget);
        setMovable(false);
        setResizable(false);
    }

    @Override
    public Double getMinimumWidth() {
        double minimumWidth = super.getMinimumWidth();
        final GridData model = gridWidget.getModel();
        final int columnIndex = getLogicalColumnIndex(model);

        if (columnIndex != -1) {
            for (GridRow row : model.getRows()) {
                final GridCell<?> cell = row.getCells().get(columnIndex);
                if (cell != null) {
                    final GridCellValue<?> value = cell.getValue();
                    if (value instanceof ExpressionCellValue) {
                        final ExpressionCellValue ecv = (ExpressionCellValue) value;
                        final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> editor = ecv.getValue();
                        final double padding = editor.map(BaseExpressionGrid::getPadding).orElse(0.0);
                        minimumWidth = Math.max(minimumWidth,
                                                ecv.getMinimumWidth().orElse(0.0) + padding * 2);
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

    @Override
    public void edit(final GridCell<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> cell,
                     final GridBodyCellEditContext context,
                     final Consumer<GridCellValue<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>>> callback) {
        cell.getValue().getValue()
                .filter(nestedGrid -> nestedGrid instanceof UndefinedExpressionGrid)
                .ifPresent(nestedGrid -> nestedGrid.startEditingCell(0, 0));
        cell.getValue().getValue()
                .filter(nestedGrid -> !(nestedGrid instanceof UndefinedExpressionGrid))
                .ifPresent(nestedGrid -> nestedGrid.selectFirstCell());
        cell.getValue().getValue()
                .filter(nestedGrid -> nestedGrid instanceof LiteralExpressionGrid)
                .ifPresent(nestedGrid -> nestedGrid.startEditingCell(0, 0));
    }

    protected void updateWidthOfChildren() {
        final double columnWidth = getWidth();
        final GridData model = gridWidget.getModel();
        final int columnIndex = getLogicalColumnIndex(model);

        if (columnIndex != -1) {
            for (GridRow row : model.getRows()) {
                final GridCell<?> cell = row.getCells().get(columnIndex);
                if (cell != null) {
                    final GridCellValue<?> value = cell.getValue();
                    if (value instanceof ExpressionCellValue) {
                        final ExpressionCellValue ecv = (ExpressionCellValue) value;
                        final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> editor = ecv.getValue();
                        if (editor.isPresent()) {
                            final BaseExpressionGrid beg = editor.get();
                            updateWidthOfLastColumn(beg, columnWidth);
                        }
                    }
                }
            }
        }
    }

    private void updateWidthOfLastColumn(final BaseExpressionGrid gridWidget,
                                         final double columnWidth) {
        final List<GridColumn<?>> gwcs = gridWidget.getModel().getColumns();
        double targetGridWidth = columnWidth - gridWidget.getPadding() * 2;
        for (GridColumn<?> gwc : gwcs) {
            targetGridWidth = targetGridWidth - gwc.getWidth();
        }

        if (gwcs.size() > 0) {
            final GridColumn<?> lastColumn = gwcs.get(gwcs.size() - 1);
            final double lastColumnWidth = lastColumn.getWidth();
            if (lastColumn instanceof DMNGridColumn) {
                ((DMNGridColumn) lastColumn).setWidthInternal(lastColumnWidth + targetGridWidth);
            }
        }
    }

    /*
     * It is index of the this column that can be seen in UI.
     * It may differ to physical index of the column in the list
     * because user could reorder columns in UI
     *
     * return index of the column, -1 if column was not found in the model
     */
    private int getLogicalColumnIndex(GridData model) {
        final int columnIndex = model.getColumns().indexOf(this);
        return columnIndex != -1 ? model.getColumns().get(columnIndex).getIndex() : -1;
    }
}
