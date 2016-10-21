/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.backend.integration;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.def.SQLDataSourceDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.impl.AbstractDataSource;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefInfo;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class KieSQLDataSourceLocatorTest {

    private KieSQLDataSourceLocator dataSourceLocator;

    @Mock
    private DataSourceDefQueryService queryService;

    @Mock
    private DataSourceRuntimeManager runtimeManager;

    private List< DataSourceDefInfo > dataSourceDefInfos = new ArrayList<>( );

    @Mock
    private SQLDataSetDef dataSetDef;

    @Mock
    private AbstractDataSource abstractDataSource;

    @Mock
    private DataSource dataSource;

    @Before
    public void setup( ) {
        dataSourceLocator = new KieSQLDataSourceLocator( queryService, runtimeManager );

        // initialize the list of existing data sources
        dataSourceDefInfos.add( new DataSourceDefInfo( "uuid1", "DS1", new DataSourceDeploymentInfo( ) ) );
        dataSourceDefInfos.add( new DataSourceDefInfo( "uuid2", "DS2", new DataSourceDeploymentInfo( ) ) );
        dataSourceDefInfos.add( new DataSourceDefInfo( "uuid3", "DS3", new DataSourceDeploymentInfo( ) ) );
    }

    @Test
    public void testLookup( ) throws Exception {
        when( dataSetDef.getDataSource( ) ).thenReturn( "uuid" );
        when( runtimeManager.lookupDataSource( "uuid" ) ).thenReturn( abstractDataSource );
        when( abstractDataSource.getInternalDataSource( ) ).thenReturn( dataSource );

        DataSource result = dataSourceLocator.lookup( dataSetDef );
        // the lookup operation should have been invoked on the runtime manager.
        verify( runtimeManager, times( 1 ) ).lookupDataSource( "uuid" );
        assertEquals( dataSource, result );
    }

    @Test
    public void testListDataSources( ) {
        when( queryService.findGlobalDataSources( true ) ).thenReturn( dataSourceDefInfos );
        List< SQLDataSourceDef > result = dataSourceLocator.list( );
        // the query service should have been invoked
        verify( queryService, times( 1 ) ).findGlobalDataSources( true );
        // and all the definitions should have been returned as SQLDataSourceDefinitions
        assertSameElements( dataSourceDefInfos, result );
    }

    private void assertSameElements( List< DataSourceDefInfo > currentDefs, List< SQLDataSourceDef > returnedDefs ) {
        assertEquals( currentDefs.size( ), returnedDefs.size( ) );
        for ( DataSourceDefInfo currentDef : currentDefs ) {
            if ( !returnedDefs.stream( ).anyMatch( sqlDataSourceDef -> currentDef.getUuid( ).equals( sqlDataSourceDef.getName( ) ) &&
                    currentDef.getName( ).equals( sqlDataSourceDef.getDescription( ) ) ) ) {
                fail( "Expected Data source: " + currentDef.getName( ) + " is not present in calculated result." );
            }
        }
    }
}