/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.marshaller.marshall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.marshaller.common.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.marshaller.common.WrapperUtils;
import org.kie.workbench.common.dmn.client.marshaller.converters.AssociationConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.BusinessKnowledgeModelConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.DecisionConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.DecisionServiceConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.DefinitionsConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.InputDataConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.KnowledgeSourceConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.TextAnnotationConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIDiagramElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAuthorityRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBusinessKnowledgeModel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeSource;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentsWidthsExtension;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getEdgeId;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getRawId;
import static org.kie.workbench.common.dmn.client.marshaller.common.JsInteropUtils.anyMatch;
import static org.kie.workbench.common.dmn.client.marshaller.common.JsInteropUtils.forEach;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.upperLeftBound;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.xOfBound;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.yOfBound;
import static org.kie.workbench.common.stunner.core.util.DefinitionUtils.getElementDefinition;

@ApplicationScoped
public class DMNMarshaller {

    public static final String PREFIX = "dmn";

    public enum JSINodeLocalPartName {

        UNKNOWN("UNKNOWN"),
        BUSINESS_KNOWLEDGE_MODEL("businessKnowledgeModel"),
        DECISION("decision"),
        DECISION_SERVICE("decisionService"),
        INPUT_DATA("inputData"),
        KNOWLEDGE_SOURCE("knowledgeSource");

        private String localPart;

        JSINodeLocalPartName(final String localPart) {
            this.localPart = localPart;
        }

        public String getLocalPart() {
            return localPart;
        }
    }

    private InputDataConverter inputDataConverter;
    private DecisionConverter decisionConverter;
    private BusinessKnowledgeModelConverter bkmConverter;
    private KnowledgeSourceConverter knowledgeSourceConverter;
    private TextAnnotationConverter textAnnotationConverter;
    private DecisionServiceConverter decisionServiceConverter;

    @Inject
    private DMNDiagramsSession dmnDiagramsSession;

    protected DMNMarshaller() {
        this(null);
    }

    @Inject
    public DMNMarshaller(final FactoryManager factoryManager) {
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

    public JSITDefinitions marshall() {
        final Map<String, JSITDRGElement> nodes = new HashMap<>();
        final Map<String, JSITTextAnnotation> textAnnotations = new HashMap<>();
        final Node<View<DMNDiagram>, ?> dmnDiagramRoot = (Node<View<DMNDiagram>, ?>) DMNGraphUtils.findDMNDiagramRoot(dmnDiagramsSession.getDRGDiagram().getGraph());
        final Definitions definitionsStunnerPojo = ((DMNDiagram) getElementDefinition(dmnDiagramRoot)).getDefinitions();
        final List<String> dmnDiagramElementIds = new ArrayList<>();

        final JSITDefinitions definitions = DefinitionsConverter.dmnFromWB(definitionsStunnerPojo, true);
        if (Objects.isNull(definitions.getExtensionElements())) {
            JSITDMNElement.JSIExtensionElements jsiExtensionElements = new JSITDMNElement.JSIExtensionElements();
            definitions.setExtensionElements(jsiExtensionElements);
        }

        final List<JSIDMNDiagram> dmnDiagrams = definitions.getDMNDI().getDMNDiagram();
        forEach(dmnDiagrams, diagram -> {

            final String elementDiagramId = diagram.getId();
            final List<JSIDMNEdge> dmnEdges = new ArrayList<>();
            final List<Node> diagramNodes = getNodeStream(dmnDiagramsSession.getDiagram(elementDiagramId));

            //Setup callback for marshalling ComponentWidths
            if (Objects.isNull(diagram.getExtension())) {
                diagram.setExtension(new JSIDiagramElement.JSIExtension());
            }
            final JSITComponentsWidthsExtension componentsWidthsExtension = new JSITComponentsWidthsExtension();
            final JSIDiagramElement.JSIExtension extension = diagram.getExtension();
            JSITComponentsWidthsExtension wrappedComponentsWidthsExtension = WrapperUtils.getWrappedJSITComponentsWidthsExtension(componentsWidthsExtension);
            extension.addAny(wrappedComponentsWidthsExtension);

            final Consumer<JSITComponentWidths> componentWidthsConsumer = (cw) -> componentsWidthsExtension.addComponentWidths(cw);

            //Convert relative positioning to absolute
            for (final Node<?, ?> node : diagramNodes) {
                PointUtils.convertToAbsoluteBounds(node);
            }

            //Iterate Graph processing nodes..
            for (final Node<?, ?> node : diagramNodes) {

                if (!(node.getContent() instanceof View<?>)) {
                    continue;
                }

                final View<?> view = (View<?>) node.getContent();
                final Object viewDefinition = view.getDefinition();

                if (!(viewDefinition instanceof HasContentDefinitionId)) {
                    continue;
                }

                final HasContentDefinitionId hasContentDefinitionId = (HasContentDefinitionId) viewDefinition;
                final String nodeDiagramId = hasContentDefinitionId.getDiagramId();

                if (!Objects.equals(nodeDiagramId, elementDiagramId)) {
                    continue;
                }

                if (viewDefinition instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) viewDefinition;
                    if (!drgElement.isAllowOnlyVisualChange()) {
                        nodes.put(drgElement.getId().getValue(),
                                  stunnerToDMN(withIncludedModels(node, definitionsStunnerPojo),
                                               componentWidthsConsumer));
                    }
                    final String namespaceURI = definitionsStunnerPojo.getDefaultNamespace();
                    diagram.addDMNDiagramElement(WrapperUtils.getWrappedJSIDMNShape(diagram,
                                                                                    dmnDiagramElementIds,
                                                                                    definitionsStunnerPojo,
                                                                                    (View<? extends DMNElement>) view,
                                                                                    namespaceURI));
                }

                if (viewDefinition instanceof TextAnnotation) {
                    final TextAnnotation textAnnotation = (TextAnnotation) viewDefinition;
                    if (!textAnnotation.isAllowOnlyVisualChange()) {
                        textAnnotations.put(textAnnotation.getId().getValue(),
                                            textAnnotationConverter.dmnFromNode((Node<View<TextAnnotation>, ?>) node,
                                                                                componentWidthsConsumer));
                    }
                    final String namespaceURI = definitionsStunnerPojo.getDefaultNamespace();
                    diagram.addDMNDiagramElement(WrapperUtils.getWrappedJSIDMNShape(diagram,
                                                                                    dmnDiagramElementIds,
                                                                                    definitionsStunnerPojo,
                                                                                    (View<? extends DMNElement>) view,
                                                                                    namespaceURI));

                    final List<JSITAssociation> associations = AssociationConverter.dmnFromWB((Node<View<TextAnnotation>, ?>) node);
                    forEach(associations, association -> {
                        final JSITAssociation wrappedJSITAssociation = WrapperUtils.getWrappedJSITAssociation(Js.uncheckedCast(association));
                        definitions.addArtifact(wrappedJSITAssociation);
                    });
                }
                connect(diagram, dmnDiagramElementIds, definitionsStunnerPojo, dmnEdges, node, view);
            }

            nodes.values().forEach(node -> {
                mergeOrAddNodeToDefinitions(node, definitions);
            });

            textAnnotations.values().forEach(text -> {
                final boolean exists = anyMatch(definitions.getArtifact(),
                                                artifact -> Objects.equals(artifact.getId(), text.getId()));
                if (!exists) {
                    definitions.addArtifact(WrapperUtils.getWrappedJSITTextAnnotation(text));
                }
            });

            forEach(dmnEdges, dmnEdge -> {
                diagram.addDMNDiagramElement(WrapperUtils.getWrappedJSIDMNEdge(Js.uncheckedCast(dmnEdge)));
            });

            //Convert absolute positioning to relative
            for (final Node<?, ?> node : diagramNodes) {
                PointUtils.convertToRelativeBounds(node);
            }
        });

        return definitions;
    }

    Node<?, ?> withIncludedModels(final Node<?, ?> node,
                                  final Definitions definitionsStunnerPojo) {

        final Object elementDefinition = getElementDefinition(node);
        final List<Import> diagramImports = definitionsStunnerPojo.getImport();

        if (!(elementDefinition instanceof DRGElement) || diagramImports.isEmpty()) {
            return node;
        }

        final DRGElement drgElement = (DRGElement) elementDefinition;
        final Optional<Definitions> nodeDefinitions = getDefinitions(drgElement);

        if (!nodeDefinitions.isPresent()) {
            return node;
        }

        final List<Import> nodeImports = nodeDefinitions.get().getImport();
        updateNodeImports(diagramImports, nodeImports);

        return node;
    }

    private void updateNodeImports(final List<Import> diagramImports,
                                   final List<Import> nodeImports) {
        for (final Import anImport : diagramImports) {
            if (!nodeImports.contains(anImport)) {
                nodeImports.add(anImport);
            }
        }
    }

    private static Optional<Definitions> getDefinitions(final DRGElement drgElement) {

        final DMNModelInstrumentedBase parent = drgElement.getParent();

        if (parent instanceof DMNDiagram) {
            final DMNDiagram diagram = (DMNDiagram) parent;
            return Optional.ofNullable(diagram.getDefinitions());
        }

        if (parent instanceof Definitions) {
            return Optional.of((Definitions) parent);
        }

        return Optional.empty();
    }

    void mergeOrAddNodeToDefinitions(final JSITDRGElement node,
                                     final JSITDefinitions definitions) {

        final Optional<JSITDRGElement> existingNode = getExistingNode(definitions, node);

        if (existingNode.isPresent()) {
            final JSITDRGElement existingDRGElement = Js.uncheckedCast(existingNode.get());
            mergeNodeRequirements(node, existingDRGElement);
        } else {
            addNodeToDefinitions(node, definitions);
        }
    }

    private void addNodeToDefinitions(final JSITDRGElement node,
                                      final JSITDefinitions definitions) {

        final JSINodeLocalPartName localPart = getLocalPart(node);
        final JSITDRGElement toAdd = getWrappedJSITDRGElement(node, localPart);

        definitions.addDrgElement(toAdd);
    }

    JSITDRGElement getWrappedJSITDRGElement(final JSITDRGElement node,
                                            final JSINodeLocalPartName localPart) {
        return WrapperUtils.getWrappedJSITDRGElement(node, PREFIX, localPart.getLocalPart());
    }

    private void mergeNodeRequirements(final JSITDRGElement node,
                                       final JSITDRGElement existingDRGElement) {

        if (instanceOfBusinessKnowledgeModel(node)) {

            final JSITBusinessKnowledgeModel existingBkm = Js.uncheckedCast(existingDRGElement);
            final JSITBusinessKnowledgeModel nodeBkm = Js.uncheckedCast(node);

            final List<JSITAuthorityRequirement> authorityRequirement = nodeBkm.getAuthorityRequirement();
            final List<JSITKnowledgeRequirement> knowledgeRequirement = nodeBkm.getKnowledgeRequirement();

            existingBkm.addAllAuthorityRequirement(authorityRequirement.toArray(new JSITAuthorityRequirement[0]));
            existingBkm.addAllKnowledgeRequirement(knowledgeRequirement.toArray(new JSITKnowledgeRequirement[0]));
        } else if (instanceOfDecision(node)) {

            final JSITDecision existingDecision = Js.uncheckedCast(existingDRGElement);
            final JSITDecision nodeDecision = Js.uncheckedCast(node);

            final List<JSITAuthorityRequirement> authorityRequirement = nodeDecision.getAuthorityRequirement();
            final List<JSITInformationRequirement> informationRequirement = nodeDecision.getInformationRequirement();
            final List<JSITKnowledgeRequirement> knowledgeRequirement = nodeDecision.getKnowledgeRequirement();

            existingDecision.addAllAuthorityRequirement(authorityRequirement.toArray(new JSITAuthorityRequirement[0]));
            existingDecision.addAllInformationRequirement(informationRequirement.toArray(new JSITInformationRequirement[0]));
            existingDecision.addAllKnowledgeRequirement(knowledgeRequirement.toArray(new JSITKnowledgeRequirement[0]));
        } else if (instanceOfKnowledgeSource(node)) {

            final JSITKnowledgeSource existingKnowledgeSource = Js.uncheckedCast(existingDRGElement);
            final JSITKnowledgeSource nodeKnowledgeSource = Js.uncheckedCast(node);

            final List<JSITAuthorityRequirement> authorityRequirement = nodeKnowledgeSource.getAuthorityRequirement();
            existingKnowledgeSource.addAllAuthorityRequirement(authorityRequirement.toArray(new JSITAuthorityRequirement[0]));
        }
    }

    boolean instanceOfBusinessKnowledgeModel(final JSITDRGElement node) {
        return JSITBusinessKnowledgeModel.instanceOf(node);
    }

    boolean instanceOfDecision(final JSITDRGElement node) {
        return JSITDecision.instanceOf(node);
    }

    boolean instanceOfKnowledgeSource(final JSITDRGElement node) {
        return JSITKnowledgeSource.instanceOf(node);
    }

    Optional<JSITDRGElement> getExistingNode(final JSITDefinitions definitions,
                                             final JSITDRGElement node) {
        final JSITDRGElement[] existingDRGElement = new JSITDRGElement[1];

        forEach(definitions.getDrgElement(),
                drgElement -> {
                    if (Objects.equals(drgElement.getId(), node.getId())) {
                        existingDRGElement[0] = drgElement;
                    }
                });

        final JSITDRGElement value = Js.uncheckedCast(existingDRGElement[0]);
        return Optional.ofNullable(value);
    }

    private JSINodeLocalPartName getLocalPart(final JSITDRGElement node) {
        if (JSITBusinessKnowledgeModel.instanceOf(node)) {
            return JSINodeLocalPartName.BUSINESS_KNOWLEDGE_MODEL;
        } else if (JSITDecision.instanceOf(node)) {
            return JSINodeLocalPartName.DECISION;
        } else if (JSITDecisionService.instanceOf(node)) {
            return JSINodeLocalPartName.DECISION_SERVICE;
        } else if (JSITInputData.instanceOf(node)) {
            return JSINodeLocalPartName.INPUT_DATA;
        } else if (JSITKnowledgeSource.instanceOf(node)) {
            return JSINodeLocalPartName.KNOWLEDGE_SOURCE;
        } else {
            return JSINodeLocalPartName.UNKNOWN;
        }
    }

    public List<Node> getNodeStream(final Diagram diagram) {
        final Graph<?, Node> graph = diagram.getGraph();
        return StreamSupport.stream(graph.nodes().spliterator(), false).collect(Collectors.toList());
    }

    private void connect(final JSIDMNDiagram diagram,
                         final List<String> dmnDiagramElementIds,
                         final Definitions definitionsStunnerPojo,
                         final List<JSIDMNEdge> dmnEdges,
                         final Node<?, ?> node,
                         final View<?> view) {

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
                    if (Objects.isNull(sourcePoint)) {
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
                    if (Objects.isNull(targetPoint)) {
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

                    final JSIDMNEdge dmnEdge = new JSIDMNEdge();
                    // DMNDI edge elementRef is uuid of Stunner edge,
                    // with the only exception when edge contains as content a DMN Association (Association is an edge)
                    final String uuid = getRawId(getUUID(e));
                    final String edgeId = getEdgeId(diagram, dmnDiagramElementIds, uuid);

                    dmnEdge.setId(edgeId);
                    final String namespaceURI = definitionsStunnerPojo.getDefaultNamespace();
                    dmnEdge.setDmnElementRef(new QName(namespaceURI,
                                                       uuid,
                                                       XMLConstants.DEFAULT_NS_PREFIX));
                    dmnEdge.addWaypoint(PointUtils.point2dToDMNDIPoint(sourcePoint));
                    for (ControlPoint cp : connectionContent.getControlPoints()) {
                        dmnEdge.addWaypoint(PointUtils.point2dToDMNDIPoint(cp.getLocation()));
                    }
                    dmnEdge.addWaypoint(PointUtils.point2dToDMNDIPoint(targetPoint));
                    dmnEdges.add(dmnEdge);
                }
            }
        }
    }

    private String getUUID(final Edge<?, ?> edge) {

        if (edge.getContent() instanceof View<?>) {

            final View<?> edgeView = (View<?>) edge.getContent();
            final Object definition = edgeView.getDefinition();

            if (definition instanceof Association) {
                final Association association = (Association) definition;
                return association.getId().getValue();
            }
        }
        return edge.getUUID();
    }

    @SuppressWarnings("unchecked")
    public JSITDRGElement stunnerToDMN(final Node<?, ?> node,
                                       final Consumer<JSITComponentWidths> componentWidthsConsumer) {
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
                throw new UnsupportedOperationException("Unsupported View type [" + view.getDefinition().getClass().getName() + "]");
            }
        }
        throw new RuntimeException("Wrong diagram structure to marshall");
    }
}
