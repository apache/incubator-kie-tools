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

package org.guvnor.ala.config;

import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.providers.ProviderId;

/**
 * Base Runtime configuration interface. Provide different implementations
 * for each runtime type that you want to configure.
 */
public interface RuntimeConfig extends Config {

    /**
     * Standard attribute name for referencing or defining a runtime. Pipeline inputs that wants to refer to an already
     * registered runtime, or wants to create a runtime during pipeline execution should use this parameter for
     * holding the runtime name.
     */
    String RUNTIME_NAME = "runtime-name";

    /*
     * Get the providerId for that runtime configuration
     * @return the providerId
     * @see ProviderId
     */
    ProviderId getProviderId();

    /**
     * Gets the human readable name for the runtime within the provider. The runtime name must be unique within the
     * provider. When no runtime name is provided, the by default name for the generated Runtime will be the
     * RuntimeId.getId() value. @see {@link RuntimeId}
     * @return the runtime name.
     */
    default String getRuntimeName() {
        return "${input." + RUNTIME_NAME + "}";
    }
}
