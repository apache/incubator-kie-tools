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

package org.kie.workbench.common.dmn.client.editors.expressions.commands;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FunctionProps;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FillFunctionExpressionCommandTest {

    private FillFunctionExpressionCommand command;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private FunctionProps expressionProps;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private FunctionDefinition existingExpression;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Before
    public void setup() {

        when(hasExpression.getExpression()).thenReturn(existingExpression);

        command = spy(new FillFunctionExpressionCommand(hasExpression,
                                                        expressionProps,
                                                        editorSelectedEvent,
                                                        "nodeUUID",
                                                        itemDefinitionUtils,
                                                        Optional.empty()));
        doNothing().when(command).fill(any(), any());
    }

    @Test
    public void testFill() {
        command.fill();
        verify(command).fill(existingExpression, expressionProps);
    }

    @Test
    public void testGetNewExpression() {
        final Expression newExpression = command.getNewExpression();
        assertTrue(newExpression instanceof FunctionDefinition);
    }

    @Test
    public void testIsCurrentExpressionOfTheSameType_WhenItIs() {
        assertTrue(command.isCurrentExpressionOfTheSameType());
    }

    @Test
    public void testIsCurrentExpressionOfTheSameType_WhenItIsNot() {
        when(hasExpression.getExpression()).thenReturn(mock(Expression.class));
        assertFalse(command.isCurrentExpressionOfTheSameType());
    }
}
