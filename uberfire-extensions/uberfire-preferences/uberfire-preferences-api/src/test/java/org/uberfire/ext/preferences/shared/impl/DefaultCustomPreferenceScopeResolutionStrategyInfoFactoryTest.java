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

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.shared.CustomPreferenceScopeResolutionStrategyInfoFactory;
import org.uberfire.ext.preferences.shared.PreferenceScopeResolutionStrategy;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Mockito.*;

public class DefaultCustomPreferenceScopeResolutionStrategyInfoFactoryTest {

    private PreferenceScopeResolutionStrategyInfo defaultScopeResolutionStrategyInfo;

    private PreferenceScopeResolutionStrategy defaultScopeResolutionStrategy;

    private CustomPreferenceScopeResolutionStrategyInfoFactory scopeResolutionStrategyInfoFactory;

    @Before
    public void setup() {
        defaultScopeResolutionStrategyInfo = mock( PreferenceScopeResolutionStrategyInfo.class );
        defaultScopeResolutionStrategy = mock( PreferenceScopeResolutionStrategy.class );
        doReturn( defaultScopeResolutionStrategyInfo ).when( defaultScopeResolutionStrategy ).getInfo();

        scopeResolutionStrategyInfoFactory = new DefaultCustomPreferenceScopeResolutionStrategyInfoFactory( defaultScopeResolutionStrategy );
    }

    @Test
    public void getTest() {
        final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo = scopeResolutionStrategyInfoFactory.create( null );
        assertEquals( defaultScopeResolutionStrategyInfo, scopeResolutionStrategyInfo );
    }
}
