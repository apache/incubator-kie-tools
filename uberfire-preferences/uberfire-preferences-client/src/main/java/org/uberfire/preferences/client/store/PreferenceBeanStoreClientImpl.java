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

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

/**
 * Client-only implementation for {@link PreferenceBeanStore}.
 */
@Alternative
@ApplicationScoped
public class PreferenceBeanStoreClientImpl implements PreferenceBeanStore {

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void load(final T emptyPortablePreference,
                                                                                        final ParameterizedCommand<T> successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        // loads the default value, without a backend it won't consult the preference storage
        successCallback.execute((T) emptyPortablePreference.defaultValue((U) emptyPortablePreference));
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void load(final T emptyPortablePreference,
                                                                                        final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                                                        final ParameterizedCommand<T> successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        // loads the default value, without a backend it won't consult the preference storage
        successCallback.execute((T) emptyPortablePreference.defaultValue((U) emptyPortablePreference));
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(final T portablePreference,
                                                                                        final Command successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        // just call success callback because there's nothing to do, no backend means no save in our case
        callSuccessCallback(successCallback);
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(final T portablePreference,
                                                                                        final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                                                        final Command successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        // just call success callback because there's nothing to do, no backend means no save in our case
        callSuccessCallback(successCallback);
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(T portablePreference,
                                                                                        PreferenceScope scope,
                                                                                        Command successCallback,
                                                                                        ParameterizedCommand<Throwable> errorCallback) {
        // just call success callback because there's nothing to do, no backend means no save in our case
        callSuccessCallback(successCallback);
    }

    @Override
    public void save(final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                     final Command successCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        // just call success callback because there's nothing to do, no backend means no save in our case
        callSuccessCallback(successCallback);
    }

    @Override
    public void save(final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                     final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                     final Command successCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        // just call success callback because there's nothing to do, no backend means no save in our case
        callSuccessCallback(successCallback);
    }

    @Override
    public void save(Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                     PreferenceScope scope,
                     Command successCallback,
                     ParameterizedCommand<Throwable> errorCallback) {
        // just call success callback because there's nothing to do, no backend means no save in our case
        callSuccessCallback(successCallback);
    }
    
    private void callSuccessCallback(final Command successCallback) {
        if (successCallback != null) {
            successCallback.execute();
        }
    }
}