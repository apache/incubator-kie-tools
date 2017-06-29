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

package org.uberfire.preferences.shared.bean.mock;

import javax.annotation.Generated;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.bean.BasePreferenceBean;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

@Dependent
@Generated("org.uberfire.ext.preferences.processors.WorkbenchPreferenceProcessor")
/*
* WARNING! This class is generated. Do not modify.
*/
public class PortablePreferenceMockBeanGeneratedImpl extends PortablePreferenceMock implements BasePreferenceBean<PortablePreferenceMock> {

    private PreferenceBeanStore store;

    @Inject
    public PortablePreferenceMockBeanGeneratedImpl(final PreferenceBeanStore store) {
        this.store = store;
    }

    @Override
    public void load() {
        load((ParameterizedCommand<Throwable>) null);
    }

    @Override
    public void load(final ParameterizedCommand<Throwable> errorCallback) {
        load((ParameterizedCommand<PortablePreferenceMock>) null,
             errorCallback);
    }

    @Override
    public void load(final ParameterizedCommand<PortablePreferenceMock> successCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        final PortablePreferenceMockBeanGeneratedImpl preferenceBean = this;

        store.load(new PortablePreferenceMockPortableGeneratedImpl(),
                   getLoadSuccessCallback(successCallback),
                   errorCallback);
    }

    @Override
    public void load(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy) {
        load(customScopeResolutionStrategy,
             null);
    }

    @Override
    public void load(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                     final ParameterizedCommand<Throwable> errorCallback) {
        load(customScopeResolutionStrategy,
             null,
             errorCallback);
    }

    @Override
    public void load(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                     final ParameterizedCommand<PortablePreferenceMock> successCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        final PortablePreferenceMockBeanGeneratedImpl preferenceBean = this;

        store.load(new PortablePreferenceMockPortableGeneratedImpl(),
                   customScopeResolutionStrategy,
                   getLoadSuccessCallback(successCallback),
                   errorCallback);
    }

    private ParameterizedCommand<BasePreferencePortable<PortablePreferenceMock>> getLoadSuccessCallback(final ParameterizedCommand<PortablePreferenceMock> successCallback) {
        final PortablePreferenceMockBeanGeneratedImpl preferenceBean = this;

        return new ParameterizedCommand<BasePreferencePortable<PortablePreferenceMock>>() {
            @Override
            public void execute(final BasePreferencePortable<PortablePreferenceMock> portablePreference) {
                copy((PortablePreferenceMockPortableGeneratedImpl) portablePreference,
                     preferenceBean);
                if (successCallback != null) {
                    successCallback.execute(preferenceBean);
                }
            }
        };
    }

    private void copy(final PortablePreferenceMock from,
                      final PortablePreferenceMock to) {
        to.property = from.property;
    }

    @Override
    public void save() {
        save((ParameterizedCommand<Throwable>) null);
    }

    @Override
    public void save(final ParameterizedCommand<Throwable> errorCallback) {
        save((Command) null,
             errorCallback);
    }

    @Override
    public void save(final Command successCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        store.save(createPortableCopy(),
                   successCallback,
                   errorCallback);
    }

    @Override
    public void save(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy) {
        save(customScopeResolutionStrategy,
             null);
    }

    @Override
    public void save(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                     final ParameterizedCommand<Throwable> errorCallback) {
        save(customScopeResolutionStrategy,
             null,
             errorCallback);
    }

    @Override
    public void save(final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                     final Command successCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        store.save(createPortableCopy(),
                   customScopeResolutionStrategy,
                   successCallback,
                   errorCallback);
    }

    private BasePreferencePortable<PortablePreferenceMock> createPortableCopy() {
        PortablePreferenceMockPortableGeneratedImpl portablePreference = new PortablePreferenceMockPortableGeneratedImpl();

        copy(this,
             portablePreference);

        return portablePreference;
    }
}
