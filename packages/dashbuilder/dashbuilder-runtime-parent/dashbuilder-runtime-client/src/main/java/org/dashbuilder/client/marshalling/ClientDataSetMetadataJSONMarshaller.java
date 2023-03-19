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
package org.dashbuilder.client.marshalling;

import java.util.Arrays;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.dataprovider.DataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.dataset.json.DataSetMetadataJSONMarshaller;

@ApplicationScoped
public class ClientDataSetMetadataJSONMarshaller extends DataSetMetadataJSONMarshaller {

    public ClientDataSetMetadataJSONMarshaller() {
        super(new DataSetDefJSONMarshaller(new ClientDataSetProviderRegistry()));
    }

    public static class ClientDataSetProviderRegistry implements DataSetProviderRegistry {

        DataSetProviderType<?>[] PROVIDERS = {
                                           DataSetProviderType.STATIC,
                                           DataSetProviderType.BEAN,
                                           DataSetProviderType.SQL,
                                           DataSetProviderType.CSV,
                                           DataSetProviderType.PROMETHEUS,
                                           DataSetProviderType.KAFKA
        };

        @Override
        public void registerDataProvider(DataSetProvider dataProvider) {
            // not used
        }

        @Override
        public DataSetProvider getDataSetProvider(DataSetProviderType type) {
            // not used
            return null;
        }

        @Override
        public Set<DataSetProviderType> getAvailableTypes() {
            // not used
            return null;
        }

        @Override
        public DataSetProviderType getProviderTypeByName(String name) {
            return Arrays.stream(PROVIDERS)
                         .filter(p -> p.getName().equalsIgnoreCase(name))
                         .findFirst()
                         .orElseThrow(() -> new RuntimeException("Provider not found: " + name));
        }
    }

}
