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

package org.kie.workbench.common.screens.datasource.management.backend.core.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datasource.management.backend.core.DataSource;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceProvider;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceProviderFactory;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.backend.core.DriverDeploymentCache;
import org.kie.workbench.common.screens.datasource.management.backend.core.DriverDeploymentCacheEntry;
import org.kie.workbench.common.screens.datasource.management.backend.core.DriverProvider;
import org.kie.workbench.common.screens.datasource.management.backend.core.UnDeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DataSourceRuntimeManagerImpl
        implements DataSourceRuntimeManager {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceRuntimeManagerImpl.class);

    private DataSourceProvider dataSourceProvider;

    private DriverProvider driverProvider;

    private DriverDeploymentCache driverDeploymentCache = new DriverDeploymentCacheImpl();

    private DataSourceProviderFactory providerFactory;

    public DataSourceRuntimeManagerImpl() {
    }

    @Inject
    public DataSourceRuntimeManagerImpl(DataSourceProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }

    @PostConstruct
    protected void init() {
        dataSourceProvider = providerFactory.getDataSourceProvider();
        driverProvider = providerFactory.getDriverProvider();
    }

    @Override
    public synchronized DataSourceDeploymentInfo deployDataSource(DataSourceDef dataSourceDef,
                                                                  DeploymentOptions options) throws Exception {
        try {
            DataSourceDeploymentInfo dataSourceDeploymentInfo = dataSourceProvider.getDeploymentInfo(dataSourceDef.getUuid());
            DriverDeploymentInfo driverDeploymentInfo = driverProvider.getDeploymentInfo(dataSourceDef.getDriverUuid());

            if (dataSourceDeploymentInfo != null) {
                if (options.isCreateOrResyncDeployment()) {
                    dataSourceDeploymentInfo = dataSourceProvider.resync(dataSourceDef,
                                                                         dataSourceDeploymentInfo);
                } else {
                    throw new Exception("Data source: " + dataSourceDef + " is already deployed");
                }
            } else if (driverDeploymentInfo != null) {
                dataSourceDeploymentInfo = dataSourceProvider.deploy(dataSourceDef);
            } else {
                throw new Exception("Required driver: " + dataSourceDef.getDriverUuid() + " is not deployed.");
            }

            if (driverDeploymentInfo != null && driverDeploymentCache.get(driverDeploymentInfo) != null) {
                driverDeploymentCache.get(driverDeploymentInfo).addDependant(dataSourceDeploymentInfo);
            }

            return dataSourceDeploymentInfo;
        } catch (Exception e) {
            logger.error("Data source deployment failed for dataSourceDef: " + dataSourceDef,
                         e);
            throw e;
        }
    }

    @Override
    public synchronized DataSourceDeploymentInfo getDataSourceDeploymentInfo(String uuid) throws Exception {
        try {
            return dataSourceProvider.getDeploymentInfo(uuid);
        } catch (Exception e) {
            logger.error("It was not possible to read the deploymentInfo for data source: " + uuid);
            throw e;
        }
    }

    @Override
    public synchronized void unDeployDataSource(DataSourceDeploymentInfo deploymentInfo,
                                                UnDeploymentOptions options)
            throws Exception {
        try {
            dataSourceProvider.undeploy(deploymentInfo);
            deReferFromDrivers(deploymentInfo);
        } catch (Exception e) {
            logger.error("Data source un-deployment failed for deploymentInfo: " + deploymentInfo,
                         e);
            throw e;
        }
    }

    @Override
    public synchronized DriverDeploymentInfo deployDriver(DriverDef driverDef,
                                                          DeploymentOptions options) throws Exception {
        try {
            DriverDeploymentInfo deploymentInfo = driverProvider.getDeploymentInfo(driverDef.getUuid());
            if (deploymentInfo != null) {
                if (options.isCreateOrResyncDeployment()) {
                    deploymentInfo = driverProvider.resync(driverDef,
                                                           deploymentInfo);
                } else {
                    throw new Exception("Driver: " + driverDef + " is already deployed.");
                }
            } else {
                deploymentInfo = driverProvider.deploy(driverDef);
            }

            driverDeploymentCache.put(deploymentInfo,
                                      driverDef);
            return deploymentInfo;
        } catch (Exception e) {
            logger.error("Driver deployment failed for driverDef: " + driverDef,
                         e);
            throw e;
        }
    }

    @Override
    public synchronized DriverDeploymentInfo getDriverDeploymentInfo(String uuid) throws Exception {
        try {
            DriverDeploymentInfo deploymentInfo = driverProvider.getDeploymentInfo(uuid);
            if (deploymentInfo != null && driverDeploymentCache.get(deploymentInfo) != null) {
                DriverDeploymentInfo updatedInfo = new DriverDeploymentInfo(deploymentInfo.getDeploymentId(),
                                                                            deploymentInfo.getDriverDeploymentId(),
                                                                            deploymentInfo.isManaged(),
                                                                            deploymentInfo.getUuid(),
                                                                            deploymentInfo.getDriverClass());
                updatedInfo.getDependants().addAll(driverDeploymentCache.get(deploymentInfo).getDependants());
                deploymentInfo = updatedInfo;
            }
            return deploymentInfo;
        } catch (Exception e) {
            logger.error("It was not possible to read the deploymentInfo for driver: " + uuid);
            throw e;
        }
    }

    @Override
    public synchronized void unDeployDriver(DriverDeploymentInfo deploymentInfo,
                                            UnDeploymentOptions options) throws Exception {
        try {
            DriverDeploymentCacheEntry cacheEntry = driverDeploymentCache.get(deploymentInfo);
            if (cacheEntry != null && cacheEntry.hasDependants() && options.isSoftUnDeployment()) {
                throw new Exception("Driver: " + deploymentInfo + " can't be un-deployed. " +
                                            "It's currently referenced by : " + cacheEntry.getDependants().size() + " data sources");
            }
            driverDeploymentCache.remove(deploymentInfo);
            driverProvider.undeploy(deploymentInfo);
        } catch (Exception e) {
            logger.error("Driver un-deployment failed for deploymentInfo: " + deploymentInfo,
                         e);
            throw e;
        }
    }

    @Override
    public synchronized DataSource lookupDataSource(String uuid) throws Exception {
        DataSourceDeploymentInfo deploymentInfo = dataSourceProvider.getDeploymentInfo(uuid);
        if (deploymentInfo != null) {
            return dataSourceProvider.lookupDataSource(deploymentInfo);
        } else {
            throw new Exception("Data source: " + uuid + " is not deployed in current system.");
        }
    }

    @Override
    public void hasStarted() throws Exception {
        driverProvider.hasStarted();
        dataSourceProvider.hasStarted();
    }

    private void deReferFromDrivers(DataSourceDeploymentInfo deploymentInfo) {
        for (DriverDeploymentCacheEntry entry : driverDeploymentCache.findReferencedEntries(deploymentInfo)) {
            entry.removeDependant(deploymentInfo);
        }
    }
}
