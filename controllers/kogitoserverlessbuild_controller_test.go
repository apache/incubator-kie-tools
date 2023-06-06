// Copyright 2023 Red Hat, Inc. and/or its affiliates
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

package controllers

import (
	"context"
	"testing"

	"github.com/stretchr/testify/assert"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/record"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/api"
	"github.com/kiegroup/kogito-serverless-operator/test"
)

func TestKogitoServerlessBuildController(t *testing.T) {
	namespace := t.Name()
	ksw := test.GetBaseServerlessWorkflow(namespace)
	ksb := test.GetNewEmptyKogitoServerlessBuild(ksw.Name, namespace)

	cl := test.NewKogitoClientBuilder().
		WithRuntimeObjects(ksb, ksw).
		WithRuntimeObjects(test.GetBasePlatformInReadyPhase(namespace)).
		WithRuntimeObjects(test.GetKogitoServerlessOperatorBuilderConfig("../", namespace)).
		WithStatusSubresource(ksb, ksw).
		Build()

	r := &KogitoServerlessBuildReconciler{cl, cl.Scheme(), &record.FakeRecorder{}, &rest.Config{}}
	req := reconcile.Request{
		NamespacedName: types.NamespacedName{
			Name:      ksb.Name,
			Namespace: ksb.Namespace,
		},
	}

	result, err := r.Reconcile(context.TODO(), req)
	assert.NoError(t, err)
	assert.Equal(t, requeueAfterForNewBuild, result.RequeueAfter)

	// verify if the inner build has been persisted correctly
	assert.NoError(t, cl.Get(context.TODO(), req.NamespacedName, ksb))
	assert.Equal(t, operatorapi.BuildPhaseScheduling, ksb.Status.BuildPhase)
	assert.NotNil(t, ksb.Status.InnerBuild)

	containerBuild := &api.ContainerBuild{}
	assert.NoError(t, ksb.Status.GetInnerBuild(containerBuild))
	assert.Equal(t, string(ksb.Status.BuildPhase), string(containerBuild.Status.Phase))
}
