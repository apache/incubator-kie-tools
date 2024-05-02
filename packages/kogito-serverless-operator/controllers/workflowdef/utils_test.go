/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package workflowdef

import (
	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	. "github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"
	cncfmodel "github.com/serverlessworkflow/sdk-go/v2/model"
)

var (
	emptyDuration = ""
	isoDuration   = "PT30S"
)

var _ = DescribeTable("Workflow has timeouts",
	func(workflow *operatorapi.SonataFlow, expectedHasTimeouts bool) {
		hasTimeouts := HasTimeouts(workflow)
		Expect(hasTimeouts).Should(Equal(expectedHasTimeouts))
	},
	Entry("for a workflow with WorkflowExecTimeout", workflowWithWorkflowExecTimeout(&isoDuration), true),
	Entry("for a workflow with empty WorkflowExecTimeout", workflowWithWorkflowExecTimeout(&emptyDuration), false),
	Entry("for a workflow with nil WorkflowExecTimeout", workflowWithWorkflowExecTimeout(&emptyDuration), false),

	Entry("for a workflow with WorkflowEventTimeout", workflowWithWorkflowEventStateTimeout(&isoDuration), true),
	Entry("for a workflow with empty WorkflowEventTimeout", workflowWithWorkflowEventStateTimeout(&emptyDuration), false),
	Entry("for a workflow with nil WorkflowEventTimeout", workflowWithWorkflowEventStateTimeout(nil), false),

	Entry("for a workflow with EventState with timeouts", workflowWithEventStateWithTimeout(&isoDuration), true),
	Entry("for a workflow with EventState empty timeouts", workflowWithEventStateWithTimeout(&emptyDuration), false),
	Entry("for a workflow with EventState nil timeouts", workflowWithEventStateWithTimeout(&emptyDuration), false),
	Entry("for a workflow with EventState with action sleep at before", workflowWithEventStateWithActionSleep(true, false), true),
	Entry("for a workflow with EventState with action sleep at before", workflowWithEventStateWithActionSleep(false, true), true),

	Entry("for a workflow with OperationState with action sleep at before", workflowWithEventStateWithActionSleep(true, false), true),
	Entry("for a workflow with OperationState with with action sleep at after", workflowWithEventStateWithActionSleep(false, true), true),
	Entry("for a workflow with OperationState with no action sleep", workflowWithEventStateWithActionSleep(false, false), false),

	Entry("for a workflow with SwitchState with timeouts", workflowWithSwitchStateWithTimeout(&isoDuration), true),
	Entry("for a workflow with SwitchState with empty timeouts", workflowWithSwitchStateWithTimeout(&emptyDuration), false),
	Entry("for a workflow with SwitchState with nil timeouts", workflowWithSwitchStateWithTimeout(nil), false),

	Entry("for a workflow with SleepState", workflowWithSleepState(), true),

	Entry("for a workflow with ParallelState with branch with sleep at before", workflowWithParallelState(true, false), true),
	Entry("for a workflow with ParallelState with branch with sleep at after", workflowWithParallelState(false, true), true),
	Entry("for a workflow with ParallelState with branches with sleep at before and after", workflowWithParallelState(true, true), true),
	Entry("for a workflow with ParallelState with no sleep branches", workflowWithParallelState(false, false), false),

	Entry("for a workflow with ForEachState with action sleep at before", workflowWithForEachStateWithActionSleep(true, false), true),
	Entry("for a workflow with ForEachState with with action sleep at after", workflowWithForEachStateWithActionSleep(false, true), true),
	Entry("for a workflow with ForEachState with no action sleep", workflowWithForEachStateWithActionSleep(false, false), false),

	Entry("for a workflow with CallbackState with timeouts", workflowWithCallbackStateTimeoutAndActionSleep(&isoDuration, nil, nil), true),
	Entry("for a workflow with CallbackState with nil timeouts and before action sleep", workflowWithCallbackStateTimeoutAndActionSleep(nil, &isoDuration, nil), true),
	Entry("for a workflow with CallbackState with nil timeouts and after action sleep", workflowWithCallbackStateTimeoutAndActionSleep(nil, nil, &isoDuration), true),
	Entry("for a workflow with CallbackState with nil timeouts and no action sleep", workflowWithCallbackStateTimeoutAndActionSleep(nil, nil, nil), false),
)

func workflowWithWorkflowExecTimeout(duration *string) *operatorapi.SonataFlow {
	wf := generateWorkflow()
	if duration != nil {
		wf.Spec.Flow.Timeouts = &cncfmodel.Timeouts{}
		wf.Spec.Flow.Timeouts.WorkflowExecTimeout = &cncfmodel.WorkflowExecTimeout{
			Duration: *duration,
		}
	}
	return wf
}

func workflowWithWorkflowEventStateTimeout(duration *string) *operatorapi.SonataFlow {
	wf := generateWorkflow()
	if duration != nil {
		wf.Spec.Flow.Timeouts = &cncfmodel.Timeouts{
			EventTimeout: *duration,
		}
	}
	return wf
}

func workflowWithEventStateWithTimeout(duration *string) *operatorapi.SonataFlow {
	wf := generateWorkflow()
	state := generateEventState()
	if duration != nil {
		state.EventState.Timeouts = &cncfmodel.EventStateTimeout{EventTimeout: *duration}
	}
	wf.Spec.Flow.States = []cncfmodel.State{*state}
	return wf
}

func workflowWithEventStateWithActionSleep(before bool, after bool) *operatorapi.SonataFlow {
	wf := generateWorkflow()
	state := generateEventState()
	wf.Spec.Flow.States = []cncfmodel.State{*state}
	state.EventState.OnEvents = []cncfmodel.OnEvents{
		{
			Actions: generateActionsWithSleep(before, after),
		},
	}
	return wf
}

func workflowWithOperationStateWithActionSleep(before bool, after bool) *operatorapi.SonataFlow {
	wf := generateWorkflow()
	state := generateOperationState()
	wf.Spec.Flow.States = []cncfmodel.State{*state}
	state.OperationState.Actions = generateActionsWithSleep(before, after)
	return wf
}

func workflowWithSwitchStateWithTimeout(duration *string) *operatorapi.SonataFlow {
	wf := generateWorkflow()
	state := generateSwitchState()
	wf.Spec.Flow.States = []cncfmodel.State{*state}
	if duration != nil {
		state.SwitchState.Timeouts = &cncfmodel.SwitchStateTimeout{
			EventTimeout: *duration,
		}
	}
	return wf
}

func workflowWithSleepState() *operatorapi.SonataFlow {
	wf := generateWorkflow()
	wf.Spec.Flow.States = []cncfmodel.State{*generateSleepState()}
	return wf
}

func workflowWithParallelState(branchWithBeforeSleep bool, branchWithAfterSleep bool) *operatorapi.SonataFlow {
	wf := generateWorkflow()
	state := generateParallelState()
	wf.Spec.Flow.States = []cncfmodel.State{*state}
	if branchWithBeforeSleep {
		branch := cncfmodel.Branch{
			Actions: []cncfmodel.Action{{Sleep: &cncfmodel.Sleep{Before: "PT5S"}}},
		}
		state.ParallelState.Branches = append(state.ParallelState.Branches, branch)
	}
	if branchWithAfterSleep {
		branch := cncfmodel.Branch{
			Actions: []cncfmodel.Action{{Sleep: &cncfmodel.Sleep{After: "PT5S"}}},
		}
		state.ParallelState.Branches = append(state.ParallelState.Branches, branch)
	}
	return wf
}

func workflowWithForEachStateWithActionSleep(before bool, after bool) *operatorapi.SonataFlow {
	wf := generateWorkflow()
	state := generateForEachState()
	wf.Spec.Flow.States = []cncfmodel.State{*state}
	state.ForEachState.Actions = generateActionsWithSleep(before, after)
	return wf
}

func workflowWithCallbackStateTimeoutAndActionSleep(duration *string, before *string, after *string) *operatorapi.SonataFlow {
	wf := generateWorkflow()
	state := generateCallbackState()
	wf.Spec.Flow.States = []cncfmodel.State{*state}
	if duration != nil {
		state.CallbackState.Timeouts = &cncfmodel.CallbackStateTimeout{EventTimeout: *duration}
	}
	state.CallbackState.Action = cncfmodel.Action{}
	if before != nil || after != nil {
		state.CallbackState.Action.Sleep = &cncfmodel.Sleep{}
		if before != nil {
			state.CallbackState.Action.Sleep.Before = *before
		}
		if after != nil {
			state.CallbackState.Action.Sleep.After = *after
		}
	}
	return wf
}

func generateWorkflow() *operatorapi.SonataFlow {
	wf := &operatorapi.SonataFlow{
		Spec: operatorapi.SonataFlowSpec{
			Flow: operatorapi.Flow{},
		},
	}
	return wf
}

func generateEventState() *cncfmodel.State {
	return &cncfmodel.State{
		BaseState: cncfmodel.BaseState{
			Type: cncfmodel.StateTypeEvent,
		},
		EventState: &cncfmodel.EventState{},
	}
}

func generateOperationState() *cncfmodel.State {
	return &cncfmodel.State{
		BaseState: cncfmodel.BaseState{
			Type: cncfmodel.StateTypeOperation,
		},
		OperationState: &cncfmodel.OperationState{},
	}
}

func generateSwitchState() *cncfmodel.State {
	return &cncfmodel.State{
		BaseState: cncfmodel.BaseState{
			Type: cncfmodel.StateTypeSwitch,
		},
		SwitchState: &cncfmodel.SwitchState{},
	}
}

func generateSleepState() *cncfmodel.State {
	return &cncfmodel.State{
		BaseState: cncfmodel.BaseState{
			Type: cncfmodel.StateTypeSleep,
		},
		SleepState: &cncfmodel.SleepState{},
	}
}

func generateParallelState() *cncfmodel.State {
	return &cncfmodel.State{
		BaseState: cncfmodel.BaseState{
			Type: cncfmodel.StateTypeParallel,
		},
		ParallelState: &cncfmodel.ParallelState{
			Branches: []cncfmodel.Branch{},
		},
	}
}

func generateForEachState() *cncfmodel.State {
	return &cncfmodel.State{
		BaseState: cncfmodel.BaseState{
			Type: cncfmodel.StateTypeForEach,
		},
		ForEachState: &cncfmodel.ForEachState{},
	}
}

func generateCallbackState() *cncfmodel.State {
	return &cncfmodel.State{
		BaseState: cncfmodel.BaseState{
			Type: cncfmodel.StateTypeCallback,
		},
		CallbackState: &cncfmodel.CallbackState{},
	}
}

func generateActionsWithSleep(before bool, after bool) []cncfmodel.Action {
	var actions []cncfmodel.Action
	if before {
		actions = append(actions, cncfmodel.Action{
			Sleep: &cncfmodel.Sleep{
				Before: "PT30S",
			},
		})
	}
	if after {
		actions = append(actions, cncfmodel.Action{
			Sleep: &cncfmodel.Sleep{
				After: "PT30S",
			},
		})
	}
	return actions
}
