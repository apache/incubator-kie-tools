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

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceScopeResolver;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

/**
 * Default scope resolver for {@link DefaultPreferenceScopeResolutionStrategy}. If you use another
 * {@link PreferenceScopeResolutionStrategy} implementation, you should provide your own portable resolver
 * when implementing {@link PreferenceScopeResolutionStrategy#getScopeResolver() getScopeResolver}.
 */
@Portable
public class DefaultPreferenceScopeResolver implements PreferenceScopeResolver {

    private final List<PreferenceScope> order;

    public DefaultPreferenceScopeResolver(@MapsTo("order") final List<PreferenceScope> order) {
        this.order = order;
    }

    @Override
    public PreferenceScope resolve(final String... scopeTypes) {
        if (scopeTypes != null && scopeTypes.length >= 1 && scopeTypes.length <= 2) {
            if (scopeTypes.length == 2) {
                return getScopeFromOrder(scopeTypes);
            }

            String scopeType = scopeTypes[0];

            if (scopeType.equals(DefaultScopes.USER.type())) {
                return getScopeFromOrder(DefaultScopes.USER.type(),
                                         DefaultScopes.ENTIRE_APPLICATION.type());
            } else if (scopeType.equals(DefaultScopes.ALL_USERS.type())) {
                return getScopeFromOrder(DefaultScopes.ALL_USERS.type(),
                                         DefaultScopes.ENTIRE_APPLICATION.type());
            } else if (scopeType.equals(DefaultScopes.COMPONENT.type())) {
                return getScopeFromOrder(DefaultScopes.ALL_USERS.type(),
                                         DefaultScopes.COMPONENT.type());
            } else if (scopeType.equals(DefaultScopes.ENTIRE_APPLICATION.type())) {
                return getScopeFromOrder(DefaultScopes.ALL_USERS.type(),
                                         DefaultScopes.ENTIRE_APPLICATION.type());
            }
        }

        throw new InvalidPreferenceScopeException("The passed scope types are invalid.");
    }

    private PreferenceScope getScopeFromOrder(final String... scopeTypes) {
        for (PreferenceScope scope : order) {
            boolean match = true;
            PreferenceScope currentScope = scope;

            for (int i = 0; i < scopeTypes.length; i++) {
                if (currentScope == null || !currentScope.type().equals(scopeTypes[i])) {
                    match = false;
                    break;
                }

                currentScope = currentScope.childScope();
            }

            if (match && currentScope == null) {
                return scope;
            }
        }

        throw new InvalidPreferenceScopeException("The passed scope types are invalid.");
    }
}
