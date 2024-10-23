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

package test

import (
	"context"

	"testing"

	"github.com/apache/incubator-kie-kogito-serverless-operator/utils"
	prometheus "github.com/prometheus-operator/prometheus-operator/pkg/apis/monitoring/v1"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	sourcesv1 "knative.dev/eventing/pkg/apis/sources/v1"
	servingv1 "knative.dev/serving/pkg/apis/serving/v1"

	buildv1 "github.com/openshift/api/build/v1"
	imgv1 "github.com/openshift/api/image/v1"
	routev1 "github.com/openshift/api/route/v1"
	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
	"k8s.io/client-go/kubernetes/scheme"
	"k8s.io/client-go/tools/record"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
)

func NewFakeRecorder() record.EventRecorder {
	return record.NewFakeRecorder(10)
}

type SonataFlowClientBuilder struct {
	innerBuilder *fake.ClientBuilder
}

// Build from the underlying fake.ClientBuilder.
// To overwrite this method we need to forward all the functions from the
// base implementation since they do not use an interface.
func (s *SonataFlowClientBuilder) Build() ctrl.WithWatch {
	cli := s.innerBuilder.Build()
	utils.SetClient(cli)
	return cli
}

func (s *SonataFlowClientBuilder) WithRuntimeObjects(initRuntimeObjs ...runtime.Object) *SonataFlowClientBuilder {
	_ = s.innerBuilder.WithRuntimeObjects(initRuntimeObjs...)
	return s
}

func (s *SonataFlowClientBuilder) WithStatusSubresource(o ...ctrl.Object) *SonataFlowClientBuilder {
	_ = s.innerBuilder.WithStatusSubresource(o...)
	return s
}

// NewSonataFlowClientBuilder creates a new fake.ClientBuilder with the right scheme references
func NewSonataFlowClientBuilder() *SonataFlowClientBuilder {
	s := scheme.Scheme
	utilruntime.Must(operatorapi.AddToScheme(s))
	builder := fake.NewClientBuilder().WithScheme(s)
	return &SonataFlowClientBuilder{
		innerBuilder: builder,
	}
}

func NewSonataFlowClientBuilderWithKnative() *SonataFlowClientBuilder {
	s := scheme.Scheme
	utilruntime.Must(operatorapi.AddToScheme(s))
	utilruntime.Must(servingv1.AddToScheme(s))
	builder := fake.NewClientBuilder().WithScheme(s)
	return &SonataFlowClientBuilder{
		innerBuilder: builder,
	}
}

// NewKogitoClientBuilderWithOpenShift creates a new fake client with OpenShift schemas.
// If your object is not present, just add in the list below.
func NewKogitoClientBuilderWithOpenShift() *SonataFlowClientBuilder {
	s := scheme.Scheme
	utilruntime.Must(routev1.Install(s))
	utilruntime.Must(buildv1.Install(s))
	utilruntime.Must(imgv1.Install(s))
	utilruntime.Must(operatorapi.AddToScheme(s))
	utilruntime.Must(eventingv1.AddToScheme(s))
	utilruntime.Must(sourcesv1.AddToScheme(s))
	utilruntime.Must(prometheus.AddToScheme(s))
	builder := fake.NewClientBuilder().WithScheme(s)
	return &SonataFlowClientBuilder{
		innerBuilder: builder,
	}
}

func MustGetDeployment(t *testing.T, client ctrl.WithWatch, workflow *operatorapi.SonataFlow) *appsv1.Deployment {
	deployment := &appsv1.Deployment{}
	return mustGet(t, client, workflow, deployment).(*appsv1.Deployment)
}

func MustGetService(t *testing.T, client ctrl.WithWatch, workflow *operatorapi.SonataFlow) *v1.Service {
	svc := &v1.Service{}
	return mustGet(t, client, workflow, svc).(*v1.Service)
}

func MustGetConfigMap(t *testing.T, client ctrl.WithWatch, workflow *operatorapi.SonataFlow) *v1.ConfigMap {
	cm := &v1.ConfigMap{}
	return mustGet(t, client, workflow, cm).(*v1.ConfigMap)
}

func MustGetWorkflow(t *testing.T, client ctrl.WithWatch, name types.NamespacedName) *operatorapi.SonataFlow {
	workflow := &operatorapi.SonataFlow{}
	workflow.Name = name.Name
	workflow.Namespace = name.Namespace
	return mustGet(t, client, workflow, workflow).(*operatorapi.SonataFlow)
}

func MustGetBuild(t *testing.T, client ctrl.WithWatch, name types.NamespacedName) *operatorapi.SonataFlowBuild {
	build := &operatorapi.SonataFlowBuild{}
	err := client.Get(context.TODO(), name, build)
	assert.NoError(t, err)
	return build
}

func mustGet(t *testing.T, client ctrl.WithWatch, workflow *operatorapi.SonataFlow, obj ctrl.Object) ctrl.Object {
	err := client.Get(context.TODO(), ctrl.ObjectKeyFromObject(workflow), obj)
	assert.NoError(t, err)
	return obj
}
