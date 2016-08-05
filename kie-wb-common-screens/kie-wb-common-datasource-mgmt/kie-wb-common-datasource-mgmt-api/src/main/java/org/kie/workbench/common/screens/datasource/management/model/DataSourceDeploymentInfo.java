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

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DataSourceDeploymentInfo
        extends DeploymentInfo {

    private String uuid;

    private String jndi;

    private boolean referenced;

    public DataSourceDeploymentInfo() {
    }

    public DataSourceDeploymentInfo( String deploymentId, boolean managed, String uuid, boolean referenced ) {
        super( deploymentId, managed );
        this.uuid = uuid;
        this.referenced = referenced;
    }

    public DataSourceDeploymentInfo( String deploymentId, boolean managed, String uuid, String jndi, boolean referenced ) {
        super( deploymentId, managed );
        this.uuid = uuid;
        this.jndi = jndi;
        this.referenced = referenced;
    }

    public String getUuid() {
        return uuid;
    }

    public String getJndi() {
        return jndi;
    }

    public boolean wasReferenced() {
        return referenced;
    }

    @Override
    public String toString() {
        return "DataSourceDeploymentInfo{" +
                "deploymentId='" + deploymentId + '\'' +
                ", managed=" + managed +
                ", uuid='" + uuid + '\'' +
                ", jndi='" + jndi + '\'' +
                "} ";
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

        DataSourceDeploymentInfo that = ( DataSourceDeploymentInfo ) o;

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