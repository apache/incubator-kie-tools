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
 * The pair (type, key) represents a unique preference scope, which groups a set of preferences
 * and its values.
 */
public interface PreferenceScope {

    /**
     * @return Type of the preference scope. Represents a unique category of scopes.
     */
    String type();

    /**
     * @return Key of the preference scope. Represents a unique key inside a scope type.
     */
    String key();

    /**
     * Child scope, a scope inside a scope. This allows a hierarchy inside each scope.
     * @return Child scope. This can be null, indicating the end of the hierarchy.
     */
    PreferenceScope childScope();
}
