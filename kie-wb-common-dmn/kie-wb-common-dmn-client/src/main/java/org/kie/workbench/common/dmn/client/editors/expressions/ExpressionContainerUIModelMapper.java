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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGridCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public class ExpressionContainerUIModelMapper extends BaseUIModelMapper<Expression> {

    private final GridCellTuple parent;
    private final Supplier<String> nodeUUID;
    private final Supplier<HasExpression> hasExpression;
    private final Supplier<Optional<HasName>> hasName;
    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitions;

    private final ListSelectorView.Presenter listSelector;

    public ExpressionContainerUIModelMapper(final GridCellTuple parent,
                                            final Supplier<GridData> uiModel,
                                            final Supplier<Optional<Expression>> dmnModel,
                                            final Supplier<String> nodeUUID,
                                            final Supplier<HasExpression> hasExpression,
                                            final Supplier<Optional<HasName>> hasName,
                                            final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitions,
                                            final ListSelectorView.Presenter listSelector) {
        super(uiModel,
              dmnModel);
        this.parent = parent;
        this.nodeUUID = nodeUUID;
        this.hasExpression = hasExpression;
        this.hasName = hasName;
        this.expressionEditorDefinitions = expressionEditorDefinitions;
        this.listSelector = listSelector;
    }

    @Override
    public void fromDMNModel(final int rowIndex,
                             final int columnIndex) {
        final GridData uiModel = this.uiModel.get();
        final Optional<Expression> expression = dmnModel.get();
        final Optional<HasName> hasName = this.hasName.get();
        final HasExpression hasExpression = this.hasExpression.get();

        final Optional<ExpressionEditorDefinition<Expression>> expressionEditorDefinition = expressionEditorDefinitions.get().getExpressionEditorDefinition(expression);
        expressionEditorDefinition.ifPresent(definition -> {
            final Optional<BaseExpressionGrid> oEditor = definition.getEditor(parent,
                                                                              Optional.of(nodeUUID.get()),
                                                                              hasExpression,
                                                                              expression,
                                                                              hasName,
                                                                              0);
            uiModel.setCell(0,
                            0,
                            () -> new ContextGridCell<>(new ExpressionCellValue(oEditor),
                                                        listSelector));

            final GridColumn<?> uiColumn = uiModel.getColumns().get(columnIndex);
            uiColumn.setWidth(uiColumn.getMinimumWidth());
        });
    }

    @Override
    public void toDMNModel(final int rowIndex,
                           final int columnIndex,
                           final Supplier<Optional<GridCellValue<?>>> cell) {
        throw new UnsupportedOperationException("ExpressionContainerUIModelMapper does not support updating DMN models.");
    }
}
