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

import org.dashbuilder.displayer.DisplayerSettings;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface DataManagementService {

    /**
     * Calculates the required settings needed to draw a TableDisplayer for a given database table.
     * @param dataSourceUuid a datasource identifier.
     * @param schema a schema name where the table is located. (null values are accepted)
     * @param table the name of the table that will be displayed. (null values are not accepted.
     * @return the calculated displayer settings required for drawing the table.
     */
    DisplayerSettings getDisplayerSettings( String dataSourceUuid, String schema, String table );

}