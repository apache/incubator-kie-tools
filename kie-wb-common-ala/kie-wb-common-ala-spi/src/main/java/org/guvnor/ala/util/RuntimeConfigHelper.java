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

package org.guvnor.ala.util;

import org.guvnor.ala.config.RuntimeConfig;

/**
 * Utility class for holding helper operations related to runtimes.
 */
public class RuntimeConfigHelper {

    /**
     * Gets the runtime to be used given a runtime configuration.
     * @param config a runtime configuration.
     * @param defaultName the by default name to be used in cases where no name was provided in the runtime configuration.
     * @return the proper runtime name that must be used for the runtime.
     */
    public static final String buildRuntimeName(RuntimeConfig config,
                                                String defaultName) {
        if (config.getRuntimeName() == null || config.getRuntimeName().isEmpty()) {
            return defaultName;
        } else {
            return config.getRuntimeName();
        }
    }
}
