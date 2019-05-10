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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.models.testscenarios.backend.ScenarioRunner4JUnit;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.common.services.shared.test.TestService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.bus.server.annotations.Service;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieSession;
import org.kie.workbench.common.services.backend.session.SessionService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

import static org.drools.workbench.screens.testscenario.backend.server.ScenarioUtil.failureToFailure;
import static org.drools.workbench.screens.testscenario.backend.server.ScenarioUtil.failuresToFailures;
import static org.drools.workbench.screens.testscenario.backend.server.ScenarioUtil.getKSessionName;

@Service
@ApplicationScoped
public class ScenarioRunnerService
        implements TestService {

    protected KieModuleService moduleService;
    private ScenarioLoader scenarioLoader;
    private SessionService sessionService;
    private ConfigurationService configurationService;

    public ScenarioRunnerService() {
    }

    @Inject
    public ScenarioRunnerService(final ConfigurationService configurationService,
                                 final SessionService sessionService,
                                 final KieModuleService moduleService,
                                 final ScenarioLoader scenarioLoader) {
        this.configurationService = configurationService;
        this.sessionService = sessionService;
        this.moduleService = moduleService;
        this.scenarioLoader = scenarioLoader;
    }

    public TestScenarioResult run(final String identifier,
                                  final Scenario scenario,
                                  final KieModule module) {
        try {

            final HashMap<String, KieSession> ksessions = new HashMap<String, KieSession>();
            final String ksessionName = getKSessionName(scenario.getKSessions());
            ksessions.put(ksessionName,
                          loadKSession(module,
                                       ksessionName));

            final AuditLogger auditLogger = new AuditLogger(ksessions);

            final ScenarioRunner4JUnit scenarioRunner = new ScenarioRunner4JUnit(
                    scenario,
                    ksessions,
                    getMaxRuleFirings());

            final TestResultMessage testResultMessage = run(identifier,
                                                            null,
                                                            scenarioRunner);

            return new TestScenarioResult(scenario,
                                          auditLogger.getLog(),
                                          testResultMessage);
        } catch (InitializationError initializationError) {
            throw new GenericPortableException(initializationError.getMessage());
        }
    }

    @Override
    public List<TestResultMessage> runAllTests(final String identifier,
                                               final Path path) {
        try {
            final ArrayList<TestResultMessage> result = new ArrayList<>();
            final Map<Path, Scenario> scenarios = scenarioLoader.loadScenarios(path);

            final Map<String, KieSession> kSessions = getKSessions(path,
                                                                   scenarios.values());

            for (Map.Entry<Path, Scenario> entry : scenarios.entrySet()) {
                ScenarioRunner4JUnit scenarioRunner = new ScenarioRunner4JUnit(
                        entry.getValue(),
                        kSessions,
                        getMaxRuleFirings());
                result.add(run(identifier,
                               entry.getKey(),
                               scenarioRunner));
            }

            return result;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private TestResultMessage run(final String identifier,
                                  final Path path,
                                  final ScenarioRunner4JUnit scenarioRunner) {

        final List<org.guvnor.common.services.shared.test.Failure> failures = new ArrayList<org.guvnor.common.services.shared.test.Failure>();

        final JUnitCore jUnitCore = new JUnitCore();

        jUnitCore.addListener(new RunListener() {
            @Override
            public void testAssumptionFailure(Failure failure) {
                failures.add(failureToFailure(path, failure));
            }
        });

        final Result result = jUnitCore.run(scenarioRunner);

        failures.addAll(failuresToFailures(path, result.getFailures()));

        return new TestResultMessage(identifier,
                                     result.getRunCount(),
                                     result.getRunTime(),
                                     failures);
    }

    private int getMaxRuleFirings() {
        for (ConfigGroup editorConfigGroup : configurationService.getConfiguration(ConfigType.EDITOR)) {
            if (ScenarioTestEditorService.TEST_SCENARIO_EDITOR_SETTINGS.equals(editorConfigGroup.getName())) {
                for (ConfigItem item : editorConfigGroup.getItems()) {
                    String itemName = item.getName();
                    if (itemName.equals(ScenarioTestEditorService.TEST_SCENARIO_EDITOR_MAX_RULE_FIRINGS)) {
                        return (Integer) item.getValue();
                    }
                }
            }
        }
        return 0;
    }

    private Map<String, KieSession> getKSessions(Path path,
                                                 Collection<Scenario> scenarios) {
        Map<String, KieSession> ksessions = new HashMap<String, KieSession>();
        for (Scenario scenario : scenarios) {
            String ksessionName = getKSessionName(scenario.getKSessions());
            ksessions.put(ksessionName,
                          loadKSession(moduleService.resolveModule(path),
                                       ksessionName));
        }
        return ksessions;
    }

    private KieSession loadKSession(KieModule module,
                                    String ksessionName) {
        KieSession ksession = null;
        try {
            if (ksessionName == null || ksessionName.equals("defaultKieSession")) {
                ksession = sessionService.newDefaultKieSessionWithPseudoClock(module);
            } else {
                ksession = sessionService.newKieSession(module,
                                                        ksessionName);
            }
        } catch (Exception e) {
            // If for one reason or another we can not load the ksession. Return null
            return null;
        }

        return ksession;
    }
}
