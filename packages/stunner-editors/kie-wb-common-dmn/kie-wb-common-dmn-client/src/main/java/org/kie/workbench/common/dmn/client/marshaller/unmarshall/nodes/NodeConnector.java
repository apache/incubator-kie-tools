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

package org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Point2D;
import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.marshaller.common.IdUtils;
import org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIBounds;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIPoint;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAuthorityRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBusinessKnowledgeModel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeSource;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.kie.workbench.common.dmn.client.marshaller.common.JsInteropUtils.forEach;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.upperLeftBound;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.xOfBound;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.yOfBound;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

@Dependent
public class NodeConnector {

    private final FactoryManager factoryManager;

    static final double CENTRE_TOLERANCE = 1.0;

    private static final String INFO_REQ_ID = getDefinitionId(InformationRequirement.class);

    private static final String KNOWLEDGE_REQ_ID = getDefinitionId(KnowledgeRequirement.class);

    private static final String AUTH_REQ_ID = getDefinitionId(AuthorityRequirement.class);

    private static final String ASSOCIATION_ID = getDefinitionId(Association.class);

    @Inject
    public NodeConnector(final FactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    void connect(final JSIDMNDiagram dmnDiagram,
                 final List<JSIDMNEdge> edges,
                 final List<JSITAssociation> associations,
                 final List<NodeEntry> nodeEntries,
                 final boolean isDMNDIPresent) {

        final Map<String, List<NodeEntry>> entriesById = makeNodeIndex(nodeEntries);
        final String diagramId = dmnDiagram.getId();

        for (final NodeEntry nodeEntry : nodeEntries) {

            final JSITDMNElement element = nodeEntry.getDmnElement();
            final Node node = nodeEntry.getNode();
            // For imported nodes, we don't have its connections
            if (nodeEntry.isIncluded()) {
                continue;
            }

            // DMN spec table 2: Requirements
            if (JSITDecision.instanceOf(element)) {
                final JSITDecision decision = Js.uncheckedCast(element);
                final List<JSITInformationRequirement> jsiInformationRequirements = decision.getInformationRequirement();
                for (int i = 0; i < jsiInformationRequirements.size(); i++) {
                    final JSITInformationRequirement ir = Js.uncheckedCast(jsiInformationRequirements.get(i));
                    connectEdgeToNodes(INFO_REQ_ID,
                                       ir,
                                       ir.getRequiredInput(),
                                       entriesById,
                                       diagramId,
                                       edges,
                                       isDMNDIPresent,
                                       node);
                    connectEdgeToNodes(INFO_REQ_ID,
                                       ir,
                                       ir.getRequiredDecision(),
                                       entriesById,
                                       diagramId,
                                       edges,
                                       isDMNDIPresent,
                                       node);
                }
                final List<JSITKnowledgeRequirement> jsiKnowledgeRequirements = decision.getKnowledgeRequirement();
                for (int i = 0; i < jsiKnowledgeRequirements.size(); i++) {
                    final JSITKnowledgeRequirement kr = Js.uncheckedCast(jsiKnowledgeRequirements.get(i));
                    connectEdgeToNodes(KNOWLEDGE_REQ_ID,
                                       kr,
                                       kr.getRequiredKnowledge(),
                                       entriesById,
                                       diagramId,
                                       edges,
                                       isDMNDIPresent,
                                       node);
                }
                final List<JSITAuthorityRequirement> jsiAuthorityRequirements = decision.getAuthorityRequirement();
                for (int i = 0; i < jsiAuthorityRequirements.size(); i++) {
                    final JSITAuthorityRequirement ar = Js.uncheckedCast(jsiAuthorityRequirements.get(i));
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredAuthority(),
                                       entriesById,
                                       diagramId,
                                       edges,
                                       isDMNDIPresent,
                                       node);
                }
                continue;
            }

            if (JSITBusinessKnowledgeModel.instanceOf(element)) {
                final JSITBusinessKnowledgeModel bkm = Js.uncheckedCast(element);
                final List<JSITKnowledgeRequirement> jsiKnowledgeRequirements = bkm.getKnowledgeRequirement();
                for (int i = 0; i < jsiKnowledgeRequirements.size(); i++) {
                    final JSITKnowledgeRequirement kr = Js.uncheckedCast(jsiKnowledgeRequirements.get(i));
                    connectEdgeToNodes(KNOWLEDGE_REQ_ID,
                                       kr,
                                       kr.getRequiredKnowledge(),
                                       entriesById,
                                       diagramId,
                                       edges,
                                       isDMNDIPresent,
                                       node);
                }
                final List<JSITAuthorityRequirement> jsiAuthorityRequirements = bkm.getAuthorityRequirement();
                for (int i = 0; i < jsiAuthorityRequirements.size(); i++) {
                    final JSITAuthorityRequirement ar = Js.uncheckedCast(jsiAuthorityRequirements.get(i));
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredAuthority(),
                                       entriesById,
                                       diagramId,
                                       edges,
                                       isDMNDIPresent,
                                       node);
                }
                continue;
            }

            if (JSITKnowledgeSource.instanceOf(element)) {
                final JSITKnowledgeSource ks = Js.uncheckedCast(element);
                final List<JSITAuthorityRequirement> jsiAuthorityRequirements = ks.getAuthorityRequirement();
                for (int i = 0; i < jsiAuthorityRequirements.size(); i++) {
                    final JSITAuthorityRequirement ar = Js.uncheckedCast(jsiAuthorityRequirements.get(i));
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredInput(),
                                       entriesById,
                                       diagramId,
                                       edges,
                                       isDMNDIPresent,
                                       node);
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredDecision(),
                                       entriesById,
                                       diagramId,
                                       edges,
                                       isDMNDIPresent,
                                       node);
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredAuthority(),
                                       entriesById,
                                       diagramId,
                                       edges,
                                       isDMNDIPresent,
                                       node);
                }
                continue;
            }

            if (JSITDecisionService.instanceOf(element)) {
                final JSITDecisionService ds = Js.uncheckedCast(element);

                final List<JSITDMNElementReference> encapsulatedDecisions = ds.getEncapsulatedDecision();
                forEach(encapsulatedDecisions, er -> {
                    final String reqInputID = getId(er);
                    getNode(nodeEntry, reqInputID, entriesById)
                            .ifPresent(requiredNode -> {
                                connectDSChildEdge(node, requiredNode);
                            });
                });

                final List<JSITDMNElementReference> outputDecisions = ds.getOutputDecision();
                forEach(outputDecisions, er -> {
                    final String reqInputID = getId(er);
                    getNode(nodeEntry, reqInputID, entriesById)
                            .ifPresent(requiredNode -> {
                                connectDSChildEdge(node, requiredNode);
                            });
                });
            }
        }

        forEach(associations, association -> {

            final String sourceId = getId(association.getSourceRef());
            final String targetId = getId(association.getTargetRef());
            final List<NodeEntry> source = entriesById.get(sourceId);
            final List<NodeEntry> target = entriesById.get(targetId);
            final boolean sourcePresent = source != null && source.size() > 0;
            final boolean targetPresent = target != null && target.size() > 0;

            if (sourcePresent && targetPresent) {
                final NodeEntry sourceEntry = source.get(0);
                final NodeEntry targetEntry = target.get(0);
                final Node sourceNode = sourceEntry.getNode();
                final Node targetNode = targetEntry.getNode();

                @SuppressWarnings("unchecked")
                final Edge<View<Association>, ?> myEdge = (Edge<View<Association>, ?>) factoryManager.newElement(diagramId + "#" + association.getId(),
                                                                                                                 ASSOCIATION_ID).asEdge();

                final ViewConnector connectionContent = (ViewConnector) myEdge.getContent();
                final Id id = new Id(association.getId());
                final Description description = new Description(association.getDescription());
                final Association definition = new Association(id, description);

                connectEdge(myEdge,
                            sourceNode,
                            targetNode);

                connectionContent.setDefinition(definition);
                connectionContent.setTargetConnection(MagnetConnection.Builder.atCenter(targetNode));
                connectionContent.setSourceConnection(MagnetConnection.Builder.atCenter(sourceNode));

                findExistingEdge(association, edges).ifPresent(e -> setConnectionControlPoints(connectionContent, e));
            }
        });
    }

    private Optional<Node> getNode(final NodeEntry decisionServiceEntry,
                                   final String internalDMNElementId,
                                   final Map<String, List<NodeEntry>> entriesById) {

        final JSIBounds decisionServiceBounds = decisionServiceEntry.getDmnShape().getBounds();

        for (final Map.Entry<String, List<NodeEntry>> entry : entriesById.entrySet()) {

            final String id = entry.getKey();
            final List<NodeEntry> entries = entry.getValue();

            if (id.contains(internalDMNElementId)) {
                for (final NodeEntry nodeEntry : entries) {
                    final JSIBounds nodeBounds = nodeEntry.getDmnShape().getBounds();

                    final boolean b = (nodeBounds.getX() + nodeBounds.getWidth()) < (decisionServiceBounds.getX() + decisionServiceBounds.getWidth());
                    final boolean b1 = nodeBounds.getX() > decisionServiceBounds.getX();
                    final boolean innerX = b1 && b;

                    final boolean b2 = (nodeBounds.getY() + nodeBounds.getHeight()) < (decisionServiceBounds.getY() + decisionServiceBounds.getHeight());
                    final boolean b3 = nodeBounds.getY() > decisionServiceBounds.getY();
                    final boolean innerY = b2 && b3;

                    if (innerX && innerY) {
                        return Optional.of(nodeEntry.getNode());
                    }
                }
            }
        }

        return Optional.empty();
    }

    private Map<String, List<NodeEntry>> makeNodeIndex(final List<NodeEntry> nodeEntries) {

        final Map<String, List<NodeEntry>> map = new HashMap<>();

        nodeEntries.forEach(nodeEntry -> {
            final String dmnElementId = nodeEntry.getDmnElement().getId();
            map.putIfAbsent(dmnElementId, new ArrayList<>());
            map.get(dmnElementId).add(nodeEntry);
        });

        return map;
    }

    /**
     * Stunner's factoryManager is only used to create Nodes that are considered part of a "Definition Set" (a collection of nodes visible to the User e.g. BPMN2 StartNode, EndNode and DMN's DecisionNode etc).
     * Relationships are not created with the factory.
     * This method specializes to connect with an Edge containing a Child relationship the target Node.
     */
    private void connectDSChildEdge(final Node dsNode,
                                    final Node requiredNode) {
        final String uuid = dsNode.getUUID() + "er" + requiredNode.getUUID();
        final Edge<Child, Node> myEdge = new EdgeImpl<>(uuid);
        myEdge.setContent(new Child());
        connectEdge(myEdge,
                    dsNode,
                    requiredNode);
    }

    private String getId(final JSITDMNElementReference elementReference) {
        return Optional
                .ofNullable(elementReference)
                .map(ref -> {
                    final String href = elementReference.getHref();
                    return href.contains("#") ? href.substring(href.indexOf('#') + 1) : href;
                })
                .orElse("");
    }

    void connectEdgeToNodes(final String connectorTypeId,
                            final JSITDMNElement jsiDMNElement,
                            final JSITDMNElementReference jsiDMNElementReference,
                            final Map<String, List<NodeEntry>> entriesById,
                            final String diagramId,
                            final List<JSIDMNEdge> edges,
                            final boolean isDMNDIPresent,
                            final Node currentNode) {

        final String reqInputID = getId(jsiDMNElementReference);
        final List<NodeEntry> nodeEntries = entriesById.get(reqInputID);

        if (nodeEntries == null || nodeEntries.isEmpty()) {
            return;
        }

        final Optional<JSIDMNEdge> existingEdge = findExistingEdge(jsiDMNElement, edges);

        if (!isDMNDIPresent) {
            // Generate new a edge and connect it
            final NodeEntry nodeEntry = nodeEntries.get(0);
            final Node requiredNode = nodeEntry.getNode();
            final View<?> view = (View<?>) requiredNode.getContent();
            final double viewWidth = view.getBounds().getWidth();
            final double viewHeight = view.getBounds().getHeight();

            connectWbEdge(connectorTypeId,
                          diagramId,
                          currentNode,
                          requiredNode,
                          newEdge(viewWidth / 2, viewHeight / 2),
                          uuid());
        } else if (existingEdge.isPresent()) {
            // Connect existing edge
            final JSIDMNEdge edge = Js.uncheckedCast(existingEdge.get());
            final Optional<Node> requiredNode = getSourceNode(edge, nodeEntries);
            final String id = edge.getDmnElementRef().getLocalPart();
            final String currentNodeId = ((DRGElement) ((Definition) currentNode.getContent()).getDefinition()).getContentDefinitionId();

            // The edge can be connected with another instance of the same Node and not the currentNode
            if (isEdgeConnectedWithNode(edge, currentNode, entriesById.get(currentNodeId))) {
                connectWbEdge(connectorTypeId,
                              diagramId,
                              currentNode,
                              requiredNode.get(),
                              edge,
                              id);
            }
        }
    }

    boolean isEdgeConnectedWithNode(final JSIDMNEdge edge,
                                    final Node currentNode,
                                    final List<NodeEntry> nodeEntries) {
        final Optional<Node> targetNode = getTargetNode(edge, nodeEntries);
        final Optional<Node> sourceNode = getSourceNode(edge, nodeEntries);

        return (targetNode.isPresent() && Objects.equals(targetNode.get(), currentNode))
                || (sourceNode.isPresent() && Objects.equals(sourceNode.get(), currentNode));
    }

    private static Optional<JSIDMNEdge> findExistingEdge(final JSITDMNElement dmnElement,
                                                         final List<JSIDMNEdge> edges) {
        return edges.stream()
                .filter(e -> e.getDmnElementRef() != null && Objects.equals(e.getDmnElementRef().getLocalPart(), dmnElement.getId()))
                .findFirst();
    }

    void connectWbEdge(final String connectorTypeId,
                       final String diagramId,
                       final Node currentNode,
                       final Node requiredNode,
                       final JSIDMNEdge dmnEdge,
                       final String id) {

        final String prefixedId = IdUtils.getPrefixedId(diagramId, id);
        final Edge wbEdge = factoryManager.newElement(prefixedId, connectorTypeId).asEdge();
        final ViewConnector connectionContent = (ViewConnector) wbEdge.getContent();

        connectEdge(wbEdge, requiredNode, currentNode);
        setConnectionMagnets(wbEdge, connectionContent, dmnEdge);
    }

    JSIDMNEdge newEdge(double x, double y) {
        final JSIDMNEdge dmnEdge = JSIDMNEdge.newInstance();
        final JSIPoint point = JSIPoint.newInstance();
        point.setX(x);
        point.setY(y);
        dmnEdge.addAllWaypoint(point, point);
        return dmnEdge;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void connectEdge(final Edge edge,
                     final Node source,
                     final Node target) {
        edge.setSourceNode(source);
        edge.setTargetNode(target);
        source.getOutEdges().add(edge);
        target.getInEdges().add(edge);
    }

    void setConnectionMagnets(final Edge edge,
                              final ViewConnector connectionContent,
                              final JSIDMNEdge jsidmnEdge) {

        final JSIDMNEdge e = Js.uncheckedCast(jsidmnEdge);
        final JSIPoint source = Js.uncheckedCast(e.getWaypoint().get(0));
        final Node<View<?>, Edge> sourceNode = edge.getSourceNode();
        if (null != sourceNode) {
            setConnectionMagnet(sourceNode,
                                source,
                                connectionContent::setSourceConnection,
                                isSourceAutoConnectionEdge(jsidmnEdge));
        }
        final JSIPoint target = Js.uncheckedCast(e.getWaypoint().get(e.getWaypoint().size() - 1));
        final Node<View<?>, Edge> targetNode = edge.getTargetNode();
        if (null != targetNode) {
            setConnectionMagnet(targetNode,
                                target,
                                connectionContent::setTargetConnection,
                                isTargetAutoConnectionEdge(jsidmnEdge));
        }
        setConnectionControlPoints(connectionContent, jsidmnEdge);
    }

    void setConnectionControlPoints(final ViewConnector connectionContent,
                                    final JSIDMNEdge jsidmnEdge) {

        if (jsidmnEdge.getWaypoint().size() > 2) {
            connectionContent.setControlPoints(jsidmnEdge.getWaypoint()
                                                       .subList(1, jsidmnEdge.getWaypoint().size() - 1)
                                                       .stream()
                                                       .map(p -> ControlPoint.build(PointUtils.dmndiPointToPoint2D(p)))
                                                       .toArray(ControlPoint[]::new));
        }
    }

    protected boolean isSourceAutoConnectionEdge(JSIDMNEdge jsidmnEdge) {
        return isAutoConnection(jsidmnEdge, IdUtils.AUTO_SOURCE_CONNECTION);
    }

    protected boolean isTargetAutoConnectionEdge(JSIDMNEdge jsidmnEdge) {
        return isAutoConnection(jsidmnEdge, IdUtils.AUTO_TARGET_CONNECTION);
    }

    protected boolean isAutoConnection(JSIDMNEdge jsidmnEdge, String autoConnectionID) {
        String dmnEdgeID = jsidmnEdge.getId();
        if (dmnEdgeID != null) {
            return dmnEdgeID.contains(autoConnectionID);
        } else {
            return false;
        }
    }

    private void setConnectionMagnet(final Node<View<?>, Edge> node,
                                     final JSIPoint magnetPoint,
                                     final Consumer<Connection> connectionConsumer,
                                     final Boolean isAutoConnection) {
        final View<?> view = node.getContent();
        final double viewX = xOfBound(upperLeftBound(view));
        final double viewY = yOfBound(upperLeftBound(view));
        final double magnetRelativeX = magnetPoint.getX() - viewX;
        final double magnetRelativeY = magnetPoint.getY() - viewY;
        final double viewWidth = view.getBounds().getWidth();
        final double viewHeight = view.getBounds().getHeight();

        MagnetConnection connection;
        if (isCentre(magnetRelativeX,
                     magnetRelativeY,
                     viewWidth,
                     viewHeight)) {
            connection = MagnetConnection.Builder.atCenter(node);
        } else {
            connection = MagnetConnection.Builder.at(magnetRelativeX, magnetRelativeY);
        }
        connection.setAuto(isAutoConnection);
        connectionConsumer.accept(connection);
    }

    private boolean isCentre(final double magnetRelativeX,
                             final double magnetRelativeY,
                             final double viewWidth,
                             final double viewHeight) {
        return Math.abs((viewWidth / 2) - magnetRelativeX) < CENTRE_TOLERANCE &&
                Math.abs((viewHeight / 2) - magnetRelativeY) < CENTRE_TOLERANCE;
    }

    Optional<Node> getSourceNode(final JSIDMNEdge jsidmnEdge,
                                 final List<NodeEntry> entries) {

        if (entries.size() == 1) {
            return Optional.of(entries.get(0).getNode());
        }

        final JSIPoint jsiSource = Js.uncheckedCast(jsidmnEdge.getWaypoint().get(0));
        final Point2D source = createPoint(jsiSource);

        return getNodeFromPoint(source, entries);
    }

    Point2D createPoint(final JSIPoint point) {
        return new Point2D(point.getX(), point.getY());
    }

    Optional<Node> getTargetNode(final JSIDMNEdge jsidmnEdge,
                                 final List<NodeEntry> entries) {

        if (entries.size() == 1) {
            return Optional.of(entries.get(0).getNode());
        }

        final JSIPoint jsiTarget = Js.uncheckedCast(jsidmnEdge.getWaypoint().get(jsidmnEdge.getWaypoint().size() - 1));
        final Point2D source = createPoint(jsiTarget);

        return getNodeFromPoint(source, entries);
    }

    Optional<Node> getNodeFromPoint(final Point2D point,
                                    final List<NodeEntry> entries) {
        if (entries.size() == 1) {
            return Optional.of(entries.get(0).getNode());
        }

        final Map<Point2D, NodeEntry> entriesByPoint2D = new HashMap<>();

        for (final NodeEntry entry : entries) {
            final JSIBounds bounds = entry.getDmnShape().getBounds();
            final double centerX = bounds.getX() + (bounds.getWidth() / 2);
            final double centerY = bounds.getY() + (bounds.getHeight() / 2);
            entriesByPoint2D.put(new Point2D(centerX, centerY), entry);
        }

        final Point2D nearest = Collections.min(entriesByPoint2D.keySet(), (point1, point2) -> {
            final Double distance1 = point.distance(point1);
            final Double distance2 = point.distance(point2);
            return distance1.compareTo(distance2);
        });

        if (!isPointInsideNode(entriesByPoint2D.get(nearest), point)) {
            return Optional.empty();
        }
        return Optional.of(entriesByPoint2D.get(nearest).getNode());
    }

    boolean isPointInsideNode(final NodeEntry node,
                              final Point2D point) {

        final JSIBounds bounds = node.getDmnShape().getBounds();
        final double width = bounds.getX() + bounds.getWidth();
        final double height = bounds.getY() + bounds.getHeight();

        return point.getX() <= width + CENTRE_TOLERANCE
                && point.getX() >= bounds.getX() - CENTRE_TOLERANCE
                && point.getY() <= height + CENTRE_TOLERANCE
                && point.getY() >= bounds.getY() - CENTRE_TOLERANCE;
    }

    String uuid() {
        return UUID.uuid();
    }
}
