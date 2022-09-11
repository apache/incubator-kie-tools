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
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.ActionTransition;
import org.kie.workbench.common.stunner.sw.definition.ActionsContainer;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.CompensationTransition;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.EventRef;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.EventTimeout;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.NodeMarshaller;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.NodeUnmarshaller;

import static org.kie.workbench.common.stunner.sw.marshall.DefinitionTypeUtils.getEnd;
import static org.kie.workbench.common.stunner.sw.marshall.DefinitionTypeUtils.getTransition;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.STATE_END;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.marshallEdge;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.unmarshallEdge;
import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.getElementDefinition;
import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.isValidString;

public interface StateMarshalling {

    NodeMarshaller<Object> ANY_NODE_MARSHALLER =
            (context, node) -> node.getContent().getDefinition();

    NodeUnmarshaller<State> STATE_UNMARSHALLER =
            (context, state) -> {
                // Parse common fields.
                String name = state.getName();
                if (context.isStateAlreadyExist(name)) {
                    context.getContext().addMessage(new Message(MessageCode.DUPLICATE_STATE_NAME,
                                                                name));
                }
                final Node stateNode = context.addNode(name, state);

                context.sourceNode = stateNode;

                // Parse end.
                boolean end = getEnd(state.getEnd());
                if (end) {
                    final Transition tend = new Transition();
                    tend.setTo(STATE_END);
                    Edge tendEdge = unmarshallEdge(context, tend);
                }

                // Parse transition.
                String transition = getTransition(state.getTransition());
                if (isValidString(transition)) {
                    final Transition t = new Transition();
                    t.setTo(transition);
                    Edge edge = unmarshallEdge(context, t);
                }

                // Parse compensation transition.
                if (isValidString(state.getCompensatedBy())) {
                    CompensationTransition compensationTransition = new CompensationTransition();
                    compensationTransition.setTransition(state.getCompensatedBy());
                    Edge<ViewConnector<Object>, Node> compensationEdge = unmarshallEdge(context, compensationTransition);
                }

                // Parse on-errors.
                ErrorTransition[] onErrors = state.getOnErrors();
                if (null != onErrors && onErrors.length > 0) {
                    for (int i = 0; i < onErrors.length; i++) {
                        ErrorTransition onError = onErrors[i];
                        if (null != onError) {
                            Edge errorEdge = unmarshallEdge(context, onError);
                        }
                    }
                }

                // TODO: Timeouts not in use, just was a PoC, consider dropping at the end if not making sense.
                /*if (isValidString(state.eventTimeout)) {
                    EventTimeout eventTimeout = new EventTimeout();
                    eventTimeout.setEventTimeout(state.eventTimeout);
                    Node eventTimeoutNode = context.addNode(null, eventTimeout);
                    context.dock(stateNode, eventTimeoutNode);
                }*/

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
                        Node timeoutNode = edge.getTargetNode();
                        if (null != timeoutNode) {
                            Object def = getElementDefinition(timeoutNode);
                            if (def instanceof EventTimeout) {
                                state.setEventTimeout(((EventTimeout) def).getEventTimeout());
                            }
                        }
                    } else {
                        Object def = getElementDefinition(edge);
                        if (def instanceof ErrorTransition) {
                            errors.add((ErrorTransition) def);
                        }
                        marshallEdge(context, edge);
                    }
                }
                state.setOnErrors(errors.isEmpty() ? null : errors.toArray(new ErrorTransition[errors.size()]));
                return state;
            };

    NodeUnmarshaller<OnEvent[]> ONEVENTS_UNMARSHALLER =
            (context, onEvents) -> {
                // TODO: Only parsing a SINGLE (FIRST) onEvent def.
                OnEvent onEvent = onEvents[0];
                final Node onEventsNode = context.addNode(null, onEvent);

                // Set actual context parent node.
                Node parent = context.parentNode;
                context.parentNode = onEventsNode;

                String[] eventRefs = onEvent.getEventRefs();
                ActionNode[] actions = onEvent.getActions();

                // TODO: Only parsing a SINGLE (FIRST) event definition.
                // Event Node.
                String eventRef = eventRefs[0];
                EventRef event = new EventRef();
                event.setEventRef(eventRef);
                event.setName(eventRef);
                final Node eventNode = context.addNode(null, event);

                // TODO: Only parsing a SINGLE (FIRST) action definition.
                // Action Node.
                ActionNode action = actions[0];
                final Node actionNode = context.addNode(null, action);

                // Transition to Actions Node.
                final ActionTransition at = new ActionTransition();
                // at.setName("Call " + action.getName());
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
                // TODO: Parser for [eventRef + Action]
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
                        /*
                        TODO: If necessary, enable the transition (but missing to check connection rules for ActionTransition)
                        final ActionTransition actionsTransition = new ActionTransition();
                        Edge onEventsEdge = context.addEdgeToTargetUUID(actionsTransition, stateNode, actionsNode.getUUID());
                        */
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
