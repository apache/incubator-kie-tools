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

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;

/**
 * BaseProvider implementation to be extended by concrete Providers
 */
public abstract class BaseProvider<C extends ProviderConfig>
        implements Provider<C>,
                   ProviderConfig {

    private String id;
    private C config;
    private ProviderType providerType;

    public BaseProvider() {
        //No-args constructor for enabling marshalling to work, please do not remove.
    }

    public BaseProvider(final String id,
                        final ProviderType providerType,
                        final C config) {
        this.id = id;
        this.providerType = providerType;
        this.config = config;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public C getConfig() {
        return config;
    }

    @Override
    public ProviderType getProviderType() {
        return providerType;
    }

    @Override
    public String toString() {
        return "BaseProvider{" +
                "id='" + id + '\'' +
                ", config=" + config +
                ", providerType=" + providerType +
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

        BaseProvider<?> that = (BaseProvider<?>) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (config != null ? !config.equals(that.config) : that.config != null) {
            return false;
        }
        return providerType != null ? providerType.equals(that.providerType) : that.providerType == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (config != null ? config.hashCode() : 0);
        result = 31 * result + (providerType != null ? providerType.hashCode() : 0);
        return result;
    }
}
