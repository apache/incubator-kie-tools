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

package org.guvnor.ala.registry.inmemory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.registry.inmemory.util.PageSortUtil;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.runtime.providers.ProviderType;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class InMemoryRuntimeRegistry
        implements RuntimeRegistry {

    protected Map<ProviderType, ProviderType> providerTypes = new ConcurrentHashMap<>();

    protected Map<String, Provider> providers = new ConcurrentHashMap<>();

    protected Map<String, Runtime> runtimes = new ConcurrentHashMap<>();

    public InMemoryRuntimeRegistry() {
        //Empty constructor for Weld proxying
    }

    @Override
    public void registerProviderType(final ProviderType providerType) {
        checkNotNull("providerType",
                     providerType);
        providerTypes.put(providerType,
                          providerType);
    }

    @Override
    public List<ProviderType> getProviderTypes(final Integer page,
                                               final Integer pageSize,
                                               final String sort,
                                               final boolean sortOrder) {
        final Collection<ProviderType> values = providerTypes.values();
        return PageSortUtil.pageSort(values,
                                     (ProviderType pt1, ProviderType pt2) -> {
                                         switch (sort) {
                                             case PROVIDER_TYPE_NAME_SORT:
                                                 return pt1.getProviderTypeName().compareTo(pt2.getProviderTypeName());
                                             case PROVIDER_TYPE_VERSION_SORT:
                                                 return pt1.getVersion().compareTo(pt2.getVersion());
                                             default:
                                                 return pt1.toString().compareTo(pt2.toString());
                                         }
                                     },
                                     page,
                                     pageSize,
                                     sort,
                                     sortOrder);
    }

    @Override
    public void deregisterProviderType(final ProviderType providerType) {
        checkNotNull("providerType",
                     providerType);
        providerTypes.remove(providerType);
    }

    @Override
    public void registerProvider(final Provider provider) {
        checkNotNull("provider",
                     provider);
        providers.put(provider.getId(),
                      provider);
    }

    @Override
    public List<Provider> getProviders(final Integer page,
                                       final Integer pageSize,
                                       final String sort,
                                       final boolean sortOrder) {
        final Collection<Provider> values = providers.values();
        return PageSortUtil.pageSort(values,
                                     (Provider p1, Provider p2) -> {
                                         switch (sort) {
                                             case PROVIDER_ID_SORT:
                                                 return p1.getId().compareTo(p2.getId());
                                             case PROVIDER_TYPE_NAME_SORT:
                                                 return p1.getProviderType().getProviderTypeName().compareTo(p2.getProviderType().getProviderTypeName());
                                             case PROVIDER_TYPE_VERSION_SORT:
                                                 return p1.getProviderType().getVersion().compareTo(p2.getProviderType().getVersion());
                                             default:
                                                 return p1.toString().compareTo(p2.toString());
                                         }
                                     },
                                     page,
                                     pageSize,
                                     sort,
                                     sortOrder);
    }

    @Override
    public List<Provider> getProvidersByType(final ProviderType providerType) {
        checkNotNull("providerType",
                     providerType);
        return providers.values().stream()
                .filter(provider -> providerType.equals(provider.getProviderType()))
                .collect(Collectors.toList());
    }

    @Override
    public Provider getProvider(final String providerId) {
        checkNotNull("providerId",
                     providerId);
        return providers.get(providerId);
    }

    @Override
    public void deregisterProvider(final Provider provider) {
        checkNotNull("provider",
                     provider);
        providers.remove(provider.getId());
    }

    @Override
    public void deregisterProvider(final String providerId) {
        checkNotNull("providerId",
                     providerId);
        providers.remove(providerId);
    }

    @Override
    public void registerRuntime(final Runtime runtime) {
        checkNotNull("runtime",
                     runtime);
        runtimes.put(runtime.getId(),
                     runtime);
    }

    @Override
    public List<Runtime> getRuntimes(final Integer page,
                                     final Integer pageSize,
                                     final String sort,
                                     final boolean sortOrder) {
        final Collection<Runtime> values = runtimes.values();
        return PageSortUtil.pageSort(values,
                                     (Runtime r1, Runtime r2) -> {
                                         switch (sort) {
                                             case RUNTIME_ID_SORT:
                                                 return r1.getId().compareTo(r2.getId());
                                             case RUNTIME_STATE_SORT:
                                                 return r1.getState().getState().compareTo(r2.getState().getState());
                                             default:
                                                 return r1.toString().compareTo(r2.toString());
                                         }
                                     },
                                     page,
                                     pageSize,
                                     sort,
                                     sortOrder);
    }

    @Override
    public Runtime getRuntimeById(final String runtimeId) {
        checkNotNull("runtimeId",
                     runtimeId);
        return runtimes.get(runtimeId);
    }

    @Override
    public void deregisterRuntime(final RuntimeId runtimeId) {
        checkNotNull("runtimeId",
                     runtimeId);
        runtimes.remove(runtimeId.getId());
    }

    @Override
    public <T extends Provider> Optional<T> getProvider(final ProviderId providerId,
                                                        final Class<T> clazz) {
        checkNotNull("providerId",
                     providerId);
        checkNotNull("clazz",
                     clazz);
        final Provider value = providers.get(providerId.getId());
        return Optional.ofNullable(value)
                .filter(provider -> clazz.isInstance(provider))
                .map(provider -> clazz.cast(provider));
    }
}
