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

package org.guvnor.ala.ui.client.events;

import org.guvnor.ala.ui.model.ProviderTypeKey;

public class ProviderTypeSelectedEvent {

    private final ProviderTypeKey providerTypeKey;
    private final String providerId;

    public ProviderTypeSelectedEvent(final ProviderTypeKey providerTypeKey) {
        this(providerTypeKey,
             null);
    }

    public ProviderTypeSelectedEvent(final ProviderTypeKey providerTypeKey,
                                     final String providerId) {
        this.providerTypeKey = providerTypeKey;
        this.providerId = providerId;
    }

    public ProviderTypeKey getProviderTypeKey() {
        return providerTypeKey;
    }

    public String getProviderId() {
        return providerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProviderTypeSelectedEvent that = (ProviderTypeSelectedEvent) o;

        if (providerTypeKey != null ? !providerTypeKey.equals(that.providerTypeKey) : that.providerTypeKey != null) {
            return false;
        }
        return providerId != null ? providerId.equals(that.providerId) : that.providerId == null;
    }

    @Override
    public int hashCode() {
        int result = providerTypeKey != null ? providerTypeKey.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (providerId != null ? providerId.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
