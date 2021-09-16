// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package kogitoservice

import (
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	v1 "github.com/openshift/api/route/v1"
	"github.com/stretchr/testify/assert"
	v13 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestRouteReconciler_K8s(t *testing.T) {
	ns := t.Name()
	instance := test.CreateFakeKogitoRuntime(ns)
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	routeReconciler := newRouteReconciler(context, instance)
	err := routeReconciler.Reconcile()
	assert.NoError(t, err)

	route := &v1.Route{ObjectMeta: v13.ObjectMeta{Name: instance.Name, Namespace: instance.Namespace}}
	exists, err := kubernetes.ResourceC(cli).Fetch(route)
	assert.NoError(t, err)
	assert.False(t, exists)
}

func TestRouteReconciler_Openshift(t *testing.T) {
	ns := t.Name()
	instance := test.CreateFakeKogitoRuntime(ns)
	cli := test.NewFakeClientBuilder().OnOpenShift().AddK8sObjects(instance).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	routeReconciler := newRouteReconciler(context, instance)
	err := routeReconciler.Reconcile()
	assert.NoError(t, err)

	route := &v1.Route{ObjectMeta: v13.ObjectMeta{Name: instance.Name, Namespace: instance.Namespace}}
	exists, err := kubernetes.ResourceC(cli).Fetch(route)
	assert.NoError(t, err)
	assert.True(t, exists)
}
