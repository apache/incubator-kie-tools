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

package org.kie.workbench.common.screens.datasource.management.backend;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceDefDeployer;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceSettings;
import org.kie.workbench.common.screens.datasource.management.backend.core.DefaultDriverInitializer;
import org.kie.workbench.common.screens.datasource.management.backend.core.DriverDefDeployer;
import org.kie.workbench.common.screens.datasource.management.backend.service.DefChangeHandler;
import org.kie.workbench.common.screens.datasource.management.backend.service.DefResourceChangeObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;

import static org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceSettings.*;
import static org.kie.workbench.common.screens.datasource.management.util.ServiceUtil.*;

/**
 * Initializations required by the data sources management system.
 */
@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class DataSourceManagementBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceManagementBootstrap.class);

    public static final String DEPLOYMENTS_INITIALIZATION_RETRIES = DATASOURCE_MANAGEMENT_PREFIX + ".deploymentsInitializationRetries";

    public static final String DEPLOYMENTS_INITIALIZATION_DELAY = DATASOURCE_MANAGEMENT_PREFIX + ".deploymentsInitializationDelay";

    public static final String DEF_CHANGE_HANDLER_BEAN = DATASOURCE_MANAGEMENT_PREFIX + ".DefChangeHandler";

    private DataSourceRuntimeManager dataSourceRuntimeManager;

    private DataSourceDefDeployer dataSourceDefDeployer;

    private DriverDefDeployer driverDefDeployer;

    private DefaultDriverInitializer driverInitializer;

    private DefResourceChangeObserver defResourceChangeObserver;

    private BeanManager beanManager;

    protected long deploymentsInitializationRetries = 20;

    protected long deploymentsInitializationDelay = 30000;

    protected ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public DataSourceManagementBootstrap() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public DataSourceManagementBootstrap(DataSourceRuntimeManager dataSourceRuntimeManager,
                                         DataSourceDefDeployer dataSourceDefDeployer,
                                         DriverDefDeployer driverDefDeployer,
                                         DefaultDriverInitializer driverInitializer,
                                         DefResourceChangeObserver defResourceChangeObserver,
                                         BeanManager beanManager) {
        this.dataSourceRuntimeManager = dataSourceRuntimeManager;
        this.dataSourceDefDeployer = dataSourceDefDeployer;
        this.driverDefDeployer = driverDefDeployer;
        this.driverInitializer = driverInitializer;
        this.defResourceChangeObserver = defResourceChangeObserver;
        this.beanManager = beanManager;
    }

    @PostConstruct
    public void init() {
        initializeConfigParams();
        initializeDefChangeHandler();
        driverInitializer.initializeDefaultDrivers();
        scheduler.schedule(getInitializeDeploymentsTask(),
                           deploymentsInitializationDelay,
                           TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void destroy() {
        if (scheduler != null && !scheduler.isShutdown()) {
            try {
                scheduler.shutdownNow();
            } catch (Exception e) {
                //uncommon case
                logger.warn("DataSourceManagementBootstrap termination error: " + e.getMessage(),
                            e);
            }
        }
    }

    /**
     * Ensures that data sources and drivers definitions are properly deployed when the server is restarted.
     */
    protected void initializeDeployments() {
        driverDefDeployer.deployGlobalDefs();
        dataSourceDefDeployer.deployGlobalDefs();
    }

    /**
     * Initializes the data source and drivers definitions change handler when configured.
     */
    protected void initializeDefChangeHandler() {
        logger.info("Initializing data source definitions change handler");
        String defChangeHandlerName = getManagedProperty(DataSourceSettings.getInstance().getProperties(),
                                                         DEF_CHANGE_HANDLER_BEAN);
        if (defChangeHandlerName != null) {
            try {
                defResourceChangeObserver.setDefChangeHandler(getDefChangeHandler(defChangeHandlerName));
            } catch (Exception e) {
                logger.error("An error was produced during defChangeHandler initialization: " + defChangeHandlerName,
                             e);
            }
        } else {
            logger.info("defChangeHandler was not set");
        }
    }

    protected void initializeConfigParams() {
        String value = null;
        try {
            value = getManagedProperty(DataSourceSettings.getInstance().getProperties(),
                                       DEPLOYMENTS_INITIALIZATION_RETRIES,
                                       Long.toString(deploymentsInitializationRetries));
            deploymentsInitializationRetries = Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warn("Wrong integer value: " + value + " was set for property: " + DEPLOYMENTS_INITIALIZATION_RETRIES
                                + " The by default value: " + deploymentsInitializationRetries + " will be used instead.");
        }
        try {
            value = getManagedProperty(DataSourceSettings.getInstance().getProperties(),
                                       DEPLOYMENTS_INITIALIZATION_DELAY,
                                       Long.toString(deploymentsInitializationDelay));
            deploymentsInitializationDelay = Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warn("Wrong integer value: " + value + " was set for property: " + DEPLOYMENTS_INITIALIZATION_DELAY
                                + " The by default value: " + deploymentsInitializationDelay + " will be used instead.");
        }
    }

    protected Runnable getInitializeDeploymentsTask() {
        return () -> {
            try {
                logger.debug("Initialize deployments task started.");
                deploymentsInitializationRetries--;
                dataSourceRuntimeManager.hasStarted();
                initializeDeployments();
                logger.info("Initialize deployments task finished successfully.");
                scheduler.shutdown();
            } catch (Exception e) {
                logger.warn("Initialize deployments task finished with errors: " + e.getMessage());
                if (deploymentsInitializationRetries > 0) {
                    logger.warn("Startup drivers and datasources initialization will be retried again in: " + deploymentsInitializationDelay + " " + TimeUnit.MILLISECONDS.name() + "(" + deploymentsInitializationRetries + " attempts left)");
                    scheduler.schedule(getInitializeDeploymentsTask(),
                                       deploymentsInitializationDelay,
                                       TimeUnit.MILLISECONDS);
                } else {
                    logger.error("No more retries are available, some drivers or datasources might not work properly. " + e.getMessage(),
                                 e);
                    scheduler.shutdown();
                }
            }
        };
    }

    /**
     * for testing purposes.
     */
    protected DefChangeHandler getDefChangeHandler(String defChangeHandlerName) {
        return (DefChangeHandler) getManagedBean(beanManager,
                                                 defChangeHandlerName);
    }
}