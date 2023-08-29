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


package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.definition.clone.ClonePolicy;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public abstract class AbstractCloneCommandTest extends AbstractGraphCommandTest {

    @Mock
    protected Node<View, Edge> clone;

    @Mock
    protected Edge cloneEdge;

    @Mock
    protected Element cloneElement;

    @Mock
    protected View candidateContent;

    protected ViewConnector connectorContent;

    @Mock
    protected Object connectorDefinition;

    @Mock
    protected View cloneContent;

    @Mock
    protected Definition definition;

    @Mock
    protected CloneManager cloneManager;

    @Mock
    protected Bounds bounds;

    @Mock
    protected Bound bound;

    @Mock
    protected ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessorManagedInstance;

    protected TestingGraphInstanceBuilder.TestGraph3 graphInstance;

    protected MagnetConnection sourceConnection;

    protected MagnetConnection targetConnection;

    protected static final String CLONE_UUID = UUID.uuid();

    protected static final String CLONE_EDGE_UUID = UUID.uuid();

    protected Bounds candidateBounds = Bounds.create();

    public void setUp() {
        super.init();

        MockitoAnnotations.initMocks(this);

        //creating the mock graph for test
        graphInstance = TestingGraphInstanceBuilder.newGraph3(testingGraphMockHandler);

        //mocking the clone nodes on the graphIndex
        ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);
        when(testingGraphMockHandler.graphIndex.addNode(nodeArgumentCaptor.capture())).thenAnswer(
                t -> {
                    //Node node = (Node)t.getArguments()[0];
                    when(graphIndex.getNode(eq(nodeArgumentCaptor.getValue().getUUID()))).thenReturn(nodeArgumentCaptor.getValue());
                    return graphIndex;
                });
        doAnswer(invocationOnMock -> {
            Object o = invocationOnMock.getArguments()[0];
            return DefinitionId.build(BindableAdapterUtils.getDefinitionId(o.getClass()));
        }).when(definitionAdapter).getId(anyObject());

        //edge mock
        connectorContent = new ViewConnectorImpl(connectorDefinition, Bounds.create(1d, 1d, 1d, 1d));
        sourceConnection = MagnetConnection.Builder.atCenter(graphInstance.startNode);
        connectorContent.setSourceConnection(sourceConnection);
        targetConnection = MagnetConnection.Builder.atCenter(graphInstance.intermNode);
        connectorContent.setTargetConnection(targetConnection);
        graphInstance.edge1.setContent(connectorContent);
        graphInstance.edge2.setContent(connectorContent);

        when(definitionManager.cloneManager()).thenReturn(cloneManager);
        when(cloneManager.clone(definition, ClonePolicy.ALL)).thenReturn(definition);
        when(cloneManager.clone(connectorDefinition, ClonePolicy.ALL)).thenReturn(connectorDefinition);
        when(graphCommandExecutionContext.getGraphIndex()).thenReturn(graphIndex);
        when(candidateContent.getDefinition()).thenReturn(definition);
        when(candidateContent.getBounds()).thenReturn(candidateBounds);
        when(factoryManager.newElement(anyString(), anyString())).thenReturn(cloneElement);
        when(cloneElement.asNode()).thenReturn(clone);
        when(cloneElement.asEdge()).thenReturn(cloneEdge);
        when(cloneEdge.getContent()).thenReturn(connectorContent);
        when(cloneEdge.getUUID()).thenReturn(CLONE_EDGE_UUID);
        when(clone.getContent()).thenReturn(cloneContent);
        when(clone.getUUID()).thenReturn(CLONE_UUID);
        when(cloneElement.getUUID()).thenReturn(CLONE_UUID);
        when(cloneContent.getBounds()).thenReturn(bounds);
        when(bounds.getUpperLeft()).thenReturn(bound);
        when(bounds.getLowerRight()).thenReturn(bound);
        when(childrenTraverseProcessorManagedInstance.get()).thenReturn(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()));
    }
}
