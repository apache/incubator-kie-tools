/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import (
	"context"
	"github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/test/utils"
	"github.com/stretchr/testify/assert"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/kubernetes/scheme"
	"k8s.io/client-go/tools/record"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
	"testing"
)

func TestKogitoServerlessPlatformController(t *testing.T) {
	t.Run("verify that a basic reconcile is performed without error", func(t *testing.T) {
		var (
			name      = "kogito-serverless-operator"
			namespace = "kogito-serverless-operator-system"
		)
		// Create a KogitoServerlessPlatform object with metadata and spec.
		ksp, errYaml := utils.GetKogitoServerlessPlatform("../config/samples/sw.kogito_v1alpha08_kogitoserverlessplatform.yaml")
		if errYaml != nil {
			t.Fatalf("Error reading YAML file #%v ", errYaml)
		}
		kspl := &v1alpha08.KogitoServerlessPlatformList{}
		// Objects to track in the fake Client.
		objs := []runtime.Object{ksp, kspl}

		// Register operator types with the runtime scheme.
		s := scheme.Scheme
		s.AddKnownTypes(v1alpha08.GroupVersion, ksp)
		s.AddKnownTypes(v1alpha08.GroupVersion, kspl)
		// Create a fake client to mock API calls.
		cl := fake.NewFakeClient(objs...)

		// Create a KogitoServerlessPlatformReconciler object with the scheme and fake client.
		r := &KogitoServerlessPlatformReconciler{cl, cl, s, cfg, &record.FakeRecorder{}}

		// Mock request to simulate Reconcile() being called on an event for a
		// watched resource .
		req := reconcile.Request{
			NamespacedName: types.NamespacedName{
				Name:      name,
				Namespace: namespace,
			},
		}
		_, err := r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}
		// Perform some checks on the created CR
		assert.True(t, ksp.Spec.BuildPlatform.Registry.Address == "quay.io/kiegroup")
		assert.True(t, ksp.Spec.BuildPlatform.Registry.Secret == "regcred")
		assert.True(t, ksp.Status.Phase == "")
	})
}
