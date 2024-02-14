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

package kubernetes

import (
	"testing"

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
)

func TestReplaceOrAddVolume(t *testing.T) {
	podSpec := v1.PodSpec{Volumes: []v1.Volume{
		{Name: "volume1", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cm1"},
		}}},
		{Name: "volume2", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cm2"},
		}}},
	}}
	volumes := []v1.Volume{
		{Name: "volume1", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cmA"},
		}}},
		{Name: "volume2", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cmB"},
		}}},
		{Name: "volume3", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cmC"},
		}}},
	}

	AddOrReplaceVolume(&podSpec, volumes...)

	assert.Len(t, podSpec.Volumes, 3)
	assert.Equal(t, "cmA", podSpec.Volumes[0].ConfigMap.Name)
	assert.Equal(t, "cmB", podSpec.Volumes[1].ConfigMap.Name)
	assert.Equal(t, "cmC", podSpec.Volumes[2].ConfigMap.Name)
}

func TestReplaceOrAddVolume_Append(t *testing.T) {
	podSpec := v1.PodSpec{Volumes: []v1.Volume{
		{Name: "volume1", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cm1"},
		}}},
	}}
	volumes := []v1.Volume{
		{Name: "volume2", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cmB"},
		}}},
	}

	AddOrReplaceVolume(&podSpec, volumes...)

	assert.Len(t, podSpec.Volumes, 2)
	assert.Equal(t, "cm1", podSpec.Volumes[0].ConfigMap.Name)
	assert.Equal(t, "cmB", podSpec.Volumes[1].ConfigMap.Name)
}

func TestReplaceOrAddVolume_EmptyVolumes(t *testing.T) {
	podSpec := v1.PodSpec{Volumes: []v1.Volume{
		{Name: "volume1", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cm1"},
		}}},
		{Name: "volume2", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cm2"},
		}}},
	}}
	var volumes []v1.Volume

	AddOrReplaceVolume(&podSpec, volumes...)

	assert.Len(t, podSpec.Volumes, 2)
	assert.Equal(t, "cm1", podSpec.Volumes[0].ConfigMap.Name)
	assert.Equal(t, "cm2", podSpec.Volumes[1].ConfigMap.Name)
}

func TestReplaceOrAddVolume_EmptyPodVolumes(t *testing.T) {
	podSpec := v1.PodSpec{}
	volumes := []v1.Volume{
		{Name: "volume1", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cmA"},
		}}},
		{Name: "volume2", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cmB"},
		}}},
		{Name: "volume3", VolumeSource: v1.VolumeSource{ConfigMap: &v1.ConfigMapVolumeSource{
			LocalObjectReference: v1.LocalObjectReference{Name: "cmC"},
		}}},
	}

	AddOrReplaceVolume(&podSpec, volumes...)

	assert.Len(t, podSpec.Volumes, 3)
	assert.Equal(t, "cmA", podSpec.Volumes[0].ConfigMap.Name)
	assert.Equal(t, "cmB", podSpec.Volumes[1].ConfigMap.Name)
	assert.Equal(t, "cmC", podSpec.Volumes[2].ConfigMap.Name)
}

func TestAddOrReplaceVolumeMount(t *testing.T) {
	podSpec := v1.PodSpec{
		Containers: []v1.Container{
			{Name: "container1", VolumeMounts: []v1.VolumeMount{
				{Name: "mount1", MountPath: "/tmp/any/path"},
			}},
		},
	}
	mounts := []v1.VolumeMount{
		{Name: "mount2", MountPath: "/tmp/any/path"},
		{Name: "mount1", MountPath: "/dev"},
	}

	AddOrReplaceVolumeMount(0, &podSpec, mounts...)
	assert.Len(t, podSpec.Containers[0].VolumeMounts, 2)
	assert.Equal(t, "/dev", podSpec.Containers[0].VolumeMounts[0].MountPath)
	assert.Equal(t, "/tmp/any/path", podSpec.Containers[0].VolumeMounts[1].MountPath)
}
