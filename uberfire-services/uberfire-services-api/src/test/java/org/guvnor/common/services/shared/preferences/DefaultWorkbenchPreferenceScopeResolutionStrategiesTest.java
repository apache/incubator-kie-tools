/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.shared.preferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultWorkbenchPreferenceScopeResolutionStrategiesTest {

    private DefaultWorkbenchPreferenceScopeResolutionStrategies strategies;

    @Mock
    private PreferenceScopeResolutionStrategy preferenceScopeResolutionStrategy;

    @Before
    public void setup() {
        strategies = spy(new DefaultWorkbenchPreferenceScopeResolutionStrategies(preferenceScopeResolutionStrategy));
    }

    @Test
    public void getUserInfoForTest() {
        strategies.getUserInfoFor(anyString(),
                                  anyString());

        verify(preferenceScopeResolutionStrategy).getInfo();
    }

    @Test
    public void getSpaceInfoForTest() {
        strategies.getSpaceInfoFor(anyString());

        verify(preferenceScopeResolutionStrategy).getInfo();
    }
}
