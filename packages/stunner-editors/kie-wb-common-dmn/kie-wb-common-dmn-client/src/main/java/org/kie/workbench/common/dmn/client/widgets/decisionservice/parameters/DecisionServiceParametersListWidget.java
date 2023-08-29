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

package org.kie.workbench.common.dmn.client.widgets.decisionservice.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceParametersList;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.widgets.decisionservice.parameters.parametergroup.ParameterGroup;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.kie.workbench.common.dmn.client.editors.common.RemoveHelper.removeChildren;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionServiceParameters_EncapsulatedDecisions;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionServiceParameters_Inputs;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionServiceParameters_Outputs;

@Dependent
@Templated
public class DecisionServiceParametersListWidget extends Composite implements HasValue<DecisionServiceParametersList>,
                                                                              HasEnabled {

    private final ClientTranslationService translationService;

    private final DMNDiagramsSession dmnDiagramsSession;

    private final Elemental2DomUtil util;

    private final ParameterGroup groupEncapsulated;

    private final ParameterGroup groupOutputs;

    private final ParameterGroup groupInputs;

    @DataField("decision-service-parameters-widget-container")
    private final HTMLDivElement container;

    private boolean enabled;

    private DecisionServiceParametersList value;

    @Inject
    public DecisionServiceParametersListWidget(final ClientTranslationService translationService,
                                               final Elemental2DomUtil util,
                                               final DMNDiagramsSession dmnDiagramsSession,
                                               final ParameterGroup groupEncapsulated,
                                               final ParameterGroup groupOutputs,
                                               final ParameterGroup groupInputs,
                                               final HTMLDivElement container) {

        this.value = new DecisionServiceParametersList();
        this.enabled = true;
        this.container = container;
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.util = util;
        this.groupEncapsulated = groupEncapsulated;
        this.groupOutputs = groupOutputs;
        this.groupInputs = groupInputs;
        this.translationService = translationService;
    }

    @PostConstruct
    public void setup() {
        groupEncapsulated.setHeader(translationService.getValue(DecisionServiceParameters_EncapsulatedDecisions));
        groupOutputs.setHeader(translationService.getValue(DecisionServiceParameters_Outputs));
        groupInputs.setHeader(translationService.getValue(DecisionServiceParameters_Inputs));
    }

    @Override
    public DecisionServiceParametersList getValue() {
        return value;
    }

    @Override
    public void setValue(final DecisionServiceParametersList documentationLinks) {
        setValue(documentationLinks, false);
    }

    @Override
    public void setValue(final DecisionServiceParametersList decisionServiceParametersList,
                         final boolean fireEvents) {
        value = decisionServiceParametersList;
        refresh();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    void refresh() {

        clear();

        final Node node = getNode(getValue().getDecisionService().getContentDefinitionId());
        final List<Edge<?, ?>> outEdges = (List<Edge<?, ?>>) node.getOutEdges();
        final List<InputData> inputs = new ArrayList<>();

        outEdges.stream()
                .filter(e -> e.getContent() instanceof Child)
                .forEach(e -> getTargetDRGElement(e).ifPresent(drgElement -> {
                    final Node<View<?>, ?> targetNode = e.getTargetNode();
                    loadDecisionsFromNode(node, targetNode);
                    loadInputsFromNode(inputs, targetNode);
                    loadInputsFromOthersDiagrams(inputs, targetNode);
                }));

        loadInputsParameters(getSortedInputs(inputs));
        loadGroupsElements();
    }

    void loadInputsFromOthersDiagrams(final List<InputData> inputs,
                                      final Node<View<?>, ?> targetNode) {
        final List<Node> allNodes = dmnDiagramsSession.getNodesFromAllDiagramsWithContentId(getDRGElementFromContentDefinition(targetNode).getContentDefinitionId());
        for (final Node n : allNodes) {
            loadInputsFromNode(inputs, n);
        }
    }

    /**
     * Sort the InputData list based on the order of the input nodes in Decision Service and new items in alphabetical order.
     *
     * @param inputs The unsorted list of InputData.
     * @return The sorted list.
     */
    List<InputData> getSortedInputs(final List<InputData> inputs) {

        final List<InputData> currentItems = getCurrentItems(inputs);
        final List<InputData> newItems = getNewItems(inputs, currentItems);

        currentItems.addAll(newItems);

        return currentItems;
    }

    List<InputData> getCurrentItems(final List<InputData> inputs) {
        final List<InputData> sorted = new ArrayList<>();

        getValue().getDecisionService().getInputData().forEach(ref -> {
            final String href = ref.getHref().replace("#", "");
            final Optional<InputData> currentInput = inputs.stream()
                    .filter(input -> Objects.equals(input.getId().getValue(), href))
                    .findFirst();
            currentInput.ifPresent(inputData -> sorted.add(inputData));
        });
        return sorted;
    }

    List<InputData> getNewItems(final List<InputData> inputs,
                                final List<InputData> currentItems) {

        // The marshaller also sorts new items in alphabetical order
        final List<InputData> newItems = inputs.stream()
                .filter(item -> !currentItems.contains(item))
                .sorted(comparing(x -> x.getName().getValue()))
                .collect(toList());

        return newItems;
    }

    void loadDecisionsFromNode(final Node node,
                               final Node<View<?>, ?> targetNode) {
        if (isOutputDecision(targetNode.getContent(), (View<DecisionService>) node.getContent())) {
            addDecisionNodeToGroup(groupOutputs, targetNode);
        } else {
            addDecisionNodeToGroup(groupEncapsulated, targetNode);
        }
    }

    void loadGroupsElements() {
        container.appendChild(util.asHTMLElement(groupInputs.getElement()));
        container.appendChild(util.asHTMLElement(groupEncapsulated.getElement()));
        container.appendChild(util.asHTMLElement(groupOutputs.getElement()));
    }

    void loadInputsParameters(final List<InputData> inputs) {
        for (final InputData input : inputs) {
            final InformationItemPrimary variable = input.getVariable();
            final String name = input.getName().getValue();
            final String type = variable.getTypeRef().getLocalPart();
            groupInputs.addParameter(name, type);
        }
    }

    Optional<DRGElement> getTargetDRGElement(final Edge<?, ?> e) {
        final Node<View<?>, ?> targetNode = e.getTargetNode();
        final View<?> targetNodeView = targetNode.getContent();
        if (targetNodeView.getDefinition() instanceof DRGElement) {
            return Optional.of((DRGElement) targetNodeView.getDefinition());
        }
        return Optional.empty();
    }

    void clear() {
        removeChildren(container);
        this.groupEncapsulated.clear();
        this.groupInputs.clear();
        this.groupOutputs.clear();
    }

    Node getNode(final String contentId) {
        final Stream<Node> stream = StreamSupport.stream(
                dmnDiagramsSession.getCurrentGraphDiagram().getGraph().nodes().spliterator(),
                false);

        return getElementWithContentId(contentId, stream);
    }

    void loadInputsFromNode(final List<InputData> inputs,
                            final Node<View<?>, ?> targetNode) {
        final List<InputData> nodeInputs = getInputs(targetNode);
        for (final InputData input : nodeInputs) {
            if (inputs.stream().noneMatch(i -> Objects.equals(i.getId().getValue(), input.getId().getValue()))) {
                inputs.add(input);
            }
        }
    }

    void addDecisionNodeToGroup(final ParameterGroup group,
                                final Node<View<?>, ?> node) {
        final Decision decision = (Decision) ((Definition) node.getContent()).getDefinition();
        final InformationItemPrimary variable = decision.getVariable();
        final String name = decision.getName().getValue();
        final String type = variable.getTypeRef().getLocalPart();
        group.addParameter(name, type);
    }

    @SuppressWarnings("unchecked")
    List<InputData> getInputs(final Node<View<?>, ?> targetNode) {
        final List<InputData> inputs = new ArrayList<>();
        final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) targetNode.getInEdges();
        for (final Edge<?, ?> e : inEdges) {
            getSourceNodeInputData(e).ifPresent(inputs::add);
        }

        return inputs;
    }

    Optional<InputData> getSourceNodeInputData(final Edge<?, ?> edge) {
        final Node<?, ?> sourceNode = edge.getSourceNode();
        if (sourceNode.getContent() instanceof View<?>) {
            final View<?> view = (View<?>) sourceNode.getContent();
            if (view.getDefinition() instanceof DRGElement) {
                final DRGElement drgElement = (DRGElement) view.getDefinition();
                if (drgElement instanceof InputData) {
                    return Optional.of((InputData) drgElement);
                }
            }
        }

        return Optional.empty();
    }

    boolean isOutputDecision(final View<?> childView,
                             final View<DecisionService> decisionServiceView) {
        final double childViewY = childView.getBounds().getUpperLeft().getY();
        final double decisionServiceViewLineY = decisionServiceView.getDefinition()
                .getDividerLineY()
                .getValue();
        return childViewY < decisionServiceViewLineY;
    }

    Node getElementWithContentId(final String contentDefinitionId,
                                 final Stream<Node> stream) {
        return stream
                .filter(node -> definitionContainsDRGElement(node)
                        && Objects.equals(getDRGElementFromContentDefinition(node).getContentDefinitionId(), contentDefinitionId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Decision Service for contentDefinitionId '" + contentDefinitionId + "' not found."));
    }

    private boolean definitionContainsDRGElement(final Node node) {
        return node.getContent() instanceof Definition
                && ((Definition) node.getContent()).getDefinition() instanceof DRGElement;
    }

    private DRGElement getDRGElementFromContentDefinition(final Node node) {
        return ((DRGElement) ((Definition) node.getContent()).getDefinition());
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<DecisionServiceParametersList> valueChangeHandler) {
        return addHandler(valueChangeHandler,
                          ValueChangeEvent.getType());
    }
}
