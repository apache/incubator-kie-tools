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
 * Provides a simple validator method for preference scopes.
 */
public interface PreferenceScopeValidator {

    /**
     * Checks if a scope is valid.
     * @param scope Scope to check.
     * @throws InvalidPreferenceScopeException if the type passed is invalid, or if the scope's key
     * is null and a key is required for that type, or if the key is not null and the scope's type
     * does not require one.
     */
    void validate(PreferenceScope scope) throws InvalidPreferenceScopeException;
}
