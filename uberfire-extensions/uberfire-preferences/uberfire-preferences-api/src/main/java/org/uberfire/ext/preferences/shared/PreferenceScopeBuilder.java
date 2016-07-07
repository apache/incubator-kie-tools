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

package org.uberfire.ext.preferences.shared;

import org.uberfire.ext.preferences.shared.impl.exception.InvalidPreferenceScopeException;

/**
 * Builder for new preference scope creation.
 */
public interface PreferenceScopeBuilder {

    /**
     * Builds a new preference scope, that does not require a custom key.
     * If the type passed requires a custom key, a exception is thrown.
     * @param type Type of the new preference scope.
     * @return A new preference scope.
     * @throws InvalidPreferenceScopeException if the type passed is invalid (validation made by the
     * {@link PreferenceScopeTypes#validate(PreferenceScope) validate} method ), or if it requires a key.
     */
    PreferenceScope build( final String type ) throws InvalidPreferenceScopeException;


    /**
     * Builds a new preference scope, that requires a custom key.
     * If the type passed does not require a custom key, a exception is thrown.
     * @param type Type of the new preference scope.
     * @param key Key of the new preference scope.
     * @return A new preference scope.
     * @throws InvalidPreferenceScopeException if the type passed is invalid (validation made by the
     * {@link PreferenceScopeTypes#validate(PreferenceScope) validate} method ), or if it does not require a key.
     */
    PreferenceScope build( final String type,
                           final String key ) throws InvalidPreferenceScopeException;
}
