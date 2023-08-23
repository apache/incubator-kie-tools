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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

public class InvocationUIModelMapper extends BaseUIModelMapper<Invocation> {

    public static final int ROW_NUMBER_COLUMN_INDEX = 0;

    public static final int BINDING_PARAMETER_COLUMN_INDEX = 1;

    public static final int BINDING_EXPRESSION_COLUMN_INDEX = 2;

    private final GridWidget gridWidget;
    private final Supplier<Boolean> isOnlyVisualChangeAllowedSupplier;
    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private final ListSelectorView.Presenter listSelector;
    private final int nesting;

    public InvocationUIModelMapper(final GridWidget gridWidget,
                                   final Supplier<GridData> uiModel,
                                   final Supplier<Optional<Invocation>> dmnModel,
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
        dmnModel.get().ifPresent(invocation -> {
            switch (columnIndex) {
                case ROW_NUMBER_COLUMN_INDEX:
                    uiModel.get().setCell(rowIndex,
                                          columnIndex,
                                          () -> new InvocationGridCell<>(new BaseGridCellValue<>(rowIndex + 1),
                                                                         listSelector));
                    uiModel.get().getCell(rowIndex,
                                          columnIndex).setSelectionStrategy(RowSelectionStrategy.INSTANCE);
                    break;
                case BINDING_PARAMETER_COLUMN_INDEX:
                    final InformationItem variable = invocation.getBinding().get(rowIndex).getParameter();
                    uiModel.get().setCell(rowIndex,
                                          columnIndex,
                                          () -> new InformationItemCell(() -> InformationItemCell.HasNameAndDataTypeCell.wrap(variable),
                                                                        listSelector));
                    break;
                case BINDING_EXPRESSION_COLUMN_INDEX:
                    final Binding binding = invocation.getBinding().get(rowIndex);
                    final Optional<Expression> expression = Optional.ofNullable(binding.getExpression());
                    final boolean isOnlyVisualChangeAllowed = this.isOnlyVisualChangeAllowedSupplier.get();

                    final Optional<ExpressionEditorDefinition<Expression>> expressionEditorDefinition = expressionEditorDefinitionsSupplier.get().getExpressionEditorDefinition(expression);
                    expressionEditorDefinition.ifPresent(ed -> {
                        final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> editor = ed.getEditor(new GridCellTuple(rowIndex,
                                                                                                                                                                          columnIndex,
                                                                                                                                                                          gridWidget),
                                                                                                                                                        Optional.empty(),
                                                                                                                                                        binding,
                                                                                                                                                        Optional.ofNullable(binding.getParameter()),
                                                                                                                                                        isOnlyVisualChangeAllowed,
                                                                                                                                                        nesting + 1);
                        uiModel.get().setCell(rowIndex,
                                              columnIndex,
                                              () -> new InvocationGridCell<>(new ExpressionCellValue(editor),
                                                                             listSelector));
                    });
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void toDMNModel(final int rowIndex,
                           final int columnIndex,
                           final Supplier<Optional<GridCellValue<?>>> cell) {
        dmnModel.get().ifPresent(invocation -> {
            switch (columnIndex) {
                case ROW_NUMBER_COLUMN_INDEX:
                    break;
                case BINDING_PARAMETER_COLUMN_INDEX:
                    invocation.getBinding()
                            .get(rowIndex)
                            .getParameter()
                            .getName()
                            .setValue(cell.get().orElse(new BaseGridCellValue<>("")).getValue().toString());
                    break;
                case BINDING_EXPRESSION_COLUMN_INDEX:
                    cell.get().ifPresent(v -> {
                        final ExpressionCellValue ecv = (ExpressionCellValue) v;
                        ecv.getValue().ifPresent(beg -> {
                            beg.getExpression().get().ifPresent(e -> invocation.getBinding()
                                    .get(rowIndex)
                                    .setExpression((Expression) e));
                        });
                    });
            }
        });
    }
}
