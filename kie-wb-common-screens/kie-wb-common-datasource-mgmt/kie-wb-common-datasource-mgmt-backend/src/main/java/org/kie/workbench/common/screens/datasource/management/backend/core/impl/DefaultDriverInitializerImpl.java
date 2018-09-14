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

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceSettings;
import org.kie.workbench.common.screens.datasource.management.backend.core.DefaultDriverInitializer;
import org.kie.workbench.common.screens.datasource.management.backend.service.DataSourceServicesHelper;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.util.DriverDefSerializer;
import org.kie.workbench.common.screens.datasource.management.util.DriverDefValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;

import static org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceSettings.DATASOURCE_MANAGEMENT_PREFIX;
import static org.kie.workbench.common.screens.datasource.management.util.ServiceUtil.getManagedProperty;

@ApplicationScoped
public class DefaultDriverInitializerImpl
        implements DefaultDriverInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDriverInitializerImpl.class);

    private static final String PREFIX = "driverDef";

    private static final String UUID = PREFIX + ".uuid";

    private static final String NAME = PREFIX + ".name";

    private static final String DRIVER_CLASS = PREFIX + ".driverClass";

    private static final String GROUP_ID = PREFIX + ".groupId";

    private static final String ARTIFACT_ID = PREFIX + ".artifactId";

    private static final String VERSION = PREFIX + ".version";

    public static final String DISABLE_DEFAULT_DRIVERS = DATASOURCE_MANAGEMENT_PREFIX + ".disableDefaultDrivers";

    private IOService ioService;

    private DataSourceServicesHelper serviceHelper;

    private CommentedOptionFactory optionsFactory;

    private DriverDefValidator driverDefValidator = new DriverDefValidator();

    public DefaultDriverInitializerImpl() {
    }

    @Inject
    public DefaultDriverInitializerImpl(@Named("ioStrategy") IOService ioService,
                                        DataSourceServicesHelper serviceHelper,
                                        CommentedOptionFactory optionsFactory) {
        this.ioService = ioService;
        this.serviceHelper = serviceHelper;
        this.optionsFactory = optionsFactory;
    }

    @Override
    public void initializeDefaultDrivers() {
        boolean disableDefaultDrivers = areDriversDisabledByDefault();
        if (disableDefaultDrivers) {
            logger.debug("Default drivers initialization was disabled by using the " + DISABLE_DEFAULT_DRIVERS + " configuration property.");
        } else {
            initializeFromSystemProperties();
            initializeFromConfigFile();
        }
    }

    boolean areDriversDisabledByDefault() {
        return Boolean.valueOf(getManagedProperty(DataSourceSettings.getInstance().getProperties(),
                                                  DISABLE_DEFAULT_DRIVERS,
                                                  "true"));
    }

    protected void initializeFromSystemProperties() {
        initializeFromProperties(System.getProperties());
    }

    protected void initializeFromConfigFile() {
        initializeFromProperties(DataSourceSettings.getInstance().getProperties());
    }

    private void initializeFromProperties(Properties properties) {
        final Set<String> driverCodes = new HashSet<>();
        DriverDef driverDef;
        final org.uberfire.java.nio.file.Path globalPath = Paths.convert(serviceHelper.getGlobalDataSourcesContext());
        org.uberfire.java.nio.file.Path targetPath;
        String source;

        for (String propertyName : properties.stringPropertyNames()) {
            if (propertyName.length() > UUID.length() && propertyName.startsWith(UUID + ".")) {
                driverCodes.add(propertyName.substring(UUID.length() + 1,
                                                       propertyName.length()));
            }
        }

        for (String driverCode : driverCodes) {
            driverDef = new DriverDef();
            driverDef.setUuid(getDriverParam(properties,
                                             UUID,
                                             driverCode));
            driverDef.setName(getDriverParam(properties,
                                             NAME,
                                             driverCode));
            driverDef.setDriverClass(getDriverParam(properties,
                                                    DRIVER_CLASS,
                                                    driverCode));
            driverDef.setGroupId(getDriverParam(properties,
                                                GROUP_ID,
                                                driverCode));
            driverDef.setArtifactId(getDriverParam(properties,
                                                   ARTIFACT_ID,
                                                   driverCode));
            driverDef.setVersion(getDriverParam(properties,
                                                VERSION,
                                                driverCode));
            if (driverDefValidator.validate(driverDef)) {
                targetPath = globalPath.resolve(driverDef.getName() + ".driver");
                try {
                    if (!ioService.exists(targetPath)) {
                        source = DriverDefSerializer.serialize(driverDef);
                        serviceHelper.getDefRegistry().setEntry(Paths.convert(targetPath),
                                                                driverDef);
                        ioService.write(targetPath,
                                        source,
                                        optionsFactory.makeCommentedOption("system generated driver"));
                    }
                } catch (Exception e) {
                    serviceHelper.getDefRegistry().invalidateCache(Paths.convert(targetPath));
                    logger.error("It was not possible to write driver definition {} in path {}. ",
                                 driverDef,
                                 targetPath);
                }
            } else {
                logger.warn("Driver will be skipped due to invalid or uncompleted properties {}.",
                            driverCode);
            }
        }
    }

    private String getDriverParam(Properties properties,
                                  String propertyName,
                                  String driverCode) {
        return properties.getProperty(propertyName + "." + driverCode);
    }
}