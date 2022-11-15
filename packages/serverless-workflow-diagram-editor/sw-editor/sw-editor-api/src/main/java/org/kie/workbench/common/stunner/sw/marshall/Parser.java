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
import org.kie.workbench.common.stunner.sw.definition.Metadata;
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
        State[] states = jso.getStates();
        workflow.setStates(new State[states.length]);
        for (int i = 0; i < states.length; i++) {
            State s = states[i];
            State state = parseState(s);
            workflow.getStates()[i] = state;
        }
    }

    private State parseState(State jso) {
        State state = null;
        if (InjectState.TYPE_INJECT.equals(jso.getType())) {
            state = parse(InjectState.class, jso);
        } else if (EventState.TYPE_EVENT.equals(jso.getType())) {
            state = parseEventState(jso);
        } else if (SwitchState.TYPE_SWITCH.equals(jso.getType())) {
            state = parseSwitchState(jso);
        } else if (OperationState.TYPE_OPERATION.equals(jso.getType())) {
            state = parseOperationState(jso);
        } else if (SleepState.TYPE_SLEEP.equals(jso.getType())) {
            state = parse(SleepState.class, jso);
        } else if (ParallelState.TYPE_PARALLEL.equals(jso.getType())) {
            state = parse(ParallelState.class, jso);
        } else if (ForEachState.TYPE_FOR_EACH.equals(jso.getType())) {
            state = parseForEachState(jso);
        } else if (CallbackState.TYPE_CALLBACK.equals(jso.getType())) {
            state = parseCallbackState(jso);
        }

        if (null != state) {
            ErrorTransition[] onErrors = jso.getOnErrors();
            if (null != onErrors) {
                state.setOnErrors(new ErrorTransition[onErrors.length]);
                for (int i = 0; i < onErrors.length; i++) {
                    ErrorTransition e = onErrors[i];
                    ErrorTransition et = parseErrorTransition(e);
                    state.getOnErrors()[i] = et;
                }
            }
            Metadata metadata = jso.getMetadata();
            if (null != metadata) {
                state.metadata = metadata;
            }
        }

        return state;
    }

    private SwitchState parseSwitchState(State jso) {
        SwitchState state = (SwitchState) parse(SwitchState.class, jso);
        DefaultConditionTransition defaultCondition = state.getDefaultCondition();
        if (null != defaultCondition) {
            state.setDefaultCondition(parse(DefaultConditionTransition.class, defaultCondition));
        }
        EventConditionTransition[] eventConditions = state.getEventConditions();
        if (null != eventConditions) {
            state.setEventConditions(new EventConditionTransition[eventConditions.length]);
            for (int i = 0; i < eventConditions.length; i++) {
                EventConditionTransition eventConditionJSO = eventConditions[i];
                EventConditionTransition eventCondition = parse(EventConditionTransition.class, eventConditionJSO);
                state.getEventConditions()[i] = eventCondition;
            }
        }
        DataConditionTransition[] dataConditions = state.getDataConditions();
        if (null != dataConditions) {
            state.setDataConditions(new DataConditionTransition[dataConditions.length]);
            for (int i = 0; i < dataConditions.length; i++) {
                DataConditionTransition dataConditionJSO = dataConditions[i];
                DataConditionTransition dataCondition = parse(DataConditionTransition.class, dataConditionJSO);
                state.getDataConditions()[i] = dataCondition;
            }
        }
        return state;
    }

    private CallbackState parseCallbackState(State jso) {
        CallbackState state = (CallbackState) parse(CallbackState.class, jso);
        if (null != state.getAction()) {
            state.setAction(parse(ActionNode.class, state.getAction()));
        }
        return state;
    }

    private ForEachState parseForEachState(State jso) {
        ForEachState state = (ForEachState) parse(ForEachState.class, jso);
        ActionNode[] actions = state.getActions();
        if (null != actions) {
            state.setActions(actions);
        }
        return state;
    }

    private OperationState parseOperationState(State jso) {
        OperationState state = (OperationState) parse(OperationState.class, jso);
        ActionNode[] actions = state.getActions();
        if (null != actions) {
            state.setActions(actions);
        }
        return state;
    }

    private EventState parseEventState(State jso) {
        EventState state = (EventState) parse(EventState.class, jso);
        OnEvent[] onEvents = state.getOnEvents();
        if (null != onEvents) {
            state.setOnEvents(new OnEvent[onEvents.length]);
            for (int i = 0; i < onEvents.length; i++) {
                OnEvent e = onEvents[i];
                OnEvent onEvent = parseOnEvent(e);
                state.getOnEvents()[i] = onEvent;
            }
        }
        return state;
    }

    private OnEvent parseOnEvent(OnEvent jso) {
        OnEvent onEvent = parse(OnEvent.class, jso);
        ActionNode[] actions = jso.getActions();
        if (null != actions) {
            onEvent.setActions(new ActionNode[actions.length]);
            for (int i = 0; i < actions.length; i++) {
                ActionNode a = actions[i];
                ActionNode action = parseAction(a);
                onEvent.getActions()[i] = action;
            }
        }
        return onEvent;
    }

    private ActionNode parseAction(ActionNode jso) {
        ActionNode action = null;
        if (null != jso.getFunctionRef()) {
            action = parse(CallFunctionAction.class, jso);
            action.setFunctionRef(jso.getFunctionRef());
        } else if (null != jso.getSubFlowRef()) {
            action = parse(CallSubflowAction.class, jso);
            action.setSubFlowRef(jso.getSubFlowRef());
        } else if (null != jso.getEventRef()) {
            action = parse(CallEventAction.class, jso);
            action.setEventRef(jso.getEventRef());
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