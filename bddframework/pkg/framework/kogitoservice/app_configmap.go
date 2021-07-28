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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	appPropConfigMapSuffix = "-properties"
)

// AppConfigMapHandler ...
type AppConfigMapHandler interface {
	GetAppConfigMapName(service api.KogitoService) string
	CreateAppConfigMap(service api.KogitoService, appProps map[string]string) *corev1.ConfigMap
}

type appConfigMapHandler struct {
	operator.Context
}

// NewAppConfigMapHandler ...
func NewAppConfigMapHandler(context operator.Context) AppConfigMapHandler {
	return &appConfigMapHandler{
		context,
	}
}

func (a *appConfigMapHandler) CreateAppConfigMap(service api.KogitoService, appProps map[string]string) *corev1.ConfigMap {
	var data map[string]string = nil
	if len(appProps) > 0 {
		data = appProps
	}
	configMapName := a.GetAppConfigMapName(service)
	configMap := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      configMapName,
			Namespace: service.GetNamespace(),
			Labels: map[string]string{
				framework.LabelAppKey: service.GetName(),
			},
			Annotations: map[string]string{
				infrastructure.FromLiteralKey: "true",
			},
		},
		Data: data,
	}
	return configMap
}

func (a *appConfigMapHandler) GetAppConfigMapName(service api.KogitoService) string {
	return service.GetName() + appPropConfigMapSuffix
}
