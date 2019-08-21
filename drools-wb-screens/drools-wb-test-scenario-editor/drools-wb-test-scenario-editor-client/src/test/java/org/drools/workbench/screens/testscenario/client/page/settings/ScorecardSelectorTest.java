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

import java.util.HashSet;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.service.ScoreCardServiceLoader;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScorecardSelectorTest {

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

    private ScorecardSelector presenter;

    @Before
    public void setUp() throws Exception {
        doReturn(true).when(authorizationManager).authorize(any(ResourceRef.class),
                                                            eq(ResourceAction.READ),
                                                            any(User.class));
        presenter = new ScorecardSelector(new CallerMock<>(scoreCardServiceLoader),
                                          authorizationManager,
                                          sessionInfo,
                                          view);
    }

    @Test
    public void fillDropdown() {
        final Path path = mock(Path.class);
        final Scenario scenario = new Scenario();
        scenario.setPackageName("org.test");

        final HashSet<String> scoreCardNames = new HashSet<>();
        scoreCardNames.add("1");
        scoreCardNames.add("2");
        doReturn(scoreCardNames).when(scoreCardServiceLoader).find(path,
                                                                   "org.test");

        presenter.init(path,
                       scenario);

        verify(view).clear();
        verify(view).add("- SelectScoreCard -");
        verify(view).add("1");
        verify(view).add("2");
        verify(view, never()).setSelected(anyString());
    }

    @Test
    public void setSelected() {
        final Path path = mock(Path.class);
        final Scenario scenario = new Scenario();
        scenario.setPackageName("org.test");
        scenario.setModelName("selected");

        final HashSet<String> scoreCardNames = new HashSet<>();
        scoreCardNames.add("something");
        scoreCardNames.add("selected");
        doReturn(scoreCardNames).when(scoreCardServiceLoader).find(path,
                                                                   "org.test");

        presenter.init(path,
                       scenario);

        verify(view).setSelected("selected");
    }

    @Test
    public void onValueSelected() {
        final Path path = mock(Path.class);
        final Scenario scenario = new Scenario();
        scenario.setPackageName("org.test");

        final HashSet<String> scoreCardNames = new HashSet<>();
        scoreCardNames.add("something");
        scoreCardNames.add("selected");
        doReturn(scoreCardNames).when(scoreCardServiceLoader).find(path,
                                                                   "org.test");

        presenter.init(path,
                       scenario);

        presenter.onValueSelected("selected");

        assertEquals("selected", scenario.getModelName());
    }
}