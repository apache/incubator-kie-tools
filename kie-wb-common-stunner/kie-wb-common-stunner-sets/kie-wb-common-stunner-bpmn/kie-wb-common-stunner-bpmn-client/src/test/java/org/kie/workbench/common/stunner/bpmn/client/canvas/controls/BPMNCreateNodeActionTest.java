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

package org.kie.workbench.common.stunner.bpmn.client.canvas.controls;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.canvas.controls.BPMNCreateNodeAction.isAutoMagnetConnection;

@RunWith(MockitoJUnitRunner.class)
public class BPMNCreateNodeActionTest {

    private Node gatewayNode;
    private Node eventNode;
    private Node taskNode;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        gatewayNode = new NodeImpl<>("gNode");
        gatewayNode.setContent(new ViewImpl<>(new ParallelGateway(),
                                              Bounds.createEmpty()));
        taskNode = new NodeImpl<>("tNode");
        taskNode.setContent(new ViewImpl<>(new UserTask(),
                                           Bounds.createEmpty()));
        eventNode = new NodeImpl<>("eNode");
        eventNode.setContent(new ViewImpl<>(new StartNoneEvent(),
                                            Bounds.createEmpty()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsAutoConnection() {
        assertFalse(isAutoMagnetConnection(gatewayNode, taskNode));
        assertFalse(isAutoMagnetConnection(gatewayNode, eventNode));
        assertFalse(isAutoMagnetConnection(taskNode, gatewayNode));
        assertFalse(isAutoMagnetConnection(eventNode, gatewayNode));
        assertFalse(isAutoMagnetConnection(gatewayNode, gatewayNode));
        assertTrue(isAutoMagnetConnection(taskNode, taskNode));
        assertTrue(isAutoMagnetConnection(eventNode, taskNode));
        assertTrue(isAutoMagnetConnection(taskNode, eventNode));
        assertTrue(isAutoMagnetConnection(eventNode, eventNode));
    }
}
