/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.resources.i18n;

import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;

public class ScenarioSimulationEditorI18nServerManager {

    private ScenarioSimulationEditorI18nServerManager() {
        // Util Class
    }

    public static String retrieveMessage(FactMappingValidationError error) {
        if (error != null && error.getServerMessage() != null) {
            switch (error.getServerMessage()) {
                case SCENARIO_VALIDATION_NODE_CHANGED_ERROR:
                    return ScenarioSimulationEditorConstants.INSTANCE.scenarioValidationNodeChangedError(error.getParameters()[0],
                                                                                                         error.getParameters()[1]);
                case SCENARIO_VALIDATION_FIELD_CHANGED_ERROR:
                    return ScenarioSimulationEditorConstants.INSTANCE.scenarioValidationFieldChangedError(error.getParameters()[0],
                                                                                                          error.getParameters()[1]);
                case SCENARIO_VALIDATION_FIELD_ADDED_CONSTRAINT_ERROR:
                    return ScenarioSimulationEditorConstants.INSTANCE.scenarioValidationFieldAddedConstraintError();
                case SCENARIO_VALIDATION_FIELD_REMOVED_CONSTRAINT_ERROR:
                    return ScenarioSimulationEditorConstants.INSTANCE.scenarioValidationFieldRemovedConstraintError();
            }
        }
        throw new IllegalArgumentException();
    }
}