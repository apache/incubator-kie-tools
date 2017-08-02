/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.marshalling.server.ServerMarshalling;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.backend.definition.v1_1.AssociationConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.BusinessKnowledgeModelConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.DecisionConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.DefinitionsConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.InputDataConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.KnowledgeSourceConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.TextAnnotationConverter;
import org.kie.workbench.common.stunner.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetImpl;
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

    @Override
    public Graph unmarshall(final Metadata metadata,
                            final InputStream input) throws IOException {
        org.kie.dmn.model.v1_1.Definitions dmnXml = marshaller.unmarshal(new InputStreamReader(input));

        Map<String, Entry<org.kie.dmn.model.v1_1.DRGElement, Node>> elems = dmnXml.getDrgElement().stream().collect(Collectors.toMap(org.kie.dmn.model.v1_1.DRGElement::getId,
                                                                                                                                     dmn -> new SimpleEntry<>(dmn,
                                                                                                                                                              dmnToStunner(dmn))));

        for (Entry<org.kie.dmn.model.v1_1.DRGElement, Node> kv : elems.values()) {
            org.kie.dmn.model.v1_1.DRGElement elem = kv.getKey();
            Node currentNode = kv.getValue();
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

    private void setConnectionMagnets(final Edge edge) {
        Magnet sourceMagnet = MagnetImpl.Builder.build(Magnet.MagnetType.OUTGOING);
        Magnet targetMagnet = MagnetImpl.Builder.build(Magnet.MagnetType.INCOMING);
        ViewConnector connectionContent = (ViewConnector) edge.getContent();
        connectionContent.setSourceMagnet(sourceMagnet);
        connectionContent.setTargetMagnet(targetMagnet);
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

        for (Node<?, ?> node : g.nodes()) {
            if (node.getContent() instanceof View<?>) {
                View<?> view = (View<?>) node.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    DRGElement n = (org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement) view.getDefinition();
                    nodes.put(n.getId().getValue(),
                              stunnerToDMN(node));
                } else if (view.getDefinition() instanceof TextAnnotation) {
                    TextAnnotation textAnnotation = (TextAnnotation) view.getDefinition();
                    textAnnotations.put(textAnnotation.getId().getValue(),
                                        textAnnotationConverter.dmnFromNode((Node<View<TextAnnotation>, ?>) node));

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