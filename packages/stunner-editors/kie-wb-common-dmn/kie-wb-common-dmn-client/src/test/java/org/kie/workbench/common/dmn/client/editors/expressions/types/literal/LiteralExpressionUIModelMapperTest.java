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
package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell.DEFAULT_HEIGHT;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExpressionUIModelMapperTest {

    @Mock
    protected LiteralExpressionColumn uiLiteralExpressionColumn;

    @Mock
    protected ListSelectorView.Presenter listSelector;

    protected BaseGridData uiModel;

    protected LiteralExpression literalExpression;

    protected LiteralExpressionUIModelMapper mapper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        uiModel = new BaseGridData();
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendColumn(uiLiteralExpressionColumn);
        doReturn(0).when(uiLiteralExpressionColumn).getIndex();

        literalExpression = new LiteralExpression();

        mapper = getMapper();
    }

    protected LiteralExpressionUIModelMapper getMapper() {
        return new LiteralExpressionUIModelMapper(() -> uiModel,
                                                  () -> Optional.of(literalExpression),
                                                  listSelector,
                                                  DEFAULT_HEIGHT);
    }

    @Test
    public void testFromDmn_Empty() {
        mapper.fromDMNModel(0, 0);

        assertEquals("", ((BaseGridCellValue) uiModel.getCell(0, 0).getValue()).getValue());
    }

    @Test
    public void testFromDmn_MultiByte() {
        literalExpression.getText().setValue("学校");
        mapper.fromDMNModel(0, 0);

        assertEquals("学校", ((BaseGridCellValue) uiModel.getCell(0, 0).getValue()).getValue());
    }

    @Test
    public void testFromDmn_CellType() {
        mapper.fromDMNModel(0, 0);

        assertTrue(uiModel.getCell(0, 0) instanceof LiteralExpressionCell);
    }

    @Test
    public void testToDmn_Null() {
        mapper.toDMNModel(0, 0, () -> Optional.of(new BaseGridCellValue<>(null)));

        assertNull(literalExpression.getText().getValue());
    }

    @Test
    public void testToDmn_Empty() {
        mapper.toDMNModel(0, 0, () -> Optional.of(new BaseGridCellValue<>("")));

        assertEquals("", literalExpression.getText().getValue());
    }

    @Test
    public void testToDmn_MultiByte() {
        mapper.toDMNModel(0, 0, () -> Optional.of(new BaseGridCellValue<>("学校")));

        assertEquals("学校", literalExpression.getText().getValue());
    }
}
