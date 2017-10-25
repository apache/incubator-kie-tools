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

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

/**
 * Represent the State of a particular runtime
 */
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public interface RuntimeState {

    /**
     * Common state value for indicating that a it's not possible to establish current runtime state.
     */
    String UNKNOWN = "UNKNOWN";

    /**
     * Common state value for indicating that a runtime is ready but not started.
     */
    String READY = "READY";

    /**
     * Common state value for indicating that a runtime is running.
     */
    String RUNNING = "RUNNING";

    /**
     * Common state value for indicating that a runtime is stopped.
     */
    String STOPPED = "STOPPED";

    /*
     * Get the Runtime State
     * @return String with the State
    */
    String getState();

    /*
     * Get the Runtime started at time as String
     * @return String with the started At timestamp
    */
    String getStartedAt();
}
