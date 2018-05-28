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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UndefinedExpressionUIModelMapperTest {

    private static final int PARENT_ROW_INDEX = 0;

    private static final int PARENT_COLUMN_INDEX = 1;

    @Mock
    private Expression expression;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private DMNModelInstrumentedBase hasExpressionDMNModelInstrumentedBase;

    @Mock
    private LiteralExpressionGrid editor;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private UndefinedExpressionColumn uiColumn;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private GridData parentGridUiModel;

    @Mock
    private GridCell parentGridUiCell;

    @Mock
    private CellSelectionStrategy parentGridUiCellCellSelectionStrategy;

    private GridData uiModel;

    private Supplier<Optional<GridCellValue<?>>> cellValueSupplier;

    private UndefinedExpressionUIModelMapper mapper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.uiModel = new BaseGridData();
        this.uiModel.appendColumn(uiColumn);
        this.uiModel.appendRow(new DMNGridRow());
        this.mapper = new UndefinedExpressionUIModelMapper(() -> uiModel,
                                                           () -> Optional.ofNullable(expression),
                                                           listSelector,
                                                           hasExpression,
                                                           new GridCellTuple(PARENT_ROW_INDEX,
                                                                             PARENT_COLUMN_INDEX,
                                                                             parentGridWidget));
        this.cellValueSupplier = () -> Optional.of(new ExpressionCellValue(Optional.of(editor)));

        when(parentGridWidget.getModel()).thenReturn(parentGridUiModel);
        when(parentGridUiModel.getCell(eq(PARENT_ROW_INDEX), eq(PARENT_COLUMN_INDEX))).thenReturn(parentGridUiCell);
        when(parentGridUiCell.getSelectionStrategy()).thenReturn(parentGridUiCellCellSelectionStrategy);
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(hasExpressionDMNModelInstrumentedBase);
    }

    @Test
    public void testFromDMNModel() {
        mapper.fromDMNModel(0, 0);

        assertThat(mapper.getUiModel().get().getCell(0, 0)).isInstanceOf(UndefinedExpressionCell.class);
    }

    @Test
    public void testFromDMNCellSelectionStrategy() {
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
    public void testToDMNModelNoEditor() {
        doReturn(Optional.empty()).when(editor).getExpression();

        mapper.toDMNModel(0, 0, cellValueSupplier);

        verify(hasExpression).setExpression(eq(null));
    }

    @Test
    public void testToDMNModelWithEditor() {
        doReturn(Optional.of(expression)).when(editor).getExpression();

        mapper.toDMNModel(0, 0, cellValueSupplier);

        verify(hasExpression).setExpression(eq(expression));
        verify(expression).setParent(eq(hasExpressionDMNModelInstrumentedBase));
    }
}
