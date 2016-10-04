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

package org.kie.workbench.common.screens.datasource.management.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DriverDeploymentInfo
        extends DeploymentInfo {

    private String uuid;

    private String driverClass;

    private String driverDeploymentId;

    private List<DataSourceDeploymentInfo> dependants = new ArrayList<>(  );

    public DriverDeploymentInfo() {
    }

    public DriverDeploymentInfo( String deploymentId,
            String driverDeploymentId, boolean managed, String uuid, String driverClass ) {
        super( deploymentId, managed );
        this.driverDeploymentId = driverDeploymentId;
        this.uuid = uuid;
        this.driverClass = driverClass;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getDriverDeploymentId() {
        return driverDeploymentId;
    }

    @Override
    public String toString() {
        return "DriverDeploymentInfo{" +
                "deploymentId='" + deploymentId + '\'' +
                ", driverDeploymentId='" + driverDeploymentId + '\'' +
                ", managed=" + managed +
                ", uuid='" + uuid + '\'' +
                ", driverClass='" + driverClass + '\'' +
                "} " + super.toString();
    }

    public boolean hasDependants() {
        return dependants != null && !dependants.isEmpty();
    }

    public List<DataSourceDeploymentInfo> getDependants() {
        return dependants;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        DriverDeploymentInfo that = ( DriverDeploymentInfo ) o;

        return !( uuid != null ? !uuid.equals( that.uuid ) : that.uuid != null );

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( uuid != null ? uuid.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
