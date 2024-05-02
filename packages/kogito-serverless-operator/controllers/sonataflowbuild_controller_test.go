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

package controllers

import (
	"context"
	"testing"

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/record"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/api"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test"
)

func TestSonataFlowBuildController(t *testing.T) {
	namespace := t.Name()
	ksw := test.GetBaseSonataFlow(namespace)
	ksb := test.GetNewEmptySonataFlowBuild(ksw.Name, namespace)

	cl := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(ksb, ksw).
		WithRuntimeObjects(test.GetBasePlatformInReadyPhase(namespace)).
		WithRuntimeObjects(test.GetSonataFlowBuilderConfig(namespace)).
		WithStatusSubresource(ksb, ksw).
		Build()

	r := &SonataFlowBuildReconciler{cl, cl.Scheme(), &record.FakeRecorder{}, &rest.Config{}}
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

func TestSonataFlowBuildController_WithArgsAndEnv(t *testing.T) {
	namespace := t.Name()
	ksw := test.GetBaseSonataFlow(namespace)
	ksb := test.GetNewEmptySonataFlowBuild(ksw.Name, namespace)

	ksb.Spec.Arguments = make([]string, 1)
	ksb.Spec.Arguments[0] = "--build-args=MYENV=VALUE"
	ksb.Spec.Envs = make([]v1.EnvVar, 1)
	ksb.Spec.Envs[0] = v1.EnvVar{
		Name:  "QUARKUS_EXTENSIONS",
		Value: "extension1,extension2",
	}

	cl := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(ksb, ksw).
		WithRuntimeObjects(test.GetBasePlatformInReadyPhase(namespace)).
		WithRuntimeObjects(test.GetSonataFlowBuilderConfig(namespace)).
		WithStatusSubresource(ksb, ksw).
		Build()

	r := &SonataFlowBuildReconciler{cl, cl.Scheme(), &record.FakeRecorder{}, &rest.Config{}}
	req := reconcile.Request{
		NamespacedName: types.NamespacedName{
			Name:      ksb.Name,
			Namespace: ksb.Namespace,
		},
	}

	result, err := r.Reconcile(context.TODO(), req)
	assert.NoError(t, err)
	assert.Equal(t, requeueAfterForNewBuild, result.RequeueAfter)

	assert.NoError(t, cl.Get(context.TODO(), req.NamespacedName, ksb))
	assert.Equal(t, operatorapi.BuildPhaseScheduling, ksb.Status.BuildPhase)
	assert.NotNil(t, ksb.Status.InnerBuild)

	containerBuild := &api.ContainerBuild{}
	assert.NoError(t, ksb.Status.GetInnerBuild(containerBuild))
	assert.Equal(t, string(ksb.Status.BuildPhase), string(containerBuild.Status.Phase))
	assert.Len(t, containerBuild.Spec.Tasks[0].Kaniko.AdditionalFlags, 1)
	assert.Len(t, containerBuild.Spec.Tasks[0].Kaniko.Envs, 1)
}

func TestSonataFlowBuildController_MarkToRestart(t *testing.T) {
	namespace := t.Name()
	ksw := test.GetBaseSonataFlow(namespace)
	ksb := test.GetNewEmptySonataFlowBuild(ksw.Name, namespace)
	ksb.Annotations = map[string]string{operatorapi.BuildRestartAnnotation: "true"}

	cl := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(ksb, ksw).
		WithRuntimeObjects(test.GetBasePlatformInReadyPhase(namespace)).
		WithRuntimeObjects(test.GetSonataFlowBuilderConfig(namespace)).
		WithStatusSubresource(ksb, ksw).
		Build()

	r := &SonataFlowBuildReconciler{cl, cl.Scheme(), &record.FakeRecorder{}, &rest.Config{}}
	req := reconcile.Request{
		NamespacedName: types.NamespacedName{
			Name:      ksb.Name,
			Namespace: ksb.Namespace,
		},
	}

	_, err := r.Reconcile(context.TODO(), req)
	assert.NoError(t, err)
	ksb = test.MustGetBuild(t, cl, types.NamespacedName{Name: ksb.Name, Namespace: namespace})
	assert.Equal(t, "false", ksb.Annotations[operatorapi.BuildRestartAnnotation])
}
