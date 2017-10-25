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

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base Provider configuration interface. Provide different implementations for different
 * Provider Configuration types.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface ProviderConfig extends Config {

    /**
     * Standard attribute name for referencing or defining a provider. Pipeline inputs that wants to refer to an already
     * registered provider, or wants to create a provider during pipeline execution should use this parameter for
     * holding the provider name.
     */
    String PROVIDER_NAME = "provider-name";
}
