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
package org.drools.workbench.screens.scenariosimulation.model;

import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.utils.ScenarioSimulationI18nServerMessage;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Transport object that contains validation errors
 */
@Portable
public class FactMappingValidationError {

    private String errorId;
    private String errorMessage;
    private ScenarioSimulationI18nServerMessage serverMessage;
    private String[] parameters;

    public static FactMappingValidationError createFieldChangedError(FactMapping factMapping, String newType) {
        return new FactMappingValidationError(extractFactMappingId(factMapping),
                                              ScenarioSimulationI18nServerMessage.SCENARIO_VALIDATION_FIELD_CHANGED_ERROR,
                                              factMapping.getClassName(),
                                              newType);
    }

    public static FactMappingValidationError createNodeChangedError(FactMapping factMapping, String newType) {
        return new FactMappingValidationError(extractFactMappingId(factMapping),
                                              ScenarioSimulationI18nServerMessage.SCENARIO_VALIDATION_NODE_CHANGED_ERROR,
                                              factMapping.getFactIdentifier().getClassName(),
                                              newType);
    }

    public static FactMappingValidationError createFieldAddedConstraintError(FactMapping factMapping) {
        return new FactMappingValidationError(extractFactMappingId(factMapping),
                                              ScenarioSimulationI18nServerMessage.SCENARIO_VALIDATION_FIELD_ADDED_CONSTRAINT_ERROR);
    }

    public static FactMappingValidationError createFieldRemovedConstraintError(FactMapping factMapping) {
        return new FactMappingValidationError(extractFactMappingId(factMapping),
                                              ScenarioSimulationI18nServerMessage.SCENARIO_VALIDATION_FIELD_REMOVED_CONSTRAINT_ERROR);
    }

    public static FactMappingValidationError createGenericError(FactMapping factMapping, String genericError) {
        return new FactMappingValidationError(extractFactMappingId(factMapping), genericError);
    }

    private static String extractFactMappingId(FactMapping factMapping) {
        return factMapping.getFactAlias() + "." + factMapping.getExpressionAlias();
    }

    public FactMappingValidationError() {
        // CDI
    }

    public FactMappingValidationError(String errorId, String errorMessage) {
        this.errorId = errorId;
        this.errorMessage = errorMessage;
    }

    public FactMappingValidationError(String errorId,
                                      ScenarioSimulationI18nServerMessage serverMessage,
                                      String ... parameters) {
        this.errorId = errorId;
        this.serverMessage = serverMessage;
        this.parameters = parameters;
    }

    public String getErrorId() {
        return errorId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ScenarioSimulationI18nServerMessage getServerMessage() {
        return serverMessage;
    }

    public String[] getParameters() {
        return parameters;
    }
}
