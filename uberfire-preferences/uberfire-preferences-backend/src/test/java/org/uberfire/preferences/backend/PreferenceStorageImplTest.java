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
import java.util.Collection;

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
import org.uberfire.preferences.shared.PreferenceScope;
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
import static org.mockito.Mockito.*;

public class PreferenceStorageImplTest {

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
    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();
    private PreferenceScope userEntireApplicationScope, allUsersEntireApplicationScope;

    private PreferenceStorageImpl preferenceStorageServiceBackendImpl;

    private PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo;

    private PreferenceScopeFactoryImpl scopeFactory;

    private PreferenceScopeTypes scopeTypes;

    @Before
    public void setup() throws IOException {
        MappingContextSingleton.get();
        fileSystemTestingUtils.setup();

        final SessionInfo sessionInfo = mockSessionInfo();
        final FileSystem fileSystem = mockFileSystem();
        final IOService ioService = mockIoService(fileSystem);

        ObjectStorage objectStorage = new ObjectStorageImpl(ioService);

        scopeTypes = new DefaultPreferenceScopeTypes(new ServerUsernameProvider(sessionInfo));
        scopeFactory = new PreferenceScopeFactoryImpl(scopeTypes);
        scopeResolutionStrategyInfo = new DefaultPreferenceScopeResolutionStrategy(scopeFactory,
                                                                                   null).getInfo();
        Event<PreferenceUpdatedEvent>  preferenceUpdatedEvent = mock(Event.class);
        preferenceStorageServiceBackendImpl = new PreferenceStorageImpl(ioService,
                                                                        sessionInfo,
                                                                        scopeTypes,
                                                                        scopeFactory,
                                                                        objectStorage,
                                                                        new SpacesAPIImpl(),
                                                                        preferenceUpdatedEvent);
        preferenceStorageServiceBackendImpl.init();

        userEntireApplicationScope = scopeFactory.createScope(userScope,
                                                              entireApplicationScope);
        allUsersEntireApplicationScope = scopeFactory.createScope(allUsersScope,
                                                                  entireApplicationScope);
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void unexistentPreferenceDoesNotExistsInAScopeTest() {
        final PreferenceScope scope = userEntireApplicationScope;
        final String key = "my.preference.key";

        final boolean preferenceExists = preferenceStorageServiceBackendImpl.exists(scope,
                                                                                    key);

        assertFalse(preferenceExists);
    }

    @Test
    public void preferenceExistsInAScopeTest() {
        final PreferenceScope scope = userEntireApplicationScope;
        final String key = "my.preference.key";
        final long value = 23L;

        preferenceStorageServiceBackendImpl.write(scope,
                                                  key,
                                                  value);
        final boolean preferenceExists = preferenceStorageServiceBackendImpl.exists(scope,
                                                                                    key);

        assertTrue(preferenceExists);
    }

    @Test
    public void unexistentPreferenceDoesNotExistUsingScopeResolutionStrategyTest() {
        final String key = "my.preference.key";

        final boolean preferenceExists = preferenceStorageServiceBackendImpl.exists(scopeResolutionStrategyInfo,
                                                                                    key);

        assertFalse(preferenceExists);
    }

    @Test
    public void userPreferenceExistsUsingScopeResolutionStrategyTest() {
        final PreferenceScope scope = userEntireApplicationScope;
        final String key = "my.preference.key";
        final long value = 23L;

        preferenceStorageServiceBackendImpl.write(scope,
                                                  key,
                                                  value);
        final boolean preferenceExists = preferenceStorageServiceBackendImpl.exists(scopeResolutionStrategyInfo,
                                                                                    key);

        assertTrue(preferenceExists);
    }

    @Test
    public void globalPreferenceExistsUsingScopeResolutionStrategyTest() {
        final PreferenceScope scope = allUsersEntireApplicationScope;
        final String key = "my.preference.key";
        final long value = 23L;

        preferenceStorageServiceBackendImpl.write(scope,
                                                  key,
                                                  value);
        final boolean preferenceExists = preferenceStorageServiceBackendImpl.exists(scopeResolutionStrategyInfo,
                                                                                    key);

        assertTrue(preferenceExists);
    }

    @Test
    public void writeReadLongTest() {
        final PreferenceScope scope = userEntireApplicationScope;

        preferenceStorageServiceBackendImpl.write(scope,
                                                  "my.preference.key",
                                                  23L);
        final long value = preferenceStorageServiceBackendImpl.read(scope,
                                                                    "my.preference.key");

        assertEquals(23,
                     value);
    }

    @Test
    public void writeReadStringTest() {
        final PreferenceScope scope = userEntireApplicationScope;

        preferenceStorageServiceBackendImpl.write(scope,
                                                  "my.preference.key",
                                                  "text");
        final String value = preferenceStorageServiceBackendImpl.read(scope,
                                                                      "my.preference.key");

        assertEquals("text",
                     value);
    }

    @Test
    public void writeReadBooleanTest() {
        final PreferenceScope scope = userEntireApplicationScope;

        preferenceStorageServiceBackendImpl.write(scope,
                                                  "my.preference.key.true",
                                                  true);
        preferenceStorageServiceBackendImpl.write(scope,
                                                  "my.preference.key.false",
                                                  false);
        final boolean value1 = preferenceStorageServiceBackendImpl.read(scope,
                                                                        "my.preference.key.true");
        final boolean value2 = preferenceStorageServiceBackendImpl.read(scope,
                                                                        "my.preference.key.false");

        assertEquals(true,
                     value1);
        assertEquals(false,
                     value2);
    }

    @Test
    public void writeReadCustomObjectTest() {
        final PreferenceScope scope = userEntireApplicationScope;
        CustomObject customObject = new CustomObject(61L,
                                                     "some text");

        preferenceStorageServiceBackendImpl.write(scope,
                                                  "my.preference.key",
                                                  customObject);
        final CustomObject value = preferenceStorageServiceBackendImpl.read(scope,
                                                                            "my.preference.key");

        assertEquals(customObject.id,
                     value.id);
        assertEquals(customObject.text,
                     value.text);
    }

    @Test
    public void readNonexistentPreferenceFromSpecificScopeTest() {
        final PreferenceScope scope = userEntireApplicationScope;

        final String value = preferenceStorageServiceBackendImpl.read(scope,
                                                                      "my.nonexistent.preference.key");

        assertNull(value);
    }

    @Test
    public void readNonexistentPreferenceWithResolutionStrategyTest() {
        final String value = preferenceStorageServiceBackendImpl.read(scopeResolutionStrategyInfo,
                                                                      "my.nonexistent.preference.key");

        assertNull(value);
    }

    @Test
    public void writeGlobalAndUserReadUserWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write(userEntireApplicationScope,
                                                  "my.preference.key",
                                                  "user_value");
        preferenceStorageServiceBackendImpl.write(allUsersEntireApplicationScope,
                                                  "my.preference.key",
                                                  "global_value");
        final String value = preferenceStorageServiceBackendImpl.read(scopeResolutionStrategyInfo,
                                                                      "my.preference.key");

        assertEquals("user_value",
                     value);
    }

    @Test
    public void writeGlobalReadGlobalWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write(allUsersEntireApplicationScope,
                                                  "my.preference.key",
                                                  "global_value");
        final String value = preferenceStorageServiceBackendImpl.read(scopeResolutionStrategyInfo,
                                                                      "my.preference.key");

        assertEquals("global_value",
                     value);
    }

    @Test
    public void writeUserReadUserWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write(userEntireApplicationScope,
                                                  "my.preference.key",
                                                  "user_value");
        final String value = preferenceStorageServiceBackendImpl.read(scopeResolutionStrategyInfo,
                                                                      "my.preference.key");

        assertEquals("user_value",
                     value);
    }

    @Test
    public void readFromSpecificScopeTest() {
        preferenceStorageServiceBackendImpl.write(userEntireApplicationScope,
                                                  "my.preference.key",
                                                  "value");
        final String value = preferenceStorageServiceBackendImpl.read(userEntireApplicationScope,
                                                                      "my.preference.key");

        assertEquals("value",
                     value);
    }

    @Test
    public void readWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write(allUsersEntireApplicationScope,
                                                  "my.preference.key",
                                                  "value");
        final String value = preferenceStorageServiceBackendImpl.read(scopeResolutionStrategyInfo,
                                                                      "my.preference.key");

        assertEquals("value",
                     value);
    }

    @Test
    public void writeGlobalAndUserReadWithScopeUserWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write(userEntireApplicationScope,
                                                  "my.preference.key",
                                                  "user_value");
        preferenceStorageServiceBackendImpl.write(allUsersEntireApplicationScope,
                                                  "my.preference.key",
                                                  "global_value");
        final PreferenceScopedValue<String> scopedValue = preferenceStorageServiceBackendImpl.readWithScope(scopeResolutionStrategyInfo,
                                                                                                            "my.preference.key");

        assertEquals("user_value",
                     scopedValue.getValue());
        assertEquals(userEntireApplicationScope.key(),
                     scopedValue.getScope().key());
    }

    @Test
    public void writeGlobalReadWithScopeGlobalWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write(allUsersEntireApplicationScope,
                                                  "my.preference.key",
                                                  "global_value");
        final PreferenceScopedValue<String> scopedValue = preferenceStorageServiceBackendImpl.readWithScope(scopeResolutionStrategyInfo,
                                                                                                            "my.preference.key");

        assertEquals("global_value",
                     scopedValue.getValue());
        assertEquals(allUsersEntireApplicationScope.key(),
                     scopedValue.getScope().key());
    }

    @Test
    public void writeUserReadWithScopeUserUsingResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write(userEntireApplicationScope,
                                                  "my.preference.key",
                                                  "user_value");
        final PreferenceScopedValue<String> scopedValue = preferenceStorageServiceBackendImpl.readWithScope(scopeResolutionStrategyInfo,
                                                                                                            "my.preference.key");

        assertEquals("user_value",
                     scopedValue.getValue());
        assertEquals(userEntireApplicationScope.key(),
                     scopedValue.getScope().key());
    }

    @Test
    public void deleteFromUserTest() {
        String value;

        // create preference defined for global and user scopes
        preferenceStorageServiceBackendImpl.write(userEntireApplicationScope,
                                                  "my.preference.key",
                                                  "user_value");
        preferenceStorageServiceBackendImpl.write(allUsersEntireApplicationScope,
                                                  "my.preference.key",
                                                  "global_value");
        value = preferenceStorageServiceBackendImpl.read(scopeResolutionStrategyInfo,
                                                         "my.preference.key");
        assertEquals("user_value",
                     value);

        // delete preference from user scope
        preferenceStorageServiceBackendImpl.delete(userEntireApplicationScope,
                                                   "my.preference.key");
        value = preferenceStorageServiceBackendImpl.read(scopeResolutionStrategyInfo,
                                                         "my.preference.key");
        assertEquals("global_value",
                     value);

        // delete preference from global scope
        preferenceStorageServiceBackendImpl.delete(allUsersEntireApplicationScope,
                                                   "my.preference.key");
        value = preferenceStorageServiceBackendImpl.read(scopeResolutionStrategyInfo,
                                                         "my.preference.key");
        assertNull(value);
    }

    @Test
    public void allKeysWithKeysTest() {
        // global preferences
        preferenceStorageServiceBackendImpl.write(allUsersEntireApplicationScope,
                                                  "my.first.global.preference.key",
                                                  "global_value1");
        preferenceStorageServiceBackendImpl.write(allUsersEntireApplicationScope,
                                                  "my.second.global.preference.key",
                                                  "global_value2");
        preferenceStorageServiceBackendImpl.write(allUsersEntireApplicationScope,
                                                  "my.third.global.preference.key",
                                                  "global_value3");

        // user preferences
        preferenceStorageServiceBackendImpl.write(userEntireApplicationScope,
                                                  "my.first.user.preference.key",
                                                  "user_value1");
        preferenceStorageServiceBackendImpl.write(userEntireApplicationScope,
                                                  "my.second.user.preference.key",
                                                  "user_value2");

        final Collection<String> globalKeys = preferenceStorageServiceBackendImpl.allKeys(allUsersEntireApplicationScope);
        final Collection<String> userKeys = preferenceStorageServiceBackendImpl.allKeys(userEntireApplicationScope);

        assertNotNull(globalKeys);
        assertEquals(3,
                     globalKeys.size());
        assertTrue(globalKeys.contains("my.first.global.preference.key"));
        assertTrue(globalKeys.contains("my.second.global.preference.key"));
        assertTrue(globalKeys.contains("my.third.global.preference.key"));

        assertNotNull(userKeys);
        assertEquals(2,
                     userKeys.size());
        assertTrue(userKeys.contains("my.first.user.preference.key"));
        assertTrue(userKeys.contains("my.second.user.preference.key"));
    }

    @Test
    public void allKeysWithNoKeysTest() {
        final Collection<String> keys = preferenceStorageServiceBackendImpl.allKeys(allUsersEntireApplicationScope);

        assertNotNull(keys);
        assertEquals("There should not exist any keys.",
                     0,
                     keys.size());
    }

    @Test
    public void buildScopePathForAllUsersEntireApplicationScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopePath(allUsersEntireApplicationScope);

        assertEquals("/config/all-users/all-users/entire-application/entire-application/",
                     path);
    }

    @Test
    public void buildScopePathForUserScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopePath(userEntireApplicationScope);

        assertEquals("/config/user/my-user/entire-application/entire-application/",
                     path);
    }

    @Test
    public void buildStoragePathForUserScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopedPreferencePath(userEntireApplicationScope,
                                                                                          "my.preference.key");

        assertEquals("/config/user/my-user/entire-application/entire-application/my.preference.key.preferences",
                     path);
    }

    @Test
    public void buildStoragePathForGlobalScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopedPreferencePath(allUsersEntireApplicationScope,
                                                                                          "my.preference.key");

        assertEquals("/config/all-users/all-users/entire-application/entire-application/my.preference.key.preferences",
                     path);
    }

    private SessionInfo mockSessionInfo() {
        return new SessionInfoMock(userScopeKey);
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
