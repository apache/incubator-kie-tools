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
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

import static org.junit.Assert.*;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.allUsersComponentScope;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.allUsersEntireApplicationScope;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.allUsersScopeType;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.componentScopeType;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.defaultOrder;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.entireApplicationScopeType;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.userComponentScope;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.userEntireApplicationScope;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.userScopeType;

public class DefaultPreferenceScopeResolverTest {

    private DefaultPreferenceScopeResolver scopeResolver;

    @Before
    public void setup() {
        scopeResolver = new DefaultPreferenceScopeResolver(defaultOrder);
    }

    @Test
    public void resolveValidScopesTest() {
        assertEquals(userEntireApplicationScope,
                     scopeResolver.resolve(userScopeType));
        assertEquals(allUsersComponentScope,
                     scopeResolver.resolve(componentScopeType));
        assertEquals(allUsersEntireApplicationScope,
                     scopeResolver.resolve(allUsersScopeType));
        assertEquals(allUsersEntireApplicationScope,
                     scopeResolver.resolve(entireApplicationScopeType));

        assertEquals(userComponentScope,
                     scopeResolver.resolve(userScopeType,
                                           componentScopeType));
        assertEquals(userEntireApplicationScope,
                     scopeResolver.resolve(userScopeType,
                                           entireApplicationScopeType));
        assertEquals(allUsersComponentScope,
                     scopeResolver.resolve(allUsersScopeType,
                                           componentScopeType));
        assertEquals(allUsersEntireApplicationScope,
                     scopeResolver.resolve(allUsersScopeType,
                                           entireApplicationScopeType));
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void tryResolveWithInvalidScopeTypeTest() {
        scopeResolver.resolve("invalidScopeType");
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void tryResolveWithNoScopeTypesTest() {
        scopeResolver.resolve();
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void tryResolveWithThreeScopeTypesTest() {
        scopeResolver.resolve(userScopeType,
                              componentScopeType,
                              entireApplicationScopeType);
    }
}
