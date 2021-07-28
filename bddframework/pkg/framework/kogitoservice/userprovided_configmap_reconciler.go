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
	"fmt"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	appPropFilePath = operator.KogitoHomeDir + "/config"
)

// UserProvidedConfigMapReconciler ...
type UserProvidedConfigMapReconciler interface {
	Reconcile() error
}

type userProvidedConfigConfigMapReconciler struct {
	operator.Context
	instance api.KogitoService
}

// NewUserProvidedConfigConfigMapReconciler ...
func NewUserProvidedConfigConfigMapReconciler(context operator.Context, instance api.KogitoService) UserProvidedConfigMapReconciler {
	return &userProvidedConfigConfigMapReconciler{
		Context:  context,
		instance: instance,
	}
}

func (c *userProvidedConfigConfigMapReconciler) Reconcile() (err error) {
	if err = c.reconcileUserProvidedConfigMap(); err != nil {
		return
	}
	return
}

func (c *userProvidedConfigConfigMapReconciler) reconcileUserProvidedConfigMap() error {
	customConfigMapName := c.instance.GetSpec().GetPropertiesConfigMap()
	if len(customConfigMapName) > 0 {
		c.Log.Debug("custom app properties are provided in custom properties ConfigMap", "PropertiesConfigMap", customConfigMapName)
		propertiesConfigMap := &corev1.ConfigMap{
			ObjectMeta: metav1.ObjectMeta{
				Namespace: c.instance.GetNamespace(),
				Name:      customConfigMapName,
			},
		}
		if exists, err := kubernetes.ResourceC(c.Client).Fetch(propertiesConfigMap); err != nil {
			return err
		} else if !exists {
			return fmt.Errorf("propertiesConfigMap %s not found", customConfigMapName)
		}

		updateRequire := appendVolumeMountAnnotations(propertiesConfigMap)

		if c.appendAppKeyLabels(propertiesConfigMap) {
			updateRequire = true
		}

		if !framework.IsOwner(propertiesConfigMap, c.instance) {
			if err := framework.AddOwnerReference(c.instance, c.Scheme, propertiesConfigMap); err != nil {
				return err
			}
			updateRequire = true
		}

		if updateRequire {
			if err := kubernetes.ResourceC(c.Client).Update(propertiesConfigMap); err != nil {
				return err
			}
		}
	}
	return nil
}

func (c *userProvidedConfigConfigMapReconciler) appendAppKeyLabels(configMap *corev1.ConfigMap) bool {
	updateRequire := false
	labels := configMap.Labels
	if labels == nil {
		labels = map[string]string{}
	}
	if labels[framework.LabelAppKey] != c.instance.GetName() {
		labels[framework.LabelAppKey] = c.instance.GetName()
		updateRequire = true
	}
	configMap.Labels = labels
	return updateRequire
}

func appendVolumeMountAnnotations(configMap *corev1.ConfigMap) bool {
	updateRequire := false
	annotations := configMap.Annotations
	if annotations == nil {
		annotations = map[string]string{}
	}

	if annotations[infrastructure.FromFileKey] != "true" {
		annotations[infrastructure.FromFileKey] = "true"
		updateRequire = true
	}

	if annotations[infrastructure.MountPathKey] != appPropFilePath {
		annotations[infrastructure.MountPathKey] = appPropFilePath
		updateRequire = true
	}

	fileMode := fmt.Sprint(framework.ModeForPropertyFiles)
	if annotations[infrastructure.FileModeKey] != fileMode {
		annotations[infrastructure.FileModeKey] = fileMode
		updateRequire = true
	}
	configMap.Annotations = annotations
	return updateRequire
}
