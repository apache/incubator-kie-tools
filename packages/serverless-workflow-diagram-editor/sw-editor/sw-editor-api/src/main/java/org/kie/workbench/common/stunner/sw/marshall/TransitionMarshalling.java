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


package org.kie.workbench.common.stunner.sw.marshall;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.sw.definition.CompensationTransition;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.HasCompensatedBy;
import org.kie.workbench.common.stunner.sw.definition.HasEnd;
import org.kie.workbench.common.stunner.sw.definition.HasTransition;
import org.kie.workbench.common.stunner.sw.definition.StartDefinition;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.StateTransition;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.EdgeMarshaller;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.EdgeUnmarshaller;

import static org.kie.workbench.common.stunner.sw.marshall.DefinitionTypeUtils.getTransition;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.EDGE_START;
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
                if (null == sourceNode) {
                    return edge;
                }

                Node targetNode = edge.getTargetNode();
                if (null == targetNode) {
                    return edge;
                }

                State sourceState = getElementDefinition(sourceNode);
                Object targetDef = getElementDefinition(targetNode);

                if (!(sourceState instanceof HasEnd)) {
                    return edge;
                }

                if (targetDef instanceof End) {
                    ((HasTransition<?>) sourceState).setTransition(null);
                    if(((HasEnd<?>)sourceState).getEnd() instanceof Boolean) {
                        ((HasEnd<?>)sourceState).setEnd(true);
                    } // else is Object
                } else {
                    if (null == ((HasTransition<?>) sourceState).getTransition() ||
                            ((HasTransition<?>) sourceState).getTransition() instanceof String) {
                        ((HasTransition<?>) sourceState).setTransition(getStateNodeName(targetNode));
                    } else {
                        ((StateTransition) ((HasTransition<?>) sourceState).getTransition()).setNextState(getStateNodeName(targetNode));
                    }

                    if (((HasEnd<?>)sourceState).getEnd() instanceof Boolean) {
                        ((HasEnd<?>)sourceState).setEnd(false);
                    }
                }
                return edge;
            };

    EdgeUnmarshaller<CompensationTransition> COMPENSATION_TRANSITION_UNMARSHALLER =
            (context, compensationTransition) -> {
                String transition = getTransition(compensationTransition.getTransition());

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
                    ViewImpl view = (ViewImpl) sourceNode.getContent();
                    if (null != targetNode && view != null && (view.getDefinition() instanceof HasCompensatedBy<?>)) {
                        Object sourceState = getElementDefinition(sourceNode);
                        ((HasCompensatedBy<?>) sourceState).setCompensatedBy(getStateNodeName(targetNode));
                    }
                }

                return edge;
            };

    EdgeUnmarshaller<DataConditionTransition> DATA_CONDITION_TRANSITION_UNMARSHALLER =
            (context, dataConditionTransition) -> {
                String transition = getTransition(dataConditionTransition.getTransition());

                Edge edge = null;
                if (DefinitionTypeUtils.toEnd(dataConditionTransition.getEnd())) {
                    final End endBean = new End();
                    String endName = UUID.uuid();
                    Node endNode = context.addNode(endName, endBean);
                    edge = context.addEdgeToTargetName(dataConditionTransition, context.sourceNode, endName);
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
                            dataConditionTransition.setTransition(null);

                            if (dataConditionTransition.getEnd() instanceof Boolean) {
                                dataConditionTransition.setEnd(true);
                            }
                        } else {
                            // New states created from the canvas have null transition
                            if (null == dataConditionTransition.getTransition() ||
                                    dataConditionTransition.getTransition() instanceof String) {
                                dataConditionTransition.setTransition(getStateNodeName(targetNode));
                            } else {
                                ((StateTransition) dataConditionTransition.getTransition()).setNextState(getStateNodeName(targetNode));
                            }

                            if (dataConditionTransition.getEnd() instanceof Boolean) {
                                dataConditionTransition.setEnd(false);
                            }
                        }
                    }
                }
                return edge;
            };

    EdgeUnmarshaller<DefaultConditionTransition> DEFAULT_CONDITION_TRANSITION_UNMARSHALLER =
            (context, defaultConditionTransition) -> {
                String transition = getTransition(defaultConditionTransition.getTransition());

                Edge edge = null;
                if (DefinitionTypeUtils.toEnd(defaultConditionTransition.getEnd())) {
                    final End endBean = new End();
                    String endName = UUID.uuid();
                    Node endNode = context.addNode(endName, endBean);
                    edge = context.addEdgeToTargetName(defaultConditionTransition, context.sourceNode, endName);
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
                            defaultConditionTransition.setTransition(null);

                            if (defaultConditionTransition.getEnd() instanceof Boolean) {
                                defaultConditionTransition.setEnd(true);
                            }
                        } else {
                            // New states created from the canvas have null transition
                            if (null == defaultConditionTransition.getTransition() ||
                                    defaultConditionTransition.getTransition() instanceof String) {
                                defaultConditionTransition.setTransition(getStateNodeName(targetNode));
                            } else {
                                ((StateTransition) defaultConditionTransition.getTransition()).setNextState(getStateNodeName(targetNode));
                            }

                            if (defaultConditionTransition.getEnd() instanceof Boolean) {
                                defaultConditionTransition.setEnd(false);
                            }
                        }
                    }
                }
                return edge;
            };

    EdgeUnmarshaller<ErrorTransition> ERROR_TRANSITION_UNMARSHALLER =
            (context, errorTransition) -> {
                String transition = getTransition(errorTransition.getTransition());

                Edge edge = null;
                if (DefinitionTypeUtils.toEnd(errorTransition.getEnd())) {
                    final End endBean = new End();
                    String endName = UUID.uuid();
                    Node endNode = context.addNode(endName, endBean);
                    edge = context.addEdgeToTargetName(errorTransition, context.sourceNode, endName);
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
                            errorTransition.setTransition(null);

                            if (errorTransition.getEnd() instanceof Boolean) {
                                errorTransition.setEnd(true);
                            }
                        } else {
                            // New states created from the canvas have null transition
                            if (null == errorTransition.getTransition() ||
                                    errorTransition.getTransition() instanceof String) {
                                errorTransition.setTransition(getStateNodeName(targetNode));
                            } else {
                                ((StateTransition) errorTransition.getTransition())
                                        .setNextState(getStateNodeName(targetNode));
                            }

                            if (errorTransition.getEnd() instanceof Boolean) {
                                errorTransition.setEnd(false);
                            }
                        }
                    }
                }
                return edge;
            };

    EdgeUnmarshaller<EventConditionTransition> EVENT_CONDITION_TRANSITION_UNMARSHALLER =
            (context, eventConditionTransition) -> {
                String transition = getTransition(eventConditionTransition.getTransition());

                Edge edge = null;
                if (DefinitionTypeUtils.toEnd(eventConditionTransition.getEnd())) {
                    final End endBean = new End();
                    String endName = UUID.uuid();
                    Node endNode = context.addNode(endName, endBean);
                    edge = context.addEdgeToTargetName(eventConditionTransition, context.sourceNode, endName);
                } else if (isValidString(transition)) {
                    edge = context.addEdgeToTargetName(eventConditionTransition, context.sourceNode, transition);
                }

                return edge;
            };

    EdgeMarshaller<EventConditionTransition> EVENT_CONDITION_TRANSITION_MARSHALLER =
            (context, edge) -> {
                Node sourceNode = edge.getSourceNode();
                if (null == sourceNode) {
                    return edge;
                }

                Node targetNode = edge.getTargetNode();
                if (null == targetNode) {
                    return edge;
                }
                EventConditionTransition eventConditionTransition = getElementDefinition(edge);
                Object targetDef = getElementDefinition(targetNode);

                if (targetDef instanceof End) {
                    eventConditionTransition.setTransition(null);

                    if (eventConditionTransition.getEnd() instanceof Boolean) {
                        eventConditionTransition.setEnd(true);
                    }
                } else {
                    // New states created from the canvas have null transition
                    if (null == eventConditionTransition.getTransition() ||
                            eventConditionTransition.getTransition() instanceof String) {
                        eventConditionTransition.setTransition(getStateNodeName(targetNode));
                    } else {
                        ((StateTransition) eventConditionTransition.getTransition()).setNextState(getStateNodeName(targetNode));
                    }

                    if (eventConditionTransition.getEnd() instanceof Boolean) {
                        eventConditionTransition.setEnd(false);
                    }
                }
                return edge;
            };

    EdgeUnmarshaller<StartTransition> START_TRANSITION_UNMARSHALLER =
            (context, startTransition) -> {
                Node sourceNode = context.sourceNode;
                Edge startEdge = context.addEdge(EDGE_START, startTransition, sourceNode);

                String transition = getTransition(startTransition.getTransition());
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

                    if (workflow.getStart() instanceof String) {
                        workflow.setStart(stateName);
                    } else if (workflow.getStart() != null) {
                        Object startName = ((StartDefinition) workflow.getStart()).getStateName();
                        if (startName != null) {
                            ((StartDefinition) workflow.getStart()).setStateName(stateName);
                        }
                    }
                } else {
                    workflow.setStart(null);
                }

                return edge;
            };
}
