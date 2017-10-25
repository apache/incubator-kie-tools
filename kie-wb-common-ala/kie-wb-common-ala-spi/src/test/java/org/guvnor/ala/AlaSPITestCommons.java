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

package org.guvnor.ala;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;

import static org.mockito.Mockito.mock;

public class AlaSPITestCommons {

    public static final String PROVIDER_TYPE_NAME_FIELD = "ProviderType.providerTypeName.";

    public static final String PROVIDER_TYPE_VERSION_FIELD = "ProviderType.version.";

    public static final String PROVIDER_ID_FIELD = "Provider.id.";

    public static ProviderType mockProviderTypeSPI(final String suffix) {
        return new ProviderType() {
            @Override
            public String getProviderTypeName() {
                return PROVIDER_TYPE_NAME_FIELD + suffix;
            }

            @Override
            public String getVersion() {
                return PROVIDER_TYPE_VERSION_FIELD + suffix;
            }
        };
    }

    public static Provider mockProviderSPI(final ProviderType providerType,
                                           final String suffix) {
        return new Provider() {
            @Override
            public String getId() {
                return PROVIDER_ID_FIELD + suffix;
            }

            @Override
            public ProviderType getProviderType() {
                return providerType;
            }

            @Override
            public ProviderConfig getConfig() {
                return null;
            }
        };
    }

    public static List<ProviderType> mockProviderTypeListSPI(final int count) {
        List<ProviderType> providerTypes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            //mock an arbitrary set of provider types.
            providerTypes.add(mockProviderTypeSPI(Integer.toString(i)));
        }
        return providerTypes;
    }

    public static List<Provider> mockProviderListSPI(final ProviderType providerType,
                                                     int count) {
        return mockProviderListSPI(providerType,
                                   "",
                                   count);
    }

    public static List<Provider> mockProviderListSPI(final ProviderType providerType,
                                                     final String suffix,
                                                     int count) {
        List<Provider> providers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            providers.add(mockProviderSPI(providerType,
                                          Integer.toString(i) + suffix));
        }
        return providers;
    }

    public static <T> List<T> mockList(Class<T> clazz,
                                       int count) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(mock(clazz));
        }
        return result;
    }
}
