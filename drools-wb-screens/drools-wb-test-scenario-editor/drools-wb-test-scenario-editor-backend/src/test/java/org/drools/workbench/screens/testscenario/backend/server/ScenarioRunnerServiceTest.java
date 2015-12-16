/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.backend.server;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import javax.enterprise.event.Event;

import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieSession;
import org.kie.workbench.common.services.backend.session.SessionService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioRunnerServiceTest {

    private ScenarioRunnerService service;

    @Mock
    private KieSession defaultPseudoClockKieSession;

    @Mock
    private SessionService sessionService;

    @Mock
    ScenarioLoader scenarioLoader;

    private TestResultMessageEventMock defaultTestResultMessageEvent;

    @Before
    public void setUp() throws Exception {
        ConfigurationService configurationService = mock(ConfigurationService.class);
        KieProjectService projectService = mock(KieProjectService.class);
        User identity = mock(User.class);

        defaultTestResultMessageEvent = spy(new TestResultMessageEventMock());

        service = new ScenarioRunnerService(configurationService,
                                            defaultTestResultMessageEvent,
                                            sessionService,
                                            projectService,
                                            scenarioLoader,
                                            identity);

        when(sessionService.newDefaultKieSessionWithPseudoClock(any(KieProject.class))).thenReturn(defaultPseudoClockKieSession);
        when(identity.getIdentifier()).thenReturn("testUser");

    }

    // TODO: Make sure the ksessions get loaded

    @Test
    public void testRunEmptyScenario() throws Exception {
        TestScenarioResult result = service.run(makeScenario("test.scenario"), new KieProject());

        assertNotNull(result);

        ArgumentCaptor<TestResultMessage> argumentCaptor = ArgumentCaptor.forClass(TestResultMessage.class);
        verify(defaultTestResultMessageEvent).fire(argumentCaptor.capture());
        assertEquals("testUser", argumentCaptor.getValue().getIdentifier());
    }

    @Test
    public void testRunSeveralScenarios() throws Exception {

        Path path = mock(Path.class);

        ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
        scenarios.add(makeScenario("test1.scenario"));
        scenarios.add(makeScenario("test2.scenario"));
        scenarios.add(makeScenario("test3.scenario"));
        when(scenarioLoader.loadScenarios(path)).thenReturn(scenarios);

        service.runAllTests(path);

        ArgumentCaptor<TestResultMessage> argumentCaptor = ArgumentCaptor.forClass(TestResultMessage.class);
        verify(defaultTestResultMessageEvent).fire(argumentCaptor.capture());
        assertEquals("testUser", argumentCaptor.getValue().getIdentifier());
    }

    private Scenario makeScenario(String name) {
        Scenario scenario = new Scenario();
        scenario.setName(name);
        return scenario;
    }

    class TestResultMessageEventMock
            implements Event<TestResultMessage> {

        @Override public void fire(TestResultMessage testResultMessage) {

        }

        @Override public Event<TestResultMessage> select(Annotation... annotations) {
            return null;
        }

        @Override public <U extends TestResultMessage> Event<U> select(Class<U> aClass, Annotation... annotations) {
            return null;
        }
    }
}