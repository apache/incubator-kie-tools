/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.backend.services.dataset.provider;

import java.util.Optional;
import java.util.Set;

import javax.sql.DataSource;

import org.dashbuilder.backend.services.dataset.sql.SQLDataSourceLoader;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeSQLDataSourceLocatorTest {

    @Mock
    SQLDataSourceLoader loader;

    @InjectMocks
    RuntimeSQLDataSourceLocator runtimeSQLDataSourceLocator;

    @Test
    public void testLookupExistingDSByName() throws Exception {
        final var ds = "ds";
        var dsMock = registerDataSource(ds);
        var def = DataSetDefFactory.newSQLDataSetDef().name(ds).buildDef();
        var dataSource = runtimeSQLDataSourceLocator.lookup((SQLDataSetDef) def);
        
        assertEquals(dsMock, dataSource);
    }

    @Test
    public void testLookupExistingDSByUUID() throws Exception {
        final var ds = "ds";
        var dsMock = registerDataSource(ds);
        var def = DataSetDefFactory.newSQLDataSetDef().uuid(ds).buildDef();
        var dataSource = runtimeSQLDataSourceLocator.lookup((SQLDataSetDef) def);
        
        assertEquals(dsMock, dataSource);
    }

    @Test
    public void testLookupExistingDSByDataSource() throws Exception {
        final var ds = "ds";
        var dsMock = registerDataSource(ds);
        var def = DataSetDefFactory.newSQLDataSetDef().dataSource(ds).buildDef();
        var dataSource = runtimeSQLDataSourceLocator.lookup((SQLDataSetDef) def);
        
        assertEquals(dsMock, dataSource);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLookupNonExisting() throws Exception {
        var def = DataSetDefFactory.newSQLDataSetDef().dataSource("not found").buildDef();
        
        when(loader.datasources()).thenReturn(Set.of());
        
        runtimeSQLDataSourceLocator.lookup((SQLDataSetDef) def);
    }

    private DataSource registerDataSource(final String ds) {
        var dsMock = Mockito.mock(DataSource.class);
        
        when(loader.datasources()).thenReturn(Set.of(ds));
        when(loader.getDataSource(eq(ds))).thenReturn(Optional.of(dsMock));
        
        return dsMock;
    }

}