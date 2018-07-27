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
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddControlPointCommandTest extends AbstractControlPointCommandTest {

    private AddControlPointCommand tested;
    protected ControlPoint controlPointEmptyIndex;
    protected ControlPoint controlPointNew;

    @Before
    public void setUp() {
        super.setUp();
        controlPointNew = ControlPoint.build(newLocation, 2);
        controlPointEmptyIndex = ControlPoint.build(newLocation);
        tested = spy(new AddControlPointCommand(edge, controlPointNew));
    }

    @Test
    public void testCheckError() {
        CommandResult<RuleViolation> result = new AddControlPointCommand(edge, controlPointEmptyIndex).check(graphCommandExecutionContext);
        assertTrue(CommandUtils.isError(result));
    }

    @Test
    public void testCheck() {
        CommandResult<RuleViolation> result = tested.check(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
    }

    @Test
    public void testExecuteEmpty() {
        controlPointList.clear();
        assertEquals(controlPointList.size(), 0);
        tested.execute(graphCommandExecutionContext);
        verify(viewConnector, atLeastOnce()).getControlPoints();
        assertEquals(controlPointList.size(), 1);
        assertEquals(controlPointList.get(0), controlPointNew);
        assertEquals(controlPointNew.getIndex(), 0, 0);
    }

    @Test
    public void testExecute() {
        assertEquals(controlPointList.size(), 3);
        tested.execute(graphCommandExecutionContext);
        verify(viewConnector, atLeastOnce()).getControlPoints();
        assertEquals(controlPointList.size(), 4);
        assertEquals(controlPointList.get(2), controlPointNew);
        assertEquals(controlPointNew.getIndex(), 2, 0);
        assertEquals(controlPoint1.getIndex(), 0, 0);
        assertEquals(controlPoint2.getIndex(), 1, 0);
        assertEquals(controlPoint3.getIndex(), 3, 0);
    }

    @Test
    public void undo() {
        tested.undo(graphCommandExecutionContext);
        verify(tested).newUndoCommand();
    }
}