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

package org.kie.workbench.common.dmn.client.widgets.panel;

import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridPanelCellSelectionHandlerTest {

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private GridCell gridCell;

    @Mock
    private CellSelectionStrategy cellSelectionStrategy;

    private DMNGridPanelCellSelectionHandler cellSelectionHandler;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.cellSelectionHandler = new DMNGridPanelCellSelectionHandlerImpl(gridLayer);

        when(gridCell.getSelectionStrategy()).thenReturn(cellSelectionStrategy);
        when(cellSelectionStrategy.handleSelection(any(GridData.class),
                                                   anyInt(),
                                                   anyInt(),
                                                   anyBoolean(),
                                                   anyBoolean())).thenReturn(true);
    }

    @Test
    public void testSelectCellIfRequired() {
        final GridWidget gridWidget = mockGridWidget(BaseExpressionGrid.class);
        final GridData gridData = gridWidget.getModel();
        gridData.setCell(0, 1, () -> gridCell);

        cellSelectionHandler.selectCellIfRequired(0, 1, gridWidget, true, false);

        verify(gridLayer).select(eq(gridWidget));
        verify(cellSelectionStrategy).handleSelection(eq(gridData),
                                                      eq(0),
                                                      eq(1),
                                                      eq(true),
                                                      eq(false));
        verify(gridLayer).batch();
    }

    @Test
    public void testSelectCellIfRequiredWhenAlreadySelected() {
        final GridWidget gridWidget = mockGridWidget(BaseExpressionGrid.class);
        final GridData gridData = gridWidget.getModel();
        gridData.setCell(0, 1, () -> gridCell);
        gridData.selectCell(0, 1);

        cellSelectionHandler.selectCellIfRequired(0, 1, gridWidget, true, false);

        verify(gridLayer, never()).select(eq(gridWidget));
        verify(cellSelectionStrategy, never()).handleSelection(any(GridData.class),
                                                               anyInt(),
                                                               anyInt(),
                                                               anyBoolean(),
                                                               anyBoolean());
        verify(gridLayer, never()).batch();
    }

    @Test
    public void testSelectCellIfRequiredWhenLiteralExpressionGrid() {
        assertSelectCellIfRequiredForParentGridWidget(mockGridWidget(LiteralExpressionGrid.class));
    }

    @Test
    public void testSelectCellIfRequiredWhenUndefinedExpressionGrid() {
        assertSelectCellIfRequiredForParentGridWidget(mockGridWidget(UndefinedExpressionGrid.class));
    }

    private void assertSelectCellIfRequiredForParentGridWidget(final BaseExpressionGrid gridWidget) {
        final GridData gridData = gridWidget.getModel();
        gridData.setCell(0, 1, () -> gridCell);

        final GridWidget parentGridWidget = mockGridWidget(BaseExpressionGrid.class);
        final GridCellTuple parentInformation = new GridCellTuple(2, 3, parentGridWidget);
        when(gridWidget.getParentInformation()).thenReturn(parentInformation);
        final GridData parentGridData = parentGridWidget.getModel();
        parentGridData.setCell(2, 3, () -> gridCell);

        cellSelectionHandler.selectCellIfRequired(0, 1, gridWidget, true, false);

        verify(gridLayer).select(eq(parentGridWidget));
        verify(cellSelectionStrategy).handleSelection(eq(parentGridData),
                                                      eq(2),
                                                      eq(3),
                                                      eq(true),
                                                      eq(false));
        verify(gridLayer).batch();
    }

    private <G extends BaseExpressionGrid> G mockGridWidget(final Class<G> gridClass) {
        final G gridWidget = mock(gridClass);
        final GridData gridData = new DMNGridData();
        when(gridWidget.getModel()).thenReturn(gridData);

        gridData.appendColumn(new RowNumberColumn());
        IntStream.range(0, 3).forEach(i -> {
            final GridColumn gridColumn = mock(GridColumn.class);
            when(gridColumn.getIndex()).thenReturn(i);
            gridData.appendColumn(gridColumn);
        });

        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());

        return gridWidget;
    }
}
