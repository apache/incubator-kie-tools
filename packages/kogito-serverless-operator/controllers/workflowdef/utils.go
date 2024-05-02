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
	"github.com/serverlessworkflow/sdk-go/v2/model"
)

// HasTimeouts returns true if current workflow has configured any of the SonataFlow supported timeouts, false
// in any other case. This method might be reviewed when more timeouts are supported.
func HasTimeouts(workflow *operatorapi.SonataFlow) bool {
	flow := &workflow.Spec.Flow
	hasTimeouts := HasWorkflowExecTimeout(flow) || HasWorkflowEventTimeout(flow)
	for i := 0; !hasTimeouts && i < len(flow.States); i++ {
		state := flow.States[i]
		switch state.Type {
		case model.StateTypeEvent:
			hasTimeouts = HasEventStateTimeouts(state.EventState)
		case model.StateTypeOperation:
			hasTimeouts = HasOperationStateTimeouts(state.OperationState)
		case model.StateTypeSwitch:
			hasTimeouts = HasSwitchStateTimeouts(state.SwitchState)
		case model.StateTypeSleep:
			hasTimeouts = true
		case model.StateTypeParallel:
			hasTimeouts = HasParallelStateTimeouts(state.ParallelState)
		case model.StateTypeForEach:
			hasTimeouts = HasForEachStateTimeouts(state.ForEachState)
		case model.StateTypeCallback:
			hasTimeouts = HasCallbackStateTimeouts(state.CallbackState)
		}
	}
	return hasTimeouts
}

func HasWorkflowEventTimeout(flow *operatorapi.Flow) bool {
	return flow.Timeouts != nil && len(flow.Timeouts.EventTimeout) > 0
}
func HasWorkflowExecTimeout(flow *operatorapi.Flow) bool {
	return flow.Timeouts != nil && flow.Timeouts.WorkflowExecTimeout != nil && len(flow.Timeouts.WorkflowExecTimeout.Duration) > 0
}

func HasEventStateTimeouts(state *model.EventState) bool {
	if state.Timeouts != nil && len(state.Timeouts.EventTimeout) > 0 {
		return true
	}
	for _, onEvent := range state.OnEvents {
		if hasActionsWithSleep(&onEvent.Actions) {
			return true
		}
	}
	return false
}

func HasOperationStateTimeouts(state *model.OperationState) bool {
	return hasActionsWithSleep(&state.Actions)
}

func HasSwitchStateTimeouts(state *model.SwitchState) bool {
	return state.Timeouts != nil && len(state.Timeouts.EventTimeout) > 0
}

func HasParallelStateTimeouts(state *model.ParallelState) bool {
	for _, branch := range state.Branches {
		if hasBranchTimeouts(&branch) {
			return true
		}
	}
	return false
}

func hasBranchTimeouts(branch *model.Branch) bool {
	return hasActionsWithSleep(&branch.Actions)
}

func HasForEachStateTimeouts(state *model.ForEachState) bool {
	return hasActionsWithSleep(&state.Actions)
}

func HasCallbackStateTimeouts(state *model.CallbackState) bool {
	return (state.Timeouts != nil && len(state.Timeouts.EventTimeout) > 0) || hasAnySleep(&state.Action)
}

func hasActionsWithSleep(actions *[]model.Action) bool {
	for _, action := range *actions {
		if hasAnySleep(&action) {
			return true
		}
	}
	return false
}

func hasAnySleep(action *model.Action) bool {
	return action.Sleep != nil && (len(action.Sleep.Before) > 0 || len(action.Sleep.After) > 0)
}
