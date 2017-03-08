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

/**
 * Provides a way to obtain scopes related to a scope resolution order.
 */
public interface PreferenceScopeResolver {

    /**
     * Returns a PreferenceScope that belongs to the resolution strategy hierarchy, based on the passed
     * types.
     * @param scopeTypes Scope types associated to the desired scope.
     * @return The PreferenceScope related to the passed types (if it exists, null otherwise).
     */
    PreferenceScope resolve(String... scopeTypes);
}
