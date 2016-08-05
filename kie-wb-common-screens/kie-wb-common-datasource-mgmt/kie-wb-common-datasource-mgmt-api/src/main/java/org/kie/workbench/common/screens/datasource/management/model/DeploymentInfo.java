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

public abstract class DeploymentInfo {

    protected String deploymentId;

    protected boolean managed;

    public DeploymentInfo() {
    }

    public DeploymentInfo( String deploymentId, boolean managed ) {
        this.deploymentId = deploymentId;
        this.managed = managed;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public boolean isManaged() {
        return managed;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        DeploymentInfo that = ( DeploymentInfo ) o;

        return !( deploymentId != null ? !deploymentId.equals( that.deploymentId ) : that.deploymentId != null );

    }

    @Override
    public int hashCode() {
        int result = deploymentId != null ? deploymentId.hashCode() : 0;
        return ~~result;
    }
}
