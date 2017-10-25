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

import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.ProvidersInfo;
import org.guvnor.ala.ui.model.RuntimesInfo;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Service for holding methods oriented to the different provisioning-ui screens and client widgets.
 */
@Remote
public interface ProvisioningScreensService {

    /**
     * Gets the information about all the defined providers for a given provider type and loads provider type at the same time.
     * @param providerTypeKey
     * @return a ProviderInfo with all the providers related to the provider type, or null if the provider type not exists.
     */
    ProvidersInfo getProvidersInfo(final ProviderTypeKey providerTypeKey);

    /**
     * Gets the information about the runtimes associated to a given provider and loads the provider at the same time.
     * @param providerKey
     * @return a RuntimeInfo with the runtimes associated to the providerKey and the Provider information, or null if
     * the provider not exists.
     */
    RuntimesInfo getRuntimesInfo(final ProviderKey providerKey);

    /**
     * @param providerKey
     * @return true if there are runtimes associated to the given provider, false in any other case.
     */
    boolean hasRuntimes(final ProviderKey providerKey);
}
