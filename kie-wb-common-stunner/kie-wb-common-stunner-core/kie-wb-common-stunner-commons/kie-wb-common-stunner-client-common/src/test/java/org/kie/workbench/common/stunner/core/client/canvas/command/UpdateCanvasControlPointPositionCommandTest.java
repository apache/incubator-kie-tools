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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpdateCanvasControlPointPositionCommandTest extends AbstractCanvasControlPointCommandTest {

    private UpdateCanvasControlPointPositionCommand tested;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.tested = spy(new UpdateCanvasControlPointPositionCommand(edge, controlPoint1));
    }

    @Test
    public void execute() {
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        verify(shape, times(1)).updateControlPoint(eq(controlPoint1));
    }

    @Test
    public void executeAndUndo() {
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        CommandResult<CanvasViolation> undo = tested.undo(canvasHandler);
        assertFalse(CommandUtils.isError(undo));
        verify(shape, times(2)).updateControlPoint(eq(controlPoint1));
    }
}