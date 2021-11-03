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
package org.dashbuilder.dataset;

import java.util.List;

import org.dashbuilder.DataSetCore;
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.junit.Assert.*;

public class DataSetColumnTest {

    public static final String EXPENSE_REPORTS = "expense_reports";

    DataSetManager dataSetManager = DataSetCore.get().getDataSetManager();

    @Before
    public void setUp() throws Exception {
        DataSet dataSet = ExpenseReportsData.INSTANCE.toDataSet();
        dataSet.setUUID(EXPENSE_REPORTS);
        dataSetManager.registerDataSet(dataSet);
    }

    @Test
    public void testDataSetLookupColumns() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .column(COLUMN_CITY, "City")
                        .column(COLUMN_DEPARTMENT, "Department")
                        .column(COLUMN_EMPLOYEE, "Employee")
                        .column(COLUMN_AMOUNT, "Amount")
                        .buildLookup());

        assertEquals(result.getRowCount(), 50);
        assertEquals(result.getColumnByIndex(0).getId(), "City");
        assertEquals(result.getColumnByIndex(1).getId(), "Department");
        assertEquals(result.getColumnByIndex(2).getId(), "Employee");
        assertEquals(result.getColumnByIndex(3).getId(), "Amount");

        assertEquals(result.getColumnByIndex(0).getColumnType(), ColumnType.LABEL);
        assertEquals(result.getColumnByIndex(1).getColumnType(), ColumnType.LABEL);
        assertEquals(result.getColumnByIndex(2).getColumnType(), ColumnType.LABEL);
        assertEquals(result.getColumnByIndex(3).getColumnType(), ColumnType.NUMBER);

        assertEquals(result.getValueAt(0, 0), "Barcelona");
        assertEquals(result.getValueAt(0, 1), "Engineering");
        assertEquals(result.getValueAt(0, 2), "Roxie Foraker");
        assertEquals(result.getValueAt(0, 3), 120.35d);

        assertNotNull(result.getColumnById("City"));
        assertNotNull(result.getColumnById("Department"));
        assertNotNull(result.getColumnById("Employee"));
        assertNotNull(result.getColumnById("Amount"));

        assertNotNull(result.getColumnById("CITY"));
        assertNotNull(result.getColumnById("DEPARTMENT"));
        assertNotNull(result.getColumnById("EMPLOYEE"));
        assertNotNull(result.getColumnById("AMOUNT"));

        assertNotNull(result.getColumnById("city"));
        assertNotNull(result.getColumnById("department"));
        assertNotNull(result.getColumnById("employee"));
        assertNotNull(result.getColumnById("amount"));
    }

    @Test
    public void testDataSetMetadataColumns() throws Exception {
        DataSetMetadata result = dataSetManager.getDataSetMetadata(EXPENSE_REPORTS);

        assertEquals(result.getNumberOfColumns(), 6);
        assertEquals(result.getNumberOfRows(), 50);

        List<String> columnIds = result.getColumnIds();
        assertEquals(columnIds.size(), 6);
        
        assertTrue(containsIgnoreCase(columnIds, COLUMN_ID));
        assertTrue(containsIgnoreCase(columnIds, COLUMN_CITY));
        assertTrue(containsIgnoreCase(columnIds, COLUMN_DEPARTMENT));
        assertTrue(containsIgnoreCase(columnIds, COLUMN_EMPLOYEE));
        assertTrue(containsIgnoreCase(columnIds, COLUMN_DATE));
        assertTrue(containsIgnoreCase(columnIds, COLUMN_AMOUNT));

        assertEquals(result.getColumnType("Expenses_id"), ColumnType.NUMBER);
        assertEquals(result.getColumnType("City"), ColumnType.LABEL);
        assertEquals(result.getColumnType("Department"), ColumnType.LABEL);
        assertEquals(result.getColumnType("Employee"), ColumnType.LABEL);
        assertEquals(result.getColumnType("Creation_date"), ColumnType.DATE);
        assertEquals(result.getColumnType("Amount"), ColumnType.NUMBER);

        assertEquals(result.getColumnType("expenses_id"), ColumnType.NUMBER);
        assertEquals(result.getColumnType("city"), ColumnType.LABEL);
        assertEquals(result.getColumnType("department"), ColumnType.LABEL);
        assertEquals(result.getColumnType("employee"), ColumnType.LABEL);
        assertEquals(result.getColumnType("creation_date"), ColumnType.DATE);
        assertEquals(result.getColumnType("amount"), ColumnType.NUMBER);

        assertEquals(result.getColumnType("EXPENSES_ID"), ColumnType.NUMBER);
        assertEquals(result.getColumnType("CITY"), ColumnType.LABEL);
        assertEquals(result.getColumnType("DEPARTMENT"), ColumnType.LABEL);
        assertEquals(result.getColumnType("EMPLOYEE"), ColumnType.LABEL);
        assertEquals(result.getColumnType("CREATION_DATE"), ColumnType.DATE);
        assertEquals(result.getColumnType("AMOUNT"), ColumnType.NUMBER);
    }

    public boolean containsIgnoreCase(List<String> list, String str) {
        for (String element : list) {
            if (element.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }
}
