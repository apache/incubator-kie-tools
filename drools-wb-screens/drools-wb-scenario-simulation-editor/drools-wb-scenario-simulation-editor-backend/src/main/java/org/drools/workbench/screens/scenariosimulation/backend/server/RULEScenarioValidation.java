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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.runner.ScenarioException;
import org.drools.scenariosimulation.backend.util.ScenarioBeanWrapper;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.kie.api.runtime.KieContainer;

import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.fillBean;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.loadClass;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.navigateToObject;
import static org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError.createFieldChangedError;
import static org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError.createGenericError;

public class RULEScenarioValidation extends AbstractScenarioValidation {

    public static RULEScenarioValidation INSTANCE = new RULEScenarioValidation();

    /**
     * Validate structure of a RULE test scenario.
     * Supported checks for each column:
     * - empty column skip
     * - instance type removed
     * - navigation of bean still valid
     * - field type changed
     * @param simulation
     * @param kieContainer
     * @return
     */
    @Override
    public List<FactMappingValidationError> validate(Simulation simulation, KieContainer kieContainer) {
        List<FactMappingValidationError> errors = new ArrayList<>();
        Map<String, Object> beanInstanceMap = new HashMap<>();
        for (FactMapping factMapping : simulation.getSimulationDescriptor().getFactMappings()) {
            if (isToSkip(factMapping)) {
                continue;
            }

            // try to navigate using all the steps to verify if structure is still valid
            List<String> steps = expressionElementToString(factMapping);

            try {
                String instanceClassName = factMapping.getFactIdentifier().getClassName();

                if (steps.isEmpty()) {
                    // in case of top level simple types just try to load the class
                    loadClass(instanceClassName, kieContainer.getClassLoader());
                } else {
                    Object bean = beanInstanceMap.computeIfAbsent(
                            instanceClassName,
                            className -> fillBean(className, Collections.emptyMap(), kieContainer.getClassLoader()));

                    List<String> stepsToField = steps.subList(0, steps.size() - 1);
                    String lastStep = steps.get(steps.size() - 1);

                    ScenarioBeanWrapper<?> beanBeforeLastStep = navigateToObject(bean, stepsToField, true);

                    ScenarioBeanWrapper<?> beanWrapper = navigateToObject(beanBeforeLastStep.getBean(), Collections.singletonList(lastStep), false);

                    String targetClassName = beanWrapper.getBeanClass() != null ?
                            beanWrapper.getBeanClass().getCanonicalName() :
                            null;

                    // check if target field has valid type
                    if (!Objects.equals(factMapping.getClassName(), targetClassName)) {
                        errors.add(createFieldChangedError(factMapping, targetClassName));
                    }
                }
            } catch (ScenarioException e) {
                errors.add(createGenericError(factMapping, e.getMessage()));
            }
        }
        return errors;
    }
}