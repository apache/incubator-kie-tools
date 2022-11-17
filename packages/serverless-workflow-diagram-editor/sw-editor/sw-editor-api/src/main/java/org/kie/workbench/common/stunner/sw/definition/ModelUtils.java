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

package org.kie.workbench.common.stunner.sw.definition;

import java.util.Arrays;

import jsinterop.base.Js;

public class ModelUtils {

    public static void cleanWorkflow(Workflow workflow) {
        if (workflow != null) {
            workflow.setId((String) Js.undefined());
            workflow.setName((String) Js.undefined());
            workflow.setStart((String) Js.undefined());
            if (null != workflow.getEvents()) {
                if(workflow.getEvents() instanceof Event[]) {
                    Event[] events = (Event[]) workflow.getEvents();
                    for (int i = 0; i < events.length; i++) {
                        cleanEvent(events[i]);
                        events[i] = (Event) Js.undefined();
                    }
                }
            }
            if (null != workflow.getStates()) {
                for (int i = 0; i < workflow.getStates().length; i++) {
                    cleanState(workflow.getStates()[i]);
                    workflow.getStates()[i] = (State) Js.undefined();
                }
            }
            workflow.setEvents((Event[]) Js.undefined());
            workflow.setStates((State[]) Js.undefined());
        }
    }

    private static void cleanEvent(Event event) {
        if (event != null) {
            event.setName((String) Js.undefined());
            event.setSource((String) Js.undefined());
            event.setType((String) Js.undefined());
        }
    }

    private static void cleanState(State state) {
        if (state != null) {
            if (InjectState.TYPE_INJECT.equals(state.getType())) {
                cleanInjectState((InjectState) state);
            } else if (EventState.TYPE_EVENT.equals(state.getType())) {
                cleanEventState((EventState) state);
            } else if (SwitchState.TYPE_SWITCH.equals(state.getType())) {
                cleanSwitchState((SwitchState) state);
            } else if (OperationState.TYPE_OPERATION.equals(state.getType())) {
                cleanOperationState((OperationState) state);
            } else if (SleepState.TYPE_SLEEP.equals(state.getType())) {
                cleanSleepState((SleepState) state);
            } else if (ParallelState.TYPE_PARALLEL.equals(state.getType())) {
                cleanParallelState((ParallelState) state);
            } else if (ForEachState.TYPE_FOR_EACH.equals(state.getType())) {
                cleanForEachState((ForEachState) state);
            } else if (CallbackState.TYPE_CALLBACK.equals(state.getType())) {
                cleanCallbackState((CallbackState) state);
            }


            state.setName((String) Js.undefined());
            state.setType((String) Js.undefined());
            state.setTransition((String) Js.undefined());
            state.setEventTimeout((String) Js.undefined());
            state.setCompensatedBy((String) Js.undefined());
            state.setEnd(false);
            if (null != state.getOnErrors()) {
                for (int i = 0; i < state.getOnErrors().length; i++) {
                    cleanErrorTransition(state.getOnErrors()[i]);
                    state.getOnErrors()[i] = (ErrorTransition) Js.undefined();
                }
            }
            state.setOnErrors((ErrorTransition[]) Js.undefined());
        }
    }

    public static void cleanErrorTransition(ErrorTransition errorTransition) {
        if (errorTransition != null) {
            errorTransition.setErrorRef((String) Js.undefined());
            errorTransition.setTransition(Js.undefined());
            errorTransition.setEnd(false);
        }
    }

    public static void cleanActionNode(ActionNode actionNode) {
        if (actionNode != null) {
            actionNode.setId((String) Js.undefined());
            actionNode.setName((String) Js.undefined());
            actionNode.setFunctionRef((String) Js.undefined());
            actionNode.setEventRef((ActionEventRef) Js.undefined());
            actionNode.setSubFlowRef((Object) Js.undefined());
        }
    }

    public static void cleanActions(ActionNode[] actionNodes) {
        if (null != actionNodes) {
            for (int i = 0; i < actionNodes.length; i++) {
                cleanActionNode(actionNodes[i]);
                actionNodes[i] = (ActionNode) Js.undefined();
            }
        }
    }

    public static void cleanOperationState(OperationState operationState) {
        if (operationState != null) {
            operationState.setActionMode((String) Js.undefined());
            operationState.setUsedForCompensation(false);
            cleanActions(operationState.getActions());
            operationState.setActions((ActionNode[]) Js.undefined());
        }
    }

    public static void cleanOnEvent(OnEvent onEvent) {
        if (onEvent != null) {
            if (null != onEvent.getEventRefs()) {
                Arrays.stream(onEvent.getEventRefs()).forEach(eventRef -> eventRef = (String) Js.undefined());
            }
            onEvent.setEventRefs((String[]) Js.undefined());
            cleanActions(onEvent.getActions());
            onEvent.setActions((ActionNode[]) Js.undefined());
        }
    }

    public static void cleanEventState(EventState eventState) {
        if (eventState != null) {
            eventState.setExclusive(false);
            if (null != eventState.getOnEvents()) {
                for (int i = 0; i < eventState.getOnEvents().length; i++) {
                    cleanOnEvent(eventState.getOnEvents()[i]);
                    eventState.getOnEvents()[i] = (OnEvent) Js.undefined();
                }
            }
            eventState.setOnEvents((OnEvent[]) Js.undefined());
        }
    }

    public static void cleanInjectState(InjectState injectState) {
        if (injectState != null) {
            injectState.setData((Data) Js.undefined());
            injectState.setUsedForCompensation(false);
        }
    }

    public static void cleanSwitchState(SwitchState switchState) {
        if (switchState != null) {
            cleanDefaultConditionTransition(switchState.getDefaultCondition());
            if (null != switchState.getEventConditions()) {
                for (int i = 0; i < switchState.getEventConditions().length; i++) {
                    cleanEventConditionTransition(switchState.getEventConditions()[i]);
                    switchState.getEventConditions()[i] = (EventConditionTransition) Js.undefined();
                }
            }
            switchState.setEventConditions((EventConditionTransition[]) Js.undefined());
            if (null != switchState.getDataConditions()) {
                for (int i = 0; i < switchState.getDataConditions().length; i++) {
                    cleanDataConditionTransition(switchState.getDataConditions()[i]);
                    switchState.getDataConditions()[i] = (DataConditionTransition) Js.undefined();
                }
            }
            switchState.setDataConditions((DataConditionTransition[]) Js.undefined());
            switchState.setUsedForCompensation(false);
        }
    }

    public static void cleanDefaultConditionTransition(DefaultConditionTransition defaultConditionTransition) {
        if (defaultConditionTransition != null) {
            defaultConditionTransition.setTransition(Js.undefined());
            defaultConditionTransition.setEnd(false);
        }
    }

    public static void cleanEventConditionTransition(EventConditionTransition eventConditionTransition) {
        if (eventConditionTransition != null) {
            eventConditionTransition.setName((String) Js.undefined());
            eventConditionTransition.setEventRef((String) Js.undefined());
            eventConditionTransition.setTransition((String) Js.undefined());
            eventConditionTransition.setEnd(false);
        }
    }

    public static void cleanDataConditionTransition(DataConditionTransition dataConditionTransition) {
        if (dataConditionTransition != null) {
            dataConditionTransition.setName((String) Js.undefined());
            dataConditionTransition.setCondition((String) Js.undefined());
            dataConditionTransition.setTransition((String) Js.undefined());
            dataConditionTransition.setEnd(false);
        }
    }

    public static void cleanSleepState(SleepState sleepState) {
        if (sleepState != null) {
            sleepState.setDuration((String) Js.undefined());
        }
    }

    public static void cleanParallelState(ParallelState parallelState) {
        //No fields to be cleaned
    }

    public static void cleanForEachState(ForEachState forEachState) {
        if (forEachState != null) {
            cleanActions(forEachState.getActions());
            forEachState.setActions((ActionNode[]) Js.undefined());
        }
    }

    public static void cleanCallbackState(CallbackState callbackState) {
        if (callbackState != null) {
            callbackState.setEventRef((String) Js.undefined());
            cleanActionNode(callbackState.getAction());
            callbackState.setAction((ActionNode) Js.undefined());
        }
    }
}
