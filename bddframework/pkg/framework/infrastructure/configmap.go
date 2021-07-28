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
	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"path"
	"reflect"
	"strconv"
)

const (
	// FromFileKey ket to indicate that configmap is created using file.
	FromFileKey = "from-file"

	// FromLiteralKey ket to indicate that configmap is created using literals.
	FromLiteralKey = "from-literal"

	// MountPathKey volume mount path
	MountPathKey = "mount-path"

	// FileModeKey define file permission
	FileModeKey = "file-mode"
)

// ConfigMapHandler ...
type ConfigMapHandler interface {
	FetchConfigMap(key types.NamespacedName) (*corev1.ConfigMap, error)
	FetchConfigMapForOwner(owner resource.KubernetesResource) ([]*corev1.ConfigMap, error)
	FetchConfigMapsForLabel(namespace string, labels map[string]string) (*corev1.ConfigMapList, error)
	MountConfigMapOnDeployment(deployment *appsv1.Deployment, configMap *corev1.ConfigMap) error
	GetComparator() compare.MapComparator
	RemoveConfigMapOwnership(key types.NamespacedName, owner resource.KubernetesResource) (err error)
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

func (c *configMapHandler) FetchConfigMapForOwner(owner resource.KubernetesResource) ([]*corev1.ConfigMap, error) {
	c.Log.Debug("fetching config map for given owner.")
	objectTypes := []runtime.Object{&corev1.ConfigMapList{}}
	resources, err := kubernetes.ResourceC(c.Client).ListAll(objectTypes, owner.GetNamespace(), owner)
	if err != nil {
		return nil, err
	}
	kubernetesResources := resources[reflect.TypeOf(corev1.ConfigMap{})]
	configMapList := make([]*corev1.ConfigMap, len(kubernetesResources))
	for i, kubernetesResource := range kubernetesResources {
		configMapList[i] = kubernetesResource.(*corev1.ConfigMap)
	}
	return configMapList, nil
}

func (c *configMapHandler) MountConfigMapOnDeployment(deployment *appsv1.Deployment, configMap *corev1.ConfigMap) error {
	if isConfigMapCreatedUsingFile(configMap) {
		if err := mountAsFile(deployment, configMap); err != nil {
			return err
		}
	}

	if isConfigMapCreatedUsingLiteral(configMap) {
		mountAsLiteral(deployment, configMap)
	}
	return nil
}

func isConfigMapCreatedUsingFile(configMap *corev1.ConfigMap) bool {
	return configMap.Annotations[FromFileKey] == "true"
}

func mountAsFile(deployment *appsv1.Deployment, configMap *corev1.ConfigMap) error {
	if err := appendVolumeIntoDeployment(deployment, configMap); err != nil {
		return err
	}
	appendVolumeMountIntoDeployment(deployment, configMap)
	return nil
}

func appendVolumeIntoDeployment(deployment *appsv1.Deployment, cm *corev1.ConfigMap) (err error) {
	for _, volume := range deployment.Spec.Template.Spec.Volumes {
		if volume.Name == cm.Name {
			return
		}
	}

	fileMode, err := strconv.ParseInt(cm.Annotations[FileModeKey], 0, 32)
	if err != nil {
		return
	}
	fileModeInt32 := int32(fileMode)

	// append volume if its not exists
	deployment.Spec.Template.Spec.Volumes =
		append(deployment.Spec.Template.Spec.Volumes, corev1.Volume{
			Name: cm.Name,
			VolumeSource: corev1.VolumeSource{
				ConfigMap: &corev1.ConfigMapVolumeSource{
					DefaultMode: &fileModeInt32,
					LocalObjectReference: corev1.LocalObjectReference{
						Name: cm.Name,
					},
				},
			},
		})
	return nil
}

func appendVolumeMountIntoDeployment(deployment *appsv1.Deployment, cm *corev1.ConfigMap) {
	for fileName := range cm.Data {
		mountPath := path.Join(cm.Annotations[MountPathKey], fileName)
		for _, volumeMount := range deployment.Spec.Template.Spec.Containers[0].VolumeMounts {
			if volumeMount.MountPath == mountPath {
				return
			}
		}

		// append volume mount if its not exists
		deployment.Spec.Template.Spec.Containers[0].VolumeMounts =
			append(deployment.Spec.Template.Spec.Containers[0].VolumeMounts, corev1.VolumeMount{
				Name:      cm.Name,
				MountPath: mountPath,
				SubPath:   fileName,
			})
	}
}

func isConfigMapCreatedUsingLiteral(configMap *corev1.ConfigMap) bool {
	return configMap.Annotations[FromLiteralKey] == "true"
}

func mountAsLiteral(deployment *appsv1.Deployment, configMap *corev1.ConfigMap) {
	for _, envFrom := range deployment.Spec.Template.Spec.Containers[0].EnvFrom {
		if envFrom.ConfigMapRef.LocalObjectReference.Name == configMap.GetName() {
			return
		}
	}
	envFromSource := corev1.EnvFromSource{
		ConfigMapRef: &corev1.ConfigMapEnvSource{
			LocalObjectReference: corev1.LocalObjectReference{
				Name: configMap.GetName(),
			},
		},
	}
	deployment.Spec.Template.Spec.Containers[0].EnvFrom = append(deployment.Spec.Template.Spec.Containers[0].EnvFrom, envFromSource)
}

func (c *configMapHandler) RemoveConfigMapOwnership(key types.NamespacedName, owner resource.KubernetesResource) (err error) {
	c.Log.Debug("Removing Configmap ownership", "configmap", key.Name, "owner", owner.GetName())
	configMap, err := c.FetchConfigMap(key)
	if err != nil || configMap == nil {
		return
	}
	framework.RemoveOwnerReference(owner, configMap)
	if err = kubernetes.ResourceC(c.Client).Update(configMap); err != nil {
		return err
	}
	c.Log.Debug("Successfully removed Configmap ownership", "configmap", configMap.GetName(), "owner", owner.GetName())
	return
}
