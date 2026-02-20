/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package controller

import (
	"context"
	"testing"

	"k8s.io/client-go/rest"

	"github.com/stretchr/testify/assert"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
)

func TestSonataFlowControllerAddsFinalizerReplicasAndSelector(t *testing.T) {
	t.Run("verify that workflow finalizer, replicas and selector are early added are if missing", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlow object with metadata and spec.
		ksw := test.GetBaseSonataFlow(namespace)
		// The Workflow controller needs at least to perform a call for Platforms, so we need to add this kind to the known
		// ones by the fake client
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		// Objects to track in the fake Client.
		objs := []runtime.Object{ksw, ksp}

		// Create a fake client to mock API calls.
		cl := test.NewSonataFlowClientBuilder().WithRuntimeObjects(objs...).WithStatusSubresource(ksw, ksp).Build()
		// Create a SonataFlowReconciler object with the scheme and fake client.
		r := &SonataFlowReconciler{Client: cl, Scheme: cl.Scheme(), Config: &rest.Config{}, Recorder: test.NewFakeRecorder()}

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
		afterReconcileWorkflow := &v1alpha08.SonataFlow{}
		if err := cl.Get(context.TODO(), req.NamespacedName, afterReconcileWorkflow); err != nil {
			t.Fatalf("Failed to fetch supposed to exist workflow %v", err)
		}
		// finalizer and replicas must have been set.
		assert.NotNil(t, afterReconcileWorkflow.Spec.PodTemplate.Replicas)
		assert.Equal(t, int32(1), *afterReconcileWorkflow.Spec.PodTemplate.Replicas)
		assert.Equal(t, []string{"workflow-deletion"}, afterReconcileWorkflow.GetFinalizers())

		_, err = r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}
		if err := cl.Get(context.TODO(), req.NamespacedName, afterReconcileWorkflow); err != nil {
			t.Fatalf("Failed to fetch supposed to exist workflow %v", err)
		}
		// status scale selector must have been set.
		expectedSelector := "app=greeting,app.kubernetes.io/component=serverless-workflow,app.kubernetes.io/instance=greeting,app.kubernetes.io/managed-by=sonataflow-operator,app.kubernetes.io/name=greeting,app.kubernetes.io/version=main,sonataflow.org/workflow-app=greeting,sonataflow.org/workflow-namespace=TestSonataFlowControllerAddsFinalizerReplicasAndSelector/verify_that_workflow_finalizer,_replicas_and_selector_are_early_added_are_if_missing"
		assert.Equal(t, expectedSelector, afterReconcileWorkflow.Status.Selector)
	})
}

func TestSonataFlowController(t *testing.T) {
	t.Run("verify that a basic reconcile is performed without error", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlow object with metadata and spec.
		ksw := test.GetBaseSonataFlow(namespace)
		replicas := int32(2)
		ksw.Spec.PodTemplate.Replicas = &replicas
		ksw.SetFinalizers([]string{"workflow-deletion"})
		ksw.Status.Selector = "current-selector"
		// The Workflow controller needs at least to perform a call for Platforms, so we need to add this kind to the known
		// ones by the fake client
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		// Objects to track in the fake Client.
		objs := []runtime.Object{ksw, ksp}

		// Create a fake client to mock API calls.
		cl := test.NewSonataFlowClientBuilder().WithRuntimeObjects(objs...).WithStatusSubresource(ksw, ksp).Build()
		// Create a SonataFlowReconciler object with the scheme and fake client.
		r := &SonataFlowReconciler{Client: cl, Scheme: cl.Scheme(), Config: &rest.Config{}, Recorder: test.NewFakeRecorder()}

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
		afterReconcileWorkflow := &v1alpha08.SonataFlow{}
		if err := cl.Get(context.TODO(), req.NamespacedName, afterReconcileWorkflow); err != nil {
			t.Fatalf("Failed to fetch supposed to exist workflow %v", err)
		}
		// Perform some checks on the created CR
		// finalizers, replicas and selector remained untouched.
		assert.Equal(t, int32(2), *afterReconcileWorkflow.Spec.PodTemplate.Replicas)
		assert.Equal(t, []string{"workflow-deletion"}, afterReconcileWorkflow.GetFinalizers())
		assert.Equal(t, "current-selector", afterReconcileWorkflow.Status.Selector)

		assert.True(t, afterReconcileWorkflow.Spec.Flow.Start.StateName == "ChooseOnLanguage")
		// We create the initial build and return
		assert.True(t, afterReconcileWorkflow.Status.GetCondition(api.BuiltConditionType).IsFalse())
		assert.True(t, afterReconcileWorkflow.Status.GetCondition(api.RunningConditionType).IsFalse())
		assert.True(t, afterReconcileWorkflow.Status.IsWaitingForBuild())
		assert.True(t, len(afterReconcileWorkflow.Spec.Flow.States) == 4)

		assert.True(t, ksw.Spec.Flow.Start.StateName == "ChooseOnLanguage")
		assert.True(t, len(ksw.Spec.Flow.States) == 4)

		assert.Equal(t, ksp.Name, afterReconcileWorkflow.Status.Platform.Name)
		assert.Equal(t, ksp.Namespace, afterReconcileWorkflow.Status.Platform.Namespace)
	})
}
