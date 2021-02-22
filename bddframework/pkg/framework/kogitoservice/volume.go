// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package kogitoservice

import (
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/core/framework"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	corev1 "k8s.io/api/core/v1"
)

const (
	// AppPropVolumeName is the name of the volume for application.properties
	AppPropVolumeName = "app-prop-config"
	appPropFilePath   = operator.KogitoHomeDir + "/config"
)

// AppPropsVolumeHandler ...
type AppPropsVolumeHandler interface {
	CreateAppPropVolume(service api.KogitoService) corev1.Volume
	CreateAppPropVolumeMount() corev1.VolumeMount
}

type appPropsVolumeHandler struct {
}

// NewAppPropsVolumeHandler ...
func NewAppPropsVolumeHandler() AppPropsVolumeHandler {
	return &appPropsVolumeHandler{}
}

// CreateAppPropVolume creates a volume for application.properties
func (a *appPropsVolumeHandler) CreateAppPropVolume(service api.KogitoService) corev1.Volume {
	return corev1.Volume{
		Name: AppPropVolumeName,
		VolumeSource: corev1.VolumeSource{
			ConfigMap: &corev1.ConfigMapVolumeSource{
				LocalObjectReference: corev1.LocalObjectReference{
					Name: getAppPropConfigMapName(service),
				},
				Items: []corev1.KeyToPath{
					{
						Key:  ConfigMapApplicationPropertyKey,
						Path: ConfigMapApplicationPropertyKey,
					},
				},
				DefaultMode: &framework.ModeForPropertyFiles,
			},
		},
	}
}

// CreateAppPropVolumeMount creates a container volume mount for mounting application.properties
func (a *appPropsVolumeHandler) CreateAppPropVolumeMount() corev1.VolumeMount {
	return corev1.VolumeMount{
		Name:      AppPropVolumeName,
		MountPath: appPropFilePath,
		ReadOnly:  true,
	}
}
