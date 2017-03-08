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
 * Defines which scopes are supported by the preference API.
 * If no backend implementation for this class exists, a default one will be provided.
 */
public interface PreferenceScopeTypes {

    /**
     * Checks if a scope type requires a custom key.
     * @param type Type to check.
     * @return true if the type requires a custom key or false if it has a default one.
     * @throws InvalidPreferenceScopeException if the type passed is invalid.
     */
    boolean typeRequiresKey(String type) throws InvalidPreferenceScopeException;

    /**
     * Returns the default key for a type. Throws a exception if the type requires a custom one.
     * @param type Type to check.
     * @return Default key for the type (or null if there is not one).
     * @throws InvalidPreferenceScopeException if the type passed is invalid.
     */
    String getDefaultKeyFor(String type) throws InvalidPreferenceScopeException;
}
