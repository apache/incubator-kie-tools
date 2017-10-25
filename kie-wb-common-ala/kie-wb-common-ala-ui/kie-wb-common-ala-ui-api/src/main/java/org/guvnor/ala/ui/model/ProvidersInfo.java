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

package org.guvnor.ala.ui.model;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Class for modelling the providers associated with a provider type.
 */
@Portable
public class ProvidersInfo {

    private ProviderType providerType;

    private Collection<ProviderKey> providersKey = new ArrayList<>();

    public ProvidersInfo(@MapsTo("providerType") final ProviderType providerType,
                         @MapsTo("providersKey") final Collection<ProviderKey> providersKey) {
        this.providerType = providerType;
        this.providersKey = providersKey;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public Collection<ProviderKey> getProvidersKey() {
        return providersKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProvidersInfo that = (ProvidersInfo) o;

        if (providerType != null ? !providerType.equals(that.providerType) : that.providerType != null) {
            return false;
        }
        return providersKey != null ? providersKey.equals(that.providersKey) : that.providersKey == null;
    }

    @Override
    public int hashCode() {
        int result = providerType != null ? providerType.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (providersKey != null ? providersKey.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
