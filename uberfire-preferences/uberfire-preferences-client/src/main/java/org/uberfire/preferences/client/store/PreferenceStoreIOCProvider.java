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

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.IOCProvider;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.preferences.client.utils.PreferenceQualifierUtils;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.impl.DefaultPreferenceScopeResolutionStrategy;

@IOCProvider
@Dependent
public class PreferenceStoreIOCProvider implements ContextualTypeProvider<PreferenceStore> {

    @Inject
    private ManagedInstance<PreferenceScopeResolutionStrategy> scopeResolutionStrategyProvider;

    @Inject
    private Caller<org.uberfire.preferences.shared.PreferenceStore> preferenceStoreCaller;

    @Inject
    private PreferenceScopeFactory scopeFactory;

    @Override
    public PreferenceStore provide(final Class<?>[] typeargs,
                                   final Annotation[] qualifiers) {
        final String componentKey = PreferenceQualifierUtils.getComponentKeyIfExists(qualifiers);
        final PreferenceScopeResolutionStrategy scopeResolutionStrategy = getScopeResolutionStrategy(componentKey);

        return new PreferenceStore(componentKey,
                                   preferenceStoreCaller,
                                   scopeResolutionStrategy.getInfo(),
                                   scopeResolutionStrategy.getScopeResolver());
    }

    private PreferenceScopeResolutionStrategy getScopeResolutionStrategy(final String componentKey) {
        if (this.scopeResolutionStrategyProvider.isUnsatisfied()) {
            return new DefaultPreferenceScopeResolutionStrategy(scopeFactory,
                                                                componentKey);
        }

        return this.scopeResolutionStrategyProvider.get();
    }
}
