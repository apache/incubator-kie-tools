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

package kogitoinfra

import (
	v1 "github.com/infinispan/infinispan-operator/pkg/apis/infinispan/v1"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	v12 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"software.sslmate.com/src/go-pkcs12"
)

const (
	truststoreSecretName   = "kogito-infinispan-truststore"
	certMountPath          = operator.KogitoHomeDir + "/certs/infinispan"
	truststoreSecretKey    = "truststore.p12"
	infinispanTLSSecretKey = "tls.crt"
	truststoreMountPath    = certMountPath + "/" + truststoreSecretKey
)

type infinispanTrustStoreReconciler struct {
	infraContext
	infinispanInstance *v1.Infinispan
	secretHandler      infrastructure.SecretHandler
}

func newInfinispanTrustStoreReconciler(context infraContext, infinispanInstance *v1.Infinispan) Reconciler {
	return &infinispanTrustStoreReconciler{
		infraContext:       context,
		infinispanInstance: infinispanInstance,
		secretHandler:      infrastructure.NewSecretHandler(context.Context),
	}
}

func (i *infinispanTrustStoreReconciler) Reconcile() error {

	if !i.isInfinispanCertEncryptionEnabled(i.infinispanInstance) {
		return nil
	}
	if err := i.addTrustStoreVolumeSource(); err != nil {
		return err
	}
	if err := i.addTrustStoreSecret(); err != nil {
		return err
	}
	return nil
}

func (i *infinispanTrustStoreReconciler) isInfinispanCertEncryptionEnabled(infinispanInstance *v1.Infinispan) bool {
	return *infinispanInstance.Spec.Security.EndpointAuthentication && infinispanInstance.Spec.Security.EndpointEncryption != nil && len(infinispanInstance.Spec.Security.EndpointEncryption.CertSecretName) > 0
}

func (i *infinispanTrustStoreReconciler) addTrustStoreVolumeSource() error {
	// Get Deployed resource
	secretExists, err := i.isTrustStoreSecretExists()
	if err != nil {
		return err
	}
	if !secretExists {
		// Create Required resource
		err = i.createTrustStoreSecret()
		if err != nil {
			return err
		}
	}

	i.instance.GetStatus().AddSecretVolumeReference(truststoreSecretName, certMountPath, &framework.ModeForCertificates, nil)
	return nil
}

func (i *infinispanTrustStoreReconciler) isTrustStoreSecretExists() (bool, error) {
	// fetch truststore secret
	deployedSecret, err := i.secretHandler.FetchSecret(types.NamespacedName{Name: truststoreSecretName, Namespace: i.instance.GetNamespace()})
	if err != nil {
		return false, err
	}
	if deployedSecret == nil {
		return false, nil
	}
	return true, nil
}

func (i *infinispanTrustStoreReconciler) createTrustStoreSecret() error {

	ispnSecret, err := i.secretHandler.MustFetchSecret(types.NamespacedName{Name: i.infinispanInstance.Spec.Security.EndpointEncryption.CertSecretName, Namespace: i.infinispanInstance.GetNamespace()})
	if err != nil {
		return err
	}
	trustStore, err := framework.CreatePKCS12TrustStoreFromSecret(ispnSecret, pkcs12.DefaultPassword, infinispanTLSSecretKey)
	if err != nil {
		return err
	}

	kogitoInfraEncryptionSecret := &v12.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      truststoreSecretName,
			Namespace: i.instance.GetNamespace(),
		},
		Type: v12.SecretTypeOpaque,
		Data: map[string][]byte{
			truststoreSecretKey: trustStore,
		},
	}
	if err := framework.SetOwner(i.instance, i.Scheme, kogitoInfraEncryptionSecret); err != nil {
		return err
	}
	if err = kubernetes.ResourceC(i.Client).Create(kogitoInfraEncryptionSecret); err != nil {
		return err
	}
	return nil
}

func (i *infinispanTrustStoreReconciler) addTrustStoreSecret() error {
	if err := newInfinispanTrustStoreSecretReconciler(i.infraContext, api.QuarkusRuntimeType).Reconcile(); err != nil {
		return err
	}
	if err := newInfinispanTrustStoreSecretReconciler(i.infraContext, api.SpringBootRuntimeType).Reconcile(); err != nil {
		return err
	}
	return nil
}
