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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNElementReference;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionService;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.DecisionServiceRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class DecisionServiceConverter implements NodeConverter<org.kie.dmn.model.api.DecisionService, org.kie.workbench.common.dmn.api.definition.v1_1.DecisionService> {

    private FactoryManager factoryManager;

    public DecisionServiceConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<DecisionService>, ?> nodeFromDMN(final org.kie.dmn.model.api.DecisionService dmn,
                                                      final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        @SuppressWarnings("unchecked")
        final Node<View<DecisionService>, ?> node = (Node<View<DecisionService>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                               getDefinitionId(DecisionService.class)).asNode();
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Name name = new Name(dmn.getName());
        final InformationItemPrimary informationItem = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn.getVariable(), dmn);
        final List<DMNElementReference> outputDecision = dmn.getOutputDecision().stream().map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        final List<DMNElementReference> encapsulatedDecision = dmn.getEncapsulatedDecision().stream().map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        final List<DMNElementReference> inputDecision = dmn.getInputDecision().stream().map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        final List<DMNElementReference> inputData = dmn.getInputData().stream().map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        final DecisionService decisionService = new DecisionService(id,
                                                                    description,
                                                                    name,
                                                                    informationItem,
                                                                    outputDecision,
                                                                    encapsulatedDecision,
                                                                    inputDecision,
                                                                    inputData,
                                                                    new BackgroundSet(),
                                                                    new FontSet(),
                                                                    new DecisionServiceRectangleDimensionsSet(),
                                                                    new DecisionServiceDividerLineY());
        node.getContent().setDefinition(decisionService);

        if (informationItem != null) {
            informationItem.setParent(decisionService);
        }

        DMNExternalLinksToExtensionElements.loadExternalLinksFromExtensionElements(dmn, decisionService);

        return node;
    }

    @Override
    @SuppressWarnings("unchecked")
    public org.kie.dmn.model.api.DecisionService dmnFromNode(final Node<View<DecisionService>, ?> node,
                                                             final Consumer<ComponentWidths> componentWidthsConsumer) {
        final DecisionService source = node.getContent().getDefinition();
        final org.kie.dmn.model.api.DecisionService ds = new org.kie.dmn.model.v1_2.TDecisionService();
        ds.setId(source.getId().getValue());
        ds.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        ds.setName(source.getName().getValue());
        final org.kie.dmn.model.api.InformationItem variable = InformationItemPrimaryPropertyConverter.dmnFromWB(source.getVariable(), source);
        if (variable != null) {
            variable.setParent(ds);
        }
        ds.setVariable(variable);

        final List<org.kie.dmn.model.api.DMNElementReference> existing_outputDecision = source.getOutputDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<org.kie.dmn.model.api.DMNElementReference> existing_encapsulatedDecision = source.getEncapsulatedDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<org.kie.dmn.model.api.DMNElementReference> existing_inputDecision = source.getInputDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<org.kie.dmn.model.api.DMNElementReference> existing_inputData = source.getInputData().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<org.kie.dmn.model.api.DMNElementReference> candidate_outputDecision = new ArrayList<>();
        final List<org.kie.dmn.model.api.DMNElementReference> candidate_encapsulatedDecision = new ArrayList<>();
        final List<org.kie.dmn.model.api.DMNElementReference> candidate_inputDecision = new ArrayList<>();
        final List<org.kie.dmn.model.api.DMNElementReference> candidate_inputData = new ArrayList<>();

        final List<InputData> reqInputs = new ArrayList<>();
        final List<Decision> reqDecisions = new ArrayList<>();
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
                        final org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(decision.getId().getValue()).toString());
                        if (isOutputDecision(targetNode.getContent(), node.getContent())) {
                            candidate_outputDecision.add(ri);
                        } else {
                            candidate_encapsulatedDecision.add(ri);
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
                    final org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                    ri.setHref(new StringBuilder("#").append(x.getId().getValue()).toString());
                    return ri;
                })
                .forEach(candidate_inputData::add);
        reqDecisions.stream()
                .sorted(Comparator.comparing(x -> x.getName().getValue()))
                .map(x -> {
                    final org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                    ri.setHref(new StringBuilder("#").append(x.getId().getValue()).toString());
                    return ri;
                })
                .forEach(candidate_inputDecision::add);
        for (org.kie.dmn.model.api.DMNElementReference er : candidate_outputDecision) {
            candidate_inputDecision.removeIf(x -> x.getHref().equals(er.getHref()));
        }
        for (org.kie.dmn.model.api.DMNElementReference er : candidate_encapsulatedDecision) {
            candidate_inputDecision.removeIf(x -> x.getHref().equals(er.getHref()));
        }

        reconcileExistingAndCandidate(ds.getInputData(), existing_inputData, candidate_inputData);
        reconcileExistingAndCandidate(ds.getInputDecision(), existing_inputDecision, candidate_inputDecision);
        reconcileExistingAndCandidate(ds.getEncapsulatedDecision(), existing_encapsulatedDecision, candidate_encapsulatedDecision);
        reconcileExistingAndCandidate(ds.getOutputDecision(), existing_outputDecision, candidate_outputDecision);

        DMNExternalLinksToExtensionElements.loadExternalLinksIntoExtensionElements(source, ds);

        return ds;
    }

    private void reconcileExistingAndCandidate(final List<org.kie.dmn.model.api.DMNElementReference> targetList,
                                               final List<org.kie.dmn.model.api.DMNElementReference> existingList,
                                               final List<org.kie.dmn.model.api.DMNElementReference> candidateList) {
        final List<org.kie.dmn.model.api.DMNElementReference> existing = new ArrayList<>(existingList);
        final List<org.kie.dmn.model.api.DMNElementReference> candidate = new ArrayList<>(candidateList);
        for (org.kie.dmn.model.api.DMNElementReference e : existing) {
            boolean existingIsAlsoCandidate = candidate.removeIf(er -> er.getHref().equals(e.getHref()));
            if (existingIsAlsoCandidate) {
                targetList.add(e);
            }
        }
        for (org.kie.dmn.model.api.DMNElementReference c : candidate) {
            targetList.add(c);
        }
    }

    @SuppressWarnings("unchecked")
    private void inspectDecisionForDSReqs(final Node<View<?>, ?> targetNode,
                                          final List<InputData> reqInputs,
                                          final List<Decision> reqDecisions) {
        final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) targetNode.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            final Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) view.getDefinition();
                    if (drgElement instanceof Decision) {
                        reqDecisions.add((Decision) drgElement);
                    } else if (drgElement instanceof InputData) {
                        reqInputs.add((InputData) drgElement);
                    }
                }
            }
        }
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
}