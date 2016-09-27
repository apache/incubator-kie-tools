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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.PreferenceScopeFactory;
import org.uberfire.ext.preferences.shared.PreferenceScopeTypes;
import org.uberfire.mocks.SessionInfoMock;

import static org.junit.Assert.*;
import static org.uberfire.ext.preferences.shared.impl.DefaultPreferenceScopesForTests.*;

public class DefaultPreferenceScopeResolutionStrategyTest {

    private PreferenceScopeFactory scopesBuilder;

    private PreferenceScopeTypes scopeTypes;

    private DefaultPreferenceScopeResolutionStrategy defaultPreferenceScopeResolutionStrategy;

    @Before
    public void setup() {
        scopeTypes = new DefaultPreferenceScopeTypes( new SessionInfoMock( userScopeKey ) );
        scopesBuilder = new PreferenceScopeFactoryImpl( scopeTypes );

        defaultPreferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy( scopesBuilder, null );
    }

    @Test
    public void defaultOrderTest() {
        final List<PreferenceScope> order = defaultPreferenceScopeResolutionStrategy.getInfo().order();

        assertEquals( 2, order.size() );

        final PreferenceScope secondScope = order.get( 0 );
        assertEquals( userScopeType, secondScope.type() );
        assertEquals( userScopeKey, secondScope.key() );
        final PreferenceScope secondScopeChild = secondScope.childScope();
        assertEquals( entireApplicationScopeType, secondScopeChild.type() );
        assertEquals( entireApplicationScopeKey, secondScopeChild.key() );
        assertNull( secondScopeChild.childScope() );

        final PreferenceScope fourthScope = order.get( 1 );
        assertEquals( allUsersScopeType, fourthScope.type() );
        assertEquals( allUsersScopeKey, fourthScope.key() );
        final PreferenceScope fourthScopeChild = fourthScope.childScope();
        assertEquals( entireApplicationScopeType, fourthScopeChild.type() );
        assertEquals( entireApplicationScopeKey, fourthScopeChild.key() );
        assertNull( fourthScopeChild.childScope() );
    }

    @Test
    public void orderWithComponentScopeTest() {
        defaultPreferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy( scopesBuilder, componentScopeKey );
        final List<PreferenceScope> order = defaultPreferenceScopeResolutionStrategy.getInfo().order();

        assertEquals( 4, order.size() );

        final PreferenceScope firstScope = order.get( 0 );
        assertEquals( userScopeType, firstScope.type() );
        assertEquals( userScopeKey, firstScope.key() );
        final PreferenceScope firstScopeChild = firstScope.childScope();
        assertEquals( componentScopeType, firstScopeChild.type() );
        assertEquals( componentScopeKey, firstScopeChild.key() );
        assertNull( firstScopeChild.childScope() );

        final PreferenceScope secondScope = order.get( 1 );
        assertEquals( userScopeType, secondScope.type() );
        assertEquals( userScopeKey, secondScope.key() );
        final PreferenceScope secondScopeChild = secondScope.childScope();
        assertEquals( entireApplicationScopeType, secondScopeChild.type() );
        assertEquals( entireApplicationScopeKey, secondScopeChild.key() );
        assertNull( secondScopeChild.childScope() );

        final PreferenceScope thirdScope = order.get( 2 );
        assertEquals( allUsersScopeType, thirdScope.type() );
        assertEquals( allUsersScopeKey, thirdScope.key() );
        final PreferenceScope thirdScopeChild = thirdScope.childScope();
        assertEquals( componentScopeType, thirdScopeChild.type() );
        assertEquals( componentScopeKey, thirdScopeChild.key() );
        assertNull( thirdScopeChild.childScope() );

        final PreferenceScope fourthScope = order.get( 3 );
        assertEquals( allUsersScopeType, fourthScope.type() );
        assertEquals( allUsersScopeKey, fourthScope.key() );
        final PreferenceScope fourthScopeChild = fourthScope.childScope();
        assertEquals( entireApplicationScopeType, fourthScopeChild.type() );
        assertEquals( entireApplicationScopeKey, fourthScopeChild.key() );
        assertNull( fourthScopeChild.childScope() );
    }

    @Test
    public void defaultScopeTest() {
        final PreferenceScope defaultScope = defaultPreferenceScopeResolutionStrategy.getInfo().defaultScope();

        assertEquals( userScopeType, defaultScope.type() );
        assertEquals( userScopeKey, defaultScope.key() );
        final PreferenceScope defaultScopeChild = defaultScope.childScope();
        assertEquals( entireApplicationScopeType, defaultScopeChild.type() );
        assertEquals( entireApplicationScopeKey, defaultScopeChild.key() );
        assertNull( defaultScopeChild.childScope() );
    }
}
