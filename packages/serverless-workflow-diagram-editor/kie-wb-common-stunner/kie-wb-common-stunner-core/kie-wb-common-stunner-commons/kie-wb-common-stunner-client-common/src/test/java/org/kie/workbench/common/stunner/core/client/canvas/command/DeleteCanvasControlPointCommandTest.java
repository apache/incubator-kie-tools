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
public class DeleteCanvasControlPointCommandTest extends AbstractCanvasControlPointCommandTest {

    private DeleteCanvasControlPointCommand tested;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.tested = new DeleteCanvasControlPointCommand(edge, 0);
    }

    @Test
    public void testChecks() {
        tested = new DeleteCanvasControlPointCommand(edge, 0);
        CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        tested = new DeleteCanvasControlPointCommand(edge, 1);
        result = tested.allow(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        tested = new DeleteCanvasControlPointCommand(edge, 2);
        result = tested.allow(canvasHandler);
        assertFalse(CommandUtils.isError(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIndex() {
        tested = new DeleteCanvasControlPointCommand(edge, -1);
        tested.allow(canvasHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIndexForbidden() {
        tested = new DeleteCanvasControlPointCommand(edge, 3);
        tested.allow(canvasHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIndexDuringExecute() {
        tested = new DeleteCanvasControlPointCommand(edge, -1);
        tested.execute(canvasHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIndexForbiddenDuringExecute() {
        tested = new DeleteCanvasControlPointCommand(edge, 3);
        tested.execute(canvasHandler);
    }

    @Test
    public void testDeleteControlPoint() {
        checkExecution(true);
    }

    @Test
    public void testDeleteControlPointWhenNotVisible() {
        checkExecution(false);
    }

    private void checkExecution(boolean areControlPointsVisible) {
        when(connectorView.areControlsVisible()).thenReturn(areControlPointsVisible);
        tested = new DeleteCanvasControlPointCommand(edge, 0);
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        checkControlPointsVisibilitySwitch(areControlPointsVisible);
        verify(connectorView, times(1)).deleteControlPoint(eq(0));
        verify(connectorView, never()).updateControlPoints(any(ControlPoint[].class));
        verify(connectorView, never()).addControlPoint(any(ControlPoint.class), anyInt());
    }
}