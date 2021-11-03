/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.dataprovider.sql;

import static org.dashbuilder.dataprovider.sql.SQLFactory.dropTable;
import static org.dashbuilder.dataprovider.sql.SQLFactory.table;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.sql.Statement;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.junit.Before;
import org.junit.Test;

public class SQLColumnsTypeTest extends SQLDataSetTestBase {
    
    String CLOB_TABLE = "CLOB_TABLE";
    String CLOB_COLUMN = "CLOB_CL";
    String CLOB_VAL = "TEST_CLOB";

    @Before
    public void prepareForClobTest() throws SQLException {
        String TABLE_SQL = createTableWithClobSQL();
        String INSERT = "INSERT INTO " + CLOB_TABLE + " VALUES('"+ CLOB_VAL + "')";
        JDBCUtils.execute(conn, TABLE_SQL);
        JDBCUtils.execute(conn, INSERT);
    }

    @Override
    public void tearDown() throws Exception {
        removeClobTable();
        super.tearDown();
    }

    @Override
    public void testAll() throws Exception {
        // before won't work when running all tests
        prepareForClobTest();
        clobColumnTest();
        removeClobTable();
    }
    
    @Test
    public void clobColumnTest() throws Exception {
        SQLDataSetDef def = new SQLDataSetDef();
        def.setDbTable(CLOB_TABLE);
        
        // it does not actually matter what is the datasource name
        // it will use the settings provided by the current test settings
        // and by default the datasource name is ignored.
        def.setDataSource("test");
        dataSetDefRegistry.registerDataSetDef(def);
        DataSet ds = sqlDataSetProvider.lookupDataSet(def, null);
        
        DataColumn clobColumn = ds.getColumnById(CLOB_COLUMN);
        assertNotNull(clobColumn);
        assertEquals(1, clobColumn.getValues().size());
        Object object = clobColumn.getValues().get(0);
        assertEquals(CLOB_VAL, object.toString());
    }
    
    /**
     * CLOB type may not be present in some DBMS systems.
     * 
     * @return
     *  The SQL create table specific for the current database.
     */
    private String createTableWithClobSQL() {
        String databaseType = testSettings.getDatabaseType();
        switch(databaseType) {
            case DatabaseTestSettings.MYSQL:
            case DatabaseTestSettings.MARIADB:
                return  "CREATE TABLE "+ CLOB_TABLE +" ("
                        + CLOB_COLUMN + " LONGTEXT)";
            case DatabaseTestSettings.POSTGRES:
                return  "CREATE TABLE "+ CLOB_TABLE +" ("
                        + CLOB_COLUMN + " TEXT)";
            case DatabaseTestSettings.SYBASE:
                return  "CREATE TABLE "+ CLOB_TABLE +" ("
                        + CLOB_COLUMN + " VARCHAR(1000))";   
            case DatabaseTestSettings.SQLSERVER:
                return  "CREATE TABLE "+ CLOB_TABLE +" ("
                        + CLOB_COLUMN + " VARCHAR(max))"; 
            default:
                return "CREATE TABLE "+ CLOB_TABLE +" ("
                        + CLOB_COLUMN + " CLOB)";
        } 
    }
    
    public void removeClobTable() throws SQLException {
        String DELETE = "DROP TABLE " + CLOB_TABLE;
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(DELETE);
    }
    
}
