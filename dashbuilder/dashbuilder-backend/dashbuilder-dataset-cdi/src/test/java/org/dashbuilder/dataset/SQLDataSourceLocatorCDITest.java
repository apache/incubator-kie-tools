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
import javax.inject.Inject;
import javax.sql.DataSource;

import org.dashbuilder.dataprovider.SQLDataSetProviderCDI;
import org.dashbuilder.dataprovider.sql.SQLDataSourceLocator;
import org.dashbuilder.dataset.def.SQLDataSourceDef;
import org.dashbuilder.test.BaseCDITest;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@Ignore("see https://issues.jboss.org/browse/RHPAM-832")
public class SQLDataSourceLocatorCDITest extends BaseCDITest {

    @Inject
    SQLDataSetProviderCDI sqlDataSetProviderCDI;

    @Test
    public void testInit() throws Exception {
        assertNotNull(sqlDataSetProviderCDI);
        SQLDataSourceLocator dataSourceLocator = sqlDataSetProviderCDI.getDataSourceLocator();
        assertNotNull(dataSourceLocator);
        assertTrue(dataSourceLocator instanceof SQLDataSourceLocatorMock);
    }

    @Test
    public void testLookup() throws Exception {
        SQLDataSourceLocator dataSourceLocator = sqlDataSetProviderCDI.getDataSourceLocator();
        DataSource dataSource = dataSourceLocator.lookup(null);
        assertNull(dataSource);
    }

    @Test
    public void testList() throws Exception {
        SQLDataSourceLocator dataSourceLocator = sqlDataSetProviderCDI.getDataSourceLocator();
        List<SQLDataSourceDef> l = dataSourceLocator.list();
        assertEquals(l.size(), 2);
    }
}
