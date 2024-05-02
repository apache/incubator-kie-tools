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

package builder

import (
	"context"
	"testing"

	buildv1 "github.com/openshift/api/build/v1"
	imgv1 "github.com/openshift/api/image/v1"
	buildfake "github.com/openshift/client-go/build/clientset/versioned/fake"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/workflowdef"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test"
)

func Test_openshiftBuilderManager_Reconcile(t *testing.T) {
	// Setup
	ns := t.Name()
	workflow := test.GetBaseSonataFlow(ns)
	platform := test.GetBasePlatformInReadyPhase(t.Name())
	config := test.GetSonataFlowBuilderConfig(ns)
	namespacedName := types.NamespacedName{Namespace: workflow.Namespace, Name: workflow.Name}
	client := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(workflow, platform, config).Build()
	buildClient := buildfake.NewSimpleClientset().BuildV1()

	managerContext := buildManagerContext{
		ctx:              context.TODO(),
		client:           client,
		platform:         platform,
		builderConfigMap: config,
	}

	buildManager := newOpenShiftBuilderManagerWithClient(managerContext, buildClient)
	// End Setup

	// Schedule a build
	kogitoBuildManager := NewSonataFlowBuildManager(context.TODO(), client)
	kbuild, err := kogitoBuildManager.GetOrCreateBuild(workflow)
	assert.NoError(t, err)
	assert.NotNil(t, kbuild)
	assert.NoError(t, buildManager.Schedule(kbuild))
	assert.NoError(t, client.Update(context.TODO(), kbuild))
	assert.Equal(t, operatorapi.BuildPhaseInitialization, kbuild.Status.BuildPhase)

	// Verify if we have the BC and IS
	bc := &buildv1.BuildConfig{}
	assert.NoError(t, client.Get(context.TODO(), namespacedName, bc))
	is := &imgv1.ImageStream{}
	assert.NoError(t, client.Get(context.TODO(), namespacedName, is))
	assert.Contains(t, *bc.Spec.Source.Dockerfile, "FROM "+workflowdef.GetDefaultWorkflowBuilderImageTag()+" AS builder")

	// Reconcile
	// unfortunately, the fake buildclient doesn't implement the RESTAPI, thus we can't push a new build to it
	// so we emulate.
	ocpBuild := &buildv1.Build{
		ObjectMeta: metav1.ObjectMeta{Name: namespacedName.Name, Namespace: namespacedName.Namespace},
		Spec:       buildv1.BuildSpec{},
		Status: buildv1.BuildStatus{
			Phase: buildv1.BuildPhaseRunning,
		},
	}
	assert.NoError(t, client.Create(context.TODO(), ocpBuild))
	kbuild.Status.BuildPhase = operatorapi.BuildPhaseRunning
	assert.NoError(t, kbuild.Status.SetInnerBuild(ocpBuild))
	assert.NoError(t, buildManager.Reconcile(kbuild))
	assert.NoError(t, client.Update(context.TODO(), kbuild))

	assert.NotNil(t, kbuild.Status.InnerBuild.Raw)
}

func Test_openshiftbuilder_externalCMs(t *testing.T) {
	ns := t.Name()
	workflow := test.GetBaseSonataFlow(ns)
	platform := test.GetBasePlatformInReadyPhase(t.Name())
	config := test.GetSonataFlowBuilderConfig(ns)
	externalCm := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "myopenapis",
			Namespace: ns,
		},
	}
	workflow.Spec.Resources.ConfigMaps = append(workflow.Spec.Resources.ConfigMaps,
		operatorapi.ConfigMapWorkflowResource{ConfigMap: v1.LocalObjectReference{Name: externalCm.Name}})

	namespacedName := types.NamespacedName{Namespace: workflow.Namespace, Name: workflow.Name}
	client := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(workflow, platform, config, externalCm).Build()
	buildClient := buildfake.NewSimpleClientset().BuildV1()

	managerContext := buildManagerContext{
		ctx:              context.TODO(),
		client:           client,
		platform:         platform,
		builderConfigMap: config,
	}

	buildManager := newOpenShiftBuilderManagerWithClient(managerContext, buildClient)
	// End Setup

	// Schedule a build
	kogitoBuildManager := NewSonataFlowBuildManager(context.TODO(), client)
	kbuild, err := kogitoBuildManager.GetOrCreateBuild(workflow)
	assert.NoError(t, err)

	assert.NoError(t, buildManager.Schedule(kbuild))

	bc := &buildv1.BuildConfig{}
	assert.NoError(t, client.Get(context.TODO(), namespacedName, bc))

	assert.Len(t, bc.Spec.Source.ConfigMaps, 1)
}

func Test_openshiftbuilder_forcePull(t *testing.T) {
	// Setup
	ns := t.Name()
	workflow := test.GetBaseSonataFlow(ns)
	platform := test.GetBasePlatformInReadyPhase(t.Name())
	config := test.GetSonataFlowBuilderConfig(ns)
	namespacedName := types.NamespacedName{Namespace: workflow.Namespace, Name: workflow.Name}
	client := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(workflow, platform, config).Build()
	buildClient := buildfake.NewSimpleClientset().BuildV1()
	managerContext := buildManagerContext{
		ctx:              context.TODO(),
		client:           client,
		platform:         platform,
		builderConfigMap: config,
	}

	buildManager := newOpenShiftBuilderManagerWithClient(managerContext, buildClient)
	// End Setup

	// Schedule a build
	kogitoBuildManager := NewSonataFlowBuildManager(context.TODO(), client)
	kbuild, err := kogitoBuildManager.GetOrCreateBuild(workflow)
	assert.NoError(t, err)
	assert.NotNil(t, kbuild)
	assert.NoError(t, buildManager.Schedule(kbuild))

	bc := &buildv1.BuildConfig{}
	assert.NoError(t, client.Get(context.TODO(), namespacedName, bc))

	// verify if we set force pull to BC
	assert.True(t, bc.Spec.Strategy.DockerStrategy.ForcePull)
}
