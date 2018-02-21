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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpdateControlPointPositionCommandTest extends AbstractControlPointCommandTest {

    private UpdateControlPointPositionCommand updateControlPointPositionCommand;

    @Before
    public void setUp() {
        super.setUp();
        updateControlPointPositionCommand = spy(new UpdateControlPointPositionCommand(edge, controlPoint1, newLocation));
    }

    @Test
    public void initialize() {
        updateControlPointPositionCommand.initialize(graphCommandExecutionContext);
        List<Command<GraphCommandExecutionContext, RuleViolation>> commands = updateControlPointPositionCommand.getCommands();
        assertEquals(commands.size(), 2, 0);
        assertCommandsByPosition(commands.get(0), controlPoint1.getLocation(), commands.get(1), newLocation);
    }

    private void assertCommandsByPosition(Command<GraphCommandExecutionContext, RuleViolation> firstCommand,
                                          Point2D firstPosition,
                                          Command<GraphCommandExecutionContext, RuleViolation> secondCommand,
                                          Point2D secondPosition) {
        assertTrue(firstCommand instanceof DeleteControlPointCommand);
        assertEquals(((DeleteControlPointCommand) firstCommand).getControlPoints()[0].getLocation(), firstPosition);
        assertTrue(secondCommand instanceof AddControlPointCommand);
        assertEquals(((AddControlPointCommand) secondCommand).getControlPoints()[0].getLocation(), secondPosition);
    }

    @Test
    public void undo() {
        updateControlPointPositionCommand.undo(graphCommandExecutionContext);
        verify(updateControlPointPositionCommand).newUndoCommand();
        List<Command<GraphCommandExecutionContext, RuleViolation>> commands = updateControlPointPositionCommand.newUndoCommand().getCommands();
        assertCommandsByPosition(commands.get(0), newLocation, commands.get(1), controlPoint1.getLocation());
    }
}
