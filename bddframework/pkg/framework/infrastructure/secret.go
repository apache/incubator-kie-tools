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
	"fmt"
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"path"
)

// SecretHandler ...
type SecretHandler interface {
	FetchSecret(key types.NamespacedName) (*corev1.Secret, error)
	MustFetchSecret(key types.NamespacedName) (*corev1.Secret, error)
	GetComparator() compare.MapComparator
	MountAsVolume(deployment *appsv1.Deployment, volumeReference api.VolumeReferenceInterface) error
	MountAsEnvFrom(deployment *appsv1.Deployment, cmName string)
}

type secretHandler struct {
	operator.Context
}

// NewSecretHandler ...
func NewSecretHandler(context operator.Context) SecretHandler {
	return &secretHandler{
		Context: context,
	}
}

func (c *secretHandler) FetchSecret(key types.NamespacedName) (*corev1.Secret, error) {
	c.Log.Debug("fetching secret.")
	secret := &corev1.Secret{}
	if exists, err := kubernetes.ResourceC(c.Client).FetchWithKey(key, secret); err != nil {
		return nil, err
	} else if !exists {
		c.Log.Debug("Secret not found.")
		return nil, nil
	} else {
		c.Log.Debug("Successfully fetch deployed Secret")
		return secret, nil
	}
}

func (c *secretHandler) MustFetchSecret(key types.NamespacedName) (*corev1.Secret, error) {
	c.Log.Debug("going to must fetch deployed secret.")
	if secret, err := c.FetchSecret(key); err != nil {
		return nil, err
	} else if secret == nil {
		return nil, fmt.Errorf("Secret not found with name %s in namespace %s ", key.Name, key.Namespace)
	} else {
		return secret, nil
	}
}

func (c *secretHandler) MountAsVolume(deployment *appsv1.Deployment, volumeReference api.VolumeReferenceInterface) error {
	c.appendVolumeIntoDeployment(deployment, volumeReference)
	if err := c.appendVolumeMountIntoDeployment(deployment, volumeReference); err != nil {
		return err
	}
	return nil
}

func (c *secretHandler) appendVolumeIntoDeployment(deployment *appsv1.Deployment, secretReference api.VolumeReferenceInterface) {
	for _, volume := range deployment.Spec.Template.Spec.Volumes {
		if volume.Name == secretReference.GetName() {
			return
		}
	}
	defaultMode := secretReference.GetFileMode()
	if defaultMode == nil {
		defaultMode = &framework.ModeForPropertyFiles
	}
	// append volume if its not exists
	deployment.Spec.Template.Spec.Volumes =
		append(deployment.Spec.Template.Spec.Volumes, corev1.Volume{
			Name: secretReference.GetName(),
			VolumeSource: corev1.VolumeSource{
				Secret: &corev1.SecretVolumeSource{
					DefaultMode: defaultMode,
					SecretName:  secretReference.GetName(),
					Optional:    secretReference.IsOptional(),
				},
			},
		})
}

func (c *secretHandler) appendVolumeMountIntoDeployment(deployment *appsv1.Deployment, secretReference api.VolumeReferenceInterface) error {
	cm, err := c.FetchSecret(types.NamespacedName{Name: secretReference.GetName(), Namespace: deployment.Namespace})
	if err != nil {
		return err
	}
	for fileName := range cm.Data {
		mountPath := secretReference.GetMountPath()
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
				Name:      secretReference.GetName(),
				MountPath: mountPath,
				SubPath:   fileName,
			})
	}
	return nil
}

func (c *secretHandler) isVolumeMountExists(deployment *appsv1.Deployment, mountPath string) bool {
	for _, volumeMount := range deployment.Spec.Template.Spec.Containers[0].VolumeMounts {
		if volumeMount.MountPath == mountPath {
			return true
		}
	}
	return false
}

func (c *secretHandler) MountAsEnvFrom(deployment *appsv1.Deployment, cmName string) {
	for _, envFrom := range deployment.Spec.Template.Spec.Containers[0].EnvFrom {
		if envFrom.SecretRef != nil && envFrom.SecretRef.LocalObjectReference.Name == cmName {
			return
		}
	}
	envFromSource := corev1.EnvFromSource{
		SecretRef: &corev1.SecretEnvSource{
			LocalObjectReference: corev1.LocalObjectReference{
				Name: cmName,
			},
		},
	}
	deployment.Spec.Template.Spec.Containers[0].EnvFrom = append(deployment.Spec.Template.Spec.Containers[0].EnvFrom, envFromSource)
}

func (c *secretHandler) GetComparator() compare.MapComparator {
	resourceComparator := compare.DefaultComparator()
	return compare.MapComparator{Comparator: resourceComparator}
}
