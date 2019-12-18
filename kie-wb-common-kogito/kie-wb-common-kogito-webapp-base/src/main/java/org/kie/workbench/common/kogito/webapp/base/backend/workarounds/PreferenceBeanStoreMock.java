/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.kogito.webapp.base.backend.workarounds;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

@ApplicationScoped
public class PreferenceBeanStoreMock implements PreferenceBeanStore {

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void load(T emptyPortablePreference, ParameterizedCommand<T> successCallback, ParameterizedCommand<Throwable> errorCallback) {

    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void load(T emptyPortablePreference, PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo, ParameterizedCommand<T> successCallback, ParameterizedCommand<Throwable> errorCallback) {

    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(T portablePreference, Command successCallback, ParameterizedCommand<Throwable> errorCallback) {

    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(T portablePreference, PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo, Command successCallback, ParameterizedCommand<Throwable> errorCallback) {

    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(T portablePreference, PreferenceScope scope, Command successCallback, ParameterizedCommand<Throwable> errorCallback) {

    }

    @Override
    public void save(Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences, Command successCallback, ParameterizedCommand<Throwable> errorCallback) {

    }

    @Override
    public void save(Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences, PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo, Command successCallback, ParameterizedCommand<Throwable> errorCallback) {

    }

    @Override
    public void save(Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences, PreferenceScope scope, Command successCallback, ParameterizedCommand<Throwable> errorCallback) {

    }
}
