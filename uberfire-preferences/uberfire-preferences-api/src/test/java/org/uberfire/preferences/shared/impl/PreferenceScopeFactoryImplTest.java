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

import org.junit.Before;
import org.junit.Test;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.entireApplicationScope;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.userScope;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.userScopeKey;

public class PreferenceScopeFactoryImplTest {

    private PreferenceScopeFactoryImpl scopeFactory;

    private PreferenceScope genericScope;

    @Before
    public void setup() {
        genericScope = mock(PreferenceScope.class);

        final SessionInfoMock sessionInfo = new SessionInfoMock();
        final PreferenceScopeTypes scopeTypes = new DefaultPreferenceScopeTypes(new UsernameProviderMock(sessionInfo));
        scopeFactory = new PreferenceScopeFactoryImpl(scopeTypes);
    }

    @Test
    public void createScopeByTypeWithDefaultKeyTest() {
        PreferenceScope userScope = scopeFactory.createScope(DefaultScopes.USER.type());
        assertEquals(DefaultScopes.USER.type(),
                     userScope.type());
        assertEquals("admin",
                     userScope.key());
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void createScopeByTypeWithoutDefaultKeyTest() {
        scopeFactory.createScope(DefaultScopes.COMPONENT.type());
    }

    @Test
    public void createScopeByTypeAndKeyWithoutDefaultKeyTest() {
        PreferenceScope componentScope = scopeFactory.createScope(DefaultScopes.COMPONENT.type(),
                                                                  "my-component");
        assertEquals(DefaultScopes.COMPONENT.type(),
                     componentScope.type());
        assertEquals("my-component",
                     componentScope.key());
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void createScopeByTypeAndKeyWithDefaultKeyTest() {
        scopeFactory.createScope(DefaultScopes.USER.type(),
                                 "user");
    }

    @Test
    public void createScopeByTypeAndChildScopeWithDefaultKeyTest() {
        PreferenceScope userScope = scopeFactory.createScope(DefaultScopes.USER.type(),
                                                             genericScope);
        assertEquals(DefaultScopes.USER.type(),
                     userScope.type());
        assertEquals("admin",
                     userScope.key());
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void createScopeByTypeAndChildScopeWithoutDefaultKeyTest() {
        scopeFactory.createScope(DefaultScopes.COMPONENT.type(),
                                 genericScope);
    }

    @Test
    public void createScopeByTypeKeyAndChildScopeWithoutDefaultKeyTest() {
        PreferenceScope componentScope = scopeFactory.createScope(DefaultScopes.COMPONENT.type(),
                                                                  "my-component",
                                                                  genericScope);
        assertEquals(DefaultScopes.COMPONENT.type(),
                     componentScope.type());
        assertEquals("my-component",
                     componentScope.key());
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void createScopeByTypeKeyAndChildScopeWithDefaultKeyTest() {
        scopeFactory.createScope(DefaultScopes.USER.type(),
                                 "user",
                                 genericScope);
    }

    @Test
    public void createHierarchicalScopeByScopesTest() {
        PreferenceScope scope = scopeFactory.createScope(userScope,
                                                         entireApplicationScope);

        assertEquals(DefaultScopes.USER.type(),
                     scope.type());
        assertEquals(userScopeKey,
                     scope.key());

        final PreferenceScope childScope = scope.childScope();
        assertEquals(DefaultScopes.ENTIRE_APPLICATION.type(),
                     childScope.type());
        assertEquals(DefaultScopes.ENTIRE_APPLICATION.type(),
                     childScope.key());
        assertNull(childScope.childScope());
    }

    @Test
    public void cloneScopeTest() {
        PreferenceScope scope = scopeFactory.createScope(userScope,
                                                         entireApplicationScope);
        PreferenceScope newScope = scopeFactory.cloneScope(scope);

        assertTrue(scope != newScope);
        assertEquals(scope,
                     newScope);
    }
}
