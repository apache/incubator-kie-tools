/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.backend;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.backend.BaseDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionBackendService;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.properties.CaseManagementPropertyWriterFactory;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.UUID;

@Dependent
@CaseManagementEditor
public class CaseManagementDirectDiagramMarshaller extends BaseDirectDiagramMarshaller {

    @Inject
    public CaseManagementDirectDiagramMarshaller(final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller,
                                                 final DefinitionManager definitionManager,
                                                 final RuleManager ruleManager,
                                                 final WorkItemDefinitionBackendService workItemDefinitionService,
                                                 final FactoryManager factoryManager,
                                                 final GraphCommandFactory commandFactory,
                                                 final GraphCommandManager commandManager) {
        super(diagramMetadataMarshaller,
              definitionManager,
              ruleManager,
              workItemDefinitionService,
              factoryManager,
              commandFactory,
              commandManager);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String marshall(Diagram<Graph, Metadata> diagram) throws IOException {
        preMarshallProcess(diagram);

        return super.marshall(diagram);
    }

    @SuppressWarnings("unchecked")
    private void preMarshallProcess(final Diagram<Graph, Metadata> diagram) {
        Iterable<Node<View<?>, Edge>> nodes = diagram.getGraph().nodes();

        // adjust the position of the elements
        StreamSupport.stream(nodes.spliterator(), false)
                .filter(node -> !CaseManagementDiagram.class.isInstance(node.getContent().getDefinition()))
                .forEach(node -> {
                    View content = (View) ((Node) node).getContent();
                    Bounds bounds = content.getBounds();
                    Bound ul = bounds.getUpperLeft();
                    Bound lr = bounds.getLowerRight();
                    content.setBounds(Bounds.create(ul.getX() + 10d, ul.getY() + 10d, lr.getX() + 10d, lr.getY() + 10d));
                });

        StreamSupport.stream(nodes.spliterator(), false)
                .filter(node -> CaseManagementDiagram.class.isInstance(node.getContent().getDefinition()))
                .findAny().ifPresent(root -> {
            final Node rootNode = (Node) root;

            // create sequence flow between elements in stages
            rootNode.getOutEdges().stream().map(e -> ((Edge) e).getTargetNode())
                    .forEach(n -> buildChildEdge((Node) n));

            // create start event and end event
            final Node startNoneEvent = typedFactoryManager.newNode(UUID.uuid(), StartNoneEvent.class);
            ((View) startNoneEvent.getContent()).setBounds(Bounds.create(20, 20, 75, 75));
            diagram.getGraph().addNode(startNoneEvent);
            createChild(UUID.uuid(), rootNode, startNoneEvent, 0);

            final Node endNoneEvent = typedFactoryManager.newNode(UUID.uuid(), EndNoneEvent.class);
            ((View) endNoneEvent.getContent()).setBounds(Bounds.create(20, 20, 75, 75));
            diagram.getGraph().addNode(endNoneEvent);
            createChild(UUID.uuid(), rootNode, endNoneEvent, -1);

            // create sequence flow between stages
            buildChildEdge(rootNode);
        });
    }

    @SuppressWarnings("unchecked")
    private void buildChildEdge(Node parentNode) {
        final List<Node> nodes = (List<Node>) parentNode.getOutEdges().stream()
                .map(e -> ((Edge) e).getTargetNode()).collect(Collectors.toList());

        for (int i = 0, n = nodes.size() - 1; i < n; i++) {
            createEdge(UUID.uuid(), nodes.get(i), nodes.get(i + 1));
        }
    }

    @SuppressWarnings("unchecked")
    private void createEdge(String uuid, Node sourceNode, Node targetNode) {
        final Edge<View<SequenceFlow>, Node> edge = typedFactoryManager.newEdge(uuid, SequenceFlow.class);
        edge.setSourceNode(sourceNode);
        edge.setTargetNode(targetNode);
        sourceNode.getOutEdges().add(edge);
        targetNode.getInEdges().add(edge);

        ViewConnector<SequenceFlow> content = (ViewConnector<SequenceFlow>) edge.getContent();
        content.setSourceConnection(MagnetConnection.Builder.forElement(sourceNode));
        content.setTargetConnection(MagnetConnection.Builder.forElement(targetNode));
    }

    @SuppressWarnings("unchecked")
    private void createChild(String uuid, Node parent, Node child, int parentIndex) {
        final Edge<Child, Node> edge = new EdgeImpl<>(uuid);
        edge.setContent(new Child());
        edge.setSourceNode(parent);
        edge.setTargetNode(child);

        if (parentIndex >= 0) {
            parent.getOutEdges().add(parentIndex, edge);
        } else {
            parent.getOutEdges().add(edge);
        }

        child.getInEdges().add(edge);
    }

    @Override
    public Graph<DefinitionSet, Node> unmarshall(Metadata metadata, InputStream inputStream) throws IOException {
        Graph<DefinitionSet, Node> graph = super.unmarshall(metadata, inputStream);

        postUnmarshallProcess(graph);

        return graph;
    }

    @SuppressWarnings("unchecked")
    private void postUnmarshallProcess(final Graph<DefinitionSet, Node> graph) {
        List<Node<View<?>, Edge>> nodes = StreamSupport.stream(graph.nodes().spliterator(), false)
                .map(n -> (Node<View<?>, Edge>) n).collect(Collectors.toList());

        nodes.stream()
                .filter(node -> CaseManagementDiagram.class.isInstance(node.getContent().getDefinition()))
                .findAny().ifPresent(root -> {

            // remove sequence flow between stages
            deleteChildEdge(root);

            // remove start event and end event
            final Node<View<?>, Edge> startNode = root.getOutEdges().get(0).getTargetNode();
            if (StartNoneEvent.class.isInstance(startNode.getContent().getDefinition())) {
                deleteChild(root, startNode);
                graph.removeNode(startNode.getUUID());
            }

            final Node<View<?>, Edge> endNode = root.getOutEdges().get(root.getOutEdges().size() - 1).getTargetNode();
            if (EndNoneEvent.class.isInstance(endNode.getContent().getDefinition())) {
                deleteChild(root, endNode);
                graph.removeNode(endNode.getUUID());
            }

            // remove sequence flow between elements in stages
            Stream<Node<View<?>, Edge>> stageStream = root.getOutEdges().stream().map(Edge::getTargetNode);
            stageStream.filter(n -> AdHocSubprocess.class.isInstance(n.getContent().getDefinition()))
                    .forEach(n -> deleteChildEdge((Node) n));
        });

        // reset position of elements
        StreamSupport.stream(nodes.spliterator(), false)
                .filter(node -> !CaseManagementDiagram.class.isInstance(node.getContent().getDefinition()))
                .forEach(node -> {
                    View content = (View) node.getContent();
                    Bounds bounds = content.getBounds();
                    Bound ul = bounds.getUpperLeft();
                    Bound lr = bounds.getLowerRight();
                    content.setBounds(Bounds.create(ul.getX() - 10d, ul.getY() - 10d, lr.getX() - 10d, lr.getY() - 10d));
                });
    }

    @SuppressWarnings("unchecked")
    private void deleteChildEdge(Node parentNode) {
        final List<Node> childNodes = new LinkedList<>();

        parentNode.getOutEdges().stream().map(e -> ((Edge) e).getTargetNode())
                .filter(n -> ((Node) n).getInEdges().size() == 1)
                .findAny().ifPresent(nd -> {
            Node node = (Node) nd;

            do {
                childNodes.add(node);
            } while ((node = deleteEdge(node)) != null);

            final List<Edge> childEdges = childNodes.stream()
                    .map(n -> (Edge) parentNode.getOutEdges().stream().filter(e -> n.equals(((Edge) e).getTargetNode())).findAny().get())
                    .collect(Collectors.toList());

            parentNode.getOutEdges().clear();
            childEdges.forEach(e -> parentNode.getOutEdges().add(e));
        });
    }

    @SuppressWarnings("unchecked")
    private void deleteChild(Node parent, Node child) {
        parent.getOutEdges().stream().filter(edge -> child.equals(((Edge) edge).getTargetNode()))
                .findAny().ifPresent(edge -> {
            parent.getOutEdges().remove(edge);
            child.getInEdges().remove(edge);
        });
    }

    @SuppressWarnings("unchecked")
    private Node deleteEdge(Node sourceNode) {
        final Edge targetEdge = (Edge) sourceNode.getOutEdges().stream().filter(edge -> (((Edge) edge).getContent() instanceof ViewConnector)
                && ((ViewConnector) ((Edge) edge).getContent()).getDefinition() instanceof SequenceFlow)
                .findAny().orElse(null);

        if (targetEdge != null) {
            final Node targetNode = targetEdge.getTargetNode();

            sourceNode.getOutEdges().remove(targetEdge);
            targetNode.getInEdges().remove(targetEdge);

            return targetNode;
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.CaseManagementConverterFactory createFromStunnerConverterFactory(
            final Graph graph,
            final PropertyWriterFactory propertyWriterFactory) {
        return new org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.CaseManagementConverterFactory(
                new DefinitionsBuildingContext(graph, CaseManagementDiagram.class), propertyWriterFactory);
    }

    @Override
    protected org.kie.workbench.common.stunner.cm.backend.converters.tostunner.CaseManagementConverterFactory createToStunnerConverterFactory(
            final DefinitionResolver definitionResolver,
            final TypedFactoryManager typedFactoryManager) {
        return new org.kie.workbench.common.stunner.cm.backend.converters.tostunner.CaseManagementConverterFactory(
                definitionResolver, typedFactoryManager);
    }

    @Override
    protected CaseManagementPropertyWriterFactory createPropertyWriterFactory() {
        return new CaseManagementPropertyWriterFactory();
    }

    @Override
    protected Class<CaseManagementDefinitionSet> getDefinitionSetClass() {
        return CaseManagementDefinitionSet.class;
    }
}
