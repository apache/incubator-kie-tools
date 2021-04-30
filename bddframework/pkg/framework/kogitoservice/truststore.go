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
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	trustStoreMountPath          = operator.KogitoHomeDir + "/certs/custom-truststore"
	trustStoreSecretPasswordKey  = "keyStorePassword"
	trustStoreSecretFileKey      = "cacerts"
	trustStoreEnvVarPassword     = "CUSTOM_TRUSTSTORE_PASSWORD"
	trustStoreEnvVarCertFileName = "CUSTOM_TRUSTSTORE"
	trustStoreVolumeName         = "custom-truststore"
)

// TrustStoreHandler takes care of mounting the custom TrustStoreSecret in a given Deployment based on api.KogitoService spec
type TrustStoreHandler interface {
	MountTrustStore(deployment *appsv1.Deployment, service api.KogitoService) error
}

// NewTrustStoreHandler creates a new TrustStoreHandler with the given context
func NewTrustStoreHandler(context operator.Context) TrustStoreHandler {
	return &trustStoreHandler{
		context: context,
	}
}

type trustStoreHandler struct {
	context operator.Context
}

// MountTrustStore mounts the given custom TrustStoreSecret based on api.KogitoService
func (t *trustStoreHandler) MountTrustStore(deployment *appsv1.Deployment, service api.KogitoService) error {
	if len(service.GetSpec().GetTrustStoreSecret()) == 0 {
		return nil
	}

	secret, err := t.fetchAndValidateTrustStoreSecret(service)
	if err != nil {
		return err
	}

	t.mapTrustStorePassword(deployment, secret)
	if err := t.mountTrustStoreFile(deployment, secret); err != nil {
		return err
	}

	return nil
}

func (t *trustStoreHandler) fetchAndValidateTrustStoreSecret(service api.KogitoService) (*v1.Secret, error) {
	secret := &v1.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      service.GetSpec().GetTrustStoreSecret(),
			Namespace: service.GetNamespace(),
		},
	}
	if exists, err := kubernetes.ResourceC(t.context.Client).Fetch(secret); err != nil {
		return nil, err
	} else if !exists {
		return nil, errorForTrustStoreMount("Failed to find Secret named " + secret.Name + " in the namespace " + secret.Namespace)
	}

	if _, ok := secret.Data[trustStoreSecretFileKey]; !ok {
		return nil, errorForTrustStoreMount("Failed to mount Truststore. Secret " + secret.Name + " doesn't have the file with key " + trustStoreSecretFileKey)
	}

	return secret, nil
}

func (t *trustStoreHandler) mountTrustStoreFile(deployment *appsv1.Deployment, secret *v1.Secret) error {
	trustStoreVolume := v1.Volume{
		Name: trustStoreVolumeName,
		VolumeSource: v1.VolumeSource{
			Secret: &v1.SecretVolumeSource{
				SecretName:  secret.Name,
				Items:       []v1.KeyToPath{{Key: trustStoreSecretFileKey, Path: trustStoreSecretFileKey}},
				DefaultMode: &framework.ModeForCertificates,
			},
		},
	}
	trustStoreMount := v1.VolumeMount{
		Name:      trustStoreVolumeName,
		MountPath: trustStoreMountPath + "/" + trustStoreSecretFileKey,
		SubPath:   trustStoreSecretFileKey,
	}

	framework.AddVolumeToDeployment(deployment, trustStoreMount, trustStoreVolume)
	deployment.Spec.Template.Spec.Containers[0].Env = framework.EnvOverride(deployment.Spec.Template.Spec.Containers[0].Env, v1.EnvVar{
		Name:  trustStoreEnvVarCertFileName,
		Value: trustStoreSecretFileKey,
	})

	return nil
}

func (t *trustStoreHandler) mapTrustStorePassword(deployment *appsv1.Deployment, secret *v1.Secret) {
	if _, ok := secret.Data[trustStoreSecretPasswordKey]; !ok {
		return
	}

	deployment.Spec.Template.Spec.Containers[0].Env = framework.EnvOverride(deployment.Spec.Template.Spec.Containers[0].Env,
		framework.CreateSecretEnvVar(trustStoreEnvVarPassword, secret.Name, trustStoreSecretPasswordKey))
}
