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

package org.uberfire.ext.preferences.shared.impl;

import java.util.Map;
import javax.inject.Inject;

import org.uberfire.annotations.Customizable;
import org.uberfire.annotations.FallbackImplementation;
import org.uberfire.ext.preferences.shared.CustomPreferenceScopeResolutionStrategyInfoFactory;
import org.uberfire.ext.preferences.shared.PreferenceScopeResolutionStrategy;

@FallbackImplementation
public class DefaultCustomPreferenceScopeResolutionStrategyInfoFactory implements CustomPreferenceScopeResolutionStrategyInfoFactory {

    private PreferenceScopeResolutionStrategy defaultScopeResolutionStrategy;

    public DefaultCustomPreferenceScopeResolutionStrategyInfoFactory() {
    }

    @Inject
    public DefaultCustomPreferenceScopeResolutionStrategyInfoFactory( @Customizable final PreferenceScopeResolutionStrategy defaultScopeResolutionStrategy ) {
        this.defaultScopeResolutionStrategy = defaultScopeResolutionStrategy;
    }

    @Override
    public PreferenceScopeResolutionStrategyInfo create( final Map<String, String> params ) {
        return defaultScopeResolutionStrategy.getInfo();
    }
}
