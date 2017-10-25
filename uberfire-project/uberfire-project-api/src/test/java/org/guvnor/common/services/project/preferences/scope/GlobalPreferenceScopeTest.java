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

import org.junit.Before;
import org.junit.Test;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.PreferenceScopeFactoryImpl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GlobalPreferenceScopeTest {

    private PreferenceScopeTypes scopeTypes;

    private PreferenceScopeFactory scopeFactory;

    private GlobalPreferenceScope globalScope;

    @Before
    public void setup() {
        scopeTypes = mock(PreferenceScopeTypes.class);
        doReturn("global").when(scopeTypes).getDefaultKeyFor("global");
        doReturn(false).when(scopeTypes).typeRequiresKey("global");

        scopeFactory = new PreferenceScopeFactoryImpl(scopeTypes);

        globalScope = new GlobalPreferenceScope(scopeFactory);
    }

    @Test
    public void globalScopeIsResolvedCorrectlyTest() {
        final PreferenceScope resolvedGlobalScope = globalScope.resolve();

        assertEquals("global",
                     resolvedGlobalScope.type());
        assertEquals("global",
                     resolvedGlobalScope.key());
        assertNull(resolvedGlobalScope.childScope());
    }
}
