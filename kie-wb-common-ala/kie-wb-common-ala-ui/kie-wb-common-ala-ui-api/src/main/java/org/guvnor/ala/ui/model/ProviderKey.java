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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Identifier of a provider.
 */
@Portable
public class ProviderKey {

    private ProviderTypeKey providerTypeKey;
    private String id;

    public ProviderKey(@MapsTo("providerTypeKey") final ProviderTypeKey providerTypeKey,
                       @MapsTo("id") final String id) {
        this.providerTypeKey = providerTypeKey;
        this.id = id;
    }

    public ProviderTypeKey getProviderTypeKey() {
        return providerTypeKey;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProviderKey that = (ProviderKey) o;

        if (providerTypeKey != null ? !providerTypeKey.equals(that.providerTypeKey) : that.providerTypeKey != null) {
            return false;
        }
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = providerTypeKey != null ? providerTypeKey.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "ProviderKey{" +
                "providerTypeKey=" + providerTypeKey +
                ", id='" + id + '\'' +
                '}';
    }
}