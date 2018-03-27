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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.mockito.InOrder;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCanvasControlPointCommandTest extends AbstractCanvasControlPointCommandTest {

    private DeleteCanvasControlPointCommand deleteCanvasControlPointCommand;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.deleteCanvasControlPointCommand = spy(new DeleteCanvasControlPointCommand(edge, controlPoint1));
    }

    @Test
    public void execute() {
        CommandResult<CanvasViolation> result = deleteCanvasControlPointCommand.execute(canvasHandler);
        InOrder inOrder = inOrder(shape);
        inOrder.verify(shape).applyState(ShapeState.NONE);
        inOrder.verify(shape).removeControlPoints(controlPoint1);
        inOrder.verify(shape).applyState(ShapeState.SELECTED);
        assertFalse(CommandUtils.isError(result));
    }

    @Test
    public void undo() {
        deleteCanvasControlPointCommand.undo(canvasHandler);
        verify(deleteCanvasControlPointCommand).newUndoCommand();
    }
}