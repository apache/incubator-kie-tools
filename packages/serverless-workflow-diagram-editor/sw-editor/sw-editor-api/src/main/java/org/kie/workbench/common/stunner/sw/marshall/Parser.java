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
import org.kie.workbench.common.stunner.sw.definition.CallEventAction;
import org.kie.workbench.common.stunner.sw.definition.CallFunctionAction;
import org.kie.workbench.common.stunner.sw.definition.CallSubflowAction;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.ModelUtils;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
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
        loadStates(workflow, jso);

        return workflow;
    }

    public Workflow reParse(Workflow workflow, Workflow newWorkflow) {
        ModelUtils.cleanWorkflow(workflow);
        parse(workflow, newWorkflow);
        loadStates(workflow, newWorkflow);

        return workflow;
    }

    private void loadStates(Workflow workflow, Workflow jso) {
        State[] states = jso.states;
        workflow.states = new State[states.length];
        for (int i = 0; i < states.length; i++) {
            State s = states[i];
            State state = parseState(s);
            workflow.states[i] = state;
        }
    }

    private State parseState(State jso) {
        State state = null;
        if (InjectState.TYPE_INJECT.equals(jso.type)) {
            state = parse(InjectState.class, jso);
        } else if (EventState.TYPE_EVENT.equals(jso.type)) {
            state = parseEventState(jso);
        } else if (SwitchState.TYPE_SWITCH.equals(jso.type)) {
            state = parseSwitchState(jso);
        } else if (OperationState.TYPE_OPERATION.equals(jso.type)) {
            state = parseOperationState(jso);
        } else if (SleepState.TYPE_SLEEP.equals(jso.type)) {
            state = parse(SleepState.class, jso);
        } else if (ParallelState.TYPE_PARALLEL.equals(jso.type)) {
            state = parse(ParallelState.class, jso);
        } else if (ForEachState.TYPE_FOR_EACH.equals(jso.type)) {
            state = parseForEachState(jso);
        } else if (CallbackState.TYPE_CALLBACK.equals(jso.type)) {
            state = parseCallbackState(jso);
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

    private SwitchState parseSwitchState(State jso) {
        SwitchState state = (SwitchState) parse(SwitchState.class, jso);
        DefaultConditionTransition defaultCondition = Js.uncheckedCast(Js.asPropertyMap(jso).get("defaultCondition"));
        if (null != defaultCondition) {
            state.defaultCondition = parse(DefaultConditionTransition.class, defaultCondition);
        }
        EventConditionTransition[] eventConditions = Js.uncheckedCast(Js.asPropertyMap(jso).get("eventConditions"));
        if (null != eventConditions) {
            state.eventConditions = new EventConditionTransition[eventConditions.length];
            for (int i = 0; i < eventConditions.length; i++) {
                EventConditionTransition eventConditionJSO = eventConditions[i];
                EventConditionTransition eventCondition = parse(EventConditionTransition.class, eventConditionJSO);
                state.eventConditions[i] = eventCondition;
            }
        }
        DataConditionTransition[] dataConditions = Js.uncheckedCast(Js.asPropertyMap(jso).get("dataConditions"));
        if (null != dataConditions) {
            state.dataConditions = new DataConditionTransition[dataConditions.length];
            for (int i = 0; i < dataConditions.length; i++) {
                DataConditionTransition dataConditionJSO = dataConditions[i];
                DataConditionTransition dataCondition = parse(DataConditionTransition.class, dataConditionJSO);
                state.dataConditions[i] = dataCondition;
            }
        }
        return state;
    }

    private CallbackState parseCallbackState(State jso) {
        CallbackState state = (CallbackState) parse(CallbackState.class, jso);
        if (null != state.action) {
            state.action = parse(ActionNode.class, state.action);
        }
        return state;
    }

    private ForEachState parseForEachState(State jso) {
        ForEachState state = (ForEachState) parse(ForEachState.class, jso);
        ActionNode[] actions = parseActions(jso);
        if (null != actions) {
            state.actions = actions;
        }
        return state;
    }

    private OperationState parseOperationState(State jso) {
        OperationState state = (OperationState) parse(OperationState.class, jso);
        ActionNode[] actions = parseActions(jso);
        if (null != actions) {
            state.actions = actions;
        }
        return state;
    }

    private ActionNode[] parseActions(State jso) {
        ActionNode[] actions = Js.uncheckedCast(Js.asPropertyMap(jso).get("actions"));
        if (null != actions) {
            ActionNode[] result = new ActionNode[actions.length];
            for (int i = 0; i < actions.length; i++) {
                ActionNode a = actions[i];
                ActionNode action = parseAction(a);
                result[i] = action;
            }
            return result;
        }
        return null;
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
            action.setFunctionRef(jso.functionRef);
        } else if (null != jso.subFlowRef) {
            action = parse(CallSubflowAction.class, jso);
            action.setSubFlowRef(jso.subFlowRef);
        } else if (null != jso.eventRef) {
            action = parse(CallEventAction.class, jso);
            action.setEventRef(jso.eventRef);
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

    private <T> T parse(T workflow, T jso) {
        return MarshallerUtils.parse(workflow, jso);
    }
}