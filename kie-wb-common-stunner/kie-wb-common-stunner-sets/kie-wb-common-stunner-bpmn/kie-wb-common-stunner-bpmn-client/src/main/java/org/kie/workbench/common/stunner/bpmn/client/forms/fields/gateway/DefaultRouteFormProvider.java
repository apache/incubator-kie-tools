/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.gateway;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ContextUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

@Dependent
public class DefaultRouteFormProvider implements SelectorDataProvider {

    @Inject
    SessionManager canvasSessionManager;

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public SelectorData getSelectorData(FormRenderingContext context) {
        List<Edge> outEdges = getGatewayOutEdges(context);
        Map<String, String> values = new HashMap<>();
        if (outEdges != null) {
            for (Edge outEdge : outEdges) {
                // routeIdentifier is flowName followed by flowId
                SequenceFlow sequenceFlow = (SequenceFlow) ((ViewConnector) outEdge.getContent()).getDefinition();
                String flowName = sequenceFlow.getGeneral().getName().getValue();
                String edgeId = outEdge.getUUID();
                String routeIdentifier = (flowName != null && flowName.length() > 0) ? (flowName + " : " + edgeId) : edgeId;

                // UI value for the route is the target node name or target node type
                String targetName = null;
                String targetNodeType = null;
                BPMNDefinition bpmnDefinition = getEdgeTarget(outEdge);
                if (bpmnDefinition != null) {
                    targetNodeType = bpmnDefinition.getTitle();
                    if (bpmnDefinition instanceof BaseStartEvent) {
                        targetName = ((BaseStartEvent) bpmnDefinition).getGeneral().getName().getValue();
                    } else if (bpmnDefinition instanceof BaseEndEvent) {
                        targetName = ((BaseEndEvent) bpmnDefinition).getGeneral().getName().getValue();
                    } else if (bpmnDefinition instanceof BaseTask) {
                        targetName = ((BaseTask) bpmnDefinition).getGeneral().getName().getValue();
                    } else if (bpmnDefinition instanceof BaseGateway) {
                        targetName = ((BaseGateway) bpmnDefinition).getGeneral().getName().getValue();
                    } else if (bpmnDefinition instanceof BaseSubprocess) {
                        targetName = ((BaseSubprocess) bpmnDefinition).getGeneral().getName().getValue();
                    }
                }
                if (targetName != null && !targetName.isEmpty()) {
                    values.put(routeIdentifier,
                               targetName);
                } else if (targetNodeType != null && !targetNodeType.isEmpty()) {
                    values.put(routeIdentifier,
                               targetNodeType);
                } else {
                    values.put(routeIdentifier,
                               routeIdentifier);
                }
            }
        }
        return new SelectorData(values,
                                null);
    }

    protected List<Edge> getGatewayOutEdges(FormRenderingContext context) {
        Object model = ContextUtils.getModel(context);
        if (model instanceof ExclusiveDatabasedGateway) {
            ExclusiveDatabasedGateway gateway = (ExclusiveDatabasedGateway) model;
            Node gatewayNode = getExclusiveDatabasedGatewayNode(gateway);
            if (gatewayNode != null) {
                return gatewayNode.getOutEdges();
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

    protected Node getExclusiveDatabasedGatewayNode(ExclusiveDatabasedGateway gateway) {
        Diagram diagram = canvasSessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        Iterator<Element> it = diagram.getGraph().nodes().iterator();
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof ExclusiveDatabasedGateway) {
                    ExclusiveDatabasedGateway elementGateway = (ExclusiveDatabasedGateway) oDefinition;
                    if (elementGateway.getId() == gateway.getId()) {
                        return (Node) element;
                    }
                }
            }
        }
        return null;
    }
}
