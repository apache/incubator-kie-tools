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

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.dashbuilder.dataprovider.sql.model.Select;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.dashbuilder.dataprovider.sql.SQLFactory.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectStatementTest {

    @Mock
    Connection connection;
    
    @Mock
    DatabaseMetaData metadata;
    
    @Before
    public void setUp() throws Exception {
        when(connection.getMetaData()).thenReturn(metadata);
    }

    @Test
    public void testFixSQLCase() throws Exception {
        when(metadata.storesLowerCaseIdentifiers()).thenReturn(false);
        when(metadata.storesUpperCaseIdentifiers()).thenReturn(true);

        Select select = new Select(connection, JDBCUtils.H2);
        select.columns(column("id"));
        select.from(table("table"));

        assertEquals(select.getSQL(), "SELECT ID FROM TABLE");
    }

    @Test
    public void testKeepColumnAsIs() throws Exception {
        when(metadata.storesLowerCaseIdentifiers()).thenReturn(false);
        when(metadata.storesUpperCaseIdentifiers()).thenReturn(true);

        Select select = new Select(connection, JDBCUtils.H2);
        select.columns(column("id"));
        select.from("SELECT ID as \"id\" FROM TABLE");

        assertEquals(select.getSQL(), "SELECT \"id\" FROM (SELECT ID AS \"id\" FROM TABLE) \"dbSQL\"");
    }
}
