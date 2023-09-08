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

package test

import (
	"context"
	"github.com/stretchr/testify/assert"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
	"testing"
)

// AssertReconcile asserts if the reconcile.Reconciler call finished without errors
func AssertReconcile(t *testing.T, r reconcile.Reconciler, instance client.Object) (result reconcile.Result) {
	result, err := r.Reconcile(context.TODO(), reconcile.Request{NamespacedName: types.NamespacedName{Name: instance.GetName(), Namespace: instance.GetNamespace()}})
	assert.NoError(t, err)
	assert.NotNil(t, result)
	return
}

// AssertReconcileMustRequeue asserts the reconciliation result and that the result adds the object in the reconciliation queue again
func AssertReconcileMustRequeue(t *testing.T, r reconcile.Reconciler, instance client.Object) (result reconcile.Result) {
	result = AssertReconcile(t, r, instance)
	assert.True(t, result.Requeue)
	return
}

// AssertReconcileMustNotRequeue asserts the reconciliation result and that the result DOES NOT add the object in the reconciliation queue again
func AssertReconcileMustNotRequeue(t *testing.T, r reconcile.Reconciler, instance client.Object) (result reconcile.Result) {
	result = AssertReconcile(t, r, instance)
	assert.False(t, result.Requeue)
	return
}
