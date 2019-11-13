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

package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.drools.scenariosimulation.api.model.FactMappingType.GIVEN;
import static org.drools.scenariosimulation.api.model.FactMappingType.OTHER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RULESimulationSettingsCreationStrategyTest {

    private final String value = "test";
    private static RULESimulationSettingsCreationStrategy ruleSimulationSettingsCreationStrategy = new RULESimulationSettingsCreationStrategy();
    @Mock
    private Path pathMock;

    @Test
    public void createSimulation() throws Exception {
        final Simulation retrieved = ruleSimulationSettingsCreationStrategy.createSimulation(pathMock, value);
        assertNotNull(retrieved);
    }

    @Test
    public void createBackground() throws Exception {
        final Background retrieved = ruleSimulationSettingsCreationStrategy.createBackground(pathMock, value);
        assertNotNull(retrieved);
        assertFalse(retrieved.getScesimModelDescriptor().getUnmodifiableFactMappings().stream()
                            .anyMatch(elem -> OTHER.equals(elem.getExpressionIdentifier().getType())));
        assertTrue(retrieved.getScesimModelDescriptor().getUnmodifiableFactMappings().stream()
                           .allMatch(elem -> GIVEN.equals(elem.getExpressionIdentifier().getType())));
    }

    @Test
    public void createSettings() throws Exception {
        final Settings retrieved = ruleSimulationSettingsCreationStrategy.createSettings(pathMock, value);
        assertNotNull(retrieved);
    }
}