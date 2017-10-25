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

package org.guvnor.ala.ui.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.services.api.backend.RuntimeProvisioningServiceBackend;
import org.guvnor.ala.ui.backend.service.converter.ProviderConverterFactory;
import org.guvnor.ala.ui.exceptions.ServiceException;
import org.guvnor.ala.ui.model.AbstractHasKeyObject;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.service.ProviderService;
import org.jboss.errai.bus.server.annotations.Service;

import static java.util.stream.Collectors.toList;
import static org.guvnor.ala.registry.RuntimeRegistry.PROVIDER_TYPE_NAME_SORT;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class ProviderServiceImpl
        implements ProviderService {

    private RuntimeProvisioningServiceBackend runtimeProvisioningService;

    private ProviderConverterFactory providerConverterFactory;

    public ProviderServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public ProviderServiceImpl(final RuntimeProvisioningServiceBackend runtimeProvisioningService,
                               final ProviderConverterFactory providerConverterFactory) {
        this.runtimeProvisioningService = runtimeProvisioningService;
        this.providerConverterFactory = providerConverterFactory;
    }

    @Override
    public Collection<Provider> getProviders(final ProviderType providerType) {
        checkNotNull("providerType",
                     providerType);
        return getAllProviders().stream()
                .filter(provider -> provider.getKey().getProviderTypeKey().equals(providerType.getKey()))
                .collect(toList());
    }

    @Override
    public Collection<ProviderKey> getProvidersKey(final ProviderType providerType) {
        return getProviders(providerType).stream()
                .map(AbstractHasKeyObject::getKey)
                .collect(toList());
    }

    @Override
    public void createProvider(final ProviderType providerType,
                               final ProviderConfiguration configuration) {
        checkNotNull("providerType",
                     providerType);
        checkNotNull("providerType.providerTypeKey",
                     providerType.getKey());
        checkNotNull("configuration",
                     configuration);
        checkNotEmpty("configuration.values",
                      configuration.getValues());

        validateForCreateProvider(configuration);

        @SuppressWarnings("unchecked")
        final ProviderConfig providerConfig =
                (ProviderConfig) providerConverterFactory.getProviderConfigConverter(providerType.getKey()).toDomain(configuration);
        runtimeProvisioningService.registerProvider(providerConfig);
    }

    @Override
    public void deleteProvider(final ProviderKey providerKey) {
        runtimeProvisioningService.unregisterProvider(providerKey.getId());
    }

    @Override
    public Provider getProvider(final ProviderKey providerKey) {
        List<org.guvnor.ala.runtime.providers.Provider> providers =
                runtimeProvisioningService.getProviders(0,
                                                        1000,
                                                        PROVIDER_TYPE_NAME_SORT,
                                                        true);
        Optional<Provider> result = Optional.empty();
        if (providers != null) {
            result = providers.stream()
                    .filter(provider -> provider.getId().equals(providerKey.getId()))
                    .map(this::convert)
                    .findFirst();
        }
        return result.orElse(null);
    }

    private Collection<Provider> getAllProviders() {
        Collection<Provider> result = new ArrayList<>();
        List<org.guvnor.ala.runtime.providers.Provider> providers =
                runtimeProvisioningService.getProviders(0,
                                                        1000,
                                                        PROVIDER_TYPE_NAME_SORT,
                                                        true);
        if (providers != null) {
            result = providers.stream()
                    .map(this::convert)
                    .collect(toList());
        }
        return result;
    }

    private Provider convert(org.guvnor.ala.runtime.providers.Provider provider) {
        return providerConverterFactory.getProviderConverter().toModel(provider);
    }

    private void validateForCreateProvider(ProviderConfiguration configuration) {
        for (final Provider provider : getAllProviders()) {
            if (configuration.getId().equals(provider.getKey().getId())) {
                throw new ServiceException("A provider with the given name already exists: " + configuration.getId());
            }
        }
    }
}