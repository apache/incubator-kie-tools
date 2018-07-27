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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeleteControlPointCommandTest extends AbstractControlPointCommandTest {

    private DeleteControlPointCommand deleteControlPointCommand;

    @Before
    public void setUp() {
        super.setUp();
        deleteControlPointCommand = spy(new DeleteControlPointCommand(edge, controlPoint1));
    }

    @Test
    public void check() {
        CommandResult<RuleViolation> result = deleteControlPointCommand.check(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
    }

    @Test
    public void execute() {
        assertEquals(controlPointList.size(), 3);
        deleteControlPointCommand.execute(graphCommandExecutionContext);
        assertEquals(controlPointList.size(), 2);
        assertEquals(controlPointList.get(0), controlPoint2);
        assertEquals(controlPointList.get(1), controlPoint3);
        assertEquals(controlPoint2.getIndex(), 0, 0);
        assertEquals(controlPoint3.getIndex(), 1, 0);
    }

    @Test
    public void undo() {
        deleteControlPointCommand.undo(graphCommandExecutionContext);
        verify(deleteControlPointCommand).newUndoCommand();
    }
}