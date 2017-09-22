/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddNodeCommandTest extends AbstractCanvasCommandTest {

    @Mock
    private Node candidate;

    private AddNodeCommand tested;

    @Before
    public void setup() throws Exception {
        super.setup();
        when(candidate.getUUID()).thenReturn("uuid1");
        this.tested = new AddNodeCommand(candidate,
                                         SHAPE_SET_ID);
    }

    @Test
    public void testGetGraphCommand() {
        final org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand graphCommand =
                (org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand) tested.newGraphCommand(canvasHandler);
        assertNotNull(graphCommand);
        assertEquals(candidate,
                     graphCommand.getCandidate());
    }

    @Test
    public void testGetCanvasCommand() {
        final AddCanvasNodeCommand canvasCommand =
                (AddCanvasNodeCommand) tested.newCanvasCommand(canvasHandler);
        assertNotNull(canvasCommand);
        assertEquals(candidate,
                     canvasCommand.getCandidate());
        assertEquals(SHAPE_SET_ID,
                     canvasCommand.getShapeSetId());
    }
}
