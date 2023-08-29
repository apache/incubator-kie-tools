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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.DMNElementReference;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.property.dimensions.DecisionServiceRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntry;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class DecisionServiceConverter implements NodeConverter<JSITDecisionService, DecisionService> {

    private FactoryManager factoryManager;

    private DMNDiagramsSession diagramsSession;

    public DecisionServiceConverter(final FactoryManager factoryManager,
                                    final DMNDiagramsSession diagramsSession) {
        super();
        this.factoryManager = factoryManager;
        this.diagramsSession = diagramsSession;
    }

    private static boolean isOutputDecision(final View<?> childView,
                                            final View<DecisionService> decisionServiceView) {
        //ChildViewY is absolute
        //DecisionServiceViewY is absolute
        //DecisionServiceViewLineY is relative to the DecisionService
        final double childViewY = childView.getBounds().getUpperLeft().getY();
        final double decisionServiceViewY = decisionServiceView.getBounds().getUpperLeft().getY();
        final double decisionServiceViewLineY = decisionServiceView.getDefinition().getDividerLineY().getValue();
        return childViewY < decisionServiceViewY + decisionServiceViewLineY;
    }

    @Override
    public Node<View<DecisionService>, ?> nodeFromDMN(final NodeEntry nodeEntry) {

        final JSITDecisionService dmn = Js.uncheckedCast(nodeEntry.getDmnElement());

        @SuppressWarnings("unchecked")
        final Node<View<DecisionService>, ?> node = (Node<View<DecisionService>, ?>) factoryManager.newElement(nodeEntry.getId(),
                                                                                                               getDefinitionId(DecisionService.class)).asNode();
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Name name = new Name(dmn.getName());
        final InformationItemPrimary informationItem = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn.getVariable(), dmn);

        final List<DMNElementReference> outputDecision = new ArrayList<>();
        final List<DMNElementReference> encapsulatedDecision = new ArrayList<>();
        final List<DMNElementReference> inputDecision = new ArrayList<>();
        final List<DMNElementReference> inputData = new ArrayList<>();

        final List<JSITDMNElementReference> jsiOutputDecisions = dmn.getOutputDecision();
        if (Objects.nonNull(jsiOutputDecisions)) {
            for (int i = 0; i < jsiOutputDecisions.size(); i++) {
                final JSITDMNElementReference jsiOutputDecision = Js.uncheckedCast(jsiOutputDecisions.get(i));
                outputDecision.add(DMNElementReferenceConverter.wbFromDMN(jsiOutputDecision));
            }
        }

        final List<JSITDMNElementReference> jsiEncapsulatedDecisions = dmn.getEncapsulatedDecision();
        if (Objects.nonNull(jsiEncapsulatedDecisions)) {
            for (int i = 0; i < jsiEncapsulatedDecisions.size(); i++) {
                final JSITDMNElementReference jsiEncapsulatedDecision = Js.uncheckedCast(jsiEncapsulatedDecisions.get(i));
                encapsulatedDecision.add(DMNElementReferenceConverter.wbFromDMN(jsiEncapsulatedDecision));
            }
        }

        final List<JSITDMNElementReference> jsiInputDecisions = dmn.getInputDecision();
        if (Objects.nonNull(jsiInputDecisions)) {
            for (int i = 0; i < jsiInputDecisions.size(); i++) {
                final JSITDMNElementReference jsiInputDecision = Js.uncheckedCast(jsiInputDecisions.get(i));
                inputDecision.add(DMNElementReferenceConverter.wbFromDMN(jsiInputDecision));
            }
        }

        final List<JSITDMNElementReference> jsiInputDatas = dmn.getInputData();
        if (Objects.nonNull(jsiInputDatas)) {
            for (int i = 0; i < jsiInputDatas.size(); i++) {
                final JSITDMNElementReference jsiInputData = Js.uncheckedCast(jsiInputDatas.get(i));
                inputData.add(DMNElementReferenceConverter.wbFromDMN(jsiInputData));
            }
        }

        final DecisionService decisionService = new DecisionService(id,
                                                                    description,
                                                                    name,
                                                                    informationItem,
                                                                    outputDecision,
                                                                    encapsulatedDecision,
                                                                    inputDecision,
                                                                    inputData,
                                                                    new StylingSet(),
                                                                    new DecisionServiceRectangleDimensionsSet(),
                                                                    new DecisionServiceDividerLineY());
        decisionService.setDiagramId(nodeEntry.getDiagramId());
        node.getContent().setDefinition(decisionService);

        if (Objects.nonNull(informationItem)) {
            informationItem.setParent(decisionService);
        }

        DMNExternalLinksToExtensionElements.loadExternalLinksFromExtensionElements(dmn, decisionService);

        return node;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSITDecisionService dmnFromNode(final Node<View<DecisionService>, ?> node,
                                           final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final DecisionService source = (DecisionService) DefinitionUtils.getElementDefinition(node);
        final JSITDecisionService ds = JSITDecisionService.newInstance();
        ds.setId(source.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        description.ifPresent(ds::setDescription);
        ds.setName(source.getName().getValue());
        final JSITInformationItem variable = InformationItemPrimaryPropertyConverter.dmnFromWB(source.getVariable(), source);
        ds.setVariable(variable);

        final List<JSITDMNElementReference> existing_outputDecision = source.getOutputDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<JSITDMNElementReference> existing_encapsulatedDecision = source.getEncapsulatedDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<JSITDMNElementReference> existing_inputDecision = source.getInputDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<JSITDMNElementReference> existing_inputData = source.getInputData().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<JSITDMNElementReference> candidate_outputDecision = new ArrayList<>();
        final List<JSITDMNElementReference> candidate_encapsulatedDecision = new ArrayList<>();
        final List<JSITDMNElementReference> candidate_inputDecision = new ArrayList<>();
        final List<JSITDMNElementReference> candidate_inputData = new ArrayList<>();

        final HashSet<InputData> reqInputs = new HashSet();
        final HashSet<Decision> reqDecisions = new HashSet();

        // DMN spec table 2: Requirements connection rules
        final List<Edge<?, ?>> outEdges = (List<Edge<?, ?>>) node.getOutEdges();
        for (Edge<?, ?> e : outEdges) {
            if (e.getContent() instanceof Child) {
                @SuppressWarnings("unchecked")
                final Node<View<?>, ?> targetNode = e.getTargetNode();
                final View<?> targetNodeView = targetNode.getContent();
                if (targetNodeView.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) targetNodeView.getDefinition();
                    if (drgElement instanceof Decision) {
                        final Decision decision = (Decision) drgElement;
                        final JSITDMNElementReference ri = JSITDMNElementReference.newInstance();
                        ri.setHref("#" + decision.getId().getValue());
                        if (isOutputDecision(targetNode.getContent(), node.getContent())) {
                            candidate_outputDecision.add(ri);
                        } else {
                            candidate_encapsulatedDecision.add(ri);

                            final List<Node> all = diagramsSession.getNodesFromAllDiagramsWithContentId(drgElement.getContentDefinitionId());
                            for (final Node other : all) {
                                if (!Objects.equals(other, targetNode)) {
                                    inspectDecisionForDSReqs(other, reqInputs, reqDecisions);
                                }
                            }
                        }

                        inspectDecisionForDSReqs(targetNode, reqInputs, reqDecisions);
                    } else {
                        throw new UnsupportedOperationException("wrong model definition: a DecisionService is expected to encapsulate only Decision");
                    }
                }
            } else if (e.getContent() instanceof View && ((View) e.getContent()).getDefinition() instanceof KnowledgeRequirement) {
                // this was taken care by the receiving Decision or BKM.
            } else {
                throw new UnsupportedOperationException("wrong model definition.");
            }
        }
        reqInputs.stream()
                .sorted(Comparator.comparing(x -> x.getName().getValue()))
                .map(x -> {
                    final JSITDMNElementReference ri = JSITDMNElementReference.newInstance();
                    ri.setHref("#" + x.getId().getValue());
                    return ri;
                })
                .forEach(ri -> candidate_inputData.add(Js.uncheckedCast(ri)));
        reqDecisions.stream()
                .sorted(Comparator.comparing(x -> x.getName().getValue()))
                .map(x -> {
                    final JSITDMNElementReference ri = JSITDMNElementReference.newInstance();
                    ri.setHref("#" + x.getId().getValue());
                    return ri;
                })
                .forEach(rs -> candidate_inputDecision.add(Js.uncheckedCast(rs)));
        for (int i = 0; i < candidate_outputDecision.size(); i++) {
            final JSITDMNElementReference er = Js.uncheckedCast(candidate_outputDecision.get(i));
            candidate_inputDecision.removeIf(x -> x.getHref().equals(er.getHref()));
        }
        for (int i = 0; i < candidate_encapsulatedDecision.size(); i++) {
            final JSITDMNElementReference er = Js.uncheckedCast(candidate_encapsulatedDecision.get(i));
            candidate_inputDecision.removeIf(x -> x.getHref().equals(er.getHref()));
        }

        ds.setInputData(reconcileExistingAndCandidate(existing_inputData, candidate_inputData));
        ds.setInputDecision(reconcileExistingAndCandidate(existing_inputDecision, candidate_inputDecision));
        ds.setEncapsulatedDecision(reconcileExistingAndCandidate(existing_encapsulatedDecision, candidate_encapsulatedDecision));
        ds.setOutputDecision(reconcileExistingAndCandidate(existing_outputDecision, candidate_outputDecision));

        DMNExternalLinksToExtensionElements.loadExternalLinksIntoExtensionElements(source, ds);

        return ds;
    }

    private List<JSITDMNElementReference> reconcileExistingAndCandidate(final List<JSITDMNElementReference> existingList,
                                                                        final List<JSITDMNElementReference> candidateList) {
        final List<JSITDMNElementReference> targetList = new ArrayList<>();
        final List<JSITDMNElementReference> existing = new ArrayList<>(existingList);
        final List<JSITDMNElementReference> candidate = new ArrayList<>(candidateList);
        for (int i = 0; i < existing.size(); i++) {
            final JSITDMNElementReference e = Js.uncheckedCast(existing.get(i));
            final boolean existingIsAlsoCandidate = candidate.removeIf(er -> er.getHref().equals(e.getHref()));
            if (existingIsAlsoCandidate) {
                targetList.add(Js.uncheckedCast(e));
            }
        }
        for (int i = 0; i < candidate.size(); i++) {
            final JSITDMNElementReference c = Js.uncheckedCast(candidate.get(i));
            targetList.add(c);
        }
        return targetList;
    }

    @SuppressWarnings("unchecked")
    private void inspectDecisionForDSReqs(final Node<View<?>, ?> targetNode,
                                          final HashSet<InputData> reqInputs,
                                          final HashSet<Decision> reqDecisions) {
        final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) targetNode.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            final Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) view.getDefinition();
                    if (drgElement instanceof Decision) {
                        if (!reqDecisions.contains(drgElement)) {
                            reqDecisions.add((Decision) drgElement);
                        }
                    } else if (drgElement instanceof InputData) {
                        if (!reqInputs.contains(drgElement)) {
                            reqInputs.add((InputData) drgElement);
                        }
                    }
                }
            }
        }
    }
}
