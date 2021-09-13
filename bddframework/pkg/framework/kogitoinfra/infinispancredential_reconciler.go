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
	"github.com/RHsyseng/operator-utils/pkg/resource"
	v1 "github.com/infinispan/infinispan-operator/pkg/apis/infinispan/v1"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	v12 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
)

const (
	credentialSecretName = "kogito-infinispan-%s-credential"
)

type infinispanCredentialReconciler struct {
	infraContext
	infinispanInstance *v1.Infinispan
	runtime            api.RuntimeType
	secretHandler      infrastructure.SecretHandler
}

func newInfinispanCredentialReconciler(context infraContext, infinispanInstance *v1.Infinispan, runtime api.RuntimeType) Reconciler {
	return &infinispanCredentialReconciler{
		infraContext:       context,
		infinispanInstance: infinispanInstance,
		runtime:            runtime,
		secretHandler:      infrastructure.NewSecretHandler(context.Context),
	}
}

func (i *infinispanCredentialReconciler) Reconcile() (err error) {

	if !isInfinispanSecretEncryptionEnabled(i.infinispanInstance) {
		return nil
	}

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

	i.instance.GetStatus().AddSecretEnvFromReferences(i.getCredentialSecretName())
	return nil
}

func (i *infinispanCredentialReconciler) createRequiredResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	infinispanHandler := infrastructure.NewInfinispanHandler(i.Context)
	credentials, err := infinispanHandler.GetInfinispanCredential(i.infinispanInstance)
	if err != nil {
		return nil, err
	}
	secret := i.createInfinispanSecret(credentials)
	if err := framework.SetOwner(i.instance, i.Scheme, secret); err != nil {
		return resources, err
	}
	resources[reflect.TypeOf(v12.Secret{})] = []resource.KubernetesResource{secret}
	return resources, nil
}

func (i *infinispanCredentialReconciler) getDeployedResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	// fetch owned image stream
	deployedSecret, err := i.secretHandler.FetchSecret(types.NamespacedName{Name: i.getCredentialSecretName(), Namespace: i.instance.GetNamespace()})
	if err != nil {
		return nil, err
	}
	if deployedSecret != nil {
		resources[reflect.TypeOf(v12.Secret{})] = []resource.KubernetesResource{deployedSecret}
	}
	return resources, nil
}

func (i *infinispanCredentialReconciler) processDelta(requestedResources map[reflect.Type][]resource.KubernetesResource, deployedResources map[reflect.Type][]resource.KubernetesResource) (err error) {
	comparator := i.secretHandler.GetComparator()
	deltaProcessor := infrastructure.NewDeltaProcessor(i.Context)
	_, err = deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	return err
}

func (i *infinispanCredentialReconciler) createInfinispanSecret(credentials *infrastructure.InfinispanCredential) *v12.Secret {
	secret := &v12.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      i.getCredentialSecretName(),
			Namespace: i.instance.GetNamespace(),
		},
		Type: v12.SecretTypeOpaque,
	}
	if credentials != nil {
		secret.StringData = map[string]string{
			propertiesInfinispan[i.runtime][envVarInfinispanUser]:     credentials.Username,
			propertiesInfinispan[i.runtime][envVarInfinispanPassword]: credentials.Password,
		}
	}
	return secret
}

func (i *infinispanCredentialReconciler) getCredentialSecretName() string {
	return fmt.Sprintf(credentialSecretName, i.runtime)
}

func isInfinispanSecretEncryptionEnabled(infinispanInstance *v1.Infinispan) bool {
	return *infinispanInstance.Spec.Security.EndpointAuthentication && len(infinispanInstance.Spec.Security.EndpointSecretName) > 0
}
