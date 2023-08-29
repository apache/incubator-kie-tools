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


package org.kie.workbench.common.stunner.core.graph.processing.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AbstractLayoutServiceTest {

    @Mock
    private AbstractLayoutService layoutService;

    @Mock
    private Graph graph;

    @Mock
    private Node n1;

    @Mock
    private Node n2;

    @Mock
    private HasBounds hasBounds1;

    @Mock
    private HasBounds hasBounds2;

    @Test
    public void getLayoutInformationThreshold() {
        when(n1.getUUID()).thenReturn(UUID.uuid());

        final List<Node> nodes = new ArrayList<>(Collections.singletonList(n1));

        doCallRealMethod().when(layoutService).getLayoutInformationThreshold(any());

        double threshold = layoutService.getLayoutInformationThreshold(nodes);
        assertEquals(0.25, threshold, 0.01);

        nodes.add(n2);

        threshold = layoutService.getLayoutInformationThreshold(nodes);
        assertEquals(0.50, threshold, 0.01);
    }

    @Test
    public void hasLayoutInformation() {
        when(n1.getUUID()).thenReturn(UUID.uuid());
        when(n2.getUUID()).thenReturn(UUID.uuid());
        final GraphNodeStoreImpl store = new GraphNodeStoreImpl();
        store.add(n2);
        store.add(n1);

        doCallRealMethod().when(layoutService).hasLayoutInformation(graph);
        doCallRealMethod().when(layoutService).getLayoutInformationThreshold(any());

        when(graph.nodes()).thenReturn(store);
        assertFalse(layoutService.hasLayoutInformation(graph));

        final Bounds bounds = Bounds.create(10, 10, 10, 10);
        final Bounds noBounds = Bounds.create(0, 0, 0, 0);

        when(n1.getContent()).thenReturn(hasBounds1);
        when(hasBounds1.getBounds()).thenReturn(noBounds);

        when(n2.getContent()).thenReturn(hasBounds2);
        when(hasBounds2.getBounds()).thenReturn(bounds);

        assertTrue(layoutService.hasLayoutInformation(graph));
    }
}
