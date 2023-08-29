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
public class AddControlPointCommandTest extends AbstractControlPointCommandTest {

    private AddControlPointCommand tested;
    private ControlPoint newControlPoint;

    @Before
    public void setUp() {
        super.setUp();
        newControlPoint = ControlPoint.build(5, 5);
    }

    @Test
    public void testCheck() {
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 0);
        CommandResult<RuleViolation> result = tested.check(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 1);
        result = tested.check(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 2);
        result = tested.check(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 3);
        result = tested.check(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIndex() {
        // index must be equals or greater than 0
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, -1);
        tested.check(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIndexDuringExecute() {
        // index must be equals or greater than 0
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, -1);
        tested.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCPIndex() {
        // Index cannot be bigger than actual CP's length
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 4);
        tested.check(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCPIndexDuringExecute() {
        // Index cannot be bigger than actual CP's length
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 4);
        tested.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCP() {
        // the CP's location must be present
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 2);
        newControlPoint.setLocation(null);
        tested.check(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCPDuringExecute() {
        // the CP's location must be present
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 2);
        newControlPoint.setLocation(null);
        tested.execute(graphCommandExecutionContext);
    }

    @Test
    public void testAddControlPointAt0() {
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 0);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        assertFalse(CommandUtils.isError(result));
        verify(viewConnector, times(1)).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(4, controlPoints.length);
        assertEquals(newControlPoint, controlPoints[0]);
        assertEquals(controlPoint1, controlPoints[1]);
        assertEquals(controlPoint2, controlPoints[2]);
        assertEquals(controlPoint3, controlPoints[3]);
    }

    @Test
    public void testAddControlPointAt1() {
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 1);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        assertFalse(CommandUtils.isError(result));
        verify(viewConnector, times(1)).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(4, controlPoints.length);
        assertEquals(controlPoint1, controlPoints[0]);
        assertEquals(newControlPoint, controlPoints[1]);
        assertEquals(controlPoint2, controlPoints[2]);
        assertEquals(controlPoint3, controlPoints[3]);
    }

    @Test
    public void testAddControlPointAt2() {
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 2);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        assertFalse(CommandUtils.isError(result));
        verify(viewConnector, times(1)).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(4, controlPoints.length);
        assertEquals(controlPoint1, controlPoints[0]);
        assertEquals(controlPoint2, controlPoints[1]);
        assertEquals(newControlPoint, controlPoints[2]);
        assertEquals(controlPoint3, controlPoints[3]);
    }

    @Test
    public void testAddControlPointAt3() {
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 3);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        assertFalse(CommandUtils.isError(result));
        verify(viewConnector, times(1)).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(4, controlPoints.length);
        assertEquals(controlPoint1, controlPoints[0]);
        assertEquals(controlPoint2, controlPoints[1]);
        assertEquals(controlPoint3, controlPoints[2]);
        assertEquals(newControlPoint, controlPoints[3]);
    }

    @Test
    public void testAddControlPointFirstTime() {
        when(viewConnector.getControlPoints()).thenReturn(null);
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 0);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        assertFalse(CommandUtils.isError(result));
        verify(viewConnector, times(1)).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(1, controlPoints.length);
        assertEquals(newControlPoint, controlPoints[0]);
    }

    @Test
    public void testAddControlPointAndUndoIt() {
        // Add a new CP at 0.
        tested = new AddControlPointCommand(EDGE_UUID, newControlPoint, 0);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        assertFalse(CommandUtils.isError(result));
        verify(viewConnector, atLeastOnce()).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(4, controlPoints.length);
        assertEquals(newControlPoint, controlPoints[0]);
        assertEquals(controlPoint1, controlPoints[1]);
        assertEquals(controlPoint2, controlPoints[2]);
        assertEquals(controlPoint3, controlPoints[3]);
        // Undo it.
        when(viewConnector.getControlPoints())
                .thenReturn(new ControlPoint[]{newControlPoint, controlPoint1, controlPoint2, controlPoint3});
        result = tested.undo(graphCommandExecutionContext);
        ArgumentCaptor<ControlPoint[]> pointsCaptor2 = ArgumentCaptor.forClass(ControlPoint[].class);
        assertFalse(CommandUtils.isError(result));
        verify(viewConnector, atLeastOnce()).setControlPoints(pointsCaptor2.capture());
        controlPoints = pointsCaptor2.getValue();
        assertNotNull(controlPoints);
        assertEquals(3, controlPoints.length);
        assertEquals(controlPoint1, controlPoints[0]);
        assertEquals(controlPoint2, controlPoints[1]);
        assertEquals(controlPoint3, controlPoints[2]);
    }
}