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

import java.util.List;

import org.guvnor.common.services.shared.preferences.WorkbenchPreferenceScopeResolutionStrategies;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.UsernameProvider;
import org.uberfire.preferences.shared.impl.PreferenceScopeFactoryImpl;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class WorkbenchPreferenceScopeResolutionStrategiesImplTest {

    private PreferenceScopeFactory scopesFactory;

    private PreferenceScopeTypes scopeTypes;

    private WorkbenchPreferenceScopeResolutionStrategies scopeResolutionStrategies;

    @Before
    public void setup() {
        final UsernameProvider usernameProvider = mock(UsernameProvider.class);
        scopeTypes = new WorkbenchPreferenceScopeTypes(usernameProvider);
        scopesFactory = new PreferenceScopeFactoryImpl(scopeTypes);

        scopeResolutionStrategies = new WorkbenchPreferenceScopeResolutionStrategiesImpl(scopesFactory);
    }

    @Test
    public void getInfoTest() {
        final PreferenceScopeResolutionStrategyInfo scopeInfo = scopeResolutionStrategies.getUserInfoFor(null,
                                                                                                         null);

        final List<PreferenceScope> order = scopeInfo.order();

        assertEquals(2, order.size());

        final PreferenceScope firstScope = order.get(0);
        assertEquals("user", firstScope.type());

        final PreferenceScope secondScope = order.get(1);
        assertEquals("global", secondScope.type());
        assertEquals("global", secondScope.key());

        final PreferenceScope defaultScope = scopeInfo.defaultScope();

        assertEquals("user", defaultScope.type());
    }

    @Test
    public void getInfoWithScopeTest() {
        final PreferenceScopeResolutionStrategyInfo scopeInfo = scopeResolutionStrategies.getUserInfoFor(WorkbenchPreferenceScopes.PROJECT,
                                                                                                         "my-project");

        final List<PreferenceScope> order = scopeInfo.order();

        assertEquals(3, order.size());

        final PreferenceScope firstScope = order.get(0);
        assertEquals("user", firstScope.type());
        assertEquals("project", firstScope.childScope().type());
        assertEquals("my-project", firstScope.childScope().key());

        final PreferenceScope secondScope = order.get(1);
        assertEquals("user", secondScope.type());

        final PreferenceScope threeScope = order.get(2);
        assertEquals("global", threeScope.type());
        assertEquals("global", threeScope.key());

        final PreferenceScope defaultScope = scopeInfo.defaultScope();

        assertEquals("user", defaultScope.type());
        assertEquals("project", defaultScope.childScope().type());
        assertEquals("my-project", defaultScope.childScope().key());
    }

    @Test
    public void getSpaceInfoTest() {
        final PreferenceScopeResolutionStrategyInfo scopeInfo = scopeResolutionStrategies.getSpaceInfoFor(null);

        final List<PreferenceScope> order = scopeInfo.order();

        assertEquals(2, order.size());

        final PreferenceScope firstScope = order.get(0);
        assertEquals("user", firstScope.type());

        final PreferenceScope secondScope = order.get(1);
        assertEquals("global", secondScope.type());
        assertEquals("global", secondScope.key());

        final PreferenceScope defaultScope = scopeInfo.defaultScope();

        assertEquals("user", defaultScope.type());
    }

    @Test
    public void getSpaceInfoWithScopeTest() {
        final PreferenceScopeResolutionStrategyInfo scopeInfo = scopeResolutionStrategies.getSpaceInfoFor("mySpace");

        final List<PreferenceScope> order = scopeInfo.order();

        assertEquals(3, order.size());

        final PreferenceScope firstScope = order.get(0);
        assertEquals("space", firstScope.type());
        assertEquals("mySpace", firstScope.key());

        final PreferenceScope secondScope = order.get(1);
        assertEquals("user", secondScope.type());

        final PreferenceScope thirdScope = order.get(2);
        assertEquals("global", thirdScope.type());
        assertEquals("global", thirdScope.key());

        final PreferenceScope defaultScope = scopeInfo.defaultScope();

        assertEquals("space", defaultScope.type());
        assertEquals("mySpace", defaultScope.key());
    }
}
