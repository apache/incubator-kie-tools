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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;

/**
 * Helper class for properly generate deployment identifiers for the data sources and drivers to be deployed on the
 * Wildfly server.
 */
public class DeploymentIdGenerator {

    private static final String SEPARATOR = "#";

    private static final Pattern KIE_GENERATED_ID = Pattern.compile( "kie#([^#]+)#(.*)" );

    private static final Pattern KIE_ID = Pattern.compile( "kie#([^#]+)#" );

    public static String extractUuid( final String deploymentId ) throws Exception {
        //deployments created by the kie-wb typically has the from "kie#uuid#XXXX", where could XXXX be a string added
        // by the wildfly server and that we can't manage. For example for the driver deployments.

        if ( deploymentId != null && isKieGenerated( deploymentId ) ) {
            Matcher matcher = KIE_ID.matcher( deploymentId );
            if ( matcher.find() ) {
                String[] parts = matcher.group().split( SEPARATOR );
                if ( parts.length > 1 ) {
                    return parts[1];
                }
            }
        }
        throw new Exception( "Unknown deployment identifier." + deploymentId );
    }

    public static String generateDeploymentId( final DriverDef driverDef ) {
        return generateDeploymentId( driverDef.getUuid() );
    }

    public static String generateDeploymentId( final DataSourceDef dataSourceDef ) {
        return generateDeploymentId( dataSourceDef.getUuid() );
    }

    public static String generateDeploymentId( final String uuid ) {
        return "kie" + SEPARATOR + uuid + SEPARATOR;
    }

    private static final boolean isKieGenerated( String value ) {
        return KIE_GENERATED_ID.matcher( value ).matches();
    }
}