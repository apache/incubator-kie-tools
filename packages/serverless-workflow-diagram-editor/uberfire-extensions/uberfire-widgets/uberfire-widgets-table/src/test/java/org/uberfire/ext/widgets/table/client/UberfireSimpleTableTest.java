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

import org.gwtproject.cell.client.TextCell;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.cellview.client.Column;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

public class UberfireSimpleTableTest {

    @Mock
    protected UberfireColumnPicker columnPickerMock;

    @InjectMocks
    private UberfireSimpleTable<String> uberfireSimpleTable;

    @Before
    public void setup() {

    }

    @Test
    @Ignore
    public void testSetColumnWidth() {
        Column<String, String> testColumn = new Column<String, String>(new TextCell()) {
            @Override
            public String getValue(String object) {
                return "col1";
            }
        };
        uberfireSimpleTable.setColumnPicker(columnPickerMock);
        uberfireSimpleTable.setColumnWidth(testColumn,
                                           35,
                                           Style.Unit.PX);
        verify(columnPickerMock).adjustColumnWidths();
    }
}

