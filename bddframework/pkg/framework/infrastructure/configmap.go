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

package infrastructure

import (
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"path"
	"reflect"
)

const (
	// DefaultFileMountPath ...
	DefaultFileMountPath = operator.KogitoHomeDir + "/config"
)

// ConfigMapHandler ...
type ConfigMapHandler interface {
	FetchConfigMap(key types.NamespacedName) (*corev1.ConfigMap, error)
	FetchConfigMapsForLabel(namespace string, labels map[string]string) (*corev1.ConfigMapList, error)
	MountAsVolume(deployment *appsv1.Deployment, volumeReference api.VolumeReferenceInterface) error
	MountAsEnvFrom(deployment *appsv1.Deployment, cmName string)
	GetComparator() compare.MapComparator
}

type configMapHandler struct {
	operator.Context
}

// NewConfigMapHandler ...
func NewConfigMapHandler(context operator.Context) ConfigMapHandler {
	return &configMapHandler{
		Context: context,
	}
}

func (c *configMapHandler) FetchConfigMap(key types.NamespacedName) (*corev1.ConfigMap, error) {
	c.Log.Debug("fetching config map.")
	configMap := &corev1.ConfigMap{}
	if exists, err := kubernetes.ResourceC(c.Client).FetchWithKey(key, configMap); err != nil {
		return nil, err
	} else if !exists {
		c.Log.Debug("Configmap not found.")
		return nil, nil
	} else {
		c.Log.Debug("Successfully fetch deployed Configmap")
		return configMap, nil
	}
}

func (c *configMapHandler) GetComparator() compare.MapComparator {
	resourceComparator := compare.DefaultComparator()
	resourceComparator.SetComparator(
		framework.NewComparatorBuilder().
			WithType(reflect.TypeOf(corev1.ConfigMap{})).
			WithCustomComparator(framework.CreateConfigMapComparator()).
			Build())
	return compare.MapComparator{Comparator: resourceComparator}
}

// FetchConfigMapsForLabel will return every configMap with an given labels in the given namespace
func (c *configMapHandler) FetchConfigMapsForLabel(namespace string, labels map[string]string) (*corev1.ConfigMapList, error) {
	cms := &corev1.ConfigMapList{}
	if err := kubernetes.ResourceC(c.Client).ListWithNamespaceAndLabel(namespace, cms, labels); err != nil {
		return nil, err
	}
	return cms, nil
}

func (c *configMapHandler) MountAsVolume(deployment *appsv1.Deployment, volumeReference api.VolumeReferenceInterface) error {
	c.appendVolumeIntoDeployment(deployment, volumeReference)
	return c.appendVolumeMountIntoDeployment(deployment, volumeReference)
}

func (c *configMapHandler) appendVolumeIntoDeployment(deployment *appsv1.Deployment, configMapReference api.VolumeReferenceInterface) {
	for _, volume := range deployment.Spec.Template.Spec.Volumes {
		if volume.Name == configMapReference.GetName() {
			return
		}
	}
	defaultMode := configMapReference.GetFileMode()
	if defaultMode == nil {
		defaultMode = &framework.ModeForPropertyFiles
	}
	// append volume if its not exists
	deployment.Spec.Template.Spec.Volumes =
		append(deployment.Spec.Template.Spec.Volumes, corev1.Volume{
			Name: configMapReference.GetName(),
			VolumeSource: corev1.VolumeSource{
				ConfigMap: &corev1.ConfigMapVolumeSource{
					DefaultMode: defaultMode,
					LocalObjectReference: corev1.LocalObjectReference{
						Name: configMapReference.GetName(),
					},
					Optional: configMapReference.IsOptional(),
				},
			},
		})
}

func (c *configMapHandler) appendVolumeMountIntoDeployment(deployment *appsv1.Deployment, configMapReference api.VolumeReferenceInterface) error {
	cm, err := c.FetchConfigMap(types.NamespacedName{Name: configMapReference.GetName(), Namespace: deployment.Namespace})
	if err != nil {
		return err
	}
	for fileName := range cm.Data {
		mountPath := configMapReference.GetMountPath()
		if len(mountPath) == 0 {
			mountPath = DefaultFileMountPath
		}
		mountPath = path.Join(mountPath, fileName)

		if c.isVolumeMountExists(deployment, mountPath) {
			continue
		}

		// append volume mount if its not exists
		deployment.Spec.Template.Spec.Containers[0].VolumeMounts =
			append(deployment.Spec.Template.Spec.Containers[0].VolumeMounts, corev1.VolumeMount{
				Name:      configMapReference.GetName(),
				MountPath: mountPath,
				SubPath:   fileName,
			})
	}
	return nil
}

func (c *configMapHandler) isVolumeMountExists(deployment *appsv1.Deployment, mountPath string) bool {
	for _, volumeMount := range deployment.Spec.Template.Spec.Containers[0].VolumeMounts {
		if volumeMount.MountPath == mountPath {
			return true
		}
	}
	return false
}

func (c *configMapHandler) MountAsEnvFrom(deployment *appsv1.Deployment, cmName string) {
	for _, envFrom := range deployment.Spec.Template.Spec.Containers[0].EnvFrom {
		if envFrom.ConfigMapRef != nil && envFrom.ConfigMapRef.LocalObjectReference.Name == cmName {
			return
		}
	}
	envFromSource := corev1.EnvFromSource{
		ConfigMapRef: &corev1.ConfigMapEnvSource{
			LocalObjectReference: corev1.LocalObjectReference{
				Name: cmName,
			},
		},
	}
	deployment.Spec.Template.Spec.Containers[0].EnvFrom = append(deployment.Spec.Template.Spec.Containers[0].EnvFrom, envFromSource)
}
