/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.service;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetManagerCDI;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.DataSetDefRegistryCDI;
import org.dashbuilder.dataset.backend.EditDataSetDef;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.exception.ExceptionManager;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
@Service
public class DataSetDefVfsServicesImpl implements DataSetDefVfsServices {

    private static final String SYSTEM = "system";
    protected User identity;
    protected DataSetDefRegistryCDI dataSetDefRegistry;
    protected DataSetManagerCDI dataSetManager;
    protected ExceptionManager exceptionManager;

    public DataSetDefVfsServicesImpl() {
    }

    @Inject
    public DataSetDefVfsServicesImpl(User identity,
                                     DataSetDefRegistryCDI dataSetDefRegistry,
                                     DataSetManagerCDI dataSetManager,
                                     ExceptionManager exceptionManager) {
        this.identity = identity;
        this.dataSetDefRegistry = dataSetDefRegistry;
        this.dataSetManager = dataSetManager;
        this.exceptionManager = exceptionManager;
    }

    @Override
    public Path resolve(DataSetDef dataSetDef) {
        return dataSetDefRegistry.resolveVfsPath(dataSetDef);
    }

    @Override
    public DataSetDef get(Path path) {
        return dataSetDefRegistry.loadDataSetDef(path);
    }

    @Override
    public EditDataSetDef load(Path path) {
        try {
            DataSetDef def = dataSetDefRegistry.loadDataSetDef(path);
            if (def == null) {
                return null;
            }

            // Clone the definition
            DataSetDef cloned = def.clone();

            // Enable all columns and set columns to null, force to obtain metadata with all original columns
            // and all original column types.
            boolean clonedAllColumns = cloned.isAllColumnsEnabled();
            List<DataColumnDef> clonedColumns = cloned.getColumns();
            cloned.setAllColumnsEnabled(true);
            cloned.setColumns(null);

            // Obtain all original columns and all original column types.
            DataSetMetadata _cd = dataSetManager.resolveProvider(cloned)
                    .getDataSetMetadata(cloned);

            // Return the list of original columns and its types.
            List<DataColumnDef> columns = new ArrayList<DataColumnDef>();
            if (_cd.getNumberOfColumns() > 0) {
                for (int x = 0; x < _cd.getNumberOfColumns(); x++) {
                    String cId = _cd.getColumnId(x);
                    ColumnType cType = _cd.getColumnType(x);
                    DataColumnDef cdef = new DataColumnDef(cId, cType);
                    columns.add(cdef);
                }
            }

            // Set columns attributes as initially were.
            cloned.setAllColumnsEnabled(clonedAllColumns);
            cloned.setColumns(clonedColumns);
            return new EditDataSetDef(cloned, columns);

        } catch (Exception e) {
            throw exceptionManager.handleException(e);
        }
    }

    @Override
    public Path save(DataSetDef definition, String commitMessage) {
        dataSetDefRegistry.registerDataSetDef(definition,
                identity != null ? identity.getIdentifier() : SYSTEM,
                commitMessage);
        return dataSetDefRegistry.resolveVfsPath(definition);
    }

    @Override
    public Path copy(Path path, String newName, String commitMessage) {
        DataSetDef def = dataSetDefRegistry.loadDataSetDef(path);
        if (def == null) {
            throw exceptionManager.handleException(
                    new Exception("Data set definition not found: " + path.getFileName()));
        }
        DataSetDef clone = dataSetDefRegistry.copyDataSetDef(def, newName,
                identity != null ? identity.getIdentifier() : SYSTEM,
                commitMessage);
        return dataSetDefRegistry.resolveVfsPath(clone);
    }

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final Path targetDirectory,
                      final String comment ) {
        throw new UnsupportedOperationException( "A data set definition cannot be copied to another directory." );
    }

    @Override
    public void delete(Path path, String commitMessage) {
        dataSetDefRegistry.removeDataSetDef(path,
                identity != null ? identity.getIdentifier() : SYSTEM,
                commitMessage);
    }
}
