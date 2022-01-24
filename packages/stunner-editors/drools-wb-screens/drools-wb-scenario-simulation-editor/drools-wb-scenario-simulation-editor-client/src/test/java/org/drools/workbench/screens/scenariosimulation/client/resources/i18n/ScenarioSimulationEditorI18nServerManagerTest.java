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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationEditorI18nServerManagerTest {

    private FactMapping factMapping;

    @Before
    public void setup() {
        factMapping = new FactMapping(FactIdentifier.create("myType", "tMYTYPE"),
                                      ExpressionIdentifier.create(VALUE, FactMappingType.GIVEN));
        factMapping.setFactAlias("FactAlias");
        factMapping.setExpressionAlias("ExpressionAlias");
    }

    @Test(expected = IllegalArgumentException.class)
    public void retrieveMessageNullParameter() {
        ScenarioSimulationEditorI18nServerManager.retrieveMessage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void retrieveMessageNullServerMessage() {
        ScenarioSimulationEditorI18nServerManager.retrieveMessage(FactMappingValidationError.createGenericError(factMapping, "error"));
    }

    @Test
    public void retrieveMessageFieldAddedConstraint() {
        FactMappingValidationError error = FactMappingValidationError.createFieldAddedConstraintError(factMapping);
        String message = ScenarioSimulationEditorI18nServerManager.retrieveMessage(error);
        String expected = ScenarioSimulationEditorConstants.INSTANCE.scenarioValidationFieldAddedConstraintError();
        assertEquals(expected, message);
    }

    @Test
    public void retrieveMessageFieldRemovedConstraint() {
        FactMappingValidationError error = FactMappingValidationError.createFieldRemovedConstraintError(factMapping);
        String message = ScenarioSimulationEditorI18nServerManager.retrieveMessage(error);
        String expected = ScenarioSimulationEditorConstants.INSTANCE.scenarioValidationFieldRemovedConstraintError();
        assertEquals(expected, message);
    }

    @Test
    public void retrieveMessageFieldChangedError() {
        String newType = "newType";
        FactMappingValidationError error = FactMappingValidationError.createFieldChangedError(factMapping, newType);
        String message = ScenarioSimulationEditorI18nServerManager.retrieveMessage(error);
        String expected = ScenarioSimulationEditorConstants.INSTANCE.scenarioValidationFieldChangedError("tMYTYPE", newType);
        assertEquals(expected, message);
    }

    @Test
    public void retrieveMessageNodeChangedError() {
        String newType = "newType";
        FactMappingValidationError error = FactMappingValidationError.createNodeChangedError(factMapping, newType);
        String message = ScenarioSimulationEditorI18nServerManager.retrieveMessage(error);
        String expected = ScenarioSimulationEditorConstants.INSTANCE.scenarioValidationNodeChangedError("tMYTYPE", newType);
        assertEquals(expected, message);
    }
}
