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

import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceProvider;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceProviderFactory;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceSettings;
import org.kie.workbench.common.screens.datasource.management.backend.core.DriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceSettings.*;
import static org.kie.workbench.common.screens.datasource.management.util.ServiceUtil.*;

/**
 * CDI based implementation of a DataSourceProviderFactory.
 */
@ApplicationScoped
public class DataSourceProviderFactoryImpl
        implements DataSourceProviderFactory
{

    private static final Logger logger = LoggerFactory.getLogger( DataSourceProviderFactoryImpl.class );

    private static final String DATASOURCE_SERVICE = DATASOURCE_MANAGEMENT_PREFIX +".DataSourceProvider";

    private static final String DRIVER_SERVICE = DATASOURCE_MANAGEMENT_PREFIX + ".DriverProvider";

    @Inject
    private BeanManager beanManager;

    private DataSourceProvider dataSourceProvider;

    private DriverProvider driverService;

    @PostConstruct
    public void init() {
        Properties properties = DataSourceSettings.getInstance().getProperties();

        String serviceName = getManagedProperty( properties, DATASOURCE_SERVICE );
        if ( !isEmpty( serviceName ) ) {
            try {
                dataSourceProvider = ( DataSourceProvider ) getManagedBean( beanManager, serviceName );
                if ( dataSourceProvider == null ) {
                    logger.error( "It was not possible to get the reference to the data sources service: "
                            + serviceName + ". Data source services won't be available." );
                } else {
                    dataSourceProvider.loadConfig( properties );
                }
            } catch ( Exception e ) {
                logger.error( "An error was produced during: " + serviceName + " initialization.", e);
            }
        } else {
            logger.warn( "Data source serviceName: " + DATASOURCE_SERVICE +
                    " property was not properly configured. Data source services won't be available." );
        }

        serviceName = getManagedProperty( properties, DRIVER_SERVICE );
        if ( !isEmpty( serviceName ) ) {
            try {
                driverService = ( DriverProvider ) getManagedBean( beanManager, serviceName );
                if ( driverService == null ) {
                    logger.error( "It was not possible to get reference to the drivers service: "
                            + serviceName + ". Drivers services won't be available." );
                } else {
                    driverService.loadConfig( properties );
                }
            } catch ( Exception e ) {
                logger.error( "An error was produced during: " + serviceName + " initialization.", e );
            }
        } else {
            logger.warn( "Drivers serviceName: " + DRIVER_SERVICE +
                    " property was not properly configured. Drivers services won't be available." );
        }

        if ( dataSourceProvider != null ) {
            logger.debug( "Data source service was properly initialized." );
        }
        if ( driverService != null ) {
            logger.debug( "Drivers service was properly initialized."  );
        }
    }

    @Override
    public DataSourceProvider getDataSourceProvider() {
        return dataSourceProvider;
    }

    @Override
    public DriverProvider getDriverProvider() {
        return driverService;
    }
}