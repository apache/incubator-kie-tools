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
 * This class models the available un-deployment options that can be used for un-deploying data sources and drivers.
 */
public class UnDeploymentOptions {

    enum UnDeploymentMode { SOFT_UN_DEPLOYMENT, FORCED_UN_DEPLOYMENT }

    private static final UnDeploymentOptions SOFT = new UnDeploymentOptions( UnDeploymentMode.SOFT_UN_DEPLOYMENT );

    private static final UnDeploymentOptions FORCED = new UnDeploymentOptions( UnDeploymentMode.FORCED_UN_DEPLOYMENT );

    private UnDeploymentMode unDeploymentMode;

    public UnDeploymentOptions( UnDeploymentMode unDeploymentMode ) {
        this.unDeploymentMode = unDeploymentMode;
    }

    public boolean isSoftUnDeployment() {
        return UnDeploymentMode.SOFT_UN_DEPLOYMENT.equals( unDeploymentMode );
    }

    public boolean isForcedUnDeployment() {
        return  UnDeploymentMode.FORCED_UN_DEPLOYMENT.equals( unDeploymentMode );
    }

    public static UnDeploymentOptions softUnDeployment() {
        return SOFT;
    }

    public static UnDeploymentOptions forcedUnDeployment() {
        return FORCED;
    }
}
