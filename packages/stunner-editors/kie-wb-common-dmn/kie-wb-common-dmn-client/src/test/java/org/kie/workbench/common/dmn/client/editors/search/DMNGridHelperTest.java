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

package org.kie.workbench.common.dmn.client.editors.search;

import java.util.HashSet;
import java.util.Set;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.GridHighlightHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridHelperTest {

    @Mock
    private DMNSession dmnSession;

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DefaultGridLayer defaultGridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private GridHighlightHelper highlightHelper;

    @Mock
    private GridWidget gridWidget1;

    @Mock
    private GridWidget gridWidget2;

    @Mock
    private GridWidget gridWidget3;

    @Mock
    private GridData model;

    private DMNGridHelper helper;

    @Before
    public void setup() {

        when(dmnSession.getGridPanel()).thenReturn(gridPanel);
        when(gridPanel.getDefaultGridLayer()).thenReturn(defaultGridLayer);
        when(sessionManager.getCurrentSession()).thenReturn(dmnSession);

        helper = spy(new DMNGridHelper(sessionManager));
    }

    @Test
    public void testHighlightHelper() {

        final int row = 1;
        final int column = 1;
        final GridColumn<?> headerColumn = mock(GridColumn.class);
        final GridColumn<?> currentColumn = mock(GridColumn.class);
        final GridRow headerRow = mock(GridRow.class);
        final GridRow currentRow = mock(GridRow.class);
        final double headerColumnWidth = 2d;
        final double currentColumnWidth = 4d;
        final double headerRowWidth = 8d;
        final double currentRowWidth = 16d;

        doReturn(highlightHelper).when(helper).highlightHelper(gridWidget1);
        when(gridWidget1.getModel()).thenReturn(model);
        when(model.getColumns()).thenReturn(asList(headerColumn, currentColumn));
        when(model.getRows()).thenReturn(asList(headerRow, currentRow));

        when(headerColumn.getWidth()).thenReturn(headerColumnWidth);
        when(currentColumn.getWidth()).thenReturn(currentColumnWidth);
        when(headerRow.getHeight()).thenReturn(headerRowWidth);
        when(currentRow.getHeight()).thenReturn(currentRowWidth);

        when(highlightHelper.withPaddingX(headerColumnWidth + currentColumnWidth)).thenReturn(highlightHelper);
        when(highlightHelper.withPaddingY(headerRowWidth + currentRowWidth)).thenReturn(highlightHelper);
        when(highlightHelper.withPinnedGrid()).thenReturn(highlightHelper);

        helper.highlightCell(row, column, gridWidget1);

        verify(highlightHelper).highlight(row, column);
    }

    @Test
    public void clearSelections() {

        when(defaultGridLayer.getGridWidgets()).thenReturn(asSet(gridWidget1, null, gridWidget2, null, gridWidget3));

        doReturn(highlightHelper).when(helper).highlightHelper(gridWidget1);
        doReturn(highlightHelper).when(helper).highlightHelper(gridWidget2);
        doReturn(highlightHelper).when(helper).highlightHelper(gridWidget3);

        helper.clearSelections();

        verify(highlightHelper, times(3)).clearSelections();
    }

    @Test
    public void getGridWidgets() {

        when(defaultGridLayer.getGridWidgets()).thenReturn(asSet(gridWidget1, null, gridWidget2, null, gridWidget3));

        final Set<GridWidget> actualGridWidgets = helper.getGridWidgets();
        final Set<GridWidget> expectedGridWidgets = asSet(gridWidget1, gridWidget2, gridWidget3);

        assertEquals(expectedGridWidgets, actualGridWidgets);
    }

    @Test
    public void testFocusGridPanel() {
        helper.focusGridPanel();

        verify(gridPanel).setFocus(true);
    }

    private Set<GridWidget> asSet(final GridWidget... a) {
        return new HashSet<>(asList(a));
    }
}
