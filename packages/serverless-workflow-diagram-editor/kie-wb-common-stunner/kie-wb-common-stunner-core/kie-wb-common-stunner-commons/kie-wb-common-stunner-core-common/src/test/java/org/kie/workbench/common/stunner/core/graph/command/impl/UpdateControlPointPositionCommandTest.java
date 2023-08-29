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
public class UpdateControlPointPositionCommandTest extends AbstractControlPointCommandTest {

    private static final ControlPoint newControlPoint1 = ControlPoint.build(0.1, 0.2);
    private static final ControlPoint newControlPoint2 = ControlPoint.build(0.3, 0.4);
    private static final ControlPoint newControlPoint3 = ControlPoint.build(0.4, 0.5);

    private UpdateControlPointPositionCommand tested;
    private ControlPoint[] newControlPoints;

    @Before
    public void setUp() {
        super.setUp();
        newControlPoints = new ControlPoint[]{newControlPoint1, newControlPoint2, newControlPoint3};
    }

    @Test
    public void testCheck() {
        tested = new UpdateControlPointPositionCommand(EDGE_UUID, newControlPoints);
        CommandResult<RuleViolation> result = tested.check(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotUpdateCPs() {
        newControlPoints = new ControlPoint[]{newControlPoint1, newControlPoint2};
        tested = new UpdateControlPointPositionCommand(EDGE_UUID, newControlPoints);
        tested.check(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotUpdateCPs2() {
        when(viewConnector.getControlPoints()).thenReturn(new ControlPoint[]{controlPoint1, controlPoint2});
        tested = new UpdateControlPointPositionCommand(EDGE_UUID, newControlPoints);
        tested.check(graphCommandExecutionContext);
    }

    @Test
    public void testUpdateControlPoints() {
        tested = new UpdateControlPointPositionCommand(EDGE_UUID, newControlPoints);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        verify(viewConnector, times(1)).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(3, controlPoints.length);
        assertEquals(newControlPoint1, controlPoints[0]);
        assertEquals(newControlPoint2, controlPoints[1]);
        assertEquals(newControlPoint3, controlPoints[2]);
    }

    @Test
    public void testUpdateControlPointsAndUndoIt() {
        // Update the CPs.
        tested = new UpdateControlPointPositionCommand(EDGE_UUID, newControlPoints);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        assertFalse(CommandUtils.isError(result));
        ArgumentCaptor<ControlPoint[]> pointsCaptor = ArgumentCaptor.forClass(ControlPoint[].class);
        verify(viewConnector, atLeastOnce()).setControlPoints(pointsCaptor.capture());
        ControlPoint[] controlPoints = pointsCaptor.getValue();
        assertNotNull(controlPoints);
        assertEquals(3, controlPoints.length);
        assertEquals(newControlPoint1, controlPoints[0]);
        assertEquals(newControlPoint2, controlPoints[1]);
        assertEquals(newControlPoint3, controlPoints[2]);
        // Undo it.
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
