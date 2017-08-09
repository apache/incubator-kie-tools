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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExpressionUIModelMapperTest {

    private static final String TEXT = "text";
    private static final String UPDATE = "update";

    private GridData uiModel;
    private Optional<LiteralExpression> dmnModel;
    private LiteralExpression expression;

    private Supplier<GridData> supplierUiModel;
    private Supplier<Optional<LiteralExpression>> supplierDmnModel;

    private LiteralExpressionUIModelMapper mapper;

    @Before
    public void setup() {
        uiModel = new BaseGridData();
        uiModel.appendColumn(mock(GridColumn.class));
        uiModel.appendRow(new BaseGridRow());
        expression = new LiteralExpression();
        expression.setText(TEXT);

        dmnModel = Optional.of(expression);

        supplierUiModel = () -> uiModel;
        supplierDmnModel = () -> dmnModel;

        this.mapper = new LiteralExpressionUIModelMapper(supplierUiModel,
                                                         supplierDmnModel);
    }

    @Test
    public void checkFromDMNModel() {
        mapper.fromDMNModel(0,
                            0);
        assertEquals(TEXT,
                     uiModel.getCell(0,
                                     0).getValue().getValue().toString());
    }

    @Test
    public void checkToDMNModel() {
        mapper.toDMNModel(0,
                          0,
                          () -> Optional.of(new BaseGridCellValue<>(UPDATE)));
        assertEquals(UPDATE,
                     expression.getText());
    }
}
