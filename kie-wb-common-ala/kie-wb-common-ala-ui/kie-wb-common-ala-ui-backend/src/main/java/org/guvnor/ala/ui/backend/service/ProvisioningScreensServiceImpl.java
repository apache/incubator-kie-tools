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

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.ProvidersInfo;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.guvnor.ala.ui.model.RuntimesInfo;
import org.guvnor.ala.ui.service.ProviderService;
import org.guvnor.ala.ui.service.ProviderTypeService;
import org.guvnor.ala.ui.service.ProvisioningScreensService;
import org.guvnor.ala.ui.service.RuntimeService;
import org.jboss.errai.bus.server.annotations.Service;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class ProvisioningScreensServiceImpl
        implements ProvisioningScreensService {

    private ProviderTypeService providerTypeService;

    private ProviderService providerService;

    private RuntimeService runtimeService;

    public ProvisioningScreensServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public ProvisioningScreensServiceImpl(final ProviderTypeService providerTypeService,
                                          final ProviderService providerService,
                                          final RuntimeService runtimeService) {
        this.providerTypeService = providerTypeService;
        this.providerService = providerService;
        this.runtimeService = runtimeService;
    }

    @Override
    public ProvidersInfo getProvidersInfo(final ProviderTypeKey providerTypeKey) {
        checkNotNull("providerTypeKey",
                     providerTypeKey);
        final ProviderType providerType = providerTypeService.getProviderType(providerTypeKey);
        if (providerType == null) {
            return null;
        }
        final Collection<ProviderKey> providersKey = providerService.getProvidersKey(providerType);
        return new ProvidersInfo(providerType,
                                 providersKey);
    }

    @Override
    public RuntimesInfo getRuntimesInfo(final ProviderKey providerKey) {
        checkNotNull("providerKey",
                     providerKey);
        final Provider provider = providerService.getProvider(providerKey);
        if (provider == null) {
            return null;
        }
        final Collection<RuntimeListItem> items = runtimeService.getRuntimeItems(providerKey);
        return new RuntimesInfo(provider,
                                items);
    }

    @Override
    public boolean hasRuntimes(final ProviderKey providerKey) {
        checkNotNull("providerKey",
                     providerKey);
        return !runtimeService.getRuntimeItems(providerKey).isEmpty();
    }
}
