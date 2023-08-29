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

import java.util.Collections;

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
public class SetChildrenCommandTest extends AbstractCanvasCommandTest {

    private static final String P_ID = "p1";
    private static final String ID = "e1";

    @Mock
    private Node parent;
    @Mock
    private Node candidate;

    private SetChildrenCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
        when(candidate.getUUID()).thenReturn(ID);
        when(parent.getUUID()).thenReturn(P_ID);
        this.tested = new SetChildrenCommand(parent,
                                             Collections.singleton(candidate));
    }

    @Test
    public void testGetGraphCommand() {
        final org.kie.workbench.common.stunner.core.graph.command.impl.SetChildrenCommand graphCommand =
                (org.kie.workbench.common.stunner.core.graph.command.impl.SetChildrenCommand) tested.newGraphCommand(canvasHandler);
        assertNotNull(graphCommand);
        assertEquals(candidate,
                     graphCommand.getCandidates().iterator().next());
        assertEquals(parent,
                     graphCommand.getParent());
    }

    @Test
    public void testGetCanvasCommand() {
        final SetCanvasChildrenCommand canvasCommand =
                (SetCanvasChildrenCommand) tested.newCanvasCommand(canvasHandler);
        assertNotNull(canvasCommand);
        assertEquals(candidate,
                     canvasCommand.getCandidates().iterator().next());
        assertEquals(parent,
                     canvasCommand.getParent());
    }
}
