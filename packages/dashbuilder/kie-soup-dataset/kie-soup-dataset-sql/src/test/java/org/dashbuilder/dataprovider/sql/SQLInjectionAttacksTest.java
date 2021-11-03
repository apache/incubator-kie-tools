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
package org.dashbuilder.dataprovider.sql;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetGroupTest;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import static org.dashbuilder.dataset.ExpenseReportsData.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SQLInjectionAttacksTest extends SQLDataSetTestBase {

    @Mock
    Logger logger;

    @Before
    public void setUp() throws Exception{
        super.setUp();

        sqlDataSetProvider.log = logger;

        doAnswer(invocationOnMock -> {
                String sql = (String) invocationOnMock.getArguments()[0];
                System.out.println(sql);
                return null;
        }).when(logger).debug(anyString());
    }

    @Override
    public void testAll() throws Exception {
        testStringFilterInjection();
    }

    public void testStringFilterInjection() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .filter(COLUMN_EMPLOYEE, FilterFactory.equalsTo("David' OR EMPLOYEE != 'Toni"))
                        .buildLookup());

        assertEquals(result.getRowCount(), 0);

        result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .filter(COLUMN_EMPLOYEE, FilterFactory.equalsTo("David\" OR EMPLOYEE != \"Toni"))
                        .buildLookup());

        assertEquals(result.getRowCount(), 0);

        result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .filter(COLUMN_EMPLOYEE, FilterFactory.equalsTo("David` OR EMPLOYEE != `Toni"))
                        .buildLookup());

        assertEquals(result.getRowCount(), 0);

    }

    @Test
    public void testDropTable() throws Exception {
        DataSet result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .filter(COLUMN_EMPLOYEE, FilterFactory.equalsTo("David'; DROP TABLE 'EXPENSE_REPORTS; SELECT 'a' = 'a"))
                        .buildLookup());

        result = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset(DataSetGroupTest.EXPENSE_REPORTS)
                        .buildLookup());

        assertEquals(result.getRowCount(), 50);
    }
}
