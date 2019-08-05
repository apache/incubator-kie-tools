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
import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.IsUnaryTests;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.ENUMERATION;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.EXPRESSION;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.RANGE;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UnaryTestsPropertyConverterTest {

    private static final String ID = "thisId";
    private static final String TEXT = "1,2,3";
    private static final String DESCRIPTION = "description";
    private static final String EXPRESSION_LANGUAGE = "FEEL";

    private static final QName CONSTRAINT_KEY = new QName(DMNModelInstrumentedBase.Namespace.KIE.getUri(),
                                                          ConstraintType.CONSTRAINT_KEY,
                                                          DMNModelInstrumentedBase.Namespace.KIE.getPrefix());

    @Mock
    private IsUnaryTests wbUnaryTests;

    @Mock
    private org.kie.dmn.model.api.UnaryTests dmnUnary;

    @Mock
    private Map<QName, String> additionalAttributes;

    @Before
    public void setup() {

        when(dmnUnary.getId()).thenReturn(ID);
        when(dmnUnary.getAdditionalAttributes()).thenReturn(additionalAttributes);
        when(dmnUnary.getText()).thenReturn(TEXT);
        when(dmnUnary.getDescription()).thenReturn(DESCRIPTION);
        when(dmnUnary.getExpressionLanguage()).thenReturn(EXPRESSION_LANGUAGE);
        when(additionalAttributes.containsKey(CONSTRAINT_KEY)).thenReturn(true);

        when(wbUnaryTests.getId()).thenReturn(new Id(ID));
        when(wbUnaryTests.getText()).thenReturn(new Text(TEXT));
    }

    @Test
    public void testWbFromDMNEnumeration() {
        testWbFromDMN(ENUMERATION);
    }

    @Test
    public void testWbFromDMNRange() {
        testWbFromDMN(RANGE);
    }

    @Test
    public void testWbFromDMNExpression() {
        testWbFromDMN(EXPRESSION);
    }

    @Test
    public void testDmnFromWBEnumeration() {
        testDmnFromWB(ENUMERATION);
    }

    @Test
    public void testDmnFromWBRange() {
        testDmnFromWB(RANGE);
    }

    @Test
    public void testDmnFromWBExpression() {
        testDmnFromWB(EXPRESSION);
    }

    public void testWbFromDMN(final ConstraintType constraintType) {
        when(additionalAttributes.get(CONSTRAINT_KEY)).thenReturn(constraintType.value());

        final UnaryTests unaryTests = UnaryTestsPropertyConverter.wbFromDMN(dmnUnary);
        assertEquals(ID, unaryTests.getId().getValue());
        assertEquals(DESCRIPTION, unaryTests.getDescription().getValue());
        assertEquals(TEXT, unaryTests.getText().getValue());
        assertEquals(EXPRESSION_LANGUAGE, unaryTests.getExpressionLanguage().getValue());
        assertEquals(constraintType, unaryTests.getConstraintType());
    }

    public void testDmnFromWB(final ConstraintType constraintType) {

        when(wbUnaryTests.getConstraintType()).thenReturn(constraintType);

        final org.kie.dmn.model.api.UnaryTests unaryTests = UnaryTestsPropertyConverter.dmnFromWB(wbUnaryTests);
        assertEquals(ID, unaryTests.getId());
        assertEquals(TEXT, unaryTests.getText());
        assertEquals(constraintType.value(), unaryTests.getAdditionalAttributes().get(CONSTRAINT_KEY));
    }
}