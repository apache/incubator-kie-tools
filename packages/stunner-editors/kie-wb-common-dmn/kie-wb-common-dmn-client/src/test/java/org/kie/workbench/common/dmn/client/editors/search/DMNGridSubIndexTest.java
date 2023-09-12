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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell.HasNameAndDataTypeCell;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridSubIndexTest {

    @Mock
    private DMNGridHelper dmnGridHelper;

    @Mock
    private GridWidget gridWidget1;

    @Mock
    private GridWidget gridWidget2;

    @Mock
    private GridWidget gridWidget3;

    @Mock
    private GridData gridData1;

    @Mock
    private GridData gridData2;

    @Mock
    private GridData gridData3;

    @Mock
    private GridCell<String> cell1;

    @Mock
    private GridCell<String> cell2;

    @Mock
    private GridCell<String> cell3;

    @Mock
    private GridCell<String> cell4;

    @Mock
    private GridCell<String> cell5;

    @Mock
    private GridCell<String> cell6;

    @Mock
    private GridCell<String> cell7;

    @Mock
    private GridCell<HasNameAndDataTypeCell> cell8;

    @Mock
    private GridCellValue<String> cellValue1;

    @Mock
    private GridCellValue<String> cellValue2;

    @Mock
    private GridCellValue<String> cellValue3;

    @Mock
    private GridCellValue<String> cellValue4;

    @Mock
    private GridCellValue<String> cellValue5;

    @Mock
    private GridCellValue<String> cellValue6;

    @Mock
    private GridCellValue<String> cellValue7;

    @Mock
    private GridCellValue<HasNameAndDataTypeCell> cellValue8;

    @Mock
    private HasNameAndDataTypeCell cellNameAndDataTypeValue8;

    private String cellStringValue1 = "Cell value 1";

    private String cellStringValue2 = "Cell value 2";

    private String cellStringValue3 = "Cell value 3";

    private String cellStringValue4 = "Cell value 4";

    private String cellStringValue5 = "Cell value 5";

    private String cellStringValue6 = "Cell value 6";

    private String cellStringValue7 = "Cell value 7";

    private String cellStringValue8 = "Cell value 8";

    private DMNGridSubIndex index;

    @Before
    public void setup() {

        index = spy(new DMNGridSubIndex(dmnGridHelper));

        // GridWidget
        when(dmnGridHelper.getGridWidgets()).thenReturn(asSet(gridWidget1, gridWidget2, gridWidget3));
        when(gridWidget1.getModel()).thenReturn(gridData1);
        when(gridWidget2.getModel()).thenReturn(gridData2);
        when(gridWidget3.getModel()).thenReturn(gridData3);

        // GridData
        when(gridData1.getRowCount()).thenReturn(1);
        when(gridData1.getColumnCount()).thenReturn(2);
        when(gridData2.getRowCount()).thenReturn(2);
        when(gridData2.getColumnCount()).thenReturn(1);
        when(gridData3.getRowCount()).thenReturn(2);
        when(gridData3.getColumnCount()).thenReturn(2);

        // GridCell
        doReturn(cell1).when(gridData1).getCell(0, 0);
        doReturn(cell2).when(gridData1).getCell(0, 1);

        doReturn(cell3).when(gridData2).getCell(0, 0);
        doReturn(cell4).when(gridData2).getCell(1, 0);

        doReturn(cell5).when(gridData3).getCell(0, 0);
        doReturn(cell6).when(gridData3).getCell(0, 1);
        doReturn(cell7).when(gridData3).getCell(1, 0);
        doReturn(null).when(gridData3).getCell(1, 1);

        // GridCellValue
        when(cell1.getValue()).thenReturn(cellValue1);
        when(cell2.getValue()).thenReturn(cellValue2);
        when(cell3.getValue()).thenReturn(cellValue3);
        when(cell4.getValue()).thenReturn(cellValue4);
        when(cell5.getValue()).thenReturn(cellValue5);
        when(cell6.getValue()).thenReturn(cellValue6);
        when(cell7.getValue()).thenReturn(cellValue7);
        when(cell8.getValue()).thenReturn(cellValue8);

        when(cellValue1.getValue()).thenReturn(cellStringValue1);
        when(cellValue2.getValue()).thenReturn(cellStringValue2);
        when(cellValue3.getValue()).thenReturn(cellStringValue3);
        when(cellValue4.getValue()).thenReturn(cellStringValue4);
        when(cellValue5.getValue()).thenReturn(cellStringValue5);
        when(cellValue6.getValue()).thenReturn(cellStringValue6);
        when(cellValue7.getValue()).thenReturn(cellStringValue7);
        when(cellValue8.getValue()).thenReturn(cellNameAndDataTypeValue8);

        when(cellNameAndDataTypeValue8.getName()).thenReturn(new Name(cellStringValue8));
        when(cellNameAndDataTypeValue8.hasData()).thenReturn(true);
    }

    @Test
    public void testGetSearchableElements() {

        final List<DMNSearchableElement> elements = index
                .getSearchableElements()
                .stream()
                .sorted(Comparator.comparing(DMNSearchableElement::getText))
                .collect(Collectors.toList());

        assertEquals(7, elements.size());

        // Text values
        assertEquals(cellStringValue1, elements.get(0).getText());
        assertEquals(cellStringValue2, elements.get(1).getText());
        assertEquals(cellStringValue3, elements.get(2).getText());
        assertEquals(cellStringValue4, elements.get(3).getText());
        assertEquals(cellStringValue5, elements.get(4).getText());
        assertEquals(cellStringValue6, elements.get(5).getText());
        assertEquals(cellStringValue7, elements.get(6).getText());

        // Text values
        elements.get(0).onFound().execute();
        elements.get(1).onFound().execute();
        elements.get(2).onFound().execute();
        elements.get(3).onFound().execute();
        elements.get(4).onFound().execute();
        elements.get(5).onFound().execute();
        elements.get(6).onFound().execute();

        verify(dmnGridHelper).highlightCell(0, 0, gridWidget1);
        verify(dmnGridHelper).highlightCell(0, 1, gridWidget1);
        verify(dmnGridHelper).highlightCell(0, 0, gridWidget2);
        verify(dmnGridHelper).highlightCell(1, 0, gridWidget2);
        verify(dmnGridHelper).highlightCell(0, 0, gridWidget3);
        verify(dmnGridHelper).highlightCell(0, 1, gridWidget3);
        verify(dmnGridHelper).highlightCell(1, 0, gridWidget3);
    }

    @Test
    public void testGetValue() {

        final String expected = cellStringValue1;
        final String actual = index.getValue(cell1);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetValueWithCellThatHasNameAndDataType() {

        final String expected = cellStringValue8;
        final String actual = index.getValue(cell8);

        assertEquals(expected, actual);
    }

    @Test
    public void testOnNoResultsFound() {
        index.onNoResultsFound();
        verify(dmnGridHelper).clearSelections();
    }

    @Test
    public void testOnSearchClosed() {
        index.onSearchClosed();
        verify(dmnGridHelper).focusGridPanel();
    }

    private Set<GridWidget> asSet(final GridWidget... a) {
        return new HashSet<>(asList(a));
    }
}
