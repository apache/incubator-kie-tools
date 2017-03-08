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

package org.uberfire.preferences.shared.impl;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.annotations.FallbackImplementation;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceScopeResolver;

/**
 * Default implementation for {@link PreferenceScopeResolutionStrategy}. To override it, just
 * provide a default CDI bean that implements {@link PreferenceScopeResolutionStrategy}.
 */
@FallbackImplementation
public class DefaultPreferenceScopeResolutionStrategy implements PreferenceScopeResolutionStrategy {

    private PreferenceScopeResolutionStrategyInfo info;

    private PreferenceScopeResolver resolver;

    protected DefaultPreferenceScopeResolutionStrategy() {
    }

    public DefaultPreferenceScopeResolutionStrategy(final PreferenceScopeFactory scopeFactory,
                                                    final String componentKey) {
        final List<PreferenceScope> order = getScopeOrder(scopeFactory,
                                                          componentKey);
        final PreferenceScope defaultScope = getDefaultScope(order);

        info = new PreferenceScopeResolutionStrategyInfo(order,
                                                         defaultScope);
        resolver = new DefaultPreferenceScopeResolver(order);
    }

    @Override
    public PreferenceScopeResolutionStrategyInfo getInfo() {
        return info;
    }

    @Override
    public PreferenceScopeResolver getScopeResolver() {
        return resolver;
    }

    public PreferenceScope getDefaultScope(final List<PreferenceScope> order) {
        return order.get(0);
    }

    private List<PreferenceScope> getScopeOrder(final PreferenceScopeFactory scopeFactory,
                                                final String componentKey) {
        List<PreferenceScope> order = new ArrayList<>();

        addUserComponentScope(order,
                              scopeFactory,
                              componentKey);
        addUserEntireApplicationScope(order,
                                      scopeFactory);
        addAllUsersComponentScope(order,
                                  scopeFactory,
                                  componentKey);
        addAllUsersEntireApplicationScope(order,
                                          scopeFactory);

        return order;
    }

    private void addUserComponentScope(List<PreferenceScope> order,
                                       final PreferenceScopeFactory scopeFactory,
                                       final String componentKey) {
        if (componentKey != null) {
            final PreferenceScope userScope = scopeFactory.createScope(DefaultScopes.USER.type());
            final PreferenceScope componentScope = scopeFactory.createScope(DefaultScopes.COMPONENT.type(),
                                                                            componentKey);
            order.add(scopeFactory.createScope(userScope,
                                               componentScope));
        }
    }

    private void addUserEntireApplicationScope(List<PreferenceScope> order,
                                               final PreferenceScopeFactory scopeFactory) {
        final PreferenceScope userScope = scopeFactory.createScope(DefaultScopes.USER.type());
        final PreferenceScope entireApplicationScope = scopeFactory.createScope(DefaultScopes.ENTIRE_APPLICATION.type());
        order.add(scopeFactory.createScope(userScope,
                                           entireApplicationScope));
    }

    private void addAllUsersComponentScope(List<PreferenceScope> order,
                                           final PreferenceScopeFactory scopeFactory,
                                           final String componentKey) {
        if (componentKey != null) {
            final PreferenceScope allUsersScope = scopeFactory.createScope(DefaultScopes.ALL_USERS.type());
            final PreferenceScope componentScope = scopeFactory.createScope(DefaultScopes.COMPONENT.type(),
                                                                            componentKey);
            order.add(scopeFactory.createScope(allUsersScope,
                                               componentScope));
        }
    }

    private void addAllUsersEntireApplicationScope(List<PreferenceScope> order,
                                                   final PreferenceScopeFactory scopeFactory) {
        final PreferenceScope allUsersScope = scopeFactory.createScope(DefaultScopes.ALL_USERS.type());
        final PreferenceScope entireApplicationScope = scopeFactory.createScope(DefaultScopes.ENTIRE_APPLICATION.type());
        order.add(scopeFactory.createScope(allUsersScope,
                                           entireApplicationScope));
    }
}