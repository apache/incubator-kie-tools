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

package org.kie.workbench.common.screens.datasource.management.backend.core.wildfly;

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
import org.kie.workbench.common.screens.datasource.management.backend.integration.wildfly.WildflyDriverDef;
import org.kie.workbench.common.screens.datasource.management.backend.integration.wildfly.WildflyDriverManagementClient;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.util.MavenArtifactResolver;

/**
 * Widlfy based implementation of a DriverProvider.
 */
@ApplicationScoped
@Named(value = "WildflyDriverProvider" )
public class WildflyDriverProvider
        implements DriverProvider {

    @Inject
    private MavenArtifactResolver artifactResolver;

    private WildflyDriverManagementClient driverMgmtClient = new WildflyDriverManagementClient();

    private Map<String, DriverDeploymentInfo> managedDrivers = new HashMap<>( );

    /**
     * Deploys a driver definition on the Wildfly server.
     *
     * @param driverDef A driver definition to be deployed.
     *
     * @return The deployment information for the just deployed driver.
     *
     * @throws Exception exceptions may be thrown if was not possible to deployDataSource the driver.
     */
    public DriverDeploymentInfo deploy( final DriverDef driverDef ) throws Exception {

        final URI uri = artifactResolver.resolve( driverDef.getGroupId(),
                driverDef.getArtifactId(), driverDef.getVersion() );
        if ( uri == null ) {
            throw new Exception( "Unable to get driver library artifact for driver: " + driverDef );
        }

        String deploymentId = DeploymentIdGenerator.generateDeploymentId( driverDef );
        driverMgmtClient.deploy( deploymentId, uri );
        DriverDeploymentInfo deploymentInfo = new DriverDeploymentInfo( deploymentId,
                true, driverDef.getUuid(), driverDef.getDriverClass() );
        managedDrivers.put( deploymentInfo.getDeploymentId(), deploymentInfo );
        return deploymentInfo;
    }

    @Override
    public DriverDeploymentInfo resync( DriverDef driverDef, DriverDeploymentInfo deploymentInfo ) throws Exception {
        managedDrivers.put( deploymentInfo.getDeploymentId(), deploymentInfo );
        return deploymentInfo;
    }

    @Override
    public void undeploy( final DriverDeploymentInfo deploymentInfo ) throws Exception {
        driverMgmtClient.undeploy( deploymentInfo.getDeploymentId() );
        managedDrivers.remove( deploymentInfo.getDeploymentId() );
    }

    /**
     * Gets the deployment information about a driver definition.
     *
     * @param uuid the driver definition identifier.
     *
     * @return the deployment information for the driver definition of null if the driver wasn't deployed.
     *
     * @throws Exception exceptions may be thrown if e.g. communication with the Wildfly server fails, etc.
     */
    @Override
    public DriverDeploymentInfo getDeploymentInfo( final String uuid ) throws Exception {
        for ( DriverDeploymentInfo deploymentInfo : getDeploymentsInfo() ) {
            if ( uuid.equals( deploymentInfo.getUuid() ) ) {
                return deploymentInfo;
            }
        }
        return null;
    }

    /**
     * Gets the list of driver definitions for the currently deployed drivers.
     *
     * @return list with the definitions for the deployed drivers.
     *
     * @throws Exception exceptions may be thrown if e.g. communication with the Wildfly server fails, etc.
     */
    public List<DriverDef> getDeployments() throws Exception {

        List<DriverDef> driverDefs = new ArrayList<>(  );
        DriverDef driverDef;
        String uuid;

        for ( WildflyDriverDef internalDef : driverMgmtClient.getDeployedDrivers() ) {
            driverDef = new DriverDef();
            try {
                uuid = DeploymentIdGenerator.extractUuid( internalDef.getDriverName() );
            } catch ( Exception e ) {
                uuid = internalDef.getDriverName();
            }
            driverDef.setUuid( uuid );
            driverDef.setName( internalDef.getDeploymentName() );
            driverDef.setDriverClass( internalDef.getDriverClass() );
            driverDefs.add( driverDef );
        }

        return driverDefs;
    }

    /**
     * Gets the deployment information for all the drivers currently deployed on the Wildfly server.
     *
     * @return a list with the deployment information for all the drivers.
     *
     * @throws Exception exceptions may be thrown if e.g. communication with the Wildfly server fails, etc.
     */
    public List<DriverDeploymentInfo> getDeploymentsInfo() throws Exception {

        List<DriverDeploymentInfo> deploymentsInfo = new ArrayList<>(  );
        DriverDeploymentInfo deploymentInfo;
        String uuid;
        boolean managed;

        for ( WildflyDriverDef internalDef : driverMgmtClient.getDeployedDrivers() ) {
            try {
                uuid = DeploymentIdGenerator.extractUuid( internalDef.getDriverName() );
            } catch ( Exception e ) {
                uuid = internalDef.getDriverName();
            }
            managed = managedDrivers.containsKey( internalDef.getDriverName() );
            deploymentInfo = new DriverDeploymentInfo( internalDef.getDriverName(),
                    managed, uuid, internalDef.getDriverClass() );

            deploymentsInfo.add( deploymentInfo );
        }

        return deploymentsInfo;
    }

    @Override
    public void loadConfig( Properties properties ) {
        driverMgmtClient.loadConfig( properties );
    }
}