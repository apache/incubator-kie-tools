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

import java.util.HashSet;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.DATE_FORMAT;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.getDroolsDateFormat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableSearchableElementTest {

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

    private DateTimeFormat format;

    private DTCellValue52 cellValue52;

    private String stringValue = "Element value";

    private Double minY = -2400d;

    private Double minX = -3400d;

    private GuidedDecisionTableSearchableElement element;

    @Before
    public void setUp() {

        final HashSet<GridWidget> gridWidgets = new HashSet<>(asList(gridWidget1, gridWidget2, gridWidget3));

        ApplicationPreferences.setUp(new Maps.Builder<String, String>().put(DATE_FORMAT, "dd/mm/yy").build());

        format = DateTimeFormat.getFormat(getDroolsDateFormat());
        cellValue52 = new DTCellValue52(stringValue);
        element = spy(new GuidedDecisionTableSearchableElement());

        element.setCellValue52(cellValue52);
        element.setModeller(modeller);

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
    public void testMatchesWhenItReturnsTrue() {
        final boolean matches = element.matches("ELE");
        assertTrue(matches);
    }

    @Test
    public void testMatchesWhenItReturnsFalse() {
        final boolean matches = element.matches("LEE");
        assertFalse(matches);
    }

    @Test
    public void testConvertDTCellValueToString() {
        assertEquals("string", element.convertDTCellValueToString(new DTCellValue52("string")));
        assertEquals("10", element.convertDTCellValueToString(new DTCellValue52(10)));
        assertEquals("true", element.convertDTCellValueToString(new DTCellValue52(true)));
        assertEquals("02/01/92", element.convertDTCellValueToString(new DTCellValue52(format.parse("02/01/92"))));
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

        element.setRow(row);
        element.setColumn(column);

        doReturn(highlightHelper).when(element).highlightHelper();
        when(gridWidget2.getModel()).thenReturn(model);
        when(model.getColumns()).thenReturn(asList(column1, column2));
        when(model.getRows()).thenReturn(asList(gridRow1, gridRow2, gridRow3, gridRow4));
        when(gridRow1.getHeight()).thenReturn(gridRow1Height);
        when(gridRow2.getHeight()).thenReturn(gridRow2Height);
        when(gridRow3.getHeight()).thenReturn(gridRow3Height);
        when(gridRow4.getHeight()).thenReturn(gridRow4Height);
        when(column1.getWidth()).thenReturn(column1Width);
        when(column2.getWidth()).thenReturn(column2Width);
        when(highlightHelper.withMinX(minX)).thenReturn(highlightHelper);
        when(highlightHelper.withMinY(minY)).thenReturn(highlightHelper);
        when(highlightHelper.withPaddingX(310)).thenReturn(highlightHelper);
        when(highlightHelper.withPaddingY(406)).thenReturn(highlightHelper);

        element.onFound().execute();

        verify(highlightHelper).highlight(row, column);
    }

    @Test
    public void testHighlightHelper() {

        final GridHighlightHelper expectedHighlightHelper = mock(GridHighlightHelper.class);

        doReturn(expectedHighlightHelper).when(element).makeGridHighlightHelper(gridPanel, gridWidget2);

        final GridHighlightHelper actualHighlightHelper = element.highlightHelper();

        assertEquals(expectedHighlightHelper, actualHighlightHelper);
    }
}
