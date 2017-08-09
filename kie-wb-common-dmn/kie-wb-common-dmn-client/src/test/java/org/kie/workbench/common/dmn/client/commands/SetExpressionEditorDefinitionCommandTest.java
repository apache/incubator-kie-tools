/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.commands;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SetExpressionEditorDefinitionCommandTest {

    @Mock
    private HasExpression hasExpression;

    @Mock
    private ExpressionEditorView.Presenter presenter;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    @Mock
    private Expression oldExpression;

    @Mock
    private Expression newExpression;

    @Captor
    private ArgumentCaptor<Optional<Expression>> oExpressionCaptor;

    private Optional<Expression> oExpression;

    private SetExpressionTypeCommand command;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(hasExpression.getExpression()).thenReturn(oldExpression);

        this.oExpression = Optional.of(newExpression);
        this.command = new SetExpressionTypeCommand(hasExpression,
                                                    oExpression,
                                                    presenter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkGraphCommand() {
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).allow(graphCommandExecutionContext));
        verify(presenter,
               never()).setExpression(anyObject());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeGraphCommand() {
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext));
        verify(presenter,
               never()).setExpression(anyObject());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void undoGraphCommand() {
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).undo(graphCommandExecutionContext));
        verify(presenter,
               never()).setExpression(anyObject());
    }

    @Test
    public void allowCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).allow(canvasHandler));
        verify(presenter,
               never()).setExpression(anyObject());
    }

    @Test
    public void executeCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        assertExpression(newExpression);
    }

    @Test
    public void undoCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        assertExpression(oldExpression);
    }

    private void assertExpression(final Expression expression) {
        verify(presenter).setExpression(oExpressionCaptor.capture());

        final Optional<Expression> oExpression = oExpressionCaptor.getValue();
        assertTrue(oExpression.isPresent());
        assertEquals(expression,
                     oExpression.get());
    }
}
