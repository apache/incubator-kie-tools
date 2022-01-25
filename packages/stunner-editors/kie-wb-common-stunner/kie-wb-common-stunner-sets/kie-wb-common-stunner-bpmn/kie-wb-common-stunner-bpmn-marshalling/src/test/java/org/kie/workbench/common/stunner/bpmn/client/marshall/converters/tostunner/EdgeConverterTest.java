/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner;

import java.util.Map;

import org.eclipse.bpmn2.BaseElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.EdgePropertyReader;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EdgeConverterTest {

    private EdgeConverter tested;

    @Mock
    private BpmnNode node1;

    @Mock
    private BpmnNode node2;

    @Mock
    private BpmnNode node3;

    @Mock
    private Edge<? extends View<?>, Node> edge;

    @Mock
    private EdgePropertyReader reader;

    private static final String message = "message";
    private static final String key = "key";

    @Before
    public void setUp() {
        when(reader.getSourceId()).thenReturn("1");
        when(reader.getTargetId()).thenReturn("2");

        tested = new EdgeConverter() {
            @Override
            public Result<BpmnEdge> convertEdge(BaseElement element, Map nodes) {
                return null;
            }
        };
    }

    @Test
    public void resultWithExistingNodes() {
        Map<String, BpmnNode> nodes = new Maps.Builder<String, BpmnNode>()
                .put("1", node1)
                .put("2", node2)
                .put("3", node3)
                .build();

        when(reader.getSourceId()).thenReturn("1");
        when(reader.getTargetId()).thenReturn("2");

        Result<BpmnEdge> result = tested.result(nodes, edge, reader, message, key);
        BpmnEdge value = result.value();
        assertTrue(result.isSuccess());
        assertEquals(node1, value.getSource());
        assertEquals(node2, value.getTarget());
    }

    @Test
    public void resultWithMissingSourceNode() {
        Map<String, BpmnNode> nodes = new Maps.Builder<String, BpmnNode>()
                .put("2", node1)
                .build();

        Result<BpmnEdge> result = tested.result(nodes, edge, reader, message, key);
        BpmnEdge value = result.value();
        assertTrue(result.isIgnored());
        assertNull(value);
    }

    @Test
    public void resultWithMissingTargetNode() {
        Map<String, BpmnNode> nodes = new Maps.Builder<String, BpmnNode>()
                .put("1", node1)
                .build();

        Result<BpmnEdge> result = tested.result(nodes, edge, reader, message, key);
        BpmnEdge value = result.value();
        assertTrue(result.isIgnored());
        assertNull(value);
    }

    @Test
    public void valid() {
        Map<String, BpmnNode> nodes = new Maps.Builder<String, BpmnNode>()
                .put("1", node1)
                .put("2", node1)
                .build();

        assertTrue(tested.valid(nodes, "1", "2"));
        assertFalse(tested.valid(nodes, "1", "3"));
        assertFalse(tested.valid(nodes, "0", "2"));
        assertFalse(tested.valid(nodes, "0", "0"));
    }
}