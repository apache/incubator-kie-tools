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


package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeleteControlPointCommandTest extends AbstractControlPointCommandTest {

    private DeleteControlPointCommand tested;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testChecks() {
        tested = new DeleteControlPointCommand(EDGE_UUID, 0);
        CommandResult<RuleViolation> result = tested.check(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
        tested = new DeleteControlPointCommand(EDGE_UUID, 1);
        result = tested.check(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
        tested = new DeleteControlPointCommand(EDGE_UUID, 2);
        result = tested.check(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIndex() {
        tested = new DeleteControlPointCommand(EDGE_UUID, -1);
        tested.check(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIndexForbidden() {
        tested = new DeleteControlPointCommand(EDGE_UUID, 3);
        tested.check(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIndexDuringExecute() {
        tested = new DeleteControlPointCommand(EDGE_UUID, -1);
        tested.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIndexForbiddenDuringExecute() {
        tested = new DeleteControlPointCommand(EDGE_UUID, 3);
        tested.execute(graphCommandExecutionContext);
    }

    @Test
    public void testDeleteControlPointAt0() {
        tested = new DeleteControlPointCommand(EDGE_UUID, 0);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        assertFalse(CommandUtils.isError(result));
        verify(viewConnector, times(1)).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(2, controlPoints.length);
        assertEquals(controlPoint2, controlPoints[0]);
        assertEquals(controlPoint3, controlPoints[1]);
    }

    @Test
    public void testDeleteControlPointAt1() {
        tested = new DeleteControlPointCommand(EDGE_UUID, 1);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        assertFalse(CommandUtils.isError(result));
        verify(viewConnector, times(1)).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(2, controlPoints.length);
        assertEquals(controlPoint1, controlPoints[0]);
        assertEquals(controlPoint3, controlPoints[1]);
    }

    @Test
    public void testDeleteControlPointAt2() {
        tested = new DeleteControlPointCommand(EDGE_UUID, 2);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        assertFalse(CommandUtils.isError(result));
        verify(viewConnector, times(1)).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(2, controlPoints.length);
        assertEquals(controlPoint1, controlPoints[0]);
        assertEquals(controlPoint2, controlPoints[1]);
    }

    @Test
    public void testDeleteControlPointAndUndoIt() {
        // Delete CP at 0.
        tested = new DeleteControlPointCommand(EDGE_UUID, 0);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        assertFalse(CommandUtils.isError(result));
        verify(viewConnector, atLeastOnce()).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(2, controlPoints.length);
        assertEquals(controlPoint2, controlPoints[0]);
        assertEquals(controlPoint3, controlPoints[1]);
        // Undo it.
        when(viewConnector.getControlPoints())
                .thenReturn(new ControlPoint[]{controlPoint2, controlPoint3});
        result = tested.undo(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
        pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        verify(viewConnector, atLeastOnce()).setControlPoints(pointsCaptor.capture());
        controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(3, controlPoints.length);
        assertEquals(controlPoint1, controlPoints[0]);
        assertEquals(controlPoint2, controlPoints[1]);
        assertEquals(controlPoint3, controlPoints[2]);
    }
}