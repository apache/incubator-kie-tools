/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.serialization.impl;

import java.util.HashSet;
import java.util.Set;

import org.dashbuilder.dataprovider.DataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.kieserver.RuntimeKieServerDataSetProviderType;

public class InternalDataSetProviderRegistry implements DataSetProviderRegistry {

    Set<DataSetProviderType> providers;

    public InternalDataSetProviderRegistry() {
        initProviders();
    }

    @Override
    public void registerDataProvider(DataSetProvider dataProvider) {
        // empty
    }

    @Override
    public DataSetProvider getDataSetProvider(DataSetProviderType type) {
        // returning null because this registry will not be used in runtime, 
        // so this method should not be called in the scope of the API 
        return null;
    }

    @Override
    public Set<DataSetProviderType> getAvailableTypes() {
        return providers;
    }

    @Override
    public DataSetProviderType getProviderTypeByName(String name) {
        return providers.stream()
                        .filter(p -> p.getName().equals(name)).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Provider not found: " + name));
    }

    private void initProviders() {
        providers = new HashSet<>();
        providers.add(DataSetProviderType.BEAN);
        providers.add(DataSetProviderType.CSV);
        providers.add(DataSetProviderType.ELASTICSEARCH);
        providers.add(DataSetProviderType.KAFKA);
        providers.add(DataSetProviderType.PROMETHEUS);
        providers.add(DataSetProviderType.SQL);
        providers.add(DataSetProviderType.STATIC);
        providers.add(DataSetProviderType.PROMETHEUS);
        providers.add(new RuntimeKieServerDataSetProviderType());
    }

}
