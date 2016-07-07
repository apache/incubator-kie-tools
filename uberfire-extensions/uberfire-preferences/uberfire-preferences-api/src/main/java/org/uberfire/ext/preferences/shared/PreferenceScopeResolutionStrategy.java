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

import java.util.List;

import org.uberfire.commons.lifecycle.Disposable;

/**
 * Defines the hierarchy used to resolve a preference value.
 * If no backend implementation for this class exists, a default one will be provided.
 */
public interface PreferenceScopeResolutionStrategy {

    /**
     * Defines the order of scopes that will be used when a preference value is searched.
     * @return Scope order.
     */
    List<PreferenceScope> order();

    /**
     * Defines the default scope that will be used to persist a preference, when none is passed.
     * @return Default scope.
     */
    PreferenceScope defaultScope();
}
