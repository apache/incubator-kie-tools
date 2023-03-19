/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.dashbuilder.backend.services.dataset.sql.SQLDataSourceLoader;
import org.dashbuilder.dataprovider.sql.SQLDataSourceLocator;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.def.SQLDataSourceDef;

@ApplicationScoped
public class RuntimeSQLDataSourceLocator implements SQLDataSourceLocator {

    @Inject
    SQLDataSourceLoader sqlDataSourceLoader;

    @Override
    public DataSource lookup(SQLDataSetDef def) throws Exception {
        return sqlDataSourceLoader.datasources()
                                  .stream()
                                  .filter(ds -> ds.equals(def.getName()) ||
                                                ds.equals(def.getUUID()) ||
                                                ds.equals(def.getDataSource()))
                                  .findFirst()
                                  .flatMap(sqlDataSourceLoader::getDataSource)
                                  .orElseThrow(() -> dataSourceNotFound(def));

    }

    @Override
    public List<SQLDataSourceDef> list() {
        return sqlDataSourceLoader.datasources()
                                  .stream()
                                  .map(ds -> new SQLDataSourceDef(ds, ""))
                                  .collect(Collectors.toList());
    }

    private IllegalArgumentException dataSourceNotFound(SQLDataSetDef def) {
        return new IllegalArgumentException("Datasource for definition " + def.getName() + " not found");
    }

}