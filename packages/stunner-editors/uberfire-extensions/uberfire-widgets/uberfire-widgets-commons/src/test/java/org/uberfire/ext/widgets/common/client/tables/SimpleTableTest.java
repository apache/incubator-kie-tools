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


package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.widgets.table.client.DataGrid;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Image.class, Label.class, Text.class})
public class SimpleTableTest {

    @GwtMock
    DataGrid dataGridMock;

    private SimpleTable simpleTable;

    @Before
    public void setupMocks() {
        simpleTable = new SimpleTable();
    }

    @Test
    public void testRedrawFlush() throws Exception {
        this.simpleTable = new SimpleTable();

        simpleTable.dataGrid = dataGridMock;
        simpleTable.redraw();
        verify(dataGridMock).redraw();
        verify(dataGridMock).flush();
    }

    @Test
    public void testSavePreferencesAfterColumnChangeByDefault() {
        simpleTable.afterColumnChangedHandler();

        assertTrue(simpleTable.isPersistingPreferencesOnChange());
    }

    @Test
    public void testSavePreferencesAfterColumnChangeConf() {
        simpleTable.setPersistPreferencesOnChange(true);
        simpleTable.afterColumnChangedHandler();

        simpleTable.setPersistPreferencesOnChange(false);
        simpleTable.afterColumnChangedHandler();
    }
}