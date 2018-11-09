/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.jboss.errai.marshalling.server.ServerMarshalling;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.model.api.dmndi.Bounds;
import org.kie.dmn.model.api.dmndi.Color;
import org.kie.dmn.model.api.dmndi.DMNEdge;
import org.kie.dmn.model.api.dmndi.DMNShape;
import org.kie.dmn.model.api.dmndi.DMNStyle;
import org.kie.dmn.model.api.dmndi.Point;
import org.kie.dmn.model.v1_2.dmndi.DMNDI;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.background.BgColour;
import org.kie.workbench.common.dmn.api.property.background.BorderColour;
import org.kie.workbench.common.dmn.api.property.dimensions.Height;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.Width;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.backend.definition.v1_1.AssociationConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.BusinessKnowledgeModelConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.DecisionConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.DefinitionsConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.InputDataConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.KnowledgeSourceConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.TextAnnotationConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ColorUtils;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.FontSetPropertyConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.PointUtils;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds.Bound;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;

@ApplicationScoped
public class DMNMarshaller implements DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> {

    private XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller;
    private FactoryManager factoryManager;
    private InputDataConverter inputDataConverter;
    private DecisionConverter decisionConverter;
    private BusinessKnowledgeModelConverter bkmConverter;
    private KnowledgeSourceConverter knowledgeSourceConverter;
    private TextAnnotationConverter textAnnotationConverter;
    private org.kie.dmn.api.marshalling.DMNMarshaller marshaller;

    protected DMNMarshaller() {
        this(null,
             null);
    }

    @Inject
    public DMNMarshaller(final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller,
                         final FactoryManager factoryManager) {
        this.diagramMetadataMarshaller = diagramMetadataMarshaller;
        this.factoryManager = factoryManager;
        this.inputDataConverter = new InputDataConverter(factoryManager);
        this.decisionConverter = new DecisionConverter(factoryManager);
        this.bkmConverter = new BusinessKnowledgeModelConverter(factoryManager);
        this.knowledgeSourceConverter = new KnowledgeSourceConverter(factoryManager);
        this.textAnnotationConverter = new TextAnnotationConverter(factoryManager);
        this.marshaller = DMNMarshallerFactory.newDefaultMarshaller();
    }

    @Deprecated
    public Graph unmarshallFromStunnerJSON(final Metadata metadata,
                                           final InputStream input) throws IOException {
        Graph result = (Graph) ServerMarshalling.fromJSON(input);
        return result;
    }

    @Deprecated
    public String marshallFromStunnerToJSON(final Diagram<Graph, Metadata> diagram) throws IOException {
        String result = ServerMarshalling.toJSON(diagram.getGraph());
        return result;
    }

    private static Optional<org.kie.dmn.model.api.dmndi.DMNDiagram> findDMNDiagram(org.kie.dmn.model.api.Definitions dmnXml) {
        if (!(dmnXml instanceof org.kie.dmn.model.v1_2.TDefinitions)) {
            return Optional.empty();
        }
        if (dmnXml.getDMNDI() == null) {
            return Optional.empty();
        }
        List<org.kie.dmn.model.api.dmndi.DMNDiagram> elems = dmnXml.getDMNDI().getDMNDiagram();
        if (elems.size() != 1) {
            return Optional.empty();
        } else {
            return Optional.of(elems.get(0));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Graph unmarshall(final Metadata metadata,
                            final InputStream input) throws IOException {
        org.kie.dmn.model.api.Definitions dmnXml = marshaller.unmarshal(new InputStreamReader(input));

        Map<String, Entry<org.kie.dmn.model.api.DRGElement, Node>> elems = dmnXml.getDrgElement().stream().collect(Collectors.toMap(org.kie.dmn.model.api.DRGElement::getId,
                                                                                                                                    dmn -> new SimpleEntry<>(dmn,
                                                                                                                                                             dmnToStunner(dmn))));

        Optional<org.kie.dmn.model.api.dmndi.DMNDiagram> dmnDDDiagram = findDMNDiagram(dmnXml);

        for (Entry<org.kie.dmn.model.api.DRGElement, Node> kv : elems.values()) {
            org.kie.dmn.model.api.DRGElement elem = kv.getKey();
            Node currentNode = kv.getValue();

            // Stunner rely on relative positioning for Edge connections, so need to cycle on DMNShape first.
            ddExtAugmentStunner(dmnDDDiagram, currentNode);
        }

        for (Entry<org.kie.dmn.model.api.DRGElement, Node> kv : elems.values()) {
            org.kie.dmn.model.api.DRGElement elem = kv.getKey();
            Node currentNode = kv.getValue();

            // DMN spec table 2: Requirements connection rules
            if (elem instanceof org.kie.dmn.model.api.Decision) {
                org.kie.dmn.model.api.Decision decision = (org.kie.dmn.model.api.Decision) elem;
                for (org.kie.dmn.model.api.InformationRequirement ir : decision.getInformationRequirement()) {
                    if (ir.getRequiredInput() != null) {
                        String reqInputID = getId(ir.getRequiredInput());
                        Node requiredNode = elems.get(reqInputID).getValue();
                        Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ir),
                                                                org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement.class).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge, ir.getId(), dmnXml);
                    }
                    if (ir.getRequiredDecision() != null) {
                        String reqInputID = getId(ir.getRequiredDecision());
                        Node requiredNode = elems.get(reqInputID).getValue();
                        Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ir),
                                                                org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement.class).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge, ir.getId(), dmnXml);
                    }
                }
                for (org.kie.dmn.model.api.KnowledgeRequirement kr : decision.getKnowledgeRequirement()) {
                    String reqInputID = getId(kr.getRequiredKnowledge());
                    Node requiredNode = elems.get(reqInputID).getValue();
                    Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(kr),
                                                            org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement.class).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge, kr.getId(), dmnXml);
                }
                for (org.kie.dmn.model.api.AuthorityRequirement kr : decision.getAuthorityRequirement()) {
                    String reqInputID = getId(kr.getRequiredAuthority());
                    Node requiredNode = elems.get(reqInputID).getValue();
                    Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(kr),
                                                            org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement.class).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge, kr.getId(), dmnXml);
                }
            } else if (elem instanceof org.kie.dmn.model.api.BusinessKnowledgeModel) {
                org.kie.dmn.model.api.BusinessKnowledgeModel bkm = (org.kie.dmn.model.api.BusinessKnowledgeModel) elem;
                for (org.kie.dmn.model.api.KnowledgeRequirement kr : bkm.getKnowledgeRequirement()) {
                    String reqInputID = getId(kr.getRequiredKnowledge());
                    Node requiredNode = elems.get(reqInputID).getValue();
                    Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(kr),
                                                            org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement.class).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge, kr.getId(), dmnXml);
                }
                for (org.kie.dmn.model.api.AuthorityRequirement kr : bkm.getAuthorityRequirement()) {
                    String reqInputID = getId(kr.getRequiredAuthority());
                    Node requiredNode = elems.get(reqInputID).getValue();
                    Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(kr),
                                                            org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement.class).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge, kr.getId(), dmnXml);
                }
            } else if (elem instanceof org.kie.dmn.model.api.KnowledgeSource) {
                org.kie.dmn.model.api.KnowledgeSource ks = (org.kie.dmn.model.api.KnowledgeSource) elem;
                for (org.kie.dmn.model.api.AuthorityRequirement ir : ks.getAuthorityRequirement()) {
                    if (ir.getRequiredInput() != null) {
                        String reqInputID = getId(ir.getRequiredInput());
                        Node requiredNode = elems.get(reqInputID).getValue();
                        Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ir),
                                                                org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement.class).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge, ir.getId(), dmnXml);
                    }
                    if (ir.getRequiredDecision() != null) {
                        String reqInputID = getId(ir.getRequiredDecision());
                        Node requiredNode = elems.get(reqInputID).getValue();
                        Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ir),
                                                                org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement.class).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge, ir.getId(), dmnXml);
                    }
                    if (ir.getRequiredAuthority() != null) {
                        String reqInputID = getId(ir.getRequiredAuthority());
                        Node requiredNode = elems.get(reqInputID).getValue();
                        Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ir),
                                                                org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement.class).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge, ir.getId(), dmnXml);
                    }
                }
            }
        }

        Map<String, Node<View<TextAnnotation>, ?>> textAnnotations = dmnXml.getArtifact().stream().filter(org.kie.dmn.model.api.TextAnnotation.class::isInstance).map(org.kie.dmn.model.api.TextAnnotation.class::cast)
                .collect(Collectors.toMap(org.kie.dmn.model.api.TextAnnotation::getId,
                                          textAnnotationConverter::nodeFromDMN));
        textAnnotations.values().forEach(n -> ddExtAugmentStunner(dmnDDDiagram, n));

        List<org.kie.dmn.model.api.Association> associations = dmnXml.getArtifact().stream().filter(org.kie.dmn.model.api.Association.class::isInstance).map(org.kie.dmn.model.api.Association.class::cast).collect(
                Collectors.toList());
        for (org.kie.dmn.model.api.Association a : associations) {
            String sourceId = getId(a.getSourceRef());
            Node sourceNode = Optional.ofNullable(elems.get(sourceId)).map(Entry::getValue).orElse(textAnnotations.get(sourceId));

            String targetId = getId(a.getTargetRef());
            Node targetNode = Optional.ofNullable(elems.get(targetId)).map(Entry::getValue).orElse(textAnnotations.get(targetId));

            @SuppressWarnings("unchecked")
            Edge<View<Association>, ?> myEdge = (Edge<View<Association>, ?>) factoryManager.newElement(idOfDMNorWBUUID(a),
                                                                                                       Association.class).asEdge();

            Id id = new Id(a.getId());
            Description description = new Description(a.getDescription());
            Association definition = new Association(id,
                                                     description);
            myEdge.getContent().setDefinition(definition);

            connectEdge(myEdge,
                        sourceNode,
                        targetNode);
            setConnectionMagnets(myEdge, a.getId(), dmnXml);
        }

        Graph graph = factoryManager.newDiagram("prova",
                                                BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class),
                                                metadata).getGraph();
        elems.values().stream().map(Map.Entry::getValue).forEach(graph::addNode);
        textAnnotations.values().forEach(graph::addNode);

        Node<?, ?> dmnDiagramRoot = findDMNDiagramRoot(graph);
        Definitions definitionsStunnerPojo = DefinitionsConverter.wbFromDMN(dmnXml);
        ((View<DMNDiagram>) dmnDiagramRoot.getContent()).getDefinition().setDefinitions(definitionsStunnerPojo);
        elems.values().stream().map(Map.Entry::getValue).forEach(node -> connectRootWithChild(dmnDiagramRoot,
                                                                                              node));
        textAnnotations.values().stream().forEach(node -> connectRootWithChild(dmnDiagramRoot,
                                                                               node));

        return graph;
    }

    private static String idOfDMNorWBUUID(org.kie.dmn.model.api.DMNElement dmn) {
        return dmn.getId() != null ? dmn.getId() : UUID.uuid();
    }

    public static Node<?, ?> findDMNDiagramRoot(final Graph<?, Node<View, ?>> graph) {
        return StreamSupport.stream(graph.nodes().spliterator(),
                                    false).filter(n -> n.getContent().getDefinition() instanceof DMNDiagram).findFirst().orElseThrow(() -> new UnsupportedOperationException("TODO"));
    }

    private String getId(org.kie.dmn.model.api.DMNElementReference er) {
        String href = er.getHref();
        return href.contains("#") ? href.substring(href.indexOf('#') + 1) : href;
    }

    private Node dmnToStunner(org.kie.dmn.model.api.DRGElement dmn) {
        if (dmn instanceof org.kie.dmn.model.api.InputData) {
            return inputDataConverter.nodeFromDMN((org.kie.dmn.model.api.InputData) dmn);
        } else if (dmn instanceof org.kie.dmn.model.api.Decision) {
            return decisionConverter.nodeFromDMN((org.kie.dmn.model.api.Decision) dmn);
        } else if (dmn instanceof org.kie.dmn.model.api.BusinessKnowledgeModel) {
            return bkmConverter.nodeFromDMN((org.kie.dmn.model.api.BusinessKnowledgeModel) dmn);
        } else if (dmn instanceof org.kie.dmn.model.api.KnowledgeSource) {
            return knowledgeSourceConverter.nodeFromDMN((org.kie.dmn.model.api.KnowledgeSource) dmn);
        } else {
            throw new UnsupportedOperationException("TODO"); // TODO 
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void connectRootWithChild(final Node dmnDiagramRoot,
                                            final Node child) {
        final String uuid = org.kie.workbench.common.stunner.core.util.UUID.uuid();
        final Edge<Child, Node> edge = new EdgeImpl<>(uuid);
        edge.setContent(new Child());
        connectEdge(edge,
                    dmnDiagramRoot,
                    child);
        Definitions definitions = ((DMNDiagram) ((View) dmnDiagramRoot.getContent()).getDefinition()).getDefinitions();
        DMNModelInstrumentedBase childDRG = (DMNModelInstrumentedBase) ((View) child.getContent()).getDefinition();
        childDRG.setParent(definitions);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void connectEdge(final Edge edge,
                                   final Node source,
                                   final Node target) {
        edge.setSourceNode(source);
        edge.setTargetNode(target);
        source.getOutEdges().add(edge);
        target.getInEdges().add(edge);
    }

    @SuppressWarnings("unchecked")
    private void setConnectionMagnets(final Edge edge, String dmnEdgeElementRef, org.kie.dmn.model.api.Definitions dmnXml) {
        final ViewConnector connectionContent = (ViewConnector) edge.getContent();

        Optional<org.kie.dmn.model.api.dmndi.DMNDiagram> dmnDiagram = findDMNDiagram(dmnXml);
        Optional<DMNEdge> dmnEdge = Optional.empty();
        if (dmnDiagram.isPresent()) {
            dmnEdge = dmnDiagram.get().getDMNDiagramElement().stream()
                                .filter(DMNEdge.class::isInstance)
                                .map(DMNEdge.class::cast)
                                .filter(e -> e.getDmnElementRef().getLocalPart().equals(dmnEdgeElementRef))
                                .findFirst();
        }
        if (dmnEdge.isPresent()) {
            DMNEdge e = dmnEdge.get();
            Point source = e.getWaypoint().get(0);
            final Node sourceNode = edge.getSourceNode();
            if (null != sourceNode) {
                View<?> sourceView = (View<?>) sourceNode.getContent();
                double xSource = xOfBound(upperLeftBound(sourceView));
                double ySource = yOfBound(upperLeftBound(sourceView));
                connectionContent.setSourceConnection(MagnetConnection.Builder.at(source.getX() - xSource, source.getY() - ySource)); // Stunner connection x,y is relative to shape
            }
            Point target = e.getWaypoint().get(e.getWaypoint().size() - 1);
            final Node targetNode = edge.getTargetNode();
            if (null != targetNode) {
                View<?> targetView = (View<?>) targetNode.getContent();
                double xTarget = xOfBound(upperLeftBound(targetView));
                double yTarget = yOfBound(upperLeftBound(targetView));
                connectionContent.setTargetConnection(MagnetConnection.Builder.at(target.getX() - xTarget, target.getY() - yTarget)); // Stunner connection x,y is relative to shape
            }
            if (e.getWaypoint().size() > 2) {
                List<Point> sublist = e.getWaypoint().subList(1, e.getWaypoint().size() - 1);
                for (Point p : sublist) {
                    connectionContent.getControlPoints().add(ControlPoint.build(PointUtils.dmndiPointToPoint2D(p)));
                }
            }
        } else {
            // Set the source connection, if any.
            final Node sourceNode = edge.getSourceNode();
            if (null != sourceNode) {
                connectionContent.setSourceConnection(MagnetConnection.Builder.forElement(sourceNode));
            }
            // Set the target connection, if any.
            final Node targetNode = edge.getTargetNode();
            if (null != targetNode) {
                connectionContent.setTargetConnection(MagnetConnection.Builder.forElement(targetNode));
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String marshall(final Diagram<Graph, Metadata> diagram) throws IOException {
        Graph<?, Node<View, ?>> g = diagram.getGraph();

        Map<String, org.kie.dmn.model.api.DRGElement> nodes = new HashMap<>();
        Map<String, org.kie.dmn.model.api.TextAnnotation> textAnnotations = new HashMap<>();

        Node<View<DMNDiagram>, ?> dmnDiagramRoot = (Node<View<DMNDiagram>, ?>) findDMNDiagramRoot(g);
        Definitions definitionsStunnerPojo = dmnDiagramRoot.getContent().getDefinition().getDefinitions();
        org.kie.dmn.model.api.Definitions definitions = DefinitionsConverter.dmnFromWB(definitionsStunnerPojo);
        if (definitions.getExtensionElements() == null) {
            if (definitions instanceof org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase) {
                definitions.setExtensionElements(new org.kie.dmn.model.v1_1.TDMNElement.TExtensionElements());
            } else if (definitions instanceof org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase) {
                definitions.setExtensionElements(new org.kie.dmn.model.v1_2.TDMNElement.TExtensionElements());
            } else {
                definitions.setExtensionElements(new org.kie.dmn.model.v1_2.TDMNElement.TExtensionElements());
            }
        }

        if (definitions.getDMNDI() == null) {
            definitions.setDMNDI(new DMNDI());
        }
        org.kie.dmn.model.api.dmndi.DMNDiagram dmnDDDMNDiagram = new org.kie.dmn.model.v1_2.dmndi.DMNDiagram();
        definitions.getDMNDI().getDMNDiagram().add(dmnDDDMNDiagram);
        List<DMNEdge> dmnEdges = new ArrayList<>();

        for (Node<?, ?> node : g.nodes()) {
            if (node.getContent() instanceof View<?>) {
                View<?> view = (View<?>) node.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    DRGElement n = (org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement) view.getDefinition();
                    nodes.put(n.getId().getValue(),
                              stunnerToDMN(node));
                    dmnDDDMNDiagram.getDMNDiagramElement().add(stunnerToDDExt((View<? extends DMNElement>) view));
                } else if (view.getDefinition() instanceof TextAnnotation) {
                    TextAnnotation textAnnotation = (TextAnnotation) view.getDefinition();
                    textAnnotations.put(textAnnotation.getId().getValue(),
                                        textAnnotationConverter.dmnFromNode((Node<View<TextAnnotation>, ?>) node));
                    dmnDDDMNDiagram.getDMNDiagramElement().add(stunnerToDDExt((View<? extends DMNElement>) view));

                    List<org.kie.dmn.model.api.Association> associations = AssociationConverter.dmnFromWB((Node<View<TextAnnotation>, ?>) node);
                    definitions.getArtifact().addAll(associations);
                }
                // DMNDI Edge management.
                List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
                for (Edge<?, ?> e : inEdges) {
                    if (e.getContent() instanceof ViewConnector) {
                        final ViewConnector connectionContent = (ViewConnector) e.getContent();
                        if (connectionContent.getSourceConnection().isPresent() && connectionContent.getTargetConnection().isPresent()) {
                            Point2D sourcePoint = ((Connection) connectionContent.getSourceConnection().get()).getLocation();
                            Point2D targetPoint = ((Connection) connectionContent.getTargetConnection().get()).getLocation();
                            if (sourcePoint == null) { // If the "connection source/target location is null" assume it's the centre of the shape.
                                View<?> sourceView = (View<?>) e.getSourceNode().getContent();
                                double xSource = xOfBound(upperLeftBound(sourceView));
                                double ySource = yOfBound(upperLeftBound(sourceView));
                                if (sourceView.getDefinition() instanceof DMNViewDefinition) {
                                    DMNViewDefinition dmnViewDefinition = (DMNViewDefinition) sourceView.getDefinition();
                                    xSource += dmnViewDefinition.getDimensionsSet().getWidth().getValue() / 2;
                                    ySource += dmnViewDefinition.getDimensionsSet().getHeight().getValue() / 2;
                                }
                                sourcePoint = Point2D.create(xSource, ySource);
                            } else { // If it is non-null it is relative to the source/target shape location.
                                View<?> sourceView = (View<?>) e.getSourceNode().getContent();
                                double xSource = xOfBound(upperLeftBound(sourceView));
                                double ySource = yOfBound(upperLeftBound(sourceView));
                                sourcePoint = Point2D.create(xSource + sourcePoint.getX(), ySource + sourcePoint.getY());
                            }
                            if (targetPoint == null) { // If the "connection source/target location is null" assume it's the centre of the shape.
                                double xTarget = xOfBound(upperLeftBound(view));
                                double yTarget = yOfBound(upperLeftBound(view));
                                if (view.getDefinition() instanceof DMNViewDefinition) {
                                    DMNViewDefinition dmnViewDefinition = (DMNViewDefinition) view.getDefinition();
                                    xTarget += dmnViewDefinition.getDimensionsSet().getWidth().getValue() / 2;
                                    yTarget += dmnViewDefinition.getDimensionsSet().getHeight().getValue() / 2;
                                }
                                targetPoint = Point2D.create(xTarget, yTarget);
                            } else { // If it is non-null it is relative to the source/target shape location.
                                double xTarget = xOfBound(upperLeftBound(view));
                                double yTarget = yOfBound(upperLeftBound(view));
                                targetPoint = Point2D.create(xTarget + targetPoint.getX(), yTarget + targetPoint.getY());
                            }

                            DMNEdge dmnEdge = new org.kie.dmn.model.v1_2.dmndi.DMNEdge();
                            // DMNDI edge elementRef is uuid of Stunner edge, 
                            // with the only exception when edge contains as content a DMN Association (Association is an edge)
                            String uuid = e.getUUID();
                            if (e.getContent() instanceof View<?>) {
                                final View<?> edgeView = (View<?>) e.getContent();
                                if (edgeView.getDefinition() instanceof Association) {
                                    uuid = ((Association) edgeView.getDefinition()).getId().getValue();
                                }
                            }
                            dmnEdge.setId("dmnedge-" + uuid);
                            dmnEdge.setDmnElementRef(new QName(uuid));

                            dmnEdge.getWaypoint().add(PointUtils.point2dToDMNDIPoint(sourcePoint));
                            for (ControlPoint cp : connectionContent.getControlPoints()) {
                                dmnEdge.getWaypoint().add(PointUtils.point2dToDMNDIPoint(cp.getLocation()));
                            }
                            dmnEdge.getWaypoint().add(PointUtils.point2dToDMNDIPoint(targetPoint));
                            dmnEdges.add(dmnEdge);
                        }
                    }
                }
            }
        }

        nodes.values().forEach(n -> {
            n.setParent(definitions);
            definitions.getDrgElement().add(n);
        });
        textAnnotations.values().forEach(definitions.getArtifact()::add);

        // add DMNEdge last.
        dmnDDDMNDiagram.getDMNDiagramElement().addAll(dmnEdges);

        return marshaller.marshal(definitions);
    }

    private void ddExtAugmentStunner(Optional<org.kie.dmn.model.api.dmndi.DMNDiagram> dmnDDDiagram, Node currentNode) {
        if (!dmnDDDiagram.isPresent()) {
            return;
        }

        Stream<DMNShape> drgShapeStream = dmnDDDiagram.get().getDMNDiagramElement().stream().filter(DMNShape.class::isInstance).map(DMNShape.class::cast);

        View content = (View) currentNode.getContent();
        if (content.getDefinition() instanceof Decision) {
            Decision d = (Decision) content.getDefinition();
            internalAugment(drgShapeStream, d.getId(), upperLeftBound(content), d.getDimensionsSet(), lowerRightBound(content), d.getBackgroundSet(), d::setFontSet);
        } else if (content.getDefinition() instanceof InputData) {
            InputData d = (InputData) content.getDefinition();
            internalAugment(drgShapeStream, d.getId(), upperLeftBound(content), d.getDimensionsSet(), lowerRightBound(content), d.getBackgroundSet(), d::setFontSet);
        } else if (content.getDefinition() instanceof BusinessKnowledgeModel) {
            BusinessKnowledgeModel d = (BusinessKnowledgeModel) content.getDefinition();
            internalAugment(drgShapeStream, d.getId(), upperLeftBound(content), d.getDimensionsSet(), lowerRightBound(content), d.getBackgroundSet(), d::setFontSet);
        } else if (content.getDefinition() instanceof KnowledgeSource) {
            KnowledgeSource d = (KnowledgeSource) content.getDefinition();
            internalAugment(drgShapeStream, d.getId(), upperLeftBound(content), d.getDimensionsSet(), lowerRightBound(content), d.getBackgroundSet(), d::setFontSet);
        } else if (content.getDefinition() instanceof TextAnnotation) {
            TextAnnotation d = (TextAnnotation) content.getDefinition();
            internalAugment(drgShapeStream, d.getId(), upperLeftBound(content), d.getDimensionsSet(), lowerRightBound(content), d.getBackgroundSet(), d::setFontSet);
        }
    }

    private void internalAugment(Stream<DMNShape> drgShapeStream, Id id, Bound ul, RectangleDimensionsSet dimensionsSet, Bound lr, BackgroundSet bgset, Consumer<FontSet> fontSetSetter) {
        Optional<DMNShape> drgShapeOpt = drgShapeStream.filter(shape -> shape.getDmnElementRef().getLocalPart().equals(id.getValue())).findFirst();
        if (!drgShapeOpt.isPresent()) {
            return;
        }
        DMNShape drgShape = drgShapeOpt.get();

        if (ul != null) {
            ((BoundImpl) ul).setX(xOfShape(drgShape));
            ((BoundImpl) ul).setY(yOfShape(drgShape));
        }
        dimensionsSet.setWidth(new Width(widthOfShape(drgShape)));
        dimensionsSet.setHeight(new Height(heightOfShape(drgShape)));
        if (lr != null) {
            ((BoundImpl) lr).setX(xOfShape(drgShape) + widthOfShape(drgShape));
            ((BoundImpl) lr).setY(yOfShape(drgShape) + heightOfShape(drgShape));
        }

        DMNStyle dmnStyleOfDrgShape = drgShape.getStyle() instanceof DMNStyle ? (DMNStyle) drgShape.getStyle() : null;
        if (dmnStyleOfDrgShape != null) {
            if (null != dmnStyleOfDrgShape.getFillColor()) {
                bgset.setBgColour(new BgColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getFillColor())));
            }
            if (null != dmnStyleOfDrgShape.getStrokeColor()) {
                bgset.setBorderColour(new BorderColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getStrokeColor())));
            }
        }

        FontSet fontSet = new FontSet();
        if (dmnStyleOfDrgShape != null && dmnStyleOfDrgShape.getFontFamily() != null) {
            mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN(dmnStyleOfDrgShape));
        }
        if (drgShape.getDMNLabel() != null && drgShape.getDMNLabel().getSharedStyle() instanceof DMNStyle) {
            mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN((DMNStyle) drgShape.getDMNLabel().getSharedStyle()));
        }
        if (drgShape.getDMNLabel() != null && drgShape.getDMNLabel().getStyle() instanceof DMNStyle) {
            mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN((DMNStyle) drgShape.getDMNLabel().getStyle()));
        }
        fontSetSetter.accept(fontSet);
    }

    private static void mergeFontSet(FontSet fontSet, FontSet additional) {
        if (additional.getFontFamily() != null) {
            fontSet.setFontFamily(additional.getFontFamily());
        }
        if (additional.getFontSize() != null) {
            fontSet.setFontSize(additional.getFontSize());
        }
        if (additional.getFontColour() != null) {
            fontSet.setFontColour(additional.getFontColour());
        }
    }

    private static DMNShape stunnerToDDExt(View<? extends DMNElement> v) {
        DMNShape result = new org.kie.dmn.model.v1_2.dmndi.DMNShape();
        result.setId("dmnshape-" + v.getDefinition().getId().getValue());
        result.setDmnElementRef(new QName(v.getDefinition().getId().getValue()));
        Bounds bounds = new org.kie.dmn.model.v1_2.dmndi.Bounds();
        result.setBounds(bounds);
        bounds.setX(xOfBound(upperLeftBound(v)));
        bounds.setY(yOfBound(upperLeftBound(v)));
        result.setStyle(new org.kie.dmn.model.v1_2.dmndi.DMNStyle());
        result.setDMNLabel(new org.kie.dmn.model.v1_2.dmndi.DMNLabel());
        if (v.getDefinition() instanceof Decision) {
            Decision d = (Decision) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            applyFontStyle(d.getFontSet(), result);
        } else if (v.getDefinition() instanceof InputData) {
            InputData d = (InputData) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            applyFontStyle(d.getFontSet(), result);
        } else if (v.getDefinition() instanceof BusinessKnowledgeModel) {
            BusinessKnowledgeModel d = (BusinessKnowledgeModel) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            applyFontStyle(d.getFontSet(), result);
        } else if (v.getDefinition() instanceof KnowledgeSource) {
            KnowledgeSource d = (KnowledgeSource) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            applyFontStyle(d.getFontSet(), result);
        } else if (v.getDefinition() instanceof TextAnnotation) {
            TextAnnotation d = (TextAnnotation) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            applyFontStyle(d.getFontSet(), result);
        }
        return result;
    }

    private static void applyFontStyle(FontSet fontSet, DMNShape result) {
        if (!(result.getStyle() instanceof DMNStyle)) {
            return;
        }
        DMNStyle shapeStyle = (DMNStyle) result.getStyle();
        Color fontColor = ColorUtils.dmnFromWB(fontSet.getFontColour().getValue());
        shapeStyle.setFontColor(fontColor);
        if (null != fontSet.getFontFamily().getValue()) {
            shapeStyle.setFontFamily(fontSet.getFontFamily().getValue());
        }
        if (null != fontSet.getFontSize().getValue()) {
            shapeStyle.setFontSize(fontSet.getFontSize().getValue());
        }
    }

    private static void applyBounds(final RectangleDimensionsSet dimensionsSet,
                                    final Bounds bounds) {
        if (null != dimensionsSet.getWidth().getValue() &&
                null != dimensionsSet.getHeight().getValue()) {
            bounds.setWidth(dimensionsSet.getWidth().getValue());
            bounds.setHeight(dimensionsSet.getHeight().getValue());
        }
    }

    private static void applyBackgroundStyles(final BackgroundSet bgset,
                                              final DMNShape result) {
        if (!(result.getStyle() instanceof DMNStyle)) {
            return;
        }
        DMNStyle style = (DMNStyle) result.getStyle();
        if (null != bgset.getBgColour().getValue()) {
            style.setFillColor(ColorUtils.dmnFromWB(bgset.getBgColour().getValue()));
        }
        if (null != bgset.getBorderColour().getValue()) {
            style.setStrokeColor(ColorUtils.dmnFromWB(bgset.getBorderColour().getValue()));
        }
    }

    @SuppressWarnings("unchecked")
    private org.kie.dmn.model.api.DRGElement stunnerToDMN(final Node<?, ?> node) {
        if (node.getContent() instanceof View<?>) {
            View<?> view = (View<?>) node.getContent();
            if (view.getDefinition() instanceof InputData) {
                return inputDataConverter.dmnFromNode((Node<View<InputData>, ?>) node);
            } else if (view.getDefinition() instanceof Decision) {
                return decisionConverter.dmnFromNode((Node<View<Decision>, ?>) node);
            } else if (view.getDefinition() instanceof BusinessKnowledgeModel) {
                return bkmConverter.dmnFromNode((Node<View<BusinessKnowledgeModel>, ?>) node);
            } else if (view.getDefinition() instanceof KnowledgeSource) {
                return knowledgeSourceConverter.dmnFromNode((Node<View<KnowledgeSource>, ?>) node);
            } else {
                throw new UnsupportedOperationException("TODO"); // TODO 
            }
        }
        throw new RuntimeException("wrong diagram structure to marshall");
    }

    @Override
    public DiagramMetadataMarshaller<Metadata> getMetadataMarshaller() {
        return diagramMetadataMarshaller;
    }

    private static Bound upperLeftBound(final View view) {
        if (view != null) {
            if (view.getBounds() != null) {
                return view.getBounds().getUpperLeft();
            }
        }
        return null;
    }

    private static Bound lowerRightBound(final View view) {
        if (view != null) {
            if (view.getBounds() != null) {
                return view.getBounds().getLowerRight();
            }
        }
        return null;
    }

    private static double xOfBound(final Bound bound) {
        if (bound != null) {
            return bound.getX();
        }
        return 0.0;
    }

    private static double yOfBound(final Bound bound) {
        if (bound != null) {
            return bound.getY();
        }
        return 0.0;
    }

    private static double xOfShape(final DMNShape shape) {
        if (shape != null) {
            if (shape.getBounds() != null) {
                return shape.getBounds().getX();
            }
        }
        return 0.0;
    }

    private static double yOfShape(final DMNShape shape) {
        if (shape != null) {
            if (shape.getBounds() != null) {
                return shape.getBounds().getY();
            }
        }
        return 0.0;
    }

    private static double widthOfShape(final DMNShape shape) {
        if (shape != null) {
            if (shape.getBounds() != null) {
                return shape.getBounds().getWidth();
            }
        }
        return 0.0;
    }

    private static double heightOfShape(final DMNShape shape) {
        if (shape != null) {
            if (shape.getBounds() != null) {
                return shape.getBounds().getHeight();
            }
        }
        return 0.0;
    }
}