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

package org.guvnor.ala.ui.service;

import java.util.Collection;
import java.util.Map;

import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Service for managing the provider types.
 */
@Remote
public interface ProviderTypeService {

    /**
     * Gets the list of available provider types in the system.
     * @return a list of ProviderType.
     */
    Collection<ProviderType> getAvailableProviderTypes();

    /**
     * Gets a provider type given a provider type key.
     * @param providerTypeKey a provider type key.
     * @return the provider type or null when there aren't a provider with given key.
     */
    ProviderType getProviderType(final ProviderTypeKey providerTypeKey);

    /**
     * Gets the list of provider types currently enabled in the system.
     * @return a list of ProviderType.
     */
    Collection<ProviderType> getEnabledProviderTypes();

    /**
     * Enables a collection of provider types.
     * @param providerTypes a collection of provider types to enable.
     */
    void enableProviderTypes(final Collection<ProviderType> providerTypes);

    /**
     * Disables a provider type.
     * @param providerType a provider type to disable.
     */
    void disableProviderType(final ProviderType providerType);

    /**
     * Gets the status of the available provider types.
     * @return a map with the status for each for the available provider types.
     */
    Map<ProviderType, ProviderTypeStatus> getProviderTypesStatus();
}