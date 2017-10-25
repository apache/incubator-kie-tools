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

import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderType;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Service for managing the providers related to the different provider types.
 */
@Remote
public interface ProviderService {

    /**
     * Gets the provides related to a given provider type.
     * @param providerType a provider type.
     * @return a list of providers.
     */
    Collection<Provider> getProviders(final ProviderType providerType);

    /**
     * Gets the keys of the providers related to a given provider type.
     * @param providerType a provider type.
     * @return a list of provider keys.
     */
    Collection<ProviderKey> getProvidersKey(final ProviderType providerType);

    /**
     * Creates a provider by associating it to a provider type.
     * @param providerType the provider for creating the provider.
     * @param configuration a provider configuration with all the required parameters for creating the provider.
     */
    void createProvider(final ProviderType providerType,
                        final ProviderConfiguration configuration);

    /**
     * Deletes a provider.
     * @param providerKey the key of the provider to delete.
     */
    void deleteProvider(final ProviderKey providerKey);

    /**
     * Gets a provider.
     * @param providerKey the key of the provider to return.
     * @return the provider identified by the key if exists, null in any other case.
     */
    Provider getProvider(final ProviderKey providerKey);
}