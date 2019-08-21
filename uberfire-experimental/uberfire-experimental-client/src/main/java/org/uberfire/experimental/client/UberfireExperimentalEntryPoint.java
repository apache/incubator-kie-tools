/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.experimental.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.bus.client.util.BusToolsCli;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesDefRegistry;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;

@EntryPoint
@Bundle("resources/i18n/UberfireExperimentalConstants.properties")
public class UberfireExperimentalEntryPoint {

    private ClientExperimentalFeaturesRegistryService registryService;
    private ClientExperimentalFeaturesDefRegistry defRegistry;
    private ExperimentalActivitiesAuthorizationManager activitiesAuthorizationManager;

    @Inject
    public UberfireExperimentalEntryPoint(ClientExperimentalFeaturesRegistryService registryService, ClientExperimentalFeaturesDefRegistry defRegistry, ExperimentalActivitiesAuthorizationManager activitiesAuthorizationManager) {
        this.registryService = registryService;
        this.defRegistry = defRegistry;
        this.activitiesAuthorizationManager = activitiesAuthorizationManager;
    }

    @PostConstruct
    public void init() {
        if (!BusToolsCli.isRemoteCommunicationEnabled()) {
            return;
        }

        defRegistry.loadRegistry();
        registryService.loadRegistry();
        activitiesAuthorizationManager.init();
    }
}
