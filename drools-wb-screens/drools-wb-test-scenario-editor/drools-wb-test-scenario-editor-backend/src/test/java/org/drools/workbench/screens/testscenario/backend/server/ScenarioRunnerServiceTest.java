/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.backend.server;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.structure.server.config.ConfigurationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.workbench.common.services.backend.session.SessionService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioRunnerServiceTest {

    @Mock
    private ScenarioLoader scenarioLoader;

    private ScenarioRunnerService service;

    @Spy
    private IOService ioService = new IOServiceDotFileImpl("testIoService");

    @InjectMocks
    private ScenarioTestEditorServiceImpl testEditorService = new ScenarioTestEditorServiceImpl();

    @Mock
    private SessionService sessionService;

    private KieSession kieSession;
    private KieContainer kieContainer;

    @Before
    public void setUp() throws Exception {
        final ConfigurationService configurationService = mock(ConfigurationService.class);
        final KieModuleService moduleService = mock(KieModuleService.class);

        service = new ScenarioRunnerService(configurationService,
                                            sessionService,
                                            moduleService,
                                            scenarioLoader);
    }

    @After
    public void tearDown() throws Exception {
        if (Objects.nonNull(kieSession)) {
            kieSession.dispose();
            kieSession.destroy();
        }
        if (Objects.nonNull(kieContainer)) {
            kieContainer.dispose();
        }
    }

    @Test
    public void testRunEmptyScenario() throws Exception {
        initKieSession();
        TestScenarioResult result = service.run("userName",
                                                makeScenario("test.scenario"),
                                                new KieModule());

        assertNotNull(result);

        assertEquals("userName",
                     result.getTestResultMessage().getIdentifier());
    }

    @Test
    public void testGreetings() throws Exception {
        initKieSession("HelloEveryOne.gdst");
        testScenario("greetings.scenario", true);
    }

    /**
     * DROOLS-2107
     * Focus on rule inheritance
     */
    @Test
    public void testHighAndSmall() throws Exception {
        initKieSession("thereIsHighPerson.rdrl", "thereAreBothHighAndSmallPerson.rdrl");
        testScenario("highAndSmall.scenario", true);
    }

    /**
     * DROOLS-2104
     * Use XLS table
     */
    @Test
    public void testCouples() throws Exception {
        initKieSession("couplesWithSameEyes.xls");
        testScenario("testCouples.scenario", true);
    }

    @Test
    public void testCouplesNegative() throws Exception {
        initKieSession("couplesWithSameEyes.xls");
        testScenario("testCouplesNegative.scenario", false);
    }

    @Test
    public void testSimilarPerson() throws Exception {
        initKieSession("mostSimilarPerson.gdst");
        testScenario("testSimilarPerson.scenario", true);
    }

    @Test
    public void testSimilarPersonNegative() throws Exception {
        initKieSession("mostSimilarPerson.gdst");
        testScenario("testSimilarPersonNegative.scenario", false);
    }

    @Test
    public void testIsGithubContributor() throws Exception {
        initKieSession("isGithubContributor.gdst");
        testScenario("testContributors.scenario", true);
    }

    @Test
    public void testIsGithubContributorNegative() throws Exception {
        initKieSession("isGithubContributor.gdst");
        testScenario("testContributorsNegative.scenario", false);
    }

    @Test
    public void testMergeMolecules() throws Exception {
        initKieSession("mergeMolecules.gdst");
        testScenario("testMergedMolecules.scenario", true);
    }

    @Test
    public void testMergeMoleculesNegative() throws Exception {
        initKieSession("mergeMolecules.gdst");
        testScenario("testMergedMoleculesNegative.scenario", false);
    }

    @Test
    public void testAcidReactions() throws Exception {
        initKieSession("acidReactions.gdst");
        testScenario("testAcid.scenario", true);
    }

    @Test
    public void testAcidReactionsNegative() throws Exception {
        initKieSession("acidReactions.gdst");
        testScenario("testAcidNegative.scenario", false);
    }

    @Test
    public void testRunSeveralScenarios() throws Exception {
        initKieSession();
        Path path = mock(Path.class);

        ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
        scenarios.add(makeScenario("test1.scenario"));
        scenarios.add(makeScenario("test2.scenario"));
        scenarios.add(makeScenario("test3.scenario"));
        when(scenarioLoader.loadScenarios(path)).thenReturn(scenarios);

        List<TestResultMessage> testResultMessages = service.runAllTests("userName",
                                                                         path);

        for (TestResultMessage testResultMessage : testResultMessages) {

            assertEquals("userName",
                         testResultMessage.getIdentifier());
        }
    }

    @Test
    public void testNullPointerDueToInnerAttributesCheck() throws Exception {
        initKieSession("check_salary.gdst");
        testScenario("test_salaries.scenario", false);
    }

    private void testScenario(String scenarioName, boolean isExpectedSuccess) throws Exception {
        final KieModule module = mock(KieModule.class);

        final URL scenarioResource = getClass().getResource(scenarioName);
        final Path scenarioPath = PathFactory.newPath(scenarioResource.getFile(),
                                                      scenarioResource.toURI().toString());

        final Scenario scenario = testEditorService.load(scenarioPath);
        assertFalse(scenario.wasSuccessful());

        final TestScenarioResult result = service.run("userName", scenario, module);

        assertEquals(isExpectedSuccess, scenario.wasSuccessful());
        assertEquals(isExpectedSuccess, result.getScenario().wasSuccessful());

        final TestResultMessage resultMessage = result.getTestResultMessage();
        assertEquals(isExpectedSuccess, resultMessage.getFailures().size() == 0);
        assertEquals(isExpectedSuccess, resultMessage.wasSuccessful());
    }

    private Scenario makeScenario(String name) {
        Scenario scenario = new Scenario();
        scenario.setName(name);
        return scenario;
    }

    private void initKieSession(String... resourceNames) {
        final KieServices kieServices = KieServices.Factory.get();
        final KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
        final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel("defaultKieBase")
                .setDefault(true)
                .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
                .setEventProcessingMode(EventProcessingOption.STREAM);

        kieBaseModel.newKieSessionModel("defaultKieSession")
                .setDefault(true)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType(ClockTypeOption.get("pseudo"))
                .newWorkItemHandlerModel("Rest", "org.drools.workbench.screens.testscenario.backend.server.GithubContributorsWIH");

        final KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.writeKModuleXML(kieModuleModel.toXML());
        for (String resource : resourceNames) {
            kfs.write(ResourceFactory.newUrlResource(this.getClass()
                                                             .getResource(resource)
                                                             .toString()));
        }

        kieServices.newKieBuilder(kfs).buildAll();

        kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        kieSession = kieContainer.newKieSession();
        doReturn(kieSession).when(sessionService).newDefaultKieSessionWithPseudoClock(any(KieModule.class));
    }
}