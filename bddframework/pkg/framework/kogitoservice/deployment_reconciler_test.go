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

package kogitoservice

import (
	api "github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/apps/v1"
	v13 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestDeploymentReconciler(t *testing.T) {
	ns := t.Name()
	instance := test.CreateFakeKogitoRuntime(ns)
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	serviceDefinition := ServiceDefinition{}
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	image := &api.Image{
		Name: "test-image",
		Tag:  "1.0",
	}
	imageHandler := infrastructure.NewImageHandler(context, image, "default-image", "image-stream", ns, false, false)
	deploymentReconciler := newDeploymentReconciler(context, instance, serviceDefinition, imageHandler)
	err := deploymentReconciler.Reconcile()
	assert.NoError(t, err)

	deployment := &v1.Deployment{ObjectMeta: v13.ObjectMeta{Name: instance.Name, Namespace: instance.Namespace}}
	exists, err := kubernetes.ResourceC(cli).Fetch(deployment)
	assert.NoError(t, err)
	assert.True(t, exists)
}
