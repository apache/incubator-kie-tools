/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

public class LiteralExpressionPMMLUIModelMapper extends LiteralExpressionUIModelMapper {

    private String placeHolder;

    public LiteralExpressionPMMLUIModelMapper(final Supplier<GridData> uiModel,
                                              final Supplier<Optional<LiteralExpression>> dmnModel,
                                              final ListSelectorView.Presenter listSelector,
                                              final String placeHolder) {
        super(uiModel,
              dmnModel,
              listSelector);
        this.placeHolder = placeHolder;
    }

    @Override
    public void fromDMNModel(final int rowIndex,
                             final int columnIndex) {
        dmnModel.get().ifPresent(literalExpression -> {
            uiModel.get().setCell(rowIndex,
                                  columnIndex,
                                  () -> new DMNGridCell<>(new BaseGridCellValue<>(literalExpression.getText().getValue(),
                                                                                  placeHolder)));
        });
    }
}
