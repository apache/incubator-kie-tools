/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.client;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.SUM;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientDataSetManagerTest extends AbstractDataSetTest {

    @Test
    public void testGroupWithNullDates() {
        // Insert a null entry into the dataset
        DataSet expensesDataSet = clientDataSetManager.getDataSet(EXPENSES);
        int column = expensesDataSet.getColumnIndex(expensesDataSet.getColumnById(COLUMN_DATE));
        expensesDataSet.setValueAt(0, column, null);

        // Group by date. No NPE must occur.
        DataSet result = clientDataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(EXPENSES)
                        .group(COLUMN_DATE)
                        .column(COLUMN_DATE)
                        .column(COLUMN_AMOUNT, SUM)
                        .buildLookup());

        assertEquals(result.getRowCount(), 4);
    }
}