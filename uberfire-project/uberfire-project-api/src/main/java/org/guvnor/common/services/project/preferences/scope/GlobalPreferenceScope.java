/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.project.preferences.scope;

import javax.inject.Inject;

import org.guvnor.common.services.shared.preferences.GuvnorPreferenceScopes;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.bean.PreferenceScopeBean;

public class GlobalPreferenceScope implements PreferenceScopeBean {

    private PreferenceScopeFactory scopeFactory;

    @Inject
    public GlobalPreferenceScope(final PreferenceScopeFactory scopeFactory) {
        this.scopeFactory = scopeFactory;
    }

    @Override
    public PreferenceScope resolve() {
        return scopeFactory.createScope(GuvnorPreferenceScopes.GLOBAL);
    }
}
