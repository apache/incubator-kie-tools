/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.displayer.DisplayerSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.service.DatabaseMetadataService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceManagementTestConstants.SEPARATOR;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataManagementServiceImplTest {

    private static final String DATASOURCE_UUID = "DATASOURCE_UUID";

    private static final String SCHEMA = "SCHEMA";

    private static final String TABLE = "TABLE";

    private static final int COLUMNS_COUNT = 6;

    private static final int COLUMN_WIDTH = 100;

    private static final String DATA_COLUMN = "DATA_COLUMN";

    @Mock
    private DataSourceRuntimeManager dataSourceRuntimeManager;

    @Mock
    private DatabaseMetadataService databaseMetadataService;

    @Mock
    private DataSetDefRegistry dataSetDefRegistry;

    @Mock
    private DataSetManager dataSetManager;

    @Mock
    private DataManagementServiceImpl dataManagementService;

    @Mock
    private DataSourceDeploymentInfo dataSourceDeploymentInfo;

    @Mock
    private DatabaseMetadata databaseMetadata;

    private ArgumentCaptor<DataSetDef> dataSetDefCaptor;

    private String expectedDataSetUUID;

    @Before
    public void setUp() throws Exception {
        dataSetDefCaptor = ArgumentCaptor.forClass(DataSetDef.class);
        when(dataSourceRuntimeManager.getDataSourceDeploymentInfo(DATASOURCE_UUID)).thenReturn(dataSourceDeploymentInfo);
        when(dataSourceDeploymentInfo.getUuid()).thenReturn(DATASOURCE_UUID);
        when(databaseMetadataService.getMetadata(DATASOURCE_UUID,
                                                 false,
                                                 false)).thenReturn(databaseMetadata);

        dataManagementService = new DataManagementServiceImpl(dataSourceRuntimeManager,
                                                              databaseMetadataService,
                                                              dataSetDefRegistry,
                                                              dataSetManager);
    }

    @Test
    public void testGetDisplayerSettings() {
        expectedDataSetUUID = DATASOURCE_UUID + SEPARATOR + SCHEMA + SEPARATOR + TABLE;

        DataSetLookup expectedLookup = new DataSetLookup();
        expectedLookup.setDataSetUUID(expectedDataSetUUID);

        DataSet expectedSet = mock(DataSet.class);
        List<DataColumn> dataColumns = new ArrayList<>();
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            DataColumn dataColumn = mock(DataColumn.class);
            when(dataColumn.getId()).thenReturn(DATA_COLUMN + String.valueOf(i));
            dataColumns.add(dataColumn);
        }
        when(expectedSet.getColumns()).thenReturn(dataColumns);
        when(dataSetManager.lookupDataSet(expectedLookup)).thenReturn(expectedSet);

        DisplayerSettings settings = dataManagementService.getDisplayerSettings(DATASOURCE_UUID,
                                                                                SCHEMA,
                                                                                TABLE);

        verify(dataSetDefRegistry,
               times(1)).registerDataSetDef(dataSetDefCaptor.capture());
        verifyDataSetDef(dataSetDefCaptor.getValue());
        verifySettings(settings);
    }

    private void verifyDataSetDef(DataSetDef dataSetDef) {
        assertEquals(DATASOURCE_UUID + SEPARATOR + SCHEMA + SEPARATOR + TABLE,
                     dataSetDef.getUUID());
        assertEquals(SCHEMA + "." + TABLE,
                     dataSetDef.getName());
        assertEquals(DATASOURCE_UUID,
                     ((SQLDataSetDef) dataSetDef).getDataSource());
        assertEquals(SCHEMA,
                     ((SQLDataSetDef) dataSetDef).getDbSchema());
        assertEquals(TABLE,
                     ((SQLDataSetDef) dataSetDef).getDbTable());
    }

    private void verifySettings(DisplayerSettings settings) {
        assertEquals(DATASOURCE_UUID + SEPARATOR + SCHEMA + SEPARATOR + TABLE,
                     settings.getDataSetLookup().getDataSetUUID());
        assertEquals(TABLE,
                     settings.getTitle());
        assertEquals(true,
                     settings.isTitleVisible());
        assertEquals(20,
                     settings.getTablePageSize());
        assertEquals(true,
                     settings.isTableSortEnabled());

        assertEquals(1,
                     settings.getDataSetLookup().getOperationList().size());
        DataSetGroup dataSetOp = (DataSetGroup) settings.getDataSetLookup().getOperationList().get(0);
        List<GroupFunction> groupFunctions = dataSetOp.getGroupFunctions();
        assertEquals(COLUMNS_COUNT,
                     groupFunctions.size());

        for (int i = 0; i < groupFunctions.size(); i++) {
            assertEquals(DATA_COLUMN + String.valueOf(i),
                         groupFunctions.get(i).getColumnId());
        }
        assertEquals(COLUMN_WIDTH * COLUMNS_COUNT,
                     settings.getTableWidth());
    }
}
