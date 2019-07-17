/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.preferences.client.store;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

import static org.junit.Assert.assertEquals;

public class PreferenceBeanStoreClientImplTest {

    private PreferenceBeanStore preferenceBeanStoreClient;

    @Before
    public void before() {
        preferenceBeanStoreClient = new PreferenceBeanStoreClientImpl();
    }

    @Test
    public void testLoad() {
        MyPreferencePortable preference = new MyPreferencePortable();
        preferenceBeanStoreClient.load(preference,
                                       p -> {
                                       },
                                       p -> {
                                       });

        assertEquals("foo", preference.value);
    }

    @Test
    public void testLoadWithPreferenceScopeResolutionStrategyInfo() {
        MyPreferencePortable preference = new MyPreferencePortable();
        preferenceBeanStoreClient.load(preference,
                                       null,
                                       p -> {
                                       },
                                       p -> {
                                       });

        assertEquals("foo", preference.value);
    }

    private static class MyPreferencePortable implements BasePreferencePortable<MyPreferencePortable> {

        private String value;

        @Override
        public Class<MyPreferencePortable> getPojoClass() {
            return MyPreferencePortable.class;
        }

        @Override
        public String identifier() {
            return null;
        }

        @Override
        public String[] parents() {
            return new String[0];
        }

        @Override
        public String bundleKey() {
            return null;
        }

        @Override
        public void set(final String property,
                        final Object value) {

        }

        @Override
        public Object get(final String property) {
            return null;
        }

        @Override
        public Map<String, PropertyFormType> getPropertiesTypes() {
            return null;
        }

        @Override
        public boolean isPersistable() {
            return false;
        }

        @Override
        public MyPreferencePortable defaultValue(final MyPreferencePortable defaultValue) {
            defaultValue.value = "foo";
            return defaultValue;
        }
    }
}