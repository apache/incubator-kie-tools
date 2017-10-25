/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.access;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.uberfire.commons.lifecycle.Disposable;

/**
 * This interface abstracts the OpenShift client creation and caching (per provider id).
 */
public interface OpenShiftAccessInterface extends Disposable {

    /**
     * Gets the OpenShift Client for the provided ProviderId.
     * @param providerId the ProviderId
     * @return OpenShiftClient for the ProviderId; if it doesn't exist, it creates a new OpenshiftClient
     * @see OpenShiftClient the (potentially cached) OpenShiftClient
     */
    OpenShiftClient getOpenShiftClient(final ProviderId providerId);

    /**
     * Always creates a new OpenShiftClient with the provided Provider configuration.
     * @param providerConfig the Provider configuration
     * @return the OpenShiftClient
     * @see OpenShiftClient the (not-cached) OpenShiftClient
     */
    public OpenShiftClient newOpenShiftClient(final ProviderConfig providerConfig);

}
