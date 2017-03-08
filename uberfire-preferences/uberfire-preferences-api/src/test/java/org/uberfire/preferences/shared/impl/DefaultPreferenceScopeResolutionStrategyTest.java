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

import org.junit.Before;
import org.junit.Test;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeTypes;

import static org.junit.Assert.*;

public class DefaultPreferenceScopeResolutionStrategyTest {

    private PreferenceScopeFactory scopesBuilder;

    private PreferenceScopeTypes scopeTypes;

    private DefaultPreferenceScopeResolutionStrategy defaultPreferenceScopeResolutionStrategy;

    @Before
    public void setup() {
        final SessionInfoMock sessionInfo = new SessionInfoMock(DefaultPreferenceScopesForTests.userScopeKey);
        scopeTypes = new DefaultPreferenceScopeTypes(new UsernameProviderMock(sessionInfo));
        scopesBuilder = new PreferenceScopeFactoryImpl(scopeTypes);

        defaultPreferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy(scopesBuilder,
                                                                                                null);
    }

    @Test
    public void defaultOrderTest() {
        final List<PreferenceScope> order = defaultPreferenceScopeResolutionStrategy.getInfo().order();

        assertEquals(2,
                     order.size());

        final PreferenceScope secondScope = order.get(0);
        assertEquals(DefaultPreferenceScopesForTests.userScopeType,
                     secondScope.type());
        assertEquals(DefaultPreferenceScopesForTests.userScopeKey,
                     secondScope.key());
        final PreferenceScope secondScopeChild = secondScope.childScope();
        assertEquals(DefaultPreferenceScopesForTests.entireApplicationScopeType,
                     secondScopeChild.type());
        assertEquals(DefaultPreferenceScopesForTests.entireApplicationScopeKey,
                     secondScopeChild.key());
        assertNull(secondScopeChild.childScope());

        final PreferenceScope fourthScope = order.get(1);
        assertEquals(DefaultPreferenceScopesForTests.allUsersScopeType,
                     fourthScope.type());
        assertEquals(DefaultPreferenceScopesForTests.allUsersScopeKey,
                     fourthScope.key());
        final PreferenceScope fourthScopeChild = fourthScope.childScope();
        assertEquals(DefaultPreferenceScopesForTests.entireApplicationScopeType,
                     fourthScopeChild.type());
        assertEquals(DefaultPreferenceScopesForTests.entireApplicationScopeKey,
                     fourthScopeChild.key());
        assertNull(fourthScopeChild.childScope());
    }

    @Test
    public void orderWithComponentScopeTest() {
        defaultPreferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy(scopesBuilder,
                                                                                                DefaultPreferenceScopesForTests.componentScopeKey);
        final List<PreferenceScope> order = defaultPreferenceScopeResolutionStrategy.getInfo().order();

        assertEquals(4,
                     order.size());

        final PreferenceScope firstScope = order.get(0);
        assertEquals(DefaultPreferenceScopesForTests.userScopeType,
                     firstScope.type());
        assertEquals(DefaultPreferenceScopesForTests.userScopeKey,
                     firstScope.key());
        final PreferenceScope firstScopeChild = firstScope.childScope();
        assertEquals(DefaultPreferenceScopesForTests.componentScopeType,
                     firstScopeChild.type());
        assertEquals(DefaultPreferenceScopesForTests.componentScopeKey,
                     firstScopeChild.key());
        assertNull(firstScopeChild.childScope());

        final PreferenceScope secondScope = order.get(1);
        assertEquals(DefaultPreferenceScopesForTests.userScopeType,
                     secondScope.type());
        assertEquals(DefaultPreferenceScopesForTests.userScopeKey,
                     secondScope.key());
        final PreferenceScope secondScopeChild = secondScope.childScope();
        assertEquals(DefaultPreferenceScopesForTests.entireApplicationScopeType,
                     secondScopeChild.type());
        assertEquals(DefaultPreferenceScopesForTests.entireApplicationScopeKey,
                     secondScopeChild.key());
        assertNull(secondScopeChild.childScope());

        final PreferenceScope thirdScope = order.get(2);
        assertEquals(DefaultPreferenceScopesForTests.allUsersScopeType,
                     thirdScope.type());
        assertEquals(DefaultPreferenceScopesForTests.allUsersScopeKey,
                     thirdScope.key());
        final PreferenceScope thirdScopeChild = thirdScope.childScope();
        assertEquals(DefaultPreferenceScopesForTests.componentScopeType,
                     thirdScopeChild.type());
        assertEquals(DefaultPreferenceScopesForTests.componentScopeKey,
                     thirdScopeChild.key());
        assertNull(thirdScopeChild.childScope());

        final PreferenceScope fourthScope = order.get(3);
        assertEquals(DefaultPreferenceScopesForTests.allUsersScopeType,
                     fourthScope.type());
        assertEquals(DefaultPreferenceScopesForTests.allUsersScopeKey,
                     fourthScope.key());
        final PreferenceScope fourthScopeChild = fourthScope.childScope();
        assertEquals(DefaultPreferenceScopesForTests.entireApplicationScopeType,
                     fourthScopeChild.type());
        assertEquals(DefaultPreferenceScopesForTests.entireApplicationScopeKey,
                     fourthScopeChild.key());
        assertNull(fourthScopeChild.childScope());
    }

    @Test
    public void defaultScopeTest() {
        final PreferenceScope defaultScope = defaultPreferenceScopeResolutionStrategy.getInfo().defaultScope();

        assertEquals(DefaultPreferenceScopesForTests.userScopeType,
                     defaultScope.type());
        assertEquals(DefaultPreferenceScopesForTests.userScopeKey,
                     defaultScope.key());
        final PreferenceScope defaultScopeChild = defaultScope.childScope();
        assertEquals(DefaultPreferenceScopesForTests.entireApplicationScopeType,
                     defaultScopeChild.type());
        assertEquals(DefaultPreferenceScopesForTests.entireApplicationScopeKey,
                     defaultScopeChild.key());
        assertNull(defaultScopeChild.childScope());
    }
}
