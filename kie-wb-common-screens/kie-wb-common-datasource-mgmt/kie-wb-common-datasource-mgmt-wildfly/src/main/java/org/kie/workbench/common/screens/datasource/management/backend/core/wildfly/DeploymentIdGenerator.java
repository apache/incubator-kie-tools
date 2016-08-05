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

import java.util.UUID;

import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;

/**
 * Helper class for properly generate deployment identifiers for the data sources and drivers to be deployed on the
 * Wildfly server.
 */
public class DeploymentIdGenerator {

    public static String extractUuid( final String deploymentId ) throws Exception {
        //deployments created by the kie-wb typically starts with "kie-" plus a 36 characters long java UUID in the form
        // kie-8d568f4b-723a-4708-b0d6-8d76a6b500f4

        if ( deploymentId == null || deploymentId.length() < 40 || ! deploymentId.startsWith( "kie-" ) ) {
            throw new Exception( "Unknown deployment identifier." + deploymentId );
        } else {
            String uuid = deploymentId.substring( 4, 40 );
            try {
                UUID.fromString( uuid );
                return uuid;
            } catch ( Exception e ) {
                //the prefix is not a uuid
                throw new Exception( "Unknown deployment identifier." + deploymentId );
            }
        }
    }

    public static String generateDeploymentId( final DriverDef driverDef ) {
        return generateDeploymentId( driverDef.getUuid() );
    }

    public static String generateDeploymentId( final DataSourceDef dataSourceDef ) {
        return generateDeploymentId( dataSourceDef.getUuid() );
    }

    public static String generateDeploymentId( final String uuid ) {
        return "kie-"+ uuid;
    }
}