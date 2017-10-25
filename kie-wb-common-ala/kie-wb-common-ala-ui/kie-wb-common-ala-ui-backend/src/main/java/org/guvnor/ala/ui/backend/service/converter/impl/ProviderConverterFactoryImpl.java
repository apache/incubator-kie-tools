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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.ui.backend.service.converter.ProviderConfigConverter;
import org.guvnor.ala.ui.backend.service.converter.ProviderConverter;
import org.guvnor.ala.ui.backend.service.converter.ProviderConverterFactory;
import org.guvnor.ala.ui.backend.service.handler.BackendProviderHandler;
import org.guvnor.ala.ui.backend.service.handler.BackendProviderHandlerRegistry;
import org.guvnor.ala.ui.model.ProviderTypeKey;

@ApplicationScoped
public class ProviderConverterFactoryImpl
        implements ProviderConverterFactory {

    private BackendProviderHandlerRegistry handlerRegistry;

    private ProviderConverter providerConverter;

    public ProviderConverterFactoryImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public ProviderConverterFactoryImpl(final BackendProviderHandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
        this.providerConverter = new ProviderConverterImpl(handlerRegistry);
    }

    @Override
    public ProviderConfigConverter getProviderConfigConverter(final ProviderTypeKey providerTypeKey) {
        final BackendProviderHandler handler = handlerRegistry.ensureHandler(providerTypeKey);
        return handler.getProviderConfigConverter();
    }

    @Override
    public ProviderConverter getProviderConverter() {
        return providerConverter;
    }
}
