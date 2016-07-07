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

package org.uberfire.ext.preferences.backend;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.ext.preferences.shared.PreferenceScopeTypes;
import org.uberfire.ext.preferences.shared.PreferenceScopedValue;
import org.uberfire.ext.preferences.shared.impl.DefaultScopes;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class PreferenceStorageImplTest {

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    private PreferenceStorageImpl preferenceStorageServiceBackendImpl;

    private PreferenceScopeResolutionStrategy preferenceScopeResolutionStrategy;

    private PreferenceScopeBuilderImpl scopeBuilder;

    private PreferenceScopeTypes scopeTypes;

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();

        final SessionInfo sessionInfo = mockSessionInfo();
        final FileSystem fileSystem = mockFileSystem();
        final IOService ioService = mockIoService( fileSystem );

        scopeTypes = new DefaultPreferenceScopeTypes( sessionInfo );
        scopeBuilder = new PreferenceScopeBuilderImpl( scopeTypes );
        preferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy( scopeBuilder );
        preferenceStorageServiceBackendImpl = new PreferenceStorageImpl( ioService,
                                                                         sessionInfo,
                                                                         scopeTypes );
        preferenceStorageServiceBackendImpl.init();
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void unexistentPreferenceDoesNotExistsInAScopeTest() {
        final PreferenceScope scope = scopeBuilder.build( DefaultScopes.USER.type() );
        final String key = "my.preference.key";

        final boolean preferenceExists = preferenceStorageServiceBackendImpl.exists( scope,
                                                                                     key );

        assertFalse( preferenceExists );
    }

    @Test
    public void preferenceExistsInAScopeTest() {
        final PreferenceScope scope = scopeBuilder.build( DefaultScopes.USER.type() );
        final String key = "my.preference.key";
        final long value = 23L;

        preferenceStorageServiceBackendImpl.write( scope,
                                                   key,
                                                   value );
        final boolean preferenceExists = preferenceStorageServiceBackendImpl.exists( scope,
                                                                                     key );

        assertTrue( preferenceExists );
    }

    @Test
    public void unexistentPreferenceDoesNotExistUsingScopeResolutionStrategyTest() {
        final String key = "my.preference.key";

        final boolean preferenceExists = preferenceStorageServiceBackendImpl.exists( preferenceScopeResolutionStrategy,
                                                                                     key );

        assertFalse( preferenceExists );
    }

    @Test
    public void userPreferenceExistsUsingScopeResolutionStrategyTest() {
        final PreferenceScope scope = scopeBuilder.build( DefaultScopes.USER.type() );
        final String key = "my.preference.key";
        final long value = 23L;

        preferenceStorageServiceBackendImpl.write( scope,
                                                   key,
                                                   value );
        final boolean preferenceExists = preferenceStorageServiceBackendImpl.exists( preferenceScopeResolutionStrategy,
                                                                                     key );

        assertTrue( preferenceExists );
    }

    @Test
    public void globalPreferenceExistsUsingScopeResolutionStrategyTest() {
        final PreferenceScope scope = scopeBuilder.build( DefaultScopes.GLOBAL.type() );
        final String key = "my.preference.key";
        final long value = 23L;

        preferenceStorageServiceBackendImpl.write( scope,
                                                   key,
                                                   value );
        final boolean preferenceExists = preferenceStorageServiceBackendImpl.exists( preferenceScopeResolutionStrategy,
                                                                                     key );

        assertTrue( preferenceExists );
    }

    @Test
    public void writeReadLongTest() {
        final PreferenceScope scope = scopeBuilder.build( DefaultScopes.USER.type() );

        preferenceStorageServiceBackendImpl.write( scope,
                                                   "my.preference.key",
                                                   23L );
        final long value = preferenceStorageServiceBackendImpl.read( scope,
                                                                     "my.preference.key" );

        assertEquals( 23, value );
    }

    @Test
    public void writeReadStringTest() {
        final PreferenceScope scope = scopeBuilder.build( DefaultScopes.USER.type() );

        preferenceStorageServiceBackendImpl.write( scope,
                                                   "my.preference.key",
                                                   "text" );
        final String value = preferenceStorageServiceBackendImpl.read( scope,
                                                                       "my.preference.key" );

        assertEquals( "text", value );
    }

    @Test
    public void writeReadBooleanTest() {
        final PreferenceScope scope = scopeBuilder.build( DefaultScopes.USER.type() );

        preferenceStorageServiceBackendImpl.write( scope,
                                                   "my.preference.key.true",
                                                   true );
        preferenceStorageServiceBackendImpl.write( scope,
                                                   "my.preference.key.false",
                                                   false );
        final boolean value1 = preferenceStorageServiceBackendImpl.read( scope,
                                                                         "my.preference.key.true" );
        final boolean value2 = preferenceStorageServiceBackendImpl.read( scope,
                                                                         "my.preference.key.false" );

        assertEquals( true, value1 );
        assertEquals( false, value2 );
    }

    @Test
    public void writeReadCustomObjectTest() {
        final PreferenceScope scope = scopeBuilder.build( DefaultScopes.USER.type() );
        CustomObject customObject = new CustomObject( 61L, "some text" );

        preferenceStorageServiceBackendImpl.write( scope,
                                                   "my.preference.key",
                                                   customObject );
        final CustomObject value = preferenceStorageServiceBackendImpl.read( scope,
                                                                             "my.preference.key" );

        assertEquals( customObject.id, value.id );
        assertEquals( customObject.text, value.text );
    }

    @Test
    public void readNonexistentPreferenceFromSpecificScopeTest() {
        final PreferenceScope scope = scopeBuilder.build( DefaultScopes.USER.type() );

        final String value = preferenceStorageServiceBackendImpl.read( scope,
                                                                       "my.nonexistent.preference.key" );

        assertNull( value );
    }

    @Test
    public void readNonexistentPreferenceWithResolutionStrategyTest() {
        final String value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                                       "my.nonexistent.preference.key" );

        assertNull( value );
    }

    @Test
    public void writeGlobalAndUserReadUserWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.USER.type() ),
                                                   "my.preference.key",
                                                   "user_value" );
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.GLOBAL.type() ),
                                                   "my.preference.key",
                                                   "global_value" );
        final String value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                                       "my.preference.key" );

        assertEquals( "user_value", value );
    }

    @Test
    public void writeGlobalReadGlobalWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.GLOBAL.type() ),
                                                   "my.preference.key",
                                                   "global_value" );
        final String value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                                       "my.preference.key" );

        assertEquals( "global_value", value );
    }

    @Test
    public void writeUserReadUserWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.USER.type() ),
                                                   "my.preference.key",
                                                   "user_value" );
        final String value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                                       "my.preference.key" );

        assertEquals( "user_value", value );
    }

    @Test
    public void readFromSpecificScopeTest() {
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.USER.type() ),
                                                   "my.preference.key",
                                                   "value" );
        final String value = preferenceStorageServiceBackendImpl.read( scopeBuilder.build( DefaultScopes.USER.type() ),
                                                                      "my.preference.key" );

        assertEquals( "value", value );
    }

    @Test
    public void readWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.GLOBAL.type() ),
                                                   "my.preference.key",
                                                   "value" );
        final String value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                                      "my.preference.key" );

        assertEquals( "value", value );
    }

    @Test
    public void writeGlobalAndUserReadWithScopeUserWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.USER.type() ),
                                                   "my.preference.key",
                                                   "user_value" );
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.GLOBAL.type() ),
                                                   "my.preference.key",
                                                   "global_value" );
        final PreferenceScopedValue<String> scopedValue = preferenceStorageServiceBackendImpl.readWithScope( preferenceScopeResolutionStrategy,
                                                                                                             "my.preference.key" );

        assertEquals( "user_value", scopedValue.getValue() );
        assertEquals( scopeBuilder.build( DefaultScopes.USER.type() ).key(), scopedValue.getScope().key() );
    }

    @Test
    public void writeGlobalReadWithScopeGlobalWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.GLOBAL.type() ),
                                                   "my.preference.key",
                                                   "global_value" );
        final PreferenceScopedValue<String> scopedValue = preferenceStorageServiceBackendImpl.readWithScope( preferenceScopeResolutionStrategy,
                                                                                                             "my.preference.key" );

        assertEquals( "global_value", scopedValue.getValue() );
        assertEquals( scopeBuilder.build( DefaultScopes.GLOBAL.type() ).key(), scopedValue.getScope().key() );
    }

    @Test
    public void writeUserReadWithScopeUserUsingResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.USER.type() ),
                                                   "my.preference.key",
                                                   "user_value" );
        final PreferenceScopedValue<String> scopedValue = preferenceStorageServiceBackendImpl.readWithScope( preferenceScopeResolutionStrategy,
                                                                                                             "my.preference.key" );

        assertEquals( "user_value", scopedValue.getValue() );
        assertEquals( scopeBuilder.build( DefaultScopes.USER.type() ).key(), scopedValue.getScope().key() );
    }

    @Test
    public void deleteFromUserTest() {
        String value;

        // create preference defined for global and user scopes
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.USER.type() ),
                                                   "my.preference.key",
                                                   "user_value" );
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.GLOBAL.type() ),
                                                   "my.preference.key",
                                                   "global_value" );
        value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                          "my.preference.key" );
        assertEquals( "user_value", value );

        // delete preference from user scope
        preferenceStorageServiceBackendImpl.delete( scopeBuilder.build( DefaultScopes.USER.type() ),
                                                    "my.preference.key" );
        value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                          "my.preference.key" );
        assertEquals( "global_value", value );

        // delete preference from global scope
        preferenceStorageServiceBackendImpl.delete( scopeBuilder.build( DefaultScopes.GLOBAL.type() ),
                                                    "my.preference.key" );
        value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                          "my.preference.key" );
        assertNull( value );
    }

    @Test
    public void allKeysWithKeysTest() {
        // global preferences
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.GLOBAL.type() ),
                                                   "my.first.global.preference.key",
                                                   "global_value1" );
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.GLOBAL.type() ),
                                                   "my.second.global.preference.key",
                                                   "global_value2" );
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.GLOBAL.type() ),
                                                   "my.third.global.preference.key",
                                                   "global_value3" );

        // user preferences
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.USER.type() ),
                                                   "my.first.user.preference.key",
                                                   "user_value1" );
        preferenceStorageServiceBackendImpl.write( scopeBuilder.build( DefaultScopes.USER.type() ),
                                                   "my.second.user.preference.key",
                                                   "user_value2" );

        final Collection<String> globalKeys = preferenceStorageServiceBackendImpl.allKeys( scopeBuilder.build( DefaultScopes.GLOBAL.type() ) );
        final Collection<String> userKeys = preferenceStorageServiceBackendImpl.allKeys( scopeBuilder.build( DefaultScopes.USER.type() ) );

        assertNotNull( globalKeys );
        assertEquals( 3, globalKeys.size() );
        assertTrue( globalKeys.contains( "my.first.global.preference.key" ) );
        assertTrue( globalKeys.contains( "my.second.global.preference.key" ) );
        assertTrue( globalKeys.contains( "my.third.global.preference.key" ) );

        assertNotNull( userKeys );
        assertEquals( 2, userKeys.size() );
        assertTrue( userKeys.contains( "my.first.user.preference.key" ) );
        assertTrue( userKeys.contains( "my.second.user.preference.key" ) );
    }

    @Test
    public void allKeysWithNoKeysTest() {
        final Collection<String> keys = preferenceStorageServiceBackendImpl.allKeys( scopeBuilder.build( DefaultScopes.GLOBAL.type() ) );

        assertNotNull( keys );
        assertEquals( "There should not exist any keys.", 0, keys.size() );
    }

    @Test
    public void buildScopePathForGlobalScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopePath( scopeBuilder.build( DefaultScopes.GLOBAL.type() ) );

        assertEquals( "/config/global/global", path );
    }

    @Test
    public void buildScopePathForUserScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopePath( scopeBuilder.build( DefaultScopes.USER.type() ) );

        assertEquals( "/config/user/myuser", path );
    }

    @Test
    public void buildStoragePathForUserScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopedPreferencePath( scopeBuilder.build( DefaultScopes.USER.type() ),
                                                                                           "my.preference.key" );

        assertEquals( "/config/user/myuser/my.preference.key.preferences", path );
    }

    @Test
    public void buildStoragePathForGlobalScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopedPreferencePath( scopeBuilder.build( DefaultScopes.GLOBAL.type() ),
                                                                                           "my.preference.key" );

        assertEquals( "/config/global/global/my.preference.key.preferences", path );
    }

    private SessionInfo mockSessionInfo() {
        return new SessionInfoMock( "myuser" );
    }

    private FileSystem mockFileSystem() {
        return fileSystemTestingUtils.getFileSystem();
    }

    private IOService mockIoService( final FileSystem fileSystem ) {
        final IOService ioService = spy( fileSystemTestingUtils.getIoService() );

        doNothing().when( ioService ).startBatch( any( FileSystem.class ) );
        doNothing().when( ioService ).endBatch();
        doReturn( fileSystem ).when( ioService ).newFileSystem( any( URI.class ), anyMap() );

        return ioService;
    }

}
