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

type secretReferenceReconciler struct {
	infraContext
	secretHandler infrastructure.SecretHandler
}

func initSecretReferenceReconciler(context infraContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "SecretReference")
	return &secretReferenceReconciler{
		infraContext:  context,
		secretHandler: infrastructure.NewSecretHandler(context.Context),
	}
}

// AppendSecretWatchedObjects ...
func AppendSecretWatchedObjects(b *builder.Builder) *builder.Builder {
	return b.Owns(&corev1.Secret{})
}

// Reconcile reconcile Kogito infra object
func (s *secretReferenceReconciler) Reconcile() error {

	// reconcileSecretEnvFromReferences
	if err := s.reconcileSecretEnvFromReferences(); err != nil {
		return err
	}

	// reconcileSecretVolumeReferences
	return s.reconcileSecretVolumeReferences()
}

func (s *secretReferenceReconciler) reconcileSecretEnvFromReferences() error {
	for _, cmName := range s.instance.GetSpec().GetSecretEnvFromReferences() {
		namespace := s.instance.GetNamespace()
		secretInstance, resultErr := s.secretHandler.FetchSecret(types.NamespacedName{Name: cmName, Namespace: namespace})
		if resultErr != nil {
			return resultErr
		}
		if secretInstance == nil {
			return errorForResourceNotFound("Secret", cmName, namespace)
		}

		s.updateSecretEnvFromReferenceInStatus(cmName)
	}
	return nil
}

func (s *secretReferenceReconciler) reconcileSecretVolumeReferences() error {
	for _, volumeReference := range s.instance.GetSpec().GetSecretVolumeReferences() {
		if len(volumeReference.GetName()) > 0 {
			s.Log.Debug("Custom Secret instance reference is provided")
			namespace := s.instance.GetNamespace()
			secretInstance, resultErr := s.secretHandler.FetchSecret(types.NamespacedName{Name: volumeReference.GetName(), Namespace: namespace})
			if resultErr != nil {
				return resultErr
			}
			if secretInstance == nil {
				return errorForResourceNotFound("Secret", volumeReference.GetName(), namespace)
			}
		} else {
			return errorForResourceConfigError(s.instance, "No Secret resource name given")
		}

		s.updateSecretVolumeReferenceInStatus(volumeReference)
	}
	return nil
}

func (s *secretReferenceReconciler) updateSecretEnvFromReferenceInStatus(cmName string) {
	secretReferences := append(s.instance.GetStatus().GetSecretEnvFromReferences(), cmName)
	s.instance.GetStatus().SetSecretEnvFromReferences(secretReferences)
}

func (s *secretReferenceReconciler) updateSecretVolumeReferenceInStatus(volumeReference api.VolumeReferenceInterface) {
	secretVolumeReferences := append(s.instance.GetStatus().GetSecretVolumeReferences(), volumeReference)
	s.instance.GetStatus().SetSecretVolumeReferences(secretVolumeReferences)
}
