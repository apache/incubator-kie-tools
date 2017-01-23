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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.impl.PreferenceScopeImpl;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

import static org.junit.Assert.assertEquals;

public class WorkbenchPreferenceScopeResolverTest {

    private WorkbenchPreferenceScopeResolver scopeResolver;

    private PreferenceScopeImpl projectScope;

    private PreferenceScopeImpl userScope;

    private PreferenceScopeImpl globalScope;

    @Before
    public void setup() {
        final List<PreferenceScope> order = new ArrayList<>();

        globalScope = new PreferenceScopeImpl( "global", "global", null );
        userScope = new PreferenceScopeImpl( "user", "my-user", null );
        projectScope = new PreferenceScopeImpl( "user", "my-user", new PreferenceScopeImpl( "project", "my-project", null ) );

        order.add( projectScope );
        order.add( userScope );
        order.add( globalScope );

        scopeResolver = new WorkbenchPreferenceScopeResolver( order );
    }

    @Test
    public void resolveValidScopesTest() {
        assertEquals( projectScope, scopeResolver.resolve( "project" ) );
        assertEquals( userScope, scopeResolver.resolve( "user" ) );
        assertEquals( globalScope, scopeResolver.resolve( "global" ) );
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void tryResolveWithInvalidScopeTypeTest() {
        scopeResolver.resolve( "invalidScopeType" );
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void tryResolveWithNoScopeTypesTest() {
        scopeResolver.resolve();
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void tryResolveWithTwoScopeTypesTest() {
        scopeResolver.resolve( "project", "user" );
    }

}
