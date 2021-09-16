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
	"fmt"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	v12 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"software.sslmate.com/src/go-pkcs12"
)

const (
	infinispanTrustStoreSecretName = "kogito-infinispan-truststore-%s-secret"
	pkcs12CertType                 = "PKCS12"
)

type infinispanTrustStoreSecretReconciler struct {
	infraContext
	runtime       api.RuntimeType
	secretHandler infrastructure.SecretHandler
}

func newInfinispanTrustStoreSecretReconciler(context infraContext, runtime api.RuntimeType) Reconciler {
	return &infinispanTrustStoreSecretReconciler{
		infraContext:  context,
		runtime:       runtime,
		secretHandler: infrastructure.NewSecretHandler(context.Context),
	}
}

func (i *infinispanTrustStoreSecretReconciler) Reconcile() (err error) {

	// Create Required resource
	requestedResources, err := i.createRequiredResources()
	if err != nil {
		return
	}

	// Get Deployed resource
	deployedResources, err := i.getDeployedResources()
	if err != nil {
		return
	}

	// Process Delta
	if err = i.processDelta(requestedResources, deployedResources); err != nil {
		return err
	}

	i.instance.GetStatus().AddSecretEnvFromReferences(i.getInfinispanTrustStoreSecretName())
	return nil
}

func (i *infinispanTrustStoreSecretReconciler) createRequiredResources() (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	appProps := i.getInfinispanTrustStoreSecretProps()
	secret := i.createInfinispanTrustStoreSecret(appProps)
	if err := framework.SetOwner(i.instance, i.Scheme, secret); err != nil {
		return resources, err
	}
	resources[reflect.TypeOf(v12.Secret{})] = []client.Object{secret}
	return resources, nil
}

func (i *infinispanTrustStoreSecretReconciler) getDeployedResources() (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	deployedSecret, err := i.secretHandler.FetchSecret(types.NamespacedName{Name: i.getInfinispanTrustStoreSecretName(), Namespace: i.instance.GetNamespace()})
	if err != nil {
		return nil, err
	}
	if deployedSecret != nil {
		resources[reflect.TypeOf(v12.Secret{})] = []client.Object{deployedSecret}
	}
	return resources, nil
}

func (i *infinispanTrustStoreSecretReconciler) processDelta(requestedResources map[reflect.Type][]client.Object, deployedResources map[reflect.Type][]client.Object) (err error) {
	comparator := i.secretHandler.GetComparator()
	deltaProcessor := infrastructure.NewDeltaProcessor(i.Context)
	_, err = deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	return err
}

func (i *infinispanTrustStoreSecretReconciler) getInfinispanTrustStoreSecretProps() map[string][]byte {
	appProps := map[string][]byte{}
	appProps[propertiesInfinispan[i.runtime][appPropInfinispanTrustStoreType]] = []byte(pkcs12CertType)
	appProps[propertiesInfinispan[i.runtime][appPropInfinispanTrustStore]] = []byte(truststoreMountPath)
	appProps[propertiesInfinispan[i.runtime][appPropInfinispanTrustStorePassword]] = []byte(pkcs12.DefaultPassword)
	return appProps
}

func (i *infinispanTrustStoreSecretReconciler) createInfinispanTrustStoreSecret(appProps map[string][]byte) *v12.Secret {
	secret := &v12.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      i.getInfinispanTrustStoreSecretName(),
			Namespace: i.instance.GetNamespace(),
			Labels: map[string]string{
				framework.LabelAppKey: i.instance.GetName(),
			},
		},
		Data: appProps,
	}
	return secret
}

func (i *infinispanTrustStoreSecretReconciler) getInfinispanTrustStoreSecretName() string {
	return fmt.Sprintf(infinispanTrustStoreSecretName, i.runtime)
}
