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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceBeanServerStore;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

/**
 * Client implementation for {@link PreferenceBeanStore}. It just pass along the method calls to the backend
 * implementation, with the proper callbacks.
 */
@Alternative
@ApplicationScoped
public class PreferenceBeanStoreImpl implements PreferenceBeanStore {

    private Caller<PreferenceBeanServerStore> store;

    @Inject
    public PreferenceBeanStoreImpl(final Caller<PreferenceBeanServerStore> store) {
        this.store = store;
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void load(final T emptyPortablePreference,
                                                                                        final ParameterizedCommand<T> successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        store.call(new RemoteCallback<T>() {
                       @Override
                       public void callback(final T portablePreference) {
                           if (successCallback != null) {
                               successCallback.execute(portablePreference);
                           }
                       }
                   },
                   new ErrorCallback<Throwable>() {
                       @Override
                       public boolean error(final Throwable throwable,
                                            final Throwable throwable2) {
                           if (errorCallback != null) {
                               errorCallback.execute(throwable);
                           }
                           return false;
                       }
                   }).load(emptyPortablePreference);
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void load(final T emptyPortablePreference,
                                                                                        final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                                                        final ParameterizedCommand<T> successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        store.call(new RemoteCallback<T>() {
                       @Override
                       public void callback(final T portablePreference) {
                           if (successCallback != null) {
                               successCallback.execute(portablePreference);
                           }
                       }
                   },
                   new ErrorCallback<Throwable>() {
                       @Override
                       public boolean error(final Throwable throwable,
                                            final Throwable throwable2) {
                           if (errorCallback != null) {
                               errorCallback.execute(throwable);
                           }
                           return false;
                       }
                   }).load(emptyPortablePreference,
                           scopeResolutionStrategyInfo);
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(final T portablePreference,
                                                                                        final Command successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        store.call(voidReturn -> {
                       if (successCallback != null) {
                           successCallback.execute();
                       }
                   },
                   (message, throwable) -> {
                       if (errorCallback != null) {
                           errorCallback.execute(throwable);
                       }
                       return false;
                   }).save(portablePreference);
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(final T portablePreference,
                                                                                        final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                                                        final Command successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        store.call(voidReturn -> {
                       if (successCallback != null) {
                           successCallback.execute();
                       }
                   },
                   (message, throwable) -> {
                       if (errorCallback != null) {
                           errorCallback.execute(throwable);
                       }
                       return false;
                   }).save(portablePreference,
                           scopeResolutionStrategyInfo);
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(T portablePreference,
                                                                                        PreferenceScope scope,
                                                                                        Command successCallback,
                                                                                        ParameterizedCommand<Throwable> errorCallback) {
        store.call(voidReturn -> {
                       if (successCallback != null) {
                           successCallback.execute();
                       }
                   },
                   (message, throwable) -> {
                       if (errorCallback != null) {
                           errorCallback.execute(throwable);
                       }
                       return false;
                   }).save(portablePreference,
                           scope);
    }

    @Override
    public void save(final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                     final Command successCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        store.call(voidReturn -> {
                       if (successCallback != null) {
                           successCallback.execute();
                       }
                   },
                   (message, throwable) -> {
                       if (errorCallback != null) {
                           errorCallback.execute(throwable);
                       }
                       return false;
                   }).save(portablePreferences);
    }

    @Override
    public void save(final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                     final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                     final Command successCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        store.call(voidReturn -> {
                       if (successCallback != null) {
                           successCallback.execute();
                       }
                   },
                   (message, throwable) -> {
                       if (errorCallback != null) {
                           errorCallback.execute(throwable);
                       }
                       return false;
                   }).save(portablePreferences,
                           scopeResolutionStrategyInfo);
    }

    @Override
    public void save(Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                     PreferenceScope scope,
                     Command successCallback,
                     ParameterizedCommand<Throwable> errorCallback) {
        store.call(voidReturn -> {
                       if (successCallback != null) {
                           successCallback.execute();
                       }
                   },
                   (message, throwable) -> {
                       if (errorCallback != null) {
                           errorCallback.execute(throwable);
                       }
                       return false;
                   }).save(portablePreferences,
                           scope);
    }
}