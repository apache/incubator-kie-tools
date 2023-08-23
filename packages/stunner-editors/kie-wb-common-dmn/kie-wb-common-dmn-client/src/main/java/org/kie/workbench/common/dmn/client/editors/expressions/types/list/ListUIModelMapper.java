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
package org.kie.workbench.common.dmn.client.editors.expressions.types.list;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGridCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

public class ListUIModelMapper extends BaseUIModelMapper<List> {

    private final GridWidget gridWidget;
    private final Supplier<Boolean> isOnlyVisualChangeAllowedSupplier;
    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private final ListSelectorView.Presenter listSelector;
    private final int nesting;

    public ListUIModelMapper(final GridWidget gridWidget,
                             final Supplier<GridData> uiModel,
                             final Supplier<Optional<List>> dmnModel,
                             final Supplier<Boolean> isOnlyVisualChangeAllowedSupplier,
                             final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                             final ListSelectorView.Presenter listSelector,
                             final int nesting) {
        super(uiModel,
              dmnModel);
        this.gridWidget = gridWidget;
        this.isOnlyVisualChangeAllowedSupplier = isOnlyVisualChangeAllowedSupplier;
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.listSelector = listSelector;
        this.nesting = nesting;
    }

    @Override
    public void fromDMNModel(final int rowIndex,
                             final int columnIndex) {
        dmnModel.get().ifPresent(list -> {
            final ListUIModelMapperHelper.ListSection section = ListUIModelMapperHelper.getSection(columnIndex);
            switch (section) {
                case ROW_INDEX:
                    uiModel.get().setCell(rowIndex,
                                          columnIndex,
                                          () -> new ContextGridCell<>(new BaseGridCellValue<>(rowIndex + 1),
                                                                      listSelector));
                    uiModel.get().getCell(rowIndex,
                                          columnIndex).setSelectionStrategy(RowSelectionStrategy.INSTANCE);
                    break;
                case EXPRESSION:
                    final HasExpression hasExpression = list.getExpression().get(rowIndex);
                    final Optional<Expression> expression = Optional.ofNullable(hasExpression.getExpression());
                    final boolean isOnlyVisualChangeAllowed = this.isOnlyVisualChangeAllowedSupplier.get();

                    final Optional<ExpressionEditorDefinition<Expression>> expressionEditorDefinition = expressionEditorDefinitionsSupplier.get().getExpressionEditorDefinition(expression);
                    expressionEditorDefinition.ifPresent(ed -> {
                        final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> editor = ed.getEditor(new GridCellTuple(rowIndex,
                                                                                                                                                                          columnIndex,
                                                                                                                                                                          gridWidget),
                                                                                                                                                        Optional.empty(),
                                                                                                                                                        hasExpression,
                                                                                                                                                        Optional.empty(),
                                                                                                                                                        isOnlyVisualChangeAllowed,
                                                                                                                                                        nesting + 1);

                        uiModel.get().setCell(rowIndex,
                                              columnIndex,
                                              () -> new ContextGridCell<>(new ExpressionCellValue(editor),
                                                                          listSelector));
                    });
            }
        });
    }

    @Override
    public void toDMNModel(final int rowIndex,
                           final int columnIndex,
                           final Supplier<Optional<GridCellValue<?>>> cell) {
        dmnModel.get().ifPresent(list -> {
            final ListUIModelMapperHelper.ListSection section = ListUIModelMapperHelper.getSection(columnIndex);
            switch (section) {
                case ROW_INDEX:
                    break;
                case EXPRESSION:
                    cell.get().ifPresent(v -> {
                        final ExpressionCellValue ecv = (ExpressionCellValue) v;
                        ecv.getValue().ifPresent(beg -> {
                            list.getExpression().get(rowIndex).setExpression(beg.getExpression().get().orElse(null));
                        });
                    });
            }
        });
    }
}
