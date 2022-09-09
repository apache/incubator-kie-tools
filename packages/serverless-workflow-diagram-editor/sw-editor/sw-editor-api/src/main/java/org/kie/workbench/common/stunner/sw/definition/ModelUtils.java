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
            workflow.id = (String) Js.undefined();
            workflow.name = (String) Js.undefined();
            workflow.start = (String) Js.undefined();
            if (null != workflow.events) {
                for (int i = 0; i < workflow.events.length; i++) {
                    cleanEvent(workflow.events[i]);
                    workflow.events[i] = (Event) Js.undefined();
                }
            }
            if (null != workflow.states) {
                for (int i = 0; i < workflow.states.length; i++) {
                    cleanState(workflow.states[i]);
                    workflow.states[i] = (State) Js.undefined();
                }
            }
            workflow.events = (Event[]) Js.undefined();
            workflow.states = (State[]) Js.undefined();
        }
    }

    private static void cleanEvent(Event event) {
        if (event != null) {
            event.name = (String) Js.undefined();
            event.source = (String) Js.undefined();
            event.type = (String) Js.undefined();
        }
    }

    private static void cleanState(State state) {
        if (state != null) {
            if (InjectState.TYPE_INJECT.equals(state.type)) {
                cleanInjectState((InjectState) state);
            } else if (EventState.TYPE_EVENT.equals(state.type)) {
                cleanEventState((EventState) state);
            } else if (SwitchState.TYPE_SWITCH.equals(state.type)) {
                cleanSwitchState((SwitchState) state);
            } else if (OperationState.TYPE_OPERATION.equals(state.type)) {
                cleanOperationState((OperationState) state);
            } else if (SleepState.TYPE_SLEEP.equals(state.type)) {
                cleanSleepState((SleepState) state);
            } else if (ParallelState.TYPE_PARALLEL.equals(state.type)) {
                cleanParallelState((ParallelState) state);
            } else if (ForEachState.TYPE_FOR_EACH.equals(state.type)) {
                cleanForEachState((ForEachState) state);
            } else if (CallbackState.TYPE_CALLBACK.equals(state.type)) {
                cleanCallbackState((CallbackState) state);
            }

            state.name = (String) Js.undefined();
            state.type = (String) Js.undefined();
            state.transition = (String) Js.undefined();
            state.eventTimeout = (String) Js.undefined();
            state.compensatedBy = (String) Js.undefined();
            state.end = null;
            if (null != state.onErrors) {
                for (int i = 0; i < state.onErrors.length; i++) {
                    cleanErrorTransition(state.onErrors[i]);
                    state.onErrors[i] = (ErrorTransition) Js.undefined();
                }
            }
            state.onErrors = (ErrorTransition[]) Js.undefined();
        }
    }

    public static void cleanErrorTransition(ErrorTransition errorTransition) {
        if (errorTransition != null) {
            errorTransition.errorRef = (String) Js.undefined();
            errorTransition.transition = (String) Js.undefined();
            errorTransition.end = null;
        }
    }

    public static void cleanActionNode(ActionNode actionNode) {
        if (actionNode != null) {
            actionNode.id = (String) Js.undefined();
            actionNode.name = (String) Js.undefined();
            actionNode.functionRef = (String) Js.undefined();
            actionNode.eventRef = (String) Js.undefined();
            actionNode.subFlowRef = (String) Js.undefined();
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
            operationState.actionMode = (String) Js.undefined();
            operationState.usedForCompensation = false;
            cleanActions(operationState.actions);
            operationState.actions = (ActionNode[]) Js.undefined();
        }
    }

    public static void cleanOnEvent(OnEvent onEvent) {
        if (onEvent != null) {
            if (null != onEvent.eventRefs) {
                Arrays.stream(onEvent.eventRefs).forEach(eventRef -> eventRef = (String) Js.undefined());
            }
            onEvent.eventRefs = (String[]) Js.undefined();
            cleanActions(onEvent.actions);
            onEvent.actions = (ActionNode[]) Js.undefined();
        }
    }

    public static void cleanEventState(EventState eventState) {
        if (eventState != null) {
            eventState.exclusive = false;
            if (null != eventState.onEvents) {
                for (int i = 0; i < eventState.onEvents.length; i++) {
                    cleanOnEvent(eventState.onEvents[i]);
                    eventState.onEvents[i] = (OnEvent) Js.undefined();
                }
            }
            eventState.onEvents = (OnEvent[]) Js.undefined();
        }
    }

    public static void cleanInjectState(InjectState injectState) {
        if (injectState != null) {
            injectState.data = (String) Js.undefined();
            injectState.usedForCompensation = false;
        }
    }

    public static void cleanSwitchState(SwitchState switchState) {
        if (switchState != null) {
            cleanDefaultConditionTransition(switchState.defaultCondition);
            if (null != switchState.eventConditions) {
                for (int i = 0; i < switchState.eventConditions.length; i++) {
                    cleanEventConditionTransition(switchState.eventConditions[i]);
                    switchState.eventConditions[i] = (EventConditionTransition) Js.undefined();
                }
            }
            switchState.eventConditions = (EventConditionTransition[]) Js.undefined();
            if (null != switchState.dataConditions) {
                for (int i = 0; i < switchState.dataConditions.length; i++) {
                    cleanDataConditionTransition(switchState.dataConditions[i]);
                    switchState.dataConditions[i] = (DataConditionTransition) Js.undefined();
                }
            }
            switchState.dataConditions = (DataConditionTransition[]) Js.undefined();
            switchState.usedForCompensation = false;
        }
    }

    public static void cleanDefaultConditionTransition(DefaultConditionTransition defaultConditionTransition) {
        if (defaultConditionTransition != null) {
            defaultConditionTransition.transition = (String) Js.undefined();
            defaultConditionTransition.end = null;
        }
    }

    public static void cleanEventConditionTransition(EventConditionTransition eventConditionTransition) {
        if (eventConditionTransition != null) {
            eventConditionTransition.name = (String) Js.undefined();
            eventConditionTransition.eventRef = (String) Js.undefined();
            eventConditionTransition.transition = (String) Js.undefined();
            eventConditionTransition.end = null;
        }
    }

    public static void cleanDataConditionTransition(DataConditionTransition dataConditionTransition) {
        if (dataConditionTransition != null) {
            dataConditionTransition.name = (String) Js.undefined();
            dataConditionTransition.condition = (String) Js.undefined();
            dataConditionTransition.transition = (String) Js.undefined();
            dataConditionTransition.end = null;
        }
    }

    public static void cleanSleepState(SleepState sleepState) {
        if (sleepState != null) {
            sleepState.duration = (String) Js.undefined();
        }
    }

    public static void cleanParallelState(ParallelState parallelState) {
        //No fields to be cleaned
    }

    public static void cleanForEachState(ForEachState forEachState) {
        if (forEachState != null) {
            cleanActions(forEachState.actions);
            forEachState.actions = (ActionNode[]) Js.undefined();
        }
    }

    public static void cleanCallbackState(CallbackState callbackState) {
        if (callbackState != null) {
            callbackState.eventRef = (String) Js.undefined();
            cleanActionNode(callbackState.action);
            callbackState.action = (ActionNode) Js.undefined();
        }
    }
}
