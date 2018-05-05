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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.commons.data.Pair;

public class DefaultRouteFormProvider
        extends AbstractProcessFilteredNodeProvider {

    private final DefinitionManager definitionManager;

    @Inject
    public DefaultRouteFormProvider(final SessionManager sessionManager,
                                    final DefinitionManager definitionManager) {
        super(sessionManager);
        this.definitionManager = definitionManager;
    }

    @Override
    public Predicate<Node> getFilter() {
        //not used in this implementation.
        return node -> true;
    }

    @Override
    public Function<Node, Pair<Object, String>> getMapper() {
        //not used in this implementation.
        return null;
    }

    @Override
    protected Collection<Pair<Object, String>> findElements(Predicate<Node> filter,
                                                            Function<Node, Pair<Object, String>> mapper) {
        Node selectedNode = getSelectedElement();
        Collection<Pair<Object, String>> result = new ArrayList<>();
        if (selectedNode != null) {
            List<Edge> outEdges = selectedNode.getOutEdges();
            if (outEdges != null) {
                result = outEdges.stream()
                        .map(outEdge -> {
                            String routeIdentifier = outEdge.getUUID();
                            // UI value for the route is the target node name or target node type
                            String targetName = null;
                            String targetNodeType = null;
                            BPMNDefinition bpmnDefinition = getEdgeTarget(outEdge);
                            if (bpmnDefinition != null) {
                                targetNodeType = definitionManager.adapters().forDefinition().getTitle(bpmnDefinition);
                                if (bpmnDefinition instanceof BaseStartEvent ||
                                        bpmnDefinition instanceof BaseCatchingIntermediateEvent ||
                                        bpmnDefinition instanceof BaseThrowingIntermediateEvent ||
                                        bpmnDefinition instanceof BaseEndEvent ||
                                        bpmnDefinition instanceof BaseTask ||
                                        bpmnDefinition instanceof BaseGateway ||
                                        bpmnDefinition instanceof BaseSubprocess) {
                                    targetName = bpmnDefinition.getGeneral().getName().getValue();
                                }
                            }
                            if (targetName != null && !targetName.isEmpty()) {
                                return new Pair<Object, String>(routeIdentifier,
                                                                targetName);
                            } else if (targetNodeType != null && !targetNodeType.isEmpty()) {
                                return new Pair<Object, String>(routeIdentifier,
                                                                targetNodeType);
                            } else {
                                return new Pair<Object, String>(routeIdentifier,
                                                                routeIdentifier);
                            }
                        })
                        .collect(Collectors.toList());
            }
        }
        return result;
    }

    protected Node getSelectedElement() {
        String elementUUID = getSelectedElementUUID(sessionManager.getCurrentSession());
        if (elementUUID != null) {
            return sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getGraph().getNode(elementUUID);
        }
        return null;
    }

    protected String getSelectedElementUUID(ClientSession clientSession) {
        if (clientSession instanceof EditorSession) {
            final SelectionControl selectionControl = ((EditorSession) clientSession).getSelectionControl();
            if (null != selectionControl) {
                final Collection<String> selectedItems = selectionControl.getSelectedItems();
                if (null != selectedItems && !selectedItems.isEmpty()) {
                    return selectedItems.iterator().next();
                }
            }
        }
        return null;
    }

    protected BPMNDefinition getEdgeTarget(Edge edge) {
        Node targetNode = edge.getTargetNode();
        if (targetNode != null && targetNode.getContent() instanceof View) {
            Object definition = ((View) targetNode.getContent()).getDefinition();
            if (definition instanceof BPMNDefinition) {
                return (BPMNDefinition) definition;
            }
        }
        return null;
    }
}
