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
package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExpressionUIModelMapperTest {

    private static final int PARENT_ROW_INDEX = 0;

    private static final int PARENT_COLUMN_INDEX = 1;

    @Mock
    private LiteralExpressionColumn uiLiteralExpressionColumn;

    @Mock
    private LiteralExpressionGrid literalExpressionEditor;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private GridData parentGridUiModel;

    @Mock
    private GridCell parentGridUiCell;

    @Mock
    private CellSelectionStrategy parentGridUiCellCellSelectionStrategy;

    private BaseGridData uiModel;

    private LiteralExpression literalExpression;

    private LiteralExpressionUIModelMapper mapper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        uiModel = new BaseGridData();
        uiModel.appendRow(new DMNGridRow());
        uiModel.appendColumn(uiLiteralExpressionColumn);
        doReturn(0).when(uiLiteralExpressionColumn).getIndex();
        when(parentGridWidget.getModel()).thenReturn(parentGridUiModel);
        when(parentGridUiModel.getCell(eq(PARENT_ROW_INDEX), eq(PARENT_COLUMN_INDEX))).thenReturn(parentGridUiCell);
        when(parentGridUiCell.getSelectionStrategy()).thenReturn(parentGridUiCellCellSelectionStrategy);

        literalExpression = new LiteralExpression();

        mapper = new LiteralExpressionUIModelMapper(() -> uiModel,
                                                    () -> Optional.of(literalExpression),
                                                    listSelector,
                                                    new GridCellTuple(PARENT_ROW_INDEX,
                                                                      PARENT_COLUMN_INDEX,
                                                                      parentGridWidget));
    }

    @Test
    public void testFromDmn_Empty() throws Exception {
        mapper.fromDMNModel(0, 0);

        assertEquals("", ((BaseGridCellValue) uiModel.getCell(0, 0).getValue()).getValue());
    }

    @Test
    public void testFromDmn_MultiByte() throws Exception {
        literalExpression.setText("学校");
        mapper.fromDMNModel(0, 0);

        assertEquals("学校", ((BaseGridCellValue) uiModel.getCell(0, 0).getValue()).getValue());
    }

    @Test
    public void testFromDmn_CellType() throws Exception {
        mapper.fromDMNModel(0, 0);

        assertTrue(uiModel.getCell(0, 0) instanceof LiteralExpressionCell);
    }

    @Test
    public void testFromDmn_CellSelectionStrategy() {
        mapper.fromDMNModel(0, 0);

        final CellSelectionStrategy strategy = uiModel.getCell(0, 0).getSelectionStrategy();

        strategy.handleSelection(uiModel, 0, 0, true, false);

        verify(parentGridUiCellCellSelectionStrategy).handleSelection(eq(parentGridUiModel),
                                                                      eq(PARENT_ROW_INDEX),
                                                                      eq(PARENT_COLUMN_INDEX),
                                                                      eq(true),
                                                                      eq(false));
    }

    @Test
    public void testToDmn_Empty() throws Exception {
        mapper.toDMNModel(0, 0, () -> Optional.of(new BaseGridCellValue<>("")));

        assertEquals("", literalExpression.getText());
    }

    @Test
    public void testToDmn_MultiByte() throws Exception {
        mapper.toDMNModel(0, 0, () -> Optional.of(new BaseGridCellValue<>("学校")));

        assertEquals("学校", literalExpression.getText());
    }
}
