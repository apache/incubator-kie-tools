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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import jsinterop.base.Js;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.CallFunctionAction;
import org.kie.workbench.common.stunner.sw.definition.CallSubflowAction;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

// TODO: Find some way to get rid of this work:
//  - Merging this stuff directly into NativeMarshaller?
//  Otherwise : Merging object via Property Adapters? Auto-generating? Native JS Adapters?
@ApplicationScoped
public class Parser {

    @Inject
    private FactoryManager factoryManager;

    public Workflow parse(Workflow jso) {
        Workflow workflow = parse(Workflow.class, jso);
        State[] states = jso.states;
        workflow.states = new State[states.length];
        for (int i = 0; i < states.length; i++) {
            State s = states[i];
            State state = parseState(s);
            workflow.states[i] = state;
        }

        return workflow;
    }

    private State parseState(State jso) {
        State state = null;
        if (InjectState.TYPE_INJECT.equals(jso.type)) {
            state = parse(InjectState.class, jso);
        } else if (EventState.TYPE_EVENT.equals(jso.type)) {
            state = parseEventState(jso);
        } else if (SwitchState.TYPE_SWITCH.equals(jso.type)) {
            state = parse(SwitchState.class, jso);
        }

        if (null != state) {
            ErrorTransition[] onErrors = jso.onErrors;
            if (null != onErrors) {
                state.onErrors = new ErrorTransition[onErrors.length];
                for (int i = 0; i < onErrors.length; i++) {
                    ErrorTransition e = onErrors[i];
                    ErrorTransition et = parseErrorTransition(e);
                    state.onErrors[i] = et;
                }
            }
        }

        return state;
    }

    private EventState parseEventState(State jso) {
        EventState state = (EventState) parse(EventState.class, jso);
        OnEvent[] onEvents = Js.uncheckedCast(Js.asPropertyMap(jso).get("onEvents"));
        if (null != onEvents) {
            state.onEvents = new OnEvent[onEvents.length];
            for (int i = 0; i < onEvents.length; i++) {
                OnEvent e = onEvents[i];
                OnEvent onEvent = parseOnEvent(e);
                state.onEvents[i] = onEvent;
            }
        }
        return state;
    }

    private OnEvent parseOnEvent(OnEvent jso) {
        OnEvent onEvent = parse(OnEvent.class, jso);
        ActionNode[] actions = Js.uncheckedCast(Js.asPropertyMap(jso).get("actions"));
        if (null != actions) {
            onEvent.actions = new ActionNode[actions.length];
            for (int i = 0; i < actions.length; i++) {
                ActionNode a = actions[i];
                ActionNode action = parseAction(a);
                onEvent.actions[i] = action;
            }
        }
        return onEvent;
    }

    private ActionNode parseAction(ActionNode jso) {
        ActionNode action = null;
        if (null != jso.functionRef) {
            action = parse(CallFunctionAction.class, jso);
            action.setName(jso.functionRef);
        } else if (null != jso.subFlowRef) {
            action = parse(CallSubflowAction.class, jso);
            action.setName(jso.subFlowRef);
        }
        return action;
    }

    private ErrorTransition parseErrorTransition(ErrorTransition jso) {
        ErrorTransition et = parse(ErrorTransition.class, jso);
        return et;
    }

    private <T> T parse(Class<? extends T> type, T jso) {
        return MarshallerUtils.parse(factoryManager, type, jso);
    }
}