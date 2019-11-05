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

import javax.enterprise.context.ApplicationScoped;

import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.kie.api.runtime.KieContainer;
import org.uberfire.backend.vfs.Path;

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type.DMN;
import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type.RULE;

@ApplicationScoped
public class ScenarioValidationService
        extends AbstractKieContainerService {

    /**
     * Validate the structure of a simulation. It does not validate the content of the cells
     * @param simulation to validate
     * @param settings
     * @param path to test scenario file
     * @return list of validation errors
     */
    public List<FactMappingValidationError> validateSimulationStructure(Simulation simulation, Settings settings, Path path) {
        // skip validation (and compilation) if there are no columns to validate
        List<FactMapping> factMappings = simulation.getScesimModelDescriptor().getFactMappings();
        if (factMappings.stream().allMatch(AbstractScenarioValidation::isToSkip)) {
            return Collections.emptyList();
        }
        KieContainer kieContainer = getKieContainer(path);
        ScenarioSimulationModel.Type type = settings.getType();
        if (DMN.equals(type)) {
            return validateDMN(simulation, settings, kieContainer);
        } else if (RULE.equals(type)) {
            return validateRULE(simulation, settings, kieContainer);
        } else {
            throw new IllegalArgumentException("Only DMN and RULE test scenarios can be validated");
        }
    }

    protected List<FactMappingValidationError> validateDMN(Simulation simulation, Settings settings, KieContainer kieContainer) {
        return DMNScenarioValidation.INSTANCE.validate(simulation, settings, kieContainer);
    }

    protected List<FactMappingValidationError> validateRULE(Simulation simulation, Settings settings, KieContainer kieContainer) {
        return RULEScenarioValidation.INSTANCE.validate(simulation, settings, kieContainer);
    }
}