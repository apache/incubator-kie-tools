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
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal/app"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestReconcileKogitoSupportingServiceExplainability_Reconcile(t *testing.T) {
	ns := t.Name()
	kafka := test.CreateFakeKafka(t.Name())
	kogitoKafka := test.CreateFakeKogitoKafka(ns)
	kogitoKafka.GetSpec().GetResource().SetName(kafka.Name)
	explainabilityService := test.CreateFakeExplainabilityService(ns)
	explainabilityService.GetSpec().AddInfra(kogitoKafka.GetName())

	cli := test.NewFakeClientBuilder().AddK8sObjects(kafka, explainabilityService, kogitoKafka).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	r := &explainabilitySupportingServiceResource{
		supportingServiceContext: supportingServiceContext{
			Context:                  context,
			instance:                 explainabilityService,
			supportingServiceHandler: app.NewKogitoSupportingServiceHandler(context),
			infraHandler:             app.NewKogitoInfraHandler(context),
			runtimeHandler:           app.NewKogitoRuntimeHandler(context),
		},
	}

	// basic checks
	err := r.Reconcile()
	assert.NoError(t, err)
}
