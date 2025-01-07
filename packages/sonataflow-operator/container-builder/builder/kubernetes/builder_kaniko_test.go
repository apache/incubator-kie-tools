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

package kubernetes

import (
	"context"
	"os"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	resource2 "k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/api"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/util"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/util/test"
)

// Test that verify we are able to create a Kaniko build with cache enabled, a specific set of resources and additional flags
func TestNewBuildWithKanikoCustomizations(t *testing.T) {
	ns := "test"
	c := test.NewFakeClient()

	dockerFile, err := os.ReadFile("testdata/sample.Dockerfile")
	assert.NoError(t, err)

	workflowDefinition, err := os.ReadFile("testdata/greetings.sw.json")
	assert.NoError(t, err)

	platform := api.PlatformContainerBuild{
		ObjectReference: api.ObjectReference{
			Namespace: ns,
			Name:      "testPlatform",
		},
		Spec: api.PlatformContainerBuildSpec{
			BuildStrategy:   api.ContainerBuildStrategyPod,
			PublishStrategy: api.PlatformBuildPublishStrategyKaniko,
			Timeout:         &metav1.Duration{Duration: 5 * time.Minute},
		},
	}

	// Sample CPU and Memory quantities
	cpuQty, _ := resource2.ParseQuantity("1")
	memQty, _ := resource2.ParseQuantity("1Gi")

	// Sample additional flag
	addFlags := make([]string, 1)
	addFlags[0] = "--use-new-run=true"

	// create the new build, schedule with cache enabled, a specific set of resources and additional flags
	build, err := NewBuild(ContainerBuilderInfo{FinalImageName: "host/namespace/myservice:latest", BuildUniqueName: "build1", Platform: platform}).
		AddResource("Dockerfile", dockerFile).
		AddResource("greetings.sw.json", workflowDefinition).
		WithClient(c).
		Scheduler().
		WithProperty(KanikoCache, api.KanikoTaskCache{Enabled: util.Pbool(true), PersistentVolumeClaim: "kaniko-cache-pv"}).
		WithResourceRequirements(v1.ResourceRequirements{
			Limits: v1.ResourceList{
				v1.ResourceCPU:    cpuQty,
				v1.ResourceMemory: memQty,
			},
			Requests: v1.ResourceList{
				v1.ResourceCPU:    cpuQty,
				v1.ResourceMemory: memQty,
			},
		}).
		WithAdditionalArgs(addFlags).
		Schedule()

	assert.NoError(t, err)
	assert.NotNil(t, build)
	assert.Equal(t, api.ContainerBuildPhaseScheduling, build.Status.Phase)

	build, err = FromBuild(build).WithClient(c).Reconcile()
	assert.NoError(t, err)
	assert.NotNil(t, build)
	assert.Equal(t, api.ContainerBuildPhasePending, build.Status.Phase)

	// The status won't change since FakeClient won't set the status upon creation, since we don't have a controller :)
	build, err = FromBuild(build).WithClient(c).Reconcile()
	assert.NoError(t, err)
	assert.NotNil(t, build)
	assert.Equal(t, api.ContainerBuildPhasePending, build.Status.Phase)

	podName := buildPodName(build)
	pod := &v1.Pod{}
	err = c.Get(context.TODO(), types.NamespacedName{Name: podName, Namespace: ns}, pod)
	assert.NoError(t, err)
	assert.NotNil(t, pod)
	assert.Len(t, pod.Spec.Volumes, 1)

	assert.Subset(t, pod.Spec.Containers[0].Args, addFlags)
}

func TestNewBuildWithKanikoWithBuildArgsAndEnv(t *testing.T) {
	ns := "test"
	c := test.NewFakeClient()

	dockerFile, err := os.ReadFile("testdata/sample.Dockerfile")
	assert.NoError(t, err)

	workflowDefinition, err := os.ReadFile("testdata/greetings.sw.json")
	assert.NoError(t, err)

	platform := api.PlatformContainerBuild{
		ObjectReference: api.ObjectReference{
			Namespace: ns,
			Name:      "testPlatform",
		},
		Spec: api.PlatformContainerBuildSpec{
			BuildStrategy:   api.ContainerBuildStrategyPod,
			PublishStrategy: api.PlatformBuildPublishStrategyKaniko,
			Timeout:         &metav1.Duration{Duration: 5 * time.Minute},
		},
	}

	build, err := NewBuild(ContainerBuilderInfo{FinalImageName: "host/namespace/service:latest", BuildUniqueName: "build1", Platform: platform}).
		AddResource("Dockerfile", dockerFile).
		AddResource("greetings.sw.json", workflowDefinition).
		WithClient(c).
		Scheduler().
		WithBuildArgs([]v1.EnvVar{{
			Name:  "QUARKUS_EXTENSIONS",
			Value: "extension1,extension2",
		}, {
			Name:  "MY_PROPERTY",
			Value: "my_property_value",
		}}).
		WithEnvs([]v1.EnvVar{{
			Name:  "MYENV",
			Value: "value",
		}}).
		Schedule()
	assert.NoError(t, err)

	// reconcile twice to push forward to the pod creation
	build, err = FromBuild(build).WithClient(c).Reconcile()
	assert.NoError(t, err)
	assert.NotNil(t, build)
	build, err = FromBuild(build).WithClient(c).Reconcile()
	assert.NoError(t, err)
	assert.NotNil(t, build)

	podName := buildPodName(build)
	pod := &v1.Pod{}
	err = c.Get(context.TODO(), types.NamespacedName{Name: podName, Namespace: ns}, pod)
	assert.NoError(t, err)
	assert.NotNil(t, pod)

	assert.Subset(t, pod.Spec.Containers[0].Args, []string{"--build-arg=QUARKUS_EXTENSIONS=extension1,extension2"})
	assert.Subset(t, pod.Spec.Containers[0].Args, []string{"--build-arg=MY_PROPERTY=my_property_value"})
	assert.Subset(t, pod.Spec.Containers[0].Env, []v1.EnvVar{{Name: "MYENV", Value: "value"}})
}
