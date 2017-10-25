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

package org.guvnor.ala.ui.handler;

import org.guvnor.ala.ui.model.ProviderTypeKey;

/**
 * Base interface for defining a provider type handler.
 */
public interface ProviderHandler {

    /**
     * Gets the priority of current handler. The priority might be used to decide which handler to apply in cases
     * where there are many handlers registered for a given provider. The highest priority one will be applied.
     * @return an integer representing current handler priority. Priorities are correlative to the integers natural order.
     */
    int getPriority();

    /**
     * Indicates if current handler can manage a given provider type.
     * @param providerTypeKey a provider type key.
     * @return true if current handle can manage the given provider type, false in any other case.
     */
    boolean acceptProviderType(ProviderTypeKey providerTypeKey);
}
