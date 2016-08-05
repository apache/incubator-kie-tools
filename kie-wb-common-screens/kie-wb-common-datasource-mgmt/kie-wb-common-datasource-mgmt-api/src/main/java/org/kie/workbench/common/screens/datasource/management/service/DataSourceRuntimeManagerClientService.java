/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.TestResult;

/**
 * Convenient client side methods to interact with the DataSourceRuntimeManager.
 */
@Remote
public interface DataSourceRuntimeManagerClientService {

    /**
     * Gets the runtime information about a data source.
     *
     * @param uuid data source identifier.
     *
     * @return the runtime information about the data source or null if no data source was registered with the given uuid.
     */
    DataSourceDeploymentInfo getDataSourceDeploymentInfo( String uuid );

    /**
     * Gets the runtime information about a driver.
     *
     * @param uuid driver identifier.
     *
     * @return the runtime information about the driver or null if not driver was registered with the given uuid.
     */
    DriverDeploymentInfo getDriverDeploymentInfo( String uuid );

    /**
     * Tests a data source in the data source management system.
     *
     * @param uuid data source identifier to test.
     *
     * @return a TestResult object with the information about the test.
     */
    TestResult testDataSource( final String uuid );

}
