// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package v1alpha08

import (
	"fmt"

	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
)

// TODO: this types MUST came from the SDK, there's no point of copying and pasting the structures here.

func (e StateType) String() string {
	switch e {
	case EventStateType:
		return "event"
	case OperationStateType:
		return "operation"
	case SwitchStateType:
		return "switch"
	case SleepStateType:
		return "sleep"
	case ParallelStateType:
		return "parallel"
	case InjectStateType:
		return "inject"
	case ForEachStateType:
		return "foreach"
	default:
		return fmt.Sprintf("%s", string(e))
	}
}

type Constant struct {
	Name  string `json:"name"`
	Value string `json:"value"`
}

type Timeout struct {
	WorkflowExecTimeout string `json:"workflowExecTimeout,omitempty"`
	StateExecTimeout    string `json:"stateExecTimeout,omitempty"`
	ActionExecTimeout   string `json:"actionExecTimeout,omitempty"`
	BranchExecTimeout   string `json:"branchExecTimeout,omitempty"`
	EventTimeout        string `json:"eventTimeout,omitempty"`
}

type Error struct {
	Name        string `json:"name"`
	Code        string `json:"code"`
	Description string `json:"description,omitempty"`
}

type BasicAuthProperties struct {
	Username string     `json:"username"`
	Password string     `json:"password"`
	Metadata []Metadata `json:"metadata,omitempty"`
}

type BearerAuthProperties struct {
	Token    string     `json:"token"`
	Metadata []Metadata `json:"metadata,omitempty"`
}

type GrantType string

const (
	PasswordGrantType          GrantType = "password"
	ClientCredentialsGrantType GrantType = "clientCredentials"
	TokenExchangeGrantType     GrantType = "tokenExchange"
)

type OAuth2Properties struct {
	// +optional
	Authority *string   `json:"basic,omitempty"`
	GrantType GrantType `json:"grantType"`
	ClientId  string    `json:"clientId"`
	// +optional
	ClientSecret *string `json:"clientSecret"`
	// +optional
	Scopes *[]string `json:"scopes,omitempty"`
	// +optional
	Username *string `json:"username,omitempty"`
	// +optional
	Password *string `json:"password,omitempty"`
	// +optional
	Audiences *[]string `json:"audiences,omitempty"`
	// +optional
	SubjectToken *string `json:"subjectToken,omitempty"`
	// +optional
	RequestedSubject *string `json:"requestedSubject,omitempty"`
	// +optional
	RequestedIssuer *string `json:"requestedIssuer,omitempty"`
	// +optional
	Metadata *[]Metadata `json:"metadata,omitempty"`
}

type AuthProperties struct {
	Basic  BasicAuthProperties  `json:"basic,omitempty"`
	Bearer BearerAuthProperties `json:"bearer,omitempty"`
	Oauth2 OAuth2Properties     `json:"oauth2,omitempty"`
}

type AuthScheme string

const (
	BasicAuthScheme  AuthScheme = "basic"
	BearerAuthScheme AuthScheme = "bearer"
	Oauth2AuthScheme AuthScheme = "oauth2"
)

type Auth struct {
	Name       string         `json:"name"`
	Scheme     AuthScheme     `json:"scheme"`
	Properties AuthProperties `json:"properties"`
}

type EventKind string

const (
	ProducedEventKind EventKind = "produced"
	ConsumedEventKind EventKind = "consumed"
)

type EventCorrelationRule struct {
	ContextAttributeName  string `json:"contextAttributeName"`
	ContextAttributeValue string `json:"contextAttributeValue,omitempty"`
}

type Metadata struct {
	Key   string `json:"key"`
	Value string `json:"value"`
}

type Event struct {
	Name string `json:"name"`
	// +optional
	Source *string `json:"source"`
	Type   string  `json:"type"`
	// +optional
	Kind *EventKind `json:"kind,omitempty"`
	// +optional
	Correlation *[]EventCorrelationRule `json:"correlation,omitempty"`
	// +optional
	DataOnly *bool `json:"dataOnly,omitempty"`
	// +optional
	Metadata *[]Metadata `json:"metadata,omitempty"`
}

type FunctionType string

const (
	RestFunctionType       FunctionType = "rest"
	AsyncApiFunctionType   FunctionType = "asyncapi"
	RpcFunctionType        FunctionType = "rpc"
	GraphQLFunctionType    FunctionType = "graphql"
	ODataFunctionType      FunctionType = "odata"
	ExpressionFunctionType FunctionType = "expression"
	CustomFunctionType     FunctionType = "custom"
)

type Function struct {
	Name      string `json:"name"`
	Operation string `json:"operation"`
	// +optional
	Type FunctionType `json:"type,omitempty"`
	// +optional
	AuthRef *string `json:"authRef,omitempty"`
	// +optional
	Metadata *[]Metadata `json:"metadata,omitempty"`
}

type Retry struct {
	Name string `json:"name"`
	// +optional
	Delay *string `json:"delay,omitempty"`
	// +optional
	MaxAttempts *int `json:"maxAttempts,omitempty"`
	// +optional
	MaxDelay *string `json:"maxDelay,omitempty"`
	// +optional
	Increment *string `json:"increment,omitempty"`
	// +optional
	Multiplier *string `json:"multiplier,omitempty"`
	// +optional
	Jitter *string `json:"jitter,omitempty"`
}

type StateType string

const (
	EventStateType     StateType = "event"
	OperationStateType StateType = "operation"
	SwitchStateType    StateType = "switch"
	SleepStateType     StateType = "sleep"
	ParallelStateType  StateType = "parallel"
	InjectStateType    StateType = "inject"
	ForEachStateType   StateType = "foreach"
	CallbackStateType  StateType = "callback"
)

type ActionMode string

const (
	SequentialActionMode ActionMode = "sequential"
	ParallelActionMode   ActionMode = "parallel"
)

type InvokeType string

const (
	SyncInvokeType  InvokeType = "sync"
	AsyncInvokeType InvokeType = "async"
)

type EventRef struct {
	// +kubebuilder:validation:Required
	ProduceEventRef     string  `json:"produceEventRef"`
	ConsumeEventRef     *string `json:"consumeEventRef,omitempty"`
	ConsumeEventTimeout *string `json:"consumeEventTimeout,omitempty"`
	Data                *string `json:"data,omitempty"`
	//TODO Define a custom type for ContextAttribute
	ContextAttributes *map[string]unstructured.Unstructured `json:"contextAttributes,omitempty"`
	Invoke            *InvokeType                           `json:"invoke,omitempty"`
}

type ActionDataFilter struct {
	FromStateData *string `json:"fromStateData,omitempty"`
	UseResults    *bool   `json:"useResults,omitempty"`
	Results       *string `json:"results,omitempty"`
	ToStateData   *string `json:"toStateData,omitempty"`
}

// Sleep ...
type Sleep struct {
	// Before Amount of time (ISO 8601 duration format) to sleep before function/subflow invocation. Does not apply if 'eventRef' is defined.
	Before *string `json:"before,omitempty"`
	// After Amount of time (ISO 8601 duration format) to sleep after function/subflow invocation. Does not apply if 'eventRef' is defined.
	After *string `json:"after,omitempty"`
}

type FunctionRef struct {
	RefName      string            `json:"refName"`
	Arguments    map[string]string `json:"arguments,omitempty"`
	SelectionSet *string           `json:"selectionSet,omitempty"`
	Invoke       *InvokeType       `json:"invoke,omitempty"`
}

type Action struct {
	Name               string            `json:"name,omitempty"`
	FunctionRef        FunctionRef       `json:"functionRef,omitempty"`
	EventRef           *EventRef         `json:"eventRef,omitempty"`
	SubFlowRef         *string           `json:"subFlowRef,omitempty"`
	RetryRef           *string           `json:"retryRef,omitempty"`
	NonRetryableErrors *[]string         `json:"nonRetryableErrors,omitempty"`
	RetryableErrors    *[]string         `json:"retryableErrors,omitempty"`
	ActionDataFilter   *ActionDataFilter `json:"actionDataFilter,omitempty"`
	Sleep              *Sleep            `json:"sleep,omitempty"`
	Condition          *bool             `json:"condition,omitempty"`
}

type CompletionType string

const (
	AllOfCompletionType   CompletionType = "allOf"
	AtLeastCompletionType CompletionType = "atLeast"
)

type IterationMode string

const (
	SequentialIterationMode IterationMode = "sequential"
	ParallelIterationMode   IterationMode = "parallel"
)

type EventDataFilter struct {
	UseData     bool   `json:"useData,omitempty"`
	Data        string `json:"data,omitempty"`
	ToStateData string `json:"toStateData,omitempty"`
}

type Timeouts struct{}

//TODO: Define Timeouts (State specific timeout settings)

type StateDataFilter struct {
	Input  string `json:"input,omitempty"`
	Output string `json:"output,omitempty"`
}

type ErrorRef struct {
	ErrorRef   string   `json:"errorRef"`
	ErrorRefs  []string `json:"errorRefs"`
	End        bool     `json:"end,omitempty"`
	Transition string   `json:"transition,omitempty"`
}

type ProduceEvents struct {
	EventRef string `json:"eventRef"`
	Data     string `json:"data,omitempty"`
	//TODO Define a custom type for ContextAttribute
	ContextAttributes *map[string]unstructured.Unstructured `json:"contextAttributes,omitempty"`
}

type Transition struct {
	ProduceEvents *[]ProduceEvents `json:"produceEvents,omitempty"`
	Compensate    *bool            `json:"compensate,omitempty"`
	// +kubebuilder:validation:Required
	NextState string `json:"nextState,omitempty"`
}

type DataCondition struct {
	Name       string     `json:"name,omitempty"`
	Condition  string     `json:"condition"`
	Transition string     `json:"transition,omitempty"`
	End        bool       `json:"end,omitempty"`
	Metadata   []Metadata `json:"metadata,omitempty"`
}

type EventCondition struct {
	Name            string          `json:"name,omitempty"`
	EventRef        string          `json:"eventRef"`
	Transition      string          `json:"transition,omitempty"`
	End             bool            `json:"end,omitempty"`
	EventDataFilter EventDataFilter `json:"eventDataFilter,omitempty"`
	Metadata        []Metadata      `json:"metadata,omitempty"`
}

type OnEvent struct {
	EventRefs       []EventRef      `json:"eventRefs"`
	ActionMode      ActionMode      `json:"actionMode,omitempty"`
	Actions         []Action        `json:"actions,omitempty"`
	EventDataFilter EventDataFilter `json:"eventDataFilter,omitempty"`
}

type Branch struct {
	Name     string   `json:"name"`
	Actions  []Action `json:"actions"`
	Timeouts Timeout  `json:"timeouts,omitempty"`
}

type State struct {
	// +kubebuilder:validation:Required
	Name string `json:"name"`
	// +kubebuilder:validation:Enum:=event;operation;switch;sleep;parallel;inject;foreach
	Type            StateType          `json:"type"`
	Exclusive       *bool              `json:"exclusive,omitempty"`
	ActionMode      *ActionMode        `json:"actionMode,omitempty"`
	Actions         *[]Action          `json:"actions,omitempty"`
	Data            *map[string]string `json:"data,omitempty"`
	DataConditions  *[]DataCondition   `json:"dataConditions,omitempty"`
	EventConditions *[]EventCondition  `json:"eventConditions,omitempty"`
	//TODO: Define a type for DefaultCondition object
	DefaultCondition    *string          `json:"defaultCondition,omitempty"`
	OnEvents            *[]OnEvent       `json:"onEvents,omitempty"`
	Duration            *string          `json:"duration,omitempty"`
	Branches            *[]Branch        `json:"branches,omitempty"`
	CompletionType      *CompletionType  `json:"completionType,omitempty"`
	NumCompleted        *int             `json:"numCompleted,omitempty"`
	InputCollection     *string          `json:"inputCollection,omitempty"`
	OutputCollection    *string          `json:"outputCollection,omitempty"`
	IterationParam      *string          `json:"iterationParam,omitempty"`
	BatchSize           *int             `json:"batchSize,omitempty"`
	Mode                *IterationMode   `json:"mode,omitempty"`
	EventRef            *EventRef        `json:"eventRef,omitempty"`
	EventDataFilter     *EventDataFilter `json:"eventDataFilter,omitempty"`
	Timeouts            *Timeout         `json:"timeouts,omitempty"`
	StateDataFilter     *StateDataFilter `json:"stateDataFilter,omitempty"`
	Transition          *string          `json:"transition,omitempty"`
	OnErrors            *[]ErrorRef      `json:"onErrors,omitempty"`
	End                 bool             `json:"end,omitempty"`
	CompensatedBy       *string          `json:"compensatedBy,omitempty"`
	UsedForCompensation *bool            `json:"usedForCompensation,omitempty"`
	Metadata            *[]Metadata      `json:"metadata,omitempty"`
}
