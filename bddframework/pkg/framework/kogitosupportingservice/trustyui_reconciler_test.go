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
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal/app"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	v12 "k8s.io/api/apps/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestReconcileKogitoSupportingServiceTrustyUI_Reconcile(t *testing.T) {
	ns := t.Name()
	instance := test.CreateFakeTrustyUIService(ns)
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	r := &trustyUISupportingServiceResource{
		supportingServiceContext: supportingServiceContext{
			instance:                 instance,
			Context:                  context,
			infraHandler:             app.NewKogitoInfraHandler(context),
			supportingServiceHandler: app.NewKogitoSupportingServiceHandler(context),
		},
	}

	// first reconciliation
	err := r.Reconcile()
	assert.NoError(t, err)
	// second time
	err = r.Reconcile()
	assert.NoError(t, err)

	_, err = kubernetes.ResourceC(cli).Fetch(instance)
	assert.NoError(t, err)
	assert.NotNil(t, instance.GetStatus())
	assert.Len(t, *instance.GetStatus().GetConditions(), 2)
}

// see: https://issues.redhat.com/browse/KOGITO-2535
func TestReconcileKogitoTrustyUI_CustomImage(t *testing.T) {
	ns := t.Name()
	instance := test.CreateFakeTrustyUIService(ns)
	instance.GetSpec().SetImage("quay.io/mynamespace/awesome-trusty-ui:0.1.3")
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	r := &trustyUISupportingServiceResource{
		supportingServiceContext: supportingServiceContext{
			Context:                  context,
			instance:                 instance,
			infraHandler:             app.NewKogitoInfraHandler(context),
			supportingServiceHandler: app.NewKogitoSupportingServiceHandler(context),
		},
	}

	err := r.Reconcile()
	assert.NoError(t, err)

	// Check image name inside deployment
	deployment := v12.Deployment{
		ObjectMeta: v1.ObjectMeta{Name: instance.Name, Namespace: ns},
	}
	exists, err := kubernetes.ResourceC(cli).Fetch(&deployment)
	assert.True(t, exists)
	assert.NoError(t, err)
	assert.Equal(t, "quay.io/mynamespace/awesome-trusty-ui:0.1.3", deployment.Spec.Template.Spec.Containers[0].Image)
}
