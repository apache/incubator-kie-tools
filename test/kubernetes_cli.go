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

package test

import (
	"context"
	"testing"

	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
	"k8s.io/client-go/kubernetes/scheme"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"

	"github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

// NewKogitoClientBuilder creates a new fake.ClientBuilder with the right scheme references
func NewKogitoClientBuilder() *fake.ClientBuilder {
	s := scheme.Scheme
	utilruntime.Must(v1alpha08.AddToScheme(s))
	return fake.NewClientBuilder().WithScheme(s)
}

func MustGetDeployment(t *testing.T, client ctrl.WithWatch, workflow *v1alpha08.KogitoServerlessWorkflow) *appsv1.Deployment {
	deployment := &appsv1.Deployment{}
	return mustGet(t, client, workflow, deployment).(*appsv1.Deployment)
}

func MustGetService(t *testing.T, client ctrl.WithWatch, workflow *v1alpha08.KogitoServerlessWorkflow) *v1.Service {
	svc := &v1.Service{}
	return mustGet(t, client, workflow, svc).(*v1.Service)
}

func MustGetConfigMap(t *testing.T, client ctrl.WithWatch, workflow *v1alpha08.KogitoServerlessWorkflow) *v1.ConfigMap {
	cm := &v1.ConfigMap{}
	return mustGet(t, client, workflow, cm).(*v1.ConfigMap)
}

func MustGetWorkflow(t *testing.T, client ctrl.WithWatch, name types.NamespacedName) *v1alpha08.KogitoServerlessWorkflow {
	workflow := &v1alpha08.KogitoServerlessWorkflow{}
	workflow.Name = name.Name
	workflow.Namespace = name.Namespace
	return mustGet(t, client, workflow, workflow).(*v1alpha08.KogitoServerlessWorkflow)
}

func mustGet(t *testing.T, client ctrl.WithWatch, workflow *v1alpha08.KogitoServerlessWorkflow, obj ctrl.Object) ctrl.Object {
	err := client.Get(context.TODO(), ctrl.ObjectKeyFromObject(workflow), obj)
	assert.NoError(t, err)
	return obj
}
