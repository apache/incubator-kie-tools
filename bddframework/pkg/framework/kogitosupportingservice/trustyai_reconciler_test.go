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

package kogitosupportingservice

import (
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"github.com/kiegroup/kogito-cloud-operator/core/test"
	"github.com/kiegroup/kogito-cloud-operator/internal"
	"github.com/kiegroup/kogito-cloud-operator/meta"
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestReconcileKogitoSupportingTrusty_Reconcile(t *testing.T) {
	ns := t.Name()
	kafka := test.CreateFakeKafka("my-kafka", t.Name())
	kogitoKafka := test.CreateFakeKogitoKafka(ns)
	kogitoKafka.GetSpec().GetResource().SetName(kafka.Name)
	instance := test.CreateFakeTrustyAIService(ns)
	instance.GetSpec().AddInfra(kogitoKafka.GetName())
	cli := test.NewFakeClientBuilder().AddK8sObjects(kafka, instance, kogitoKafka).OnOpenShift().Build()
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	r := &trustyAISupportingServiceResource{
		supportingServiceContext: supportingServiceContext{
			Context:                  context,
			instance:                 instance,
			supportingServiceHandler: internal.NewKogitoSupportingServiceHandler(context),
			infraHandler:             internal.NewKogitoInfraHandler(context),
			runtimeHandler:           internal.NewKogitoRuntimeHandler(context),
		},
	}
	// basic checks
	requeueAfter, err := r.Reconcile()
	assert.NoError(t, err)
	assert.True(t, requeueAfter == 0)
}
