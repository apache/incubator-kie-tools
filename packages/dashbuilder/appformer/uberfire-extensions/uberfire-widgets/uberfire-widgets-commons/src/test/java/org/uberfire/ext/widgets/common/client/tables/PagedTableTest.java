/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.html.Text;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreference;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.mocks.CallerMock;

import static org.jgroups.util.Util.assertFalse;
import static org.jgroups.util.Util.assertTrue;
import static org.mockito.Mockito.*;
import static org.uberfire.ext.widgets.common.client.tables.PagedTable.DEFAULT_PAGE_SIZE;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Image.class, Label.class, Text.class})
public class PagedTableTest {

    @GwtMock
    AsyncDataProvider dataProvider;

    @GwtMock
    Select select;

    protected CallerMock<UserPreferencesService> userPreferencesService;

    @Mock
    protected UserPreferencesService userPreferencesServiceMock;

    @Before
    public void setupMocks() {
        userPreferencesService = new CallerMock<>(userPreferencesServiceMock);
    }

    @Test
    public void testSetDataProvider() throws Exception {
        PagedTable pagedTable = new PagedTable();

        pagedTable.setDataProvider(dataProvider);
        verify(dataProvider).addDataDisplay(pagedTable);
    }

    @Test
    public void testDataGridHeight() throws Exception {
        final int PAGE_SIZE = 10;
        final int ROWS = 2;
        final int EXPECTED_HEIGHT_PX = PagedTable.HEIGHT_OFFSET_PX;
        PagedTable pagedTable = new PagedTable(PAGE_SIZE, null, null, false, false, false);
        pagedTable.dataGrid = spy(pagedTable.dataGrid);
        when(pagedTable.dataGrid.getRowCount()).thenReturn(ROWS);

        verify(pagedTable.dataGrid, times(0)).setHeight(anyString());
        pagedTable.loadPageSizePreferences();
        verify(pagedTable.dataGrid, times(1)).setHeight(eq(EXPECTED_HEIGHT_PX + "px"));
    }

    @Test
    public void testDataGridHeightWithMoreItemsThanPaging() throws Exception {
        final int PAGE_SIZE = 10;
        final int ROWS = 12;
        final int EXPECTED_HEIGHT_PX = PagedTable.HEIGHT_OFFSET_PX;
        PagedTable pagedTable = new PagedTable(PAGE_SIZE, null, null, false, false, false);
        pagedTable.dataGrid = spy(pagedTable.dataGrid);
        when(pagedTable.dataGrid.getRowCount()).thenReturn(ROWS);

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

    @Test
    public void testPageSizeSelectStartValue() throws Exception {
        final int size = 10;

        new PagedTable(size);

        verify(select).setValue(String.valueOf(size));
        verify(select).addValueChangeHandler(any());
    }

    @Test
    public void testDefaultPageSizeValue() throws Exception {
        new PagedTable();

        verify(select).setValue(String.valueOf(DEFAULT_PAGE_SIZE));
        verify(select).addValueChangeHandler(any());
    }

    @Test
    public void testDataGridMinWidthNotSetByDefault() throws Exception {
        final int PAGE_SIZE = 10;
        Element mockElement = mock(Element.class);
        final int minWidth = 800;
        PagedTable pagedTable = new PagedTable(PAGE_SIZE, null, null, false, false, false);
        ColumnPicker columnPickerMock = mock(ColumnPicker.class);

        when(pagedTable.dataGridContainer.getElement()).thenReturn(mockElement);
        when(columnPickerMock.getDataGridMinWidth()).thenReturn(minWidth);

        pagedTable.setColumnPicker(columnPickerMock);
        pagedTable.setTableHeight();

        verify(mockElement, never()).setAttribute(eq("style"), anyString());
    }

    @Test
    public void testEnableDataGridMinWidth() throws Exception {
        final int PAGE_SIZE = 10;
        Element mockElement = mock(Element.class);
        final int minWidth = 800;
        PagedTable pagedTable = new PagedTable(PAGE_SIZE, null, null, false, false, false);
        ColumnPicker columnPickerMock = mock(ColumnPicker.class);

        when(pagedTable.dataGridContainer.getElement()).thenReturn(mockElement);
        when(columnPickerMock.getDataGridMinWidth()).thenReturn(minWidth);

        pagedTable.setColumnPicker(columnPickerMock);
        pagedTable.enableDataGridMinWidth(true);
        pagedTable.setTableHeight();

        verify(columnPickerMock, never()).setDefaultColumnWidthSize(anyInt());
        verify(mockElement).setAttribute("style", "min-width:" + minWidth + Style.Unit.PX.getType());
    }

    @Test
    public void testDefaultColumnWidth() throws Exception {
        final int PAGE_SIZE = 10;
        Element mockElement = mock(Element.class);
        final int minWidth = 800;
        PagedTable pagedTable = new PagedTable(PAGE_SIZE, null, null, false, false, false);
        ColumnPicker columnPickerMock = mock(ColumnPicker.class);

        when(pagedTable.dataGridContainer.getElement()).thenReturn(mockElement);
        when(columnPickerMock.getDataGridMinWidth()).thenReturn(minWidth);

        pagedTable.setColumnPicker(columnPickerMock);
        pagedTable.setDefaultColumWidthSize(150);
        pagedTable.enableDataGridMinWidth(true);
        pagedTable.setTableHeight();

        verify(columnPickerMock).setDefaultColumnWidthSize(150);
        verify(mockElement).setAttribute("style", "min-width:" + minWidth + Style.Unit.PX.getType());
    }

    @Test
    public void testPreferencesPersistenceOnChange(){
        PagedTable pagedTable = new PagedTable();
        GridPreferencesStore gridPreferencesStore = new GridPreferencesStore(new GridGlobalPreferences("key", null, null));
        pagedTable.setGridPreferencesStore(gridPreferencesStore);
        pagedTable.setPreferencesService(userPreferencesService);

        when(select.getValue()).thenReturn("20");

        ArgumentCaptor<ValueChangeHandler> sizeSelectorChangeHandlerArgumentCaptor = ArgumentCaptor.forClass(ValueChangeHandler.class);
        verify(select).addValueChangeHandler(sizeSelectorChangeHandlerArgumentCaptor.capture());

        pagedTable.setPersistPreferencesOnChange(true);
        assertTrue(pagedTable.isPersistingPreferencesOnChange());
        sizeSelectorChangeHandlerArgumentCaptor.getValue().onValueChange(mock(ValueChangeEvent.class));
        verify(userPreferencesServiceMock).saveUserPreferences(any(UserPreference.class));

        pagedTable.setPersistPreferencesOnChange(false);
        assertFalse(pagedTable.isPersistingPreferencesOnChange());
        sizeSelectorChangeHandlerArgumentCaptor.getValue().onValueChange(mock(ValueChangeEvent.class));
        pagedTable.afterColumnChangedHandler();
        verifyNoMoreInteractions(userPreferencesServiceMock);
    }
}