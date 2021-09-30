/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataSetProviderRegistryImpl implements DataSetProviderRegistry {

    private Map<String,DataSetProvider> dataSetProviderMap = new HashMap<>();
    private Set<DataSetProviderType> availableTypes = new HashSet<>();

    @Override
    public void registerDataProvider(DataSetProvider dataProvider) {
        DataSetProviderType type = dataProvider.getType();
        dataSetProviderMap.put(type.getName(), dataProvider);
        availableTypes.add(type);
    }

    @Override
    public DataSetProvider getDataSetProvider(DataSetProviderType type) {
        return dataSetProviderMap.get(type.getName());
    }

    @Override
    public Set<DataSetProviderType> getAvailableTypes() {
        return availableTypes;
    }

    @Override
    public DataSetProviderType getProviderTypeByName(String name) {
        DataSetProvider provider = dataSetProviderMap.get(name);
        return provider != null ? provider.getType() : null;
    }
}
