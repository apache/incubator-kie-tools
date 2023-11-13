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
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UnDockNodeCommandTest extends AbstractCanvasCommandTest {

    @Mock
    private Node parent;
    @Mock
    private Node candidate;

    private UnDockNodeCommand tested;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(parent.getUUID()).thenReturn("uuid1");
        when(candidate.getUUID()).thenReturn("uuid2");
        this.tested = new UnDockNodeCommand(parent,
                                            candidate);
    }

    @Test
    public void testGetGraphCommand() {
        final org.kie.workbench.common.stunner.core.graph.command.impl.UnDockNodeCommand graphCommand =
                (org.kie.workbench.common.stunner.core.graph.command.impl.UnDockNodeCommand) tested.newGraphCommand(canvasHandler);
        assertNotNull(graphCommand);
        assertEquals(parent,
                     graphCommand.getParent());
        assertEquals(candidate,
                     graphCommand.getCandidate());
    }

    @Test
    public void testGetCanvasCommand() {
        final CanvasUndockNodeCommand canvasCommand =
                (CanvasUndockNodeCommand) tested.newCanvasCommand(canvasHandler);
        assertNotNull(canvasCommand);
        assertEquals(parent,
                     canvasCommand.getParent());
        assertEquals(candidate,
                     canvasCommand.getChild());
    }
}
