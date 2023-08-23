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

package org.kie.workbench.common.dmn.client.widgets.panel;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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

    private DMNGridPanelCellSelectionHandler cellSelectionHandler;

    @Before
    public void setup() {
        this.cellSelectionHandler = new DMNGridPanelCellSelectionHandlerImpl(gridLayer);
    }

    @Test
    public void testSelectCellIfRequired() {
        final GridWidget gridWidget = mockGridWidget();
        final GridData gridData = gridWidget.getModel();
        gridData.setCell(0, 1, () -> gridCell);

        when(gridWidget.selectCell(anyInt(),
                                   anyInt(),
                                   anyBoolean(),
                                   anyBoolean())).thenReturn(true);

        cellSelectionHandler.selectCellIfRequired(0, 1, gridWidget, true, false);

        verify(gridLayer).select(eq(gridWidget));
        verify(gridWidget).selectCell(eq(0),
                                      eq(1),
                                      eq(true),
                                      eq(false));
        verify(gridLayer).batch();
    }

    @Test
    public void testSelectCellIfRequiredButSelectionDidNotChanged() {
        final GridWidget gridWidget = mockGridWidget();
        final GridData gridData = gridWidget.getModel();
        gridData.setCell(0, 1, () -> gridCell);

        when(gridWidget.selectCell(anyInt(),
                                   anyInt(),
                                   anyBoolean(),
                                   anyBoolean())).thenReturn(false);

        cellSelectionHandler.selectCellIfRequired(0, 1, gridWidget, true, false);

        verify(gridLayer).select(eq(gridWidget));
        verify(gridWidget).selectCell(eq(0),
                                      eq(1),
                                      eq(true),
                                      eq(false));
        verify(gridLayer, never()).batch();
    }

    @Test
    public void testSelectCellIfRequiredWhenAlreadySelected() {
        final GridWidget gridWidget = mockGridWidget();
        final GridData gridData = gridWidget.getModel();
        gridData.setCell(0, 1, () -> gridCell);
        gridData.selectCell(0, 1);

        cellSelectionHandler.selectCellIfRequired(0, 1, gridWidget, true, false);

        verify(gridLayer, never()).select(eq(gridWidget));
        verify(gridWidget, never()).selectCell(anyInt(),
                                               anyInt(),
                                               anyBoolean(),
                                               anyBoolean());
        verify(gridLayer, never()).batch();
    }

    @Test
    public void testSelectHeaderCellIfRequired() {
        final GridWidget gridWidget = mockGridWidget();
        final GridData gridData = gridWidget.getModel();
        gridData.setCell(0, 1, () -> gridCell);

        when(gridWidget.selectHeaderCell(anyInt(),
                                         anyInt(),
                                         anyBoolean(),
                                         anyBoolean())).thenReturn(true);

        cellSelectionHandler.selectHeaderCellIfRequired(0, 1, gridWidget, true, false);

        verify(gridLayer).select(eq(gridWidget));
        verify(gridWidget).selectHeaderCell(eq(0),
                                            eq(1),
                                            eq(true),
                                            eq(false));
        verify(gridLayer).batch();
    }

    @Test
    public void testSelectHeaderCellIfRequiredButSelectionDidNotChanged() {
        final GridWidget gridWidget = mockGridWidget();
        final GridData gridData = gridWidget.getModel();
        gridData.setCell(0, 1, () -> gridCell);

        when(gridWidget.selectHeaderCell(anyInt(),
                                         anyInt(),
                                         anyBoolean(),
                                         anyBoolean())).thenReturn(false);

        cellSelectionHandler.selectHeaderCellIfRequired(0, 1, gridWidget, true, false);

        verify(gridLayer).select(eq(gridWidget));
        verify(gridWidget).selectHeaderCell(eq(0),
                                            eq(1),
                                            eq(true),
                                            eq(false));
        verify(gridLayer, never()).batch();
    }

    @Test
    public void testSelectHeaderCellIfRequiredWhenAlreadySelected() {
        final GridWidget gridWidget = mockGridWidget();
        final GridData gridData = gridWidget.getModel();
        gridData.setCell(0, 1, () -> gridCell);
        gridData.selectHeaderCell(0, 1);

        cellSelectionHandler.selectHeaderCellIfRequired(0, 1, gridWidget, true, false);

        verify(gridLayer, never()).select(eq(gridWidget));
        verify(gridWidget, never()).selectHeaderCell(anyInt(),
                                                     anyInt(),
                                                     anyBoolean(),
                                                     anyBoolean());
        verify(gridLayer, never()).batch();
    }

    private BaseExpressionGrid mockGridWidget() {
        final BaseExpressionGrid gridWidget = mock(BaseExpressionGrid.class);
        final GridData gridData = new DMNGridData();
        when(gridWidget.getModel()).thenReturn(gridData);

        gridData.appendColumn(new RowNumberColumn());
        IntStream.range(0, 3).forEach(i -> {
            final GridColumn gridColumn = mock(GridColumn.class);
            final List<GridColumn.HeaderMetaData> headerMetaData = Collections.singletonList(mock(GridColumn.HeaderMetaData.class));
            when(gridColumn.getIndex()).thenReturn(i);
            when(gridColumn.getHeaderMetaData()).thenReturn(headerMetaData);
            gridData.appendColumn(gridColumn);
        });

        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());

        return gridWidget;
    }
}
