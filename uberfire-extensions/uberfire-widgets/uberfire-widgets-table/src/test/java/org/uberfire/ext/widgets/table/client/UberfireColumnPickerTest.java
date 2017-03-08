/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.table.client;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.gwt.Widget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UberfireColumnPickerTest {

    @Mock
    protected org.gwtbootstrap3.client.ui.gwt.DataGrid dataGrid;
    @GwtMock
    VerticalPanel popupContent;
    @InjectMocks
    private UberfireColumnPicker<String> uberfireColumnPicker;
    private ColumnMeta columnMetaTextCell1;
    private ColumnMeta columnMetaTextCell2;
    private ColumnMeta columnMetaCheckBoxCell;

    @Before
    public void setup() {
        columnMetaTextCell1 = createColumnTextCell("val1",
                                                   "col1");
        columnMetaTextCell2 = createColumnTextCell("val2",
                                                   "col2");
        columnMetaCheckBoxCell = createColumnCheckboxCell("columnMetaCheckBoxCell");
        uberfireColumnPicker.addColumn(columnMetaCheckBoxCell);
        uberfireColumnPicker.addColumn(columnMetaTextCell1);
        uberfireColumnPicker.addColumn(columnMetaTextCell2);
    }

    @Test
    public void testAddThisColumnToPopup() {
        assertTrue(uberfireColumnPicker.addThisColumnToPopup(columnMetaTextCell1));
        assertFalse(uberfireColumnPicker.addThisColumnToPopup(columnMetaCheckBoxCell));
    }

    @Test
    public void testOnlyAddHeaderStringColumnPickerPopup() {
        uberfireColumnPicker.showColumnPickerPopup(0,
                                                   0);
        verify(popupContent,
               times(2)).add(any(Widget.class));
    }

    private ColumnMeta createColumnTextCell(final String value,
                                            String dataStoreName) {
        Column<String, String> testColumn = new Column<String, String>(new TextCell()) {
            @Override
            public String getValue(String object) {
                return value;
            }
        };
        testColumn.setSortable(true);
        testColumn.setDataStoreName(dataStoreName);

        final Header<String> header = new TextHeader(value);
        ColumnMeta<String> columnMeta = new ColumnMeta(testColumn,
                                                       dataStoreName);
        columnMeta.setHeader(header);
        return columnMeta;
    }

    private ColumnMeta createColumnCheckboxCell(String dataStoreName) {
        CheckboxCell checkboxCell = new CheckboxCell(true,
                                                     false);
        Column<String, Boolean> checkColumn = new Column<String, Boolean>(checkboxCell) {
            @Override
            public Boolean getValue(String object) {
                return true;
            }
        };

        Header<Boolean> selectPageHeader = new Header<Boolean>(checkboxCell) {
            @Override
            public Boolean getValue() {
                return true;
            }
        };

        checkColumn.setSortable(false);
        checkColumn.setDataStoreName(dataStoreName);
        ColumnMeta<String> checkColMeta = new ColumnMeta<String>(checkColumn,
                                                                 "");
        checkColMeta.setHeader(selectPageHeader);
        return checkColMeta;
    }
}

