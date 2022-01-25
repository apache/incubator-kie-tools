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
package org.drools.workbench.screens.scenariosimulation.model;

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.utils.ScenarioSimulationI18nServerMessage;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.junit.Assert.assertNull;

public class FactMappingValidationErrorTest {

    private FactMapping factMapping;

    @Before
    public void setup() {
        factMapping = new FactMapping(FactIdentifier.create("myType", "tMYTYPE"),
                                      ExpressionIdentifier.create(VALUE, FactMappingType.GIVEN));
        factMapping.setFactAlias("FactAlias");
        factMapping.setExpressionAlias("ExpressionAlias");
    }

    @Test
    public void createFieldChangedError() {
        FactMappingValidationError error = FactMappingValidationError.createFieldChangedError(factMapping, "tNEWTYPE");
        assertEquals("FactAlias.ExpressionAlias", error.getErrorId());
        assertEquals(ScenarioSimulationI18nServerMessage.SCENARIO_VALIDATION_FIELD_CHANGED_ERROR, error.getServerMessage());
        assertEquals(2, error.getParameters().length);
        assertEquals("tMYTYPE", error.getParameters()[0]);
        assertEquals("tNEWTYPE", error.getParameters()[1]);
        assertNull(error.getErrorMessage());
    }

    @Test
    public void createFieldAddedConstraintError() {
        FactMappingValidationError error = FactMappingValidationError.createFieldAddedConstraintError(factMapping);
        assertEquals("FactAlias.ExpressionAlias", error.getErrorId());
        assertEquals(ScenarioSimulationI18nServerMessage.SCENARIO_VALIDATION_FIELD_ADDED_CONSTRAINT_ERROR, error.getServerMessage());
        assertEquals(0, error.getParameters().length);
        assertNull(error.getErrorMessage());
    }

    @Test
    public void createFieldRemovedConstraintError() {
        FactMappingValidationError error = FactMappingValidationError.createFieldRemovedConstraintError(factMapping);
        assertEquals("FactAlias.ExpressionAlias", error.getErrorId());
        assertEquals(ScenarioSimulationI18nServerMessage.SCENARIO_VALIDATION_FIELD_REMOVED_CONSTRAINT_ERROR, error.getServerMessage());
        assertEquals(0, error.getParameters().length);
        assertNull(error.getErrorMessage());
    }

    @Test
    public void createNodeChangedError() {
        FactMappingValidationError error = FactMappingValidationError.createNodeChangedError(factMapping, "tNEWTYPE");
        assertEquals("FactAlias.ExpressionAlias", error.getErrorId());
        assertEquals(ScenarioSimulationI18nServerMessage.SCENARIO_VALIDATION_NODE_CHANGED_ERROR, error.getServerMessage());
        assertEquals(2, error.getParameters().length);
        assertEquals("tMYTYPE", error.getParameters()[0]);
        assertEquals("tNEWTYPE", error.getParameters()[1]);
        assertNull(error.getErrorMessage());
    }

    @Test
    public void createGenericError() {
        FactMappingValidationError error = FactMappingValidationError.createGenericError(factMapping, "err");
        assertEquals("FactAlias.ExpressionAlias", error.getErrorId());
        assertNull(error.getServerMessage());
        assertEquals("err", error.getErrorMessage());
        assertNull(error.getParameters());
    }
}
