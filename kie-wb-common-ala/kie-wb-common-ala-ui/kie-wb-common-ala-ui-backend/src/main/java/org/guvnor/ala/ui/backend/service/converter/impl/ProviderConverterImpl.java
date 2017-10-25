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

package org.guvnor.ala.ui.backend.service.converter.impl;

import org.guvnor.ala.ui.backend.service.converter.ProviderConverter;
import org.guvnor.ala.ui.backend.service.handler.BackendProviderHandler;
import org.guvnor.ala.ui.backend.service.handler.BackendProviderHandlerRegistry;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderTypeKey;

/**
 * Manages the conversion of a Provider representation in the guvnor-ala core domain, to a representation that will be
 * used in the UI related models.
 */
public class ProviderConverterImpl
        implements ProviderConverter {

    private BackendProviderHandlerRegistry handlerRegistry;

    public ProviderConverterImpl(final BackendProviderHandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public Class<Provider> getModelType() {
        return Provider.class;
    }

    @Override
    public Class<org.guvnor.ala.runtime.providers.Provider> getDomainType() {
        return org.guvnor.ala.runtime.providers.Provider.class;
    }

    @Override
    public org.guvnor.ala.runtime.providers.Provider toDomain(Provider modelValue) {
        throw new RuntimeException("toDomain conversion is not supported by this converter.");
    }

    /**
     * @param provider A provider representation in the guvnor-ala core domain.
     * @return the converted version of the provider in the format managed by the UI related modules.
     */
    @Override
    public Provider toModel(org.guvnor.ala.runtime.providers.Provider provider) {
        Provider result = null;
        if (provider != null) {
            ProviderTypeKey providerTypeKey = new ProviderTypeKey(provider.getProviderType().getProviderTypeName(),
                                                                  provider.getProviderType().getVersion());
            ProviderKey providerKey = new ProviderKey(providerTypeKey,
                                                      provider.getId());

            final BackendProviderHandler handler = handlerRegistry.ensureHandler(providerTypeKey);
            @SuppressWarnings("unchecked")
            final ProviderConfiguration providerConfiguration = (ProviderConfiguration)
                    handler.getProviderConfigConverter().toModel(provider.getConfig());
            result = new Provider(providerKey,
                                  providerConfiguration);
        }
        return result;
    }
}
