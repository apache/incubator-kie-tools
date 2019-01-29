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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClauseUnaryTests;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OutputClauseUnaryTestsPropertyConverterTest {

    private static final String ID = "thisId";
    private static final String TEXT = "1,2,3";

    private static final QName key = new QName(DMNModelInstrumentedBase.Namespace.KIE.getUri(),
                                               ConstraintType.CONSTRAINT_KEY,
                                               DMNModelInstrumentedBase.Namespace.KIE.getPrefix());

    @Mock
    private org.kie.dmn.model.api.UnaryTests dmnUnary;

    @Mock
    private Map<QName, String> additionalAttributes;

    @Before
    public void setup() {

        when(dmnUnary.getId()).thenReturn(ID);
        when(dmnUnary.getAdditionalAttributes()).thenReturn(additionalAttributes);
        when(dmnUnary.getText()).thenReturn(TEXT);
    }

    @Test
    public void testWbFromDMNEnumeration() {
        testWbFromDMN(ConstraintType.ENUMERATION);
    }

    @Test
    public void testWbFromDMNExpression() {
        testWbFromDMN(ConstraintType.EXPRESSION);
    }

    @Test
    public void testWbFromDMNRange() {
        testWbFromDMN(ConstraintType.RANGE);
    }

    public void testWbFromDMN(final ConstraintType constraintType) {
        when(additionalAttributes.getOrDefault(key, "")).thenReturn(constraintType.value());

        final OutputClauseUnaryTests outputClause = OutputClauseUnaryTestsPropertyConverter.wbFromDMN(dmnUnary);
        assertEquals(outputClause.getConstraintType(), constraintType);
        assertEquals(outputClause.getId().getValue(), ID);
        assertEquals(outputClause.getText().getValue(), TEXT);
    }
}