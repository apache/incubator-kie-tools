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


package org.kie.workbench.common.stunner.bpmn.client.canvas.controls;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.canvas.controls.BPMNCreateNodeAction.isGatewaySourceOrTarget;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNCreateNodeActionTest {

    private Node gatewayNode;
    private Node eventNode;
    private Node taskNode;

    @Mock
    private BPMNCreateNodeAction connection;

    @Mock
    private MagnetConnection magnetConnection;

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
        doCallRealMethod().when(connection).buildConnectionBetween(any(), any());
        when(connection.getMagnetConnectionFixed(any(), any())).thenReturn(magnetConnection);
        when(connection.getMagnetConnectionCenter(any(), any())).thenReturn(magnetConnection);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsGatewayFixedConnection() {
        assertTrue(isGatewaySourceOrTarget(gatewayNode, taskNode));
        assertTrue(isGatewaySourceOrTarget(gatewayNode, eventNode));
        assertTrue(isGatewaySourceOrTarget(taskNode, gatewayNode));
        assertTrue(isGatewaySourceOrTarget(eventNode, gatewayNode));
        assertTrue(isGatewaySourceOrTarget(gatewayNode, gatewayNode));
        assertFalse(isGatewaySourceOrTarget(taskNode, taskNode));
        assertFalse(isGatewaySourceOrTarget(eventNode, taskNode));
        assertFalse(isGatewaySourceOrTarget(taskNode, eventNode));
        assertFalse(isGatewaySourceOrTarget(eventNode, eventNode));
    }

    @Test
    public void testbuildConnectionBetweenGatewayAndTask() {
        connection.buildConnectionBetween(gatewayNode, taskNode);
        verify(connection, times(1)).getMagnetConnectionFixed(gatewayNode, taskNode);
    }

    @Test
    public void testbuildConnectionBetweenGatewayAndEvent() {
        connection.buildConnectionBetween(gatewayNode, eventNode);
        verify(connection, times(1)).getMagnetConnectionFixed(gatewayNode, eventNode);
    }

    @Test
    public void testbuildConnectionBetweenTaskAndGateway() {
        connection.buildConnectionBetween(taskNode, gatewayNode);
        verify(connection, times(1)).getMagnetConnectionFixed(taskNode, gatewayNode);
    }

    @Test
    public void testbuildConnectionBetweenEventAndGateway() {
        connection.buildConnectionBetween(eventNode, gatewayNode);
        verify(connection, times(1)).getMagnetConnectionFixed(eventNode, gatewayNode);
    }

    @Test
    public void testbuildConnectionBetweenGatewayAndGateway() {
        connection.buildConnectionBetween(gatewayNode, gatewayNode);
        verify(connection, times(1)).getMagnetConnectionFixed(gatewayNode, gatewayNode);
    }

    @Test
    public void testbuildConnectionBetweenTaskAndTask() {
        connection.buildConnectionBetween(taskNode, taskNode);
        verify(connection, times(1)).getMagnetConnectionCenter(taskNode, taskNode);
    }

    @Test
    public void testbuildConnectionBetweenTaskAndEvent() {
        connection.buildConnectionBetween(taskNode, eventNode);
        verify(connection, times(1)).getMagnetConnectionCenter(taskNode, eventNode);
    }

    @Test
    public void testbuildConnectionBetweenEventAndEvent() {
        connection.buildConnectionBetween(eventNode, eventNode);
        verify(connection, times(1)).getMagnetConnectionCenter(eventNode, eventNode);
    }
}
