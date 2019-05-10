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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.drools.workbench.screens.testscenario.type.TestScenarioResourceTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.query.FileLoader;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

public class ScenarioLoader {

    @Inject
    TestScenarioResourceTypeDefinition testScenarioResourceTypeDefinition;
    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    @Inject
    private ScenarioTestEditorService scenarioTestEditorService;
    @Inject
    private FileLoader fileLoader;

    public Map<Path, Scenario> loadScenarios(final Path testResourcePath) {
        final Map<Path, Scenario> scenarios = new HashMap<>();

        for (Path path : fileLoader.loadPaths(testResourcePath, testScenarioResourceTypeDefinition.getSuffix())) {
            scenarios.put(path, scenarioTestEditorService.load(path));
        }
        return scenarios;
    }
}
