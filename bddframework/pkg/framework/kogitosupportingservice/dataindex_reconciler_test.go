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
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal/app"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/apps/v1"
	v13 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestKogitoSupportingServiceDataIndex_Reconcile(t *testing.T) {
	ns := t.Name()
	kafka := test.CreateFakeKafka(t.Name())
	kogitoKafka := test.CreateFakeKogitoKafka(t.Name())
	kogitoKafka.GetSpec().GetResource().SetName(kafka.Name)
	kogitoInfinispan := test.CreateFakeKogitoInfinispan(t.Name())
	dataIndex := test.CreateFakeDataIndex(ns)
	dataIndex.GetSpec().AddInfra(kogitoKafka.GetName())
	dataIndex.GetSpec().AddInfra(kogitoInfinispan.GetName())
	cli := test.NewFakeClientBuilder().AddK8sObjects(dataIndex, kogitoKafka, kogitoInfinispan, kafka).Build()
	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: "1.0-SNAPSHOT",
	}
	r := &dataIndexSupportingServiceResource{
		supportingServiceContext: supportingServiceContext{
			Context:                  context,
			instance:                 dataIndex,
			supportingServiceHandler: app.NewKogitoSupportingServiceHandler(context),
			infraHandler:             app.NewKogitoInfraHandler(context),
			runtimeHandler:           app.NewKogitoRuntimeHandler(context),
		},
	}
	err := r.Reconcile()
	assert.NoError(t, err)

	dataIndexDeployment := &v1.Deployment{ObjectMeta: v13.ObjectMeta{Name: dataIndex.Name, Namespace: dataIndex.Namespace}}
	exists, err := kubernetes.ResourceC(cli).Fetch(dataIndexDeployment)
	assert.NoError(t, err)
	assert.True(t, exists)
	assert.Equal(t, "1.0-SNAPSHOT", dataIndexDeployment.Annotations[framework.KogitoOperatorVersionAnnotation])
	assert.Equal(t, "1.0-SNAPSHOT", dataIndexDeployment.Spec.Template.Annotations[framework.KogitoOperatorVersionAnnotation])
}
