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
	corev1 "k8s.io/api/core/v1"
)

// VolumeProjectionAddConfigMap adds the given ConfigMap to the ProjectedVolumeSource sources.
// Overrides the items if already exists in the list.
func VolumeProjectionAddConfigMap(volumeSource *corev1.ProjectedVolumeSource, cmName string, items ...corev1.KeyToPath) {
	for _, source := range volumeSource.Sources {
		if source.ConfigMap.Name == cmName {
			source.ConfigMap.Items = items
			return
		}
	}

	volumeSource.Sources = append(volumeSource.Sources, corev1.VolumeProjection{ConfigMap: &corev1.ConfigMapProjection{
		LocalObjectReference: corev1.LocalObjectReference{Name: cmName},
		Items:                items,
	}})

}

// VolumeAddVolumeProjectionConfigMap adds a new ConfigMapProjection to the given Volume array.
// It looks for the given mount name in the Volume array.
// If finds it, adds a new projection for the given ConfigMap.
// If it doesn't find it, adds a new VolumeSource and the projection to it.
func VolumeAddVolumeProjectionConfigMap(volumes []corev1.Volume, cmName, mountName string) []corev1.Volume {
	resourceProjection :=
		corev1.VolumeProjection{ConfigMap: &corev1.ConfigMapProjection{LocalObjectReference: corev1.LocalObjectReference{Name: cmName}}}
	projectionExists := false
	for i, vol := range volumes {
		if vol.Name == mountName {
			volumes[i].Projected.Sources =
				append(volumes[i].Projected.Sources, resourceProjection)
			projectionExists = true
		}
	}
	if !projectionExists {
		volumes = append(volumes,
			corev1.Volume{
				Name: mountName,
				VolumeSource: corev1.VolumeSource{
					Projected: &corev1.ProjectedVolumeSource{Sources: []corev1.VolumeProjection{resourceProjection}}}})
	}
	return volumes
}

// VolumeConfigMap creates a new Volume referencing the given ConfigMap name.
func VolumeConfigMap(name string, cmName string, items ...corev1.KeyToPath) corev1.Volume {
	return corev1.Volume{
		Name: name,
		VolumeSource: corev1.VolumeSource{
			ConfigMap: &corev1.ConfigMapVolumeSource{
				LocalObjectReference: corev1.LocalObjectReference{Name: cmName},
				Items:                items,
			},
		},
	}
}

func VolumeMount(name string, readonly bool, mountPath string) corev1.VolumeMount {
	return corev1.VolumeMount{
		Name:      name,
		ReadOnly:  readonly,
		MountPath: mountPath,
	}
}

// VolumeMountAdd adds a new VolumeMount to the given collection.
// If there's already a VolumeMount with the same mount path, the function overrides the name.
func VolumeMountAdd(volumeMount []corev1.VolumeMount, name, mountPath string) []corev1.VolumeMount {
	for _, v := range volumeMount {
		if v.MountPath == mountPath {
			v.Name = name
			return volumeMount
		}
	}
	return append(volumeMount, corev1.VolumeMount{Name: name, MountPath: mountPath})
}
