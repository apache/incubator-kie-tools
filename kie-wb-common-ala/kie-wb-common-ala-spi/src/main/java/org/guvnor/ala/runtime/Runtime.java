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

package org.guvnor.ala.runtime;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.guvnor.ala.config.RuntimeConfig;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

/**
 * Implementations of this interface represent a Runtime (Docker Image running
 * or a WAR deployed into a
 * server)
 */
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public interface Runtime extends RuntimeId {

    /*
     * Get the runtime endpoint
     * @return RuntimeEndpoint
     * @see RuntimeEndpoint
     */
    RuntimeEndpoint getEndpoint();

    /*
     * Get the runtime config
     * @return RuntimeConfig for this Runtime
     * @see RuntimeConfig
     */
    RuntimeConfig getConfig();

    /*
     * Get the runtime state
     * @return RuntimeState for this Runtime
     * @see RuntimeState
     */
    RuntimeState getState();

    /*
     * Get the runtime info
     * @return RuntimeInfo for this Runtime
     * @see RuntimeInfo
     */
    RuntimeInfo getInfo();
}
