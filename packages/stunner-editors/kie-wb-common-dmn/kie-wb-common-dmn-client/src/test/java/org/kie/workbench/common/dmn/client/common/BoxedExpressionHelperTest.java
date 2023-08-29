/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.common;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BoxedExpressionHelperTest {

    @Mock
    private Node<View, Edge> node;

    private BoxedExpressionHelper helper;

    @Before
    public void setup() {
        helper = new BoxedExpressionHelper();
    }

    @Test
    public void testGetOptionalHasExpressionWhenNodeIsBusinessKnowledgeModel() {

        final View content = mock(View.class);
        final BusinessKnowledgeModel businessKnowledgeModel = new BusinessKnowledgeModel();
        final FunctionDefinition encapsulatedLogic = mock(FunctionDefinition.class);
        businessKnowledgeModel.setEncapsulatedLogic(encapsulatedLogic);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(businessKnowledgeModel);

        final Optional<HasExpression> actualHasExpression = helper.getOptionalHasExpression(node);

        assertTrue(actualHasExpression.isPresent());
        assertEquals(businessKnowledgeModel, actualHasExpression.get().asDMNModelInstrumentedBase());
        assertEquals(encapsulatedLogic, actualHasExpression.get().getExpression());
    }

    @Test
    public void testGetOptionalHasExpressionWhenNodeIsDecision() {

        final View content = mock(View.class);
        final Decision expectedHasExpression = mock(Decision.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(expectedHasExpression);

        final Optional<HasExpression> actualHasExpression = helper.getOptionalHasExpression(node);

        assertTrue(actualHasExpression.isPresent());
        assertEquals(expectedHasExpression, actualHasExpression.get());
    }

    @Test
    public void testGetOptionalHasExpressionWhenNodeIsOtherDRGElement() {

        final View content = mock(View.class);
        final InputData expectedHasExpression = mock(InputData.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(expectedHasExpression);

        final Optional<HasExpression> actualHasExpression = helper.getOptionalHasExpression(node);

        assertFalse(actualHasExpression.isPresent());
    }

    @Test
    public void testGetOptionalExpressionWhenIsNotPresent() {

        final View content = mock(View.class);
        final Decision decision = mock(Decision.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(decision);
        when(decision.getExpression()).thenReturn(null);

        final Optional<Expression> optionalExpression = helper.getOptionalExpression(node);

        assertFalse(optionalExpression.isPresent());
    }

    @Test
    public void testGetOptionalExpressionWhenIsPresent() {

        final View content = mock(View.class);
        final Decision decision = mock(Decision.class);
        final Expression expectedExpression = mock(Expression.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(decision);
        when(decision.getExpression()).thenReturn(expectedExpression);

        final Optional<Expression> optionalExpression = helper.getOptionalExpression(node);

        assertTrue(optionalExpression.isPresent());
        assertEquals(expectedExpression, optionalExpression.get());
    }

    @Test
    public void testGetExpression() {

        final View content = mock(View.class);
        final Decision decision = mock(Decision.class);
        final Expression expectedExpression = mock(Expression.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(decision);
        when(decision.getExpression()).thenReturn(expectedExpression);

        final Expression actualExpression = helper.getExpression(node);

        assertEquals(expectedExpression, actualExpression);
    }

    @Test
    public void testGetHasExpression() {

        final View content = mock(View.class);
        final Decision expected = mock(Decision.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(expected);

        final HasExpression actual = helper.getHasExpression(node);

        assertEquals(expected, actual);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetHasExpressionWhenNodeDoesNotHaveExpression() {

        final View content = mock(View.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(new InputData());

        helper.getHasExpression(node);
    }
}
