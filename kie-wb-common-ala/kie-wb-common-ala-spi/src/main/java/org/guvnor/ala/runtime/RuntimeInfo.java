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
 * This interface represent the Runtime instance information.
 * This includes the configuration which was used to create the Runtime
 */
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public interface RuntimeInfo {

    /*
     * Get the RuntimeConfig used to build this runtime
     * @return RuntimeConfig with the configuration used to create this Runtime
     * @see RuntimeConfig
    */
    RuntimeConfig getConfig();
}
