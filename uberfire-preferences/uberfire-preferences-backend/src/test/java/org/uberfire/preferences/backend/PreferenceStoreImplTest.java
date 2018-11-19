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

package org.uberfire.preferences.backend;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;

import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.backend.server.io.object.ObjectStorage;
import org.uberfire.backend.server.io.object.ObjectStorageImpl;
import org.uberfire.backend.server.spaces.SpacesAPIImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.event.PreferenceUpdatedEvent;
import org.uberfire.preferences.shared.impl.DefaultPreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.impl.DefaultPreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.DefaultScopes;
import org.uberfire.preferences.shared.impl.PreferenceScopeFactoryImpl;
import org.uberfire.preferences.shared.impl.PreferenceScopeImpl;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.preferences.shared.impl.PreferenceScopedValue;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class PreferenceStoreImplTest {

    private static final String allUsersScopeType = DefaultScopes.ALL_USERS.type();
    private static final String entireApplicationScopeType = DefaultScopes.ENTIRE_APPLICATION.type();
    private static final String userScopeType = DefaultScopes.USER.type();

    private static final String allUsersScopeKey = allUsersScopeType;
    private static final String entireApplicationScopeKey = entireApplicationScopeType;
    private static final String userScopeKey = "my-user";

    private static final PreferenceScopeImpl allUsersScope = new PreferenceScopeImpl(allUsersScopeType,
                                                                                     allUsersScopeKey,
                                                                                     null);
    private static final PreferenceScopeImpl entireApplicationScope = new PreferenceScopeImpl(entireApplicationScopeType,
                                                                                              entireApplicationScopeKey,
                                                                                              null);
    private static final PreferenceScopeImpl userScope = new PreferenceScopeImpl(userScopeType,
                                                                                 userScopeKey,
                                                                                 null);
    private static final String USER = userScopeKey;
    private static final String KEY = "my.preference.key";
    private static final String VALUE = "value";
    private static final String DEFAULT_VALUE = "defaultValue";
    private static final String FIRST_KEY = "my.first.preference.key";
    private static final String FIRST_VALUE = "value1";
    private static final String SECOND_KEY = "my.second.preference.key";
    private static final String SECOND_VALUE = "value2";
    private static final String THIRD_KEY = "my.third.preference.key";
    private static final String THIRD_VALUE = "value3";
    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();
    private PreferenceScope userEntireApplicationScope, allUsersEntireApplication;
    private ParameterizedCommand<String> callback;

    private PreferenceScopeTypes scopeTypes;

    private PreferenceScopeFactory scopeFactory;

    private PreferenceStorageImpl storage;

    private PreferenceScopeResolutionStrategy preferenceScopeResolutionStrategy;

    private PreferenceScopeResolutionStrategyInfo preferenceScopeResolutionStrategyInfo;

    private PreferenceStoreImpl preferenceStore;
    
    @Before
    public void setup() throws IOException {
        MappingContextSingleton.get();
        fileSystemTestingUtils.setup();

        callback = (ParameterizedCommand<String>) mock(ParameterizedCommand.class);

        final SessionInfo sessionInfo = new SessionInfoMock(USER);
        final FileSystem fileSystem = mockFileSystem();
        final IOService ioService = mockIoService(fileSystem);

        ObjectStorage objectStorage = new ObjectStorageImpl(ioService);
        
        Event<PreferenceUpdatedEvent> preferenceUpdatedEvent = mock(Event.class);

        scopeTypes = new DefaultPreferenceScopeTypes(new ServerUsernameProvider(sessionInfo));
        scopeFactory = new PreferenceScopeFactoryImpl(scopeTypes);
        preferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy(scopeFactory,
                                                                                         null);
        preferenceScopeResolutionStrategyInfo = preferenceScopeResolutionStrategy.getInfo();
        storage = spy(new PreferenceStorageImpl(ioService,
                                                sessionInfo,
                                                scopeTypes,
                                                scopeFactory,
                                                objectStorage,
                                                new SpacesAPIImpl(),
                                                preferenceUpdatedEvent));
        storage.init();

        preferenceStore = spy(new PreferenceStoreImpl(storage,
                                                      scopeFactory,
                                                      preferenceScopeResolutionStrategy));

        userEntireApplicationScope = scopeFactory.createScope(userScope,
                                                              entireApplicationScope);
        allUsersEntireApplication = scopeFactory.createScope(allUsersScope,
                                                             entireApplicationScope);
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void putInScopeTest() {
        preferenceStore.put(allUsersEntireApplication,
                            KEY,
                            VALUE);
        preferenceStore.put(allUsersEntireApplication,
                            KEY,
                            VALUE);

        verify(storage,
               times(2)).write(allUsersEntireApplication,
                               KEY,
                               VALUE);
    }

    @Test
    public void putInDefaultScopeOfAScopeResolutionStrategyTest() {
        preferenceStore.put(preferenceScopeResolutionStrategyInfo,
                            KEY,
                            VALUE);
        preferenceStore.put(preferenceScopeResolutionStrategyInfo,
                            KEY,
                            VALUE);

        verify(storage,
               times(2)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               KEY,
                               VALUE);
    }

    @Test
    public void putInDefaultScopeTest() {
        preferenceStore.put(KEY,
                            VALUE);
        preferenceStore.put(KEY,
                            VALUE);

        verify(storage,
               times(2)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               KEY,
                               VALUE);
    }

    @Test
    public void putMapInScopeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put(FIRST_KEY,
                        FIRST_VALUE);
        preferences.put(SECOND_KEY,
                        SECOND_VALUE);

        preferenceStore.put(allUsersEntireApplication,
                            preferences);
        preferenceStore.put(allUsersEntireApplication,
                            preferences);

        verify(storage,
               times(2)).write(allUsersEntireApplication,
                               FIRST_KEY,
                               FIRST_VALUE);
        verify(storage,
               times(2)).write(allUsersEntireApplication,
                               SECOND_KEY,
                               SECOND_VALUE);
    }

    @Test
    public void putMapInDefaultScopeOfAScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put(FIRST_KEY,
                        FIRST_VALUE);
        preferences.put(SECOND_KEY,
                        SECOND_VALUE);

        preferenceStore.put(preferenceScopeResolutionStrategyInfo,
                            preferences);
        preferenceStore.put(preferenceScopeResolutionStrategyInfo,
                            preferences);

        verify(storage,
               times(2)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               FIRST_KEY,
                               FIRST_VALUE);
        verify(storage,
               times(2)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               SECOND_KEY,
                               SECOND_VALUE);
    }

    @Test
    public void putMapInDefaultScopeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put(FIRST_KEY,
                        FIRST_VALUE);
        preferences.put(SECOND_KEY,
                        SECOND_VALUE);

        preferenceStore.put(preferences);
        preferenceStore.put(preferences);

        verify(storage,
               times(2)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               FIRST_KEY,
                               FIRST_VALUE);
        verify(storage,
               times(2)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               SECOND_KEY,
                               SECOND_VALUE);
    }

    @Test
    public void putIfAbsentInScopeTest() {
        preferenceStore.putIfAbsent(allUsersEntireApplication,
                                    KEY,
                                    VALUE);
        preferenceStore.putIfAbsent(allUsersEntireApplication,
                                    KEY,
                                    VALUE);

        verify(storage,
               times(1)).write(allUsersEntireApplication,
                               KEY,
                               VALUE);
    }

    @Test
    public void putIfAbsentInDefaultScopeOfAScopeResolutionStrategyTest() {
        preferenceStore.putIfAbsent(preferenceScopeResolutionStrategyInfo,
                                    KEY,
                                    VALUE);
        preferenceStore.putIfAbsent(preferenceScopeResolutionStrategyInfo,
                                    KEY,
                                    VALUE);

        verify(storage,
               times(1)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               KEY,
                               VALUE);
    }

    @Test
    public void putIfAbsentInDefaultScopeTest() {
        preferenceStore.putIfAbsent(KEY,
                                    VALUE);
        preferenceStore.putIfAbsent(KEY,
                                    VALUE);

        verify(storage,
               times(1)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               KEY,
                               VALUE);
    }

    @Test
    public void putIfAbsentMapInScopeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put(FIRST_KEY,
                        FIRST_VALUE);
        preferences.put(SECOND_KEY,
                        SECOND_VALUE);

        preferenceStore.putIfAbsent(allUsersEntireApplication,
                                    preferences);
        preferenceStore.putIfAbsent(allUsersEntireApplication,
                                    preferences);

        verify(storage,
               times(1)).write(allUsersEntireApplication,
                               FIRST_KEY,
                               FIRST_VALUE);
        verify(storage,
               times(1)).write(allUsersEntireApplication,
                               SECOND_KEY,
                               SECOND_VALUE);
    }

    @Test
    public void putIfAbsentMapInDefaultScopeOfAScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put(FIRST_KEY,
                        FIRST_VALUE);
        preferences.put(SECOND_KEY,
                        SECOND_VALUE);

        preferenceStore.putIfAbsent(preferenceScopeResolutionStrategyInfo,
                                    preferences);
        preferenceStore.putIfAbsent(preferenceScopeResolutionStrategyInfo,
                                    preferences);

        verify(storage,
               times(1)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               FIRST_KEY,
                               FIRST_VALUE);
        verify(storage,
               times(1)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               SECOND_KEY,
                               SECOND_VALUE);
    }

    @Test
    public void putIfAbsentMapInDefaultScopeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put(FIRST_KEY,
                        FIRST_VALUE);
        preferences.put(SECOND_KEY,
                        SECOND_VALUE);

        preferenceStore.putIfAbsent(preferences);
        preferenceStore.putIfAbsent(preferences);

        verify(storage,
               times(1)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               FIRST_KEY,
                               FIRST_VALUE);
        verify(storage,
               times(1)).write(preferenceScopeResolutionStrategyInfo.defaultScope(),
                               SECOND_KEY,
                               SECOND_VALUE);
    }

    @Test
    public void getStringValueFromScopeWithoutDefaultValueTest() {
        mockStorageRead(VALUE);

        final String value = preferenceStore.get(allUsersEntireApplication,
                                                 KEY);

        assertEquals(VALUE,
                     value);

        verify(storage).read(allUsersEntireApplication,
                             KEY);
    }

    @Test
    public void getNullValueFromScopeWithoutDefaultValueTest() {
        final String value = preferenceStore.get(allUsersEntireApplication,
                                                 KEY);

        assertNull(value);

        verify(storage).read(allUsersEntireApplication,
                             KEY);
    }

    @Test
    public void getStringValueFromScopeWithDefaultValueTest() {
        mockStorageRead(VALUE);

        final String value = preferenceStore.get(allUsersEntireApplication,
                                                 KEY,
                                                 DEFAULT_VALUE);

        assertEquals(VALUE,
                     value);

        verify(storage).read(allUsersEntireApplication,
                             KEY);
    }

    @Test
    public void getNullValueFromScopeWithDefaultValueTest() {
        final String value = preferenceStore.get(allUsersEntireApplication,
                                                 KEY,
                                                 DEFAULT_VALUE);

        assertEquals(DEFAULT_VALUE,
                     value);

        verify(storage).read(allUsersEntireApplication,
                             KEY);
    }

    @Test
    public void getStringValueFromScopeResolutionStrategyWithoutDefaultValueTest() {
        mockStorageRead(VALUE);

        final String value = preferenceStore.get(preferenceScopeResolutionStrategyInfo,
                                                 KEY);

        assertEquals(VALUE,
                     value);

        verify(storage).read(preferenceScopeResolutionStrategyInfo,
                             KEY);
    }

    @Test
    public void getNullValueFromScopeResolutionStrategyWithoutDefaultValueTest() {
        final String value = preferenceStore.get(preferenceScopeResolutionStrategyInfo,
                                                 KEY);

        assertNull(value);

        verify(storage).read(preferenceScopeResolutionStrategyInfo,
                             KEY);
    }

    @Test
    public void getStringValueFromScopeResolutionStrategyWithDefaultValueTest() {
        mockStorageRead(VALUE);

        final String value = preferenceStore.get(preferenceScopeResolutionStrategyInfo,
                                                 KEY,
                                                 DEFAULT_VALUE);

        assertEquals(VALUE,
                     value);

        verify(storage).read(preferenceScopeResolutionStrategyInfo,
                             KEY);
    }

    @Test
    public void getNullValueFromScopeResolutionStrategyWithDefaultValueTest() {
        final String value = preferenceStore.get(preferenceScopeResolutionStrategyInfo,
                                                 KEY,
                                                 DEFAULT_VALUE);

        assertEquals(DEFAULT_VALUE,
                     value);

        verify(storage).read(preferenceScopeResolutionStrategyInfo,
                             KEY);
    }

    @Test
    public void getStringValueWithoutDefaultValueTest() {
        mockStorageRead(VALUE);

        final String value = preferenceStore.get(KEY);

        assertEquals(VALUE,
                     value);
    }

    @Test
    public void getNullValueWithoutDefaultValueTest() {
        final String value = preferenceStore.get(KEY);

        assertNull(value);

        verify(storage).read(allUsersEntireApplication,
                             KEY);
    }

    @Test
    public void getScopedStringValueFromScopeResolutionStrategyWithoutDefaultValueTest() {
        mockStorageRead(VALUE);

        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped(preferenceScopeResolutionStrategyInfo,
                                                                                    KEY);
        final PreferenceScope scope = preferenceScopeResolutionStrategyInfo.order().get(0);

        assertEquals(VALUE,
                     scopedValue.getValue());
        assertEquals(scope.key(),
                     scopedValue.getScope().key());
        assertEquals(scope.type(),
                     scopedValue.getScope().type());

        verify(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                      KEY);
    }

    @Test
    public void getScopedNullValueFromScopeResolutionStrategyWithoutDefaultValueTest() {
        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped(preferenceScopeResolutionStrategyInfo,
                                                                                    KEY);

        assertNull(scopedValue);

        verify(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                      KEY);
    }

    @Test
    public void getScopedStringValueFromScopeResolutionStrategyWithDefaultValueTest() {
        mockStorageRead(VALUE);

        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped(preferenceScopeResolutionStrategyInfo,
                                                                                    KEY,
                                                                                    DEFAULT_VALUE);
        final PreferenceScope scope = preferenceScopeResolutionStrategyInfo.order().get(0);

        assertEquals(VALUE,
                     scopedValue.getValue());
        assertEquals(scope.key(),
                     scopedValue.getScope().key());
        assertEquals(scope.type(),
                     scopedValue.getScope().type());

        verify(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                      KEY);
    }

    @Test
    public void getScopedNullValueFromScopeResolutionStrategyWithDefaultValueTest() {
        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped(preferenceScopeResolutionStrategyInfo,
                                                                                    KEY,
                                                                                    DEFAULT_VALUE);

        assertEquals(DEFAULT_VALUE,
                     scopedValue.getValue());
        assertNull(scopedValue.getScope());

        verify(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                      KEY);
    }

    @Test
    public void getScopedStringValueFromDefaultScopeResolutionStrategyWithoutDefaultValueTest() {
        mockStorageRead(VALUE);

        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped(KEY);
        final PreferenceScope scope = preferenceScopeResolutionStrategyInfo.order().get(0);

        assertEquals(VALUE,
                     scopedValue.getValue());
        assertEquals(scope.key(),
                     scopedValue.getScope().key());
        assertEquals(scope.type(),
                     scopedValue.getScope().type());

        verify(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                      KEY);
    }

    @Test
    public void getScopedNullValueFromDefaultScopeResolutionStrategyWithoutDefaultValueTest() {
        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped(KEY);

        assertNull(scopedValue);

        verify(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                      KEY);
    }

    @Test
    public void getScopedStringValueFromDefaultScopeResolutionStrategyWithDefaultValueTest() {
        mockStorageRead(VALUE);

        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped(KEY,
                                                                                    DEFAULT_VALUE);
        final PreferenceScope scope = preferenceScopeResolutionStrategyInfo.order().get(0);

        assertEquals(VALUE,
                     scopedValue.getValue());
        assertEquals(scope.key(),
                     scopedValue.getScope().key());
        assertEquals(scope.type(),
                     scopedValue.getScope().type());

        verify(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                      KEY);
    }

    @Test
    public void getScopedNullValueFromDefaultScopeResolutionStrategyWithDefaultValueTest() {
        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped(KEY,
                                                                                    DEFAULT_VALUE);

        assertEquals(DEFAULT_VALUE,
                     scopedValue.getValue());
        assertNull(scopedValue.getScope());

        verify(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                      KEY);
    }

    @Test
    public void searchOnScopeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put(FIRST_KEY,
                        FIRST_VALUE);
        preferences.put(SECOND_KEY,
                        SECOND_VALUE);

        for (Map.Entry<String, String> preference : preferences.entrySet()) {
            doReturn(preference.getValue()).when(storage).read(allUsersEntireApplication,
                                                               preference.getKey());
        }

        final Map<String, Object> returnedPreferences = preferenceStore.search(allUsersEntireApplication,
                                                                               preferences.keySet());

        assertEquals(preferences.size(),
                     returnedPreferences.size());

        for (Map.Entry<String, String> preference : preferences.entrySet()) {
            final String key = preference.getKey();
            final String value = preference.getValue();

            assertTrue(returnedPreferences.containsKey(key));
            assertEquals(value,
                         returnedPreferences.get(key));

            verify(storage).read(allUsersEntireApplication,
                                 key);
        }
    }

    @Test
    public void searchOnScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put(FIRST_KEY,
                        FIRST_VALUE);
        preferences.put(SECOND_KEY,
                        SECOND_VALUE);

        for (Map.Entry<String, String> preference : preferences.entrySet()) {
            doReturn(preference.getValue()).when(storage).read(preferenceScopeResolutionStrategyInfo,
                                                               preference.getKey());
        }

        final Map<String, Object> returnedPreferences = preferenceStore.search(preferenceScopeResolutionStrategyInfo,
                                                                               preferences.keySet());

        assertEquals(preferences.size(),
                     returnedPreferences.size());

        for (Map.Entry<String, String> preference : preferences.entrySet()) {
            final String key = preference.getKey();
            final String value = preference.getValue();

            assertTrue(returnedPreferences.containsKey(key));
            assertEquals(value,
                         returnedPreferences.get(key));

            verify(storage).read(preferenceScopeResolutionStrategyInfo,
                                 key);
        }
    }

    @Test
    public void searchOnDefaultScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put(FIRST_KEY,
                        FIRST_VALUE);
        preferences.put(SECOND_KEY,
                        SECOND_VALUE);

        for (Map.Entry<String, String> preference : preferences.entrySet()) {
            doReturn(preference.getValue()).when(storage).read(preferenceScopeResolutionStrategyInfo,
                                                               preference.getKey());
        }

        final Map<String, Object> returnedPreferences = preferenceStore.search(preferences.keySet());

        assertEquals(preferences.size(),
                     returnedPreferences.size());

        for (Map.Entry<String, String> preference : preferences.entrySet()) {
            final String key = preference.getKey();
            final String value = preference.getValue();

            assertTrue(returnedPreferences.containsKey(key));
            assertEquals(value,
                         returnedPreferences.get(key));

            verify(storage).read(preferenceScopeResolutionStrategyInfo,
                                 key);
        }
    }

    @Test
    public void searchScopedOnScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put(FIRST_KEY,
                        FIRST_VALUE);
        preferences.put(SECOND_KEY,
                        SECOND_VALUE);

        for (Map.Entry<String, String> preference : preferences.entrySet()) {
            doReturn(preference.getValue()).when(storage).read(preferenceScopeResolutionStrategyInfo.order().get(0),
                                                               preference.getKey());
        }

        final Map<String, PreferenceScopedValue<Object>> returnedPreferences = preferenceStore.searchScoped(preferenceScopeResolutionStrategyInfo,
                                                                                                            preferences.keySet());

        assertEquals(preferences.size(),
                     returnedPreferences.size());

        for (Map.Entry<String, String> preference : preferences.entrySet()) {
            final String key = preference.getKey();
            final String value = preference.getValue();

            assertTrue(returnedPreferences.containsKey(key));
            assertEquals(value,
                         returnedPreferences.get(key).getValue());
            assertEquals(preferenceScopeResolutionStrategyInfo.order().get(0).type(),
                         returnedPreferences.get(key).getScope().type());
            assertEquals(preferenceScopeResolutionStrategyInfo.order().get(0).key(),
                         returnedPreferences.get(key).getScope().key());

            verify(storage).read(preferenceScopeResolutionStrategyInfo.order().get(0),
                                 key);
        }
    }

    @Test
    public void searchScopedOnDefaultScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put(FIRST_KEY,
                        FIRST_VALUE);
        preferences.put(SECOND_KEY,
                        SECOND_VALUE);

        for (Map.Entry<String, String> preference : preferences.entrySet()) {
            doReturn(preference.getValue()).when(storage).read(preferenceScopeResolutionStrategyInfo.order().get(0),
                                                               preference.getKey());
        }

        final Map<String, PreferenceScopedValue<Object>> returnedPreferences = preferenceStore.searchScoped(preferences.keySet());

        assertEquals(preferences.size(),
                     returnedPreferences.size());

        for (Map.Entry<String, String> preference : preferences.entrySet()) {
            final String key = preference.getKey();
            final String value = preference.getValue();

            assertTrue(returnedPreferences.containsKey(key));
            assertEquals(value,
                         returnedPreferences.get(key).getValue());
            assertEquals(preferenceScopeResolutionStrategyInfo.order().get(0).type(),
                         returnedPreferences.get(key).getScope().type());
            assertEquals(preferenceScopeResolutionStrategyInfo.order().get(0).key(),
                         returnedPreferences.get(key).getScope().key());

            verify(storage).read(preferenceScopeResolutionStrategyInfo.order().get(0),
                                 key);
        }
    }

    @Test
    public void allPreferencesByScopeTest() {
        final PreferenceScope userScope = userEntireApplicationScope;
        doReturn(FIRST_VALUE).when(storage).read(userScope,
                                                 FIRST_KEY);
        doReturn(SECOND_VALUE).when(storage).read(userScope,
                                                  SECOND_KEY);

        List<String> keys = new ArrayList<>(2);
        keys.add(FIRST_KEY);
        keys.add(SECOND_KEY);
        doReturn(keys).when(storage).allKeys(userScope);

        final Map<String, Object> valueByKey = preferenceStore.all(userScope);

        assertNotNull(valueByKey);
        assertEquals(2,
                     valueByKey.size());

        assertTrue(valueByKey.containsKey(FIRST_KEY));
        assertTrue(valueByKey.containsKey(SECOND_KEY));

        assertEquals(FIRST_VALUE,
                     valueByKey.get(FIRST_KEY));
        assertEquals(SECOND_VALUE,
                     valueByKey.get(SECOND_KEY));
    }

    @Test
    public void allPreferencesByScopeResolutionStrategyTest() {
        doReturn(FIRST_VALUE).when(storage).read(preferenceScopeResolutionStrategyInfo,
                                                 FIRST_KEY);
        doReturn(SECOND_VALUE).when(storage).read(preferenceScopeResolutionStrategyInfo,
                                                  SECOND_KEY);
        doReturn(THIRD_VALUE).when(storage).read(preferenceScopeResolutionStrategyInfo,
                                                 THIRD_KEY);

        List<String> preferenceKeys = new ArrayList<>(3);
        preferenceKeys.add(FIRST_KEY);
        preferenceKeys.add(SECOND_KEY);
        preferenceKeys.add(THIRD_KEY);
        doReturn(preferenceKeys).when(storage).allKeys(preferenceScopeResolutionStrategyInfo.order());

        Map<String, Object> valueByKey = preferenceStore.all(preferenceScopeResolutionStrategyInfo);

        assertNotNull(valueByKey);
        assertEquals(3,
                     valueByKey.size());

        assertTrue(valueByKey.containsKey(FIRST_KEY));
        assertTrue(valueByKey.containsKey(SECOND_KEY));
        assertTrue(valueByKey.containsKey(THIRD_KEY));

        assertEquals(FIRST_VALUE,
                     valueByKey.get(FIRST_KEY));
        assertEquals(SECOND_VALUE,
                     valueByKey.get(SECOND_KEY));
        assertEquals(THIRD_VALUE,
                     valueByKey.get(THIRD_KEY));
    }

    @Test
    public void allPreferencesTest() {
        doReturn(FIRST_VALUE).when(storage).read(preferenceScopeResolutionStrategyInfo,
                                                 FIRST_KEY);
        doReturn(SECOND_VALUE).when(storage).read(preferenceScopeResolutionStrategyInfo,
                                                  SECOND_KEY);
        doReturn(THIRD_VALUE).when(storage).read(preferenceScopeResolutionStrategyInfo,
                                                 THIRD_KEY);

        List<String> preferenceKeys = new ArrayList<>(3);
        preferenceKeys.add(FIRST_KEY);
        preferenceKeys.add(SECOND_KEY);
        preferenceKeys.add(THIRD_KEY);
        doReturn(preferenceKeys).when(storage).allKeys(preferenceScopeResolutionStrategyInfo.order());

        Map<String, Object> valueByKey = preferenceStore.all();

        assertNotNull(valueByKey);
        assertEquals(3,
                     valueByKey.size());

        assertTrue(valueByKey.containsKey(FIRST_KEY));
        assertTrue(valueByKey.containsKey(SECOND_KEY));
        assertTrue(valueByKey.containsKey(THIRD_KEY));

        assertEquals(FIRST_VALUE,
                     valueByKey.get(FIRST_KEY));
        assertEquals(SECOND_VALUE,
                     valueByKey.get(SECOND_KEY));
        assertEquals(THIRD_VALUE,
                     valueByKey.get(THIRD_KEY));
    }

    @Test
    public void allScopedPreferencesByScopeResolutionStrategyTest() {
        doReturn(new PreferenceScopedValue<>(FIRST_VALUE,
                                             allUsersEntireApplication)).when(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                                                                                     FIRST_KEY);
        doReturn(new PreferenceScopedValue<>(SECOND_VALUE,
                                             allUsersEntireApplication)).when(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                                                                                     SECOND_KEY);
        doReturn(new PreferenceScopedValue<>(THIRD_VALUE,
                                             userEntireApplicationScope)).when(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                                                                                      THIRD_KEY);

        List<String> preferenceKeys = new ArrayList<>(3);
        preferenceKeys.add(FIRST_KEY);
        preferenceKeys.add(SECOND_KEY);
        preferenceKeys.add(THIRD_KEY);
        doReturn(preferenceKeys).when(storage).allKeys(preferenceScopeResolutionStrategyInfo.order());

        Map<String, PreferenceScopedValue<Object>> valueByKey = preferenceStore.allScoped(preferenceScopeResolutionStrategyInfo);

        assertNotNull(valueByKey);
        assertEquals(3,
                     valueByKey.size());

        assertTrue(valueByKey.containsKey(FIRST_KEY));
        assertTrue(valueByKey.containsKey(SECOND_KEY));
        assertTrue(valueByKey.containsKey(THIRD_KEY));

        assertEquals(FIRST_VALUE,
                     valueByKey.get(FIRST_KEY).getValue());
        assertEquals(allUsersEntireApplication,
                     valueByKey.get(FIRST_KEY).getScope());

        assertEquals(SECOND_VALUE,
                     valueByKey.get(SECOND_KEY).getValue());
        assertEquals(allUsersEntireApplication,
                     valueByKey.get(SECOND_KEY).getScope());

        assertEquals(THIRD_VALUE,
                     valueByKey.get(THIRD_KEY).getValue());
        assertEquals(userEntireApplicationScope,
                     valueByKey.get(THIRD_KEY).getScope());
    }

    @Test
    public void allScopedPreferencesByDefaultScopeResolutionStrategyTest() {
        doReturn(new PreferenceScopedValue<>(FIRST_VALUE,
                                             allUsersEntireApplication)).when(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                                                                                     FIRST_KEY);
        doReturn(new PreferenceScopedValue<>(SECOND_VALUE,
                                             allUsersEntireApplication)).when(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                                                                                     SECOND_KEY);
        doReturn(new PreferenceScopedValue<>(THIRD_VALUE,
                                             userEntireApplicationScope)).when(storage).readWithScope(preferenceScopeResolutionStrategyInfo,
                                                                                                      THIRD_KEY);

        List<String> preferenceKeys = new ArrayList<>(3);
        preferenceKeys.add(FIRST_KEY);
        preferenceKeys.add(SECOND_KEY);
        preferenceKeys.add(THIRD_KEY);
        doReturn(preferenceKeys).when(storage).allKeys(preferenceScopeResolutionStrategyInfo.order());

        Map<String, PreferenceScopedValue<Object>> valueByKey = preferenceStore.allScoped();

        assertNotNull(valueByKey);
        assertEquals(3,
                     valueByKey.size());

        assertTrue(valueByKey.containsKey(FIRST_KEY));
        assertTrue(valueByKey.containsKey(SECOND_KEY));
        assertTrue(valueByKey.containsKey(THIRD_KEY));

        assertEquals(FIRST_VALUE,
                     valueByKey.get(FIRST_KEY).getValue());
        assertEquals(allUsersEntireApplication,
                     valueByKey.get(FIRST_KEY).getScope());

        assertEquals(SECOND_VALUE,
                     valueByKey.get(SECOND_KEY).getValue());
        assertEquals(allUsersEntireApplication,
                     valueByKey.get(SECOND_KEY).getScope());

        assertEquals(THIRD_VALUE,
                     valueByKey.get(THIRD_KEY).getValue());
        assertEquals(userEntireApplicationScope,
                     valueByKey.get(THIRD_KEY).getScope());
    }

    @Test
    public void removeByScopeTest() {
        preferenceStore.remove(allUsersEntireApplication,
                               KEY);

        verify(storage).delete(allUsersEntireApplication,
                               KEY);
    }

    @Test
    public void removeByScopesTest() {
        preferenceStore.remove(preferenceScopeResolutionStrategyInfo.order(),
                               KEY);

        verify(storage).delete(preferenceScopeResolutionStrategyInfo.order().get(0),
                               KEY);
        verify(storage).delete(preferenceScopeResolutionStrategyInfo.order().get(1),
                               KEY);
    }

    private void mockStorageRead(final String value) {
        doReturn(value).when(storage).read(any(PreferenceScope.class),
                                           anyString());
        doReturn(value).when(storage).read(any(PreferenceScopeResolutionStrategyInfo.class),
                                           anyString());
    }

    private FileSystem mockFileSystem() {
        return fileSystemTestingUtils.getFileSystem();
    }

    private IOService mockIoService(final FileSystem fileSystem) {
        final IOService ioService = spy(fileSystemTestingUtils.getIoService());

        doNothing().when(ioService).startBatch(any(FileSystem.class));
        doNothing().when(ioService).endBatch();
        doReturn(fileSystem).when(ioService).newFileSystem(any(URI.class),
                                                           anyMap());

        return ioService;
    }
}
