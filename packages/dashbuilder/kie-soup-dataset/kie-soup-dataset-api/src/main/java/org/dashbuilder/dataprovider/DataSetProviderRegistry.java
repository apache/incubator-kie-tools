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

import java.util.Set;

/**
 * Data set provider registry
 */
public interface DataSetProviderRegistry {

    /**
     * Register a data provider
     */
    void registerDataProvider(DataSetProvider dataProvider);

    /**
     * Get the provider instance named as specified.
     */
    DataSetProvider getDataSetProvider(DataSetProviderType type);

    /**
     * Get the set of {@link DataSetProviderType} instances available.
     */
    Set<DataSetProviderType> getAvailableTypes();

    /**
     * Get a {@link DataSetProviderType} by its name
     *
     * @return null if not found
     */
    DataSetProviderType getProviderTypeByName(String name);
}
