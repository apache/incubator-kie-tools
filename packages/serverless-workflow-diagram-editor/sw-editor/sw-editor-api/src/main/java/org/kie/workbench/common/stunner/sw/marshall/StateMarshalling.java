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
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.ActionTransition;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventRef;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.EventTransition;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.NodeMarshaller;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller.NodeUnmarshaller;

import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.STATE_END;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.marshallEdge;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.unmarshallEdge;
import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.getElementDefinition;
import static org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils.isValidString;

public interface StateMarshalling {

    NodeUnmarshaller<State> STATE_UNMARSHALLER =
            (context, state) -> {
                // Parse common fields.
                String name = state.getName();
                final Node stateNode = context.addNode(name, state);

                context.sourceNode = stateNode;

                // Parse end.
                if (state.isEnd()) {
                    final Transition tend = new Transition();
                    tend.setTo(STATE_END);
                    Edge tendEdge = unmarshallEdge(context, tend);
                }

                // Parse transition.
                String transition = state.getTransition();
                if (isValidString(transition)) {
                    final Transition t = new Transition();
                    t.setTo(transition);
                    Edge edge = unmarshallEdge(context, t);
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

                context.sourceNode = null;

                return stateNode;
            };

    NodeMarshaller<State> STATE_MARSHALLER =
            (context, stateNode) -> {
                State state = stateNode.getContent().getDefinition();
                state.setEnd(false);
                state.setTransition(null);
                List<ErrorTransition> errors = new ArrayList<>();
                List<Edge> outEdges = stateNode.getOutEdges();
                for (Edge edge : outEdges) {
                    Object def = getElementDefinition(edge);
                    if (def instanceof ErrorTransition) {
                        errors.add((ErrorTransition) def);
                    }
                    marshallEdge(context, edge);
                }
                state.onErrors = errors.isEmpty() ? null : errors.toArray(new ErrorTransition[errors.size()]);
                return state;
            };

    NodeUnmarshaller<EventState> EVENT_STATE_UNMARSHALLER =
            (context, state) -> {
                Node stateNode = STATE_UNMARSHALLER.unmarshall(context, state);
                OnEvent[] onEvents = state.getOnEvents();
                if (null != onEvents && onEvents.length > 0) {
                    // TODO: Only parsing a SINGLE (FIRST) onEvent def.
                    OnEvent onEvent = onEvents[0];
                    final Node onEventsNode = context.addNode(null, onEvent);

                    // Set actual context parent node.
                    Node parent = context.parentNode;
                    context.parentNode = onEventsNode;

                    // Transition to OnEvents Node.
                    final EventTransition onEventsTransition = new EventTransition();
                    // onEventsTransition.setName("OnEvents");
                    Edge onEventsEdge = context.addEdgeToTargetUUID(onEventsTransition, stateNode, onEventsNode.getUUID());

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
                }
                return stateNode;
            };

    NodeMarshaller<State> EVENT_STATE_MARSHALLER =
            (context, node) -> {
                return STATE_MARSHALLER.marshall(context, node);
            };
}
