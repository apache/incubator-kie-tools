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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.UpdateCanvasNodeNameCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.util.ExpressionState;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class SaveCurrentStateCommandTest {

    private static final String NODE_UUID = "uuid";

    private SaveCurrentStateCommand command;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private ExpressionEditorView view;

    @Mock
    private UpdateCanvasNodeNameCommand updateCanvasNodeNameCommand;

    private Optional<HasName> hasName = Optional.empty();

    @Before
    public void setup() {
        command = spy(new SaveCurrentStateCommand(hasExpression,
                                                  editorSelectedEvent,
                                                  NODE_UUID,
                                                  hasName,
                                                  updateCanvasNodeNameCommand));
    }

    @Test
    public void testExecute_WhenThereIsNotStateBeforeUndo() {

        final CommandResult<CanvasViolation> result = command.execute(mock(AbstractCanvasHandler.class));

        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
        assertNull(command.getStateBeforeUndo());
    }

    @Test
    public void testExecute_WhenThereIsStateBeforeUndo() {

        final ExpressionState stateBeforeUndo = mock(ExpressionState.class);
        command.setStateBeforeUndo(stateBeforeUndo);

        final CommandResult<CanvasViolation> result = command.execute(mock(AbstractCanvasHandler.class));

        verify(stateBeforeUndo).apply();
        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
        assertNull(command.getStateBeforeUndo());
    }

    @Test
    public void testUndo_WhenThereIsStateBeforeUndo() {

        final ExpressionState originalState = mock(ExpressionState.class);
        final ExpressionState stateBeforeUndo = mock(ExpressionState.class);

        doReturn(originalState).when(command).getOriginalState();

        command.setStateBeforeUndo(stateBeforeUndo);

        final CommandResult<CanvasViolation> result = command.undo(mock(AbstractCanvasHandler.class));

        verify(originalState).apply();
        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
        assertEquals(stateBeforeUndo, command.getStateBeforeUndo());
    }

    @Test
    public void testUndo_WhenThereIsNoStateBeforeUndo() {

        final ExpressionState originalState = mock(ExpressionState.class);
        command.setStateBeforeUndo(null);

        doReturn(originalState).when(command).getOriginalState();

        final CommandResult<CanvasViolation> result = command.undo(mock(AbstractCanvasHandler.class));

        final ExpressionState stateBeforeUndo = command.getStateBeforeUndo();

        verify(originalState).apply();

        assertEquals(hasExpression, stateBeforeUndo.getHasExpression());
        assertEquals(editorSelectedEvent, stateBeforeUndo.getEditorSelectedEvent());
        assertEquals(NODE_UUID, stateBeforeUndo.getNodeUUID());
        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
    }
}
