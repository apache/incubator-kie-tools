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
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework/util"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"k8s.io/apimachinery/pkg/types"
)

const configMapFinalizer = "delete.configmap.ownership.finalizer"

// ConfigMapFinalizerHandler ...
type ConfigMapFinalizerHandler interface {
	AddFinalizer(instance api.KogitoService) error
	HandleFinalization(instance api.KogitoService) error
}

type configMapFinalizerHandler struct {
	operator.Context
}

// NewConfigMapFinalizerHandler ...
func NewConfigMapFinalizerHandler(context operator.Context) InfraFinalizerHandler {
	return &configMapFinalizerHandler{
		Context: context,
	}
}

// AddFinalizer add finalizer to provide KogitoService instance
func (f *configMapFinalizerHandler) AddFinalizer(instance api.KogitoService) error {
	if instance.GetDeletionTimestamp().IsZero() {
		if !util.Contains(configMapFinalizer, instance.GetFinalizers()) {
			f.Log.Debug("Adding Configmap Finalizer for the KogitoService")
			finalizers := append(instance.GetFinalizers(), configMapFinalizer)
			instance.SetFinalizers(finalizers)

			// Update CR
			if err := kubernetes.ResourceC(f.Client).Update(instance); err != nil {
				f.Log.Error(err, "Failed to update configmap finalizer in KogitoService")
				return err
			}
			f.Log.Debug("Successfully added configmap finalizer into KogitoService instance", "instance", instance.GetName())
		}
	}
	return nil
}

// HandleFinalization remove owner reference of provided Kogito service from KogitoInfra instances and remove finalizer from KogitoService
func (f *configMapFinalizerHandler) HandleFinalization(instance api.KogitoService) error {
	// Remove KogitoService ownership from user provided configmap
	propertiesConfigMap := instance.GetSpec().GetPropertiesConfigMap()
	if len(propertiesConfigMap) > 0 {
		configMapHandler := infrastructure.NewConfigMapHandler(f.Context)
		if err := configMapHandler.RemoveConfigMapOwnership(types.NamespacedName{Name: propertiesConfigMap, Namespace: instance.GetNamespace()}, instance); err != nil {
			return err
		}
	}
	// Update finalizer to allow delete CR
	f.Log.Debug("Removing configMap finalizer from KogitoService")
	finalizers := instance.GetFinalizers()
	removed := util.Remove(configMapFinalizer, &finalizers)
	instance.SetFinalizers(finalizers)
	if removed {
		if err := kubernetes.ResourceC(f.Client).Update(instance); err != nil {
			f.Log.Error(err, "Error occurs while removing configMap finalizer from KogitoService")
			return err
		}
	}
	f.Log.Debug("Successfully removed configMap finalizer from KogitoService")
	return nil
}
