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
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;

import static org.drools.scenariosimulation.api.model.FactMappingType.GIVEN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SimulationSettingsCreationStrategyTest {

    @Test
    public void createEmptyColumn() {
        ArgumentCaptor<ExpressionIdentifier> expressionIdentifierCaptor1 = ArgumentCaptor.forClass(ExpressionIdentifier.class);
        ArgumentCaptor<ExpressionIdentifier> expressionIdentifierCaptor2 = ArgumentCaptor.forClass(ExpressionIdentifier.class);
        int placeholderId = 1;
        int columnIndex = 0;
        SimulationSettingsCreationStrategy simulationSettingsCreationStrategy = new SimulationSettingsCreationStrategy() {
            @Override
            public Simulation createSimulation(Path context, String value) {
                return null;
            }

            @Override
            public Background createBackground(Path context, String dmnFilePath) {
                return null;
            }

            @Override
            public Settings createSettings(Path context, String value) {
                return null;
            }
        };
        ScesimModelDescriptor simulationDescriptorSpy = spy(new ScesimModelDescriptor());
        Scenario scenarioSpy = spy(new Scenario());
        ScenarioWithIndex scenarioWithIndex = new ScenarioWithIndex(1, scenarioSpy);

        simulationSettingsCreationStrategy.createEmptyColumn(simulationDescriptorSpy, scenarioWithIndex, placeholderId, GIVEN, columnIndex);

        verify(simulationDescriptorSpy, times(1)).addFactMapping(
                eq(columnIndex),
                eq(FactMapping.getInstancePlaceHolder(placeholderId)),
                eq(FactIdentifier.EMPTY),
                expressionIdentifierCaptor1.capture());

        assertEquals(GIVEN, expressionIdentifierCaptor1.getValue().getType());

        verify(scenarioSpy, times(1)).addMappingValue(
                eq(FactIdentifier.EMPTY),
                expressionIdentifierCaptor2.capture(),
                isNull());

        assertEquals(GIVEN, expressionIdentifierCaptor2.getValue().getType());
    }
}