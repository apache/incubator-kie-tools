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
	"testing"

	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/api"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/util/test"
)

func Test_addResourcesToBuilderContextVolume_specificPath(t *testing.T) {
	cm := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "cm-data",
			Namespace: t.Name(),
		},
		Data: map[string]string{
			"specfile.json": "{}",
		},
	}
	task := api.PublishTask{
		ContextDir: "/build/context",
	}
	build := &api.ContainerBuild{
		ObjectReference: api.ObjectReference{
			Name:      "build",
			Namespace: t.Name(),
		},
		Status: api.ContainerBuildStatus{
			ResourceVolumes: []api.ContainerBuildResourceVolume{
				{
					ReferenceName:  "cm-data",
					ReferenceType:  api.ResourceReferenceTypeConfigMap,
					DestinationDir: "specs",
				},
			},
		},
	}
	volumes := make([]corev1.Volume, 0)
	volumeMounts := make([]corev1.VolumeMount, 0)
	client := test.NewFakeClient(cm)

	err := addResourcesToBuilderContextVolume(context.TODO(), client, task, build, &volumes, &volumeMounts)
	assert.NoError(t, err)

	assert.Len(t, volumes, 1)
	assert.Len(t, volumeMounts, 1)
	assert.Contains(t, volumeMounts[0].MountPath, task.ContextDir)
	assert.Len(t, volumes[0].Projected.Sources, 1)
}

func Test_addResourcesToBuilderContextVolume_rootPath(t *testing.T) {
	cm := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "cm-data",
			Namespace: t.Name(),
		},
		Data: map[string]string{
			"workflow.sw.json": "{}",
			"Dockerfike":       "FROM:RHEL",
		},
	}
	task := api.PublishTask{
		ContextDir: "/build/context",
	}
	build := &api.ContainerBuild{
		ObjectReference: api.ObjectReference{
			Name:      "build",
			Namespace: t.Name(),
		},
		Status: api.ContainerBuildStatus{
			ResourceVolumes: []api.ContainerBuildResourceVolume{
				{
					ReferenceName:  "cm-data",
					ReferenceType:  api.ResourceReferenceTypeConfigMap,
					DestinationDir: "",
				},
			},
		},
	}
	volumes := make([]corev1.Volume, 0)
	volumeMounts := make([]corev1.VolumeMount, 0)
	client := test.NewFakeClient(cm)

	err := addResourcesToBuilderContextVolume(context.TODO(), client, task, build, &volumes, &volumeMounts)
	assert.NoError(t, err)

	assert.Len(t, volumes, 1)
	// one for each file within the CM
	assert.Len(t, volumeMounts, 2)
	assert.Contains(t, volumeMounts[0].MountPath, task.ContextDir)
	assert.Contains(t, volumeMounts[1].MountPath, task.ContextDir)
	assert.Len(t, volumes[0].Projected.Sources, 1)
}

func Test_addResourcesToBuilderContextVolume_multipleCMs(t *testing.T) {
	cm1 := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "cm-data1",
			Namespace: t.Name(),
		},
		Data: map[string]string{
			"workflow.sw.json": "{}",
			"Dockerfike":       "FROM:RHEL",
		},
	}
	cm2 := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "cm-data2",
			Namespace: t.Name(),
		},
		Data: map[string]string{
			"openapi.json": "{}",
		},
	}
	task := api.PublishTask{
		ContextDir: "/build/context",
	}
	build := &api.ContainerBuild{
		ObjectReference: api.ObjectReference{
			Name:      "build",
			Namespace: t.Name(),
		},
		Status: api.ContainerBuildStatus{
			ResourceVolumes: []api.ContainerBuildResourceVolume{
				{
					ReferenceName:  "cm-data1",
					ReferenceType:  api.ResourceReferenceTypeConfigMap,
					DestinationDir: "",
				},
				{
					ReferenceName:  "cm-data2",
					ReferenceType:  api.ResourceReferenceTypeConfigMap,
					DestinationDir: "specs",
				},
			},
		},
	}
	volumes := make([]corev1.Volume, 0)
	volumeMounts := make([]corev1.VolumeMount, 0)
	client := test.NewFakeClient(cm1, cm2)

	err := addResourcesToBuilderContextVolume(context.TODO(), client, task, build, &volumes, &volumeMounts)
	assert.NoError(t, err)

	assert.Len(t, volumes, 2)
	// one for each file within the CM
	assert.Len(t, volumeMounts, 3)
	assert.Contains(t, volumeMounts[0].MountPath, task.ContextDir)
	assert.Contains(t, volumeMounts[1].MountPath, task.ContextDir)
	assert.Contains(t, volumeMounts[2].MountPath, task.ContextDir)
	// Two projections in different dirs
	assert.Len(t, volumes[0].Projected.Sources, 1)
	assert.Len(t, volumes[1].Projected.Sources, 1)
}
