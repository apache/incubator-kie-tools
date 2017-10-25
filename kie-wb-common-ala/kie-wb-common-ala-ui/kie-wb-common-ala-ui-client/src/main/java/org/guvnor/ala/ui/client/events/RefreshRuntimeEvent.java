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

import org.guvnor.ala.ui.model.ProviderKey;

public class RefreshRuntimeEvent {

    private final ProviderKey providerKey;

    public RefreshRuntimeEvent(final ProviderKey providerKey) {
        this.providerKey = providerKey;
    }

    public ProviderKey getProviderKey() {
        return providerKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RefreshRuntimeEvent that = (RefreshRuntimeEvent) o;

        return providerKey != null ? providerKey.equals(that.providerKey) : that.providerKey == null;
    }

    @Override
    public int hashCode() {
        int result = providerKey != null ? providerKey.hashCode() : 0;
        result = ~~result;
        return result;
    }
}
