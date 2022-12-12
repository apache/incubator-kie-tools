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


package org.kie.workbench.common.stunner.sw;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.DomainInitializer;
import org.kie.workbench.common.stunner.sw.definition.ActionTransition;
import org.kie.workbench.common.stunner.sw.definition.ActionsContainer;
import org.kie.workbench.common.stunner.sw.definition.CallFunctionAction;
import org.kie.workbench.common.stunner.sw.definition.CallSubflowAction;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.CompensationTransition;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.EventRef;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.EventTimeout;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.Metadata;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

@ApplicationScoped
public class SWDomainInitializer {

    @Inject
    private DomainInitializer domainInitializer;

    public static final String CATEGORY_STATES = "SWStates";
    public static final String CATEGORY_EVENTS = "SWEvents";
    public static final String CATEGORY_TIMEOUTS = "SWTimeouts";
    public static final String CATEGORY_START = "SWStart";
    public static final String CATEGORY_END = "SWEnd";
    public static final String CATEGORY_ACTIONS = "SWActions";
    public static final String CATEGORY_TRANSITIONS = "SWTransitions";
    public static final String LABEL_WORKFLOW = "workflow";
    public static final String LABEL_ROOT_NODE = "rootNode";
    public static final String LABEL_EVENT = "event";
    public static final String LABEL_ON_EVENTS = "on_events";
    public static final String LABEL_TIMEOUT = "timeout";
    public static final String LABEL_START = "start";
    public static final String LABEL_END = "end";
    public static final String LABEL_ACTION = "action";
    public static final String LABEL_ACTIONS = "actions";
    public static final String LABEL_METADATA = "metadata";
    public static final String LABEL_STATE = "state";
    public static final String LABEL_TRANSITION = "transition";
    public static final String LABEL_TRANSITION_START = "transition_start";
    public static final String LABEL_TRANSITION_ERROR = "transition_error";
    public static final String LABEL_TRANSITION_EVENT_CONDITION = "transition_event_condition";
    public static final String LABEL_TRANSITION_DATA_CONDITION = "transition_data_condition";
    public static final String LABEL_TRANSITION_DEFAULT_CONDITION = "transition_default_condition";
    public static final String LABEL_TRANSITION_COMPENSATION = "transition_compensation";
    public static final String LABEL_TRANSITION_ACTION = "transition_action";

    public void initialize() {
        domainInitializer
                // Definition Set initialization.
                .initializeDefinitionSet(new SWDefinitionSet())
                .initializeDefinitionsField("definitions")
                .initializeDomainQualifier(new SWEditor() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return SWEditor.class;
                    }
                })
                // Categories.
                .initializeCategory(Workflow.class, CATEGORY_STATES)
                .initializeCategory(EventRef.class, CATEGORY_EVENTS)
                .initializeCategory(OnEvent.class, CATEGORY_EVENTS)
                .initializeCategory(EventTimeout.class, CATEGORY_TIMEOUTS)
                .initializeCategory(Start.class, CATEGORY_START)
                .initializeCategory(End.class, CATEGORY_END)
                .initializeCategory(ActionsContainer.class, CATEGORY_EVENTS)
                .initializeCategory(CallFunctionAction.class, CATEGORY_ACTIONS)
                .initializeCategory(CallSubflowAction.class, CATEGORY_ACTIONS)
                .initializeCategory(Metadata.class, CATEGORY_STATES)
                .initializeCategory(State.class, CATEGORY_STATES)
                .initializeCategory(InjectState.class, CATEGORY_STATES)
                .initializeCategory(SwitchState.class, CATEGORY_STATES)
                .initializeCategory(EventState.class, CATEGORY_STATES)
                .initializeCategory(OperationState.class, CATEGORY_STATES)
                .initializeCategory(SleepState.class, CATEGORY_STATES)
                .initializeCategory(ParallelState.class, CATEGORY_STATES)
                .initializeCategory(ForEachState.class, CATEGORY_STATES)
                .initializeCategory(CallbackState.class, CATEGORY_STATES)
                .initializeCategory(Transition.class, CATEGORY_TRANSITIONS)
                .initializeCategory(StartTransition.class, CATEGORY_TRANSITIONS)
                .initializeCategory(ErrorTransition.class, CATEGORY_TRANSITIONS)
                .initializeCategory(EventConditionTransition.class, CATEGORY_TRANSITIONS)
                .initializeCategory(DataConditionTransition.class, CATEGORY_TRANSITIONS)
                .initializeCategory(DefaultConditionTransition.class, CATEGORY_TRANSITIONS)
                .initializeCategory(CompensationTransition.class, CATEGORY_TRANSITIONS)
                .initializeCategory(ActionTransition.class, CATEGORY_TRANSITIONS)
                // Labels.
                .initializeLabels(Workflow.class, LABEL_WORKFLOW)
                .initializeLabels(EventRef.class, LABEL_ROOT_NODE, LABEL_EVENT)
                .initializeLabels(OnEvent.class, LABEL_ROOT_NODE, LABEL_ON_EVENTS)
                .initializeLabels(EventTimeout.class, LABEL_ROOT_NODE, LABEL_TIMEOUT)
                .initializeLabels(Start.class, LABEL_ROOT_NODE, LABEL_START)
                .initializeLabels(End.class, LABEL_ROOT_NODE, LABEL_END)
                .initializeLabels(ActionsContainer.class, LABEL_ROOT_NODE, LABEL_ACTIONS)
                .initializeLabels(CallFunctionAction.class, LABEL_ROOT_NODE, LABEL_ACTION)
                .initializeLabels(CallSubflowAction.class, LABEL_ROOT_NODE, LABEL_ACTION)
                .initializeLabels(Metadata.class, LABEL_ROOT_NODE, LABEL_METADATA)
                .initializeLabels(State.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(InjectState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(SwitchState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(EventState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(OperationState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(SleepState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(ParallelState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(ForEachState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(ForEachState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(ForEachState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(ForEachState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(ForEachState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(CallbackState.class, LABEL_ROOT_NODE, LABEL_STATE)
                .initializeLabels(Transition.class, LABEL_TRANSITION)
                .initializeLabels(StartTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_START)
                .initializeLabels(ErrorTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_ERROR)
                .initializeLabels(EventConditionTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_EVENT_CONDITION)
                .initializeLabels(DataConditionTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_DATA_CONDITION)
                .initializeLabels(DefaultConditionTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_DEFAULT_CONDITION)
                .initializeLabels(CompensationTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_COMPENSATION)
                .initializeLabels(ActionTransition.class, LABEL_TRANSITION_ACTION)
                // Rules.
                .setContainmentRule(OnEvent.class, LABEL_EVENT)
                .setContainmentRule(Workflow.class, LABEL_ROOT_NODE)
                .setContainmentRule(SWDefinitionSet.class, LABEL_WORKFLOW)
                .setContainmentRule(ActionsContainer.class, LABEL_ACTION)
                .setConnectionRule(DataConditionTransition.class,
                                   new String[]{LABEL_STATE, LABEL_STATE},
                                   new String[]{LABEL_STATE, LABEL_END})
                .setConnectionRule(DefaultConditionTransition.class,
                                   new String[]{LABEL_STATE, LABEL_STATE},
                                   new String[]{LABEL_STATE, LABEL_END})
                .setConnectionRule(ErrorTransition.class,
                                   new String[]{LABEL_STATE, LABEL_STATE},
                                   new String[]{LABEL_STATE, LABEL_END})
                .setConnectionRule(EventConditionTransition.class,
                                   new String[]{LABEL_STATE, LABEL_STATE},
                                   new String[]{LABEL_STATE, LABEL_END})
                .setConnectionRule(Transition.class,
                                   new String[]{LABEL_STATE, LABEL_STATE},
                                   new String[]{LABEL_STATE, LABEL_END})
                .setConnectionRule(CompensationTransition.class, new String[]{LABEL_STATE, LABEL_STATE})
                .setConnectionRule(StartTransition.class, new String[]{LABEL_START, LABEL_STATE})
                .setConnectionRule(ActionTransition.class, new String[]{LABEL_EVENT, LABEL_ACTION})
                .setDockingRule(State.class, LABEL_TIMEOUT)
                .setOccurrences(LABEL_WORKFLOW, 0, 1)
                .setOccurrences(LABEL_START, 0, 1)
                .setOccurrences(LABEL_END, 0, 1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_STATE, true, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_STATE, false, 0, 1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_END, true, 0, 0)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_END, false, 0, 0)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_STATE, true, 0, -1)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_STATE, false, 0, 1)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_END, false, 0, 0)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_STATE, true, 0, -1)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_STATE, false, 0, 1)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_END, false, 0, 0)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_STATE, true, 0, -1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_STATE, false, 0, -1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_END, false, 0, 0)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_STATE, true, 0, -1)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_STATE, false, 0, 1)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_END, false, 0, 0)
                .setEdgeOccurrences(StartTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(StartTransition.class, LABEL_START, false, 0, 1)
                .setEdgeOccurrences(Transition.class, LABEL_STATE, true, 0, -1)
                .setEdgeOccurrences(Transition.class, LABEL_STATE, false, 0, 1)
                .setEdgeOccurrences(Transition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(Transition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(Transition.class, LABEL_END, false, 0, 0)
                .setEdgeOccurrences(ActionTransition.class, LABEL_EVENT, true, 0, 0)
                .setEdgeOccurrences(ActionTransition.class, LABEL_EVENT, false, 0, -1)
                .setEdgeOccurrences(ActionTransition.class, LABEL_ACTION, true, 0, -1)
                .setEdgeOccurrences(ActionTransition.class, LABEL_ACTION, false, 0, 0)
                .setEdgeOccurrences(ActionTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(ActionTransition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(ActionTransition.class, LABEL_END, false, 0, 0)
                .initializeRules();
    }
}
