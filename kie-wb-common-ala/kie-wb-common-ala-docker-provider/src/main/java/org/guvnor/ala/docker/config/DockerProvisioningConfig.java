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

import org.guvnor.ala.config.ProvisioningConfig;

/*
 * Represents the Docker Provisioning specific settings to provision to Docker
 * @see ProvisioningConfig
 */
public interface DockerProvisioningConfig extends ProvisioningConfig {

    /*
     * Get the Docker Image Name to be provisioned
     * @return String with the Docker Image name. If not provided 
     *  it will resolve the expression: ${input.image-name} from the Pipeline's
     *  input map
    */
    default String getImageName() {
        return "${input.image-name}";
    }

    /*
     * Get the Docker Image Exposed port number
     * @return String with the Docker Image exposed port number. If not provided 
     *  it will resolve the expression: ${input.port-number} from the Pipeline's
     *  input map
    */
    default String getPortNumber() {
        return "${input.port-number}";
    }

    /*
     * Get the Docker Pull value, to decide if the Image needs to be pulled from a public 
     *  registry or not. 
     * @return String with the Docker Pull value. If not provided 
     *  it will resolve the expression: ${input.docker-pull} from the Pipeline's
     *  input map. Allowed values "true" or "false".
    */
    default String getDockerPullValue() {
        return "${input.docker-pull}";
    }
}
