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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TypeRefUtilsTest {

    private static final QName TYPE_REF = new QName();

    @Mock
    private HasExpression hasExpression;

    @Mock
    private Decision decision;

    @Mock
    private InformationItemPrimary decisionVariable;

    @Mock
    private DecisionTable expression;

    @Test
    public void testGetTypeRefOfExpression() {
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(mock(DMNModelInstrumentedBase.class));
        when(expression.getTypeRef()).thenReturn(TYPE_REF);

        assertThat(TypeRefUtils.getTypeRefOfExpression(expression, hasExpression).getTypeRef()).isEqualTo(TYPE_REF);
    }

    @Test
    public void testGetTypeRefOfExpressionWhenHasExpressionHasVariable() {
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(decision);
        when(decision.getVariable()).thenReturn(decisionVariable);
        when(decisionVariable.getTypeRef()).thenReturn(TYPE_REF);

        assertThat(TypeRefUtils.getTypeRefOfExpression(expression, hasExpression).getTypeRef()).isEqualTo(TYPE_REF);
    }
}
