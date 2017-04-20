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

package org.kie.workbench.common.screens.datasource.management.backend.core;

import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;

/**
 * Runtime system for the data sources management.
 */
public interface DataSourceRuntimeManager {

    /**
     * Deploys a data source definition in the data sources runtime system.
     * @param dataSourceDef Data source definition to be deployed.
     * @param options deployment options to apply.
     * @return the deployment information if the deployment was successful, an exception is thrown in any other case.
     */
    DataSourceDeploymentInfo deployDataSource(DataSourceDef dataSourceDef,
                                              DeploymentOptions options) throws Exception;

    /**
     * Gets the deployment information for given data source.
     * @param uuid the data source identifier.
     * @return The deployment information or null if the data source wasn't deployed.
     * @throws Exception if the deployment information couldn't be retrieved.
     */
    DataSourceDeploymentInfo getDataSourceDeploymentInfo(String uuid) throws Exception;

    /**
     * Un-deploys a data source from the data sources runtime system.
     * @param deploymentInfo the deployment information for a previously deployed data source.
     * @param options un-deployment options to apply.
     * @throws Exception if the un-deployment failed.
     */
    void unDeployDataSource(DataSourceDeploymentInfo deploymentInfo,
                            UnDeploymentOptions options) throws Exception;

    /**
     * Deploys a driver in the data sources runtime system.
     * @param driverDef Driver definition to be deployed.
     * @param options deployment options to appy.
     */
    DriverDeploymentInfo deployDriver(DriverDef driverDef,
                                      DeploymentOptions options) throws Exception;

    /**
     * Gets the deployment information for a given driver.
     * @param uuid the driver identifier.
     * @return The deployment information or null if the driver wasn't deployed.
     * @throws Exception if the deployment information couldn't be retrieved.
     */
    DriverDeploymentInfo getDriverDeploymentInfo(String uuid) throws Exception;

    /**
     * Un-deploys a driver from data sources runtime system.
     * @param deploymentInfo deployment information about the driver to un-deploy
     * @param options un-deployment options to apply.
     * @throws Exception if the un-deployment failed.
     */
    void unDeployDriver(DriverDeploymentInfo deploymentInfo,
                        UnDeploymentOptions options) throws Exception;

    /**
     * Gets a reference to a previously deployed data source.
     * @param uuid a data source identifier.
     * @return if the data source is properly deployed a reference to the data source is returned, in any other case
     * an exception is thrown.
     * @throws Exception if the data source is not deployed or in cases when there are a communication error with the
     * server, etc.
     */
    DataSource lookupDataSource(String uuid) throws Exception;

    /**
     * Indicates if the DataSourceRuntimeManager has started properly.
     * @throws Exception if the DataSourceManagement runtime has not started throws an exception.
     */
    void hasStarted() throws Exception;
}