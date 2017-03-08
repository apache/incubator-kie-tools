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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.annotations.Customizable;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

@ApplicationScoped
public class PreferenceScopeFactoryImpl implements PreferenceScopeFactory {

    private PreferenceScopeTypes scopeTypes;

    protected PreferenceScopeFactoryImpl() {
    }

    @Inject
    public PreferenceScopeFactoryImpl(@Customizable final PreferenceScopeTypes scopeTypes) {
        this.scopeTypes = scopeTypes;
    }

    @Override
    public PreferenceScope createScope(final String type) throws InvalidPreferenceScopeException {
        return createScopeWithoutKey(type,
                                     null);
    }

    @Override
    public PreferenceScope createScope(final String type,
                                       final PreferenceScope childScope) throws InvalidPreferenceScopeException {
        return createScopeWithoutKey(type,
                                     childScope);
    }

    @Override
    public PreferenceScope createScope(final String type,
                                       final String key) throws InvalidPreferenceScopeException {
        return createScopeWithKey(type,
                                  key,
                                  null);
    }

    @Override
    public PreferenceScope createScope(final String type,
                                       final String key,
                                       final PreferenceScope childScope) throws InvalidPreferenceScopeException {
        return createScopeWithKey(type,
                                  key,
                                  childScope);
    }

    @Override
    public PreferenceScope createScope(final PreferenceScope... scopes) throws InvalidPreferenceScopeException {
        PreferenceScope scope = null;
        PreferenceScope currentScope = null;
        PreferenceScope previousScope = null;

        for (int i = scopes.length - 1; i >= 0; i--) {
            scope = scopes[i];

            currentScope = new PreferenceScopeImpl(scope.type(),
                                                   scope.key(),
                                                   previousScope);
            previousScope = currentScope;
        }

        return currentScope;
    }

    @Override
    public PreferenceScope cloneScope(final PreferenceScope scope) {
        if (scope == null) {
            return null;
        }

        return new PreferenceScopeImpl(scope.type(),
                                       scope.key(),
                                       cloneScope(scope.childScope()));
    }

    private PreferenceScope createScopeWithoutKey(final String type,
                                                  final PreferenceScope childScope) {
        if (scopeTypes.typeRequiresKey(type)) {
            throw new InvalidPreferenceScopeException("This preference scope type requires a key to be built.");
        }

        return new PreferenceScopeImpl(type,
                                       scopeTypes.getDefaultKeyFor(type),
                                       childScope);
    }

    private PreferenceScope createScopeWithKey(final String type,
                                               final String key,
                                               final PreferenceScope childScope) {
        if (!scopeTypes.typeRequiresKey(type)) {
            throw new InvalidPreferenceScopeException("This preference scope type does not require a key to be built.");
        }

        return new PreferenceScopeImpl(type,
                                       key,
                                       childScope);
    }
}
