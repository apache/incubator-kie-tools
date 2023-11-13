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



package org.kie.workbench.common.stunner.sw;

import java.lang.annotation.Annotation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.client.api.DomainInitializer;
import org.kie.workbench.common.stunner.core.factory.definition.JsDefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.sw.definition.ActionDataFilters;
import org.kie.workbench.common.stunner.sw.definition.ActionEventRef;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.ActionTransition;
import org.kie.workbench.common.stunner.sw.definition.ActionsContainer;
import org.kie.workbench.common.stunner.sw.definition.CallEventAction;
import org.kie.workbench.common.stunner.sw.definition.CallFunctionAction;
import org.kie.workbench.common.stunner.sw.definition.CallSubflowAction;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.CompensationTransition;
import org.kie.workbench.common.stunner.sw.definition.ContinueAs;
import org.kie.workbench.common.stunner.sw.definition.Correlation;
import org.kie.workbench.common.stunner.sw.definition.Data;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.Event;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.EventDataFilter;
import org.kie.workbench.common.stunner.sw.definition.EventRef;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.EventTimeout;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.Function;
import org.kie.workbench.common.stunner.sw.definition.FunctionRef;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.Metadata;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.ParallelStateBranch;
import org.kie.workbench.common.stunner.sw.definition.ProducedEvent;
import org.kie.workbench.common.stunner.sw.definition.Retry;
import org.kie.workbench.common.stunner.sw.definition.Schedule;
import org.kie.workbench.common.stunner.sw.definition.Sleep;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.StartDefinition;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.StateDataFilter;
import org.kie.workbench.common.stunner.sw.definition.StateEnd;
import org.kie.workbench.common.stunner.sw.definition.StateTransition;
import org.kie.workbench.common.stunner.sw.definition.SubFlowRef;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Timeout;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.kie.workbench.common.stunner.sw.definition.WorkflowExecTimeout;
import org.kie.workbench.common.stunner.sw.definition.WorkflowTimeouts;

@ApplicationScoped
public class SWDomainInitializer {

    @Inject
    private DomainInitializer domainInitializer;

    @Inject
    private JsDefinitionFactory jsDefinitionFactory;

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
    public static final String LABEL_TRANSITION = "transition";
    public static final String LABEL_TRANSITION_START = "transition_start";
    public static final String LABEL_TRANSITION_ERROR = "transition_error";
    public static final String LABEL_TRANSITION_EVENT_CONDITION = "transition_event_condition";
    public static final String LABEL_TRANSITION_DATA_CONDITION = "transition_data_condition";
    public static final String LABEL_TRANSITION_DEFAULT_CONDITION = "transition_default_condition";
    public static final String LABEL_TRANSITION_COMPENSATION = "transition_compensation";
    public static final String LABEL_TRANSITION_ACTION = "transition_action";
    public static final String LABEL_INJECT_STATE = "inject_state";
    public static final String LABEL_EVENT_STATE = "event_state";
    public static final String LABEL_OPERATION_STATE = "operation_state";
    public static final String LABEL_SWITCH_STATE = "switch_state";
    public static final String LABEL_SLEEP_STATE = "sleep_state";
    public static final String LABEL_PARALLEL_STATE = "parallel_state";
    public static final String LABEL_FOREACH_STATE = "foreach_state";
    public static final String LABEL_CALLBACK_STATE = "callback_state";

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

                // Element Factories
                .initializeElementFactory(NodeFactory.class, CATEGORY_STATES)
                .initializeElementFactory(EdgeFactory.class, CATEGORY_TRANSITIONS)

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
                .initializeLabels(ActionsContainer.class, LABEL_ROOT_NODE, LABEL_ACTIONS)
                .initializeLabels(CallFunctionAction.class, LABEL_ROOT_NODE, LABEL_ACTION)
                .initializeLabels(CallSubflowAction.class, LABEL_ROOT_NODE, LABEL_ACTION)
                .initializeLabels(Metadata.class, LABEL_ROOT_NODE, LABEL_METADATA)
                .initializeLabels(Transition.class, LABEL_TRANSITION)
                .initializeLabels(StartTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_START)
                .initializeLabels(ErrorTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_ERROR)
                .initializeLabels(EventConditionTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_EVENT_CONDITION)
                .initializeLabels(DataConditionTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_DATA_CONDITION)
                .initializeLabels(DefaultConditionTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_DEFAULT_CONDITION)
                .initializeLabels(CompensationTransition.class, LABEL_TRANSITION, LABEL_TRANSITION_COMPENSATION)
                .initializeLabels(ActionTransition.class, LABEL_TRANSITION_ACTION)
                .initializeLabels(Start.class, LABEL_ROOT_NODE, LABEL_START)
                .initializeLabels(End.class, LABEL_ROOT_NODE, LABEL_END)
                .initializeLabels(InjectState.class, LABEL_ROOT_NODE, LABEL_INJECT_STATE)
                .initializeLabels(EventState.class, LABEL_ROOT_NODE, LABEL_EVENT_STATE)
                .initializeLabels(OperationState.class, LABEL_ROOT_NODE, LABEL_OPERATION_STATE)
                .initializeLabels(SwitchState.class, LABEL_ROOT_NODE, LABEL_SWITCH_STATE)
                .initializeLabels(SleepState.class, LABEL_ROOT_NODE, LABEL_SLEEP_STATE)
                .initializeLabels(ParallelState.class, LABEL_ROOT_NODE, LABEL_PARALLEL_STATE)
                .initializeLabels(ForEachState.class, LABEL_ROOT_NODE, LABEL_FOREACH_STATE)
                .initializeLabels(CallbackState.class, LABEL_ROOT_NODE, LABEL_CALLBACK_STATE)

                // Occurrence Rules
                .setOccurrences(LABEL_WORKFLOW, 0, 1)
                .setOccurrences(LABEL_START, 0, 1)
                .setOccurrences(LABEL_END, 0, 1)
                .setOccurrences(LABEL_INJECT_STATE, 0, -1)
                .setOccurrences(LABEL_EVENT_STATE, 0, -1)
                .setOccurrences(LABEL_OPERATION_STATE, 0, -1)
                .setOccurrences(LABEL_SWITCH_STATE, 0, -1)
                .setOccurrences(LABEL_SLEEP_STATE, 0, -1)
                .setOccurrences(LABEL_PARALLEL_STATE, 0, -1)
                .setOccurrences(LABEL_FOREACH_STATE, 0, -1)
                .setOccurrences(LABEL_CALLBACK_STATE, 0, -1)

                // Docking Rules
                .setDockingRule(State.class, LABEL_TIMEOUT)

                // Containment Rules
                .setContainmentRule(OnEvent.class, LABEL_EVENT)
                .setContainmentRule(Workflow.class, LABEL_ROOT_NODE)
                .setContainmentRule(SWDefinitionSet.class, LABEL_WORKFLOW)
                .setContainmentRule(ActionsContainer.class, LABEL_ACTION)

                // Connection Rules
                .setConnectionRule(StartTransition.class,
                                   // Start State
                                   new String[]{LABEL_START, LABEL_INJECT_STATE},
                                   new String[]{LABEL_START, LABEL_EVENT_STATE},
                                   new String[]{LABEL_START, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_START, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_START, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_START, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_START, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_START, LABEL_CALLBACK_STATE})
                .setConnectionRule(Transition.class,
                                   // Inject State
                                   new String[]{LABEL_INJECT_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_CALLBACK_STATE},
                                   // Event State
                                   new String[]{LABEL_EVENT_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_CALLBACK_STATE},
                                   // Operation State
                                   new String[]{LABEL_OPERATION_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_CALLBACK_STATE},
                                   // Sleep State
                                   new String[]{LABEL_SLEEP_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_SLEEP_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_SLEEP_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_SLEEP_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_SLEEP_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_SLEEP_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_SLEEP_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_SLEEP_STATE, LABEL_CALLBACK_STATE},
                                   // Parallel State
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_CALLBACK_STATE},
                                   // Foreach State
                                   new String[]{LABEL_FOREACH_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_CALLBACK_STATE},
                                   // Callback State
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_CALLBACK_STATE})
                .setConnectionRule(CompensationTransition.class,
                                   // Inject State
                                   new String[]{LABEL_INJECT_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_INJECT_STATE, LABEL_CALLBACK_STATE},
                                   // Event State
                                   new String[]{LABEL_EVENT_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_CALLBACK_STATE},
                                   // Operation State
                                   new String[]{LABEL_OPERATION_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_CALLBACK_STATE},
                                   // Switch State
                                   new String[]{LABEL_SWITCH_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_CALLBACK_STATE},
                                   // Parallel State
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_CALLBACK_STATE},
                                   // Foreach State
                                   new String[]{LABEL_FOREACH_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_CALLBACK_STATE},
                                   // Callback State
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_CALLBACK_STATE})
                .setConnectionRule(ErrorTransition.class,
                                   // Event State
                                   new String[]{LABEL_EVENT_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_EVENT_STATE, LABEL_CALLBACK_STATE},
                                   // Operation State
                                   new String[]{LABEL_OPERATION_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_OPERATION_STATE, LABEL_CALLBACK_STATE},
                                   // Switch State
                                   new String[]{LABEL_SWITCH_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_CALLBACK_STATE},
                                   // Parallel State
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_PARALLEL_STATE, LABEL_CALLBACK_STATE},
                                   // Foreach State
                                   new String[]{LABEL_FOREACH_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_FOREACH_STATE, LABEL_CALLBACK_STATE},
                                   // Callback State
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_CALLBACK_STATE, LABEL_CALLBACK_STATE})
                .setConnectionRule(DataConditionTransition.class,
                                   // Switch State
                                   new String[]{LABEL_SWITCH_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_CALLBACK_STATE})
                .setConnectionRule(DefaultConditionTransition.class,
                                   // Switch State
                                   new String[]{LABEL_SWITCH_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_CALLBACK_STATE})
                .setConnectionRule(EventConditionTransition.class,
                                   // Switch State
                                   new String[]{LABEL_SWITCH_STATE, LABEL_INJECT_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_EVENT_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_OPERATION_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_SWITCH_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_SLEEP_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_PARALLEL_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_FOREACH_STATE},
                                   new String[]{LABEL_SWITCH_STATE, LABEL_CALLBACK_STATE})
                .setConnectionRule(ActionTransition.class, new String[]{LABEL_EVENT, LABEL_ACTION})

                // Edge Occurrence Rules
                // Event
                .setEdgeOccurrences(ActionTransition.class, LABEL_EVENT, true, 0, 0)
                .setEdgeOccurrences(ActionTransition.class, LABEL_EVENT, false, 0, -1)
                // Action
                .setEdgeOccurrences(ActionTransition.class, LABEL_ACTION, true, 0, -1)
                .setEdgeOccurrences(ActionTransition.class, LABEL_ACTION, false, 0, 0)
                // Start State
                .setEdgeOccurrences(StartTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(StartTransition.class, LABEL_START, false, 0, 1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(Transition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(Transition.class, LABEL_START, false, 0, 0)
                .setEdgeOccurrences(ActionTransition.class, LABEL_START, true, 0, 0)
                .setEdgeOccurrences(ActionTransition.class, LABEL_START, false, 0, 0)
                // End State
                .setEdgeOccurrences(CompensationTransition.class, LABEL_END, true, 0, 0)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_END, false, 0, 0)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_END, false, 0, 0)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_END, false, 0, 0)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_END, false, 0, 0)
                .setEdgeOccurrences(Transition.class, LABEL_END, false, 0, 0)
                .setEdgeOccurrences(ActionTransition.class, LABEL_END, false, 0, 0)
                // Inject State
                .setEdgeOccurrences(Transition.class, LABEL_INJECT_STATE, true, 0, -1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_INJECT_STATE, true, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_INJECT_STATE, true, 0, -1)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_INJECT_STATE, true, 0, -1)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_INJECT_STATE, true, 0, -1)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_INJECT_STATE, true, 0, -1)
                .setEdgeOccurrences(Transition.class, LABEL_INJECT_STATE, false, 0, 1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_INJECT_STATE, false, 0, 1)
                // Event State
                .setEdgeOccurrences(Transition.class, LABEL_EVENT_STATE, true, 0, -1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_EVENT_STATE, true, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_EVENT_STATE, true, 0, -1)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_EVENT_STATE, true, 0, -1)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_EVENT_STATE, true, 0, -1)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_EVENT_STATE, true, 0, -1)
                .setEdgeOccurrences(Transition.class, LABEL_EVENT_STATE, false, 0, 1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_EVENT_STATE, false, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_EVENT_STATE, false, 0, 1)
                // Operation State
                .setEdgeOccurrences(Transition.class, LABEL_OPERATION_STATE, true, 0, -1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_OPERATION_STATE, true, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_OPERATION_STATE, true, 0, -1)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_OPERATION_STATE, true, 0, -1)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_OPERATION_STATE, true, 0, -1)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_OPERATION_STATE, true, 0, -1)
                .setEdgeOccurrences(Transition.class, LABEL_OPERATION_STATE, false, 0, 1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_OPERATION_STATE, false, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_OPERATION_STATE, false, 0, 1)
                // Switch State
                .setEdgeOccurrences(Transition.class, LABEL_SWITCH_STATE, true, 0, -1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_SWITCH_STATE, true, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_SWITCH_STATE, true, 0, -1)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_SWITCH_STATE, true, 0, -1)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_SWITCH_STATE, true, 0, -1)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_SWITCH_STATE, true, 0, -1)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_SWITCH_STATE, false, 1, 1)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_SWITCH_STATE, false, 0, -1)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_SWITCH_STATE, false, 0, -1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_SWITCH_STATE, false, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_SWITCH_STATE, false, 0, 1)
                // Sleep State
                .setEdgeOccurrences(Transition.class, LABEL_SLEEP_STATE, true, 0, -1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_SLEEP_STATE, true, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_SLEEP_STATE, true, 0, -1)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_SLEEP_STATE, true, 0, -1)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_SLEEP_STATE, true, 0, -1)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_SLEEP_STATE, true, 0, -1)
                .setEdgeOccurrences(Transition.class, LABEL_SLEEP_STATE, false, 0, 1)
                // Parallel State
                .setEdgeOccurrences(Transition.class, LABEL_PARALLEL_STATE, true, 0, -1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_PARALLEL_STATE, true, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_PARALLEL_STATE, true, 0, -1)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_PARALLEL_STATE, true, 0, -1)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_PARALLEL_STATE, true, 0, -1)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_PARALLEL_STATE, true, 0, -1)
                .setEdgeOccurrences(Transition.class, LABEL_PARALLEL_STATE, false, 0, 1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_PARALLEL_STATE, false, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_PARALLEL_STATE, false, 0, 1)
                // Foreach State
                .setEdgeOccurrences(Transition.class, LABEL_FOREACH_STATE, true, 0, -1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_FOREACH_STATE, true, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_FOREACH_STATE, true, 0, -1)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_FOREACH_STATE, true, 0, -1)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_FOREACH_STATE, true, 0, -1)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_FOREACH_STATE, true, 0, -1)
                .setEdgeOccurrences(Transition.class, LABEL_FOREACH_STATE, false, 0, 1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_FOREACH_STATE, false, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_FOREACH_STATE, false, 0, 1)
                // Callback State
                .setEdgeOccurrences(Transition.class, LABEL_CALLBACK_STATE, true, 0, -1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_CALLBACK_STATE, true, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_CALLBACK_STATE, true, 0, -1)
                .setEdgeOccurrences(EventConditionTransition.class, LABEL_CALLBACK_STATE, true, 0, -1)
                .setEdgeOccurrences(DefaultConditionTransition.class, LABEL_CALLBACK_STATE, true, 0, -1)
                .setEdgeOccurrences(DataConditionTransition.class, LABEL_CALLBACK_STATE, true, 0, -1)
                .setEdgeOccurrences(Transition.class, LABEL_CALLBACK_STATE, false, 0, 1)
                .setEdgeOccurrences(ErrorTransition.class, LABEL_CALLBACK_STATE, false, 0, -1)
                .setEdgeOccurrences(CompensationTransition.class, LABEL_CALLBACK_STATE, false, 0, 1)

                .initializeRules();

        initDefinitions();
    }

    private void initDefinitions() {
        jsDefinitionFactory.register(ActionDataFilters.class, ActionDataFilters::new);
        jsDefinitionFactory.register(ActionEventRef.class, ActionEventRef::new);
        jsDefinitionFactory.register(ActionNode.class, ActionNode::new);
        jsDefinitionFactory.register(ActionsContainer.class, ActionsContainer::new);
        jsDefinitionFactory.register(ActionTransition.class, ActionTransition::new);
        jsDefinitionFactory.register(CallbackState.class, CallbackState::new);
        jsDefinitionFactory.register(CallEventAction.class, CallEventAction::new);
        jsDefinitionFactory.register(CallFunctionAction.class, CallFunctionAction::new);
        jsDefinitionFactory.register(CallSubflowAction.class, CallSubflowAction::new);
        jsDefinitionFactory.register(CompensationTransition.class, CompensationTransition::new);
        jsDefinitionFactory.register(ContinueAs.class, ContinueAs::new);
        jsDefinitionFactory.register(Correlation.class, Correlation::new);
        jsDefinitionFactory.register(Data.class, Data::new);
        jsDefinitionFactory.register(DataConditionTransition.class, DataConditionTransition::new);
        jsDefinitionFactory.register(DefaultConditionTransition.class, DefaultConditionTransition::new);
        jsDefinitionFactory.register(End.class, End::new);
        jsDefinitionFactory.register(Error.class, Error::new);
        jsDefinitionFactory.register(ErrorTransition.class, ErrorTransition::new);
        jsDefinitionFactory.register(Event.class, Event::new);
        jsDefinitionFactory.register(EventConditionTransition.class, EventConditionTransition::new);
        jsDefinitionFactory.register(EventDataFilter.class, EventDataFilter::new);
        jsDefinitionFactory.register(EventRef.class, EventRef::new);
        jsDefinitionFactory.register(EventState.class, EventState::new);
        jsDefinitionFactory.register(EventTimeout.class, EventTimeout::new);
        jsDefinitionFactory.register(ForEachState.class, ForEachState::new);
        jsDefinitionFactory.register(Function.class, Function::new);
        jsDefinitionFactory.register(FunctionRef.class, FunctionRef::new);
        jsDefinitionFactory.register(InjectState.class, InjectState::new);
        jsDefinitionFactory.register(OnEvent.class, OnEvent::new);
        jsDefinitionFactory.register(OperationState.class, OperationState::new);
        jsDefinitionFactory.register(ParallelState.class, ParallelState::new);
        jsDefinitionFactory.register(ParallelStateBranch.class, ParallelStateBranch::new);
        jsDefinitionFactory.register(ProducedEvent.class, ProducedEvent::new);
        jsDefinitionFactory.register(Retry.class, Retry::new);
        jsDefinitionFactory.register(Schedule.class, Schedule::new);
        jsDefinitionFactory.register(Sleep.class, Sleep::new);
        jsDefinitionFactory.register(SleepState.class, SleepState::new);
        jsDefinitionFactory.register(Start.class, Start::new);
        jsDefinitionFactory.register(StartDefinition.class, StartDefinition::new);
        jsDefinitionFactory.register(StartTransition.class, StartTransition::new);
        jsDefinitionFactory.register(State.class, State::new);
        jsDefinitionFactory.register(StateDataFilter.class, StateDataFilter::new);
        jsDefinitionFactory.register(StateEnd.class, StateEnd::new);
        jsDefinitionFactory.register(StateTransition.class, StateTransition::new);
        jsDefinitionFactory.register(SubFlowRef.class, SubFlowRef::new);
        jsDefinitionFactory.register(SwitchState.class, SwitchState::new);
        jsDefinitionFactory.register(Timeout.class, Timeout::new);
        jsDefinitionFactory.register(Transition.class, Transition::new);
        jsDefinitionFactory.register(Workflow.class, Workflow::new);
        jsDefinitionFactory.register(WorkflowExecTimeout.class, WorkflowExecTimeout::new);
        jsDefinitionFactory.register(WorkflowTimeouts.class, WorkflowTimeouts::new);
    }
}
