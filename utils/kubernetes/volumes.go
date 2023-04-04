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
	v1 "k8s.io/api/core/v1"
)

func ConfigMapsToVolumesAndMount(configMaps map[string]v1.ConfigMap, prefixMountPathMap map[string]string) ([]v1.Volume, []v1.VolumeMount) {
	volumes := make([]v1.Volume, 0)
	volumeMounts := make([]v1.VolumeMount, 0)
	for keycm, cm := range configMaps {
		volumes = append(volumes, Volume(cm.Name, cm.Name))
		volumeMounts = append(volumeMounts, VolumeMount(cm.Name, true, prefixMountPathMap[keycm]))
	}
	return volumes, volumeMounts
}

func Volume(name string, localObjRefName string) v1.Volume {
	return v1.Volume{
		Name: name,
		VolumeSource: v1.VolumeSource{
			ConfigMap: &v1.ConfigMapVolumeSource{
				LocalObjectReference: v1.LocalObjectReference{Name: localObjRefName},
			},
		},
	}
}

func VolumeWithItems(name string, localObjRefName string, items []v1.KeyToPath) v1.Volume {
	return v1.Volume{
		Name: name,
		VolumeSource: v1.VolumeSource{
			ConfigMap: &v1.ConfigMapVolumeSource{
				LocalObjectReference: v1.LocalObjectReference{Name: localObjRefName},
				Items:                items,
			},
		},
	}
}

func VolumeMount(name string, readonly bool, mountPath string) v1.VolumeMount {
	return v1.VolumeMount{
		Name:      name,
		ReadOnly:  readonly,
		MountPath: mountPath,
	}
}
