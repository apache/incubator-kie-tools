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


package org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.EdgeClipboard;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LocalClipboardControlTest {

    private LocalClipboardControl localClipboardControl;
    private TestingGraphInstanceBuilder.TestGraph2 graphInstance;

    @Before
    public void setUp() throws Exception {
        localClipboardControl = new LocalClipboardControl();
        this.graphInstance = TestingGraphInstanceBuilder.newGraph2(new TestingGraphMockHandler());
    }

    @Test
    public void testSetAndGet() {
        localClipboardControl.set(graphInstance.startNode);
        assertTrue(localClipboardControl.getElements().stream().anyMatch(node -> Objects.equals(node, graphInstance.startNode)));
        localClipboardControl.set(graphInstance.intermNode, graphInstance.endNode);
        assertFalse(localClipboardControl.getElements().stream().anyMatch(node -> Objects.equals(node, graphInstance.startNode)));
        assertTrue(localClipboardControl.getElements().stream().anyMatch(node -> Objects.equals(node, graphInstance.intermNode)));
        assertTrue(localClipboardControl.getElements().stream().anyMatch(node -> Objects.equals(node, graphInstance.endNode)));
    }

    @Test
    public void testRemove() {
        localClipboardControl.set(graphInstance.startNode);
        assertTrue(localClipboardControl.hasElements());
        localClipboardControl.remove(graphInstance.startNode);
        assertFalse(localClipboardControl.hasElements());
    }

    @Test
    public void testClear() {
        localClipboardControl.set(graphInstance.startNode, graphInstance.intermNode, graphInstance.endNode);
        assertTrue(localClipboardControl.hasElements());
        localClipboardControl.clear();
        assertFalse(localClipboardControl.hasElements());
    }

    @Test
    public void testGetParent() {
        localClipboardControl.set(graphInstance.startNode);
        String parentUUID = localClipboardControl.getParent(graphInstance.startNode.getUUID());
        assertEquals(parentUUID, graphInstance.parentNode.getUUID());
    }

    @Test
    public void testEdgeMap() {
        final EdgeClipboard clipboard = localClipboardControl.buildNewEdgeClipboard("1D", null, "2D", null);
        localClipboardControl.getEdgeMap().put("Node1", clipboard);
        assertEquals(localClipboardControl.getEdgeMap().get("Node1").getSource(), clipboard.getSource());
        assertEquals(localClipboardControl.getEdgeMap().get("Node1").getSourceConnection(), clipboard.getSourceConnection());

        assertEquals(localClipboardControl.getEdgeMap().get("Node1").getTarget(), clipboard.getTarget());
        assertEquals(localClipboardControl.getEdgeMap().get("Node1").getTargetConnection(), clipboard.getTargetConnection());
    }
}