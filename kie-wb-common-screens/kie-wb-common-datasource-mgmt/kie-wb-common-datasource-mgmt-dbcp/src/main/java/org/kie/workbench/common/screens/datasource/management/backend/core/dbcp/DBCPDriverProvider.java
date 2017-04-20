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

package org.kie.workbench.common.screens.datasource.management.backend.core.dbcp;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.screens.datasource.management.backend.core.DriverProvider;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.util.MavenArtifactResolver;
import org.uberfire.io.IOService;

/**
 * Multi-platform implementation of a DriverProvider that will be used in conjunction with the
 * DBCPDataSourceProvider.
 */
@ApplicationScoped
@Named(value = "DBCPDriverProvider")
public class DBCPDriverProvider
        implements DriverProvider {

    private MavenArtifactResolver artifactResolver;

    private Map< String, URI > deployedUris = new HashMap<>();

    private Map< String, DriverDeploymentInfo > deploymentInfos = new HashMap<>();

    private Map< String, DriverDef > deployedDrivers = new HashMap<>();

    public DBCPDriverProvider() {
    }

    @Inject
    public DBCPDriverProvider(MavenArtifactResolver artifactResolver) {
        this.artifactResolver = artifactResolver;
    }

    @Override
    public DriverDeploymentInfo deploy(DriverDef driverDef) throws Exception {
        final URI uri = artifactResolver.resolve(driverDef.getGroupId(),
                                                 driverDef.getArtifactId(),
                                                 driverDef.getVersion());

        if (uri == null) {
            throw new Exception("Unable to get driver library artifact for driver: " + driverDef);
        }
        final DriverDeploymentInfo deploymentInfo = new DriverDeploymentInfo(driverDef.getUuid(),
                                                                             driverDef.getUuid(),
                                                                             true,
                                                                             driverDef.getUuid(),
                                                                             driverDef.getDriverClass());
        deployedUris.put(driverDef.getUuid(),
                         uri);
        deploymentInfos.put(driverDef.getUuid(),
                            deploymentInfo);
        deployedDrivers.put(driverDef.getUuid(),
                            driverDef);
        return deploymentInfo;
    }

    @Override
    public DriverDeploymentInfo resync(DriverDef driverDef,
                                       DriverDeploymentInfo deploymentInfo) throws Exception {
        //no more processing required for this driver.
        return deploymentInfo;
    }

    @Override
    public void undeploy(DriverDeploymentInfo deploymentInfo) throws Exception {
        deployedUris.remove(deploymentInfo.getDeploymentId());
        deploymentInfos.remove(deploymentInfo.getDeploymentId());
        deployedDrivers.remove(deploymentInfo.getDeploymentId());
    }

    @Override
    public DriverDeploymentInfo getDeploymentInfo(String uuid) throws Exception {
        return deploymentInfos.get(uuid);
    }

    @Override
    public List< DriverDeploymentInfo > getDeploymentsInfo() throws Exception {
        List< DriverDeploymentInfo > result = new ArrayList<>();
        result.addAll(deploymentInfos.values());
        return result;
    }

    public List< DriverDef > getDeployments() {
        List< DriverDef > results = new ArrayList<>();
        results.addAll(deployedDrivers.values());
        return results;
    }

    @Override
    public void loadConfig(Properties properties) {
        //no additional configurations for this provider.
    }

    @Override
    public void hasStarted() throws Exception {
        //no additional checks are required for this provider.
    }
}
