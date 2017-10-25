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
package org.guvnor.ala.docker.config;

import org.guvnor.ala.config.BuildConfig;

/*
 * Represents the Docker Build specific settings to Build a Docker Image and push to a registry
 * @see BuildConfig
 */
public interface DockerBuildConfig extends BuildConfig {

    /*
     * Get the user name for the public registry. If not provided it will resolve the expression
     *  ${input.docker-user} from the Pipeline's Input map
     */
    default String getUsername() {
        return "${input.docker-user}";
    }

    /*
     * Get the password for the public registry. If not provided it will resolve the expression
     *  ${input.docker-password} from the Pipeline's Input map
     */
    default String getPassword() {
        return "${input.docker-password}";
    }

    /*
     * Defines if the built image needs to be pushed or not to a public registry. If not provided it will resolve the expression
     *  ${input.docker-push} from the Pipeline's Input map
     */
    default boolean push() {
        return Boolean.parseBoolean("${input.docker-push}");
    }
}
