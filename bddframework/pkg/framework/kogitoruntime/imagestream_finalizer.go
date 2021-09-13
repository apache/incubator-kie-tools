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
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/framework/util"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"k8s.io/apimachinery/pkg/types"
)

const imageStreamFinalizer = "delete.imageStream.ownership.finalizer"

// ImageStreamFinalizerHandler ...
type ImageStreamFinalizerHandler interface {
	AddFinalizer(instance api.KogitoService) error
	HandleFinalization(instance api.KogitoService) error
}

type imageStreamFinalizerHandler struct {
	operator.Context
}

// NewImageStreamFinalizerHandler ...
func NewImageStreamFinalizerHandler(context operator.Context) ImageStreamFinalizerHandler {
	return &imageStreamFinalizerHandler{
		Context: context,
	}
}

// AddFinalizer add finalizer to provide KogitoService instance
func (f *imageStreamFinalizerHandler) AddFinalizer(instance api.KogitoService) error {
	if instance.GetDeletionTimestamp().IsZero() {
		if !util.Contains(imageStreamFinalizer, instance.GetFinalizers()) {
			f.Log.Debug("Adding imageStream Finalizer for the KogitoRuntime")
			finalizers := append(instance.GetFinalizers(), imageStreamFinalizer)
			instance.SetFinalizers(finalizers)

			// Update CR
			if err := kubernetes.ResourceC(f.Client).Update(instance); err != nil {
				f.Log.Error(err, "Failed to update imageStream finalizer in KogitoRuntime")
				return err
			}
			f.Log.Debug("Successfully added imageStream finalizer into KogitoRuntime instance", "instance", instance.GetName())
		}
	}
	return nil
}

// HandleFinalization remove owner reference of provided Kogito service from KogitoInfra instances and remove finalizer from KogitoService
func (f *imageStreamFinalizerHandler) HandleFinalization(instance api.KogitoService) error {
	// Remove KogitoRuntime ownership from shared ImageStream
	imageStreamHandler := infrastructure.NewImageStreamHandler(f.Context)
	imageStreamName := f.resolveImageStreamName(instance)
	if err := imageStreamHandler.RemoveSharedImageStreamOwnerShip(types.NamespacedName{Name: imageStreamName, Namespace: instance.GetNamespace()}, instance); err != nil {
		return err
	}

	// Update finalizer to allow delete CR
	f.Log.Debug("Removing imageStream finalizer from KogitoRuntime")
	finalizers := instance.GetFinalizers()
	removed := util.Remove(imageStreamFinalizer, &finalizers)
	instance.SetFinalizers(finalizers)
	if removed {
		if err := kubernetes.ResourceC(f.Client).Update(instance); err != nil {
			f.Log.Error(err, "Error occurs while removing imageStream finalizer from KogitoRuntime")
			return err
		}
	}
	f.Log.Debug("Successfully removed imageStream finalizer from KogitoRuntime")
	return nil
}

func (f *imageStreamFinalizerHandler) resolveImageStreamName(instance api.KogitoService) string {
	var image api.Image
	if len(instance.GetSpec().GetImage()) == 0 {
		return instance.GetName()
	}
	image = framework.ConvertImageTagToImage(instance.GetSpec().GetImage())
	return image.Name
}
