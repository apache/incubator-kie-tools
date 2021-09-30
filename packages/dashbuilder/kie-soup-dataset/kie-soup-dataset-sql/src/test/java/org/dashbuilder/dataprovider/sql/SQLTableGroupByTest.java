/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import org.dashbuilder.dataprovider.sql.model.Column;
import org.dashbuilder.dataprovider.sql.model.Table;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.junit.Test;

import static org.dashbuilder.dataprovider.sql.SQLFactory.*;
import static org.junit.Assert.*;

public class SQLTableGroupByTest extends SQLDataSetTestBase {

    Column ID = column("ID", ColumnType.NUMBER, 4);
    Column NAME = column("NAME", ColumnType.LABEL, 50);
    Table TEST = table("TEST");

    @Override
    public void testAll() throws Exception {
        testTableGroupBy();
    }

    public void createTestTable() throws Exception {
        createTable(conn).table(TEST).columns(ID, NAME).primaryKey(ID).execute();
        insert(conn).into(TEST).set(ID, 1).set(NAME, "Hello").execute();
        insert(conn).into(TEST).set(ID, 2).set(NAME, "World").execute();
        insert(conn).into(TEST).set(ID, 3).set(NAME, "Hello").execute();
        insert(conn).into(TEST).set(ID, 4).set(NAME, "World").execute();

        DataSetDef dataSetDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid("test")
                .dataSource("test")
                .dbTable("TEST", true).buildDef();
        dataSetDefRegistry.registerDataSetDef(dataSetDef);
    }

    public void dropTestTable() throws Exception {
        dropTable(conn).table(TEST).execute();
    }

    @Test
    public void testTableGroupBy() throws Exception {
        try {
            createTestTable();

            DataSet dataSet = dataSetManager.lookupDataSet(
                DataSetLookupFactory.newDataSetLookupBuilder()
                        .dataset("test")
                        .group(NAME.getName())
                        .column(ID.getName())
                        .column(NAME.getName())
                        .rowNumber(8)
                        .buildLookup());


            assertEquals(dataSet.getRowCount(), 2);
            assertEquals(dataSet.getRowCountNonTrimmed(), 2);
        } finally {
            dropTestTable();
        }
    }
}
