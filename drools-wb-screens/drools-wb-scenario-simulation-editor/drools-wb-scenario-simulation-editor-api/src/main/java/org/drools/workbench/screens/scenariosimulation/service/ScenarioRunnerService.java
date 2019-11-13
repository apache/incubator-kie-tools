/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.service;

import java.util.List;

import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.guvnor.common.services.shared.test.TestService;
import org.uberfire.backend.vfs.Path;

public interface ScenarioRunnerService
        extends TestService {

    SimulationRunResult runTest(final String identifier,
                                final Path path,
                                final ScesimModelDescriptor simulationDescriptor,
                                final List<ScenarioWithIndex> scenarios,
                                final Settings settings,
                                final Background background);
}
