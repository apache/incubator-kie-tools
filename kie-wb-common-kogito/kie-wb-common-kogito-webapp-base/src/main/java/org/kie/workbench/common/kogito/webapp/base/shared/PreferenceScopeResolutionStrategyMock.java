/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.kogito.webapp.base.shared;

import javax.enterprise.context.Dependent;

import org.uberfire.annotations.Customizable;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceScopeResolver;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

@Dependent
@Customizable
public class PreferenceScopeResolutionStrategyMock implements PreferenceScopeResolutionStrategy {

    @Override
    public PreferenceScopeResolutionStrategyInfo getInfo() {
        return null;
    }

    @Override
    public PreferenceScopeResolver getScopeResolver() {
        return null;
    }
}
