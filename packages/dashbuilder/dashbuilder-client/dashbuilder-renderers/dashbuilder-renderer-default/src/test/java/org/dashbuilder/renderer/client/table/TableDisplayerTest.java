/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.client.table;

import org.dashbuilder.common.client.widgets.FilterLabel;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.displayer.client.formatter.ValueFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DEPARTMENT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_EMPLOYEE;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_ID;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TableDisplayerTest extends AbstractDisplayerTest {

    public TableDisplayer createTableDisplayer(DisplayerSettings settings) {
        return initDisplayer(new TableDisplayer(mock(TableDisplayer.View.class), mock(FilterLabelSet.class)), settings);
    }

    @Mock
    DisplayerListener displayerListener;

    @Mock
    Command selectCommand;

    @Mock
    FilterLabel filterLabel;

    public void resetFilterLabelSet(FilterLabelSet filterLabelSet) {
        reset(filterLabelSet);
        doAnswer(invocationOnMock -> filterLabel).when(filterLabelSet).addLabel(anyString());
    }

    @Test
    public void testSelectCellNoDrillDown() {

        DisplayerSettings allRows = DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .tablePageSize(10)
                .tableOrderDefault(COLUMN_ID, SortOrder.DESCENDING)
                .filterOn(false, true, true)
                .buildSettings();

        TableDisplayer table = createTableDisplayer(allRows);
        TableDisplayer.View view = table.getView();
        FilterLabelSet filterLabelSet = table.getFilterLabelSet();
        table.addListener(displayerListener);
        table.draw();

        reset(view);
        reset(displayerListener);
        resetFilterLabelSet(filterLabelSet);
        table.selectCell(COLUMN_DEPARTMENT, 3);

        verify(view, never()).gotoFirstPage();
        verify(filterLabelSet).addLabel(anyString());
        verify(displayerListener, never()).onRedraw(table);
    }

    @Test
    public void testFormatEmpty() {
        TableDisplayer table = createTableDisplayer(DisplayerSettingsFactory.newTableSettings()
                .dataset(EXPENSES)
                .buildSettings());

        table.addFormatter(COLUMN_EMPLOYEE, new ValueFormatter() {

            public String formatValue(DataSet dataSet, int row, int column) {
                return "test";
            }

            public String formatValue(Object value) {
                return "test";
            }
        });
        table.draw();
        String value = table.formatValue(100, 3);
        assertEquals(value, "test");
    }

}
