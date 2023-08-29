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


package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UpdateCanvasControlPointPositionCommandTest extends AbstractCanvasControlPointCommandTest {

    private static final ControlPoint newControlPoint1 = ControlPoint.build(0.1, 0.2);
    private static final ControlPoint newControlPoint2 = ControlPoint.build(0.3, 0.4);
    private static final ControlPoint newControlPoint3 = ControlPoint.build(0.4, 0.5);

    private UpdateCanvasControlPointPositionCommand tested;
    private ControlPoint[] newControlPoints;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        newControlPoints = new ControlPoint[]{newControlPoint1, newControlPoint2, newControlPoint3};
    }

    @Test
    public void testCheck() {
        tested = new UpdateCanvasControlPointPositionCommand(edge, newControlPoints);
        CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        assertFalse(CommandUtils.isError(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotUpdateCPs() {
        newControlPoints = new ControlPoint[]{newControlPoint1, newControlPoint2};
        tested = new UpdateCanvasControlPointPositionCommand(edge, newControlPoints);
        tested.allow(canvasHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotUpdateCPsDuringExecute() {
        newControlPoints = new ControlPoint[]{newControlPoint1, newControlPoint2};
        tested = new UpdateCanvasControlPointPositionCommand(edge, newControlPoints);
        tested.execute(canvasHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotUpdateCPs2() {
        viewConnector.setControlPoints(new ControlPoint[]{controlPoint1, controlPoint2});
        tested = new UpdateCanvasControlPointPositionCommand(edge, newControlPoints);
        tested.allow(canvasHandler);
    }

    @Test
    public void execute() {
        checkExecution(true);
    }

    @Test
    public void executeWhenCPsNotVisible() {
        checkExecution(false);
    }

    private void checkExecution(boolean areControlPointsVisible) {
        when(connectorView.areControlsVisible()).thenReturn(areControlPointsVisible);
        tested = new UpdateCanvasControlPointPositionCommand(edge, newControlPoints);
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        checkControlPointsVisibilitySwitch(areControlPointsVisible);
        verify(connectorView, times(1)).updateControlPoints(eq(newControlPoints));
        verify(connectorView, never()).addControlPoint(any(ControlPoint.class), anyInt());
        verify(connectorView, never()).deleteControlPoint(anyInt());
    }
}