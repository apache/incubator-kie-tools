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

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPointImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateCanvasControlPointPositionCommandTest extends AbstractCanvasControlPointCommandTest {

    private UpdateCanvasControlPointPositionCommand updateCanvasControlPointPositionCommand;

    private Point2D newPosition;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        newPosition = new Point2D(10, 10);
        updateCanvasControlPointPositionCommand = spy(new UpdateCanvasControlPointPositionCommand(edge, controlPoint1, newPosition));
    }

    @Test
    public void testInitialize() {
        updateCanvasControlPointPositionCommand.initialize(canvasHandler);

        assertEquals(updateCanvasControlPointPositionCommand.getCommands().size(), 2);
        Command<AbstractCanvasHandler, CanvasViolation> firstCommand = updateCanvasControlPointPositionCommand.getCommands().get(0);
        Command<AbstractCanvasHandler, CanvasViolation> secondCommand = updateCanvasControlPointPositionCommand.getCommands().get(1);
        assertCommandsByPosition(firstCommand, controlPoint1.getLocation(), secondCommand, newPosition);
    }

    private void assertCommandsByPosition(Command<AbstractCanvasHandler, CanvasViolation> firstCommand,
                                          Point2D firstPosition,
                                          Command<AbstractCanvasHandler, CanvasViolation> secondCommand,
                                          Point2D secondPosition) {
        assertTrue(firstCommand instanceof DeleteCanvasControlPointCommand);
        assertEquals(((DeleteCanvasControlPointCommand) firstCommand).getControlPoints()[0].getLocation(), firstPosition);
        assertTrue(secondCommand instanceof AddCanvasControlPointCommand);
        assertEquals(((AddCanvasControlPointCommand) secondCommand).getControlPoints()[0].getLocation(), secondPosition);
    }

    @Test
    public void testInitializeCanvasAlreadyUpdated() {
        when(shape.getControlPoints()).thenReturn(Arrays.asList(new ControlPointImpl(newPosition)));
        updateCanvasControlPointPositionCommand.initialize(canvasHandler);
        assertEquals(updateCanvasControlPointPositionCommand.getCommands().size(), 0);
    }

    @Test
    public void undo() {
        updateCanvasControlPointPositionCommand.undo(canvasHandler);
        verify(updateCanvasControlPointPositionCommand).newUndoCommand();

        CompositeCommand<AbstractCanvasHandler, CanvasViolation> undoCommand = updateCanvasControlPointPositionCommand.newUndoCommand();
        assertEquals(undoCommand.size(), 2);
        Command<AbstractCanvasHandler, CanvasViolation> firstUndoCommand = undoCommand.getCommands().get(0);
        Command<AbstractCanvasHandler, CanvasViolation> secondUndoCommand = undoCommand.getCommands().get(1);
        assertCommandsByPosition(firstUndoCommand, newPosition, secondUndoCommand, controlPoint1.getLocation());
    }
}