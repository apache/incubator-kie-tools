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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.List;
import java.util.Objects;

import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.drools.workbench.screens.scenariosimulation.utils.ScenarioSimulationI18nServerMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractScenarioValidationTest {

    protected void checkResult(List<FactMappingValidationError> validationErrors, ExpectedError... expectedErrors) {
        if (expectedErrors.length == 0) {
            assertEquals(0, validationErrors.size());
        }

        for (ExpectedError expectedError : expectedErrors) {
            if (expectedError.getErrorMessage() != null) {
                assertTrue(validationErrors.stream().anyMatch(validationError -> Objects.equals(expectedError.getErrorMessage(), validationError.getErrorMessage())));
            } else {
                assertTrue(validationErrors.stream().anyMatch(validationError -> checkValidationErrorWithExpected(validationError, expectedError)));
            }
        }
    }

    protected boolean checkValidationErrorWithExpected(FactMappingValidationError validationError, ExpectedError expectedError) {
        if (validationError.getErrorMessage() != null) {
            return false;
        }

        boolean isSameServerMessage = validationError.getServerMessage().equals(expectedError.getServerMessage());
        boolean parametersSameLength = validationError.getParameters().length == expectedError.getParameters().size();

        if (isSameServerMessage && parametersSameLength) {
            if (expectedError.getParameters().isEmpty()) {
                return true;
            }

            for (int i = 0; i < validationError.getParameters().length; i++) {
                if (!Objects.equals(validationError.getParameters()[i], expectedError.getParameters().get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    static class ExpectedError {
        private String errorMessage;
        private ScenarioSimulationI18nServerMessage serverMessage;
        private List<String> parameters;

        public ExpectedError(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public ExpectedError(ScenarioSimulationI18nServerMessage serverMessage, List<String> parameters) {
            this.serverMessage = serverMessage;
            this.parameters = parameters;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public ScenarioSimulationI18nServerMessage getServerMessage() {
            return serverMessage;
        }

        public List<String> getParameters() {
            return parameters;
        }
    }
}
