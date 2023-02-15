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

/*
	<Type> Builder

the New<Type>Builder accept the mandatory args of Type,
the fluent API are for the other optional args before to invoke the Build()

i.e.

	    builder := New<Type>Builder("MyFisrtManadatoryArg", "MySecondMandatoryArg")
		builder.With<OptionalArg>("mySource")
		myType := builder.Build()
*/
type EventBuilder struct {
	Event *Event
}

func NewEventBuilder(name string, eventType string) EventBuilder {
	return EventBuilder{Event: &Event{Name: name, Type: eventType}}
}

func (b *EventBuilder) WithSource(source string) *EventBuilder {
	b.Event.Source = &source
	return b
}

func (b *EventBuilder) WithKind(kind EventKind) *EventBuilder {
	b.Event.Kind = &kind
	return b
}

func (b *EventBuilder) WithCorrelation(correlation []EventCorrelationRule) *EventBuilder {
	b.Event.Correlation = &correlation
	return b
}

func (b *EventBuilder) WithDataOnly(dataOnly bool) *EventBuilder {
	b.Event.DataOnly = &dataOnly
	return b
}

func (b *EventBuilder) WithMetadata(metadata []Metadata) *EventBuilder {
	b.Event.Metadata = &metadata
	return b
}

func (b *EventBuilder) Build() Event {
	return *b.Event
}

// END EVENT

// OAuth2PropertiesBuilder ...
type OAuth2PropertiesBuilder struct {
	OAuth2Properties *OAuth2Properties
}

func NewOAuth2PropertiesBuilder(grantType GrantType, clientId string) OAuth2PropertiesBuilder {
	return OAuth2PropertiesBuilder{
		OAuth2Properties: &OAuth2Properties{GrantType: grantType,
			ClientId: clientId}}
}

func (b *OAuth2PropertiesBuilder) WithAuthority(authority string) *OAuth2PropertiesBuilder {
	b.OAuth2Properties.Authority = &authority
	return b
}

func (b *OAuth2PropertiesBuilder) WithClientSecret(clientSecret string) *OAuth2PropertiesBuilder {
	b.OAuth2Properties.ClientSecret = &clientSecret
	return b
}

func (b *OAuth2PropertiesBuilder) WithScopes(scopes []string) *OAuth2PropertiesBuilder {
	b.OAuth2Properties.Scopes = &scopes
	return b
}

func (b *OAuth2PropertiesBuilder) WithUsername(username string) *OAuth2PropertiesBuilder {
	b.OAuth2Properties.Username = &username
	return b
}

func (b *OAuth2PropertiesBuilder) WithPassword(password string) *OAuth2PropertiesBuilder {
	b.OAuth2Properties.Password = &password
	return b
}

func (b *OAuth2PropertiesBuilder) WithAudiences(audiences []string) *OAuth2PropertiesBuilder {
	b.OAuth2Properties.Audiences = &audiences
	return b
}

func (b *OAuth2PropertiesBuilder) WithSubjectToken(subjectToken string) *OAuth2PropertiesBuilder {
	b.OAuth2Properties.SubjectToken = &subjectToken
	return b
}

func (b *OAuth2PropertiesBuilder) WithRequestedSubject(requestedSubject string) *OAuth2PropertiesBuilder {
	b.OAuth2Properties.RequestedSubject = &requestedSubject
	return b
}

func (b *OAuth2PropertiesBuilder) WithRequestedIssuer(requestedIssuer string) *OAuth2PropertiesBuilder {
	b.OAuth2Properties.RequestedIssuer = &requestedIssuer
	return b
}

func (b *OAuth2PropertiesBuilder) WithMetadata(metadata []Metadata) *OAuth2PropertiesBuilder {
	b.OAuth2Properties.Metadata = &metadata
	return b
}

func (b *OAuth2PropertiesBuilder) Build() OAuth2Properties {
	return *b.OAuth2Properties
}

// END OAuth2Properties
// function

type FunctionBuilder struct {
	Function *Function
}

func NewFunctionBuilderBuilder(name string, operation string) FunctionBuilder {
	return FunctionBuilder{
		Function: &Function{Name: name, Operation: operation}}
}

func (b *FunctionBuilder) WithFunctionType(functionType FunctionType) *FunctionBuilder {
	b.Function.Type = functionType
	return b
}

func (b *FunctionBuilder) WithAuthRef(authRef string) *FunctionBuilder {
	b.Function.AuthRef = &authRef
	return b
}

func (b *FunctionBuilder) WithMetadata(metadata []Metadata) *FunctionBuilder {
	b.Function.Metadata = &metadata
	return b
}

func (b *FunctionBuilder) Build() Function {
	return *b.Function
}

// END Function
// Retry
type RetryBuilder struct {
	Retry *Retry
}

func NewRetryBuilder(name string) RetryBuilder {
	return RetryBuilder{
		Retry: &Retry{Name: name}}
}

func (b *RetryBuilder) WithDelay(delay string) *RetryBuilder {
	b.Retry.Delay = &delay
	return b
}

func (b *RetryBuilder) WithMaxAttempts(maxAttempts int) *RetryBuilder {
	b.Retry.MaxAttempts = &maxAttempts
	return b
}

func (b *RetryBuilder) WithMaxDelay(maxDelay string) *RetryBuilder {
	b.Retry.MaxDelay = &maxDelay
	return b
}

func (b *RetryBuilder) WithIncrement(increment string) *RetryBuilder {
	b.Retry.Increment = &increment
	return b
}

func (b *RetryBuilder) WithMultiplier(multiplier string) *RetryBuilder {
	b.Retry.Multiplier = &multiplier
	return b
}

func (b *RetryBuilder) WithJitter(jitter string) *RetryBuilder {
	b.Retry.Jitter = &jitter
	return b
}

func (b *RetryBuilder) Build() Retry {
	return *b.Retry
}

// END retry
// State
type StateBuilder struct {
	State *State
}

func NewStateBuilder(name string, typeState StateType) StateBuilder {
	return StateBuilder{State: &State{Name: name, Type: typeState}}
}

func (b *StateBuilder) WithEventRef(eventRef EventRef) *StateBuilder {
	b.State.EventRef = &eventRef
	return b
}

func (b *StateBuilder) WithInputCollection(inputCollection string) *StateBuilder {
	b.State.InputCollection = &inputCollection
	return b
}

func (b *StateBuilder) WithBranches(branches []Branch) *StateBuilder {
	b.State.Branches = &branches
	return b
}

func (b *StateBuilder) WithDuration(duration string) *StateBuilder {
	b.State.Duration = &duration
	return b
}

func (b *StateBuilder) WithOnEvents(onEvents []OnEvent) *StateBuilder {
	b.State.OnEvents = &onEvents
	return b
}

func (b *StateBuilder) WithDefaultCondition(defaultCondition string) *StateBuilder {
	b.State.DefaultCondition = &defaultCondition
	return b
}

func (b *StateBuilder) WithData(data map[string]string) *StateBuilder {
	b.State.Data = &data
	return b
}

func (b *StateBuilder) WithExclusive(exclusive bool) *StateBuilder {
	b.State.Exclusive = &exclusive
	return b
}

func (b *StateBuilder) WithActionMode(actionMode ActionMode) *StateBuilder {
	b.State.ActionMode = &actionMode
	return b
}

func (b *StateBuilder) WithActions(actions []Action) *StateBuilder {
	b.State.Actions = &actions
	return b
}

func (b *StateBuilder) WithCompletionType(completionType CompletionType) *StateBuilder {
	b.State.CompletionType = &completionType
	return b
}

func (b *StateBuilder) WithNumCompleted(numCompleted int) *StateBuilder {
	b.State.NumCompleted = &numCompleted
	return b
}

func (b *StateBuilder) WithOutputCollection(outputCollection string) *StateBuilder {
	b.State.OutputCollection = &outputCollection
	return b
}

func (b *StateBuilder) WithIterationParam(iterationParam string) *StateBuilder {
	b.State.IterationParam = &iterationParam
	return b
}

func (b *StateBuilder) WithBatchSize(batchSize int) *StateBuilder {
	b.State.BatchSize = &batchSize
	return b
}

func (b *StateBuilder) WithMode(mode IterationMode) *StateBuilder {
	b.State.Mode = &mode
	return b
}

func (b *StateBuilder) WithEventDataFilter(eventDataFilter EventDataFilter) *StateBuilder {
	b.State.EventDataFilter = &eventDataFilter
	return b
}

func (b *StateBuilder) WithTimeouts(timeouts Timeout) *StateBuilder {
	b.State.Timeouts = &timeouts
	return b
}

func (b *StateBuilder) WithStateDataFilter(stateDataFilter StateDataFilter) *StateBuilder {
	b.State.StateDataFilter = &stateDataFilter
	return b
}

func (b *StateBuilder) WithTransition(transition string) *StateBuilder {
	b.State.Transition = &transition
	return b
}

func (b *StateBuilder) WithOnErrors(onErrors []ErrorRef) *StateBuilder {
	b.State.OnErrors = &onErrors
	return b
}

func (b *StateBuilder) WithEnd(end bool) *StateBuilder {
	b.State.End = end
	return b
}

func (b *StateBuilder) WithCompensatedBy(compensatedBy string) *StateBuilder {
	b.State.CompensatedBy = &compensatedBy
	return b
}

func (b *StateBuilder) WithUsedForCompensation(usedForCompensation bool) *StateBuilder {
	b.State.UsedForCompensation = &usedForCompensation
	return b
}

func (b *StateBuilder) WithMetadata(metadata []Metadata) *StateBuilder {
	b.State.Metadata = &metadata
	return b
}

func (b *StateBuilder) Build() State {
	return *b.State
}

// END State
