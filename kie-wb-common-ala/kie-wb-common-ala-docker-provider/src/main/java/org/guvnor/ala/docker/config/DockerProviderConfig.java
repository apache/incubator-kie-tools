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

import org.guvnor.ala.config.ProviderConfig;

/*
 * Represents the Docker Provider specific settings to connect to a Docker Daemon
 * @see ProviderConfig
 */
public interface DockerProviderConfig extends ProviderConfig {

    /* 
     * Get the name for the Docker Provider
     * @return String with the name of the provider. By default "local"
    */
    default String getName() {
        return "local";
    }

    /* 
     * Get the host for the Docker Provider
     * @return String with the host of the provider. By default "0.0.0.0"
    */
    default String getHostIp() {
        return "0.0.0.0";
    }
}
