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

package org.guvnor.ala.ui.backend.service.handler;

import org.guvnor.ala.ui.backend.service.converter.ProviderConfigConverter;
import org.guvnor.ala.ui.handler.ProviderHandler;

/**
 * Base interface for defining a provider type handler for using at backend.
 */
public interface BackendProviderHandler
        extends ProviderHandler {

    /**
     * Gets the provider configuration converter for the given provider type.
     * @return the configured converter for the given provider type, null if no such converter is configured.
     */
    ProviderConfigConverter getProviderConfigConverter();
}
