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

package org.kie.workbench.common.services.shared.preferences.config;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.preferences.shared.UsernameProvider;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class WorkbenchPreferenceScopeTypesTest {

    private WorkbenchPreferenceScopeTypes scopeTypes;

    @Before
    public void setup() {
        final SessionInfo sessionInfo = new SessionInfoMock();
        final UsernameProvider usernameProvider = mock(UsernameProvider.class);
        doReturn(sessionInfo.getIdentity().getIdentifier()).when(usernameProvider).get();
        scopeTypes = new WorkbenchPreferenceScopeTypes(usernameProvider);
    }

    @Test
    public void typesRequireKeyTest() {
        assertFalse(scopeTypes.typeRequiresKey(WorkbenchPreferenceScopes.GLOBAL));
        assertFalse(scopeTypes.typeRequiresKey(WorkbenchPreferenceScopes.USER));
        assertTrue(scopeTypes.typeRequiresKey(WorkbenchPreferenceScopes.PROJECT));
        assertTrue(scopeTypes.typeRequiresKey(WorkbenchPreferenceScopes.SPACE));
    }

    @Test
    public void defaultKeysForTypesTest() {
        assertEquals(WorkbenchPreferenceScopes.GLOBAL, scopeTypes.getDefaultKeyFor(WorkbenchPreferenceScopes.GLOBAL));
        assertEquals("admin", scopeTypes.getDefaultKeyFor(WorkbenchPreferenceScopes.USER));
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void defaultKeyForProjectTest() {
        scopeTypes.getDefaultKeyFor(WorkbenchPreferenceScopes.PROJECT);
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void defaultKeyForSpaceTest() {
        scopeTypes.getDefaultKeyFor(WorkbenchPreferenceScopes.SPACE);
    }

    @Test
    public void isEmptyTest() {
        assertTrue(scopeTypes.isEmpty(null));
        assertTrue(scopeTypes.isEmpty(""));
        assertTrue(scopeTypes.isEmpty("  "));
        assertFalse(scopeTypes.isEmpty("anyString"));
    }
}
