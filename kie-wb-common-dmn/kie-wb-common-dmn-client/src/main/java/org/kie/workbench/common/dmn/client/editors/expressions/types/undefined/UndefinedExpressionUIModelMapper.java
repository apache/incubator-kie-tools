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
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public class UndefinedExpressionUIModelMapper extends BaseUIModelMapper<Expression> {

    private final ListSelectorView.Presenter listSelector;
    private final HasExpression hasExpression;
    private final GridCellTuple parent;

    public UndefinedExpressionUIModelMapper(final Supplier<GridData> uiModel,
                                            final Supplier<Optional<Expression>> dmnModel,
                                            final ListSelectorView.Presenter listSelector,
                                            final HasExpression hasExpression,
                                            final GridCellTuple parent) {
        super(uiModel,
              dmnModel);
        this.listSelector = listSelector;
        this.hasExpression = hasExpression;
        this.parent = parent;
    }

    @Override
    public void fromDMNModel(final int rowIndex,
                             final int columnIndex) {
        uiModel.get().setCell(rowIndex,
                              columnIndex,
                              () -> new UndefinedExpressionCell(listSelector));
        uiModel.get().getCell(rowIndex,
                              columnIndex).setSelectionStrategy((final GridData model,
                                                                 final int uiRowIndex,
                                                                 final int uiColumnIndex,
                                                                 final boolean isShiftKeyDown,
                                                                 final boolean isControlKeyDown) -> {
            final GridData parentUiModel = parent.getGridWidget().getModel();
            return parentUiModel.getCell(parent.getRowIndex(),
                                         parent.getColumnIndex()).getSelectionStrategy().handleSelection(parentUiModel,
                                                                                                         parent.getRowIndex(),
                                                                                                         parent.getColumnIndex(),
                                                                                                         isShiftKeyDown,
                                                                                                         isControlKeyDown);
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void toDMNModel(final int rowIndex,
                           final int columnIndex,
                           final Supplier<Optional<GridCellValue<?>>> cell) {
        cell.get().ifPresent(v -> {
            final ExpressionCellValue ecv = (ExpressionCellValue) v;
            ecv.getValue().ifPresent(beg -> {
                hasExpression.setExpression((Expression) beg.getExpression().orElse(null));
                beg.getExpression().ifPresent(e -> ((Expression) e).setParent(hasExpression.asDMNModelInstrumentedBase()));
            });
        });
    }
}
