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

package org.guvnor.ala.build.maven.model.gwt;

import java.util.List;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.config.BuildConfig;

/**
 * Interface that represent the Maven Build information
 * to be used build a Maven Project
 * @see BuildConfig
 */
public interface GWTCodeServerMavenExec extends BuildConfig {

    /*
     * Get the associated Project
     * @return Project that will be built
     * @see Project
     */
    Project getProject();

    /*
     * Get the list of goals that will be executed to built the Project
     * @return List<String> with the goals to be executed
     */
    List<String> getGoals();
}
