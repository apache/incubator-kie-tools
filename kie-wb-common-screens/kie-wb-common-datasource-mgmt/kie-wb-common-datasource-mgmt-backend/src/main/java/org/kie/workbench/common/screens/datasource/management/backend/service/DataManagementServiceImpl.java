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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.TableDisplayerSettingsBuilder;
import org.dashbuilder.renderer.client.DefaultRenderer;
import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataManagementService;
import org.kie.workbench.common.screens.datasource.management.service.DatabaseMetadataService;
import org.kie.workbench.common.screens.datasource.management.util.DataSetDefBuilder;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

@Service
@ApplicationScoped
public class DataManagementServiceImpl
        implements DataManagementService {

    private DataSourceRuntimeManager dataSourceRuntimeManager;

    private DatabaseMetadataService databaseMetadataService;

    private DataSetDefRegistry dataSetDefRegistry;

    private DataSetManager dataSetManager;

    private static final int COLUMN_WIDTH = 100;

    private static final String SEPARATOR = "#";

    @Inject
    public DataManagementServiceImpl(DataSourceRuntimeManager dataSourceRuntimeManager,
                                     DatabaseMetadataService databaseMetadataService,
                                     DataSetDefRegistry dataSetDefRegistry,
                                     DataSetManager dataSetManager) {
        this.dataSourceRuntimeManager = dataSourceRuntimeManager;
        this.databaseMetadataService = databaseMetadataService;
        this.dataSetDefRegistry = dataSetDefRegistry;
        this.dataSetManager = dataSetManager;
    }

    @Override
    public DisplayerSettings getDisplayerSettings(String dataSourceUuid,
                                                  String schema,
                                                  String table) {
        checkNotNull("dataSourceUuid",
                     dataSourceUuid);
        checkNotNull("table",
                     table);
        try {
            DataSourceDeploymentInfo deploymentInfo = dataSourceRuntimeManager.getDataSourceDeploymentInfo(dataSourceUuid);
            DataSetDef dataSetDef = DataSetDefBuilder.newBuilder()
                    .dataSetUuid(buildDataSetUuid(dataSourceUuid,
                                                  schema,
                                                  table))
                    .dataSetName(buildDataSetName(schema,
                                                  table))
                    .dataSourceUuid(deploymentInfo.getUuid())
                    .schema(schema)
                    .table(buildDataSetTableName(dataSourceUuid,
                                                 table))
                    .isPublic(false)
                    .build();

            dataSetDefRegistry.registerDataSetDef(dataSetDef);
            DataSetLookup lookup = new DataSetLookup();
            lookup.setDataSetUUID(dataSetDef.getUUID());
            DataSet dataSet = dataSetManager.lookupDataSet(lookup);

            TableDisplayerSettingsBuilder settingsBuilder = DisplayerSettingsFactory.newTableSettings()
                    .dataset(dataSetDef.getUUID())
                    .title(table)
                    .titleVisible(true)
                    .tablePageSize(20)
                    .tableOrderEnabled(true);

            List<DataColumn> columns = dataSet.getColumns();
            for (DataColumn column : columns) {
                settingsBuilder.column(column.getId());
            }
            int tableWith = columns.size() * COLUMN_WIDTH;
            settingsBuilder.tableWidth(tableWith);
            settingsBuilder.renderer(DefaultRenderer.UUID);

            return settingsBuilder.buildSettings();
        } catch (Exception e) {
            throw new GenericPortableException(e.getMessage());
        }
    }

    private String buildDataSetUuid(String dataSourceUuid,
                                    String schema,
                                    String table) {
        return dataSourceUuid + SEPARATOR + schema + SEPARATOR + table;
    }

    private String buildDataSetName(String schema,
                                    String table) {
        return schema + "." + table;
    }

    private String buildDataSetTableName(String dataSourceUuid,
                                         String table) throws Exception {
        String result = table;
        DatabaseMetadata metadata = databaseMetadataService.getMetadata(dataSourceUuid,
                                                                        false,
                                                                        false);
        if (metadata.getDatabaseType() != null) {
            switch (metadata.getDatabaseType()) {
                case POSTGRESQL:
                    result = "\"" + table + "\"";
                    break;
                case H2:
                case MYSQL:
                case MARIADB:
                case ORACLE:
                case SQLSERVER:
                case DB2:
                    result = table;
            }
        }
        return result;
    }
}