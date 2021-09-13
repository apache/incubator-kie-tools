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

package kogitoinfra

import (
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/builder"
)

type configMapReferenceReconciler struct {
	infraContext
	configMapHandler infrastructure.ConfigMapHandler
}

func initConfigMapReferenceReconciler(context infraContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "ConfigMapReference")
	return &configMapReferenceReconciler{
		infraContext:     context,
		configMapHandler: infrastructure.NewConfigMapHandler(context.Context),
	}
}

// AppendConfigMapWatchedObjects ...
func AppendConfigMapWatchedObjects(b *builder.Builder) *builder.Builder {
	return b.Owns(&corev1.ConfigMap{})
}

// Reconcile reconcile Kogito infra object
func (i *configMapReferenceReconciler) Reconcile() error {

	// reconcileConfigMapEnvFromReferences
	if err := i.reconcileConfigMapEnvFromReferences(); err != nil {
		return err
	}

	// reconcileConfigMapVolumeReferences
	if err := i.reconcileConfigMapVolumeReferences(); err != nil {
		return err
	}

	return nil
}

func (i *configMapReferenceReconciler) reconcileConfigMapEnvFromReferences() error {
	for _, cmName := range i.instance.GetSpec().GetConfigMapEnvFromReferences() {
		namespace := i.instance.GetNamespace()
		configMapInstance, resultErr := i.configMapHandler.FetchConfigMap(types.NamespacedName{Name: cmName, Namespace: namespace})
		if resultErr != nil {
			return resultErr
		}
		if configMapInstance == nil {
			return errorForResourceNotFound("Configmap", cmName, namespace)
		}
		i.instance.GetStatus().AddConfigMapEnvFromReferences(cmName)
	}
	return nil
}

func (i *configMapReferenceReconciler) reconcileConfigMapVolumeReferences() error {
	for _, volumeReference := range i.instance.GetSpec().GetConfigMapVolumeReferences() {
		if len(volumeReference.GetName()) > 0 {
			i.Log.Debug("Custom Configmap instance reference is provided")
			namespace := i.instance.GetNamespace()
			configMapInstance, resultErr := i.configMapHandler.FetchConfigMap(types.NamespacedName{Name: volumeReference.GetName(), Namespace: namespace})
			if resultErr != nil {
				return resultErr
			}
			if configMapInstance == nil {
				return errorForResourceNotFound("Configmap", volumeReference.GetName(), namespace)
			}
		} else {
			return errorForResourceConfigError(i.instance, "No Configmap resource name given")
		}

		i.updateConfigMapVolumeReferenceInStatus(volumeReference)
	}
	return nil
}

func (i *configMapReferenceReconciler) updateConfigMapVolumeReferenceInStatus(volumeReference api.VolumeReferenceInterface) {
	configMapVolumeReferences := append(i.instance.GetStatus().GetConfigMapVolumeReferences(), volumeReference)
	i.instance.GetStatus().SetConfigMapVolumeReferences(configMapVolumeReferences)
}
