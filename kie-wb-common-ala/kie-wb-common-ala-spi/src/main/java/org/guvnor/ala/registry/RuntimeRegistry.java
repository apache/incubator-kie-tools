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

package org.guvnor.ala.registry;

import java.util.List;
import java.util.Optional;

import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.runtime.providers.ProviderType;

/**
 * Represents the RuntimeRegistry where all the ProviderTypes, Providers and Runtimes are registered.
 */
public interface RuntimeRegistry {

    String PROVIDER_TYPE_NAME_SORT = "providerTypeName";

    String PROVIDER_TYPE_VERSION_SORT = "version";

    String PROVIDER_ID_SORT = "id";

    String RUNTIME_ID_SORT = "id";

    String RUNTIME_STATE_SORT = "state";

    /**
     * Registers a provider type.
     * @param providerType a provider type to register.
     * @see ProviderType
     */
    void registerProviderType(final ProviderType providerType);

    /**
     * Gets the list of registered provider types.
     * @return a list with all the registered provider types.
     * @see ProviderType
     */
    List<ProviderType> getProviderTypes(final Integer page,
                                        final Integer pageSize,
                                        final String sort,
                                        final boolean sortOrder);

    /**
     * Deregisters a provider type.
     * @param providerType a provider to deregister.
     * @see ProviderType
     */
    void deregisterProviderType(final ProviderType providerType);

    /**
     * Registers a Provider
     * @param provider a provider to be registered.
     * @see Provider
     */
    void registerProvider(final Provider provider);

    /**
     * Gets a provider by identifier.
     * @param providerId the identifier of the provider to look for.
     * @return the provider with the given identifier, if registered, null y any other case.
     * @see Provider
     */
    Provider getProvider(final String providerId);

    /**
     * Gets the list of registered providers.
     * @return a list with all the registered providers.
     * @see Provider
     */
    List<Provider> getProviders(final Integer page,
                                final Integer pageSize,
                                final String sort,
                                final boolean sortOrder);

    /**
     * Gets the list of registered providers associated to a given provider type.
     * @param providerType a given provider type.
     * @return a list with all the providers associated to the provider type.
     * @see Provider
     * @see ProviderType
     */
    List<Provider> getProvidersByType(final ProviderType providerType);

    /**
     * Deregisters a provider.
     * @param provider a provider to deregister.
     * @see Provider
     */
    void deregisterProvider(final Provider provider);

    /**
     * Deregisters a provider by provider identifier.
     * @param providerId the identifier of the provider to deregister.
     * @see Provider
     */
    void deregisterProvider(final String providerId);

    /**
     * Registers a runtime.
     * @param runtime a runtime to register.
     * @see Runtime
     */
    void registerRuntime(final Runtime runtime);

    /**
     * Gets the list of registered runtimes.
     * @return a list with all the registered runtimes.
     * @see Runtime
     */
    List<Runtime> getRuntimes(final Integer page,
                              final Integer pageSize,
                              final String sort,
                              final boolean sortOrder);

    /**
     * Gets a runtime by identifier.
     * @param runtimeId the identifier of the runtime to look for.
     * @return the runtime with the given identifier, if registered, null y any other case.
     * @see Runtime
     */
    Runtime getRuntimeById(final String runtimeId);

    /**
     * Deregisters a runtime.
     * @param runtimeId the runtime id to deregister.
     * @see RuntimeId
     */
    void deregisterRuntime(final RuntimeId runtimeId);

    /**
     * Gets a provider based on ProviderId and Class type.
     * @param providerId the provider id to look for.
     * @param clazz the provider class.
     * @return the provider with the given provider id and of type clazz, if registered, null in any other case.
     * @see ProviderId
     */
    <T extends Provider> Optional<T> getProvider(final ProviderId providerId,
                                                 final Class<T> clazz);
}
