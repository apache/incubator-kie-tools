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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

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
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.AssociationConverter;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.BusinessKnowledgeModelConverter;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.DecisionConverter;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.DecisionServiceConverter;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.DefinitionsConverter;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.InputDataConverter;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.KnowledgeSourceConverter;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.TextAnnotationConverter;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.dd.PointUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.utils.DMNMarshallerUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.utils.WrapperUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIDiagramElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBusinessKnowledgeModel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeSource;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDI;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentsWidthsExtension;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.dd.PointUtils.upperLeftBound;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.dd.PointUtils.xOfBound;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.dd.PointUtils.yOfBound;

@ApplicationScoped
public class DMNMarshallerKogitoMarshaller {

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

    protected DMNMarshallerKogitoMarshaller() {
        this(null);
    }

    @Inject
    public DMNMarshallerKogitoMarshaller(final FactoryManager factoryManager) {
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
    // MARSHALL
    // ==================================

    @SuppressWarnings("unchecked")
    public JSITDefinitions marshall(final Graph<?, Node<View, ?>> graph) {
        final Map<String, JSITDRGElement> nodes = new HashMap<>();
        final Map<String, JSITTextAnnotation> textAnnotations = new HashMap<>();
        final Node<View<DMNDiagram>, ?> dmnDiagramRoot = (Node<View<DMNDiagram>, ?>) DMNMarshallerUtils.findDMNDiagramRoot(graph);
        final Definitions definitionsStunnerPojo = ((DMNDiagram) DefinitionUtils.getElementDefinition(dmnDiagramRoot)).getDefinitions();
        final List<JSIDMNEdge> dmnEdges = new ArrayList<>();

        cleanImportedItemDefinitions(definitionsStunnerPojo);

        final JSITDefinitions definitions = DefinitionsConverter.dmnFromWB(definitionsStunnerPojo);
        if (Objects.isNull(definitions.getExtensionElements())) {
            JSITDMNElement.JSIExtensionElements jsiExtensionElements = new JSITDMNElement.JSIExtensionElements();
            definitions.setExtensionElements(jsiExtensionElements);
        }

        if (Objects.isNull(definitions.getDMNDI())) {
            definitions.setDMNDI(new JSIDMNDI());
        }
        final JSIDMNDiagram dmnDDDMNDiagram = new JSIDMNDiagram();
        // TODO {gcardosi} add because  present in original json
        dmnDDDMNDiagram.setDMNDiagramElement(new ArrayList<>());
        definitions.getDMNDI().addDMNDiagram(dmnDDDMNDiagram);

        //Convert relative positioning to absolute
        for (Node<?, ?> node : graph.nodes()) {
            PointUtils.convertToAbsoluteBounds(node);
        }

        //Setup callback for marshalling ComponentWidths
        if (Objects.isNull(dmnDDDMNDiagram.getExtension())) {
            dmnDDDMNDiagram.setExtension(new JSIDiagramElement.JSIExtension());
        }
        final JSITComponentsWidthsExtension componentsWidthsExtension = new JSITComponentsWidthsExtension();
        final JSIDiagramElement.JSIExtension extension = dmnDDDMNDiagram.getExtension();
        JSITComponentsWidthsExtension wrappedComponentsWidthsExtension = WrapperUtils.getWrappedJSITComponentsWidthsExtension(componentsWidthsExtension);
        extension.addAny(wrappedComponentsWidthsExtension);

        final Consumer<JSITComponentWidths> componentWidthsConsumer = (cw) -> componentsWidthsExtension.addComponentWidths(cw);

        //Iterate Graph processing nodes..
        for (Node<?, ?> node : graph.nodes()) {
            if (node.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) node.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) view.getDefinition();
                    if (!drgElement.isAllowOnlyVisualChange()) {
                        nodes.put(drgElement.getId().getValue(),
                                  stunnerToDMN(node,
                                               componentWidthsConsumer));
                    }

                    final String namespaceURI = definitionsStunnerPojo.getDefaultNamespace();
                    dmnDDDMNDiagram.addDMNDiagramElement(WrapperUtils.getWrappedJSIDMNShape((View<? extends DMNElement>) view,
                                                                                            namespaceURI));
                } else if (view.getDefinition() instanceof TextAnnotation) {
                    final TextAnnotation textAnnotation = (TextAnnotation) view.getDefinition();
                    textAnnotations.put(textAnnotation.getId().getValue(),
                                        textAnnotationConverter.dmnFromNode((Node<View<TextAnnotation>, ?>) node,
                                                                            componentWidthsConsumer));
                    final String namespaceURI = definitionsStunnerPojo.getDefaultNamespace();
                    dmnDDDMNDiagram.addDMNDiagramElement(WrapperUtils.getWrappedJSIDMNShape((View<? extends DMNElement>) view,
                                                                                            namespaceURI));

                    final List<JSITAssociation> associations = AssociationConverter.dmnFromWB((Node<View<TextAnnotation>, ?>) node);
                    for (int i = 0; i < associations.size(); i++) {
                        final JSITAssociation wrappedJSITAssociation = WrapperUtils.getWrappedJSITAssociation(Js.uncheckedCast(associations.get(i)));
                        definitions.addArtifact(wrappedJSITAssociation);
                    }
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
                            String uuid = e.getUUID();
                            if (e.getContent() instanceof View<?>) {
                                final View<?> edgeView = (View<?>) e.getContent();
                                if (edgeView.getDefinition() instanceof Association) {
                                    uuid = ((Association) edgeView.getDefinition()).getId().getValue();
                                }
                            }
                            dmnEdge.setId("dmnedge-" + uuid);
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
        }
        nodes.values().forEach(n -> {
            JSINodeLocalPartName localPart = JSINodeLocalPartName.UNKNOWN;
            if (JSITBusinessKnowledgeModel.instanceOf(n)) {
                localPart = JSINodeLocalPartName.BUSINESS_KNOWLEDGE_MODEL;
            } else if (JSITDecision.instanceOf(n)) {
                localPart = JSINodeLocalPartName.DECISION;
            } else if (JSITDecisionService.instanceOf(n)) {
                localPart = JSINodeLocalPartName.DECISION_SERVICE;
            } else if (JSITInputData.instanceOf(n)) {
                localPart = JSINodeLocalPartName.INPUT_DATA;
            } else if (JSITKnowledgeSource.instanceOf(n)) {
                localPart = JSINodeLocalPartName.KNOWLEDGE_SOURCE;
            }
            final JSITDRGElement toAdd = WrapperUtils.getWrappedJSITDRGElement(n, PREFIX, localPart.getLocalPart());
            definitions.addDrgElement(toAdd);
        });
        textAnnotations.values().forEach(text -> {
            final JSITTextAnnotation wrappedText = WrapperUtils.getWrappedJSITTextAnnotation(text);
            definitions.addArtifact(wrappedText);
        });
        for (int i = 0; i < dmnEdges.size(); i++) {
            dmnDDDMNDiagram.addDMNDiagramElement(WrapperUtils.getWrappedJSIDMNEdge(Js.uncheckedCast(dmnEdges.get(i))));
        }
        return definitions;
    }

    void cleanImportedItemDefinitions(final Definitions definitions) {
        definitions.getItemDefinition().removeIf(ItemDefinition::isAllowOnlyVisualChange);
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
        throw new RuntimeException("wrong diagram structure to marshall");
    }
}
