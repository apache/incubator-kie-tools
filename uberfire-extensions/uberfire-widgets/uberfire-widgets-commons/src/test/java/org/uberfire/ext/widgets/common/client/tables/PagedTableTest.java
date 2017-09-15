/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Image.class, Label.class, Text.class})
public class PagedTableTest {

    @GwtMock
    AsyncDataProvider dataProvider;

    @Test
    public void testSetDataProvider() throws Exception {
    	PagedTable pagedTable = new PagedTable(5);

        pagedTable.setDataProvider(dataProvider);
        verify(dataProvider).addDataDisplay(pagedTable);
    }
    
    @Test
    public void testDataGridHeight() throws Exception {
    	final int PAGE_SIZE = 10;
    	final int EXPECTED_HEIGHT_PX = (PAGE_SIZE * PagedTable.ROW_HEIGHT_PX) + PagedTable.HEIGHT_OFFSET_PX;
    	PagedTable pagedTable = new PagedTable(PAGE_SIZE);
    	pagedTable.dataGrid = spy(pagedTable.dataGrid);
        
        verify(pagedTable.dataGrid, times(0)).setHeight(anyString());
        pagedTable.loadPageSizePreferences();
        verify(pagedTable.dataGrid, times(1)).setHeight(eq(EXPECTED_HEIGHT_PX + "px"));
    }

    @Test
    public void testLoadPageSizePreferencesResetsPageStart() throws Exception {
        final int PAGE_SIZE = 10;

        PagedTable pagedTable = new PagedTable(PAGE_SIZE);
        pagedTable.dataGrid = spy(pagedTable.dataGrid);

        verify(pagedTable.dataGrid, times(0)).setPageStart(0);

        pagedTable.loadPageSizePreferences();
        verify(pagedTable.dataGrid, times(1)).setPageStart(0);
    }

    
}