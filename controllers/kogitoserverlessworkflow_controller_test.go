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

package controllers

import (
	"context"
	"testing"

	"github.com/stretchr/testify/assert"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/kiegroup/kogito-serverless-operator/api"

	"github.com/kiegroup/kogito-serverless-operator/test"

	"github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

func TestKogitoServerlessWorkflowController(t *testing.T) {
	t.Run("verify that a basic reconcile is performed without error", func(t *testing.T) {
		namespace := t.Name()
		// Create a KogitoServerlessWorkflow object with metadata and spec.
		ksw := test.GetKogitoServerlessWorkflow("../config/samples/"+test.KogitoServerlessWorkflowSampleYamlCR, namespace)
		// The Workflow controller needs at least to perform a call for Platforms, so we need to add this kind to the known
		// ones by the fake client
		ksp := test.GetKogitoServerlessPlatformInReadyPhase("../config/samples/"+test.KogitoServerlessPlatformWithCacheYamlCR, namespace)
		// Objects to track in the fake Client.
		objs := []runtime.Object{ksw, ksp}

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilder().WithRuntimeObjects(objs...).Build()
		// Create a KogitoServerlessWorkflowReconciler object with the scheme and fake client.
		r := &KogitoServerlessWorkflowReconciler{Client: cl, Scheme: cl.Scheme()}

		// Mock request to simulate Reconcile() being called on an event for a
		// watched resource .
		req := reconcile.Request{
			NamespacedName: types.NamespacedName{
				Name:      ksw.Name,
				Namespace: ksw.Namespace,
			},
		}
		_, err := r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}
		afterReconcileWorkflow := &v1alpha08.KogitoServerlessWorkflow{}
		if err := cl.Get(context.TODO(), req.NamespacedName, afterReconcileWorkflow); err != nil {
			t.Fatalf("Failed to fetch supposed to exist workflow %v", err)
		}
		// Perform some checks on the created CR
		assert.True(t, afterReconcileWorkflow.Spec.Start == "ChooseOnLanguage")
		// We create the initial build and return
		assert.True(t, afterReconcileWorkflow.Status.GetCondition(api.BuiltConditionType).IsFalse())
		assert.True(t, afterReconcileWorkflow.Status.GetCondition(api.RunningConditionType).IsFalse())
		assert.True(t, afterReconcileWorkflow.Status.IsWaitingForBuild())
		assert.True(t, len(afterReconcileWorkflow.Spec.States) == 4)
	})
}
