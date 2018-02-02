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

import static org.guvnor.ala.registry.RuntimeRegistry.PROVIDER_TYPE_NAME_SORT;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.services.api.backend.RuntimeProvisioningServiceBackend;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.guvnor.ala.ui.preferences.ProvisioningPreferences;
import org.guvnor.ala.ui.service.ProviderTypeService;
import org.guvnor.common.services.shared.preferences.GuvnorPreferenceScopes;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.preferences.shared.PreferenceScopeFactory;

@Service
@ApplicationScoped
public class ProviderTypeServiceImpl
        implements ProviderTypeService {

    private RuntimeProvisioningServiceBackend runtimeProvisioningService;

    private ProvisioningPreferences provisioningPreferences;

    private PreferenceScopeFactory scopeFactory;

    public ProviderTypeServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public ProviderTypeServiceImpl(final RuntimeProvisioningServiceBackend runtimeProvisioningService,
                                   final ProvisioningPreferences provisioningPreferences,
                                   final PreferenceScopeFactory scopeFactory) {
        this.runtimeProvisioningService = runtimeProvisioningService;
        this.provisioningPreferences = provisioningPreferences;
        this.scopeFactory = scopeFactory;
    }

    @Override
    public Collection<ProviderType> getAvailableProviderTypes() {
        List<ProviderType> result = new ArrayList<>();
        List<org.guvnor.ala.runtime.providers.ProviderType> providers =
                runtimeProvisioningService.getProviderTypes(0,
                                                            100,
                                                            PROVIDER_TYPE_NAME_SORT,
                                                            true);

        if (providers != null) {
            providers.forEach(providerType ->
                                      result.add(new ProviderType(new ProviderTypeKey(providerType.getProviderTypeName(),
                                                                                      providerType.getVersion()),
                                                                  providerType.getProviderTypeName()))
            );
        }
        return result;
    }

    @Override
    public ProviderType getProviderType(final ProviderTypeKey providerTypeKey) {
        checkNotNull("providerTypeKey",
                     providerTypeKey);
        return getAvailableProviderTypes().stream()
                .filter(providerType -> providerType.getKey().equals(providerTypeKey))
                .findFirst().orElse(null);
    }

    @Override
    public Collection<ProviderType> getEnabledProviderTypes() {
        return getProviderTypesStatus().entrySet().stream()
                .filter(entry -> ProviderTypeStatus.ENABLED.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void enableProviderTypes(final Collection<ProviderType> providerTypes) {
        checkNotEmpty("providerTypes",
                      providerTypes);
        providerTypes.forEach(providerType -> enableProviderType(providerType,
                                                                 true));
    }

    @Override
    public void disableProviderType(final ProviderType providerType) {
        checkNotNull("providerType",
                     providerType);
        enableProviderType(providerType,
                           false);
    }

    @Override
    public Map<ProviderType, ProviderTypeStatus> getProviderTypesStatus() {
        final Map<ProviderType, ProviderTypeStatus> result = new HashMap<>();

        readProviderTypeEnablements().forEach((providerType, isEnabled) ->
                                                      result.put(providerType,
                                                                 Boolean.TRUE.equals(isEnabled) ? ProviderTypeStatus.ENABLED : ProviderTypeStatus.DISABLED)
        );
        getAvailableProviderTypes().forEach(providerType -> {
            if (!result.containsKey(providerType)) {
                result.put(providerType,
                           ProviderTypeStatus.DISABLED);
            }
        });
        return result;
    }

    private void enableProviderType(final ProviderType providerType,
                                    final boolean enable) {
        checkNotNull("providerType",
                     providerType);
        final Map<ProviderType, Boolean> result = readProviderTypeEnablements();
        result.put(providerType,
                   enable);
        saveProviderTypeEnablements(result);
    }

    private Map<ProviderType, Boolean> readProviderTypeEnablements() {
        provisioningPreferences.load();
        final Map<ProviderType, Boolean> result = provisioningPreferences.getProviderTypeEnablements();
        return result != null ? result : new HashMap<>();
    }

    private void saveProviderTypeEnablements(final Map<ProviderType, Boolean> providerTypeEnablements) {
        provisioningPreferences.load();
        provisioningPreferences.setProviderTypeEnablements(providerTypeEnablements);
        provisioningPreferences.save(scopeFactory.createScope(GuvnorPreferenceScopes.GLOBAL));
    }
}
