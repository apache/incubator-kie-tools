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
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.PreferenceScopeValidator;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

@ApplicationScoped
public class PreferenceScopeValidatorImpl implements PreferenceScopeValidator {

    private PreferenceScopeTypes scopeTypes;

    private PreferenceScopeResolutionStrategy scopeResolutionStrategy;

    protected PreferenceScopeValidatorImpl() {
    }

    @Inject
    public PreferenceScopeValidatorImpl(@Customizable final PreferenceScopeTypes scopeTypes,
                                        @Customizable final PreferenceScopeResolutionStrategy scopeResolutionStrategy) {
        this.scopeTypes = scopeTypes;
        this.scopeResolutionStrategy = scopeResolutionStrategy;
    }

    @Override
    public void validate(final PreferenceScope scope) throws InvalidPreferenceScopeException {
        if (scope == null) {
            throw new InvalidPreferenceScopeException("A root scope must not be null.");
        }

        for (PreferenceScope currentScope = scope; currentScope != null; currentScope = currentScope.childScope()) {
            final String type = currentScope.type();
            final String key = currentScope.key();

            if (scopeTypes.typeRequiresKey(type) && isEmpty(key)) {
                throw new InvalidPreferenceScopeException("The type " + type + " must be associated with a non empty key.");
            }
        }

        if (!scopeResolutionStrategy.getInfo().order().contains(scope)) {
            throw new InvalidPreferenceScopeException("This scope is not defined in the scope hierarchy.");
        }
    }

    protected boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
