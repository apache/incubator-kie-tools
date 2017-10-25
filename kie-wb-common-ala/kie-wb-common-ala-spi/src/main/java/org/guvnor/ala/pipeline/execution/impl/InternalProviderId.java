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

package org.guvnor.ala.pipeline.execution.impl;

import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.runtime.providers.ProviderType;

public class InternalProviderId
        implements ProviderId {

    private String id;

    private ProviderType providerType;

    public InternalProviderId(final String id,
                              final ProviderType providerType) {
        this.id = id;
        this.providerType = new InternalProviderType(providerType.getProviderTypeName(),
                                                     providerType.getVersion());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ProviderType getProviderType() {
        return providerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProviderId)) {
            return false;
        }

        ProviderId that = (ProviderId) o;

        if (id != null ? !id.equals(that.getId()) : that.getId() != null) {
            return false;
        }
        return providerType != null ? providerType.equals(that.getProviderType()) : that.getProviderType() == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (providerType != null ? providerType.hashCode() : 0);
        return result;
    }
}
