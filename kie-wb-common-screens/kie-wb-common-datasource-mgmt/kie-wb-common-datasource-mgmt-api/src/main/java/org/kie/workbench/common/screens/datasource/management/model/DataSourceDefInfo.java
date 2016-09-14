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
import org.uberfire.backend.vfs.Path;

@Portable
public class DataSourceDefInfo extends DefInfo {

    private DataSourceDeploymentInfo deploymentInfo;

    public DataSourceDefInfo() {
    }

    public DataSourceDefInfo( String uuid , String name, Path path, DataSourceDeploymentInfo deploymentInfo  ) {
        super( uuid, name, path );
        this.deploymentInfo = deploymentInfo;
    }

    public DataSourceDefInfo( String uuid , String name, DataSourceDeploymentInfo deploymentInfo ) {
        super( uuid, name );
        this.deploymentInfo = deploymentInfo;
    }

    public boolean isDeployed() {
        return deploymentInfo != null;
    }

    public DataSourceDeploymentInfo getDeploymentInfo() {
        return deploymentInfo;
    }

    @Override
    public String toString() {
        return "DataSourceDefInfo{" +
                "deploymentInfo=" + deploymentInfo +
                "} " + super.toString();
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

        DataSourceDefInfo that = ( DataSourceDefInfo ) o;

        return deploymentInfo != null ? deploymentInfo.equals( that.deploymentInfo ) : that.deploymentInfo == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( deploymentInfo != null ? deploymentInfo.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
