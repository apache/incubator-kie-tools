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

import java.util.List;
import java.util.stream.Collectors;

import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.kie.api.runtime.KieContainer;

import static org.drools.scenariosimulation.api.model.FactIdentifier.EMPTY;
import static org.drools.scenariosimulation.api.model.FactMappingType.OTHER;

public abstract class AbstractScenarioValidation {

    public abstract List<FactMappingValidationError> validate(Simulation simulation, KieContainer kieContainer);

    /**
     * Skip descriptive columns (FactMappingType.OTHER), column with no instance (FactIdentifier.EMPTY)
     * and with not expression elements
     * @param factMapping
     * @return
     */
    public static boolean isToSkip(FactMapping factMapping) {
        return OTHER.equals(factMapping.getExpressionIdentifier().getType()) ||
                EMPTY.equals(factMapping.getFactIdentifier()) ||
                factMapping.getExpressionElements().isEmpty();
    }

    protected List<String> expressionElementToString(FactMapping factMapping) {
        return factMapping.getExpressionElementsWithoutClass().stream()
                .map(ExpressionElement::getStep).collect(Collectors.toList());
    }
}