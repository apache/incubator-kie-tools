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
package org.drools.workbench.screens.testscenario.client.page.settings;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.service.ScoreCardServiceLoader;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mocks.CallerMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScorecardSelectorHideTest {

    @Mock
    private TestScenarioConstants constants;

    @Mock
    private ScoreCardServiceLoader scoreCardServiceLoader;
    @Mock
    private AuthorizationManager authorizationManager;
    @Mock
    private SessionInfo sessionInfo;
    @Mock
    private ScorecardSelectorView view;

    @Test
    public void show() {
        doReturn(false).when(authorizationManager).authorize(Mockito.<ResourceRef>any(),
                                                             eq(ResourceAction.READ),
                                                             any());
        new ScorecardSelector(new CallerMock<>(scoreCardServiceLoader),
                              authorizationManager,
                              sessionInfo,
                              view);
        verify(view).hide();
    }

    @Test
    public void hide() {
        doReturn(true).when(authorizationManager).authorize(Mockito.<ResourceRef>any(),
                                                            eq(ResourceAction.READ),
                                                            any());
        new ScorecardSelector(new CallerMock<>(scoreCardServiceLoader),
                              authorizationManager,
                              sessionInfo,
                              view);
        verify(view, never()).hide();
    }
}