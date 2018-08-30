/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.compiler;

import java.util.Map;
import java.util.Properties;

import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.external339.AFCliRequest;

/**
 * Wrap a compilation request
 */
public interface CompilationRequest {

    /**
     * It contains the CLI request for Maven
     * @return Request for the CLI
     */
    AFCliRequest getKieCliRequest();

    /**
     * It contains informations like the Project path  and if the kiePluginIsPresent
     * @return the information container
     */
    WorkspaceCompilationInfo getInfo();

    /**
     * Maven repo used for this build
     * @return the absolute path of the maven repo used in the current build
     */
    String getMavenRepo();

    /**
     * The origianl arguments for this build without ours additional Maven args
     * @return the arguments received for this build invocation
     */
    String[] getOriginalArgs();

    /**
     * The map used to share information with the Maven infrastructure
     * @return the map with the arguments needed for our infrastructure
     */
    Map<String, Object> getMap();

    /**
     * This contains the identifier used in all objects related to this build
     * @return the unique identifier for this build
     */
    String getRequestUUID();

    /**
     * True if no git operation is required,
     * false if update is required before build
     * @return
     */
    Boolean skipAutoSourceUpdate();

    /**
     * True if the list of the project's dependencies isn't required,
     * false if the list of project's dependencies is required.
     * @return
     */
    Boolean skipProjectDependenciesCreationList();

    /**
     * True if we want to restore the overrided files in a build
     * with temporary files for test,
     * false if we don't want restore the files, is effective in the compile with override map
     * @return
     */
    Boolean getRestoreOverride();

    /**
     * Properties with the banned env vars who could prevent the correct build
     * @return
     */
    Properties getBannedEnvVars();
}
