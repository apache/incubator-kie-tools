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

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.preferences.shared.PreferenceScope;

/**
 * Holds all necessary information to the scope resolution strategy.
 */
@Portable
public class PreferenceScopeResolutionStrategyInfo {

    /**
     * Defines the order of scopes that will be used when a preference value is searched.
     */
    private final List<PreferenceScope> order;

    /**
     * Defines the default scope that will be used to persist a preference, when none is passed.
     */
    private final PreferenceScope defaultScope;

    public PreferenceScopeResolutionStrategyInfo(@MapsTo("order") final List<PreferenceScope> order,
                                                 @MapsTo("defaultScope") final PreferenceScope defaultScope) {
        this.order = order;
        this.defaultScope = defaultScope;
    }

    public List<PreferenceScope> order() {
        return order;
    }

    public PreferenceScope defaultScope() {
        return defaultScope;
    }
}
