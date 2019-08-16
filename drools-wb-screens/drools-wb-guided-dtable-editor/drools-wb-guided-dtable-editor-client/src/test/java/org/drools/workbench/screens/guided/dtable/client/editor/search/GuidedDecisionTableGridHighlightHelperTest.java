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

package org.drools.workbench.screens.guided.dtable.client.editor.search;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.GridHighlightHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.DATE_FORMAT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableGridHighlightHelperTest {

    @Mock
    private GuidedDecisionTableModellerView.Presenter modeller;

    @Mock
    private GuidedDecisionTableModellerView view;

    @Mock
    private GridLayer gridLayer;

    @Mock
    private GridWidget gridWidget1;

    @Mock
    private GridWidget gridWidget2;

    @Mock
    private GridWidget gridWidget3;

    @Mock
    private GridLienzoPanel gridPanel;

    private Double minY = -2400d;

    private Double minX = -3400d;

    private GuidedDecisionTableGridHighlightHelper helper;

    @Before
    public void setup() {

        final HashSet<GridWidget> gridWidgets = new HashSet<>(asList(gridWidget1, gridWidget2, gridWidget3));

        ApplicationPreferences.setUp(new Maps.Builder<String, String>().put(DATE_FORMAT, "dd/mm/yy").build());

        helper = spy(new GuidedDecisionTableGridHighlightHelper());

        when(gridWidget1.getY()).thenReturn(0d);
        when(gridWidget2.getY()).thenReturn(0d);
        when(gridWidget3.getY()).thenReturn(minY);
        when(gridWidget1.getX()).thenReturn(0d);
        when(gridWidget2.getX()).thenReturn(0d);
        when(gridWidget3.getX()).thenReturn(minX);
        when(gridWidget2.isSelected()).thenReturn(true);
        when(gridWidget1.isVisible()).thenReturn(true);
        when(gridWidget2.isVisible()).thenReturn(true);
        when(gridWidget3.isVisible()).thenReturn(true);
        when(modeller.getView()).thenReturn(view);
        when(view.getGridLayerView()).thenReturn(gridLayer);
        when(view.getGridPanel()).thenReturn(gridPanel);
        when(view.getGridWidgets()).thenReturn(gridWidgets);
        when(gridLayer.getGridWidgets()).thenReturn(gridWidgets);
    }

    @Test
    public void testOnFound() {

        final GridHighlightHelper highlightHelper = mock(GridHighlightHelper.class);
        final GridData model = mock(GridData.class);
        final GridColumn<?> column1 = mock(GridColumn.class);
        final GridColumn<?> column2 = mock(GridColumn.class);
        final GridRow gridRow1 = mock(GridRow.class);
        final GridRow gridRow2 = mock(GridRow.class);
        final GridRow gridRow3 = mock(GridRow.class);
        final GridRow gridRow4 = mock(GridRow.class);
        final double gridRow1Height = 100;
        final double gridRow2Height = 101;
        final double gridRow3Height = 102;
        final double gridRow4Height = 103;
        final double column1Width = 90;
        final double column2Width = 110;
        final int row = 3;
        final int column = 1;
        final GuidedDecisionTableView widget = mock(GuidedDecisionTableView.class);
        final double paddingX = 310;
        final double paddingY = 406;

        doReturn(highlightHelper).when(helper).highlightHelper(modeller, widget);
        when(gridWidget2.getModel()).thenReturn(model);
        when(model.getColumns()).thenReturn(asList(column1, column2));
        when(model.getRows()).thenReturn(asList(gridRow1, gridRow2, gridRow3, gridRow4));
        when(gridRow1.getHeight()).thenReturn(gridRow1Height);
        when(gridRow2.getHeight()).thenReturn(gridRow2Height);
        when(gridRow3.getHeight()).thenReturn(gridRow3Height);
        when(gridRow4.getHeight()).thenReturn(gridRow4Height);
        when(column1.getWidth()).thenReturn(column1Width);
        when(column2.getWidth()).thenReturn(column2Width);

        when(highlightHelper.withPaddingX(paddingX)).thenReturn(highlightHelper);
        when(highlightHelper.withPaddingY(paddingY)).thenReturn(highlightHelper);

        doReturn(paddingX).when(helper).getPaddingX(column, modeller, widget);
        doReturn(paddingY).when(helper).getPaddingY(row, modeller, widget);

        helper.highlight(row, column, widget, modeller);

        verify(highlightHelper).highlight(row, column);
    }

    @Test
    public void testGetWidth() {
        final Double expectedWidth = 0d;
        final Double actualWidth = helper.getWidth(new ArrayList<>(), 1);
        assertEquals(expectedWidth, actualWidth);
    }

    @Test
    public void testGetHeight() {
        final Double expectedHeight = 0d;
        final Double actualHeight = helper.getHeight(new ArrayList<>(), 1);
        assertEquals(expectedHeight, actualHeight);
    }

    @Test
    public void testGetGridPanel() {
        assertEquals(gridPanel, helper.getGridPanel(modeller));
    }
}
