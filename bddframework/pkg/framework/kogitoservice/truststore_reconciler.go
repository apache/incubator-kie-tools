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
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
)

const (
	trustStoreMountPath          = operator.KogitoHomeDir + "/certs/custom-truststore"
	trustStoreSecretPasswordKey  = "keyStorePassword"
	trustStoreSecretFileKey      = "cacerts"
	trustStoreEnvVarPassword     = "CUSTOM_TRUSTSTORE_PASSWORD"
	trustStoreEnvVarCertFileName = "CUSTOM_TRUSTSTORE"
)

// TrustStoreReconciler takes care of mounting the custom TrustStoreSecret in a given Deployment based on api.KogitoService spec
type TrustStoreReconciler interface {
	Reconcile() error
}

type trustStoreReconciler struct {
	operator.Context
	instance          api.KogitoService
	serviceDefinition *ServiceDefinition
	secretHandler     infrastructure.SecretHandler
}

func newTrustStoreReconciler(context operator.Context, instance api.KogitoService, serviceDefinition *ServiceDefinition) TrustStoreReconciler {
	context.Log = context.Log.WithValues("resource", "InfraProperties")
	return &trustStoreReconciler{
		Context:           context,
		instance:          instance,
		serviceDefinition: serviceDefinition,
		secretHandler:     infrastructure.NewSecretHandler(context),
	}
}

// MountTrustStore mounts the given custom TrustStoreSecret based on api.KogitoService
func (t *trustStoreReconciler) Reconcile() error {
	if len(t.instance.GetSpec().GetTrustStoreSecret()) == 0 {
		return nil
	}

	secret, err := t.fetchAndValidateTrustStoreSecret()
	if err != nil {
		return err
	}

	t.mapTrustStorePassword(secret)
	if err := t.mountTrustStoreFile(secret); err != nil {
		return err
	}

	return nil
}

func (t *trustStoreReconciler) fetchAndValidateTrustStoreSecret() (*v1.Secret, error) {
	secret, err := t.secretHandler.FetchSecret(types.NamespacedName{Name: t.instance.GetSpec().GetTrustStoreSecret(), Namespace: t.instance.GetNamespace()})
	if err != nil {
		return nil, err
	} else if secret == nil {
		return nil, infrastructure.ErrorForTrustStoreMount("Failed to find Trust store secret named " + t.instance.GetSpec().GetTrustStoreSecret() + " in the namespace " + t.instance.GetNamespace())
	}
	return secret, nil
}

func (t *trustStoreReconciler) mountTrustStoreFile(secret *v1.Secret) error {
	if _, ok := secret.Data[trustStoreSecretFileKey]; !ok {
		return infrastructure.ErrorForTrustStoreMount("Failed to mount Truststore. Secret " + secret.Name + " doesn't have the file with key " + trustStoreSecretFileKey)
	}

	secretVolumeReference := &VolumeReference{
		Name:      secret.Name,
		MountPath: trustStoreMountPath + "/" + trustStoreSecretFileKey,
		FileMode:  &framework.ModeForCertificates,
	}
	t.serviceDefinition.SecretVolumeReferences = append(t.serviceDefinition.SecretVolumeReferences, secretVolumeReference)
	t.serviceDefinition.Envs = framework.EnvOverride(t.serviceDefinition.Envs, v1.EnvVar{
		Name:  trustStoreEnvVarCertFileName,
		Value: trustStoreSecretFileKey,
	})

	return nil
}

func (t *trustStoreReconciler) mapTrustStorePassword(secret *v1.Secret) {
	if _, ok := secret.Data[trustStoreSecretPasswordKey]; !ok {
		return
	}
	t.serviceDefinition.Envs = framework.EnvOverride(t.serviceDefinition.Envs,
		framework.CreateSecretEnvVar(trustStoreEnvVarPassword, secret.Name, trustStoreSecretPasswordKey))
}
