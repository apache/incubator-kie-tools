/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.ActivityRef;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.SafeComparator;
import org.uberfire.commons.data.Pair;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class ProcessCompensationRefProvider implements SelectorDataProvider {

    private final SessionManager sessionManager;

    @Inject
    public ProcessCompensationRefProvider(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SelectorData getSelectorData(FormRenderingContext context) {
        final Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        final String rootUUID = diagram.getMetadata().getCanvasRootUUID();
        final Node<?, ? extends Edge> selectedNode = getSelectedElement(diagram, sessionManager.getCurrentSession());
        final Map<Object, String> values = new TreeMap<>(SafeComparator.TO_STRING_COMPARATOR);
        if (selectedNode != null) {
            Node<?, ? extends Edge> currentNode = selectedNode;
            final List<Node> compensableNodes = new ArrayList<>();
            Node<?, ? extends Edge> parentNode;
            int levels = 0;
            do {
                parentNode = GraphUtils.getParent(currentNode).asNode();
                compensableNodes.addAll(getCompensableNodes(parentNode));

                if (rootUUID.equals(parentNode.getUUID())) {
                    levels = 2;
                } else if (isSubProcess(parentNode)) {
                    currentNode = parentNode;
                    levels++;
                } else if (isLane(parentNode)) {
                    currentNode = parentNode;
                }
            } while (levels < 2);

            compensableNodes.stream()
                    .map(node -> buildPair(node.getUUID(), (BPMNDefinition) (((View) node.getContent()).getDefinition())))
                    .forEach(pair -> values.put(pair.getK1(), pair.getK2()));

            ActivityRef currentActivityRef = null;
            if (isEndCompensationEvent(selectedNode)) {
                currentActivityRef = ((EndCompensationEvent) ((View)selectedNode.getContent()).getDefinition()).getExecutionSet().getActivityRef();
            } else if (isIntermediateCompensationEventThrowing(selectedNode)) {
                currentActivityRef = ((IntermediateCompensationEventThrowing) ((View)selectedNode.getContent()).getDefinition()).getExecutionSet().getActivityRef();
            }
            if (currentActivityRef != null && !isEmpty(currentActivityRef.getValue()) && !values.containsKey(currentActivityRef.getValue())) {
                Node configured = diagram.getGraph().getNode(currentActivityRef.getValue());
                if (configured != null) {
                    Pair<Object, String> pair = buildPair(configured.getUUID(), (BPMNDefinition)((View)configured.getContent()).getDefinition());
                    values.put(pair.getK1(), pair.getK2());
                }
            }
        }
        return new SelectorData(values,
                                null);
    }

    @SuppressWarnings("unchecked")
    private List<Node> getCompensableNodes(final Node<?, ? extends Edge> parent) {
        List<Node> result = GraphUtils.getChildNodes(parent)
                .stream()
                .filter(ProcessCompensationRefProvider::isCompensable)
                .collect(Collectors.toList());

        GraphUtils.getChildNodes(parent).stream()
                .filter(ProcessCompensationRefProvider::isLane)
                .forEach(lane -> result.addAll(getCompensableNodes(lane)));
        return result;
    }

    @SuppressWarnings("unchecked")
    private Node getSelectedElement(Diagram diagram, ClientSession clientSession) {
        SelectionControl selectionControl = null;
        if (clientSession instanceof EditorSession) {
            selectionControl = ((EditorSession) clientSession).getSelectionControl();
        } else if (clientSession instanceof ViewerSession) {
            selectionControl = ((ViewerSession) clientSession).getSelectionControl();
        }

        if (selectionControl != null) {
            final Collection<String> selectedItems = selectionControl.getSelectedItems();
            if (selectedItems != null && !selectedItems.isEmpty()) {
                String selectedElement = selectedItems.iterator().next();
                return diagram.getGraph().getNode(selectedElement);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static boolean isCompensable(final Node node) {
        return (isTask(node) || isSubProcess(node)) && !isCompensationTarget(node);
    }

    private static boolean isCompensationTarget(final Node<?, ? extends Edge> node) {
        return node.getInEdges().stream()
                .filter(edge -> edge.getSourceNode().getContent() instanceof View &&
                        ((View) edge.getSourceNode().getContent()).getDefinition() instanceof IntermediateCompensationEvent)
                .findFirst()
                .isPresent();
    }

    private static boolean isLane(final Node node) {
        return node.getContent() instanceof View && ((View) node.getContent()).getDefinition() instanceof Lane;
    }

    private static boolean isEndCompensationEvent(final Node node) {
        return node.getContent() instanceof View && ((View) node.getContent()).getDefinition() instanceof EndCompensationEvent;
    }

    private static boolean isIntermediateCompensationEventThrowing(final Node node) {
        return node.getContent() instanceof View && ((View) node.getContent()).getDefinition() instanceof IntermediateCompensationEventThrowing;
    }

    private static boolean isSubProcess(final Node node) {
        return node.getContent() instanceof View && ((View) node.getContent()).getDefinition() instanceof BaseSubprocess;
    }

    private static boolean isTask(final Node node) {
        return node.getContent() instanceof View && ((View) node.getContent()).getDefinition() instanceof BaseTask;
    }

    private static Pair<Object, String> buildPair(final String uuid, final BPMNDefinition definition) {
        String name = definition.getGeneral().getName().getValue();
        return new Pair<>(uuid, isEmpty(name) ? uuid : name);
    }
}