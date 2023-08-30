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

package org.kie.workbench.common.dmn.client.widgets.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.dmn.api.definition.model.DMNElementReference;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.GraphProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.layout.VertexPosition;
import org.kie.workbench.common.stunner.core.graph.processing.layout.VertexPositionImpl;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.SugiyamaLayoutService.DEFAULT_INNER_VERTICAL_PADDING;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.SugiyamaLayoutService.DEFAULT_PARENT_NODE_HEIGHT;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning.DEFAULT_VERTEX_HEIGHT;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning.DEFAULT_VERTEX_WIDTH;

@DMNEditor
public class DMNGraphProcessor implements GraphProcessor {

    public static final int DEFAULT_TOP_VERTICAL_PADDING = 40;
    public static final int INPUT_NODE_VERTICAL_PADDING = DEFAULT_PARENT_NODE_HEIGHT / 2 + DEFAULT_INNER_VERTICAL_PADDING;
    private final HashMap<String, String> replacedNodes;

    public DMNGraphProcessor() {
        replacedNodes = new HashMap<>();
    }

    @Override
    public Iterable<? extends Node> getNodes(final Graph<?, ?> graph) {

        final List<Node> nodes = extractGraphNodes(graph);

        final Map<String, DecisionService> decisionServiceNodes = getDecisionServicesNodes(graph);

        decisionServiceNodes.forEach((nodeUuid, decisionService) -> replaceDecisionServiceInnerNodes(nodes,
                                                                                                     nodeUuid,
                                                                                                     decisionService));

        return nodes;
    }

    List<Node> extractGraphNodes(final Graph<?, ?> graph) {
        final List<Node> nodes = new ArrayList();
        graph.nodes().forEach(nodes::add);
        return nodes;
    }

    void replaceDecisionServiceInnerNodes(final List<Node> nodes,
                                          final String nodeUuid,
                                          final DecisionService decisionService) {
        final Set<String> innerIds = getInnerIds(decisionService);
        final List<Node> removedNodes = new ArrayList<>();
        nodes.stream().filter(this::hasContentDefinitionId)
                .forEach(node -> {
                    final String contentId = getContentDefinitionId(node);
                    if (innerIds.contains(contentId)) {
                        removedNodes.add(node);
                        getReplacedNodes().put(node.getUUID(), nodeUuid);
                    }
                });

        nodes.removeAll(removedNodes);
    }

    Set<String> getInnerIds(final DecisionService decisionService) {
        final HashSet<String> innerIds = new HashSet<>();
        innerIds.addAll(getDecisionIds(decisionService.getEncapsulatedDecision()));
        innerIds.addAll(getDecisionIds(decisionService.getOutputDecision()));
        return innerIds;
    }

    List<String> getDecisionIds(final List<DMNElementReference> decisions) {
        return decisions.stream()
                .map(e -> e.getHref().replace("#", ""))
                .collect(Collectors.toList());
    }

    Map<String, DecisionService> getDecisionServicesNodes(final Graph<?, ?> graph) {
        return StreamSupport.stream(graph.nodes().spliterator(), false)
                .filter(n -> containsDecisionService(n))
                .collect(Collectors.toMap(Element::getUUID,
                                          n -> extractDecisionService(n)));
    }

    private static DecisionService extractDecisionService(final Node n) {
        return (DecisionService) (((Definition) n.getContent()).getDefinition());
    }

    private static boolean containsDecisionService(final Node n) {
        return n.getContent() instanceof Definition
                && ((Definition) n.getContent()).getDefinition() instanceof DecisionService;
    }

    String getContentDefinitionId(final Node node) {
        return ((HasContentDefinitionId) ((Definition) node.getContent()).getDefinition()).getContentDefinitionId();
    }

    boolean hasContentDefinitionId(final Node node) {
        return node.getContent() instanceof Definition
                && ((Definition) node.getContent()).getDefinition() instanceof HasContentDefinitionId;
    }

    @Override
    public boolean isReplacedByAnotherNode(final String uuid) {
        return replacedNodes.containsKey(uuid);
    }

    @Override
    public String getReplaceNodeId(final String uuid) {
        return replacedNodes.get(uuid);
    }

    @Override
    public Map<String, String> getReplacedNodes() {
        return this.replacedNodes;
    }

    @Override
    public void connect(final Node parentNode,
                        final Node innerNode) {
        final Edge<Child, Node> edge = createEdge(parentNode, innerNode);
        removeParent(innerNode);
        innerNode.getInEdges().add(edge);
        parentNode.getOutEdges().add(edge);
    }

    private static void removeParent(final Node innerNode) {
        innerNode.getInEdges().removeIf(innerEdge -> (innerEdge instanceof Edge)
                && ((Edge) innerEdge).getContent() instanceof Child);
    }

    Edge<Child, Node> createEdge(final Node parentNode, final Node innerNode) {
        final Edge<Child, Node> edge = new EdgeImpl<>(UUID.uuid());
        edge.setContent(new Child());
        edge.setSourceNode(parentNode);
        edge.setTargetNode(innerNode);
        return edge;
    }

    @Override
    public VertexPosition getChildVertexPosition(final String parentId,
                                                 final String innerNodeId,
                                                 double horizontalPadding,
                                                 final Graph<?, ?> graph) {

        final Optional<Node> parentNode = getNodeFromGraph(parentId, graph);
        final Node parent = parentNode.orElseThrow(() -> new UnsupportedOperationException(
                "Unable to find parent '" + parentId + "' of the node '" + innerNodeId + "'"));

        final double verticalPadding = getVerticalPadding(parent, innerNodeId);

        final Point2D ulChild = new Point2D(horizontalPadding,
                                            verticalPadding);
        final Point2D brChild = new Point2D(ulChild.getX() + DEFAULT_VERTEX_WIDTH,
                                            ulChild.getY() + DEFAULT_VERTEX_HEIGHT);
        final VertexPosition position = new VertexPositionImpl(innerNodeId,
                                                               ulChild,
                                                               brChild);
        return position;
    }

    double getVerticalPadding(final Node parent,
                              final String innerNodeId) {

        final Optional<Edge> targetEdge = getTargetEdgeToId(parent, innerNodeId);
        if (!targetEdge.isPresent()) {
            return 0; // There is no vertical padding
        }

        final Optional<DecisionService> decisionService = getDecisionService(parent);
        final DecisionService ds = decisionService.get();
        final Edge edge = targetEdge.get();
        final Node targetNode = edge.getTargetNode();
        final String realId = "#" + getContentDefinitionId(targetNode);

        if (isOutput(ds, realId)) {
            return DEFAULT_TOP_VERTICAL_PADDING;
        }
        return INPUT_NODE_VERTICAL_PADDING;
    }

    boolean isOutput(final DecisionService ds, final String realId) {
        return ds.getOutputDecision().stream()
                .anyMatch(output -> Objects.equals(output.getHref(), realId));
    }

    Optional<Edge> getTargetEdgeToId(final Node parent, final String innerNodeId) {
        return parent.getOutEdges().stream()
                .filter(e -> e instanceof Edge && Objects.equals(((Edge) e).getTargetNode().getUUID(), innerNodeId))
                .findFirst();
    }

    private Optional<DecisionService> getDecisionService(final Node node) {
        if (node.getContent() instanceof Definition) {
            final Object innerDefinition = ((Definition) node.getContent()).getDefinition();
            if (innerDefinition instanceof DecisionService) {
                return Optional.of((DecisionService) innerDefinition);
            }
        }
        return Optional.empty();
    }
}
