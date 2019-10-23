/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper;

import java.util.AbstractMap;
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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
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
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIPoint;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIDiagramElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIStyle;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITArtifact;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAuthorityRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBusinessKnowledgeModel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeSource;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDecisionServiceDividerLine;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNStyle;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentsWidthsExtension;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.BusinessKnowledgeModelConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.DecisionConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.DecisionServiceConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.DefinitionsConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.IdPropertyConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.InputDataConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.ItemDefinitionPropertyConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.KnowledgeSourceConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.TextAnnotationConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.ColorUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.FontSetPropertyConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.DMNMarshallerUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.NameSpaceUtils;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.heightOfShape;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.lowerRightBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.upperLeftBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.widthOfShape;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.xOfBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.xOfShape;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.yOfBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.yOfShape;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

@ApplicationScoped
public class DMNMarshallerKogitoUnmarshaller {

    private static final String INFO_REQ_ID = getDefinitionId(InformationRequirement.class);
    private static final String KNOWLEDGE_REQ_ID = getDefinitionId(KnowledgeRequirement.class);
    private static final String AUTH_REQ_ID = getDefinitionId(AuthorityRequirement.class);
    private static final String ASSOCIATION_ID = getDefinitionId(Association.class);

    private static final double CENTRE_TOLERANCE = 1.0;

    private final FactoryManager factoryManager;
    private final DMNMarshallerImportsHelperKogito dmnMarshallerImportsHelper;

    private InputDataConverter inputDataConverter;
    private DecisionConverter decisionConverter;
    private BusinessKnowledgeModelConverter bkmConverter;
    private KnowledgeSourceConverter knowledgeSourceConverter;
    private TextAnnotationConverter textAnnotationConverter;
    private DecisionServiceConverter decisionServiceConverter;

    protected DMNMarshallerKogitoUnmarshaller() {
        this(null, null);
    }

    @Inject
    public DMNMarshallerKogitoUnmarshaller(final FactoryManager factoryManager,
                                           final DMNMarshallerImportsHelperKogito dmnMarshallerImportsHelper) {
        this.factoryManager = factoryManager;
        this.dmnMarshallerImportsHelper = dmnMarshallerImportsHelper;

        this.inputDataConverter = new InputDataConverter(factoryManager);
        this.decisionConverter = new DecisionConverter(factoryManager);
        this.bkmConverter = new BusinessKnowledgeModelConverter(factoryManager);
        this.knowledgeSourceConverter = new KnowledgeSourceConverter(factoryManager);
        this.textAnnotationConverter = new TextAnnotationConverter(factoryManager);
        this.decisionServiceConverter = new DecisionServiceConverter(factoryManager);
    }

    @PostConstruct
    public void init() {
        MainJs.initializeJsInteropConstructors(MainJs.getConstructorsMap());
    }

    // ==================================
    // UNMARSHALL
    // ==================================

    @SuppressWarnings("unchecked")
    public Graph unmarshall(final Metadata metadata,
                            final JSITDefinitions jsiDefinitions) {
        final Map<String, HasComponentWidths> hasComponentWidthsMap = new HashMap<>();
        final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer = (uuid, hcw) -> {
            if (Objects.nonNull(uuid)) {
                hasComponentWidthsMap.put(uuid, hcw);
            }
        };

        final List<JSITDRGElement> diagramDrgElements = jsiDefinitions.getDrgElement();
        final Optional<JSIDMNDiagram> dmnDDDiagram = findJSIDiagram(jsiDefinitions);

        // Get external DMN model information
        final Map<JSITImport, JSITDefinitions> importDefinitions = dmnMarshallerImportsHelper.getImportDefinitions(metadata, jsiDefinitions.getImport());

        // Get external PMML model information
        final Map<JSITImport, PMMLDocumentMetadata> pmmlDocuments = dmnMarshallerImportsHelper.getPMMLDocuments(metadata, jsiDefinitions.getImport());

        // Map external DRGElements
        final List<JSIDMNShape> dmnShapes = new ArrayList<>();
        final List<JSITDRGElement> importedDrgElements = new ArrayList<>();
        if (dmnDDDiagram.isPresent()) {
            final JSIDMNDiagram jsidmnDiagram = Js.uncheckedCast(dmnDDDiagram.get());
            dmnShapes.addAll(getUniqueDMNShapes(jsidmnDiagram));
            importedDrgElements.addAll(getImportedDrgElementsByShape(dmnShapes,
                                                                     importDefinitions,
                                                                     jsiDefinitions));
        }

        // Combine all explicit and imported elements into one
        final List<JSITDRGElement> drgElements = new ArrayList<>();
        final Set<JSITDecisionService> dmnDecisionServices = new HashSet<>();
        drgElements.addAll(diagramDrgElements);
        drgElements.addAll(importedDrgElements);

        // Remove DRGElements that doesn't have any local or imported shape.
        removeDrgElementsWithoutShape(drgElements, dmnShapes);

        // Main conversion from DMN to Stunner
        final Map<String, Entry<JSITDRGElement, Node>> elems = new HashMap<>();
        for (int i = 0; i < drgElements.size(); i++) {
            final JSITDRGElement drgElement = Js.uncheckedCast(drgElements.get(i));
            final String id = drgElement.getId();
            final Node stunnerNode = dmnToStunner(drgElement,
                                                  hasComponentWidthsConsumer,
                                                  importedDrgElements);
            elems.put(id,
                      new AbstractMap.SimpleEntry<>(drgElement, stunnerNode));
        }

        // Stunner rely on relative positioning for Edge connections, so need to cycle on DMNShape first.
        for (Entry<JSITDRGElement, Node> kv : elems.values()) {
            ddExtAugmentStunner(dmnDDDiagram, kv.getValue());
        }

        // Setup Node Relationships and Connections all based on absolute positioning
        for (Entry<JSITDRGElement, Node> kv : elems.values()) {
            final JSITDRGElement element = Js.uncheckedCast(kv.getKey());
            final Node currentNode = kv.getValue();

            // For imported nodes, we don't have its connections
            if (isImportedDRGElement(importedDrgElements, element)) {
                continue;
            }

            // DMN spec table 2: Requirements connection rules
            if (JSITDecision.instanceOf(element)) {
                final JSITDecision decision = Js.uncheckedCast(element);
                final List<JSITInformationRequirement> jsiInformationRequirements = decision.getInformationRequirement();
                for (int i = 0; i < jsiInformationRequirements.size(); i++) {
                    final JSITInformationRequirement ir = Js.uncheckedCast(jsiInformationRequirements.get(i));
                    connectEdgeToNodes(INFO_REQ_ID,
                                       ir,
                                       ir.getRequiredInput(),
                                       elems,
                                       jsiDefinitions,
                                       currentNode);
                    connectEdgeToNodes(INFO_REQ_ID,
                                       ir,
                                       ir.getRequiredDecision(),
                                       elems,
                                       jsiDefinitions,
                                       currentNode);
                }
                final List<JSITKnowledgeRequirement> jsiKnowledgeRequirements = decision.getKnowledgeRequirement();
                for (int i = 0; i < jsiKnowledgeRequirements.size(); i++) {
                    final JSITKnowledgeRequirement kr = Js.uncheckedCast(jsiKnowledgeRequirements.get(i));
                    connectEdgeToNodes(KNOWLEDGE_REQ_ID,
                                       kr,
                                       kr.getRequiredKnowledge(),
                                       elems,
                                       jsiDefinitions,
                                       currentNode);
                }
                final List<JSITAuthorityRequirement> jsiAuthorityRequirements = decision.getAuthorityRequirement();
                for (int i = 0; i < jsiAuthorityRequirements.size(); i++) {
                    final JSITAuthorityRequirement ar = Js.uncheckedCast(jsiAuthorityRequirements.get(i));
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredAuthority(),
                                       elems,
                                       jsiDefinitions,
                                       currentNode);
                }
            } else if (JSITBusinessKnowledgeModel.instanceOf(element)) {
                final JSITBusinessKnowledgeModel bkm = Js.uncheckedCast(element);
                final List<JSITKnowledgeRequirement> jsiKnowledgeRequirements = bkm.getKnowledgeRequirement();
                for (int i = 0; i < jsiKnowledgeRequirements.size(); i++) {
                    final JSITKnowledgeRequirement kr = Js.uncheckedCast(jsiKnowledgeRequirements.get(i));
                    connectEdgeToNodes(KNOWLEDGE_REQ_ID,
                                       kr,
                                       kr.getRequiredKnowledge(),
                                       elems,
                                       jsiDefinitions,
                                       currentNode);
                }
                final List<JSITAuthorityRequirement> jsiAuthorityRequirements = bkm.getAuthorityRequirement();
                for (int i = 0; i < jsiAuthorityRequirements.size(); i++) {
                    final JSITAuthorityRequirement ar = Js.uncheckedCast(jsiAuthorityRequirements.get(i));
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredAuthority(),
                                       elems,
                                       jsiDefinitions,
                                       currentNode);
                }
            } else if (JSITKnowledgeSource.instanceOf(element)) {
                final JSITKnowledgeSource ks = Js.uncheckedCast(element);
                final List<JSITAuthorityRequirement> jsiAuthorityRequirements = ks.getAuthorityRequirement();
                for (int i = 0; i < jsiAuthorityRequirements.size(); i++) {
                    final JSITAuthorityRequirement ar = Js.uncheckedCast(jsiAuthorityRequirements.get(i));
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredInput(),
                                       elems,
                                       jsiDefinitions,
                                       currentNode);
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredDecision(),
                                       elems,
                                       jsiDefinitions,
                                       currentNode);
                    connectEdgeToNodes(AUTH_REQ_ID,
                                       ar,
                                       ar.getRequiredAuthority(),
                                       elems,
                                       jsiDefinitions,
                                       currentNode);
                }
            } else if (JSITDecisionService.instanceOf(element)) {
                final JSITDecisionService ds = Js.uncheckedCast(element);
                dmnDecisionServices.add(ds);
                final List<JSITDMNElementReference> jsiEncapsulatedDecisions = ds.getEncapsulatedDecision();
                for (int i = 0; i < jsiEncapsulatedDecisions.size(); i++) {
                    final JSITDMNElementReference er = Js.uncheckedCast(jsiEncapsulatedDecisions.get(i));
                    final String reqInputID = getId(er);
                    final Node requiredNode = getRequiredNode(elems, reqInputID);
                    if (Objects.nonNull(requiredNode)) {
                        connectDSChildEdge(currentNode, requiredNode);
                    }
                }
                final List<JSITDMNElementReference> jsiOutputDecisions = ds.getOutputDecision();
                for (int i = 0; i < jsiOutputDecisions.size(); i++) {
                    final JSITDMNElementReference er = Js.uncheckedCast(jsiOutputDecisions.get(i));
                    final String reqInputID = getId(er);
                    final Node requiredNode = getRequiredNode(elems, reqInputID);
                    if (Objects.nonNull(requiredNode)) {
                        connectDSChildEdge(currentNode, requiredNode);
                    }
                }
            }
        }

        final Map<String, Node<View<TextAnnotation>, ?>> textAnnotations = new HashMap<>();
        final List<JSITArtifact> jsiArtifacts = jsiDefinitions.getArtifact();
        for (int i = 0; i < jsiArtifacts.size(); i++) {
            final JSITArtifact jsiArtifact = Js.uncheckedCast(jsiArtifacts.get(i));
            if (JSITTextAnnotation.instanceOf(jsiArtifact)) {
                final String id = jsiArtifact.getId();
                final JSITTextAnnotation jsiTextAnnotation = Js.uncheckedCast(jsiArtifact);
                final Node<View<TextAnnotation>, ?> textAnnotation = textAnnotationConverter.nodeFromDMN(jsiTextAnnotation,
                                                                                                         hasComponentWidthsConsumer);
                textAnnotations.put(id,
                                    textAnnotation);
            }
        }
        textAnnotations.values().forEach(n -> ddExtAugmentStunner(dmnDDDiagram, n));

        final List<JSITAssociation> associations = new ArrayList<>();
        for (int i = 0; i < jsiArtifacts.size(); i++) {
            final JSITArtifact jsiArtifact = Js.uncheckedCast(jsiArtifacts.get(i));
            if (JSITAssociation.instanceOf(jsiArtifact)) {
                final JSITAssociation jsiAssociation = Js.uncheckedCast(jsiArtifact);
                associations.add(jsiAssociation);
            }
        }
        for (int i = 0; i < associations.size(); i++) {
            final JSITAssociation jsiAssociation = Js.uncheckedCast(associations.get(i));
            final String sourceId = getId(jsiAssociation.getSourceRef());
            final Node sourceNode = Optional.ofNullable(elems.get(sourceId)).map(Entry::getValue).orElse(textAnnotations.get(sourceId));

            final String targetId = getId(jsiAssociation.getTargetRef());
            final Node targetNode = Optional.ofNullable(elems.get(targetId)).map(Entry::getValue).orElse(textAnnotations.get(targetId));

            @SuppressWarnings("unchecked")
            final Edge<View<Association>, ?> myEdge = (Edge<View<Association>, ?>) factoryManager.newElement(idOfDMNorWBUUID(jsiAssociation),
                                                                                                             ASSOCIATION_ID).asEdge();

            final Id id = IdPropertyConverter.wbFromDMN(jsiAssociation.getId());
            final Description description = new Description(jsiAssociation.getDescription());
            final Association definition = new Association(id, description);
            myEdge.getContent().setDefinition(definition);

            connectEdge(myEdge,
                        sourceNode,
                        targetNode);
            setConnectionMagnets(myEdge, jsiAssociation.getId(), jsiDefinitions);
        }

        //Ensure all locations are updated to relative for Stunner
        for (Entry<JSITDRGElement, Node> kv : elems.values()) {
            PointUtils.convertToRelativeBounds(kv.getValue());
        }

        final Graph graph = factoryManager.newDiagram("prova",
                                                      BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class),
                                                      metadata).getGraph();
        elems.values().stream().map(Entry::getValue).forEach(graph::addNode);
        textAnnotations.values().forEach(graph::addNode);

        final Node<?, ?> dmnDiagramRoot = DMNMarshallerUtils.findDMNDiagramRoot(graph);
        final Definitions definitionsStunnerPojo = DefinitionsConverter.wbFromDMN(jsiDefinitions,
                                                                                  importDefinitions,
                                                                                  pmmlDocuments);
        loadImportedItemDefinitions(definitionsStunnerPojo, importDefinitions);
        ((View<DMNDiagram>) dmnDiagramRoot.getContent()).getDefinition().setDefinitions(definitionsStunnerPojo);

        //Only connect Nodes to the Diagram that are not referenced by DecisionServices
        final List<String> references = new ArrayList<>();
        final List<JSITDecisionService> lstDecisionServices = new ArrayList<>(dmnDecisionServices);
        for (int iDS = 0; iDS < lstDecisionServices.size(); iDS++) {
            final JSITDecisionService jsiDecisionService = Js.uncheckedCast(lstDecisionServices.get(iDS));
            final List<JSITDMNElementReference> jsiEncapsulatedDecisions = jsiDecisionService.getEncapsulatedDecision();
            if (Objects.nonNull(jsiEncapsulatedDecisions)) {
                for (int i = 0; i < jsiEncapsulatedDecisions.size(); i++) {
                    final JSITDMNElementReference jsiEncapsulatedDecision = Js.uncheckedCast(jsiEncapsulatedDecisions.get(i));
                    references.add(jsiEncapsulatedDecision.getHref());
                }
            }

            final List<JSITDMNElementReference> jsiOutputDecisions = jsiDecisionService.getOutputDecision();
            if (Objects.nonNull(jsiOutputDecisions)) {
                for (int i = 0; i < jsiOutputDecisions.size(); i++) {
                    final JSITDMNElementReference jsiOutputDecision = Js.uncheckedCast(jsiOutputDecisions.get(i));
                    references.add(jsiOutputDecision.getHref());
                }
            }
        }

        final Map<JSITDRGElement, Node> elementsToConnectToRoot = new HashMap<>();
        for (Entry<JSITDRGElement, Node> kv : elems.values()) {
            final JSITDRGElement element = Js.uncheckedCast(kv.getKey());
            final Node node = kv.getValue();
            if (!references.contains("#" + element.getId())) {
                elementsToConnectToRoot.put(element, node);
            }
        }
        elementsToConnectToRoot.values().forEach(node -> connectRootWithChild(dmnDiagramRoot, node));
        textAnnotations.values().forEach(node -> connectRootWithChild(dmnDiagramRoot, node));

        //Copy ComponentWidths information
        final Optional<JSITComponentsWidthsExtension> extension = findComponentsWidthsExtension(dmnDDDiagram);
        extension.ifPresent(componentsWidthsExtension -> {
            //This condition is required because a node with ComponentsWidthsExtension
            //can be imported from another diagram but the extension is not imported or present in this diagram.
            if (Objects.nonNull(componentsWidthsExtension.getComponentWidths())) {
                hasComponentWidthsMap.entrySet().forEach(es -> {
                    final List<JSITComponentWidths> jsiComponentWidths = componentsWidthsExtension.getComponentWidths();
                    for (int i = 0; i < jsiComponentWidths.size(); i++) {
                        final JSITComponentWidths jsiWidths = Js.uncheckedCast(jsiComponentWidths.get(i));
                        if (Objects.equals(jsiWidths.getDmnElementRef(), es.getKey())) {
                            final List<Double> widths = es.getValue().getComponentWidths();
                            if (Objects.nonNull(jsiWidths.getWidth())) {
                                widths.clear();
                                for (int w = 0; w < jsiWidths.getWidth().size(); w++) {
                                    final double width = jsiWidths.getWidth().get(w).doubleValue();
                                    widths.add(width);
                                }
                            }
                        }
                    }
                });
            }
        });

        return graph;
    }

    private Optional<JSIDMNDiagram> findJSIDiagram(final JSITDefinitions dmnXml) {
        if (Objects.isNull(dmnXml.getDMNDI())) {
            return Optional.empty();
        }
        final List<JSIDMNDiagram> elems = dmnXml.getDMNDI().getDMNDiagram();
        if (elems.size() != 1) {
            return Optional.empty();
        } else {
            return Optional.of(Js.uncheckedCast(elems.get(0)));
        }
    }

    private void removeDrgElementsWithoutShape(final List<JSITDRGElement> drgElements,
                                               final List<JSIDMNShape> dmnShapes) {
        // DMN 1.1 doesn't have DMNShape, so we include all DRGElements and create all the shapes.
        if (dmnShapes.isEmpty()) {
            return;
        }

        drgElements.removeIf(element -> dmnShapes.stream().noneMatch(s -> Objects.equals(s.getDmnElementRef().getLocalPart(),
                                                                                         element.getId())));
    }

    private void updateIDsWithAlias(final Map<String, String> indexByUri,
                                    final List<JSITDRGElement> importedDrgElements) {
        if (importedDrgElements.isEmpty()) {
            return;
        }

        final QName defaultNamespace = new QName(XMLConstants.NULL_NS_URI,
                                                 "Namespace",
                                                 XMLConstants.DEFAULT_NS_PREFIX);

        for (JSITDRGElement element : importedDrgElements) {
            final Map<QName, String> otherAttributes = JSITUnaryTests.getOtherAttributesMap(element);
            final String namespaceAttribute = otherAttributes.getOrDefault(defaultNamespace, "");
            if (!StringUtils.isEmpty(namespaceAttribute)) {
                if (indexByUri.containsKey(namespaceAttribute)) {
                    final String alias = indexByUri.get(namespaceAttribute);
                    changeAlias(alias, element);
                }
            }
        }
    }

    private void changeAlias(final String alias,
                             final JSITDRGElement drgElement) {
        if (drgElement.getId().contains(":")) {
            final String id = drgElement.getId().split(":")[1];
            drgElement.setId(alias + ":" + id);
        }
    }

    private Node getRequiredNode(final Map<String, Entry<JSITDRGElement, Node>> elems,
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

    private List<JSITDRGElement> getImportedDrgElementsByShape(final List<JSIDMNShape> dmnShapes,
                                                               final Map<JSITImport, JSITDefinitions> importDefinitions,
                                                               final JSITDefinitions dmnXml) {

        final List<JSITDRGElement> importedDRGElements = dmnMarshallerImportsHelper.getImportedDRGElements(importDefinitions);

        // Update IDs with the alias used in this file for the respective imports
        final Map<String, String> indexByUri = NameSpaceUtils.extractNamespacesKeyedByUri(dmnXml);
        updateIDsWithAlias(indexByUri, importedDRGElements);

        return dmnShapes
                .stream()
                .map(shape -> {
                    final String dmnElementRef = getDmnElementRef(shape);
                    final Optional<JSITDRGElement> ref = getReference(importedDRGElements, dmnElementRef);
                    return ref.orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private Optional<JSITDRGElement> getReference(final List<JSITDRGElement> importedDRGElements,
                                                  final String dmnElementRef) {
        for (JSITDRGElement importedDRGElement : importedDRGElements) {
            final String importedDRGElementId = importedDRGElement.getId();
            if (Objects.equals(importedDRGElementId, dmnElementRef)) {
                return Optional.of(importedDRGElement);
            }
        }
        return Optional.empty();
    }

    private String getDmnElementRef(final JSIDMNShape dmnShape) {
        final QName elementRef = dmnShape.getDmnElementRef();
        if (Objects.nonNull(elementRef)) {
            return elementRef.getLocalPart();
        }
        return "";
    }

    private List<JSIDMNShape> getUniqueDMNShapes(final JSIDMNDiagram dmnDDDiagram) {
        final Map<String, JSIDMNShape> jsidmnShapes = new HashMap<>();
        final List<JSIDiagramElement> unwrapped = dmnDDDiagram.getDMNDiagramElement();
        for (int i = 0; i < unwrapped.size(); i++) {
            final JSIDiagramElement jsiDiagramElement = Js.uncheckedCast(unwrapped.get(i));
            if (JSIDMNShape.instanceOf(jsiDiagramElement)) {
                final JSIDMNShape jsidmnShape = Js.uncheckedCast(jsiDiagramElement);
                if (!jsidmnShapes.containsKey(jsidmnShape.getId())) {
                    jsidmnShapes.put(jsidmnShape.getId(), jsidmnShape);
                }
            }
        }
        return new ArrayList<>(jsidmnShapes.values());
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

    private String idOfDMNorWBUUID(final JSITDMNElement dmn) {
        return Objects.nonNull(dmn.getId()) ? dmn.getId() : UUID.uuid();
    }

    private String getId(final JSITDMNElementReference er) {
        String href = er.getHref();
        return href.contains("#") ? href.substring(href.indexOf('#') + 1) : href;
    }

    private void connectEdgeToNodes(final String connectorTypeId,
                                    final JSITDMNElement jsiDMNElement,
                                    final JSITDMNElementReference jsiDMNElementReference,
                                    final Map<String, Entry<JSITDRGElement, Node>> elems,
                                    final JSITDefinitions jsiDefinitions,
                                    final Node currentNode) {
        if (Objects.nonNull(jsiDMNElementReference)) {
            final String reqInputID = getId(jsiDMNElementReference);
            final Node requiredNode = getRequiredNode(elems, reqInputID);
            final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(jsiDMNElement),
                                                          connectorTypeId).asEdge();
            connectEdge(myEdge,
                        requiredNode,
                        currentNode);
            setConnectionMagnets(myEdge, jsiDMNElement.getId(), jsiDefinitions);
        }
    }

    private Node dmnToStunner(final JSITDRGElement dmn,
                              final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer,
                              final List<JSITDRGElement> importedDrgElements) {

        final Node node = createNode(dmn,
                                     hasComponentWidthsConsumer);
        return setAllowOnlyVisualChange(importedDrgElements, node);
    }

    private Node createNode(final JSITDRGElement dmn,
                            final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (JSITInputData.instanceOf(dmn)) {
            return inputDataConverter.nodeFromDMN(Js.uncheckedCast(dmn),
                                                  hasComponentWidthsConsumer);
        } else if (JSITDecision.instanceOf(dmn)) {
            return decisionConverter.nodeFromDMN(Js.uncheckedCast(dmn),
                                                 hasComponentWidthsConsumer);
        } else if (JSITBusinessKnowledgeModel.instanceOf(dmn)) {
            return bkmConverter.nodeFromDMN(Js.uncheckedCast(dmn),
                                            hasComponentWidthsConsumer);
        } else if (JSITKnowledgeSource.instanceOf(dmn)) {
            return knowledgeSourceConverter.nodeFromDMN(Js.uncheckedCast(dmn),
                                                        hasComponentWidthsConsumer);
        } else if (JSITDecisionService.instanceOf(dmn)) {
            return decisionServiceConverter.nodeFromDMN(Js.uncheckedCast(dmn),
                                                        hasComponentWidthsConsumer);
        } else {
            throw new UnsupportedOperationException("Unsupported DRGElement type [" + dmn.getTYPE_NAME() + "]");
        }
    }

    private Node setAllowOnlyVisualChange(final List<JSITDRGElement> importedDrgElements,
                                          final Node node) {
        getDRGElement(node).ifPresent(drgElement -> {
            if (isImportedDRGElement(importedDrgElements, drgElement)) {
                drgElement.setAllowOnlyVisualChange(true);
            } else {
                drgElement.setAllowOnlyVisualChange(false);
            }
        });

        return node;
    }

    private Optional<DRGElement> getDRGElement(final Node node) {
        final Object objectDefinition = DefinitionUtils.getElementDefinition(node);

        if (objectDefinition instanceof DRGElement) {
            return Optional.of((DRGElement) objectDefinition);
        }

        return Optional.empty();
    }

    private boolean isImportedDRGElement(final List<JSITDRGElement> importedDrgElements,
                                         final JSITDRGElement drgElement) {
        return isImportedIdNode(importedDrgElements, drgElement.getId());
    }

    private boolean isImportedDRGElement(final List<JSITDRGElement> importedDrgElements,
                                         final DRGElement drgElement) {
        return isImportedIdNode(importedDrgElements, drgElement.getId().getValue());
    }

    private boolean isImportedIdNode(final List<JSITDRGElement> importedDrgElements,
                                     final String id) {
        return importedDrgElements
                .stream()
                .anyMatch(drgElement -> Objects.equals(drgElement.getId(), id));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectRootWithChild(final Node dmnDiagramRoot,
                                      final Node child) {
        final String uuid = UUID.uuid();
        final Edge<Child, Node> edge = new EdgeImpl<>(uuid);
        edge.setContent(new Child());
        connectEdge(edge, dmnDiagramRoot, child);
        final Definitions definitions = ((DMNDiagram) ((View) dmnDiagramRoot.getContent()).getDefinition()).getDefinitions();
        final DMNModelInstrumentedBase childDRG = (DMNModelInstrumentedBase) ((View) child.getContent()).getDefinition();
        childDRG.setParent(definitions);
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
    private void setConnectionMagnets(final Edge edge,
                                      final String dmnEdgeElementRef,
                                      final JSITDefinitions dmnXml) {
        final ViewConnector connectionContent = (ViewConnector) edge.getContent();
        final Optional<JSIDMNDiagram> dmnDiagram = findJSIDiagram(dmnXml);

        Optional<JSIDMNEdge> dmnEdge = Optional.empty();
        if (dmnDiagram.isPresent()) {
            final JSIDMNDiagram jsiDiagram = Js.uncheckedCast(dmnDiagram.get());
            final List<JSIDiagramElement> jsiDiagramElements = jsiDiagram.getDMNDiagramElement();
            for (int i = 0; i < jsiDiagramElements.size(); i++) {
                final JSIDiagramElement jsiDiagramElement = Js.uncheckedCast(jsiDiagramElements.get(i));
                if (JSIDMNEdge.instanceOf(jsiDiagramElement)) {
                    final JSIDMNEdge jsiEdge = Js.uncheckedCast(jsiDiagramElement);
                    if (Objects.equals(jsiEdge.getDmnElementRef().getLocalPart(), dmnEdgeElementRef)) {
                        dmnEdge = Optional.of(jsiEdge);
                        break;
                    }
                }
            }
        }
        if (dmnEdge.isPresent()) {
            final JSIDMNEdge e = Js.uncheckedCast(dmnEdge.get());
            final JSIPoint source = Js.uncheckedCast(e.getWaypoint().get(0));
            final Node<View<?>, Edge> sourceNode = edge.getSourceNode();
            if (null != sourceNode) {
                setConnectionMagnet(sourceNode,
                                    source,
                                    connectionContent::setSourceConnection);
            }
            final JSIPoint target = Js.uncheckedCast(e.getWaypoint().get(e.getWaypoint().size() - 1));
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
                                     final JSIPoint magnetPoint,
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

    private Optional<JSITComponentsWidthsExtension> findComponentsWidthsExtension(final Optional<JSIDMNDiagram> dmnDDDiagram) {
        if (!dmnDDDiagram.isPresent()) {
            return Optional.empty();
        }
        final JSIDMNDiagram jsiDiagram = Js.uncheckedCast(dmnDDDiagram.get());
        final JSIDiagramElement.JSIExtension dmnDDExtensions = Js.uncheckedCast(jsiDiagram.getExtension());

        if (Objects.isNull(dmnDDExtensions)) {
            return Optional.empty();
        }
        if (Objects.isNull(dmnDDExtensions.getAny())) {
            return Optional.empty();
        }
        final List<Object> extensions = dmnDDExtensions.getAny();
        if (!Objects.isNull(extensions)) {
            for (int i = 0; i < extensions.size(); i++) {
                final Object wrapped = extensions.get(i);
                final Object extension = JsUtils.getUnwrappedElement(wrapped);
                if (JSITComponentsWidthsExtension.instanceOf(extension)) {
                    final JSITComponentsWidthsExtension jsiExtension = Js.uncheckedCast(extension);
                    return Optional.of(jsiExtension);
                }
            }
        }
        return Optional.empty();
    }

    private void loadImportedItemDefinitions(final Definitions definitions,
                                             final Map<JSITImport, JSITDefinitions> importDefinitions) {
        definitions.getItemDefinition().addAll(getWbImportedItemDefinitions(importDefinitions));
    }

    private List<ItemDefinition> getWbImportedItemDefinitions(final Map<JSITImport, JSITDefinitions> importDefinitions) {
        return dmnMarshallerImportsHelper
                .getImportedItemDefinitions(importDefinitions)
                .stream()
                .map(ItemDefinitionPropertyConverter::wbFromDMN)
                .peek(itemDefinition -> itemDefinition.setAllowOnlyVisualChange(true))
                .collect(toList());
    }

    private void ddExtAugmentStunner(final Optional<JSIDMNDiagram> dmnDDDiagram,
                                     final Node currentNode) {
        if (!dmnDDDiagram.isPresent()) {
            return;
        }

        final JSIDMNDiagram jsiDiagram = Js.uncheckedCast(dmnDDDiagram.get());
        final List<JSIDiagramElement> jsiDiagramElements = jsiDiagram.getDMNDiagramElement();

        final List<JSIDMNShape> drgShapes = new ArrayList<>();
        for (int i = 0; i < jsiDiagramElements.size(); i++) {
            final JSIDiagramElement jsiDiagramElement = Js.uncheckedCast(jsiDiagramElements.get(i));
            if (JSIDMNShape.instanceOf(jsiDiagramElement)) {
                drgShapes.add(Js.uncheckedCast(jsiDiagramElement));
            }
        }
        final View content = (View) currentNode.getContent();
        final Bound ulBound = upperLeftBound(content);
        final Bound lrBound = lowerRightBound(content);
        if (content.getDefinition() instanceof Decision) {
            final Decision d = (Decision) content.getDefinition();
            internalAugment(drgShapes, d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof InputData) {
            final InputData d = (InputData) content.getDefinition();
            internalAugment(drgShapes,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof BusinessKnowledgeModel) {
            final BusinessKnowledgeModel d = (BusinessKnowledgeModel) content.getDefinition();
            internalAugment(drgShapes,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof KnowledgeSource) {
            final KnowledgeSource d = (KnowledgeSource) content.getDefinition();
            internalAugment(drgShapes,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof TextAnnotation) {
            final TextAnnotation d = (TextAnnotation) content.getDefinition();
            internalAugment(drgShapes,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof DecisionService) {
            final DecisionService d = (DecisionService) content.getDefinition();
            internalAugment(drgShapes,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet,
                            (dividerLineY) -> d.setDividerLineY(new DecisionServiceDividerLineY(dividerLineY - ulBound.getY())));
        }
    }

    @SuppressWarnings("unchecked")
    private void internalAugment(final List<JSIDMNShape> drgShapeStream,
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
                        (line) -> {/*NOP*/});
    }

    @SuppressWarnings("unchecked")
    private void internalAugment(final List<JSIDMNShape> drgShapes,
                                 final Id id,
                                 final Bound ulBound,
                                 final RectangleDimensionsSet dimensionsSet,
                                 final Bound lrBound,
                                 final BackgroundSet bgset,
                                 final Consumer<FontSet> fontSetSetter,
                                 final Consumer<Double> decisionServiceDividerLineYSetter) {
        //Lookup JSIDMNShape corresponding to DRGElement...
        Optional<JSIDMNShape> drgShapeOpt = Optional.empty();
        for (int i = 0; i < drgShapes.size(); i++) {
            final JSIDMNShape jsiShape = Js.uncheckedCast(drgShapes.get(i));
            if (Objects.equals(id.getValue(), jsiShape.getDmnElementRef().getLocalPart())) {
                drgShapeOpt = Optional.of(jsiShape);
            }
        }
        if (!drgShapeOpt.isPresent()) {
            return;
        }

        //Augment Stunner Node with Shape data
        final JSIDMNShape drgShape = Js.uncheckedCast(drgShapeOpt.get());

        if (Objects.nonNull(ulBound)) {
            ulBound.setX(xOfShape(drgShape));
            ulBound.setY(yOfShape(drgShape));
        }
        dimensionsSet.setWidth(new Width(widthOfShape(drgShape)));
        dimensionsSet.setHeight(new Height(heightOfShape(drgShape)));
        if (Objects.nonNull(lrBound)) {
            lrBound.setX(xOfShape(drgShape) + widthOfShape(drgShape));
            lrBound.setY(yOfShape(drgShape) + heightOfShape(drgShape));
        }

        final JSIStyle drgStyle = Js.uncheckedCast(JsUtils.getUnwrappedElement(drgShape.getStyle()));
        final JSIDMNStyle dmnStyleOfDrgShape = JSIDMNStyle.instanceOf(drgStyle) ? Js.uncheckedCast(drgStyle) : null;
        if (Objects.nonNull(dmnStyleOfDrgShape)) {
            if (Objects.nonNull(dmnStyleOfDrgShape.getFillColor())) {
                bgset.setBgColour(new BgColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getFillColor())));
            }
            if (Objects.nonNull(dmnStyleOfDrgShape.getStrokeColor())) {
                bgset.setBorderColour(new BorderColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getStrokeColor())));
            }
        }

        final FontSet fontSet = new FontSet();
        if (Objects.nonNull(dmnStyleOfDrgShape)) {
            mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN(dmnStyleOfDrgShape));
        }
        if (Objects.nonNull(drgShape.getDMNLabel())) {
            final JSIDMNShape jsiLabel = Js.uncheckedCast(drgShape.getDMNLabel());
            final JSIStyle jsiLabelStyle = jsiLabel.getStyle();
            final Object jsiLabelSharedStyle = Js.uncheckedCast(jsiLabel.getSharedStyle());
            if (Objects.nonNull(jsiLabelSharedStyle) && JSIDMNStyle.instanceOf(jsiLabelSharedStyle)) {
                mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN((Js.uncheckedCast(jsiLabelSharedStyle))));
            }
            if (Objects.nonNull(jsiLabelStyle) && JSIDMNStyle.instanceOf(jsiLabelStyle)) {
                mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN(Js.uncheckedCast(jsiLabelStyle)));
            }
        }
        fontSetSetter.accept(fontSet);

        if (Objects.nonNull(drgShape.getDMNDecisionServiceDividerLine())) {
            final JSIDMNDecisionServiceDividerLine divider = Js.uncheckedCast(drgShape.getDMNDecisionServiceDividerLine());
            final List<JSIPoint> dividerPoints = divider.getWaypoint();
            final JSIPoint dividerY = Js.uncheckedCast(dividerPoints.get(0));
            decisionServiceDividerLineYSetter.accept(dividerY.getY());
        }
    }

    private void mergeFontSet(final FontSet fontSet,
                              final FontSet additional) {
        if (Objects.nonNull(additional.getFontFamily())) {
            fontSet.setFontFamily(additional.getFontFamily());
        }
        if (Objects.nonNull(additional.getFontSize())) {
            fontSet.setFontSize(additional.getFontSize());
        }
        if (Objects.nonNull(additional.getFontColour())) {
            fontSet.setFontColour(additional.getFontColour());
        }
    }
}
