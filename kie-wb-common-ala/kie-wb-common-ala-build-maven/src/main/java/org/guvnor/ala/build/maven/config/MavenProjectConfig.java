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
package org.guvnor.ala.build.maven.config;

import org.guvnor.ala.config.ProjectConfig;

/**
 * Maven specific Project configuration. This interface represents the basic information needed
 * to configure a Maven Project.
 * @see ProjectConfig
 */
public interface MavenProjectConfig extends ProjectConfig {

    /**
     * Standard attribute name for setting the project base dir. Pipeline inputs that want to set the project base dir
     * should use this parameter name.
     */
    String PROJECT_DIR = "project-dir";

    /**
     * Standard attribute name for setting the project temp dir. Pipeline inputs that want to set the project temp dir
     * should use this parameter name.
     */
    String PROJECT_TEMP_DIR = "project-temp-dir";

    /**
     * Standard attribute name for setting the preserve temp dir option. Pipeline inputs that want to set the preserve
     * temp dir option should use this parameter name.
     */
    String PRESERVE_TEMP_DIR = "preserve-temp-dir";

    /**
     * Get the Project Base Dir
     * @return String with the project dir path if provided, if not it will default to resolve the expression
     * ${input.project-dir} from the Pipeline input map.
     */
    default String getProjectDir() {
        return "${input." + PROJECT_DIR + "}";
    }

    default String getProjectTempDir() {
        return "${input." + PROJECT_TEMP_DIR + "}";
    }

    default boolean recreateTempDir() {
        return Boolean.parseBoolean("${input." + PRESERVE_TEMP_DIR + "}");
    }
}
