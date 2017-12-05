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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

public class FunctionUIModelMapper extends BaseUIModelMapper<FunctionDefinition> {

    public FunctionUIModelMapper(final Supplier<GridData> uiModel,
                                 final Supplier<Optional<FunctionDefinition>> dmnModel) {
        super(uiModel,
              dmnModel);
    }

    @Override
    public void fromDMNModel(final int rowIndex,
                             final int columnIndex) {
        dmnModel.get().ifPresent(function -> {
            switch (rowIndex) {
                case 0:
                    final FunctionParametersHolder parameters = new FunctionParametersHolder(this::extractExpressionLanguage,
                                                                                             this::extractFormalParameters);
                    uiModel.get().setCell(rowIndex,
                                          columnIndex,
                                          new FunctionParametersCellValue(parameters));
                    break;
                case 1:
                    final Expression e = function.getExpression();
                    final LiteralExpression le = (LiteralExpression) e;
                    uiModel.get().setCell(rowIndex,
                                          columnIndex,
                                          new BaseGridCellValue<>(le.getText()));
                    break;
                default:
                    throw new IllegalArgumentException("RowIndex should be either 0 or 1.");
            }
        });
    }

    private String extractExpressionLanguage() {
        if (dmnModel.get().isPresent()) {
            final FunctionDefinition function = dmnModel.get().get();
            final Expression e = function.getExpression();
            final LiteralExpression le = (LiteralExpression) e;
            return le.getExpressionLanguage();
        } else {
            return "";
        }
    }

    private List<InformationItem> extractFormalParameters() {
        if (dmnModel.get().isPresent()) {
            final FunctionDefinition function = dmnModel.get().get();
            return function.getFormalParameter();
        }
        return Collections.emptyList();
    }

    @Override
    public void toDMNModel(final int rowIndex,
                           final int columnIndex,
                           final Supplier<Optional<GridCellValue<?>>> cell) {
        dmnModel.get().ifPresent(function -> {
            switch (rowIndex) {
                case 0:
                    break;
                case 1:
                    final Expression e = function.getExpression();
                    final LiteralExpression le = (LiteralExpression) e;
                    le.setText(cell.get().orElse(new BaseGridCellValue<>("")).getValue().toString());
                    break;
                default:
                    throw new IllegalArgumentException("RowIndex should be either 0 or 1.");
            }
        });
    }
}
