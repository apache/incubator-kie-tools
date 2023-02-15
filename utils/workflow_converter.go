// Copyright 2022 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package utils

import (
	"context"
	"errors"
	"path"
	"strings"

	"github.com/go-logr/logr"
	"github.com/serverlessworkflow/sdk-go/v2/model"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

var logger logr.Logger

// ToCNCFWorkflow converts a KogitoServerlessWorkflow object to a model.Workflow one in order to be able to convert it to a YAML/Json
func ToCNCFWorkflow(ctx context.Context, serverlessWorkflow *operatorapi.KogitoServerlessWorkflow) (*model.Workflow, error) {
	if serverlessWorkflow != nil {
		logger = ctrllog.FromContext(ctx)
		newBaseWorkflow := &model.BaseWorkflow{ID: serverlessWorkflow.ObjectMeta.Name,
			Key:            serverlessWorkflow.ObjectMeta.Annotations[metadata.Key],
			Name:           serverlessWorkflow.ObjectMeta.Name,
			Description:    serverlessWorkflow.ObjectMeta.Annotations[metadata.Description],
			Version:        serverlessWorkflow.ObjectMeta.Annotations[metadata.Version],
			SpecVersion:    extractSchemaVersion(serverlessWorkflow.APIVersion),
			ExpressionLang: extractExpressionLang(serverlessWorkflow.ObjectMeta.Annotations),
			KeepActive:     serverlessWorkflow.Spec.KeepActive,
			AutoRetries:    serverlessWorkflow.Spec.AutoRetries,
			Start:          retrieveStartState(serverlessWorkflow.Spec.Start)}
		logger.V(DebugV).Info("Created new Base Workflow with name", "name", newBaseWorkflow.Name)
		newWorkflow := &model.Workflow{BaseWorkflow: *newBaseWorkflow, Functions: retrieveFunctions(serverlessWorkflow.Spec.Functions), States: retrieveStates(serverlessWorkflow.Spec.States)}
		return newWorkflow, nil
	}
	return nil, errors.New("kogitoServerlessWorkflow is nil")
}

func extractExpressionLang(annotations map[string]string) string {
	expressionLang := annotations[metadata.ExpressionLang]
	if expressionLang != "" {
		return expressionLang
	}
	return metadata.DefaultExpressionLang
}

// Function to extract from the apiVersion the ServerlessWorkflow schema version
// For example given sw.kogito.kie.org/operatorapi we would like to extract v0.8
func extractSchemaVersion(version string) string {
	schemaVersion := path.Base(version)
	strings.Replace(schemaVersion, "v0", "v0.", 1)
	return schemaVersion
}

// Function to retrieve a Start object given the name of the start state
func retrieveStartState(name string) *model.Start {
	start := &model.Start{StateName: name, Schedule: nil}
	return start
}

// Function to retrieve a list of states coming from an array of v08.State objects
func retrieveStates(incomingStates []operatorapi.State) []model.State {
	states := make([]model.State, len(incomingStates))
	logger.V(DebugV).Info("States: ", "states", incomingStates)
	for i, s := range incomingStates {
		stateT := model.StateType(s.Type.String())
		newBaseState := &model.BaseState{Name: s.Name, Type: stateT}
		if s.End {
			newBaseState.End = &model.End{Terminate: true}
		}
		if s.Transition != nil {
			newBaseState.Transition = &model.Transition{
				NextState: *s.Transition,
			}
		}
		switch sType := s.Type; sType {
		case "switch":
			newBaseSwitchState := &model.BaseSwitchState{
				BaseState:        *newBaseState,
				DefaultCondition: model.DefaultCondition{},
			}
			if s.DataConditions != nil {
				newSwitchState := &model.DataBasedSwitchState{BaseSwitchState: *newBaseSwitchState}
				dataConditions := make([]model.DataCondition, len(*s.DataConditions))
				for k, dc := range *s.DataConditions {
					newBaseCondition := &model.BaseDataCondition{Condition: dc.Condition}
					newTrasition := &model.Transition{NextState: dc.Transition}
					dataConditions[k] = &model.TransitionDataCondition{
						BaseDataCondition: *newBaseCondition,
						Transition:        *newTrasition,
					}
				}
				if s.DefaultCondition != nil {
					// Since at the moment we are not able yet to manage default condition that can be end or transition
					// let's use Transition
					newSwitchState.DefaultCondition = model.DefaultCondition{Transition: &model.Transition{
						NextState: *s.DefaultCondition,
					}}
				}
				newSwitchState.DataConditions = dataConditions
				states[i] = newSwitchState
			}

		case "inject":
			data := getData(*s.Data)
			states[i] = &model.InjectState{BaseState: *newBaseState, Data: data}
		case "operation":
			var actions []model.Action
			if s.Actions != nil {
				actions = make([]model.Action, len(*s.Actions))
				for k, ac := range *s.Actions {
					action := &model.Action{
						Name:        ac.Name,
						FunctionRef: &model.FunctionRef{RefName: ac.FunctionRef.RefName},
					}
					if &ac.FunctionRef != nil {
						action.FunctionRef = &model.FunctionRef{RefName: ac.FunctionRef.RefName}
						if ac.FunctionRef.Arguments != nil {
							action.FunctionRef.Arguments = getArguments(ac.FunctionRef.Arguments)
						}
					}
					actions[k] = *action
				}
			}
			states[i] = &model.OperationState{BaseState: *newBaseState, Actions: actions}
		default:
			logger.Info("Unable to create a CNCF State from incoming state type ", "type", sType)
		}
	}
	return states
}

func getData(data map[string]string) map[string]interface{} {
	out := make(map[string]interface{}, len(data))
	for k, v := range data {
		out[k] = v
	}
	return out
}

func getArguments(arguments map[string]string) map[string]interface{} {
	out := make(map[string]interface{}, len(arguments))
	for k, v := range arguments {
		out[k] = v
	}
	return out
}

// Function to retrieve a list of model.Function coming from an array of v08.Function objects
func retrieveFunctions(incomingFunctions []operatorapi.Function) []model.Function {
	functions := make([]model.Function, len(incomingFunctions))
	for i, f := range incomingFunctions {
		switch ftype := f.Type; ftype {
		default:
			function := &model.Function{Name: f.Name, Type: model.FunctionType(f.Type), Operation: f.Operation}
			functions[i] = *function
		}
	}
	return functions
}
