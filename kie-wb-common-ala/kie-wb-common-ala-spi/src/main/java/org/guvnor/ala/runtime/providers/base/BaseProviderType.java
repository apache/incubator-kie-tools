/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.runtime.providers.base;

import org.guvnor.ala.runtime.providers.ProviderType;

/**
 * BaseProviderType implementation to be extended by concrete ProviderTypes
 */
public abstract class BaseProviderType
        implements ProviderType {

    private final String providerTypeName;
    private final String version;

    public BaseProviderType(String providerName,
                            String version) {
        this.providerTypeName = providerName;
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
    public String toString() {
        return "BaseProviderType{" +
                "providerTypeName='" + providerTypeName + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseProviderType that = (BaseProviderType) o;

        if (providerTypeName != null ? !providerTypeName.equals(that.providerTypeName) : that.providerTypeName != null) {
            return false;
        }
        return version != null ? version.equals(that.version) : that.version == null;
    }

    @Override
    public int hashCode() {
        int result = providerTypeName != null ? providerTypeName.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
