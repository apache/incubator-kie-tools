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

// AddOrReplaceVolume adds or removes the given volumes to the PodSpec.
// If there's already a volume with the same name, it's replaced.
func AddOrReplaceVolume(podSpec *corev1.PodSpec, volumes ...corev1.Volume) {
	// volumes iterated here are read/constructed by the caller following the order defined in the original CRD, and that
	// order must be preserved. If not preserved, in the reconciliation cycles an order change in the volumes might be
	// interpreted as configuration change in the original resource, causing undesired side effects like creating
	// a new ReplicaSet for a deployment with the subsequent pods spawning reported here.
	volumesToAdd := make([]corev1.Volume, 0)
	wasAdded := false
	for _, volume := range volumes {
		wasAdded = false
		for i := 0; !wasAdded && i < len(podSpec.Volumes); i++ {
			if volume.Name == podSpec.Volumes[i].Name {
				// replace existing
				podSpec.Volumes[i] = volume
				wasAdded = true
			}
		}
		if !wasAdded {
			// remember to add it later in order
			volumesToAdd = append(volumesToAdd, volume)
		}
	}
	for _, volume := range volumesToAdd {
		podSpec.Volumes = append(podSpec.Volumes, volume)
	}
}

// AddOrReplaceVolumeMount same as AddOrReplaceVolume, but with VolumeMounts in a specific container
func AddOrReplaceVolumeMount(containerIndex int, podSpec *corev1.PodSpec, mounts ...corev1.VolumeMount) {
	// analogous to AddOrReplaceVolume function, the processing must be realized en order.
	// see: AddOrReplaceVolume
	mountsToAdd := make([]corev1.VolumeMount, 0)
	wasAdded := false
	container := &podSpec.Containers[containerIndex]
	for _, mount := range mounts {
		wasAdded = false
		for i := 0; !wasAdded && i < len(container.VolumeMounts); i++ {
			if mount.Name == container.VolumeMounts[i].Name {
				// replace existing
				container.VolumeMounts[i] = mount
				wasAdded = true
			}
		}
		if !wasAdded {
			// remember to add it later in order
			mountsToAdd = append(mountsToAdd, mount)
		}
	}
	for _, mount := range mountsToAdd {
		container.VolumeMounts = append(container.VolumeMounts, mount)
	}
}
