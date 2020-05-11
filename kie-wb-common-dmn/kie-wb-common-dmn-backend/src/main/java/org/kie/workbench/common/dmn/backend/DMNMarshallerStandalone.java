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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.dmndi.Bounds;
import org.kie.dmn.model.api.dmndi.Color;
import org.kie.dmn.model.api.dmndi.DMNDecisionServiceDividerLine;
import org.kie.dmn.model.api.dmndi.DMNEdge;
import org.kie.dmn.model.api.dmndi.DMNShape;
import org.kie.dmn.model.api.dmndi.DMNStyle;
import org.kie.dmn.model.api.dmndi.Point;
import org.kie.dmn.model.v1_2.dmndi.DMNDI;
import org.kie.dmn.model.v1_2.dmndi.DiagramElement;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.background.BgColour;
import org.kie.workbench.common.dmn.api.property.background.BorderColour;
import org.kie.workbench.common.dmn.api.property.dimensions.Height;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.Width;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.backend.common.DMNMarshallerImportsHelperStandalone;
import org.kie.workbench.common.dmn.backend.definition.v1_1.AssociationConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.BusinessKnowledgeModelConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.DecisionConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.DecisionServiceConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.DefinitionsConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.InputDataConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.ItemDefinitionPropertyConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.KnowledgeSourceConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.TextAnnotationConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ColorUtils;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentsWidthsExtension;
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
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.PointUtils.heightOfShape;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.PointUtils.lowerRightBound;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.PointUtils.upperLeftBound;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.PointUtils.widthOfShape;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.PointUtils.xOfBound;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.PointUtils.xOfShape;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.PointUtils.yOfBound;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.PointUtils.yOfShape;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

@ApplicationScoped
public class DMNMarshallerStandalone implements DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> {

    public static final String INFO_REQ_ID = getDefinitionId(InformationRequirement.class);

    public static final String KNOWLEDGE_REQ_ID = getDefinitionId(KnowledgeRequirement.class);

    public static final String AUTH_REQ_ID = getDefinitionId(AuthorityRequirement.class);

    public static final String ASSOCIATION_ID = getDefinitionId(Association.class);

    private static final double CENTRE_TOLERANCE = 1.0;

    private XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller;
    private FactoryManager factoryManager;
    private InputDataConverter inputDataConverter;
    private DecisionConverter decisionConverter;
    private BusinessKnowledgeModelConverter bkmConverter;
    private KnowledgeSourceConverter knowledgeSourceConverter;
    private TextAnnotationConverter textAnnotationConverter;
    private DecisionServiceConverter decisionServiceConverter;
    private DMNMarshaller marshaller;
    private DMNMarshallerImportsHelperStandalone dmnMarshallerImportsHelper;

    protected DMNMarshallerStandalone() {
        this(null, null, null, null);
    }

    @Inject
    public DMNMarshallerStandalone(final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller,
                                   final FactoryManager factoryManager,
                                   final DMNMarshallerImportsHelperStandalone dmnMarshallerImportsHelper,
                                   final DMNMarshaller marshaller) {
        this.diagramMetadataMarshaller = diagramMetadataMarshaller;
        this.factoryManager = factoryManager;
        this.dmnMarshallerImportsHelper = dmnMarshallerImportsHelper;
        this.marshaller = marshaller;
        this.inputDataConverter = new InputDataConverter(factoryManager);
        this.decisionConverter = new DecisionConverter(factoryManager);
        this.bkmConverter = new BusinessKnowledgeModelConverter(factoryManager);
        this.knowledgeSourceConverter = new KnowledgeSourceConverter(factoryManager);
        this.textAnnotationConverter = new TextAnnotationConverter(factoryManager);
        this.decisionServiceConverter = new DecisionServiceConverter(factoryManager);
    }

    private static Optional<org.kie.dmn.model.api.dmndi.DMNDiagram> findDMNDiagram(final org.kie.dmn.model.api.Definitions dmnXml) {
        if (!(dmnXml instanceof org.kie.dmn.model.v1_2.TDefinitions)) {
            return Optional.empty();
        }
        if (dmnXml.getDMNDI() == null) {
            return Optional.empty();
        }
        final List<org.kie.dmn.model.api.dmndi.DMNDiagram> elems = dmnXml.getDMNDI().getDMNDiagram();
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
        final Map<String, HasComponentWidths> hasComponentWidthsMap = new HashMap<>();
        final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer = (uuid, hcw) -> {
            if (Objects.nonNull(uuid)) {
                hasComponentWidthsMap.put(uuid, hcw);
            }
        };

        final org.kie.dmn.model.api.Definitions dmnXml = marshaller.unmarshal(new InputStreamReader(input));
        final List<org.kie.dmn.model.api.DRGElement> diagramDrgElements = dmnXml.getDrgElement();
        final Optional<org.kie.dmn.model.api.dmndi.DMNDiagram> dmnDDDiagram = findDMNDiagram(dmnXml);

        // Get external DMN model information
        final Map<Import, org.kie.dmn.model.api.Definitions> importDefinitions = dmnMarshallerImportsHelper.getImportDefinitions(metadata, dmnXml.getImport());

        // Get external PMML model information
        final Map<Import, PMMLDocumentMetadata> pmmlDocuments = dmnMarshallerImportsHelper.getPMMLDocuments(metadata, dmnXml.getImport());

        // Map external DRGElements
        final List<DMNShape> dmnShapes = dmnDDDiagram.map(this::getUniqueDMNShapes).orElse(emptyList());
        final List<org.kie.dmn.model.api.DRGElement> importedDrgElements = getImportedDrgElementsByShape(dmnShapes, importDefinitions);

        // Group DRGElements
        final List<org.kie.dmn.model.api.DRGElement> drgElements = new ArrayList<>();
        drgElements.addAll(diagramDrgElements);
        drgElements.addAll(importedDrgElements);

        // Remove DRGElements that doesn't have any local or imported shape.
        removeDrgElementsWithoutShape(drgElements, dmnShapes);

        final Map<String, Entry<org.kie.dmn.model.api.DRGElement, Node>> elems = drgElements.stream().collect(toMap(org.kie.dmn.model.api.DRGElement::getId,
                                                                                                                    dmn -> new SimpleEntry<>(dmn,
                                                                                                                                             dmnToStunner(dmn, hasComponentWidthsConsumer, importedDrgElements))));

        final Set<org.kie.dmn.model.api.DecisionService> dmnDecisionServices = new HashSet<>();

        // Stunner rely on relative positioning for Edge connections, so need to cycle on DMNShape first.
        for (Entry<org.kie.dmn.model.api.DRGElement, Node> kv : elems.values()) {
            ddExtAugmentStunner(dmnDDDiagram, kv.getValue());
        }

        // Setup Node Relationships and Connections all based on absolute positioning
        for (Entry<org.kie.dmn.model.api.DRGElement, Node> kv : elems.values()) {
            final org.kie.dmn.model.api.DRGElement elem = kv.getKey();
            final Node currentNode = kv.getValue();

            // For imported nodes, we don't have its connections
            if (isImportedDRGElement(importedDrgElements, elem)) {
                continue;
            }

            // DMN spec table 2: Requirements connection rules
            if (elem instanceof org.kie.dmn.model.api.Decision) {
                final org.kie.dmn.model.api.Decision decision = (org.kie.dmn.model.api.Decision) elem;
                for (org.kie.dmn.model.api.InformationRequirement ir : decision.getInformationRequirement()) {
                    connectEdgeToNodes(INFO_REQ_ID,
                                       ir,
                                       ir.getRequiredInput(),
                                       elems,
                                       dmnXml,
                                       currentNode);
                    connectEdgeToNodes(INFO_REQ_ID,
                                       ir,
                                       ir.getRequiredDecision(),
                                       elems,
                                       dmnXml,
                                       currentNode);
                }
                for (org.kie.dmn.model.api.KnowledgeRequirement kr : decision.getKnowledgeRequirement()) {
                    connectEdgeToNodes(KNOWLEDGE_REQ_ID,
                                       kr,
                                       kr.getRequiredKnowledge(),
                                       elems,
                                       dmnXml,
                                       currentNode);
                }
                for (org.kie.dmn.model.api.AuthorityRequirement ar : decision.getAuthorityRequirement()) {
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredAuthority(),
                                       elems,
                                       dmnXml,
                                       currentNode);
                }
            } else if (elem instanceof org.kie.dmn.model.api.BusinessKnowledgeModel) {
                final org.kie.dmn.model.api.BusinessKnowledgeModel bkm = (org.kie.dmn.model.api.BusinessKnowledgeModel) elem;
                for (org.kie.dmn.model.api.KnowledgeRequirement kr : bkm.getKnowledgeRequirement()) {
                    connectEdgeToNodes(KNOWLEDGE_REQ_ID,
                                       kr,
                                       kr.getRequiredKnowledge(),
                                       elems,
                                       dmnXml,
                                       currentNode);
                }
                for (org.kie.dmn.model.api.AuthorityRequirement ar : bkm.getAuthorityRequirement()) {
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredAuthority(),
                                       elems,
                                       dmnXml,
                                       currentNode);
                }
            } else if (elem instanceof org.kie.dmn.model.api.KnowledgeSource) {
                final org.kie.dmn.model.api.KnowledgeSource ks = (org.kie.dmn.model.api.KnowledgeSource) elem;
                for (org.kie.dmn.model.api.AuthorityRequirement ar : ks.getAuthorityRequirement()) {
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredInput(),
                                       elems,
                                       dmnXml,
                                       currentNode);
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredDecision(),
                                       elems,
                                       dmnXml,
                                       currentNode);
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredAuthority(),
                                       elems,
                                       dmnXml,
                                       currentNode);
                }
            } else if (elem instanceof org.kie.dmn.model.api.DecisionService) {
                final org.kie.dmn.model.api.DecisionService ds = (org.kie.dmn.model.api.DecisionService) elem;
                dmnDecisionServices.add(ds);
                for (org.kie.dmn.model.api.DMNElementReference er : ds.getEncapsulatedDecision()) {
                    final String reqInputID = getId(er);
                    final Node requiredNode = getRequiredNode(elems, reqInputID);
                    if (Objects.nonNull(requiredNode)) {
                        connectDSChildEdge(currentNode, requiredNode);
                    }
                }
                for (org.kie.dmn.model.api.DMNElementReference er : ds.getOutputDecision()) {
                    final String reqInputID = getId(er);
                    final Node requiredNode = getRequiredNode(elems, reqInputID);
                    if (Objects.nonNull(requiredNode)) {
                        connectDSChildEdge(currentNode, requiredNode);
                    }
                }
            }
        }

        final Map<String, Node<View<TextAnnotation>, ?>> textAnnotations = dmnXml.getArtifact().stream()
                .filter(org.kie.dmn.model.api.TextAnnotation.class::isInstance)
                .map(org.kie.dmn.model.api.TextAnnotation.class::cast)
                .collect(Collectors.toMap(org.kie.dmn.model.api.TextAnnotation::getId,
                                          dmn -> textAnnotationConverter.nodeFromDMN(dmn,
                                                                                     hasComponentWidthsConsumer)));
        textAnnotations.values().forEach(n -> ddExtAugmentStunner(dmnDDDiagram, n));

        final List<org.kie.dmn.model.api.Association> associations = dmnXml.getArtifact().stream()
                .filter(org.kie.dmn.model.api.Association.class::isInstance)
                .map(org.kie.dmn.model.api.Association.class::cast)
                .collect(Collectors.toList());
        for (org.kie.dmn.model.api.Association a : associations) {
            final String sourceId = getId(a.getSourceRef());
            final Node sourceNode = Optional.ofNullable(elems.get(sourceId)).map(Entry::getValue).orElse(textAnnotations.get(sourceId));

            final String targetId = getId(a.getTargetRef());
            final Node targetNode = Optional.ofNullable(elems.get(targetId)).map(Entry::getValue).orElse(textAnnotations.get(targetId));

            @SuppressWarnings("unchecked")
            final Edge<View<Association>, ?> myEdge = (Edge<View<Association>, ?>) factoryManager.newElement(idOfDMNorWBUUID(a),
                                                                                                             ASSOCIATION_ID).asEdge();

            final Id id = new Id(a.getId());
            final Description description = new Description(a.getDescription());
            final Association definition = new Association(id,
                                                           description);
            myEdge.getContent().setDefinition(definition);

            connectEdge(myEdge,
                        sourceNode,
                        targetNode);
            setConnectionMagnets(myEdge, a.getId(), dmnXml);
        }

        //Ensure all locations are updated to relative for Stunner
        for (Entry<org.kie.dmn.model.api.DRGElement, Node> kv : elems.values()) {
            PointUtils.convertToRelativeBounds(kv.getValue());
        }

        final Graph graph = factoryManager.newDiagram("prova",
                                                      BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class),
                                                      metadata).getGraph();
        elems.values().stream().map(Map.Entry::getValue).forEach(graph::addNode);
        textAnnotations.values().forEach(graph::addNode);

        final Node<?, ?> dmnDiagramRoot = findDMNDiagramRoot(graph);
        final Definitions definitionsStunnerPojo = DefinitionsConverter.wbFromDMN(dmnXml, importDefinitions, pmmlDocuments);
        loadImportedItemDefinitions(definitionsStunnerPojo, importDefinitions);
        ((View<DMNDiagram>) dmnDiagramRoot.getContent()).getDefinition().setDefinitions(definitionsStunnerPojo);

        //Only connect Nodes to the Diagram that are not referenced by DecisionServices
        final List<String> references = new ArrayList<>();
        dmnDecisionServices.forEach(ds -> references.addAll(ds.getEncapsulatedDecision().stream().map(org.kie.dmn.model.api.DMNElementReference::getHref).collect(Collectors.toList())));
        dmnDecisionServices.forEach(ds -> references.addAll(ds.getOutputDecision().stream().map(org.kie.dmn.model.api.DMNElementReference::getHref).collect(Collectors.toList())));

        final Map<org.kie.dmn.model.api.DRGElement, Node> elemsToConnectToRoot = elems.values().stream()
                .filter(elem -> !references.contains("#" + elem.getKey().getId()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        elemsToConnectToRoot.values().stream()
                .forEach(node -> connectRootWithChild(dmnDiagramRoot,
                                                      node));

        textAnnotations.values().stream().forEach(node -> connectRootWithChild(dmnDiagramRoot,
                                                                               node));

        //Copy ComponentWidths information
        final Optional<ComponentsWidthsExtension> extension = findComponentsWidthsExtension(dmnDDDiagram);
        extension.ifPresent(componentsWidthsExtension -> {
            //This condition is required because a node with ComponentsWidthsExtension
            //can be imported from another diagram but the extension is not imported or present in this diagram.
            if (componentsWidthsExtension.getComponentsWidths() != null) {
                hasComponentWidthsMap.forEach((uuid, hasComponentWidths) -> componentsWidthsExtension
                        .getComponentsWidths()
                        .stream()
                        .filter(componentWidths -> componentWidths.getDmnElementRef().getLocalPart().equals(uuid))
                        .findFirst()
                        .ifPresent(componentWidths -> {
                            final List<Double> widths = hasComponentWidths.getComponentWidths();
                            widths.clear();
                            widths.addAll(componentWidths.getWidths());
                        }));
            }
        });

        return graph;
    }

    void removeDrgElementsWithoutShape(final List<org.kie.dmn.model.api.DRGElement> drgElements,
                                       final List<DMNShape> dmnShapes) {

        // DMN 1.1 doesn't have DMNShape, so we include all DRGElements and create all the shapes.
        if (dmnShapes.isEmpty()) {
            return;
        }

        drgElements.removeIf(element -> dmnShapes.stream().noneMatch(s -> s.getDmnElementRef().getLocalPart().endsWith(element.getId())));
    }

    private Node getRequiredNode(final Map<String, Entry<org.kie.dmn.model.api.DRGElement, Node>> elems,
                                 final String reqInputID) {
        if (elems.containsKey(reqInputID)) {
            return elems.get(reqInputID).getValue();
        } else {

            final Optional<String> match = elems.keySet().stream()
                    .filter(k -> k.contains(reqInputID))
                    .findFirst();
            if (match.isPresent()) {
                return elems.get(match.get()).getValue();
            }
        }

        return null;
    }

    List<org.kie.dmn.model.api.DRGElement> getImportedDrgElementsByShape(final List<DMNShape> dmnShapes,
                                                                         final Map<Import, org.kie.dmn.model.api.Definitions> importDefinitions) {

        final List<org.kie.dmn.model.api.DRGElement> importedDRGElements = dmnMarshallerImportsHelper.getImportedDRGElements(importDefinitions);

        return dmnShapes
                .stream()
                .map(shape -> {
                    final String dmnElementRef = getDmnElementRef(shape);
                    final Optional<org.kie.dmn.model.api.DRGElement> ref = getReference(importedDRGElements, dmnElementRef);
                    return ref.orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    Optional<org.kie.dmn.model.api.DRGElement> getReference(final List<org.kie.dmn.model.api.DRGElement> importedDRGElements,
                                                            final String dmnElementRef) {
        return importedDRGElements.stream().filter(drgElement -> dmnElementRef.endsWith(drgElement.getId())).findFirst();
    }

    String getDmnElementRef(final DMNShape dmnShape) {
        return Optional
                .ofNullable(dmnShape.getDmnElementRef())
                .map(QName::getLocalPart)
                .orElse("");
    }

    List<DMNShape> getUniqueDMNShapes(final org.kie.dmn.model.api.dmndi.DMNDiagram dmnDDDiagram) {
        return new ArrayList<>(dmnDDDiagram
                                       .getDMNDiagramElement()
                                       .stream()
                                       .filter(diagramElements -> diagramElements instanceof DMNShape)
                                       .map(d -> (DMNShape) d)
                                       .collect(toMap(DMNShape::getId, shape -> shape, (shape1, shape2) -> shape1))
                                       .values());
    }

    /**
     * Stunner's factoryManager is only used to create Nodes that are considered part of a "Definition Set" (a collection of nodes visible to the User e.g. BPMN2 StartNode, EndNode and DMN's DecisionNode etc).
     * Relationships are not created with the factory.
     * This method specializes to connect with an Edge containing a Child relationship the target Node.
     */
    private static void connectDSChildEdge(final Node dsNode,
                                           final Node requiredNode) {
        final String uuid = dsNode.getUUID() + "er" + requiredNode.getUUID();
        final Edge<Child, Node> myEdge = new EdgeImpl<>(uuid);
        myEdge.setContent(new Child());
        connectEdge(myEdge,
                    dsNode,
                    requiredNode);
    }

    private static String idOfDMNorWBUUID(final org.kie.dmn.model.api.DMNElement dmn) {
        return dmn.getId() != null ? dmn.getId() : UUID.uuid();
    }

    public static Node<?, ?> findDMNDiagramRoot(final Graph<?, Node<View, ?>> graph) {
        return StreamSupport.stream(graph.nodes().spliterator(),
                                    false).filter(n -> DefinitionUtils.getElementDefinition(n) instanceof DMNDiagram).findFirst().orElseThrow(() -> new UnsupportedOperationException("TODO"));
    }

    private String getId(final org.kie.dmn.model.api.DMNElementReference er) {
        final String href = er.getHref();
        return href.contains("#") ? href.substring(href.indexOf('#') + 1) : href;
    }

    private void connectEdgeToNodes(final String connectorTypeId,
                                    final org.kie.dmn.model.api.DMNElement dmnElement,
                                    final org.kie.dmn.model.api.DMNElementReference dmnElementReference,
                                    final Map<String, Entry<org.kie.dmn.model.api.DRGElement, Node>> elems,
                                    final org.kie.dmn.model.api.Definitions definitions,
                                    final Node currentNode) {
        if (Objects.nonNull(dmnElementReference)) {
            final String reqInputID = getId(dmnElementReference);
            final Node requiredNode = getRequiredNode(elems, reqInputID);
            final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(dmnElement),
                                                          connectorTypeId).asEdge();
            connectEdge(myEdge,
                        requiredNode,
                        currentNode);
            setConnectionMagnets(myEdge, dmnElement.getId(), definitions);
        }
    }

    private Node dmnToStunner(final org.kie.dmn.model.api.DRGElement dmn,
                              final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer,
                              final List<org.kie.dmn.model.api.DRGElement> importedDrgElements) {
        final Node node = createNode(dmn, hasComponentWidthsConsumer);
        return setAllowOnlyVisualChange(importedDrgElements, node);
    }

    private Node createNode(final org.kie.dmn.model.api.DRGElement dmn,
                            final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (dmn instanceof org.kie.dmn.model.api.InputData) {
            return inputDataConverter.nodeFromDMN((org.kie.dmn.model.api.InputData) dmn,
                                                  hasComponentWidthsConsumer);
        } else if (dmn instanceof org.kie.dmn.model.api.Decision) {
            return decisionConverter.nodeFromDMN((org.kie.dmn.model.api.Decision) dmn,
                                                 hasComponentWidthsConsumer);
        } else if (dmn instanceof org.kie.dmn.model.api.BusinessKnowledgeModel) {
            return bkmConverter.nodeFromDMN((org.kie.dmn.model.api.BusinessKnowledgeModel) dmn,
                                            hasComponentWidthsConsumer);
        } else if (dmn instanceof org.kie.dmn.model.api.KnowledgeSource) {
            return knowledgeSourceConverter.nodeFromDMN((org.kie.dmn.model.api.KnowledgeSource) dmn,
                                                        hasComponentWidthsConsumer);
        } else if (dmn instanceof org.kie.dmn.model.api.DecisionService) {
            return decisionServiceConverter.nodeFromDMN((org.kie.dmn.model.api.DecisionService) dmn,
                                                        hasComponentWidthsConsumer);
        } else {
            throw new UnsupportedOperationException("Unsupported node type detected.");
        }
    }

    Node setAllowOnlyVisualChange(final List<org.kie.dmn.model.api.DRGElement> importedDrgElements,
                                  final Node node) {
        getDRGElement(node).ifPresent(drgElement -> drgElement.setAllowOnlyVisualChange(isImportedDRGElement(importedDrgElements, drgElement)));
        return node;
    }

    Optional<DRGElement> getDRGElement(final Node node) {

        final Object objectDefinition = DefinitionUtils.getElementDefinition(node);

        if (objectDefinition instanceof DRGElement) {
            return Optional.of((DRGElement) objectDefinition);
        }

        return Optional.empty();
    }

    boolean isImportedDRGElement(final List<org.kie.dmn.model.api.DRGElement> importedDrgElements,
                                 final org.kie.dmn.model.api.DRGElement drgElement) {
        return isImportedIdNode(importedDrgElements, drgElement.getId());
    }

    boolean isImportedDRGElement(final List<org.kie.dmn.model.api.DRGElement> importedDrgElements,
                                 final DRGElement drgElement) {
        return isImportedIdNode(importedDrgElements, drgElement.getId().getValue());
    }

    private boolean isImportedIdNode(final List<org.kie.dmn.model.api.DRGElement> importedDrgElements,
                                     final String id) {
        return importedDrgElements
                .stream()
                .anyMatch(drgElement -> Objects.equals(drgElement.getId(), id));
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
        final Definitions definitions = ((DMNDiagram) ((View) dmnDiagramRoot.getContent()).getDefinition()).getDefinitions();
        final DMNModelInstrumentedBase childDRG = (DMNModelInstrumentedBase) ((View) child.getContent()).getDefinition();
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
    private void setConnectionMagnets(final Edge edge,
                                      final String dmnEdgeElementRef,
                                      final org.kie.dmn.model.api.Definitions dmnXml) {
        final ViewConnector connectionContent = (ViewConnector) edge.getContent();

        final Optional<org.kie.dmn.model.api.dmndi.DMNDiagram> dmnDiagram = findDMNDiagram(dmnXml);
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
            final Point source = e.getWaypoint().get(0);
            final Node<View<?>, Edge> sourceNode = edge.getSourceNode();
            if (null != sourceNode) {
                setConnectionMagnet(sourceNode,
                                    source,
                                    connectionContent::setSourceConnection);
            }
            final Point target = e.getWaypoint().get(e.getWaypoint().size() - 1);
            final Node<View<?>, Edge> targetNode = edge.getTargetNode();
            if (null != targetNode) {
                setConnectionMagnet(targetNode,
                                    target,
                                    connectionContent::setTargetConnection);
            }
            if (e.getWaypoint().size() > 2) {
                connectionContent.setControlPoints(e.getWaypoint()
                                                           .subList(1, e.getWaypoint().size() - 1)
                                                           .stream()
                                                           .map(p -> ControlPoint.build(PointUtils.dmndiPointToPoint2D(p)))
                                                           .toArray(ControlPoint[]::new));
            }
        } else {
            // Set the source connection, if any.
            final Node sourceNode = edge.getSourceNode();
            if (null != sourceNode) {
                connectionContent.setSourceConnection(MagnetConnection.Builder.atCenter(sourceNode));
            }
            // Set the target connection, if any.
            final Node targetNode = edge.getTargetNode();
            if (null != targetNode) {
                connectionContent.setTargetConnection(MagnetConnection.Builder.atCenter(targetNode));
            }
        }
    }

    private void setConnectionMagnet(final Node<View<?>, Edge> node,
                                     final Point magnetPoint,
                                     final Consumer<Connection> connectionConsumer) {
        final View<?> view = node.getContent();
        final double viewX = xOfBound(upperLeftBound(view));
        final double viewY = yOfBound(upperLeftBound(view));
        final double magnetRelativeX = magnetPoint.getX() - viewX;
        final double magnetRelativeY = magnetPoint.getY() - viewY;
        final double viewWidth = view.getBounds().getWidth();
        final double viewHeight = view.getBounds().getHeight();
        if (isCentre(magnetRelativeX,
                     magnetRelativeY,
                     viewWidth,
                     viewHeight)) {
            connectionConsumer.accept(MagnetConnection.Builder.atCenter(node));
        } else {
            connectionConsumer.accept(MagnetConnection.Builder.at(magnetRelativeX, magnetRelativeY).setAuto(true));
        }
    }

    private boolean isCentre(final double magnetRelativeX,
                             final double magnetRelativeY,
                             final double viewWidth,
                             final double viewHeight) {
        return Math.abs((viewWidth / 2) - magnetRelativeX) < CENTRE_TOLERANCE &&
                Math.abs((viewHeight / 2) - magnetRelativeY) < CENTRE_TOLERANCE;
    }

    private Optional<ComponentsWidthsExtension> findComponentsWidthsExtension(final Optional<org.kie.dmn.model.api.dmndi.DMNDiagram> dmnDDDiagram) {
        if (!dmnDDDiagram.isPresent()) {
            return Optional.empty();
        }
        final org.kie.dmn.model.api.dmndi.DiagramElement.Extension dmnDDExtensions = dmnDDDiagram.get().getExtension();

        if (Objects.isNull(dmnDDExtensions)) {
            return Optional.empty();
        }
        final List<Object> extensions = dmnDDExtensions.getAny();
        if (Objects.isNull(extensions)) {
            return Optional.empty();
        }
        return extensions
                .stream()
                .filter(extension -> extension instanceof ComponentsWidthsExtension)
                .map(extension -> (ComponentsWidthsExtension) extension)
                .findFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String marshall(final Diagram<Graph, Metadata> diagram) {
        final Graph<?, Node<View, ?>> g = diagram.getGraph();

        final Map<String, org.kie.dmn.model.api.DRGElement> nodes = new HashMap<>();
        final Map<String, org.kie.dmn.model.api.TextAnnotation> textAnnotations = new HashMap<>();

        final Node<View<DMNDiagram>, ?> dmnDiagramRoot = (Node<View<DMNDiagram>, ?>) findDMNDiagramRoot(g);
        final Definitions definitionsStunnerPojo = ((DMNDiagram) DefinitionUtils.getElementDefinition(dmnDiagramRoot)).getDefinitions();
        cleanImportedItemDefinitions(definitionsStunnerPojo);
        final org.kie.dmn.model.api.Definitions definitions = DefinitionsConverter.dmnFromWB(definitionsStunnerPojo);
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
        final org.kie.dmn.model.api.dmndi.DMNDiagram dmnDDDMNDiagram = new org.kie.dmn.model.v1_2.dmndi.DMNDiagram();
        definitions.getDMNDI().getDMNDiagram().add(dmnDDDMNDiagram);
        final List<DMNEdge> dmnEdges = new ArrayList<>();

        //Convert relative positioning to absolute
        for (Node<?, ?> node : g.nodes()) {
            PointUtils.convertToAbsoluteBounds(node);
        }

        //Setup callback for marshalling ComponentWidths
        if (dmnDDDMNDiagram.getExtension() == null) {
            dmnDDDMNDiagram.setExtension(new DiagramElement.Extension());
        }
        final ComponentsWidthsExtension componentsWidthsExtension = new ComponentsWidthsExtension();
        dmnDDDMNDiagram.getExtension().getAny().add(componentsWidthsExtension);

        final Consumer<ComponentWidths> componentWidthsConsumer = cw -> componentsWidthsExtension.getComponentsWidths().add(cw);

        //Iterate Graph processing nodes..
        for (Node<?, ?> node : g.nodes()) {
            if (node.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) node.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (org.kie.workbench.common.dmn.api.definition.model.DRGElement) view.getDefinition();
                    if (!drgElement.isAllowOnlyVisualChange()) {
                        nodes.put(drgElement.getId().getValue(),
                                  stunnerToDMN(node,
                                               componentWidthsConsumer));
                    }
                    dmnDDDMNDiagram.getDMNDiagramElement().add(stunnerToDDExt(definitionsStunnerPojo, (View<? extends DMNElement>) view));
                } else if (view.getDefinition() instanceof TextAnnotation) {
                    final TextAnnotation textAnnotation = (TextAnnotation) view.getDefinition();
                    textAnnotations.put(textAnnotation.getId().getValue(),
                                        textAnnotationConverter.dmnFromNode((Node<View<TextAnnotation>, ?>) node,
                                                                            componentWidthsConsumer));
                    dmnDDDMNDiagram.getDMNDiagramElement().add(stunnerToDDExt(definitionsStunnerPojo, (View<? extends DMNElement>) view));

                    final List<org.kie.dmn.model.api.Association> associations = AssociationConverter.dmnFromWB((Node<View<TextAnnotation>, ?>) node);
                    definitions.getArtifact().addAll(associations);
                }
                // DMNDI Edge management.
                final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
                for (Edge<?, ?> e : inEdges) {
                    if (e.getContent() instanceof ViewConnector) {
                        final ViewConnector connectionContent = (ViewConnector) e.getContent();
                        if (connectionContent.getSourceConnection().isPresent() && connectionContent.getTargetConnection().isPresent()) {
                            Point2D sourcePoint = ((Connection) connectionContent.getSourceConnection().get()).getLocation();
                            Point2D targetPoint = ((Connection) connectionContent.getTargetConnection().get()).getLocation();
                            final Node<?, ?> sourceNode = e.getSourceNode();
                            final View<?> sourceView = (View<?>) sourceNode.getContent();
                            double xSource = xOfBound(upperLeftBound(sourceView));
                            double ySource = yOfBound(upperLeftBound(sourceView));
                            double xTarget = xOfBound(upperLeftBound(view));
                            double yTarget = yOfBound(upperLeftBound(view));
                            if (sourcePoint == null) {
                                // If the "connection source/target location is null" assume it's the centre of the shape.
                                if (sourceView.getDefinition() instanceof DMNViewDefinition) {
                                    DMNViewDefinition dmnViewDefinition = (DMNViewDefinition) sourceView.getDefinition();
                                    xSource += dmnViewDefinition.getDimensionsSet().getWidth().getValue() / 2;
                                    ySource += dmnViewDefinition.getDimensionsSet().getHeight().getValue() / 2;
                                }
                                sourcePoint = Point2D.create(xSource, ySource);
                            } else {
                                // If it is non-null it is relative to the source/target shape location.
                                sourcePoint = Point2D.create(xSource + sourcePoint.getX(), ySource + sourcePoint.getY());
                            }
                            if (targetPoint == null) {
                                // If the "connection source/target location is null" assume it's the centre of the shape.
                                if (view.getDefinition() instanceof DMNViewDefinition) {
                                    DMNViewDefinition dmnViewDefinition = (DMNViewDefinition) view.getDefinition();
                                    xTarget += dmnViewDefinition.getDimensionsSet().getWidth().getValue() / 2;
                                    yTarget += dmnViewDefinition.getDimensionsSet().getHeight().getValue() / 2;
                                }
                                targetPoint = Point2D.create(xTarget, yTarget);
                            } else {
                                // If it is non-null it is relative to the source/target shape location.
                                targetPoint = Point2D.create(xTarget + targetPoint.getX(), yTarget + targetPoint.getY());
                            }

                            final DMNEdge dmnEdge = new org.kie.dmn.model.v1_2.dmndi.DMNEdge();
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

    void loadImportedItemDefinitions(final Definitions definitions,
                                     final Map<Import, org.kie.dmn.model.api.Definitions> importDefinitions) {
        definitions.getItemDefinition().addAll(getWbImportedItemDefinitions(importDefinitions));
    }

    void cleanImportedItemDefinitions(final Definitions definitions) {
        definitions.getItemDefinition().removeIf(ItemDefinition::isAllowOnlyVisualChange);
    }

    List<org.kie.workbench.common.dmn.api.definition.model.ItemDefinition> getWbImportedItemDefinitions(final Map<Import, org.kie.dmn.model.api.Definitions> importDefinitions) {
        return dmnMarshallerImportsHelper
                .getImportedItemDefinitions(importDefinitions)
                .stream()
                .map(ItemDefinitionPropertyConverter::wbFromDMN)
                .peek(itemDefinition -> itemDefinition.setAllowOnlyVisualChange(true))
                .collect(Collectors.toList());
    }

    private void ddExtAugmentStunner(final Optional<org.kie.dmn.model.api.dmndi.DMNDiagram> dmnDDDiagram, Node currentNode) {
        if (!dmnDDDiagram.isPresent()) {
            return;
        }

        final Stream<DMNShape> drgShapeStream = dmnDDDiagram.get().getDMNDiagramElement().stream().filter(DMNShape.class::isInstance).map(DMNShape.class::cast);
        final View content = (View) currentNode.getContent();
        final Bound ulBound = upperLeftBound(content);
        final Bound lrBound = lowerRightBound(content);
        if (content.getDefinition() instanceof Decision) {
            final Decision d = (Decision) content.getDefinition();
            internalAugment(drgShapeStream,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof InputData) {
            final InputData d = (InputData) content.getDefinition();
            internalAugment(drgShapeStream,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof BusinessKnowledgeModel) {
            final BusinessKnowledgeModel d = (BusinessKnowledgeModel) content.getDefinition();
            internalAugment(drgShapeStream,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof KnowledgeSource) {
            final KnowledgeSource d = (KnowledgeSource) content.getDefinition();
            internalAugment(drgShapeStream,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof TextAnnotation) {
            final TextAnnotation d = (TextAnnotation) content.getDefinition();
            internalAugment(drgShapeStream,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof DecisionService) {
            final DecisionService d = (DecisionService) content.getDefinition();
            internalAugment(drgShapeStream,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet,
                            dividerLineY -> d.setDividerLineY(new DecisionServiceDividerLineY(dividerLineY - ulBound.getY())));
        }
    }

    @SuppressWarnings("unchecked")
    private void internalAugment(final Stream<DMNShape> drgShapeStream,
                                 final Id id,
                                 final Bound ulBound,
                                 final RectangleDimensionsSet dimensionsSet,
                                 final Bound lrBound,
                                 final BackgroundSet bgset,
                                 final Consumer<FontSet> fontSetSetter) {
        internalAugment(drgShapeStream,
                        id,
                        ulBound,
                        dimensionsSet,
                        lrBound,
                        bgset,
                        fontSetSetter,
                        line -> {/*NOP*/});
    }

    @SuppressWarnings("unchecked")
    private void internalAugment(final Stream<DMNShape> drgShapeStream,
                                 final Id id,
                                 final Bound ulBound,
                                 final RectangleDimensionsSet dimensionsSet,
                                 final Bound lrBound,
                                 final BackgroundSet bgset,
                                 final Consumer<FontSet> fontSetSetter,
                                 final DoubleConsumer decisionServiceDividerLineYSetter) {
        final Optional<DMNShape> drgShapeOpt = drgShapeStream.filter(shape -> shape.getDmnElementRef().getLocalPart().endsWith(id.getValue())).findFirst();
        if (!drgShapeOpt.isPresent()) {
            return;
        }
        final DMNShape drgShape = drgShapeOpt.get();

        if (ulBound != null) {
            ulBound.setX(xOfShape(drgShape));
            ulBound.setY(yOfShape(drgShape));
        }
        dimensionsSet.setWidth(new Width(widthOfShape(drgShape)));
        dimensionsSet.setHeight(new Height(heightOfShape(drgShape)));
        if (lrBound != null) {
            lrBound.setX(xOfShape(drgShape) + widthOfShape(drgShape));
            lrBound.setY(yOfShape(drgShape) + heightOfShape(drgShape));
        }

        final DMNStyle dmnStyleOfDrgShape = drgShape.getStyle() instanceof DMNStyle ? (DMNStyle) drgShape.getStyle() : null;
        if (dmnStyleOfDrgShape != null) {
            if (null != dmnStyleOfDrgShape.getFillColor()) {
                bgset.setBgColour(new BgColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getFillColor())));
            }
            if (null != dmnStyleOfDrgShape.getStrokeColor()) {
                bgset.setBorderColour(new BorderColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getStrokeColor())));
            }
        }

        final FontSet fontSet = new FontSet();
        if (dmnStyleOfDrgShape != null) {
            mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN(dmnStyleOfDrgShape));
        }
        if (drgShape.getDMNLabel() != null && drgShape.getDMNLabel().getSharedStyle() instanceof DMNStyle) {
            mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN((DMNStyle) drgShape.getDMNLabel().getSharedStyle()));
        }
        if (drgShape.getDMNLabel() != null && drgShape.getDMNLabel().getStyle() instanceof DMNStyle) {
            mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN((DMNStyle) drgShape.getDMNLabel().getStyle()));
        }
        fontSetSetter.accept(fontSet);

        if (drgShape.getDMNDecisionServiceDividerLine() != null) {
            decisionServiceDividerLineYSetter.accept(drgShape.getDMNDecisionServiceDividerLine().getWaypoint().get(0).getY());
        }
    }

    private static void mergeFontSet(final FontSet fontSet,
                                     final FontSet additional) {
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

    @SuppressWarnings("unchecked")
    private static DMNShape stunnerToDDExt(final Definitions definitions,
                                           final View<? extends DMNElement> v) {

        final DMNShape result = new org.kie.dmn.model.v1_2.dmndi.DMNShape();
        result.setId("dmnshape-" + v.getDefinition().getId().getValue());
        result.setDmnElementRef(getDmnElementRef(definitions, v));
        final Bounds bounds = new org.kie.dmn.model.v1_2.dmndi.Bounds();
        result.setBounds(bounds);
        bounds.setX(xOfBound(upperLeftBound(v)));
        bounds.setY(yOfBound(upperLeftBound(v)));
        result.setStyle(new org.kie.dmn.model.v1_2.dmndi.DMNStyle());
        result.setDMNLabel(new org.kie.dmn.model.v1_2.dmndi.DMNLabel());
        if (v.getDefinition() instanceof Decision) {
            final Decision d = (Decision) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            applyFontStyle(d.getFontSet(), result);
        } else if (v.getDefinition() instanceof InputData) {
            final InputData d = (InputData) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            applyFontStyle(d.getFontSet(), result);
        } else if (v.getDefinition() instanceof BusinessKnowledgeModel) {
            final BusinessKnowledgeModel d = (BusinessKnowledgeModel) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            applyFontStyle(d.getFontSet(), result);
        } else if (v.getDefinition() instanceof KnowledgeSource) {
            final KnowledgeSource d = (KnowledgeSource) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            applyFontStyle(d.getFontSet(), result);
        } else if (v.getDefinition() instanceof TextAnnotation) {
            final TextAnnotation d = (TextAnnotation) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            applyFontStyle(d.getFontSet(), result);
        } else if (v.getDefinition() instanceof DecisionService) {
            final DecisionService d = (DecisionService) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), result);
            applyFontStyle(d.getFontSet(), result);
            final DMNDecisionServiceDividerLine dl = new org.kie.dmn.model.v1_2.dmndi.DMNDecisionServiceDividerLine();
            final org.kie.dmn.model.api.dmndi.Point leftPoint = new org.kie.dmn.model.v1_2.dmndi.Point();
            leftPoint.setX(v.getBounds().getUpperLeft().getX());
            final double dlY = v.getBounds().getUpperLeft().getY() + d.getDividerLineY().getValue();
            leftPoint.setY(dlY);
            dl.getWaypoint().add(leftPoint);
            final org.kie.dmn.model.api.dmndi.Point rightPoint = new org.kie.dmn.model.v1_2.dmndi.Point();
            rightPoint.setX(v.getBounds().getLowerRight().getX());
            rightPoint.setY(dlY);
            dl.getWaypoint().add(rightPoint);
            result.setDMNDecisionServiceDividerLine(dl);
        }
        return result;
    }

    static QName getDmnElementRef(final Definitions definitions,
                                  final View<? extends DMNElement> v) {

        final DMNElement dmnElement = v.getDefinition();
        final String dmnElementId = dmnElement.getId().getValue();

        return getImportPrefix(definitions, dmnElement)
                .map(prefix -> new QName(prefix + ":" + dmnElementId))
                .orElse(new QName(dmnElementId));
    }

    private static Optional<String> getImportPrefix(final Definitions definitions,
                                                    final DMNElement dmnElement) {

        if (!(dmnElement instanceof NamedElement)) {
            return Optional.empty();
        }

        final NamedElement namedElement = (NamedElement) dmnElement;
        final Optional<String> name = Optional.ofNullable(namedElement.getName().getValue());

        return definitions
                .getImport()
                .stream()
                .filter(anImport -> {
                    final String importName = anImport.getName().getValue();
                    return name.map(n -> n.startsWith(importName + ".")).orElse(false);
                })
                .map(anImport -> {
                    final String importNamespace = anImport.getNamespace();
                    return getNsContextsByNamespace(definitions, importNamespace);
                })
                .findFirst();
    }

    private static String getNsContextsByNamespace(final Definitions definitions,
                                                   final String namespace) {
        return definitions
                .getNsContext()
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), namespace))
                .map(Entry::getKey)
                .findFirst()
                .orElse("");
    }

    private static void applyFontStyle(final FontSet fontSet,
                                       final DMNShape result) {
        if (!(result.getStyle() instanceof DMNStyle)) {
            return;
        }
        final DMNStyle shapeStyle = (DMNStyle) result.getStyle();
        final Color fontColor = ColorUtils.dmnFromWB(fontSet.getFontColour().getValue());
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
        final DMNStyle style = (DMNStyle) result.getStyle();
        if (null != bgset.getBgColour().getValue()) {
            style.setFillColor(ColorUtils.dmnFromWB(bgset.getBgColour().getValue()));
        }
        if (null != bgset.getBorderColour().getValue()) {
            style.setStrokeColor(ColorUtils.dmnFromWB(bgset.getBorderColour().getValue()));
        }
    }

    @SuppressWarnings("unchecked")
    private org.kie.dmn.model.api.DRGElement stunnerToDMN(final Node<?, ?> node,
                                                          final Consumer<ComponentWidths> componentWidthsConsumer) {
        if (node.getContent() instanceof View<?>) {
            final View<?> view = (View<?>) node.getContent();
            if (view.getDefinition() instanceof InputData) {
                return inputDataConverter.dmnFromNode((Node<View<InputData>, ?>) node,
                                                      componentWidthsConsumer);
            } else if (view.getDefinition() instanceof Decision) {
                return decisionConverter.dmnFromNode((Node<View<Decision>, ?>) node,
                                                     componentWidthsConsumer);
            } else if (view.getDefinition() instanceof BusinessKnowledgeModel) {
                return bkmConverter.dmnFromNode((Node<View<BusinessKnowledgeModel>, ?>) node,
                                                componentWidthsConsumer);
            } else if (view.getDefinition() instanceof KnowledgeSource) {
                return knowledgeSourceConverter.dmnFromNode((Node<View<KnowledgeSource>, ?>) node,
                                                            componentWidthsConsumer);
            } else if (view.getDefinition() instanceof DecisionService) {
                return decisionServiceConverter.dmnFromNode((Node<View<DecisionService>, ?>) node,
                                                            componentWidthsConsumer);
            } else {
                throw new UnsupportedOperationException("Unsupported node type detected.");
            }
        }
        throw new IllegalStateException("wrong diagram structure to marshall");
    }

    @Override
    public DiagramMetadataMarshaller<Metadata> getMetadataMarshaller() {
        return diagramMetadataMarshaller;
    }
}
