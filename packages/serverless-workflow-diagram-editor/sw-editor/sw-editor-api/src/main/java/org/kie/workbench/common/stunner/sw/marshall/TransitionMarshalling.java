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

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.sw.definition.CompensationTransition;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.EdgeMarshaller;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.EdgeUnmarshaller;

import static org.kie.workbench.common.stunner.sw.marshall.DefinitionTypeUtils.getEnd;
import static org.kie.workbench.common.stunner.sw.marshall.DefinitionTypeUtils.getTransition;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.EDGE_START;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.STATE_END;
import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.getElementDefinition;
import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.getStateNodeName;
import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.isValidString;

public interface TransitionMarshalling {

    EdgeMarshaller<Object> ANY_EDGE_MARSHALLER =
            (context, edge) -> edge;

    EdgeUnmarshaller<Transition> TRANSITION_UNMARSHALLER =
            (context, transition) -> {
                String to = transition.getTo();
                Edge edge = context.addEdgeToTargetName(transition, context.sourceNode, to);
                return edge;
            };

    EdgeMarshaller<Transition> TRANSITION_MARSHALLER =
            (context, edge) -> {
                Node sourceNode = edge.getSourceNode();
                if (null != sourceNode) {
                    Node targetNode = edge.getTargetNode();
                    if (null != targetNode) {
                        State sourceState = getElementDefinition(sourceNode);
                        Object targetDef = getElementDefinition(targetNode);
                        if (targetDef instanceof End) {
                            sourceState.transition = null;
                            sourceState.end = true;
                        } else {
                            sourceState.transition = getStateNodeName(targetNode);
                            sourceState.end = false;
                        }
                    }
                }
                // TODO: Update Transition.to ?
                return edge;
            };

    EdgeUnmarshaller<CompensationTransition> COMPENSATION_TRANSITION_UNMARSHALLER =
            (context, compensationTransition) -> {
                String transition = getTransition(compensationTransition.transition);

                Edge edge = null;
                if (isValidString(transition)) {
                    edge = context.addEdgeToTargetName(compensationTransition, context.sourceNode, transition);
                }

                return edge;
            };

    EdgeMarshaller<CompensationTransition> COMPENSATION_TRANSITION_MARSHALLER =
            (context, edge) -> {
                Node sourceNode = edge.getSourceNode();
                if (null != sourceNode) {
                    Node targetNode = edge.getTargetNode();
                    if (null != targetNode) {
                        State sourceState = getElementDefinition(sourceNode);
                        sourceState.transition = getStateNodeName(targetNode);
                    }
                }
                // TODO: Update CompensationTransition.name ?
                // TODO: Update CompensationTransition.transition ?
                return edge;
            };

    EdgeUnmarshaller<DataConditionTransition> DATA_CONDITION_TRANSITION_UNMARSHALLER =
            (context, dataConditionTransition) -> {
                boolean end = getEnd(dataConditionTransition.end);
                String transition = getTransition(dataConditionTransition.transition);

                Edge edge = null;
                if (end) {
                    edge = context.addEdgeToTargetName(dataConditionTransition, context.sourceNode, STATE_END);
                } else if (isValidString(transition)) {
                    edge = context.addEdgeToTargetName(dataConditionTransition, context.sourceNode, transition);
                }
                return edge;
            };

    EdgeMarshaller<DataConditionTransition> DATA_CONDITION_TRANSITION_MARSHALLER =
            (context, edge) -> {
                Node sourceNode = edge.getSourceNode();
                if (null != sourceNode) {
                    Node targetNode = edge.getTargetNode();
                    if (null != targetNode) {
                        DataConditionTransition dataConditionTransition = getElementDefinition(edge);
                        Object targetDef = getElementDefinition(targetNode);
                        if (targetDef instanceof End) {
                            dataConditionTransition.transition = null;
                            dataConditionTransition.end = true;
                        } else {
                            dataConditionTransition.transition = getStateNodeName(targetNode);
                            dataConditionTransition.end = false;
                        }
                    }
                }
                return edge;
            };

    EdgeUnmarshaller<DefaultConditionTransition> DEFAULT_CONDITION_TRANSITION_UNMARSHALLER =
            (context, defaultConditionTransition) -> {
                boolean end = getEnd(defaultConditionTransition.end);
                String transition = getTransition(defaultConditionTransition.transition);

                Edge edge = null;
                if (end) {
                    edge = context.addEdgeToTargetName(defaultConditionTransition, context.sourceNode, STATE_END);
                } else if (isValidString(transition)) {
                    edge = context.addEdgeToTargetName(defaultConditionTransition, context.sourceNode, transition);
                }

                return edge;
            };

    EdgeMarshaller<DefaultConditionTransition> DEFAULT_CONDITION_TRANSITION_MARSHALLER =
            (context, edge) -> {
                Node sourceNode = edge.getSourceNode();
                if (null != sourceNode) {
                    Node targetNode = edge.getTargetNode();
                    if (null != targetNode) {
                        DefaultConditionTransition defaultConditionTransition = getElementDefinition(edge);
                        Object targetDef = getElementDefinition(targetNode);
                        if (targetDef instanceof End) {
                            defaultConditionTransition.transition = null;
                            defaultConditionTransition.end = true;
                        } else {
                            defaultConditionTransition.transition = getStateNodeName(targetNode);
                            defaultConditionTransition.end = false;
                        }
                    }
                }
                return edge;
            };

    EdgeUnmarshaller<ErrorTransition> ERROR_TRANSITION_UNMARSHALLER =
            (context, errorTransition) -> {
                boolean end = getEnd(errorTransition.end);
                String transition = getTransition(errorTransition.transition);

                Edge edge = null;
                if (end) {
                    edge = context.addEdgeToTargetName(errorTransition, context.sourceNode, STATE_END);
                } else if (isValidString(transition)) {
                    edge = context.addEdgeToTargetName(errorTransition, context.sourceNode, transition);
                }
                return edge;
            };

    EdgeMarshaller<ErrorTransition> ERROR_TRANSITION_MARSHALLER =
            (context, edge) -> {
                Node sourceNode = edge.getSourceNode();
                if (null != sourceNode) {
                    Node targetNode = edge.getTargetNode();
                    if (null != targetNode) {
                        ErrorTransition errorTransition = getElementDefinition(edge);
                        Object targetDef = getElementDefinition(targetNode);
                        if (targetDef instanceof End) {
                            errorTransition.transition = null;
                            errorTransition.end = true;
                        } else {
                            errorTransition.transition = getStateNodeName(targetNode);
                            errorTransition.end = false;
                        }
                    }
                }
                return edge;
            };

    EdgeUnmarshaller<EventConditionTransition> EVENT_CONDITION_TRANSITION_UNMARSHALLER =
            (context, eventConditionTransition) -> {
                boolean end = getEnd(eventConditionTransition.end);
                String transition = getTransition(eventConditionTransition.transition);

                Edge edge = null;
                if (end) {
                    edge = context.addEdgeToTargetName(eventConditionTransition, context.sourceNode, STATE_END);
                } else if (isValidString(transition)) {
                    edge = context.addEdgeToTargetName(eventConditionTransition, context.sourceNode, transition);
                }

                return edge;
            };


    EdgeMarshaller<EventConditionTransition> EVENT_CONDITION_TRANSITION_MARSHALLER =
            (context, edge) -> {
                Node sourceNode = edge.getSourceNode();
                if (null != sourceNode) {
                    Node targetNode = edge.getTargetNode();
                    if (null != targetNode) {
                        EventConditionTransition eventConditionTransition = getElementDefinition(edge);
                        Object targetDef = getElementDefinition(targetNode);
                        if (targetDef instanceof End) {
                            eventConditionTransition.transition = null;
                            eventConditionTransition.end = true;
                        } else {
                            eventConditionTransition.transition = getStateNodeName(targetNode);
                            eventConditionTransition.end = false;
                        }
                    }
                }
                return edge;
            };

    EdgeUnmarshaller<StartTransition> START_TRANSITION_UNMARSHALLER =
            (context, startTransition) -> {
                Node sourceNode = context.sourceNode;
                Edge startEdge = context.addEdge(EDGE_START, startTransition, sourceNode);

                String transition = getTransition(startTransition.transition);
                if (isValidString(transition)) {
                    String targetUUID = context.obtainUUID(transition);
                    context.connect(startEdge, sourceNode, targetUUID);
                }

                return startEdge;
            };

    EdgeMarshaller<StartTransition> START_TRANSITION_MARSHALLER =
            (context, edge) -> {
                Workflow workflow = context.getWorkflowRoot();
                if (null != edge.getTargetNode()) {
                    Node targetNode = edge.getTargetNode();
                    String stateName = getStateNodeName(targetNode);
                    workflow.setStart(stateName);
                } else {
                    workflow.setStart("");
                }
                // TODO: Update StartTransition.transition ?
                return edge;
            };

}
