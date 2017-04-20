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

import java.util.List;
import java.util.Properties;

import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;

/**
 * Class for managing the deployment of drivers in a given system e.g. the Wildlfy server.
 */
public interface DriverProvider {

    /**
     * Used to pass initial configurations to the provider.
     */
    void loadConfig(Properties properties);

    /**
     * Deploys a driver source in the target system.
     */
    DriverDeploymentInfo deploy(final DriverDef driverDef) throws Exception;

    /**
     * Resyncs an already deployed data source.
     */
    DriverDeploymentInfo resync(DriverDef driverDef,
                                DriverDeploymentInfo deploymentInfo) throws Exception;

    /**
     * Un-deploys a driver from the target system.
     */
    void undeploy(final DriverDeploymentInfo deploymentInfo) throws Exception;

    /**
     * Gets the deployment information form an already deployed driver.
     * @param uuid the driver identifier.
     * @return The deployment information or null if the driver wasn't deployed.
     * @throws Exception in cases e.g. when communication with the target system e.g. the Wildlfy server fails.
     */
    DriverDeploymentInfo getDeploymentInfo(final String uuid) throws Exception;

    /**
     * Gets the deployment information for all the deployed drivers in the target system.
     * @return a list with current deployments.
     * @throws Exception in cases e.g. when communication with the target system e.g. the Wildlfy server fails.
     */
    List< DriverDeploymentInfo > getDeploymentsInfo() throws Exception;

    /**
     * Indicates if the DriverProvider has started properly.
     * @throws Exception if the DriverProvider has not started throws an exception.
     */
    void hasStarted() throws Exception;
}