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

package org.uberfire.preferences.shared;

import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

/**
 * Builder for new preference scope creation.
 */
public interface PreferenceScopeFactory {

    /**
     * Builds a new preference scope, that does not require a custom key.
     * @param type Type of the new preference scope.
     * @return A new preference scope.
     * @throws InvalidPreferenceScopeException if the type passed is invalid, or if it requires a key.
     */
    PreferenceScope createScope(String type) throws InvalidPreferenceScopeException;

    /**
     * Builds a new preference scope, that does not require a custom key, and has the passed child scope.
     * @param type Type of the new preference scope.
     * @return A new preference scope.
     * @throws InvalidPreferenceScopeException if the type passed is invalid, or if it requires a key.
     */
    PreferenceScope createScope(String type,
                                PreferenceScope childScope) throws InvalidPreferenceScopeException;

    /**
     * Builds a new preference scope, that requires a custom key.
     * @param type Type of the new preference scope.
     * @param key Key of the new preference scope.
     * @return A new preference scope.
     * @throws InvalidPreferenceScopeException if the type passed is invalid, or if it does not require
     * a key.
     */
    PreferenceScope createScope(String type,
                                String key) throws InvalidPreferenceScopeException;

    /**
     * Builds a new preference scope, that requires a custom key, and has the passed child scope.
     * @param type Type of the new preference scope.
     * @param key Key of the new preference scope.
     * @param childScope Child scope of this scope. It can be null if the scope has no child.
     * @return A new preference scope.
     * @throws InvalidPreferenceScopeException if the type passed is invalid, or if it does not require
     * a key.
     */
    PreferenceScope createScope(String type,
                                String key,
                                PreferenceScope childScope) throws InvalidPreferenceScopeException;

    /**
     * Builds a new preference scope, that contains the following ones as child scopes.
     * @param scopes The first scope is the root scope, the second is its child scope, and so on.
     * @return A new preference scope.
     * @throws InvalidPreferenceScopeException if any passed scope is invalid.
     */
    PreferenceScope createScope(PreferenceScope... scopes) throws InvalidPreferenceScopeException;

    /**
     * Clones the passed scope to a new instance.
     * @param scope Scope to be cloned. Must not be null.
     * @return New scope instance equal to the passed one.
     */
    PreferenceScope cloneScope(PreferenceScope scope);
}
