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

package org.uberfire.preferences.client.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceBeanServerStore;

import static org.mockito.Mockito.*;

public class PreferenceBeanStoreImplTest {

    private final Command successCommand = spy(new Command() {
        @Override
        public void execute() {
        }
    });
    private final ParameterizedCommand<MyPreferencePortable> successParameterizedCommand = spy(new ParameterizedCommand<MyPreferencePortable>() {
        @Override
        public void execute(final MyPreferencePortable parameter) {
        }
    });
    private final ParameterizedCommand<Throwable> errorParameterizedCommand = spy(new ParameterizedCommand<Throwable>() {
        @Override
        public void execute(final Throwable parameter) {
        }
    });
    private PreferenceBeanServerStore store;
    private CallerMock<PreferenceBeanServerStore> storeCaller;
    private PreferenceBeanStoreImpl preferenceBeanStoreImpl;

    @Before
    public void setup() {
        store = mock(PreferenceBeanServerStore.class);
        storeCaller = new CallerMock<>(store);

        preferenceBeanStoreImpl = new PreferenceBeanStoreImpl(storeCaller);
    }

    @Test
    public void loadSuccessfullyTest() {
        preferenceBeanStoreImpl.load(new MyPreferencePortable(),
                                     successParameterizedCommand,
                                     errorParameterizedCommand);

        verify(store).load(any(MyPreferencePortable.class));
        verify(successParameterizedCommand).execute(any(MyPreferencePortable.class));
    }

    @Test
    public void loadWithErrorTest() {
        doThrow(new RuntimeException("error")).when(store).load(any(BasePreferencePortable.class));

        preferenceBeanStoreImpl.load(new MyPreferencePortable(),
                                     successParameterizedCommand,
                                     errorParameterizedCommand);

        verify(store).load(any(MyPreferencePortable.class));
        verify(errorParameterizedCommand).execute(any(Throwable.class));
    }

    @Test
    public void saveSuccessfullyTest() {
        preferenceBeanStoreImpl.save(new MyPreferencePortable(),
                                     successCommand,
                                     errorParameterizedCommand);

        verify(store).save(any(MyPreferencePortable.class));
        verify(successCommand).execute();
    }

    @Test
    public void saveWithErrorTest() {
        doThrow(new RuntimeException("error")).when(store).save(any(BasePreferencePortable.class));

        preferenceBeanStoreImpl.save(new MyPreferencePortable(),
                                     successCommand,
                                     errorParameterizedCommand);

        verify(store).save(any(MyPreferencePortable.class));
        verify(errorParameterizedCommand).execute(any(Throwable.class));
    }

    @Test
    public void saveCollectionSuccessfullyTest() {
        Collection<BasePreferencePortable<? extends BasePreference<?>>> preferences = new ArrayList<>();
        preferences.add(new MyPreferencePortable());
        preferenceBeanStoreImpl.save(preferences,
                                     successCommand,
                                     errorParameterizedCommand);

        verify(store).save(preferences);
        verify(successCommand).execute();
    }

    @Test
    public void saveCollectionWithErrorTest() {
        doThrow(new RuntimeException("error")).when(store).save(anyCollection());

        Collection<BasePreferencePortable<? extends BasePreference<?>>> preferences = new ArrayList<>();
        preferences.add(new MyPreferencePortable());
        preferenceBeanStoreImpl.save(preferences,
                                     successCommand,
                                     errorParameterizedCommand);

        verify(store).save(preferences);
        verify(errorParameterizedCommand).execute(any(Throwable.class));
    }

    class MyPreference implements BasePreference<MyPreference> {

    }

    class MyPreferencePortable extends MyPreference implements BasePreferencePortable<MyPreference> {

        @Override
        public Class<MyPreference> getPojoClass() {
            return MyPreference.class;
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
    }
}
