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

package org.guvnor.ala.wildfly.config;

import org.guvnor.ala.config.ProviderConfig;

/**
 * This interface represents the basic information that we need for configuring a WildflyProvider
 * @see ProviderConfig
 */
public interface WildflyProviderConfig extends ProviderConfig {

    /**
     * Standard attribute name for setting the host configuration. Pipeline inputs that wants to set the host should use
     * this parameter name.
     */
    String HOST = "host";

    /**
     * Standard attribute name for setting the host port. Pipeline inputs that wants to set the host port should use
     * this parameter name.
     */
    String PORT = "port";

    /**
     * Standard attribute name for setting the management port. Pipeline inputs that wants to set the management port
     * should use this parameter name.
     */
    String MANAGEMENT_PORT = "management-port";

    /**
     * Standard attribute name for setting the wildfly user name. Pipeline inputs that wants to set the wildfly user name
     * should use this parameter name.
     */
    String WILDFLY_USER = "wildfly-user";

    /**
     * Standard attribute name for setting the wildfly user password. Pipeline inputs that wants to set the wildfly user
     * password should use this parameter name.
     */
    String WILDFLY_PASSWORD = "wildfly-password";

    /**
     * Get the Provider name
     * @return String with the provider name. This configuration will use by default the Input value stored under the
     * parameter name PROVIDER_NAME. If no provider is register with this provider name a new provider will be registered
     * by using the remaining parameters.
     */
    default String getName() {
        return "${input." + PROVIDER_NAME + "}";
    }

    /**
     * Get the Provider Host IP address
     * @return String host IP for the provider. If not provided it will
     * resolve the expression: ${input.host} from the Pipeline's Input map
     */
    default String getHost() {
        return "${input." + HOST + "}";
    }

    /**
     * Get the Provider Host Port
     * @return String host port for the provider. If not provided it will
     * resolve the expression: ${input.port} from the Pipeline's Input map
     */
    default String getPort() {
        return "${input." + PORT + "}";
    }

    /**
     * Get the Provider Management Port
     * @return String management port for the provider. If not provided it will
     * resolve the expression: ${input.management-port} from the Pipeline's Input map
     */
    default String getManagementPort() {
        return "${input." + MANAGEMENT_PORT + "}";
    }

    /**
     * Get the Provider user name
     * @return String username used to interact with the provider. If not provided it will
     * resolve the expression: ${input.wildfly-user} from the Pipeline's Input map
     */
    default String getUser() {
        return "${input." + WILDFLY_USER + "}";
    }

    /**
     * Get the Provider password
     * @return String password used to interact with the provider. If not provided it will
     * resolve the expression: ${input.wildfly-password} from the Pipeline's Input map
     */
    default String getPassword() {
        return "${input." + WILDFLY_PASSWORD + "}";
    }
}
