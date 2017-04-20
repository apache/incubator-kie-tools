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

import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;

/**
 * Class for managing the deployment of data sources in a given system e.g. the Wildlfy server.
 */
public interface DataSourceProvider {

    /**
     * Used to pass initial configurations to the provider.
     */
    void loadConfig(Properties properties);

    /**
     * Deploys a data source in the target system.
     */
    DataSourceDeploymentInfo deploy(final DataSourceDef dataSourceDef) throws Exception;

    /**
     * Resyncs an already deployed data source.
     */
    DataSourceDeploymentInfo resync(final DataSourceDef dataSourceDef,
                                    final DataSourceDeploymentInfo deploymentInfo) throws Exception;

    /**
     * Un-deploys a data source from the target system.
     */
    void undeploy(final DataSourceDeploymentInfo deploymentInfo) throws Exception;

    /**
     * Gets the deployment information form an already deployed data source.
     * @param uuid the data source identifier.
     * @return The deployment information or null if the data source wasn't deployed.
     * @throws Exception in cases e.g. when communication with the target system e.g. the Wildlfy server fails.
     */
    DataSourceDeploymentInfo getDeploymentInfo(final String uuid) throws Exception;

    /**
     * Gets the deployment information for all the deployed data sources in the target system.
     * @return a list with current deployments.
     * @throws Exception in cases e.g. when communication with the target system e.g. the Wildlfy server fails.
     */
    List< DataSourceDeploymentInfo > getDeploymentsInfo() throws Exception;

    /**
     * Gets the definitions for all the deployed data sources in the target system.
     * @return a list with the definitions.
     * @throws Exception in cases e.g. when communication with the target system e.g. the Wildlfy server fails.
     */
    List< DataSourceDef > getDeployments() throws Exception;

    /**
     * Gets a reference to a data source previously deployed in the system.
     * @param deploymentInfo Deployment information for the data source.
     * @return The data given data source instance.
     * @throws Exception if the data source is not deployed or e.g. when communication with the
     * target system e.g. the Wildlfy server fails.
     */
    DataSource lookupDataSource(DataSourceDeploymentInfo deploymentInfo) throws Exception;

    /**
     * Indicates if the DataSourceProvider has started properly.
     * @throws Exception if the DataSourceProvider has not started throws an exception.
     */
    void hasStarted() throws Exception;
}
