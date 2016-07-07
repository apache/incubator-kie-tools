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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.PreferenceScopeBuilder;
import org.uberfire.ext.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.ext.preferences.shared.PreferenceScopedValue;
import org.uberfire.ext.preferences.shared.PreferenceStorage;
import org.uberfire.ext.preferences.shared.PreferenceStore;

@Service
public class PreferenceStoreImpl implements PreferenceStore {

    protected PreferenceStorage storage;

    protected PreferenceScopeResolutionStrategy defaultResolutionStrategy;

    protected PreferenceScopeBuilder scopeBuilder;

    protected PreferenceStoreImpl() {
    }

    @Inject
    public PreferenceStoreImpl( final PreferenceStorage storage,
                                @Customizable final PreferenceScopeResolutionStrategy defaultResolutionStrategy,
                                final PreferenceScopeBuilder scopeBuilder ) {
        this.storage = storage;
        this.defaultResolutionStrategy = defaultResolutionStrategy;
        this.scopeBuilder = scopeBuilder;
    }

    @Override
    public <T> void put( final PreferenceScope scope,
                         final String key,
                         final T value ) {
        storage.write( scope, key, value );
    }

    @Override
    public <T> void put( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                         final String key,
                         final T value ) {
        put( scopeResolutionStrategy.defaultScope(), key, value );
    }

    @Override
    public <T> void put( final String scopeType,
                         final String key,
                         final T value ) {
        put( scopeBuilder.build( scopeType ), key, value );
    }

    @Override
    public <T> void put( final String key,
                         final T value ) {
        put( defaultResolutionStrategy, key, value );
    }

    @Override
    public <T> void put( final PreferenceScope scope,
                         final Map<String, T> valueByKey ) {
        valueByKey.forEach( ( key, value ) -> put( scope, key, value ) );
    }

    @Override
    public <T> void put( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                         final Map<String, T> valueByKey ) {
        put( scopeResolutionStrategy.defaultScope(), valueByKey );
    }

    @Override
    public <T> void put( final String scopeType,
                         final Map<String, T> valueByKey ) {
        put( scopeBuilder.build( scopeType ), valueByKey );
    }

    @Override
    public <T> void put( final Map<String, T> valueByKey ) {
        put( defaultResolutionStrategy, valueByKey );
    }

    @Override
    public <T> void putIfAbsent( final PreferenceScope scope,
                                 final String key,
                                 final T value ) {
        if ( !storage.exists( scope, key ) ) {
            put( scope, key, value );
        }
    }

    @Override
    public <T> void putIfAbsent( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                                 final String key,
                                 final T value ) {
        putIfAbsent( scopeResolutionStrategy.defaultScope(), key, value );
    }

    @Override
    public <T> void putIfAbsent( final String scopeType,
                                 final String key,
                                 final T value ) {
        putIfAbsent( scopeBuilder.build( scopeType ), key, value );
    }

    @Override
    public <T> void putIfAbsent( final String key,
                                 final T value ) {
        putIfAbsent( defaultResolutionStrategy, key, value );
    }

    @Override
    public <T> void putIfAbsent( final PreferenceScope scope,
                                 final Map<String, T> valueByKey ) {
        valueByKey.forEach( ( key, value ) -> putIfAbsent( scope, key, value ) );
    }

    @Override
    public <T> void putIfAbsent( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                                 final Map<String, T> valueByKey ) {
        putIfAbsent( scopeResolutionStrategy.defaultScope(), valueByKey );
    }

    @Override
    public <T> void putIfAbsent( final String scopeType,
                                 final Map<String, T> valueByKey ) {
        putIfAbsent( scopeBuilder.build( scopeType ), valueByKey );
    }

    @Override
    public <T> void putIfAbsent( final Map<String, T> valueByKey ) {
        putIfAbsent( defaultResolutionStrategy, valueByKey );
    }

    @Override
    public <T> T get( final PreferenceScope scope,
                      final String key ) {
        return storage.read( scope, key );
    }

    @Override
    public <T> T get( final PreferenceScope scope,
                      final String key,
                      final T defaultValue ) {
        T value = get( scope, key );
        return value != null ? value : defaultValue;
    }

    @Override
    public <T> T get( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                      final String key ) {
        return storage.read( scopeResolutionStrategy, key );
    }

    @Override
    public <T> T get( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                      final String key,
                      final T defaultValue ) {
        T value = get( scopeResolutionStrategy, key );
        return value != null ? value : defaultValue;
    }

    @Override
    public <T> T get( final String scopeType,
                      final String key ) {
        return get( scopeBuilder.build( scopeType ), key );
    }

    @Override
    public <T> T get( final String scopeType,
                      final String key,
                      final T defaultValue ) {
        return get( scopeBuilder.build( scopeType ), key, defaultValue );
    }

    @Override
    public <T> T get( final String key ) {
        return get( defaultResolutionStrategy, key );
    }


    @Override
    public <T> PreferenceScopedValue<T> getScoped( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                                                   final String key ) {
        return storage.readWithScope( scopeResolutionStrategy, key );
    }

    @Override
    public <T> PreferenceScopedValue<T> getScoped( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                                                   final String key,
                                                   final T defaultValue ) {
        PreferenceScopedValue<T> value = getScoped( scopeResolutionStrategy, key );
        return value != null ? value : new PreferenceScopedValue<>( defaultValue, null, null );
    }

    @Override
    public <T> PreferenceScopedValue<T> getScoped( final String key ) {
        return getScoped( defaultResolutionStrategy, key );
    }

    @Override
    public <T> PreferenceScopedValue<T> getScoped( final String key,
                                                   final T defaultValue ) {
        return getScoped( defaultResolutionStrategy, key, defaultValue );
    }

    @Override
    public Map<String, Object> search( final PreferenceScope scope,
                                       final Collection<String> keys ) {
        if ( keys == null ) {
            return all( scope );
        }

        Map<String, Object> map = new HashMap<>();
        keys.forEach( key -> map.put( key, storage.read( scope, key ) ) );

        return map;
    }

    @Override
    public Map<String, Object> search( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                                       final Collection<String> keys ) {
        if ( keys == null ) {
            return all( scopeResolutionStrategy );
        }

        Map<String, Object> map = new HashMap<>();
        keys.forEach( key -> map.put( key, storage.read( scopeResolutionStrategy, key ) ) );

        return map;
    }

    @Override
    public Map<String, Object> search( final String scopeType,
                                       final Collection<String> keys ) {
        return search( scopeBuilder.build( scopeType ), keys );
    }

    @Override
    public Map<String, Object> search( final Collection<String> keys ) {
        return search( defaultResolutionStrategy, keys );
    }

    @Override
    public Map<String, PreferenceScopedValue<Object>> searchScoped( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                                                                    final Collection<String> keys ) {
        if ( keys == null ) {
            return allScoped( scopeResolutionStrategy );
        }

        Map<String, PreferenceScopedValue<Object>> map = new HashMap<>();
        keys.forEach( key -> map.put( key, storage.readWithScope( scopeResolutionStrategy, key ) ) );

        return map;
    }

    @Override
    public Map<String, PreferenceScopedValue<Object>> searchScoped( final Collection<String> keys ) {
        return searchScoped( defaultResolutionStrategy, keys );
    }

    @Override
    public Map<String, Object> all( final PreferenceScope scope ) {
        return search( scope, storage.allKeys( scope ) );
    }

    @Override
    public Map<String, Object> all( final PreferenceScopeResolutionStrategy scopeResolutionStrategy ) {
        return search( storage.allKeys( scopeResolutionStrategy.order() ) );
    }

    @Override
    public Map<String, Object> all( final String scopeType ) {
        return all( scopeBuilder.build( scopeType ) );
    }

    @Override
    public Map<String, Object> all() {
        return all( defaultResolutionStrategy );
    }

    @Override
    public Map<String, PreferenceScopedValue<Object>> allScoped( final PreferenceScopeResolutionStrategy scopeResolutionStrategy ) {
        return searchScoped( storage.allKeys( scopeResolutionStrategy.order() ) );
    }

    @Override
    public Map<String, PreferenceScopedValue<Object>> allScoped() {
        return allScoped( defaultResolutionStrategy );
    }

    @Override
    public void remove( final PreferenceScope scope,
                        final String key ) {
        storage.delete( scope, key );
    }

    @Override
    public void remove( final String scopeType,
                        final String key ) {
        remove( scopeBuilder.build( scopeType ), key );
    }

    @Override
    public void remove( final List<PreferenceScope> scopes,
                        final String key ) {
        scopes.forEach( scope -> remove( scope, key ) );
    }

    @Override
    public void removeScopeTypes( final List<String> scopeTypes,
                                  final String key ) {
        scopeTypes.forEach( scopeType -> remove( scopeType, key ) );
    }
}
