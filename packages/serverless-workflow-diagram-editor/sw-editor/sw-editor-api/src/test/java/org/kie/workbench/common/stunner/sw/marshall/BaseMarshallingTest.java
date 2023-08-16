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


package org.kie.workbench.common.stunner.sw.marshall;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.marshallNode;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.unmarshallNode;

public abstract class BaseMarshallingTest {

    protected TestingGraphMockHandler graphHandler;
    protected Context context;
    protected BuilderContext builderContext;
    protected Workflow workflow;

    @Before
    public void setUp() {
        graphHandler = new TestingGraphMockHandler();
        context = new Context(graphHandler.graphIndex);
        builderContext = new BuilderContext(context,
                                            graphHandler.getDefinitionManager(),
                                            graphHandler.getFactoryManager());
        workflow = createWorkflow();
        Marshaller.LOAD_DETAILS = true;
    }

    protected abstract Workflow createWorkflow();

    protected void unmarshallWorkflow() {
        unmarshallNode(builderContext, workflow);
        builderContext.execute();
    }

    protected Workflow marshallWorkflow() {
        return marshallNode(context, context.getWorkflowRootNode());
    }

    public boolean hasIncomingEdges(String name) {
        Node node = getNodeByName(name);
        return !node.getInEdges().isEmpty();
    }

    public boolean hasOutgoingEdges(String name) {
        Node node = getNodeByName(name);
        return !node.getOutEdges().isEmpty();
    }

    public boolean hasIncomingEdgeFrom(String name, String from) {
        String fromUUID = getUUIDForObjectName(from);
        Node node = getNodeByName(name);
        return node.getInEdges().stream()
                .filter(e -> fromUUID.equals(((Edge) e).getSourceNode().getUUID()))
                .findAny()
                .isPresent();
    }

    public boolean isConnectedToEnd(final String name) {
        final Node state1 = getNodeByName(name);
        final List<Edge> outEdges = (List<Edge>) state1.getOutEdges().stream()
                .filter(e -> ((Edge) e).getContent() instanceof ViewConnector)
                .collect(Collectors.toList());

        if (outEdges.size() == 1) {
            Edge edge = outEdges.get(0);
            Node targetNode = edge.getTargetNode();
            View targetContent = (View) targetNode.getContent();
            if (targetContent.getDefinition() instanceof End) {
                return true;
            }
        }
        return false;
    }

    public void assertDefinitionReferencedInNode(Object def, String nodeName) {
        assertTrue(isDefinitionReferencedInNode(def, nodeName));
    }

    public boolean isDefinitionReferencedInNode(Object def, String nodeName) {
        Node node = getNodeByName(nodeName);
        Object bean = ((View<?>) node.getContent()).getDefinition();
        return def == bean;
    }

    public int countChildren(String parentName) {
        Node parent = getNodeByName(parentName);
        return GraphUtils.getChildNodes(parent).size();
    }

    public void assertParentOf(String parentName, String nodeName) {
        assertTrue(isParentOf(parentName, nodeName));
    }

    public boolean isParentOf(String parentName, String nodeName) {
        Node parent = getNodeByName(parentName);
        Node node = getNodeByName(nodeName);
        return GraphUtils.getChildNodes(parent).contains(node);
    }

    public Node getNodeByName(String name) {
        return getGraph().getNode(getUUIDForObjectName(name));
    }

    public String getUUIDForObjectName(String name) {
        return context.obtainUUID(name);
    }

    public Graph<?, ?> getGraph() {
        return graphHandler.graph;
    }
}