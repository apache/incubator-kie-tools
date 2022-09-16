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
import static org.kie.workbench.common.stunner.sw.marshall.DefinitionTypeUtils.getObjectProperty;
import static org.kie.workbench.common.stunner.sw.marshall.DefinitionTypeUtils.getTransition;
import static org.kie.workbench.common.stunner.sw.marshall.DefinitionTypeUtils.setObjectProperty;
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
                            if (sourceState.end instanceof Boolean) {
                                sourceState.end = true;
                            }
                        } else {
                            if (sourceState.transition instanceof String) {
                                sourceState.transition = getStateNodeName(targetNode);
                            } else {
                                setObjectProperty(sourceState.transition, "nextState", getStateNodeName(targetNode));
                            }

                            if (sourceState.end instanceof Boolean) {
                                sourceState.end = false;
                            }
                        }
                    }
                }

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

                        if (sourceState.transition instanceof String) {
                            sourceState.transition = getStateNodeName(targetNode);
                        } else {
                            setObjectProperty(sourceState.transition, "nextState", getStateNodeName(targetNode));
                        }
                    }
                }

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

                            if (dataConditionTransition.end instanceof Boolean) {
                                dataConditionTransition.end = true;
                            }
                        } else {
                            if (dataConditionTransition.transition instanceof String) {
                                dataConditionTransition.transition = getStateNodeName(targetNode);
                            } else {
                                setObjectProperty(dataConditionTransition.transition, "nextState", getStateNodeName(targetNode));
                            }

                            if (dataConditionTransition.end instanceof Boolean) {
                                dataConditionTransition.end = false;
                            }
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

                            if (defaultConditionTransition.end instanceof Boolean) {
                                defaultConditionTransition.end = true;
                            }
                        } else {
                            if (defaultConditionTransition.transition instanceof String) {
                                defaultConditionTransition.transition = getStateNodeName(targetNode);
                            } else {
                                setObjectProperty(defaultConditionTransition.transition, "nextState", getStateNodeName(targetNode));
                            }

                            if (defaultConditionTransition.end instanceof Boolean) {
                                defaultConditionTransition.end = false;
                            }
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

                            if (errorTransition.end instanceof Boolean) {
                                errorTransition.end = true;
                            }
                        } else {
                            if (errorTransition.transition instanceof String) {
                                errorTransition.transition = getStateNodeName(targetNode);
                            } else {
                                setObjectProperty(errorTransition.transition, "nextState", getStateNodeName(targetNode));
                            }

                            if (errorTransition.end instanceof Boolean) {
                                errorTransition.end = false;
                            }
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

                            if (eventConditionTransition.end instanceof Boolean) {
                                eventConditionTransition.end = true;
                            }
                        } else {
                            if (eventConditionTransition.transition instanceof String) {
                                eventConditionTransition.transition = getStateNodeName(targetNode);
                            } else {
                                setObjectProperty(eventConditionTransition.transition, "nextState", getStateNodeName(targetNode));
                            }

                            if (eventConditionTransition.end instanceof Boolean) {
                                eventConditionTransition.end = false;
                            }
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

                    if (workflow.start instanceof String) {
                        workflow.setStart(stateName);
                    } else if (workflow.start != null) {
                        Object startName = getObjectProperty(workflow.start, stateName);
                        if (startName != null) {
                            setObjectProperty(workflow.start, "stateName", stateName);
                        }
                    }
                } else {
                    workflow.setStart(null);
                }

                return edge;
            };

}
