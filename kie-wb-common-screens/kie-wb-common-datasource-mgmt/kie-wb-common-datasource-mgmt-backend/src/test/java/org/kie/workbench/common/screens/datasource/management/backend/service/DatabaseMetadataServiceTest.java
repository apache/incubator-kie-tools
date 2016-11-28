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

import java.sql.Connection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSource;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.TableMetadata;
import org.kie.workbench.common.screens.datasource.management.service.DatabaseMetadataService;
import org.kie.workbench.common.screens.datasource.management.util.DatabaseMetadataUtil;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( PowerMockRunner.class )
@PrepareForTest( DatabaseMetadataUtil.class )
public class DatabaseMetadataServiceTest {

    private static final String DATASOURCE_UUID = "DATASOURCE_UUID";

    private static final String SCHEMA = "SCHEMA";

    private static final String PATTERN = "PATTERN";

    @Mock
    private DataSourceRuntimeManager runtimeManager;

    private DatabaseMetadataService metadataService;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection conn;

    @Mock
    private DatabaseMetadata metadata;

    @Mock
    private List< TableMetadata > tables;

    private DatabaseMetadata.TableType[] types = { DatabaseMetadata.TableType.ALL };

    @Before
    public void setup( ) throws Exception {
        metadataService = new DatabaseMetadataServiceImpl( runtimeManager );

        when( runtimeManager.lookupDataSource( DATASOURCE_UUID ) ).thenReturn( dataSource );
        when( dataSource.getConnection( ) ).thenReturn( conn );
    }

    /**
     * Tests the execution of the getMetadata method.
     */
    public void getMetadata( ) throws Exception {
        PowerMockito.mockStatic( DatabaseMetadataUtil.class );
        boolean includeCatalogs = true;
        boolean includeSchemas = true;
        PowerMockito.when( DatabaseMetadataUtil.getMetadata( conn, true, true ) ).thenReturn( metadata );
        DatabaseMetadata result = metadataService.getMetadata( DATASOURCE_UUID, includeCatalogs, includeSchemas );
        // the result metadata should be the same as the returned by he DatabaseMetadataUtil class.
        assertEquals( metadata, result );
    }

    /**
     * Tests the execution of the findTables method when the pattern parameter is used.
     */
    @Test
    public void testFindWithPattern( ) throws Exception {
        PowerMockito.mockStatic( DatabaseMetadataUtil.class );
        PowerMockito.when( DatabaseMetadataUtil.findTables( conn, SCHEMA, PATTERN, types ) ).thenReturn( tables );
        List< TableMetadata > result = metadataService.findTables( DATASOURCE_UUID, SCHEMA, PATTERN, types );
        // the result should be the same as the returned by the DatabaseMetadataUtil class.
        assertEquals( tables, result );
    }

    /**
     * Tests the execution of the findTables method when the pattern parameter is not used.
     */
    @Test
    public void testFindWithoutPattern( ) throws Exception {
        PowerMockito.mockStatic( DatabaseMetadataUtil.class );
        PowerMockito.when( DatabaseMetadataUtil.findTables( conn, SCHEMA, "%", types ) ).thenReturn( tables );
        List< TableMetadata > result = metadataService.findTables( DATASOURCE_UUID, SCHEMA, types );
        // the result should be the same as the returned by the DatabaseMetadataUtil class.
        assertEquals( tables, result );
    }
}