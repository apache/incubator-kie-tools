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

package org.kie.workbench.common.screens.datasource.management.service;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.TableMetadata;

/**
 * Service for getting information about a database structure.
 */
@Remote
public interface DatabaseMetadataService {

    /**
     * Gets the metadata for the database pointed by a given data source.
     * @param dataSourceUuid A data source uuid.
     * @return the metadata object for the given database.
     */
    DatabaseMetadata getMetadata( String dataSourceUuid, boolean includeCatalogs, boolean includeSchemas );

    /**
     * Gets a list of database objects metadata for a given database.
     * @param dataSourceUuid A data source uuid.
     * @param schema A schema name for filtering. A null value will query all the available schemas.
     * @param types A list of database object types for filtering.
     * @return A list of database objects fulfilling the filtering criteria.
     */
    List< TableMetadata > findTables( String dataSourceUuid, String schema, DatabaseMetadata.TableType... types );

    /**
     * Gets a list of database objects metadata for a given database.
     * @param dataSourceUuid A data source uuid.
     * @param schema A schema name for filtering. A null value will query all the available schemas.
     * @param tableNamePattern A table name pattern for filtering the database objects by name, e.g. %INVOICE_%.
     * @param types A list of database object types for filtering.
     * @return A list of database objects fulfilling the filtering criteria.
     */
    List< TableMetadata > findTables( String dataSourceUuid,
                                      String schema,
                                      String tableNamePattern,
                                      DatabaseMetadata.TableType... types );
}