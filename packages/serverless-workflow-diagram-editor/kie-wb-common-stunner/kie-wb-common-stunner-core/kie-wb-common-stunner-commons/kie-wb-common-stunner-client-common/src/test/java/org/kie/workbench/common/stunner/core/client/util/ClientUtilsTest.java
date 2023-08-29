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


package org.kie.workbench.common.stunner.core.client.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientUtilsTest {

    private static final String ITEM_UUID = "ITEM_UUID";

    @Mock
    private EditorSession editorSession;

    @Mock
    private ViewerSession viewerSession;

    @Mock
    private SelectionControl selectionControl;

    @Mock
    private Graph graph;

    @Mock
    private Diagram diagram;

    private List<String> selectedItems;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(diagram.getGraph()).thenReturn(graph);
        selectedItems = new ArrayList<>();
        selectedItems.add(ITEM_UUID);
        when(selectionControl.getSelectedItems()).thenReturn(selectedItems);
        when(editorSession.getSelectionControl()).thenReturn(selectionControl);
        when(viewerSession.getSelectionControl()).thenReturn(selectionControl);
    }

    @Test
    public void testGetSelectedElementUUIDWhenSelected() {
        assertEquals(ITEM_UUID, ClientUtils.getSelectedElementUUID(viewerSession));
        assertEquals(ITEM_UUID, ClientUtils.getSelectedElementUUID(editorSession));
    }

    @Test
    public void testGetSelectedElementUUIDWhenNotSelected() {
        selectedItems.clear();
        assertNull(ClientUtils.getSelectedElementUUID(viewerSession));
        assertNull(ClientUtils.getSelectedElementUUID(editorSession));
    }

    @Test
    public void testGetSelectedNodeWhenSelected() {
        Node node = mock(Node.class);
        when(graph.getNode(ITEM_UUID)).thenReturn(node);

        assertEquals(node, ClientUtils.getSelectedNode(diagram, viewerSession));
        assertEquals(node, ClientUtils.getSelectedNode(diagram, editorSession));
    }

    @Test
    public void testGetSelectedNodeWhenNotSelected() {
        selectedItems.clear();
        assertNull(ClientUtils.getSelectedNode(diagram, viewerSession));
        assertNull(ClientUtils.getSelectedNode(diagram, editorSession));
    }
}
