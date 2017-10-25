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

package org.guvnor.ala.ui.wildfly.client.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.handler.ClientProviderHandler;
import org.guvnor.ala.ui.client.handler.FormResolver;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.wildfly.client.resources.images.GuvnorAlaWildflyUIImageResources;

/**
 * Client provider handler implementation for Wildfly providers.
 * @see ClientProviderHandler
 */
@ApplicationScoped
public class WildflyClientProviderHandler
        implements ClientProviderHandler {

    private static final String PROVIDER_TYPE_NAME = "wildfly";

    private WildflyFormResolver formResolver;

    @Inject
    public WildflyClientProviderHandler(WildflyFormResolver formResolver) {
        this.formResolver = formResolver;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean acceptProviderType(ProviderTypeKey providerTypeKey) {
        return providerTypeKey != null && PROVIDER_TYPE_NAME.equals(providerTypeKey.getId());
    }

    @Override
    public FormResolver getFormResolver() {
        return formResolver;
    }

    @Override
    public String getProviderTypeImageURL() {
        return GuvnorAlaWildflyUIImageResources.INSTANCE.providerIcon().getSafeUri().asString();
    }
}
