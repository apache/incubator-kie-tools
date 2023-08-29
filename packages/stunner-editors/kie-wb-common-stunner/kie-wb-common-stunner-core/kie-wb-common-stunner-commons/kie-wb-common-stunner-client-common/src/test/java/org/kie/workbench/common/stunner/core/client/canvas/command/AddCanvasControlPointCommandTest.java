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
public class AddCanvasControlPointCommandTest extends AbstractCanvasControlPointCommandTest {

    private AddCanvasControlPointCommand tested;
    private ControlPoint newControlPoint;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        newControlPoint = ControlPoint.build(4, 4);
        tested = new AddCanvasControlPointCommand(edge, newControlPoint, 2);
    }

    @Test
    public void testAllow() {
        tested = new AddCanvasControlPointCommand(edge, newControlPoint, 0);
        CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        tested = new AddCanvasControlPointCommand(edge, newControlPoint, 1);
        result = tested.allow(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        tested = new AddCanvasControlPointCommand(edge, newControlPoint, 2);
        result = tested.allow(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        assertFalse(CommandUtils.isError(result));
        tested = new AddCanvasControlPointCommand(edge, newControlPoint, 3);
        result = tested.allow(canvasHandler);
        assertFalse(CommandUtils.isError(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCPIndex() {
        // Index cannot be bigger than actual CP's length
        tested = new AddCanvasControlPointCommand(edge, newControlPoint, 4);
        tested.allow(canvasHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCPIndexDuringExecute() {
        // Index cannot be bigger than actual CP's length
        tested = new AddCanvasControlPointCommand(edge, newControlPoint, 4);
        tested.execute(canvasHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCP() {
        // the CP's location must be present
        tested = new AddCanvasControlPointCommand(edge, newControlPoint, 2);
        newControlPoint.setLocation(null);
        tested.allow(canvasHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCPDuringExecute() {
        // the CP's location must be present
        tested = new AddCanvasControlPointCommand(edge, newControlPoint, 2);
        newControlPoint.setLocation(null);
        tested.execute(canvasHandler);
    }

    @Test
    public void execute() {
        checkExecution(true);
    }

    @Test
    public void executeWithCPsNotVisible() {
        checkExecution(false);
    }

    private void checkExecution(boolean areControlPointsVisible) {
        when(connectorView.areControlsVisible()).thenReturn(areControlPointsVisible);
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        checkControlPointsVisibilitySwitch(areControlPointsVisible);
        verify(connectorView, times(1)).addControlPoint(eq(newControlPoint), eq(2));
        verify(connectorView, never()).updateControlPoints(any(ControlPoint[].class));
        verify(connectorView, never()).deleteControlPoint(anyInt());
    }
}