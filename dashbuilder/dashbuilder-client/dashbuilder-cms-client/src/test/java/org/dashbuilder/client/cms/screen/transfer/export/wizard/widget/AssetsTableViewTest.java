/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.cms.screen.transfer.export.wizard.widget;

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableElement;
import elemental2.dom.HTMLTableRowElement;
import elemental2.dom.HTMLTableSectionElement;
import org.dashbuilder.client.cms.screen.util.DomFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AssetsTableViewTest {

    @Mock
    DomFactory domFactory;

    @Mock
    HTMLTableRowElement assetsTableHeaderRow;

    @Mock
    HTMLInputElement searchAssets;
    
    @Mock
    HTMLInputElement selectAllAssets;

    @InjectMocks
    AssetsTableView assetsTableView;
    

    private MockAssetsTablePresenter presenter;

    @Before
    public void prepare() {
        presenter = new MockAssetsTablePresenter();
        when(domFactory.tableCell()).thenReturn(new HTMLTableCellElement());
        when(domFactory.input()).thenReturn(new HTMLInputElement());
        when(domFactory.tableRow()).thenReturn(new HTMLTableRowElement());
        when(domFactory.element(any())).thenReturn(new HTMLElement());

        assetsTableView.assetsTable = buildHTMLTable();
        assetsTableView.init(presenter);
        presenter.view = assetsTableView;
    }

    @Test
    public void testTableRowsCreated() {
        List<String> assets = Arrays.asList("asset1", "asset2");


        presenter.setData(assets);

        HTMLTableSectionElement assetsTable = assetsTableView.assetsTable.tBodies.getAt(0);
        Mockito.verify(assetsTable, times(assets.size())).appendChild(any());
    }

    @Test
    public void testFilterTable() {
        HTMLInputElement filter = assetsTableView.searchAssets;
        HTMLTableElement table = assetsTableView.assetsTable;
        HTMLCollection rows = mock(HTMLCollection.class);
        HTMLTableRowElement[] actualRows = {
                                            mock(HTMLTableRowElement.class),
                                            mock(HTMLTableRowElement.class)
        };
        HTMLCollection<HTMLTableCellElement> firstRowCells = mock(HTMLCollection.class);
        HTMLCollection<HTMLTableCellElement> secondRowCells = mock(HTMLCollection.class);

        HTMLTableCellElement tableCellElement1 = mock(HTMLTableCellElement.class),
                tableCellElement2 = mock(HTMLTableCellElement.class);
        tableCellElement1.textContent = "Abc";
        tableCellElement2.textContent = "dEfx";
        when(firstRowCells.getAt(0)).thenReturn(tableCellElement1);
        when(firstRowCells.getAt(1)).thenReturn(tableCellElement2);

        HTMLTableCellElement tableCellElement3 = mock(HTMLTableCellElement.class),
                tableCellElement4 = mock(HTMLTableCellElement.class);
        tableCellElement3.textContent = "gHi";
        tableCellElement4.textContent = "jKlX";
        when(secondRowCells.getAt(0)).thenReturn(tableCellElement3);
        when(secondRowCells.getAt(1)).thenReturn(tableCellElement4);

        actualRows[0].cells = firstRowCells;
        actualRows[1].cells = secondRowCells;
        when(actualRows[0].cells.getLength()).thenReturn(2);
        when(actualRows[1].cells.getLength()).thenReturn(2);

        for (int i = 0; i < actualRows.length; i++) {
            when(rows.getAt(i)).thenReturn(actualRows[i]);
        }
        when(rows.getLength()).thenReturn(actualRows.length);
        table.tBodies.getAt(0).rows = rows;

        filter.value = "abC";
        assetsTableView.filterTable();
        assertTrue(actualRows[1].hidden);
        assertFalse(actualRows[0].hidden);

        filter.value = "jkL";
        assetsTableView.filterTable();
        assertTrue(actualRows[0].hidden);
        assertFalse(actualRows[1].hidden);

        filter.value = "X";
        assetsTableView.filterTable();
        assertFalse(actualRows[0].hidden);
        assertFalse(actualRows[1].hidden);

        filter.value = "z";
        assetsTableView.filterTable();
        assertTrue(actualRows[0].hidden);
        assertTrue(actualRows[1].hidden);
    }

    private HTMLTableElement buildHTMLTable() {
        HTMLTableElement table = new HTMLTableElement();
        HTMLCollection<HTMLTableSectionElement> tableBodies = mock(HTMLCollection.class);
        HTMLTableSectionElement tableBody = mock(HTMLTableSectionElement.class);
        table.tBodies = tableBodies;
        when(tableBodies.getAt(0)).thenReturn(tableBody);
        return table;
    }

    class MockAssetsTablePresenter extends AssetsTableAbstractPresenter<String> {

        @Override
        public String[] getHeaders() {
            return new String[]{"test"};
        }

        @Override
        public String[] toRow(String t) {
            return new String[]{t};
        }

    }

}