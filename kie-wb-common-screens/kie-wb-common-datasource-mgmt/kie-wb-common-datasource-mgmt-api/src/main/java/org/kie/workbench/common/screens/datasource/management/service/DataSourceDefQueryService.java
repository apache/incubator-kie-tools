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

import java.util.Collection;

import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.uberfire.backend.vfs.Path;

/**
 * Service for getting information about the different data sources and drivers defined globally or for a given module.
 */
@Remote
public interface DataSourceDefQueryService {

    /**
     * Finds the DataSourceDefInfos for the platform global data sources.
     * @param includeUnManaged if true, server side un-managed data sources information will be included. Un-managed
     * data sources are those not created by the data sources management system.
     * @return a collection containing the data sources information.
     */
    Collection<DataSourceDefInfo> findGlobalDataSources(boolean includeUnManaged);

    /**
     * Finds the DataSourceDefInfos for the data sources defined for a given module.
     * @param path a path within the module.
     * @return a collection containing the data sources information
     */
    Collection<DataSourceDefInfo> findModuleDataSources(final Path path);

    /**
     * Finds the DataSourceDefInfos for the data sources defined for a given module.
     * @param module the module containing the data sources.
     * @return a collection containing the data sources information
     */
    Collection<DataSourceDefInfo> findModuleDataSources(final Module module);

    /**
     * Finds the DriverDefInfos for the platform global drivers.
     * @return a collection containing the drivers information.
     */
    Collection<DriverDefInfo> findGlobalDrivers();

    /**
     * Finds the DriverDefInfos for the drivers defined for a given module.
     * @param path a path within the module.
     * @return a collection containing the drivers information.
     */
    Collection<DriverDefInfo> findModuleDrivers(final Path path);

    /**
     * Finds the DriverDefInfos for the drivers defined for a given module.
     * @param module the module containing the drivers.
     * @return a collection containing the drivers information.
     */

    Collection<DriverDefInfo> findModuleDrivers(final Module module);

    /**
     * Finds the DriverDefInfo for a driver defined within a module.
     * @param uuid the UUID for the given Driver.
     * @param path a path within the given module.
     * @return the driver information for the given driver, or null if the driver was not found.
     */
    DriverDefInfo findModuleDriver(final String uuid,
                                   final Path path);

    /**
     * Finds the DriverDefInfo for platform global driver.
     * @param uuid
     * @return the driver information for the given driver, or null if the driver was not found.
     */
    DriverDefInfo findGlobalDriver(final String uuid);
}
