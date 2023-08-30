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

package org.kie.workbench.common.dmn.client.graph;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.session.NodeTextSetter;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;

@ApplicationScoped
public class DMNElementsSynchronizer {

    private final DMNDiagramsSession dmnDiagramsSession;
    private final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;
    private final DMNGraphUtils graphUtils;
    private final NodeTextSetter nodeTextSetter;

    @Inject
    public DMNElementsSynchronizer(final DMNDiagramsSession dmnDiagramsSession,
                                   final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent,
                                   final DMNGraphUtils graphUtils,
                                   final NodeTextSetter nodeTextSetter) {
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.refreshDecisionComponentsEvent = refreshDecisionComponentsEvent;
        this.graphUtils = graphUtils;
        this.nodeTextSetter = nodeTextSetter;
    }

    public void onExpressionEditorChanged(final @Observes ExpressionEditorChanged event) {
        final Optional<Node> node = getNode(event.getNodeUUID());
        synchronizeFromNode(node);
    }

    public void onPropertyChanged(final @Observes FormFieldChanged event) {
        final Optional<Node> node = getNode(event.getUuid());
        synchronizeFromNode(node);
    }

    public void synchronizeElementsFrom(final DRGElement drgElement) {
        final String contentDefinitionId = drgElement.getContentDefinitionId();
        final List<Node> nodes = getElementsWithContentId(contentDefinitionId);
        for (final Node node : nodes) {
            updateText(drgElement, node);
            final DRGElement element = getDRGElementFromContentDefinition(node);
            synchronizeBaseDRGProperties(drgElement, element);
            synchronizeSpecializedProperties(drgElement, element);
        }
        refreshDecisionComponentsEvent.fire(new RefreshDecisionComponents());
    }

    void synchronizeSpecializedProperties(final DRGElement drgElement,
                                          final DRGElement element) {
        if (element instanceof Decision) {
            synchronizeDecisionNode((Decision) drgElement, (Decision) element);
        } else if (element instanceof BusinessKnowledgeModel) {
            synchronizeBusinessKnowledgeModelNode((BusinessKnowledgeModel) drgElement, (BusinessKnowledgeModel) element);
        } else if (element instanceof DecisionService) {
            synchronizeDecisionServiceNode((DecisionService) drgElement, (DecisionService) element);
        } else if (element instanceof InputData) {
            synchronizeInputDataNode((InputData) drgElement, (InputData) element);
        } else if (element instanceof KnowledgeSource) {
            synchronizeKnowledgeSourceNode((KnowledgeSource) drgElement, (KnowledgeSource) element);
        }
    }

    void synchronizeBaseDRGProperties(final DRGElement from,
                                      final DRGElement to) {
        to.setDescription(from.getDescription());
        to.setLinksHolder(from.getLinksHolder());
        to.setName(from.getName());
    }

    void synchronizeKnowledgeSourceNode(final KnowledgeSource from,
                                        final KnowledgeSource to) {
        to.setType(from.getType());
        to.setLocationURI(from.getLocationURI());
    }

    void synchronizeInputDataNode(final InputData from,
                                  final InputData to) {
        to.setVariable(from.getVariable());
    }

    void synchronizeDecisionServiceNode(final DecisionService from,
                                        final DecisionService to) {
        to.setVariable(from.getVariable());
    }

    void synchronizeBusinessKnowledgeModelNode(final BusinessKnowledgeModel from,
                                               final BusinessKnowledgeModel to) {
        to.setVariable(from.getVariable());
    }

    void synchronizeDecisionNode(final Decision from,
                                 final Decision to) {
        to.setQuestion(from.getQuestion());
        to.setAllowedAnswers(from.getAllowedAnswers());
        to.setExpression(from.getExpression());
        to.setVariable(from.getVariable());
    }

    void updateText(final DRGElement from,
                    final Node to) {
        final String name = from.getName().getValue();
        nodeTextSetter.setText(name, to);
    }

    List<Node> getElementsWithContentId(final String contentDefinitionId) {
        final List<Node> allNodes = dmnDiagramsSession.getAllNodes();
        return allNodes
                .stream()
                .filter(node -> definitionContainsDRGElement(node)
                        && Objects.equals(getDRGElementFromContentDefinition(node).getContentDefinitionId(), contentDefinitionId))
                .collect(Collectors.toList());
    }

    boolean definitionContainsDRGElement(final Node node) {
        return node.getContent() instanceof Definition
                && ((Definition) node.getContent()).getDefinition() instanceof DRGElement;
    }

    DRGElement getDRGElementFromContentDefinition(final Node node) {
        return ((DRGElement) ((Definition) node.getContent()).getDefinition());
    }

    Optional<Node> getNode(final String nodeUUID) {
        return graphUtils
                .getNodeStream()
                .filter(node -> Objects.equals(node.getUUID(), nodeUUID))
                .findFirst();
    }

    public void synchronizeFromNode(final Optional<Node> node) {
        node.ifPresent(n -> {
            if (n.getContent() instanceof Definition) {
                final Definition definition = (Definition) n.getContent();
                if (definition.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) definition.getDefinition();
                    synchronizeElementsFrom(drgElement);
                }
            }
        });
    }
}
