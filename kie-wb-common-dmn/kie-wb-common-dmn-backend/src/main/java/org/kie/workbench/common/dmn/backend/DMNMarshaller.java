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
import java.util.Arrays;
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

import org.jboss.errai.marshalling.server.ServerMarshalling;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.background.BgColour;
import org.kie.workbench.common.dmn.api.property.background.BorderColour;
import org.kie.workbench.common.dmn.api.property.background.BorderSize;
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
import org.kie.workbench.common.dmn.backend.definition.v1_1.FontSetPropertyConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.InputDataConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.KnowledgeSourceConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.TextAnnotationConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ColorUtils;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DDExtensionsRegister;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNShape;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.org.omg.spec.CMMN_20151109_DC.Bounds;
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
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
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
    private org.kie.dmn.api.marshalling.v1_1.DMNMarshaller marshaller;

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
        this.marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList(new DDExtensionsRegister()));
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

    private static Optional<org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram> findDMNDiagram(org.kie.dmn.model.v1_1.Definitions dmnXml) {
        if (dmnXml.getExtensionElements() == null) {
            return Optional.empty();
        }

        List<org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram> elems = dmnXml.getExtensionElements().getAny().stream()
                .filter(org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram.class::isInstance)
                .map(org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram.class::cast)
                .collect(Collectors.toList());

        if (elems.size() != 1) {
            return Optional.empty();
        } else {
            return Optional.of(elems.get(0));
        }
    }

    @Override
    public Graph unmarshall(final Metadata metadata,
                            final InputStream input) throws IOException {
        org.kie.dmn.model.v1_1.Definitions dmnXml = marshaller.unmarshal(new InputStreamReader(input));

        Map<String, Entry<org.kie.dmn.model.v1_1.DRGElement, Node>> elems = dmnXml.getDrgElement().stream().collect(Collectors.toMap(org.kie.dmn.model.v1_1.DRGElement::getId,
                                                                                                                                     dmn -> new SimpleEntry<>(dmn,
                                                                                                                                                              dmnToStunner(dmn))));

        Optional<org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram> dmnDDDiagram = findDMNDiagram(dmnXml);

        for (Entry<org.kie.dmn.model.v1_1.DRGElement, Node> kv : elems.values()) {
            org.kie.dmn.model.v1_1.DRGElement elem = kv.getKey();
            Node currentNode = kv.getValue();

            ddExtAugmentStunner(dmnDDDiagram, currentNode);

            // DMN spec table 2: Requirements connection rules
            if (elem instanceof org.kie.dmn.model.v1_1.Decision) {
                org.kie.dmn.model.v1_1.Decision decision = (org.kie.dmn.model.v1_1.Decision) elem;
                for (org.kie.dmn.model.v1_1.InformationRequirement ir : decision.getInformationRequirement()) {
                    if (ir.getRequiredInput() != null) {
                        String reqInputID = getId(ir.getRequiredInput());
                        Node requiredNode = elems.get(reqInputID).getValue();
                        Edge myEdge = factoryManager.newElement(UUID.uuid(),
                                                                org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement.class).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge);
                    }
                    if (ir.getRequiredDecision() != null) {
                        String reqInputID = getId(ir.getRequiredDecision());
                        Node requiredNode = elems.get(reqInputID).getValue();
                        Edge myEdge = factoryManager.newElement(UUID.uuid(),
                                                                org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement.class).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge);
                    }
                }
                for (org.kie.dmn.model.v1_1.KnowledgeRequirement kr : decision.getKnowledgeRequirement()) {
                    String reqInputID = getId(kr.getRequiredKnowledge());
                    Node requiredNode = elems.get(reqInputID).getValue();
                    Edge myEdge = factoryManager.newElement(UUID.uuid(),
                                                            org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement.class).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge);
                }
                for (org.kie.dmn.model.v1_1.AuthorityRequirement kr : decision.getAuthorityRequirement()) {
                    String reqInputID = getId(kr.getRequiredAuthority());
                    Node requiredNode = elems.get(reqInputID).getValue();
                    Edge myEdge = factoryManager.newElement(UUID.uuid(),
                                                            org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement.class).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge);
                }
            } else if (elem instanceof org.kie.dmn.model.v1_1.BusinessKnowledgeModel) {
                org.kie.dmn.model.v1_1.BusinessKnowledgeModel bkm = (org.kie.dmn.model.v1_1.BusinessKnowledgeModel) elem;
                for (org.kie.dmn.model.v1_1.KnowledgeRequirement kr : bkm.getKnowledgeRequirement()) {
                    String reqInputID = getId(kr.getRequiredKnowledge());
                    Node requiredNode = elems.get(reqInputID).getValue();
                    Edge myEdge = factoryManager.newElement(UUID.uuid(),
                                                            org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement.class).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge);
                }
                for (org.kie.dmn.model.v1_1.AuthorityRequirement kr : bkm.getAuthorityRequirement()) {
                    String reqInputID = getId(kr.getRequiredAuthority());
                    Node requiredNode = elems.get(reqInputID).getValue();
                    Edge myEdge = factoryManager.newElement(UUID.uuid(),
                                                            org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement.class).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge);
                }
            } else if (elem instanceof org.kie.dmn.model.v1_1.KnowledgeSource) {
                org.kie.dmn.model.v1_1.KnowledgeSource ks = (org.kie.dmn.model.v1_1.KnowledgeSource) elem;
                for (org.kie.dmn.model.v1_1.AuthorityRequirement ir : ks.getAuthorityRequirement()) {
                    if (ir.getRequiredInput() != null) {
                        String reqInputID = getId(ir.getRequiredInput());
                        Node requiredNode = elems.get(reqInputID).getValue();
                        Edge myEdge = factoryManager.newElement(UUID.uuid(),
                                                                org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement.class).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge);
                    }
                    if (ir.getRequiredDecision() != null) {
                        String reqInputID = getId(ir.getRequiredDecision());
                        Node requiredNode = elems.get(reqInputID).getValue();
                        Edge myEdge = factoryManager.newElement(UUID.uuid(),
                                                                org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement.class).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge);
                    }
                    if (ir.getRequiredAuthority() != null) {
                        String reqInputID = getId(ir.getRequiredAuthority());
                        Node requiredNode = elems.get(reqInputID).getValue();
                        Edge myEdge = factoryManager.newElement(UUID.uuid(),
                                                                org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement.class).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge);
                    }
                }
            }
        }

        Map<String, Node<View<TextAnnotation>, ?>> textAnnotations = dmnXml.getArtifact().stream().filter(org.kie.dmn.model.v1_1.TextAnnotation.class::isInstance).map(org.kie.dmn.model.v1_1.TextAnnotation.class::cast).collect(Collectors.toMap(org.kie.dmn.model.v1_1.TextAnnotation::getId,
                                                                                                                                                                                                                                                   textAnnotationConverter::nodeFromDMN));
        textAnnotations.values().forEach(n -> ddExtAugmentStunner(dmnDDDiagram, n));

        List<org.kie.dmn.model.v1_1.Association> associations = dmnXml.getArtifact().stream().filter(org.kie.dmn.model.v1_1.Association.class::isInstance).map(org.kie.dmn.model.v1_1.Association.class::cast).collect(Collectors.toList());
        for (org.kie.dmn.model.v1_1.Association a : associations) {
            String sourceId = getId(a.getSourceRef());
            Node sourceNode = Optional.ofNullable(elems.get(sourceId)).map(Entry::getValue).orElse(textAnnotations.get(sourceId));

            String targetId = getId(a.getTargetRef());
            Node targetNode = Optional.ofNullable(elems.get(targetId)).map(Entry::getValue).orElse(textAnnotations.get(targetId));

            @SuppressWarnings("unchecked")
            Edge<View<Association>, ?> myEdge = (Edge<View<Association>, ?>) factoryManager.newElement(UUID.uuid(),
                                                                                                       Association.class).asEdge();

            Id id = new Id(a.getId());
            Description description = new Description(a.getDescription());
            Association definition = new Association(id,
                                                     description);
            myEdge.getContent().setDefinition(definition);

            connectEdge(myEdge,
                        sourceNode,
                        targetNode);
            setConnectionMagnets(myEdge);
        }

        Graph graph = factoryManager.newDiagram("prova",
                                                BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class),
                                                metadata).getGraph();
        elems.values().stream().map(kv -> kv.getValue()).forEach(graph::addNode);
        textAnnotations.values().forEach(graph::addNode);

        @SuppressWarnings("unchecked")
        Node<View<DMNDiagram>, ?> dmnDiagramRoot = findDMNDiagramRoot(graph);
        Definitions definitionsStunnerPojo = DefinitionsConverter.wbFromDMN(dmnXml);
        dmnDiagramRoot.getContent().getDefinition().setDefinitions(definitionsStunnerPojo);
        elems.values().stream().map(kv -> kv.getValue()).forEach(node -> connectRootWithChild(dmnDiagramRoot,
                                                                                              node));
        textAnnotations.values().stream().forEach(node -> connectRootWithChild(dmnDiagramRoot,
                                                                               node));

        return graph;
    }

    public static Node<?, ?> findDMNDiagramRoot(final Graph<?, Node<View, ?>> graph) {
        return StreamSupport.stream(graph.nodes().spliterator(),
                                    false).filter(n -> n.getContent().getDefinition() instanceof DMNDiagram).findFirst().orElseThrow(() -> new UnsupportedOperationException("TODO"));
    }

    private String getId(org.kie.dmn.model.v1_1.DMNElementReference er) {
        String href = er.getHref();
        return href.contains("#") ? href.substring(href.indexOf('#') + 1) : href;
    }

    private Node dmnToStunner(org.kie.dmn.model.v1_1.DRGElement dmn) {
        if (dmn instanceof org.kie.dmn.model.v1_1.InputData) {
            return inputDataConverter.nodeFromDMN((org.kie.dmn.model.v1_1.InputData) dmn);
        } else if (dmn instanceof org.kie.dmn.model.v1_1.Decision) {
            return decisionConverter.nodeFromDMN((org.kie.dmn.model.v1_1.Decision) dmn);
        } else if (dmn instanceof org.kie.dmn.model.v1_1.BusinessKnowledgeModel) {
            return bkmConverter.nodeFromDMN((org.kie.dmn.model.v1_1.BusinessKnowledgeModel) dmn);
        } else if (dmn instanceof org.kie.dmn.model.v1_1.KnowledgeSource) {
            return knowledgeSourceConverter.nodeFromDMN((org.kie.dmn.model.v1_1.KnowledgeSource) dmn);
        } else {
            throw new UnsupportedOperationException("TODO"); // TODO 
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectRootWithChild(final Node dmnDiagramRoot,
                                      final Node child) {
        final String uuid = org.kie.workbench.common.stunner.core.util.UUID.uuid();
        final Edge<Child, Node> edge = new EdgeImpl<>(uuid);
        edge.setContent(new Child());
        connectEdge(edge,
                    dmnDiagramRoot,
                    child);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectEdge(final Edge edge,
                             final Node source,
                             final Node target) {
        edge.setSourceNode(source);
        edge.setTargetNode(target);
        source.getOutEdges().add(edge);
        target.getInEdges().add(edge);
    }

    @SuppressWarnings("unchecked")
    private void setConnectionMagnets(final Edge edge) {
        final ViewConnector connectionContent = (ViewConnector) edge.getContent();
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

    @Override
    public String marshall(final Diagram<Graph, Metadata> diagram) throws IOException {
        Graph<?, Node<View, ?>> g = diagram.getGraph();

        Map<String, org.kie.dmn.model.v1_1.DRGElement> nodes = new HashMap<>();
        Map<String, org.kie.dmn.model.v1_1.TextAnnotation> textAnnotations = new HashMap<>();

        @SuppressWarnings("unchecked")
        Node<View<DMNDiagram>, ?> dmnDiagramRoot = (Node<View<DMNDiagram>, ?>) findDMNDiagramRoot(g);
        Definitions definitionsStunnerPojo = dmnDiagramRoot.getContent().getDefinition().getDefinitions();
        org.kie.dmn.model.v1_1.Definitions definitions = DefinitionsConverter.dmnFromWB(definitionsStunnerPojo);
        if (definitions.getExtensionElements() == null) {
            definitions.setExtensionElements(new org.kie.dmn.model.v1_1.Definitions.ExtensionElements());
        }
        org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram dmnDDDMNDiagram = new org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram();
        definitions.getExtensionElements().getAny().add(dmnDDDMNDiagram);

        for (Node<?, ?> node : g.nodes()) {
            if (node.getContent() instanceof View<?>) {
                View<?> view = (View<?>) node.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    DRGElement n = (org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement) view.getDefinition();
                    nodes.put(n.getId().getValue(),
                              stunnerToDMN(node));
                    dmnDDDMNDiagram.getAny().add(stunnerToDDExt((View<? extends DMNElement>) view));
                } else if (view.getDefinition() instanceof TextAnnotation) {
                    TextAnnotation textAnnotation = (TextAnnotation) view.getDefinition();
                    textAnnotations.put(textAnnotation.getId().getValue(),
                                        textAnnotationConverter.dmnFromNode((Node<View<TextAnnotation>, ?>) node));
                    dmnDDDMNDiagram.getAny().add(stunnerToDDExt((View<? extends DMNElement>) view));

                    List<org.kie.dmn.model.v1_1.Association> associations = AssociationConverter.dmnFromWB((Node<View<TextAnnotation>, ?>) node);
                    definitions.getArtifact().addAll(associations);
                }
            }
        }

        nodes.values().forEach(definitions.getDrgElement()::add);
        textAnnotations.values().forEach(definitions.getArtifact()::add);

        String marshalled = marshaller.marshal(definitions);

        return marshalled;
    }

    private void ddExtAugmentStunner(Optional<org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram> dmnDDDiagram, Node currentNode) {
        if (!dmnDDDiagram.isPresent()) {
            return;
        }

        Stream<DMNShape> drgShapeStream = dmnDDDiagram.get().getAny().stream().filter(DMNShape.class::isInstance).map(DMNShape.class::cast);

        View content = (View) currentNode.getContent();
        if (content.getDefinition() instanceof Decision) {
            Decision d = (Decision) content.getDefinition();
            internalAugment(drgShapeStream, d.getId(), content.getBounds().getUpperLeft(), d.getDimensionsSet(), content.getBounds().getLowerRight(), d.getBackgroundSet(), d::setFontSet);
        } else if (content.getDefinition() instanceof InputData) {
            InputData d = (InputData) content.getDefinition();
            internalAugment(drgShapeStream, d.getId(), content.getBounds().getUpperLeft(), d.getDimensionsSet(), content.getBounds().getLowerRight(), d.getBackgroundSet(), d::setFontSet);
        } else if (content.getDefinition() instanceof BusinessKnowledgeModel) {
            BusinessKnowledgeModel d = (BusinessKnowledgeModel) content.getDefinition();
            internalAugment(drgShapeStream, d.getId(), content.getBounds().getUpperLeft(), d.getDimensionsSet(), content.getBounds().getLowerRight(), d.getBackgroundSet(), d::setFontSet);
        } else if (content.getDefinition() instanceof KnowledgeSource) {
            KnowledgeSource d = (KnowledgeSource) content.getDefinition();
            internalAugment(drgShapeStream, d.getId(), content.getBounds().getUpperLeft(), d.getDimensionsSet(), content.getBounds().getLowerRight(), d.getBackgroundSet(), d::setFontSet);
        } else if (content.getDefinition() instanceof TextAnnotation) {
            TextAnnotation d = (TextAnnotation) content.getDefinition();
            internalAugment(drgShapeStream, d.getId(), content.getBounds().getUpperLeft(), d.getDimensionsSet(), content.getBounds().getLowerRight(), d.getBackgroundSet(), d::setFontSet);
        }
    }

    private void internalAugment(Stream<DMNShape> drgShapeStream, Id id, Bound ul, RectangleDimensionsSet dimensionsSet, Bound lr, BackgroundSet bgset, Consumer<FontSet> fontSetSetter) {
        Optional<DMNShape> drgShapeOpt = drgShapeStream.filter(shape -> shape.getDmnElementRef().equals(id.getValue())).findFirst();
        if (!drgShapeOpt.isPresent()) {
            return;
        }
        DMNShape drgShape = drgShapeOpt.get();

        ((BoundImpl) ul).setX(drgShape.getBounds().getX());
        ((BoundImpl) ul).setY(drgShape.getBounds().getY());
        dimensionsSet.setWidth(new Width(drgShape.getBounds().getWidth()));
        dimensionsSet.setHeight(new Height(drgShape.getBounds().getHeight()));
        ((BoundImpl) lr).setX(drgShape.getBounds().getX() + drgShape.getBounds().getWidth());
        ((BoundImpl) lr).setY(drgShape.getBounds().getY() + drgShape.getBounds().getHeight());

        if (null != drgShape.getBgColor()) {
            bgset.setBgColour(new BgColour(ColorUtils.wbFromDMN(drgShape.getBgColor())));
        }
        if (null != drgShape.getBorderColor()) {
            bgset.setBorderColour(new BorderColour(ColorUtils.wbFromDMN(drgShape.getBorderColor())));
        }
        if (null != drgShape.getBorderSize()) {
            bgset.setBorderSize(new BorderSize(drgShape.getBorderSize().getValue()));
        }

        if (null != drgShape.getFontStyle()) {
            fontSetSetter.accept(FontSetPropertyConverter.wbFromDMN(drgShape.getFontStyle()));
        }
    }

    private static DMNShape stunnerToDDExt(View<? extends DMNElement> v) {
        DMNShape result = new DMNShape();
        result.setId("dmnshape-" + v.getDefinition().getId().getValue());
        result.setDmnElementRef(v.getDefinition().getId().getValue());
        Bounds bounds = new Bounds();
        result.setBounds(bounds);
        bounds.setX(v.getBounds().getUpperLeft().getX());
        bounds.setY(v.getBounds().getUpperLeft().getY());
        if (v.getDefinition() instanceof Decision) {
            Decision d = (Decision) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            result.setFontStyle(FontSetPropertyConverter.dmnFromWB(d.getFontSet()));
        } else if (v.getDefinition() instanceof InputData) {
            InputData d = (InputData) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            result.setFontStyle(FontSetPropertyConverter.dmnFromWB(d.getFontSet()));
        } else if (v.getDefinition() instanceof BusinessKnowledgeModel) {
            BusinessKnowledgeModel d = (BusinessKnowledgeModel) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            result.setFontStyle(FontSetPropertyConverter.dmnFromWB(d.getFontSet()));
        } else if (v.getDefinition() instanceof KnowledgeSource) {
            KnowledgeSource d = (KnowledgeSource) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            result.setFontStyle(FontSetPropertyConverter.dmnFromWB(d.getFontSet()));
        } else if (v.getDefinition() instanceof TextAnnotation) {
            TextAnnotation d = (TextAnnotation) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            result.setFontStyle(FontSetPropertyConverter.dmnFromWB(d.getFontSet()));
        }

        return result;
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
        if (null != bgset.getBgColour().getValue()) {
            result.setBgColor(ColorUtils.dmnFromWB(bgset.getBgColour().getValue()));
        }
        if (null != bgset.getBorderColour().getValue()) {
            result.setBorderColor(ColorUtils.dmnFromWB(bgset.getBorderColour().getValue()));
        }
        if (null != bgset.getBorderSize().getValue()) {
            result.setBorderSize(new org.kie.workbench.common.dmn.backend.definition.v1_1.dd.BorderSize(bgset.getBorderSize().getValue()));
        }
    }

    private org.kie.dmn.model.v1_1.DRGElement stunnerToDMN(final Node<?, ?> node) {
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
}