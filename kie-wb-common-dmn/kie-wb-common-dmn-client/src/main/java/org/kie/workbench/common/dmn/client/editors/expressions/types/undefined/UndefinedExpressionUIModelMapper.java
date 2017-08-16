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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNExpressionCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public class UndefinedExpressionUIModelMapper extends BaseUIModelMapper<Expression> {

    private HasExpression hasExpression;

    public UndefinedExpressionUIModelMapper(final Supplier<GridData> uiModel,
                                            final Supplier<Optional<Expression>> dmnModel,
                                            final HasExpression hasExpression) {
        super(uiModel,
              dmnModel);
        this.hasExpression = hasExpression;
    }

    @Override
    public void fromDMNModel(final int rowIndex,
                             final int columnIndex) {
        //NOP. There's nothing to map from an "undefined expression" to the DMN model
    }

    @Override
    @SuppressWarnings("unchecked")
    public void toDMNModel(final int rowIndex,
                           final int columnIndex,
                           final Supplier<Optional<GridCellValue<?>>> cell) {
        cell.get().ifPresent(v -> {
            final DMNExpressionCellValue ecv = (DMNExpressionCellValue) v;
            ecv.getValue().ifPresent(editor -> {
                final BaseExpressionGrid beg = (BaseExpressionGrid) editor;
                beg.getExpression().ifPresent(e -> hasExpression.setExpression((Expression) e));
            });
        });
    }
}
