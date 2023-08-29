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
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UpdateElementPositionCommandTest extends AbstractCanvasCommandTest {

    @Mock
    private Node candidate;
    @Mock
    private View<?> content;

    private UpdateElementPositionCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
        when(candidate.getUUID()).thenReturn("uuid1");
        when(candidate.getContent()).thenReturn(content);
        when(content.getBounds()).thenReturn(Bounds.create(3d, 27d, 50d, 50d));
        this.tested = new UpdateElementPositionCommand(candidate,
                                                       new Point2D(100d, 200d));
    }

    @Test
    public void testGetGraphCommand() {
        final org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPositionCommand graphCommand =
                (org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPositionCommand) tested.newGraphCommand(canvasHandler);
        assertNotNull(graphCommand);
        assertEquals(candidate,
                     graphCommand.getNode());
        assertEquals(100d,
                     graphCommand.getLocation().getX(),
                     0);
        assertEquals(200d,
                     graphCommand.getLocation().getY(),
                     0);
        assertEquals(3d,
                     graphCommand.getPreviousLocation().getX(),
                     0);
        assertEquals(27d,
                     graphCommand.getPreviousLocation().getY(),
                     0);
    }

    @Test
    public void testGetCanvasCommand() {
        final UpdateCanvasElementPositionCommand canvasCommand =
                (UpdateCanvasElementPositionCommand) tested.newCanvasCommand(canvasHandler);
        assertNotNull(canvasCommand);
        assertEquals(candidate,
                     canvasCommand.getElement());
    }
}
