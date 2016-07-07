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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.PreferenceScopeBuilder;
import org.uberfire.ext.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.ext.preferences.shared.PreferenceScopeTypes;
import org.uberfire.ext.preferences.shared.PreferenceScopedValue;
import org.uberfire.ext.preferences.shared.impl.DefaultScopes;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class PreferenceStoreImplTest {

    private static final String USER = "myuser";

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

    private ParameterizedCommand<String> callback;

    private PreferenceScopeTypes scopeTypes;

    private PreferenceScopeBuilder scopeBuilder;

    private PreferenceStorageImpl storage;

    private PreferenceScopeResolutionStrategy preferenceScopeResolutionStrategy;

    private PreferenceStoreImpl preferenceStore;

    private PreferenceScope globalScope;

    private PreferenceScope userScope;

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();

        callback = (ParameterizedCommand<String>) mock( ParameterizedCommand.class );

        final SessionInfo sessionInfo = new SessionInfoMock( USER );
        final FileSystem fileSystem = mockFileSystem();
        final IOService ioService = mockIoService( fileSystem );

        scopeTypes = new DefaultPreferenceScopeTypes( sessionInfo );
        scopeBuilder = new PreferenceScopeBuilderImpl( scopeTypes );
        preferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy( scopeBuilder );
        storage = spy( new PreferenceStorageImpl( ioService,
                                                  sessionInfo,
                                                  scopeTypes ) );
        storage.init();

        preferenceStore = spy( new PreferenceStoreImpl( storage,
                                                        preferenceScopeResolutionStrategy,
                                                        scopeBuilder ) );

        globalScope = scopeBuilder.build( DefaultScopes.GLOBAL.type() );
        userScope = scopeBuilder.build( DefaultScopes.USER.type() );
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void putInScopeTest() {
        preferenceStore.put( globalScope, KEY, VALUE );
        preferenceStore.put( globalScope, KEY, VALUE );

        verify( storage, times( 2 ) ).write( globalScope, KEY, VALUE );
    }

    @Test
    public void putInDefaultScopeOfAScopeResolutionStrategyTest() {
        preferenceStore.put( preferenceScopeResolutionStrategy, KEY, VALUE );
        preferenceStore.put( preferenceScopeResolutionStrategy, KEY, VALUE );

        verify( storage, times( 2 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), KEY, VALUE );
    }

    @Test
    public void putInScopeTypeTest() {
        preferenceStore.put( DefaultScopes.GLOBAL.type(), KEY, VALUE );
        preferenceStore.put( DefaultScopes.GLOBAL.type(), KEY, VALUE );

        verify( storage, times( 2 ) ).write( globalScope, KEY, VALUE );
    }

    @Test
    public void putInDefaultScopeTest() {
        preferenceStore.put( KEY, VALUE );
        preferenceStore.put( KEY, VALUE );

        verify( storage, times( 2 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), KEY, VALUE );
    }

    @Test
    public void putMapInScopeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        preferenceStore.put( globalScope, preferences );
        preferenceStore.put( globalScope, preferences );

        verify( storage, times( 2 ) ).write( globalScope, FIRST_KEY, FIRST_VALUE );
        verify( storage, times( 2 ) ).write( globalScope, SECOND_KEY, SECOND_VALUE );
    }

    @Test
    public void putMapInDefaultScopeOfAScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        preferenceStore.put( preferenceScopeResolutionStrategy, preferences );
        preferenceStore.put( preferenceScopeResolutionStrategy, preferences );

        verify( storage, times( 2 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), FIRST_KEY, FIRST_VALUE );
        verify( storage, times( 2 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), SECOND_KEY, SECOND_VALUE );
    }

    @Test
    public void putMapInScopeTypeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        preferenceStore.put( DefaultScopes.GLOBAL.type(), preferences );
        preferenceStore.put( DefaultScopes.GLOBAL.type(), preferences );

        verify( storage, times( 2 ) ).write( globalScope, FIRST_KEY, FIRST_VALUE );
        verify( storage, times( 2 ) ).write( globalScope, SECOND_KEY, SECOND_VALUE );
    }

    @Test
    public void putMapInDefaultScopeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        preferenceStore.put( preferences );
        preferenceStore.put( preferences );

        verify( storage, times( 2 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), FIRST_KEY, FIRST_VALUE );
        verify( storage, times( 2 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), SECOND_KEY, SECOND_VALUE );
    }

    @Test
    public void putIfAbsentInScopeTest() {
        preferenceStore.putIfAbsent( globalScope, KEY, VALUE );
        preferenceStore.putIfAbsent( globalScope, KEY, VALUE );

        verify( storage, times( 1 ) ).write( globalScope, KEY, VALUE );
    }

    @Test
    public void putIfAbsentInDefaultScopeOfAScopeResolutionStrategyTest() {
        preferenceStore.putIfAbsent( preferenceScopeResolutionStrategy, KEY, VALUE );
        preferenceStore.putIfAbsent( preferenceScopeResolutionStrategy, KEY, VALUE );

        verify( storage, times( 1 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), KEY, VALUE );
    }

    @Test
    public void putIfAbsentInScopeTypeTest() {
        preferenceStore.putIfAbsent( DefaultScopes.GLOBAL.type(), KEY, VALUE );
        preferenceStore.putIfAbsent( DefaultScopes.GLOBAL.type(), KEY, VALUE );

        verify( storage, times( 1 ) ).write( globalScope, KEY, VALUE );
    }

    @Test
    public void putIfAbsentInDefaultScopeTest() {
        preferenceStore.putIfAbsent( KEY, VALUE );
        preferenceStore.putIfAbsent( KEY, VALUE );

        verify( storage, times( 1 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), KEY, VALUE );
    }

    @Test
    public void putIfAbsentMapInScopeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        preferenceStore.putIfAbsent( globalScope, preferences );
        preferenceStore.putIfAbsent( globalScope, preferences );

        verify( storage, times( 1 ) ).write( globalScope, FIRST_KEY, FIRST_VALUE );
        verify( storage, times( 1 ) ).write( globalScope, SECOND_KEY, SECOND_VALUE );
    }

    @Test
    public void putIfAbsentMapInDefaultScopeOfAScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        preferenceStore.putIfAbsent( preferenceScopeResolutionStrategy, preferences );
        preferenceStore.putIfAbsent( preferenceScopeResolutionStrategy, preferences );

        verify( storage, times( 1 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), FIRST_KEY, FIRST_VALUE );
        verify( storage, times( 1 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), SECOND_KEY, SECOND_VALUE );
    }

    @Test
    public void putIfAbsentMapInScopeTypeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        preferenceStore.putIfAbsent( DefaultScopes.GLOBAL.type(), preferences );
        preferenceStore.putIfAbsent( DefaultScopes.GLOBAL.type(), preferences );

        verify( storage, times( 1 ) ).write( scopeBuilder.build( DefaultScopes.GLOBAL.type() ), FIRST_KEY, FIRST_VALUE );
        verify( storage, times( 1 ) ).write( scopeBuilder.build( DefaultScopes.GLOBAL.type() ), SECOND_KEY, SECOND_VALUE );
    }

    @Test
    public void putIfAbsentMapInDefaultScopeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        preferenceStore.putIfAbsent( preferences );
        preferenceStore.putIfAbsent( preferences );

        verify( storage, times( 1 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), FIRST_KEY, FIRST_VALUE );
        verify( storage, times( 1 ) ).write( preferenceScopeResolutionStrategy.defaultScope(), SECOND_KEY, SECOND_VALUE );
    }

    @Test
    public void getStringValueFromScopeWithoutDefaultValueTest() {
        mockStorageRead( VALUE );

        final String value = preferenceStore.get( globalScope, KEY );

        assertEquals( VALUE, value );

        verify( storage ).read( globalScope, KEY );
    }

    @Test
    public void getNullValueFromScopeWithoutDefaultValueTest() {
        final String value = preferenceStore.get( globalScope, KEY );

        assertNull( value );

        verify( storage ).read( globalScope, KEY );
    }

    @Test
    public void getStringValueFromScopeWithDefaultValueTest() {
        mockStorageRead( VALUE );

        final String value = preferenceStore.get( globalScope, KEY, DEFAULT_VALUE );

        assertEquals( VALUE, value );

        verify( storage ).read( globalScope, KEY );
    }

    @Test
    public void getNullValueFromScopeWithDefaultValueTest() {
        final String value = preferenceStore.get( globalScope, KEY, DEFAULT_VALUE );

        assertEquals( DEFAULT_VALUE, value );

        verify( storage ).read( globalScope, KEY );
    }

    @Test
    public void getStringValueFromScopeResolutionStrategyWithoutDefaultValueTest() {
        mockStorageRead( VALUE );

        final String value = preferenceStore.get( preferenceScopeResolutionStrategy, KEY );

        assertEquals( VALUE, value );

        verify( storage ).read( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void getNullValueFromScopeResolutionStrategyWithoutDefaultValueTest() {
        final String value = preferenceStore.get( preferenceScopeResolutionStrategy, KEY );

        assertNull( value );

        verify( storage ).read( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void getStringValueFromScopeResolutionStrategyWithDefaultValueTest() {
        mockStorageRead( VALUE );

        final String value = preferenceStore.get( preferenceScopeResolutionStrategy, KEY, DEFAULT_VALUE );

        assertEquals( VALUE, value );

        verify( storage ).read( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void getNullValueFromScopeResolutionStrategyWithDefaultValueTest() {
        final String value = preferenceStore.get( preferenceScopeResolutionStrategy, KEY, DEFAULT_VALUE );

        assertEquals( DEFAULT_VALUE, value );

        verify( storage ).read( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void getStringValueFromScopeTypeWithoutDefaultValueTest() {
        mockStorageRead( VALUE );

        final String value = preferenceStore.get( DefaultScopes.GLOBAL.type(), KEY );

        assertEquals( VALUE, value );

        verify( storage ).read( globalScope, KEY );
    }

    @Test
    public void getNullValueFromScopeTypeWithoutDefaultValueTest() {
        final String value = preferenceStore.get( DefaultScopes.GLOBAL.type(), KEY );

        assertNull( value );

        verify( storage ).read( globalScope, KEY );
    }

    @Test
    public void getStringValueFromScopeTypeWithDefaultValueTest() {
        mockStorageRead( VALUE );

        final String value = preferenceStore.get( DefaultScopes.GLOBAL.type(), KEY, DEFAULT_VALUE );

        assertEquals( VALUE, value );

        verify( storage ).read( globalScope, KEY );
    }

    @Test
    public void getNullValueFromScopeTypeWithDefaultValueTest() {
        final String value = preferenceStore.get( DefaultScopes.GLOBAL.type(), KEY, DEFAULT_VALUE );

        assertEquals( DEFAULT_VALUE, value );

        verify( storage ).read( globalScope, KEY );
    }

    @Test
    public void getStringValueWithoutDefaultValueTest() {
        mockStorageRead( VALUE );

        final String value = preferenceStore.get( KEY );

        assertEquals( VALUE, value );
    }

    @Test
    public void getNullValueWithoutDefaultValueTest() {
        final String value = preferenceStore.get( KEY );

        assertNull( value );

        verify( storage ).read( globalScope, KEY );
    }

    @Test
    public void getScopedStringValueFromScopeResolutionStrategyWithoutDefaultValueTest() {
        mockStorageRead( VALUE );

        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped( preferenceScopeResolutionStrategy,
                                                                                     KEY );
        final PreferenceScope scope = preferenceScopeResolutionStrategy.order().get( 0 );

        assertEquals( VALUE, scopedValue.getValue() );
        assertEquals( scope.key(), scopedValue.getScope().key() );
        assertEquals( scope.type(), scopedValue.getScope().type() );

        verify( storage ).readWithScope( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void getScopedNullValueFromScopeResolutionStrategyWithoutDefaultValueTest() {
        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped( preferenceScopeResolutionStrategy,
                                                                                     KEY );

        assertNull( scopedValue );

        verify( storage ).readWithScope( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void getScopedStringValueFromScopeResolutionStrategyWithDefaultValueTest() {
        mockStorageRead( VALUE );

        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped( preferenceScopeResolutionStrategy,
                                                                                     KEY,
                                                                                     DEFAULT_VALUE );
        final PreferenceScope scope = preferenceScopeResolutionStrategy.order().get( 0 );

        assertEquals( VALUE, scopedValue.getValue() );
        assertEquals( scope.key(), scopedValue.getScope().key() );
        assertEquals( scope.type(), scopedValue.getScope().type() );

        verify( storage ).readWithScope( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void getScopedNullValueFromScopeResolutionStrategyWithDefaultValueTest() {
        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped( preferenceScopeResolutionStrategy,
                                                                                     KEY,
                                                                                     DEFAULT_VALUE );

        assertEquals( DEFAULT_VALUE, scopedValue.getValue() );
        assertNull( scopedValue.getScope() );

        verify( storage ).readWithScope( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void getScopedStringValueFromDefaultScopeResolutionStrategyWithoutDefaultValueTest() {
        mockStorageRead( VALUE );

        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped( KEY );
        final PreferenceScope scope = preferenceScopeResolutionStrategy.order().get( 0 );

        assertEquals( VALUE, scopedValue.getValue() );
        assertEquals( scope.key(), scopedValue.getScope().key() );
        assertEquals( scope.type(), scopedValue.getScope().type() );

        verify( storage ).readWithScope( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void getScopedNullValueFromDefaultScopeResolutionStrategyWithoutDefaultValueTest() {
        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped( KEY );

        assertNull( scopedValue );

        verify( storage ).readWithScope( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void getScopedStringValueFromDefaultScopeResolutionStrategyWithDefaultValueTest() {
        mockStorageRead( VALUE );

        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped( KEY, DEFAULT_VALUE );
        final PreferenceScope scope = preferenceScopeResolutionStrategy.order().get( 0 );

        assertEquals( VALUE, scopedValue.getValue() );
        assertEquals( scope.key(), scopedValue.getScope().key() );
        assertEquals( scope.type(), scopedValue.getScope().type() );

        verify( storage ).readWithScope( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void getScopedNullValueFromDefaultScopeResolutionStrategyWithDefaultValueTest() {
        final PreferenceScopedValue<String> scopedValue = preferenceStore.getScoped( KEY, DEFAULT_VALUE );

        assertEquals( DEFAULT_VALUE, scopedValue.getValue() );
        assertNull( scopedValue.getScope() );

        verify( storage ).readWithScope( preferenceScopeResolutionStrategy, KEY );
    }

    @Test
    public void searchOnScopeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            doReturn( preference.getValue() ).when( storage ).read( globalScope, preference.getKey() );
        }

        final Map<String, Object> returnedPreferences = preferenceStore.search( globalScope, preferences.keySet() );

        assertEquals( preferences.size(), returnedPreferences.size() );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            final String key = preference.getKey();
            final String value = preference.getValue();

            assertTrue( returnedPreferences.containsKey( key ) );
            assertEquals( value, returnedPreferences.get( key ) );

            verify( storage ).read( globalScope, key );
        }
    }

    @Test
    public void searchOnScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            doReturn( preference.getValue() ).when( storage ).read( preferenceScopeResolutionStrategy, preference.getKey() );
        }

        final Map<String, Object> returnedPreferences = preferenceStore.search( preferenceScopeResolutionStrategy, preferences.keySet() );

        assertEquals( preferences.size(), returnedPreferences.size() );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            final String key = preference.getKey();
            final String value = preference.getValue();

            assertTrue( returnedPreferences.containsKey( key ) );
            assertEquals( value, returnedPreferences.get( key ) );

            verify( storage ).read( preferenceScopeResolutionStrategy, key );
        }
    }

    @Test
    public void searchOnScopeTypeTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            doReturn( preference.getValue() ).when( storage ).read( globalScope, preference.getKey() );
        }

        final Map<String, Object> returnedPreferences = preferenceStore.search( DefaultScopes.GLOBAL.type(), preferences.keySet() );

        assertEquals( preferences.size(), returnedPreferences.size() );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            final String key = preference.getKey();
            final String value = preference.getValue();

            assertTrue( returnedPreferences.containsKey( key ) );
            assertEquals( value, returnedPreferences.get( key ) );

            verify( storage ).read( globalScope, key );
        }
    }

    @Test
    public void searchOnDefaultScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            doReturn( preference.getValue() ).when( storage ).read( preferenceScopeResolutionStrategy, preference.getKey() );
        }

        final Map<String, Object> returnedPreferences = preferenceStore.search( preferences.keySet() );

        assertEquals( preferences.size(), returnedPreferences.size() );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            final String key = preference.getKey();
            final String value = preference.getValue();

            assertTrue( returnedPreferences.containsKey( key ) );
            assertEquals( value, returnedPreferences.get( key ) );

            verify( storage ).read( preferenceScopeResolutionStrategy, key );
        }
    }

    @Test
    public void searchScopedOnScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            doReturn( preference.getValue() ).when( storage ).read( preferenceScopeResolutionStrategy.order().get( 0 ), preference.getKey() );
        }

        final Map<String, PreferenceScopedValue<Object>> returnedPreferences = preferenceStore.searchScoped( preferenceScopeResolutionStrategy, preferences.keySet() );

        assertEquals( preferences.size(), returnedPreferences.size() );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            final String key = preference.getKey();
            final String value = preference.getValue();

            assertTrue( returnedPreferences.containsKey( key ) );
            assertEquals( value, returnedPreferences.get( key ).getValue() );
            assertEquals( preferenceScopeResolutionStrategy.order().get( 0 ).type(), returnedPreferences.get( key ).getScope().type() );
            assertEquals( preferenceScopeResolutionStrategy.order().get( 0 ).key(), returnedPreferences.get( key ).getScope().key() );

            verify( storage ).read( preferenceScopeResolutionStrategy.order().get( 0 ), key );
        }
    }

    @Test
    public void searchScopedOnDefaultScopeResolutionStrategyTest() {
        final Map<String, String> preferences = new HashMap<>();
        preferences.put( FIRST_KEY, FIRST_VALUE );
        preferences.put( SECOND_KEY, SECOND_VALUE );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            doReturn( preference.getValue() ).when( storage ).read( preferenceScopeResolutionStrategy.order().get( 0 ), preference.getKey() );
        }

        final Map<String, PreferenceScopedValue<Object>> returnedPreferences = preferenceStore.searchScoped( preferences.keySet() );

        assertEquals( preferences.size(), returnedPreferences.size() );

        for ( Map.Entry<String, String> preference : preferences.entrySet() ) {
            final String key = preference.getKey();
            final String value = preference.getValue();

            assertTrue( returnedPreferences.containsKey( key ) );
            assertEquals( value, returnedPreferences.get( key ).getValue() );
            assertEquals( preferenceScopeResolutionStrategy.order().get( 0 ).type(), returnedPreferences.get( key ).getScope().type() );
            assertEquals( preferenceScopeResolutionStrategy.order().get( 0 ).key(), returnedPreferences.get( key ).getScope().key() );

            verify( storage ).read( preferenceScopeResolutionStrategy.order().get( 0 ), key );
        }
    }

    @Test
    public void allPreferencesByScopeTest() {
        final PreferenceScope userScope = scopeBuilder.build( DefaultScopes.USER.type() );
        doReturn( FIRST_VALUE ).when( storage ).read( userScope, FIRST_KEY );
        doReturn( SECOND_VALUE ).when( storage ).read( userScope, SECOND_KEY );

        List<String> keys = new ArrayList<>( 2 );
        keys.add( FIRST_KEY );
        keys.add( SECOND_KEY );
        doReturn( keys ).when( storage ).allKeys( userScope );

        final Map<String, Object> valueByKey = preferenceStore.all( userScope );

        assertNotNull( valueByKey );
        assertEquals( 2, valueByKey.size() );

        assertTrue( valueByKey.containsKey( FIRST_KEY ) );
        assertTrue( valueByKey.containsKey( SECOND_KEY ) );

        assertEquals( FIRST_VALUE, valueByKey.get( FIRST_KEY ) );
        assertEquals( SECOND_VALUE, valueByKey.get( SECOND_KEY ) );
    }

    @Test
    public void allPreferencesByScopeResolutionStrategyTest() {
        doReturn( FIRST_VALUE ).when( storage ).read( preferenceScopeResolutionStrategy, FIRST_KEY );
        doReturn( SECOND_VALUE ).when( storage ).read( preferenceScopeResolutionStrategy, SECOND_KEY );
        doReturn( THIRD_VALUE ).when( storage ).read( preferenceScopeResolutionStrategy, THIRD_KEY );

        List<String> preferenceKeys = new ArrayList<>( 3 );
        preferenceKeys.add( FIRST_KEY );
        preferenceKeys.add( SECOND_KEY );
        preferenceKeys.add( THIRD_KEY );
        doReturn( preferenceKeys ).when( storage ).allKeys( preferenceScopeResolutionStrategy.order() );

        Map<String, Object> valueByKey = preferenceStore.all( preferenceScopeResolutionStrategy );

        assertNotNull( valueByKey );
        assertEquals( 3, valueByKey.size() );

        assertTrue( valueByKey.containsKey( FIRST_KEY ) );
        assertTrue( valueByKey.containsKey( SECOND_KEY ) );
        assertTrue( valueByKey.containsKey( THIRD_KEY ) );

        assertEquals( FIRST_VALUE, valueByKey.get( FIRST_KEY ) );
        assertEquals( SECOND_VALUE, valueByKey.get( SECOND_KEY ) );
        assertEquals( THIRD_VALUE, valueByKey.get( THIRD_KEY ) );
    }

    @Test
    public void allPreferencesByScopeTypeTest() {
        final PreferenceScope userScope = scopeBuilder.build( DefaultScopes.USER.type() );
        doReturn( FIRST_VALUE ).when( storage ).read( userScope, FIRST_KEY );
        doReturn( SECOND_VALUE ).when( storage ).read( userScope, SECOND_KEY );

        List<String> keys = new ArrayList<>( 2 );
        keys.add( FIRST_KEY );
        keys.add( SECOND_KEY );
        doReturn( keys ).when( storage ).allKeys( userScope );

        final Map<String, Object> valueByKey = preferenceStore.all( DefaultScopes.USER.type() );

        assertNotNull( valueByKey );
        assertEquals( 2, valueByKey.size() );

        assertTrue( valueByKey.containsKey( FIRST_KEY ) );
        assertTrue( valueByKey.containsKey( SECOND_KEY ) );

        assertEquals( FIRST_VALUE, valueByKey.get( FIRST_KEY ) );
        assertEquals( SECOND_VALUE, valueByKey.get( SECOND_KEY ) );
    }

    @Test
    public void allPreferencesTest() {
        doReturn( FIRST_VALUE ).when( storage ).read( preferenceScopeResolutionStrategy, FIRST_KEY );
        doReturn( SECOND_VALUE ).when( storage ).read( preferenceScopeResolutionStrategy, SECOND_KEY );
        doReturn( THIRD_VALUE ).when( storage ).read( preferenceScopeResolutionStrategy, THIRD_KEY );

        List<String> preferenceKeys = new ArrayList<>( 3 );
        preferenceKeys.add( FIRST_KEY );
        preferenceKeys.add( SECOND_KEY );
        preferenceKeys.add( THIRD_KEY );
        doReturn( preferenceKeys ).when( storage ).allKeys( preferenceScopeResolutionStrategy.order() );

        Map<String, Object> valueByKey = preferenceStore.all();

        assertNotNull( valueByKey );
        assertEquals( 3, valueByKey.size() );

        assertTrue( valueByKey.containsKey( FIRST_KEY ) );
        assertTrue( valueByKey.containsKey( SECOND_KEY ) );
        assertTrue( valueByKey.containsKey( THIRD_KEY ) );

        assertEquals( FIRST_VALUE, valueByKey.get( FIRST_KEY ) );
        assertEquals( SECOND_VALUE, valueByKey.get( SECOND_KEY ) );
        assertEquals( THIRD_VALUE, valueByKey.get( THIRD_KEY ) );
    }

    @Test
    public void allScopedPreferencesByScopeResolutionStrategyTest() {
        doReturn( new PreferenceScopedValue<>( FIRST_VALUE, globalScope.type(), globalScope.key() ) ).when( storage ).readWithScope( preferenceScopeResolutionStrategy, FIRST_KEY );
        doReturn( new PreferenceScopedValue<>( SECOND_VALUE, globalScope.type(), globalScope.key() ) ).when( storage ).readWithScope( preferenceScopeResolutionStrategy, SECOND_KEY );
        doReturn( new PreferenceScopedValue<>( THIRD_VALUE, userScope.type(), userScope.key() ) ).when( storage ).readWithScope( preferenceScopeResolutionStrategy, THIRD_KEY );

        List<String> preferenceKeys = new ArrayList<>( 3 );
        preferenceKeys.add( FIRST_KEY );
        preferenceKeys.add( SECOND_KEY );
        preferenceKeys.add( THIRD_KEY );
        doReturn( preferenceKeys ).when( storage ).allKeys( preferenceScopeResolutionStrategy.order() );

        Map<String, PreferenceScopedValue<Object>> valueByKey = preferenceStore.allScoped( preferenceScopeResolutionStrategy );

        assertNotNull( valueByKey );
        assertEquals( 3, valueByKey.size() );

        assertTrue( valueByKey.containsKey( FIRST_KEY ) );
        assertTrue( valueByKey.containsKey( SECOND_KEY ) );
        assertTrue( valueByKey.containsKey( THIRD_KEY ) );

        assertEquals( FIRST_VALUE, valueByKey.get( FIRST_KEY ).getValue() );
        assertEquals( globalScope.type(), valueByKey.get( FIRST_KEY ).getScope().type() );
        assertEquals( globalScope.key(), valueByKey.get( FIRST_KEY ).getScope().key() );

        assertEquals( SECOND_VALUE, valueByKey.get( SECOND_KEY ).getValue() );
        assertEquals( globalScope.type(), valueByKey.get( SECOND_KEY ).getScope().type() );
        assertEquals( globalScope.key(), valueByKey.get( SECOND_KEY ).getScope().key() );

        assertEquals( THIRD_VALUE, valueByKey.get( THIRD_KEY ).getValue() );
        assertEquals( userScope.type(), valueByKey.get( THIRD_KEY ).getScope().type() );
        assertEquals( userScope.key(), valueByKey.get( THIRD_KEY ).getScope().key() );
    }

    @Test
    public void allScopedPreferencesByDefaultScopeResolutionStrategyTest() {
        doReturn( new PreferenceScopedValue<>( FIRST_VALUE, globalScope.type(), globalScope.key() ) ).when( storage ).readWithScope( preferenceScopeResolutionStrategy, FIRST_KEY );
        doReturn( new PreferenceScopedValue<>( SECOND_VALUE, globalScope.type(), globalScope.key() ) ).when( storage ).readWithScope( preferenceScopeResolutionStrategy, SECOND_KEY );
        doReturn( new PreferenceScopedValue<>( THIRD_VALUE, userScope.type(), userScope.key() ) ).when( storage ).readWithScope( preferenceScopeResolutionStrategy, THIRD_KEY );

        List<String> preferenceKeys = new ArrayList<>( 3 );
        preferenceKeys.add( FIRST_KEY );
        preferenceKeys.add( SECOND_KEY );
        preferenceKeys.add( THIRD_KEY );
        doReturn( preferenceKeys ).when( storage ).allKeys( preferenceScopeResolutionStrategy.order() );

        Map<String, PreferenceScopedValue<Object>> valueByKey = preferenceStore.allScoped();

        assertNotNull( valueByKey );
        assertEquals( 3, valueByKey.size() );

        assertTrue( valueByKey.containsKey( FIRST_KEY ) );
        assertTrue( valueByKey.containsKey( SECOND_KEY ) );
        assertTrue( valueByKey.containsKey( THIRD_KEY ) );

        assertEquals( FIRST_VALUE, valueByKey.get( FIRST_KEY ).getValue() );
        assertEquals( globalScope.type(), valueByKey.get( FIRST_KEY ).getScope().type() );
        assertEquals( globalScope.key(), valueByKey.get( FIRST_KEY ).getScope().key() );

        assertEquals( SECOND_VALUE, valueByKey.get( SECOND_KEY ).getValue() );
        assertEquals( globalScope.type(), valueByKey.get( SECOND_KEY ).getScope().type() );
        assertEquals( globalScope.key(), valueByKey.get( SECOND_KEY ).getScope().key() );

        assertEquals( THIRD_VALUE, valueByKey.get( THIRD_KEY ).getValue() );
        assertEquals( userScope.type(), valueByKey.get( THIRD_KEY ).getScope().type() );
        assertEquals( userScope.key(), valueByKey.get( THIRD_KEY ).getScope().key() );
    }

    @Test
    public void removeByScopeTest() {
        preferenceStore.remove( globalScope, KEY );

        verify( storage ).delete( globalScope, KEY );
    }

    @Test
    public void removeByScopeTypeTest() {
        preferenceStore.remove( DefaultScopes.GLOBAL.type(), KEY );

        verify( storage ).delete( globalScope, KEY );
    }

    @Test
    public void removeByScopesTest() {
        preferenceStore.remove( preferenceScopeResolutionStrategy.order(), KEY );

        verify( storage ).delete( preferenceScopeResolutionStrategy.order().get( 0 ), KEY );
        verify( storage ).delete( preferenceScopeResolutionStrategy.order().get( 1 ), KEY );
    }

    @Test
    public void removeByScopeTypesTest() {
        preferenceStore.removeScopeTypes( DefaultScopes.allTypes(), KEY );

        verify( storage ).delete( preferenceScopeResolutionStrategy.order().get( 0 ), KEY );
        verify( storage ).delete( preferenceScopeResolutionStrategy.order().get( 1 ), KEY );
    }

    private void mockStorageRead( final String value ) {
        doReturn( value ).when( storage ).read( any( PreferenceScope.class ), anyString() );
        doReturn( value ).when( storage ).read( any( PreferenceScopeResolutionStrategy.class ), anyString() );
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
