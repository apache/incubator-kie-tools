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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public class FunctionUIModelMapper extends BaseUIModelMapper<FunctionDefinition> {

    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    public FunctionUIModelMapper(final Supplier<GridData> uiModel,
                                 final Supplier<Optional<FunctionDefinition>> dmnModel,
                                 final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                 final Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier) {
        super(uiModel,
              dmnModel);
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.supplementaryEditorDefinitionsSupplier = supplementaryEditorDefinitionsSupplier;
    }

    @Override
    public void fromDMNModel(final int rowIndex,
                             final int columnIndex) {
        dmnModel.get().ifPresent(function -> {
            final FunctionDefinition.Kind kind = extractExpressionLanguage(function);
            final Optional<Expression> expression = Optional.ofNullable(function.getExpression());

            switch (kind) {
                case FEEL:
                    final Optional<ExpressionEditorDefinition<Expression>> expressionEditorDefinition = expressionEditorDefinitionsSupplier.get().getExpressionEditorDefinition(expression);
                    expressionEditorDefinition.ifPresent(ed -> {
                        setUiModelEditor(rowIndex,
                                         columnIndex,
                                         function,
                                         ed);
                    });
                    break;
                case JAVA:
                case PMML:
                    final Optional<ExpressionEditorDefinition<Expression>> supplementaryEditorDefinition = supplementaryEditorDefinitionsSupplier.get().getExpressionEditorDefinition(expression);
                    supplementaryEditorDefinition.ifPresent(ed -> {
                        setUiModelEditor(rowIndex,
                                         columnIndex,
                                         function,
                                         ed);
                    });
            }
        });
    }

    private void setUiModelEditor(final int rowIndex,
                                  final int columnIndex,
                                  final FunctionDefinition function,
                                  final ExpressionEditorDefinition<Expression> ed) {
        final GridCellTuple expressionParent = new GridCellTuple(0, 0, uiModel.get());
        final Optional<Expression> expression = Optional.ofNullable(function.getExpression());
        final Optional<BaseExpressionGrid> editor = ed.getEditor(expressionParent,
                                                                 function,
                                                                 expression,
                                                                 Optional.empty(),
                                                                 true);
        uiModel.get().setCell(rowIndex,
                              columnIndex,
                              new ExpressionCellValue(editor));
    }

    private FunctionDefinition.Kind extractExpressionLanguage(final FunctionDefinition function) {
        final Map<QName, String> attributes = function.getOtherAttributes();
        final String code = attributes.getOrDefault(FunctionDefinition.KIND_QNAME,
                                                    FunctionDefinition.Kind.FEEL.code());
        return FunctionDefinition.Kind.determineFromString(code);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void toDMNModel(final int rowIndex,
                           final int columnIndex,
                           final Supplier<Optional<GridCellValue<?>>> cell) {
        dmnModel.get().ifPresent(function -> {
            cell.get().ifPresent(v -> {
                final ExpressionCellValue ecv = (ExpressionCellValue) v;
                ecv.getValue().ifPresent(beg -> function.setExpression((Expression) beg.getExpression().orElse(null)));
            });
        });
    }
}
