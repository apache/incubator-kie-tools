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

package org.kie.workbench.common.screens.datasource.management.backend.core;

/**
 * This class models the available deployment options that can be used for deploying data sources and drivers.
 */
public class DeploymentOptions {

    enum DeploymentMode { CREATE_DEPLOYMENT, CREATE_OR_RESYNC_DEPOLYMENT}

    private DeploymentMode deploymentMode;

    private static final DeploymentOptions CREATE = new DeploymentOptions( DeploymentMode.CREATE_DEPLOYMENT );

    private static final DeploymentOptions CREATE_OR_RESYNC = new DeploymentOptions( DeploymentMode.CREATE_OR_RESYNC_DEPOLYMENT );

    public DeploymentOptions( DeploymentMode deploymentMode ) {
        this.deploymentMode = deploymentMode;
    }

    public DeploymentMode getDeploymentMode() {
        return deploymentMode;
    }

    public void setDeploymentMode( DeploymentMode deploymentMode ) {
        this.deploymentMode = deploymentMode;
    }

    public boolean isCreateDeployment() {
        return DeploymentMode.CREATE_DEPLOYMENT.equals( deploymentMode );
    }

    public boolean isCreateOrResyncDeployment() {
        return DeploymentMode.CREATE_OR_RESYNC_DEPOLYMENT.equals( deploymentMode );
    }

    public static DeploymentOptions create() {
        return CREATE;
    }

    public static DeploymentOptions createOrResync() {
        return CREATE_OR_RESYNC;
    }
}
