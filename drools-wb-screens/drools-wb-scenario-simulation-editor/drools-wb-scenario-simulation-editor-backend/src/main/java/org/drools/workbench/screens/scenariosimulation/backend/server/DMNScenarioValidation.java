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
import java.util.List;
import java.util.Objects;

import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.feel.lang.Type;

import static org.drools.scenariosimulation.backend.util.DMNSimulationUtils.extractDMNModel;
import static org.drools.scenariosimulation.backend.util.DMNSimulationUtils.extractDMNRuntime;
import static org.drools.workbench.screens.scenariosimulation.backend.server.util.DMNUtils.getRootType;
import static org.drools.workbench.screens.scenariosimulation.backend.server.util.DMNUtils.navigateDMNType;
import static org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError.createFieldChangedError;
import static org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError.createGenericError;
import static org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError.createNodeChangedError;
import static org.kie.dmn.feel.lang.types.BuiltInType.CONTEXT;
import static org.kie.dmn.feel.lang.types.BuiltInType.UNKNOWN;

public class DMNScenarioValidation extends AbstractScenarioValidation {

    public static final DMNScenarioValidation INSTANCE = new DMNScenarioValidation();

    /**
     * Validate structure of a DMN test scenario.
     * Supported checks for each column:
     * - empty column skip
     * - DMN node removed
     * - simple type becomes complex type
     * - navigation of data type still valid
     * - field type changed
     * @param simulation
     * @param kieContainer
     * @return
     */
    @Override
    public List<FactMappingValidationError> validate(Simulation simulation, KieContainer kieContainer) {
        List<FactMappingValidationError> errors = new ArrayList<>();
        String dmnFilePath = simulation.getSimulationDescriptor().getDmnFilePath();
        DMNModel dmnModel = getDMNModel(kieContainer, dmnFilePath);

        for (FactMapping factMapping : simulation.getSimulationDescriptor().getFactMappings()) {
            if (isToSkip(factMapping)) {
                continue;
            }

            String nodeName = factMapping.getFactIdentifier().getName();

            DMNType rootDMNType;
            try {
                rootDMNType = dmnModel.getDecisionByName(nodeName) != null ?
                        dmnModel.getDecisionByName(nodeName).getResultType() :
                        dmnModel.getInputByName(nodeName).getType();
            } catch (NullPointerException e) {
                errors.add(createNodeChangedError(factMapping, "node not found"));
                continue;
            }

            List<String> steps = expressionElementToString(factMapping);

            // error if direct mapping (= simple type) but it is a composite
            // NOTE: context is a special case so it is composite even if no fields are declared
            Type rootType = getRootType((BaseDMNTypeImpl) rootDMNType);
            if (!CONTEXT.equals(rootType) && steps.isEmpty() && rootDMNType.isComposite()) {
                errors.add(createNodeChangedError(factMapping, rootDMNType.getName()));
                continue;
            }

            try {
                DMNType fieldType = navigateDMNType(rootDMNType, steps);

                if (!isDMNFactMappingValid(factMapping.getClassName(), factMapping, fieldType)) {
                    errors.add(createFieldChangedError(factMapping, fieldType.getName()));
                }
            } catch (IllegalStateException e) {
                errors.add(createGenericError(factMapping, e.getMessage()));
            }
        }
        return errors;
    }

    private boolean isDMNFactMappingValid(String typeName, FactMapping factMapping, DMNType dmnType) {
        // NOTE: Any/Undefined is a special case where collection is true
        Type rootType = getRootType((BaseDMNTypeImpl) dmnType);
        boolean isCoherent = UNKNOWN.equals(rootType) || ScenarioSimulationSharedUtils.isList(typeName) ==
                dmnType.isCollection();
        if (!isCoherent) {
            return false;
        }
        String factMappingType = ScenarioSimulationSharedUtils.isList(typeName) ?
                factMapping.getGenericTypes().get(0) :
                typeName;

        return Objects.equals(factMappingType, dmnType.getName());
    }

    protected DMNModel getDMNModel(KieContainer kieContainer, String dmnPath) {
        return extractDMNModel(extractDMNRuntime(kieContainer), dmnPath);
    }
}