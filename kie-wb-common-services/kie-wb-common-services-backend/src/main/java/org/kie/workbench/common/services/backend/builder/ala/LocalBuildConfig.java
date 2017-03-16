/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.ala;

import java.util.Map;

import org.guvnor.ala.config.BuildConfig;

/**
 * This interface represents the build information/parameters for building a project with the local build system.
 */
public interface LocalBuildConfig extends BuildConfig {

    String BUILD_TYPE = "build-type";

    String RESOURCE = "resource";

    String RESOURCE_CHANGE = "resource-change:";

    String DEPLOYMENT_TYPE = "deployment-type";

    String SUPPRESS_HANDLERS = "suppress-handlers";

    /**
     * Enumerates the different build types supported by the local build system.
     */
    enum BuildType {
        FULL_BUILD,
        INCREMENTAL_ADD_RESOURCE,
        INCREMENTAL_DELETE_RESOURCE,
        INCREMENTAL_UPDATE_RESOURCE,
        INCREMENTAL_BATCH_CHANGES,
        FULL_BUILD_AND_DEPLOY
    }

    /**
     * Enumerates the different deployment modes supported by the local build system.
     */
    enum DeploymentType {
        VALIDATED, FORCED
    }

    /**
     * @return the String representation of the BuildType to perform.
     */
    default String getBuildType( ) {
        return "${input." + BUILD_TYPE + "}";
    }

    /**
     * @return the String representation of the resource uri to build.
     */
    default String getResource( ) {
        return "${input." + RESOURCE + "}";
    }

    /**
     * @return the String representation of the DeploymentType to perform.
     */
    default String getDeploymentType( ) {
        return "${input." + DEPLOYMENT_TYPE + "}";
    }

    /**
     * @return the String representation of a boolean indicating if the PostBuildHandlers invocation should be suppressed.
     */
    default String getSuppressHandlers( ) {
        return "${input." + SUPPRESS_HANDLERS + "}";
    }

    /**
     * @return a Map with the resource changes in the case of an INCREMENTAL_BATCH_CHANGES build.
     */
    Map< String, String > getResourceChanges( );
}