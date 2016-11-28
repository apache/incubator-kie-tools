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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.TableDisplayerSettingsBuilder;
import org.dashbuilder.renderer.client.DefaultRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataManagementService;
import org.kie.workbench.common.screens.datasource.management.service.DatabaseMetadataService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class DataManagementServiceTest {

    private static final String DATASOURCE_UUID = "DATASOURCE_UUID";

    private static final String SCHEMA = "SCHEMA";

    private static final String TABLE = "TABLE";

    private static final String COLUMN1 = "COLUMN1";

    private static final String COLUMN2 = "COLUMN2";

    @Mock
    private DataSourceRuntimeManager dataSourceRuntimeManager;

    @Mock
    private DatabaseMetadataService databaseMetadataService;

    @Mock
    private DataSetDefRegistry dataSetDefRegistry;

    @Mock
    private DataSetManager dataSetManager;

    private DataManagementService managementService;

    @Mock
    private DataSourceDeploymentInfo deploymentInfo;

    @Mock
    private DatabaseMetadata databaseMetadata;

    @Mock
    private DataSet dataSet;

    @Mock
    private List< DataColumn > dataColumns;

    @Mock
    private DataColumn column1;

    @Mock
    private DataColumn column2;

    @Before
    public void setup( ) {
        managementService = new DataManagementServiceImpl( dataSourceRuntimeManager,
                databaseMetadataService, dataSetDefRegistry, dataSetManager );

        dataColumns = new ArrayList<>( );
        dataColumns.add( column1 );
        dataColumns.add( column2 );
        when( column1.getId( ) ).thenReturn( COLUMN1 );
        when( column2.getId( ) ).thenReturn( COLUMN2 );
    }

    @Test
    public void testGetDisplayerSettings( ) throws Exception {

        String dataSetUuid = DATASOURCE_UUID + ":" + SCHEMA + ":" + TABLE;

        when( dataSourceRuntimeManager.getDataSourceDeploymentInfo( DATASOURCE_UUID ) ).thenReturn( deploymentInfo );
        when( deploymentInfo.getUuid( ) ).thenReturn( DATASOURCE_UUID );
        when( databaseMetadataService.getMetadata( DATASOURCE_UUID, false, false ) ).thenReturn( databaseMetadata );
        when( dataSetManager.lookupDataSet( new DataSetLookup( dataSetUuid ) ) ).thenReturn( dataSet );
        when( dataSet.getColumns( ) ).thenReturn( dataColumns );

        DisplayerSettings displayerSettings = managementService.getDisplayerSettings( DATASOURCE_UUID, SCHEMA, TABLE );

        ArgumentCaptor< SQLDataSetDef > argumentCaptor = ArgumentCaptor.forClass( SQLDataSetDef.class );
        verify( dataSetDefRegistry, times( 1 ) ).registerDataSetDef( argumentCaptor.capture( ) );

        // data set definition that should have been created internally
        SQLDataSetDef expectedDataSet = new SQLDataSetDef( );
        expectedDataSet.setUUID( dataSetUuid );
        expectedDataSet.setName( SCHEMA + "." + TABLE );
        expectedDataSet.setDataSource( DATASOURCE_UUID );
        expectedDataSet.setDbSchema( SCHEMA );
        expectedDataSet.setDbTable( TABLE );
        expectedDataSet.setPublic( false );

        // the expected data source should have been created.
        assertEqualsDataSet( expectedDataSet, argumentCaptor.getValue( ) );


        // expected displayer settings
        TableDisplayerSettingsBuilder settingsBuilder = DisplayerSettingsFactory.newTableSettings( )
                .dataset( dataSetUuid )
                .title( TABLE )
                .titleVisible( true )
                .tablePageSize( 20 )
                .tableOrderEnabled( true )
                .column( COLUMN1 )
                .column( COLUMN2 )
                .tableWidth( 100 * dataColumns.size( ) )
                .renderer( DefaultRenderer.UUID );

        assertEquals( settingsBuilder.buildSettings( ), displayerSettings );

    }

    private void assertEqualsDataSet( SQLDataSetDef expectedDataSet, SQLDataSetDef currentDataSet ) {
        assertEquals( expectedDataSet.getUUID( ), currentDataSet.getUUID( ) );
        assertEquals( expectedDataSet.getName( ), currentDataSet.getName( ) );
        assertEquals( expectedDataSet.getDataSource( ), currentDataSet.getDataSource( ) );
        assertEquals( expectedDataSet.getDbSchema( ), currentDataSet.getDbSchema( ) );
        assertEquals( expectedDataSet.getDbTable( ), currentDataSet.getDbTable( ) );
        assertEquals( expectedDataSet.isPublic( ), currentDataSet.isPublic( ) );
    }
}