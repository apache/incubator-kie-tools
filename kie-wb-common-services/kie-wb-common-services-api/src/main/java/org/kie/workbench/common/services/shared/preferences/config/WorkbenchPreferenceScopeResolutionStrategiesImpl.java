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

package org.kie.workbench.common.services.shared.preferences.config;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.shared.preferences.WorkbenchPreferenceScopeResolutionStrategies;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

@Dependent
public class WorkbenchPreferenceScopeResolutionStrategiesImpl implements WorkbenchPreferenceScopeResolutionStrategies {

    private PreferenceScopeFactory scopeFactory;

    public WorkbenchPreferenceScopeResolutionStrategiesImpl() {
    }

    @Inject
    public WorkbenchPreferenceScopeResolutionStrategiesImpl( final PreferenceScopeFactory scopeFactory ) {
        this.scopeFactory = scopeFactory;
    }

    @Override
    public PreferenceScopeResolutionStrategyInfo getUserInfoFor(final String scopeType,
                                                                final String scopeKey ) {
        PreferenceScopeResolutionStrategy scopeResolutionStrategy;

        if ( scopeType != null ) {
            PreferenceScope userScope = scopeFactory.createScope( WorkbenchPreferenceScopes.USER );
            PreferenceScope userScopedScope;

            if ( scopeKey != null ) {
                userScopedScope = scopeFactory.createScope(scopeType, scopeKey );
            } else {
                userScopedScope = scopeFactory.createScope(scopeType );
            }

            PreferenceScope projectUserScope = scopeFactory.createScope(userScope, userScopedScope );
            scopeResolutionStrategy = new WorkbenchPreferenceScopeResolutionStrategy(scopeFactory, projectUserScope );
        } else {
            scopeResolutionStrategy = new WorkbenchPreferenceScopeResolutionStrategy(scopeFactory );
        }

        return scopeResolutionStrategy.getInfo();
    }

    @Override
    public PreferenceScopeResolutionStrategyInfo getSpaceInfoFor(final String scopeKey) {
        PreferenceScopeResolutionStrategy scopeResolutionStrategy;

        if (scopeKey != null) {
            final PreferenceScope spaceScope = scopeFactory.createScope(WorkbenchPreferenceScopes.SPACE, scopeKey);
            scopeResolutionStrategy = new WorkbenchPreferenceScopeResolutionStrategy(scopeFactory, spaceScope);
        } else {
            scopeResolutionStrategy = new WorkbenchPreferenceScopeResolutionStrategy(scopeFactory);
        }

        return scopeResolutionStrategy.getInfo();
    }
}
