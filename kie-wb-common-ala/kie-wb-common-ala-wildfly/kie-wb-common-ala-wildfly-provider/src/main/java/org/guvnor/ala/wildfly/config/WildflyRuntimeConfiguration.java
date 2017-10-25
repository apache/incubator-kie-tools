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

import org.guvnor.ala.config.ProvisioningConfig;
import org.guvnor.ala.config.RuntimeConfig;

/*
 * This interface represents the basic information that we need for configuring a
 * WildflyRuntime. This is an application running inside a Wildfly instance.
 * @see ProvisioningConfig
 * @see RuntimeConfig
 */
public interface WildflyRuntimeConfiguration
        extends ProvisioningConfig,
                RuntimeConfig {

    /**
     * Attribute name for setting the war file path parameter. Pipeline inputs that wants to set this value should
     * ideally use this constant.
     */
    String WAR_PATH = "war-path";

    /**
     * Attribute name for setting the redeploy strategy parameter. Pipeline inputs that wants to set this value should
     * ideally use this constant.
     */
    String REDEPLOY_STRATEGY = "redeploy";

    /**
     * Get the War / App path
     * @return String with the path where the WAR (Web Archive) is located.
     */
    default String getWarPath() {
        return "${input." + WAR_PATH + "}";
    }

    /**
     * Get the Redeploy Strategy for apps in wildfly
     * - auto: will automatically redeploy the app if it already exist
     * - none: will fail if you try to redeploy an app that already exist
     * @return String with the strategy
     */
    default String getRedeployStrategy() {
        return "${input." + REDEPLOY_STRATEGY + "}";
    }
}