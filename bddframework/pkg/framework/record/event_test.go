// Copyright 2020 Red Hat, Inc. and/or its affiliates
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

package record

import (
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	"testing"
)

func Test_generateEvent(t *testing.T) {
	service := test.CreateFakeKogitoRuntime(t.Name())
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	recorder := NewRecorder(meta.GetRegisteredSchema(), corev1.EventSource{Component: service.GetName()})
	recorder.Eventf(cli, service, "Normal", "Created", "Create Deployment")

	eventList := &corev1.EventList{}
	if err := kubernetes.ResourceC(cli).ListWithNamespace(t.Name(), eventList); err != nil {
		assert.Fail(t, "Not able to load kubernetes resources", err)
	}
	assert.NotNil(t, eventList.Items)
	assert.Equal(t, 1, len(eventList.Items))
}

func Test_generateEvent_InvalidEventType(t *testing.T) {
	service := test.CreateFakeKogitoRuntime(t.Name())
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	recorder := NewRecorder(meta.GetRegisteredSchema(), corev1.EventSource{Component: service.GetName()})
	recorder.Eventf(cli, service, "InvalidEventType", "Created", "Create Deployment")

	eventList := &corev1.EventList{}
	if err := kubernetes.ResourceC(cli).ListWithNamespace(t.Name(), eventList); err != nil {
		assert.Fail(t, "Not able to load kubernetes resources", err)
	}
	assert.Nil(t, eventList.Items)
}
