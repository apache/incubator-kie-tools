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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.annotations.FallbackImplementation;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.UsernameProvider;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

/**
 * Default implementation for {@link PreferenceScopeTypes}. To override it, just provide a default
 * CDI bean that implements {@link PreferenceScopeTypes}.
 */
@ApplicationScoped
@FallbackImplementation
public class DefaultPreferenceScopeTypes implements PreferenceScopeTypes {

    private Map<String, DefaultKey> defaultKeyByType;

    private UsernameProvider usernameProvider;

    protected DefaultPreferenceScopeTypes() {
    }

    @Inject
    public DefaultPreferenceScopeTypes(final UsernameProvider usernameProvider) {
        this.usernameProvider = usernameProvider;

        defaultKeyByType = new HashMap<>();
        defaultKeyByType.put(DefaultScopes.ALL_USERS.type(),
                             DefaultScopes.ALL_USERS::type);
        defaultKeyByType.put(DefaultScopes.ENTIRE_APPLICATION.type(),
                             DefaultScopes.ENTIRE_APPLICATION::type);
        defaultKeyByType.put(DefaultScopes.COMPONENT.type(),
                             null);
        defaultKeyByType.put(DefaultScopes.USER.type(),
                             usernameProvider::get);
    }

    @Override
    public boolean typeRequiresKey(final String type) throws InvalidPreferenceScopeException {
        validateType(type);

        return defaultKeyByType.get(type) == null;
    }

    @Override
    public String getDefaultKeyFor(final String type) throws InvalidPreferenceScopeException {
        validateType(type);

        final DefaultKey defaultKey = defaultKeyByType.get(type);

        if (defaultKey == null) {
            throw new InvalidPreferenceScopeException("The type " + type + " does not have a default key.");
        }

        return defaultKey.get();
    }

    protected void validateType(final String type) throws InvalidPreferenceScopeException {
        if (isEmpty(type)) {
            throw new InvalidPreferenceScopeException("Type must be a non empty string.");
        }

        if (!defaultKeyByType.containsKey(type)) {
            throw new InvalidPreferenceScopeException("Invalid preference scope type.");
        }
    }

    protected boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    protected interface DefaultKey {

        String get();
    }
}