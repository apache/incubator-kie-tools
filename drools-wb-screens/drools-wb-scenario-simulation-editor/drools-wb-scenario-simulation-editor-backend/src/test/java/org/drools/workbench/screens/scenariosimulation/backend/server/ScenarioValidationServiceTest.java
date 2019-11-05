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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.Collections;
import java.util.List;

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioValidationServiceTest {

    @Mock
    private KieContainer kieContainerMock;

    @Mock
    private Path pathMock;

    @Test
    public void validateSimulationStructure() {
        Simulation simulation = new Simulation();
        Settings settings = new Settings();
        ScenarioValidationService scenarioValidationServiceSpy = spy(new ScenarioValidationService() {
            @Override
            protected List<FactMappingValidationError> validateDMN(Simulation simulation, Settings settings, KieContainer kieContainer) {
                return Collections.emptyList();
            }

            @Override
            protected List<FactMappingValidationError> validateRULE(Simulation simulation, Settings settings, KieContainer kieContainer) {
                return Collections.emptyList();
            }

            @Override
            protected KieContainer getKieContainer(Path path) {
                return kieContainerMock;
            }
        });

        settings.setType(ScenarioSimulationModel.Type.DMN);
        scenarioValidationServiceSpy.validateSimulationStructure(simulation, settings, pathMock);
        verify(scenarioValidationServiceSpy, never()).validateDMN(eq(simulation), eq(settings), eq(kieContainerMock));
        verify(scenarioValidationServiceSpy, never()).validateRULE(eq(simulation), eq(settings), eq(kieContainerMock));

        reset(scenarioValidationServiceSpy);
        FactMapping sampleFactMapping = simulation.getScesimModelDescriptor()
                .addFactMapping(FactIdentifier.create("sample", String.class.getCanonicalName()),
                                ExpressionIdentifier.create("sample", FactMappingType.GIVEN));
        sampleFactMapping.addExpressionElement("sample", String.class.getCanonicalName());

        settings.setType(ScenarioSimulationModel.Type.DMN);
        scenarioValidationServiceSpy.validateSimulationStructure(simulation, settings, pathMock);
        verify(scenarioValidationServiceSpy, timeout(1)).validateDMN(eq(simulation), eq(settings), eq(kieContainerMock));

        reset(scenarioValidationServiceSpy);

        settings.setType(ScenarioSimulationModel.Type.RULE);
        scenarioValidationServiceSpy.validateSimulationStructure(simulation, settings, pathMock);
        verify(scenarioValidationServiceSpy, timeout(1)).validateRULE(eq(simulation), eq(settings), eq(kieContainerMock));

        settings.setType(null);
        assertThatThrownBy(() -> scenarioValidationServiceSpy.validateSimulationStructure(simulation, settings, pathMock))
                .isInstanceOf(IllegalArgumentException.class);
    }
}