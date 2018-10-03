/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FactMappingValueTest {

    @Test
    public void cleanValue() {
        Object rawValue = "Test";
        assertEquals(rawValue, FactMappingValue.cleanValue(rawValue));

        rawValue = " Test ";
        assertEquals(((String) rawValue).trim(), FactMappingValue.cleanValue(rawValue));

        rawValue = "= Test ";
        assertEquals("Test", FactMappingValue.cleanValue(rawValue));

        rawValue = " != Test ";
        assertEquals("Test", FactMappingValue.cleanValue(rawValue));

        rawValue = new Object();
        assertEquals(rawValue, FactMappingValue.cleanValue(rawValue));
    }

    @Test
    public void cleanValueEmptyAndNullString() {
        Object rawValue = "";
        assertNull(FactMappingValue.cleanValue(rawValue));

        rawValue = null;
        assertNull(FactMappingValue.cleanValue(rawValue));

        rawValue = " =  ";
        assertEquals("", FactMappingValue.cleanValue(rawValue));
    }

    @Test
    public void extractOperator() {
        Object rawValue = "Test";
        assertEquals(FactMappingValueOperator.EQUALS, FactMappingValue.extractOperator(rawValue));

        rawValue = " Test ";
        assertEquals(FactMappingValueOperator.EQUALS, FactMappingValue.extractOperator(rawValue));

        rawValue = "= Test ";
        assertEquals(FactMappingValueOperator.EQUALS, FactMappingValue.extractOperator(rawValue));

        rawValue = "!= Test ";
        assertEquals(FactMappingValueOperator.NOT_EQUALS, FactMappingValue.extractOperator(rawValue));

        rawValue = new Object();
        assertEquals(FactMappingValueOperator.EQUALS, FactMappingValue.extractOperator(rawValue));
    }

    @Test
    public void checkOperator() {
        FactMappingValue factMappingValue = new FactMappingValue(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION, null);
        assertEquals(FactMappingValueOperator.EQUALS, factMappingValue.getOperator());
        factMappingValue = new FactMappingValue(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION, "!= value");
        assertEquals(FactMappingValueOperator.NOT_EQUALS, factMappingValue.getOperator());

        assertThatThrownBy(() -> new FactMappingValue(null, ExpressionIdentifier.DESCRIPTION, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("FactIdentifier has to be not null");

        assertThatThrownBy(() -> new FactMappingValue(FactIdentifier.DESCRIPTION, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ExpressionIdentifier has to be not null");
    }
}