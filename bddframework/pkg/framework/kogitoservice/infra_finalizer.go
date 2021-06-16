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
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	"k8s.io/apimachinery/pkg/types"
)

const infraFinalizer = "delete.kogitoInfra.ownership.finalizer"

// InfraFinalizerHandler ...
type InfraFinalizerHandler interface {
	AddFinalizer(instance api.KogitoService) error
	HandleFinalization(instance api.KogitoService) error
}

type infraFinalizerHandler struct {
	operator.Context
	infraHandler manager.KogitoInfraHandler
}

// NewFinalizerHandler ...
func NewFinalizerHandler(context operator.Context, infraHandler manager.KogitoInfraHandler) InfraFinalizerHandler {
	return &infraFinalizerHandler{
		Context:      context,
		infraHandler: infraHandler,
	}
}

// AddFinalizer add finalizer to provide KogitoService instance
func (f *infraFinalizerHandler) AddFinalizer(instance api.KogitoService) error {
	if instance.GetDeletionTimestamp().IsZero() {
		if !util.Contains(infraFinalizer, instance.GetFinalizers()) {
			f.Log.Debug("Adding Infra Finalizer for the KogitoService")
			finalizers := append(instance.GetFinalizers(), infraFinalizer)
			instance.SetFinalizers(finalizers)

			// Update CR
			if err := kubernetes.ResourceC(f.Client).Update(instance); err != nil {
				f.Log.Error(err, "Failed to update infra finalizer in KogitoService")
				return err
			}
			f.Log.Debug("Successfully added infra finalizer into KogitoService instance", "instance", instance.GetName())
		}
	}
	return nil
}

// HandleFinalization remove owner reference of provided Kogito service from KogitoInfra instances and remove finalizer from KogitoService
func (f *infraFinalizerHandler) HandleFinalization(instance api.KogitoService) error {
	// Remove KogitoSupportingService ownership from referred KogitoInfra instances
	infraManager := manager.NewKogitoInfraManager(f.Context, f.infraHandler)
	for _, kogitoInfra := range instance.GetSpec().GetInfra() {
		if err := infraManager.RemoveKogitoInfraOwnership(types.NamespacedName{Name: kogitoInfra, Namespace: instance.GetNamespace()}, instance); err != nil {
			return err
		}
	}

	// Update finalizer to allow delete CR
	f.Log.Debug("Removing infra finalizer from KogitoService")
	finalizers := instance.GetFinalizers()
	removed := util.Remove(infraFinalizer, &finalizers)
	instance.SetFinalizers(finalizers)
	if removed {
		if err := kubernetes.ResourceC(f.Client).Update(instance); err != nil {
			f.Log.Error(err, "Error occurs while removing infra finalizer from KogitoService")
			return err
		}
	}
	f.Log.Debug("Successfully removed infra finalizer from KogitoService")
	return nil
}
