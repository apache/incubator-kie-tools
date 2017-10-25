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

import org.guvnor.ala.runtime.providers.ProviderType;

public class InternalProviderType
        implements ProviderType {

    private String providerTypeName;

    private String version;

    public InternalProviderType(final String providerTypeName,
                                final String version) {
        this.providerTypeName = providerTypeName;
        this.version = version;
    }

    @Override
    public String getProviderTypeName() {
        return providerTypeName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProviderType)) {
            return false;
        }

        ProviderType that = (ProviderType) o;

        if (providerTypeName != null ? !providerTypeName.equals(that.getProviderTypeName()) : that.getProviderTypeName() != null) {
            return false;
        }
        return version != null ? version.equals(that.getVersion()) : that.getVersion() == null;
    }

    @Override
    public int hashCode() {
        int result = providerTypeName != null ? providerTypeName.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
