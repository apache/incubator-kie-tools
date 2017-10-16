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

import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSource;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.TableMetadata;
import org.kie.workbench.common.screens.datasource.management.service.DatabaseMetadataService;
import org.kie.workbench.common.screens.datasource.management.util.DatabaseMetadataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

@Service
@ApplicationScoped
public class DatabaseMetadataServiceImpl
        implements DatabaseMetadataService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMetadataServiceImpl.class);

    private DataSourceRuntimeManager dataSourceRuntimeManager;

    public DatabaseMetadataServiceImpl() {
    }

    @Inject
    public DatabaseMetadataServiceImpl(DataSourceRuntimeManager dataSourceRuntimeManager) {
        this.dataSourceRuntimeManager = dataSourceRuntimeManager;
    }

    @Override
    public DatabaseMetadata getMetadata(String dataSourceUuid,
                                        boolean includeCatalogs,
                                        boolean includeSchemas) {
        checkNotNull("dataSourceUuid",
                     dataSourceUuid);
        try {
            DataSource dataSource = dataSourceRuntimeManager.lookupDataSource(dataSourceUuid);
            return DatabaseMetadataUtil.getMetadata(dataSource.getConnection(),
                                                    includeCatalogs,
                                                    includeSchemas);
        } catch (Exception e) {
            logger.error("It was not possible to get database metadata for data source: " + dataSourceUuid,
                         e);
            throw new GenericPortableException("It was not possible to get database metadata for data source: "
                                                       + dataSourceUuid + ": " + e.getMessage(),
                                               e);
        }
    }

    @Override
    public List<TableMetadata> findTables(String dataSourceUuid,
                                          String schema,
                                          DatabaseMetadata.TableType... types) {
        return findTables(dataSourceUuid,
                          schema,
                          "%",
                          types);
    }

    @Override
    public List<TableMetadata> findTables(String dataSourceUuid,
                                          String schema,
                                          String tableNamePattern,
                                          DatabaseMetadata.TableType... types) {
        checkNotNull("dataSourceUuid",
                     dataSourceUuid);
        checkNotNull("types",
                     types);
        try {
            DataSource dataSource = dataSourceRuntimeManager.lookupDataSource(dataSourceUuid);
            return DatabaseMetadataUtil.findTables(dataSource.getConnection(),
                                                   schema,
                                                   tableNamePattern,
                                                   types);
        } catch (Exception e) {
            logger.error("It was not possible to get database metadata for data source: " + dataSourceUuid,
                         e);
            throw new GenericPortableException("It was not possible to get database metadata for data source: "
                                                       + dataSourceUuid + ": " + e.getMessage(),
                                               e);
        }
    }
}