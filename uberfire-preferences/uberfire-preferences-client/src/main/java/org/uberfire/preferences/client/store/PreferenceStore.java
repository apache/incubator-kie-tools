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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeResolver;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.preferences.shared.impl.PreferenceScopedValue;

/**
 * Client store API for preferences. All client preference management should be made through this service
 * implementation (see {@link PreferenceStore}).
 * <p>
 * Each method from {@link PreferenceStore} has three corresponding methods here: one receives the
 * same parameters, another also receive a success callback and another that receives a success and an
 * error callback.
 * <p>
 * For more details on each method operation, you can check the
 * {@link org.uberfire.preferences.shared.PreferenceStore} documentation.
 */
public class PreferenceStore {

    private String componentKey;

    private Caller<org.uberfire.preferences.shared.PreferenceStore> preferenceStoreCaller;

    private PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo;

    private PreferenceScopeResolver scopeResolver;

    public PreferenceStore(final String componentKey,
                           final Caller<org.uberfire.preferences.shared.PreferenceStore> preferenceStoreCaller,
                           final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                           final PreferenceScopeResolver scopeResolver) {
        this.componentKey = componentKey;
        this.preferenceStoreCaller = preferenceStoreCaller;
        this.scopeResolutionStrategyInfo = scopeResolutionStrategyInfo;
        this.scopeResolver = scopeResolver;
    }

    /**
     * Provides a portable instance that has the scope resolution strategy order and the default scope
     * for preference persistence.
     * @return A portable scope information instance.
     */
    public PreferenceScopeResolutionStrategyInfo getDefaultScopeResolutionStrategyInfo() {
        return scopeResolutionStrategyInfo;
    }

    /**
     * Provides a portable instance that resolves scopes in the default scope resolution strategy order.
     * @return A portable scope resolver instance.
     */
    public PreferenceScopeResolver getDefaultScopeResolver() {
        return scopeResolver;
    }

    private <T> RemoteCallback<T> emptySuccessCallback() {
        return result -> {
        };
    }

    private ErrorCallback<Message> defaultErrorCallback() {
        return ( message, throwable ) -> false;
    }

    public <T> void put(final PreferenceScope scope,
                        final String key,
                        final T value) {
        put(scope,
            key,
            value,
            emptySuccessCallback());
    }

    public <T> void put(final PreferenceScope scope,
                        final String key,
                        final T value,
                        final RemoteCallback<Void> successCallback) {
        put(scope,
            key,
            value,
            successCallback,
            defaultErrorCallback());
    }

    public <T> void put(final PreferenceScope scope,
                        final String key,
                        final T value,
                        final RemoteCallback<Void> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).put(scope,
                                                      key,
                                                      value);
    }

    public <T> void put(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final String key,
                        final T value) {
        put(scopeResolutionStrategyInfo,
            key,
            value,
            emptySuccessCallback());
    }

    public <T> void put(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final String key,
                        final T value,
                        final RemoteCallback<Void> successCallback) {
        put(scopeResolutionStrategyInfo,
            key,
            value,
            successCallback,
            defaultErrorCallback());
    }

    public <T> void put(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final String key,
                        final T value,
                        final RemoteCallback<Void> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).put(scopeResolutionStrategyInfo,
                                                      key,
                                                      value);
    }

    public <T> void put(final String key,
                        final T value) {
        put(scopeResolutionStrategyInfo,
            key,
            value);
    }

    public <T> void put(final String key,
                        final T value,
                        final RemoteCallback<Void> successCallback) {
        put(scopeResolutionStrategyInfo,
            key,
            value,
            successCallback);
    }

    public <T> void put(final String key,
                        final T value,
                        final RemoteCallback<Void> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        put(scopeResolutionStrategyInfo,
            key,
            value,
            successCallback,
            errorCallback);
    }

    public <T> void put(final PreferenceScope scope,
                        final Map<String, T> valueByKey) {
        put(scope,
            valueByKey,
            emptySuccessCallback());
    }

    public <T> void put(final PreferenceScope scope,
                        final Map<String, T> valueByKey,
                        final RemoteCallback<Void> successCallback) {
        put(scope,
            valueByKey,
            successCallback,
            defaultErrorCallback());
    }

    public <T> void put(final PreferenceScope scope,
                        final Map<String, T> valueByKey,
                        final RemoteCallback<Void> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).put(scope,
                                                      valueByKey);
    }

    public <T> void put(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final Map<String, T> valueByKey) {
        put(scopeResolutionStrategyInfo,
            valueByKey,
            emptySuccessCallback());
    }

    public <T> void put(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final Map<String, T> valueByKey,
                        final RemoteCallback<Void> successCallback) {
        put(scopeResolutionStrategyInfo,
            valueByKey,
            successCallback,
            defaultErrorCallback());
    }

    public <T> void put(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final Map<String, T> valueByKey,
                        final RemoteCallback<Void> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).put(scopeResolutionStrategyInfo,
                                                      valueByKey);
    }

    public <T> void put(final Map<String, T> valueByKey) {
        put(scopeResolutionStrategyInfo,
            valueByKey);
    }

    public <T> void put(final Map<String, T> valueByKey,
                        final RemoteCallback<Void> successCallback) {
        put(scopeResolutionStrategyInfo,
            valueByKey,
            successCallback);
    }

    public <T> void put(final Map<String, T> valueByKey,
                        final RemoteCallback<Void> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        put(scopeResolutionStrategyInfo,
            valueByKey,
            successCallback,
            errorCallback);
    }

    public <T> void putIfAbsent(final PreferenceScope scope,
                                final String key,
                                final T value) {
        putIfAbsent(scope,
                    key,
                    value,
                    emptySuccessCallback());
    }

    public <T> void putIfAbsent(final PreferenceScope scope,
                                final String key,
                                final T value,
                                final RemoteCallback<Void> successCallback) {
        putIfAbsent(scope,
                    key,
                    value,
                    successCallback,
                    defaultErrorCallback());
    }

    public <T> void putIfAbsent(final PreferenceScope scope,
                                final String key,
                                final T value,
                                final RemoteCallback<Void> successCallback,
                                final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).putIfAbsent(scope,
                                                              key,
                                                              value);
    }

    public <T> void putIfAbsent(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                final String key,
                                final T value) {
        putIfAbsent(scopeResolutionStrategyInfo,
                    key,
                    value,
                    emptySuccessCallback());
    }

    public <T> void putIfAbsent(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                final String key,
                                final T value,
                                final RemoteCallback<Void> successCallback) {
        putIfAbsent(scopeResolutionStrategyInfo,
                    key,
                    value,
                    successCallback,
                    defaultErrorCallback());
    }

    public <T> void putIfAbsent(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                final String key,
                                final T value,
                                final RemoteCallback<Void> successCallback,
                                final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).putIfAbsent(scopeResolutionStrategyInfo,
                                                              key,
                                                              value);
    }

    public <T> void putIfAbsent(final String key,
                                final T value) {
        putIfAbsent(scopeResolutionStrategyInfo,
                    key,
                    value);
    }

    public <T> void putIfAbsent(final String key,
                                final T value,
                                final RemoteCallback<Void> successCallback) {
        putIfAbsent(scopeResolutionStrategyInfo,
                    key,
                    value,
                    successCallback);
    }

    public <T> void putIfAbsent(final String key,
                                final T value,
                                final RemoteCallback<Void> successCallback,
                                final ErrorCallback<Message> errorCallback) {
        putIfAbsent(scopeResolutionStrategyInfo,
                    key,
                    value,
                    successCallback,
                    errorCallback);
    }

    public <T> void putIfAbsent(final PreferenceScope scope,
                                final Map<String, T> valueByKey) {
        putIfAbsent(scope,
                    valueByKey,
                    emptySuccessCallback());
    }

    public <T> void putIfAbsent(final PreferenceScope scope,
                                final Map<String, T> valueByKey,
                                final RemoteCallback<Void> successCallback) {
        putIfAbsent(scope,
                    valueByKey,
                    successCallback,
                    defaultErrorCallback());
    }

    public <T> void putIfAbsent(final PreferenceScope scope,
                                final Map<String, T> valueByKey,
                                final RemoteCallback<Void> successCallback,
                                final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).putIfAbsent(scope,
                                                              valueByKey);
    }

    public <T> void putIfAbsent(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                final Map<String, T> valueByKey) {
        putIfAbsent(scopeResolutionStrategyInfo,
                    valueByKey,
                    emptySuccessCallback());
    }

    public <T> void putIfAbsent(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                final Map<String, T> valueByKey,
                                final RemoteCallback<Void> successCallback) {
        putIfAbsent(scopeResolutionStrategyInfo,
                    valueByKey,
                    successCallback,
                    defaultErrorCallback());
    }

    public <T> void putIfAbsent(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                final Map<String, T> valueByKey,
                                final RemoteCallback<Void> successCallback,
                                final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).putIfAbsent(scopeResolutionStrategyInfo,
                                                              valueByKey);
    }

    public <T> void putIfAbsent(final Map<String, T> valueByKey) {
        putIfAbsent(scopeResolutionStrategyInfo,
                    valueByKey);
    }

    public <T> void putIfAbsent(final Map<String, T> valueByKey,
                                final RemoteCallback<Void> successCallback) {
        putIfAbsent(scopeResolutionStrategyInfo,
                    valueByKey,
                    successCallback);
    }

    public <T> void putIfAbsent(final Map<String, T> valueByKey,
                                final RemoteCallback<Void> successCallback,
                                final ErrorCallback<Message> errorCallback) {
        putIfAbsent(scopeResolutionStrategyInfo,
                    valueByKey,
                    successCallback,
                    errorCallback);
    }

    public void get(final PreferenceScope scope,
                    final String key) {
        get(scope,
            key,
            emptySuccessCallback());
    }

    public <T> void get(final PreferenceScope scope,
                        final String key,
                        final RemoteCallback<T> successCallback) {
        get(scope,
            key,
            successCallback,
            defaultErrorCallback());
    }

    public <T> void get(final PreferenceScope scope,
                        final String key,
                        final RemoteCallback<T> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).get(scope,
                                                      key);
    }

    public <T> void get(final PreferenceScope scope,
                        final String key,
                        final T defaultValue) {
        get(scope,
            key,
            defaultValue,
            emptySuccessCallback());
    }

    public <T> void get(final PreferenceScope scope,
                        final String key,
                        final T defaultValue,
                        final RemoteCallback<T> successCallback) {
        get(scope,
            key,
            defaultValue,
            successCallback,
            defaultErrorCallback());
    }

    public <T> void get(final PreferenceScope scope,
                        final String key,
                        final T defaultValue,
                        final RemoteCallback<T> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).get(scope,
                                                      key,
                                                      defaultValue);
    }

    public void get(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                    final String key) {
        get(scopeResolutionStrategyInfo,
            key,
            emptySuccessCallback());
    }

    public <T> void get(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final String key,
                        final RemoteCallback<T> successCallback) {
        get(scopeResolutionStrategyInfo,
            key,
            successCallback,
            defaultErrorCallback());
    }

    public <T> void get(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final String key,
                        final RemoteCallback<T> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).get(scopeResolutionStrategyInfo,
                                                      key);
    }

    public <T> void get(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final String key,
                        final T defaultValue) {
        get(scopeResolutionStrategyInfo,
            key,
            defaultValue,
            emptySuccessCallback());
    }

    public <T> void get(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final String key,
                        final T defaultValue,
                        final RemoteCallback<T> successCallback) {
        get(scopeResolutionStrategyInfo,
            key,
            defaultValue,
            successCallback,
            defaultErrorCallback());
    }

    public <T> void get(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                        final String key,
                        final T defaultValue,
                        final RemoteCallback<T> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).get(scopeResolutionStrategyInfo,
                                                      key,
                                                      defaultValue);
    }

    public void get(final String key) {
        get(scopeResolutionStrategyInfo,
            key);
    }

    public <T> void get(final String key,
                        final RemoteCallback<T> successCallback) {
        get(scopeResolutionStrategyInfo,
            key,
            successCallback);
    }

    public <T> void get(final String key,
                        final RemoteCallback<T> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        get(scopeResolutionStrategyInfo,
            key,
            successCallback,
            errorCallback);
    }

    public <T> void get(final String key,
                        final T defaultValue) {
        get(scopeResolutionStrategyInfo,
            key,
            defaultValue);
    }

    public <T> void get(final String key,
                        final T defaultValue,
                        final RemoteCallback<T> successCallback) {
        get(scopeResolutionStrategyInfo,
            key,
            defaultValue,
            successCallback);
    }

    public <T> void get(final String key,
                        final T defaultValue,
                        final RemoteCallback<T> successCallback,
                        final ErrorCallback<Message> errorCallback) {
        get(scopeResolutionStrategyInfo,
            key,
            defaultValue,
            successCallback,
            errorCallback);
    }

    public void getScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                          final String key) {
        getScoped(scopeResolutionStrategyInfo,
                  key,
                  emptySuccessCallback());
    }

    public <T> void getScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                              final String key,
                              final RemoteCallback<PreferenceScopedValue<T>> successCallback) {
        getScoped(scopeResolutionStrategyInfo,
                  key,
                  successCallback,
                  defaultErrorCallback());
    }

    public <T> void getScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                              final String key,
                              final RemoteCallback<PreferenceScopedValue<T>> successCallback,
                              final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).getScoped(scopeResolutionStrategyInfo,
                                                            key);
    }

    public <T> void getScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                              final String key,
                              final T defaultValue) {
        getScoped(scopeResolutionStrategyInfo,
                  key,
                  defaultValue,
                  emptySuccessCallback());
    }

    public <T> void getScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                              final String key,
                              final T defaultValue,
                              final RemoteCallback<PreferenceScopedValue<T>> successCallback) {
        getScoped(scopeResolutionStrategyInfo,
                  key,
                  defaultValue,
                  successCallback,
                  defaultErrorCallback());
    }

    public <T> void getScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                              final String key,
                              final T defaultValue,
                              final RemoteCallback<PreferenceScopedValue<T>> successCallback,
                              final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).getScoped(scopeResolutionStrategyInfo,
                                                            key,
                                                            defaultValue);
    }

    public void getScoped(final String key) {
        getScoped(scopeResolutionStrategyInfo,
                  key);
    }

    public <T> void getScoped(final String key,
                              final RemoteCallback<PreferenceScopedValue<T>> successCallback) {
        getScoped(scopeResolutionStrategyInfo,
                  key,
                  successCallback);
    }

    public <T> void getScoped(final String key,
                              final RemoteCallback<PreferenceScopedValue<T>> successCallback,
                              final ErrorCallback<Message> errorCallback) {
        getScoped(scopeResolutionStrategyInfo,
                  key,
                  successCallback,
                  errorCallback);
    }

    public <T> void getScoped(final String key,
                              final T defaultValue) {
        getScoped(scopeResolutionStrategyInfo,
                  key,
                  defaultValue);
    }

    public <T> void getScoped(final String key,
                              final T defaultValue,
                              final RemoteCallback<PreferenceScopedValue<T>> successCallback) {
        getScoped(scopeResolutionStrategyInfo,
                  key,
                  defaultValue,
                  successCallback);
    }

    public <T> void getScoped(final String key,
                              final T defaultValue,
                              final RemoteCallback<PreferenceScopedValue<T>> successCallback,
                              final ErrorCallback<Message> errorCallback) {
        getScoped(scopeResolutionStrategyInfo,
                  key,
                  defaultValue,
                  successCallback,
                  errorCallback);
    }

    public void search(final PreferenceScope scope,
                       final Collection<String> keys) {
        search(scope,
               keys,
               emptySuccessCallback());
    }

    public void search(final PreferenceScope scope,
                       final Collection<String> keys,
                       final RemoteCallback<Map<String, Object>> successCallback) {
        search(scope,
               keys,
               successCallback,
               defaultErrorCallback());
    }

    public void search(final PreferenceScope scope,
                       final Collection<String> keys,
                       final RemoteCallback<Map<String, Object>> successCallback,
                       final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).search(scope,
                                                         keys);
    }

    public void search(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                       final Collection<String> keys) {
        search(scopeResolutionStrategyInfo,
               keys,
               emptySuccessCallback());
    }

    public void search(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                       final Collection<String> keys,
                       final RemoteCallback<Map<String, Object>> successCallback) {
        search(scopeResolutionStrategyInfo,
               keys,
               successCallback,
               defaultErrorCallback());
    }

    public void search(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                       final Collection<String> keys,
                       final RemoteCallback<Map<String, Object>> successCallback,
                       final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).search(scopeResolutionStrategyInfo,
                                                         keys);
    }

    public void search(final Collection<String> keys) {
        search(scopeResolutionStrategyInfo,
               keys);
    }

    public void search(final Collection<String> keys,
                       final RemoteCallback<Map<String, Object>> successCallback) {
        search(scopeResolutionStrategyInfo,
               keys,
               successCallback);
    }

    public void search(final Collection<String> keys,
                       final RemoteCallback<Map<String, Object>> successCallback,
                       final ErrorCallback<Message> errorCallback) {
        search(scopeResolutionStrategyInfo,
               keys,
               successCallback,
               errorCallback);
    }

    public void searchScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                             final Collection<String> keys) {
        searchScoped(scopeResolutionStrategyInfo,
                     keys,
                     emptySuccessCallback());
    }

    public void searchScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                             final Collection<String> keys,
                             final RemoteCallback<Map<String, PreferenceScopedValue<Object>>> successCallback) {
        searchScoped(scopeResolutionStrategyInfo,
                     keys,
                     successCallback,
                     defaultErrorCallback());
    }

    public void searchScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                             final Collection<String> keys,
                             final RemoteCallback<Map<String, PreferenceScopedValue<Object>>> successCallback,
                             final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).searchScoped(scopeResolutionStrategyInfo,
                                                               keys);
    }

    public void searchScoped(final Collection<String> keys) {
        searchScoped(scopeResolutionStrategyInfo,
                     keys);
    }

    public void searchScoped(final Collection<String> keys,
                             final RemoteCallback<Map<String, PreferenceScopedValue<Object>>> successCallback) {
        searchScoped(scopeResolutionStrategyInfo,
                     keys,
                     successCallback);
    }

    public void searchScoped(final Collection<String> keys,
                             final RemoteCallback<Map<String, PreferenceScopedValue<Object>>> successCallback,
                             final ErrorCallback<Message> errorCallback) {
        searchScoped(scopeResolutionStrategyInfo,
                     keys,
                     successCallback,
                     errorCallback);
    }

    public void all(final PreferenceScope scope) {
        all(scope,
            emptySuccessCallback());
    }

    public void all(final PreferenceScope scope,
                    final RemoteCallback<Map<String, Object>> successCallback) {
        all(scope,
            successCallback,
            defaultErrorCallback());
    }

    public void all(final PreferenceScope scope,
                    final RemoteCallback<Map<String, Object>> successCallback,
                    final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).all(scope);
    }

    public void all(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) {
        all(scopeResolutionStrategyInfo,
            emptySuccessCallback());
    }

    public void all(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                    final RemoteCallback<Map<String, Object>> successCallback) {
        all(scopeResolutionStrategyInfo,
            successCallback,
            defaultErrorCallback());
    }

    public void all(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                    final RemoteCallback<Map<String, Object>> successCallback,
                    final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).all(scopeResolutionStrategyInfo);
    }

    public void all() {
        all(scopeResolutionStrategyInfo);
    }

    public void all(final RemoteCallback<Map<String, Object>> successCallback) {
        all(scopeResolutionStrategyInfo,
            successCallback);
    }

    public void all(final RemoteCallback<Map<String, Object>> successCallback,
                    final ErrorCallback<Message> errorCallback) {
        all(scopeResolutionStrategyInfo,
            successCallback,
            errorCallback);
    }

    public void allScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) {
        allScoped(scopeResolutionStrategyInfo,
                  emptySuccessCallback());
    }

    public void allScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                          final RemoteCallback<Map<String, PreferenceScopedValue<Object>>> successCallback) {
        allScoped(scopeResolutionStrategyInfo,
                  successCallback,
                  defaultErrorCallback());
    }

    public void allScoped(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                          final RemoteCallback<Map<String, PreferenceScopedValue<Object>>> successCallback,
                          final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).allScoped(scopeResolutionStrategyInfo);
    }

    public void allScoped() {
        allScoped(scopeResolutionStrategyInfo);
    }

    public void allScoped(final RemoteCallback<Map<String, PreferenceScopedValue<Object>>> successCallback) {
        allScoped(scopeResolutionStrategyInfo,
                  successCallback);
    }

    public void allScoped(final RemoteCallback<Map<String, PreferenceScopedValue<Object>>> successCallback,
                          final ErrorCallback<Message> errorCallback) {
        allScoped(scopeResolutionStrategyInfo,
                  successCallback,
                  errorCallback);
    }

    public void remove(final PreferenceScope scope,
                       final String key) {
        remove(scope,
               key,
               emptySuccessCallback());
    }

    public void remove(final PreferenceScope scope,
                       final String key,
                       final RemoteCallback<Void> successCallback) {
        remove(scope,
               key,
               successCallback,
               defaultErrorCallback());
    }

    public void remove(final PreferenceScope scope,
                       final String key,
                       final RemoteCallback<Void> successCallback,
                       final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).remove(scope,
                                                         key);
    }

    public void remove(final List<PreferenceScope> scopes,
                       final String key) {
        remove(scopes,
               key,
               emptySuccessCallback());
    }

    public void remove(final List<PreferenceScope> scopes,
                       final String key,
                       final RemoteCallback<Void> successCallback) {
        remove(scopes,
               key,
               successCallback,
               defaultErrorCallback());
    }

    public void remove(final List<PreferenceScope> scopes,
                       final String key,
                       final RemoteCallback<Void> successCallback,
                       final ErrorCallback<Message> errorCallback) {
        preferenceStoreCaller.call(successCallback,
                                   errorCallback).remove(scopes,
                                                         key);
    }
}
