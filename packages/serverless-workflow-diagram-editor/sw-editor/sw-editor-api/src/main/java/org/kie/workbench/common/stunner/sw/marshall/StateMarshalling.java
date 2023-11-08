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

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.ActionTransition;
import org.kie.workbench.common.stunner.sw.definition.ActionsContainer;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.CompensationTransition;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.EventRef;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.HasCompensatedBy;
import org.kie.workbench.common.stunner.sw.definition.HasEnd;
import org.kie.workbench.common.stunner.sw.definition.HasErrors;
import org.kie.workbench.common.stunner.sw.definition.HasTransition;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.NodeMarshaller;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.NodeUnmarshaller;

import static org.kie.workbench.common.stunner.sw.marshall.DefinitionTypeUtils.getTransition;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.marshallEdge;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.unmarshallEdge;
import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.getElementDefinition;
import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.isValidString;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DUPLICATE_STATE_NAME;

public interface StateMarshalling {

    NodeMarshaller<Object> ANY_NODE_MARSHALLER =
            (context, node) -> node.getContent().getDefinition();

    NodeUnmarshaller<State<?>> STATE_UNMARSHALLER =
            (context, state) -> {
                // Parse common fields.
                String name = state.getName();
                if (context.isStateAlreadyExist(name)) {
                    context.getContext().addMessage(new Message(DUPLICATE_STATE_NAME,
                                                                name));
                }
                final Node stateNode = context.addNode(name, state);

                context.sourceNode = stateNode;

                // Parse end.
                if (state instanceof HasEnd && DefinitionTypeUtils.toEnd((((HasEnd<?>) state).getEnd()))) {
                    final End endBean = new End();
                    String endName = UUID.uuid();
                    Node endNode = context.addNode(endName, endBean);

                    final Transition tend = new Transition();
                    tend.setTo(endName);
                    Edge tendEdge = unmarshallEdge(context, tend);
                }

                // Parse transition.
                if (state instanceof HasTransition) {
                    String transition = getTransition(((HasTransition<?>) state).getTransition());
                    if (isValidString(transition)) {
                        final Transition t = new Transition();
                        t.setTo(transition);
                        Edge edge = unmarshallEdge(context, t);
                    }
                }

                if (state instanceof HasCompensatedBy) {
                    String compensatedBy = ((HasCompensatedBy<?>) state).getCompensatedBy();
                    // Parse compensation transition.
                    if (isValidString(compensatedBy)) {
                        CompensationTransition compensationTransition = new CompensationTransition();
                        compensationTransition.setTransition(compensatedBy);
                        Edge<ViewConnector<Object>, Node> compensationEdge = unmarshallEdge(context, compensationTransition);
                    }
                }

                if (state instanceof HasErrors) {
                    // Parse on-errors.
                    ErrorTransition[] onErrors = ((HasErrors<?>)state).getOnErrors();
                    if (null != onErrors && onErrors.length > 0) {
                        for (int i = 0; i < onErrors.length; i++) {
                            ErrorTransition onError = onErrors[i];
                            if (null != onError) {
                                Edge errorEdge = unmarshallEdge(context, onError);
                            }
                        }
                    }
                }

                context.sourceNode = null;

                return stateNode;
            };

    NodeMarshaller<State> STATE_MARSHALLER =
            (context, stateNode) -> {
                State state = stateNode.getContent().getDefinition();

                // Iterate and marshaller edges.
                List<ErrorTransition> errors = new ArrayList<>();
                List<Edge> outEdges = stateNode.getOutEdges();
                for (Edge edge : outEdges) {
                    if (edge.getContent() instanceof Dock) {
                    } else {
                        Object def = getElementDefinition(edge);
                        if (def instanceof ErrorTransition) {
                            errors.add((ErrorTransition) def);
                        }
                        marshallEdge(context, edge);
                    }
                }

                if (state instanceof HasErrors) {
                    ((HasErrors<?>) state).setOnErrors(errors.isEmpty() ? null : errors.toArray(new ErrorTransition[errors.size()]));
                }
                return state;
            };

    NodeMarshaller<SwitchState> SWITCH_STATE_MARSHALLER =
            (context, stateNode) -> {
                SwitchState state = stateNode.getContent().getDefinition();

                // Iterate and marshaller edges.
                List<EventConditionTransition> eventConditions = new ArrayList<>();
                List<DataConditionTransition> dataConditionTransitions = new ArrayList<>();
                List<ErrorTransition> errors = new ArrayList<>();

                List<Edge> outEdges = stateNode.getOutEdges();
                for (Edge edge : outEdges) {
                    if (edge.getContent() instanceof Dock) {
                    } else {
                        Object def = getElementDefinition(edge);

                        if (def instanceof EventConditionTransition) {
                            eventConditions.add((EventConditionTransition) def);
                        }

                        if (def instanceof DataConditionTransition) {
                            dataConditionTransitions.add((DataConditionTransition) def);
                        }

                        if (def instanceof ErrorTransition) {
                            errors.add((ErrorTransition) def);
                        }

                        if (def instanceof DefaultConditionTransition) {
                            state.setDefaultCondition((DefaultConditionTransition) def);
                        }

                        marshallEdge(context, edge);
                    }
                }

                state.setEventConditions(eventConditions.isEmpty() ? null : eventConditions.toArray(new EventConditionTransition[eventConditions.size()]));
                state.setDataConditions(dataConditionTransitions.isEmpty() ? null : dataConditionTransitions.toArray(new DataConditionTransition[dataConditionTransitions.size()]));
                state.setOnErrors(errors.isEmpty() ? null : errors.toArray(new ErrorTransition[errors.size()]));

                return state;
            };

    NodeUnmarshaller<OnEvent[]> ONEVENTS_UNMARSHALLER =
            (context, onEvents) -> {
                OnEvent onEvent = onEvents[0];
                final Node onEventsNode = context.addNode(null, onEvent);

                // Set actual context parent node.
                Node parent = context.parentNode;
                context.parentNode = onEventsNode;

                String[] eventRefs = onEvent.getEventRefs();
                ActionNode[] actions = onEvent.getActions();

                // Event Node.
                String eventRef = eventRefs[0];
                EventRef event = new EventRef();
                event.setEventRef(eventRef);
                event.setName(eventRef);
                final Node eventNode = context.addNode(null, event);

                // Action Node.
                ActionNode action = actions[0];
                final Node actionNode = context.addNode(null, action);

                // Transition to Actions Node.
                final ActionTransition at = new ActionTransition();
                Edge actionsEdge = context.addEdgeToTargetUUID(at, eventNode, actionNode.getUUID());

                // Set the original parent.
                context.parentNode = parent;

                return onEventsNode;
            };

    NodeUnmarshaller<ActionNode[]> ACTIONS_UNMARSHALLER =
            (context, actions) -> {
                ActionsContainer actionsContainer = new ActionsContainer();
                final Node actionsNode = context.addNode(null, actionsContainer);

                // Set actual context parent node.
                Node parent = context.parentNode;
                context.parentNode = actionsNode;

                for (int i = 0; i < actions.length; i++) {
                    ActionNode action = actions[i];
                    final Node actionNode = context.addNode(action.getName(), action);
                }

                // Set the original parent.
                context.parentNode = parent;

                return actionsNode;
            };

    NodeUnmarshaller<ForEachState> FOREACH_STATE_UNMARSHALLER =
            (context, state) -> {
                Node stateNode = STATE_UNMARSHALLER.unmarshall(context, state);
                if (Marshaller.LOAD_DETAILS) {
                    ActionNode[] actions = state.getActions();
                    if (null != actions && actions.length > 0) {
                        Node actionsNode = ACTIONS_UNMARSHALLER.unmarshall(context, actions);
                    }
                }
                return stateNode;
            };

    NodeUnmarshaller<CallbackState> CALLBACK_STATE_UNMARSHALLER =
            (context, state) -> {
                Node stateNode = STATE_UNMARSHALLER.unmarshall(context, state);
                return stateNode;
            };

    NodeUnmarshaller<SwitchState> SWITCH_STATE_UNMARSHALLER =
            (context, state) -> {
                Node stateNode = STATE_UNMARSHALLER.unmarshall(context, state);
                context.sourceNode = stateNode;
                DefaultConditionTransition defaultCondition = state.getDefaultCondition();
                if (null != defaultCondition) {
                    Edge defaultConditionEdge = unmarshallEdge(context, defaultCondition);
                }
                EventConditionTransition[] eventConditions = state.getEventConditions();
                if (null != eventConditions && eventConditions.length > 0) {
                    for (int i = 0; i < eventConditions.length; i++) {
                        EventConditionTransition eventCondition = eventConditions[i];
                        if (null != eventCondition) {
                            Edge eventConditionEdge = unmarshallEdge(context, eventCondition);
                        }
                    }
                }
                DataConditionTransition[] dataConditions = state.getDataConditions();
                if (null != dataConditions && dataConditions.length > 0) {
                    for (int i = 0; i < dataConditions.length; i++) {
                        DataConditionTransition dataCondition = dataConditions[i];
                        if (null != dataCondition) {
                            Edge dataConditionEdge = unmarshallEdge(context, dataCondition);
                        }
                    }
                }
                context.sourceNode = null;
                return stateNode;
            };

    NodeUnmarshaller<OperationState> OPERATION_STATE_UNMARSHALLER =
            (context, state) -> {
                Node stateNode = STATE_UNMARSHALLER.unmarshall(context, state);
                if (Marshaller.LOAD_DETAILS) {
                    ActionNode[] actions = state.getActions();
                    if (null != actions && actions.length > 0) {
                        Node actionsNode = ACTIONS_UNMARSHALLER.unmarshall(context, actions);
                    }
                }
                return stateNode;
            };

    NodeUnmarshaller<EventState> EVENT_STATE_UNMARSHALLER =
            (context, state) -> {
                Node stateNode = STATE_UNMARSHALLER.unmarshall(context, state);
                if (Marshaller.LOAD_DETAILS) {
                    OnEvent[] onEvents = state.getOnEvents();
                    if (null != onEvents && onEvents.length > 0) {
                        Node onEventsNode = ONEVENTS_UNMARSHALLER.unmarshall(context, onEvents);
                    }
                }
                return stateNode;
            };
}
