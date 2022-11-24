/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.marshall;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.NodeMarshaller;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.NodeUnmarshaller;

import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.getChildNodes;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.STATE_END;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.STATE_START;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.hasNodeMarshaller;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.isEndState;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.isStartState;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.marshallEdge;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.marshallNode;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.unmarshallEdge;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.unmarshallNode;
import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.getElementDefinition;
import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.isValidString;

public interface WorkflowMarshalling {

    NodeUnmarshaller<Workflow> START_NODE_UNMARSHALLER =
            (context, workflow) -> {
                String start = DefinitionTypeUtils.getStart(workflow);
                Node startNode = null;
                if (isValidString(start)) {
                    startNode = context.addNode(STATE_START, new Start());
                    StartTransition tStart = new StartTransition();
                    tStart.setTransition(start);
                    context.sourceNode = startNode;
                    Edge startEdge = unmarshallEdge(context, tStart);
                    context.sourceNode = null;
                }
                return startNode;
            };

    NodeMarshaller<Workflow> START_NODE_MARSHALLER =
            (context, workflowNode) -> {
                Workflow workflow = workflowNode.getContent().getDefinition();
                String startNodeUUID = context.obtainUUID(STATE_START);
                Node startNode = context.getNode(startNodeUUID);
                if (null != startNode && !startNode.getOutEdges().isEmpty()) {
                    Edge startEdge = (Edge) startNode.getOutEdges().get(0);
                    marshallEdge(context, startEdge);
                } else {
                    workflow.setStart(null);
                }
                return workflow;
            };

    NodeUnmarshaller<Workflow> END_NODE_UNMARSHALLER =
            (context, workflow) -> {
                final End endBean = new End();
                Node endNode = context.addNode(STATE_END, endBean);
                return endNode;
            };

    NodeUnmarshaller<Workflow> WORKFLOW_UNMARSHALLER =
            (context, workflow) -> {
                String workflowId = workflow.getId() != null ? workflow.getId() : workflow.getKey();
                if (StringUtils.isEmpty(workflowId)) {
                    workflowId = UUID.uuid();
                    workflow.setId(workflowId);
                }
                Node<View<Workflow>, Edge> workflowNode = context.addNodeByUUID(workflowId, workflow);
                workflowNode.getContent().setBounds(Bounds.create(0, 0, 950, 950));

                // Set workflow node into the context state.
                context.getContext().setWorkflowRootNode(workflowNode);

                // Start state.
                START_NODE_UNMARSHALLER.unmarshall(context, workflow);

                // End state.
                END_NODE_UNMARSHALLER.unmarshall(context, workflow);

                // States.
                final State[] states = workflow.getStates();
                for (int i = 0; i < states.length; i++) {
                    State state = states[i];
                    Node stateNode = unmarshallNode(context, state);
                }

                return workflowNode;
            };

    NodeMarshaller<Workflow> WORKFLOW_MARSHALLER =
            (context, workflowNode) -> {
                Workflow workflow = workflowNode.getContent().getDefinition();

                // Start State.
                START_NODE_MARSHALLER.marshall(context, workflowNode);

                // States.
                List<Object> beans = new ArrayList<>();
                List<Node> childNodes = getChildNodes(workflowNode);
                childNodes.forEach(node -> {
                    if (!isStartState(node) && !isEndState(node)) {
                        // TODO: If node has been already processed by some edge, no real need to iterate over it here....
                        if (hasNodeMarshaller(node)) {
                            marshallNode(context, node);
                            beans.add(getElementDefinition(node));
                        }
                    }
                });
                workflow.setStates(beans.isEmpty() ? null : beans.toArray(new State[beans.size()]));

                return workflow;
            };
}
