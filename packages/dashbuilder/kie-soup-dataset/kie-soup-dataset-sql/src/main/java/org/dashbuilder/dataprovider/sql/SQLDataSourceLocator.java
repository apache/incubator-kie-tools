/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.sql;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.def.SQLDataSourceDef;

/**
 * Data source locator interface for SQL providers
 */
public interface SQLDataSourceLocator {

    /**
     * Get the data source referenced in the SQL data set definition
     */
    DataSource lookup(SQLDataSetDef def) throws Exception;

    /**
     * Get the list of available data source definitions for connecting to SQL databases
     *
     * @return A list of data source definitions
     */
    default List<SQLDataSourceDef> list() {
        return new ArrayList<>();
    }
}
