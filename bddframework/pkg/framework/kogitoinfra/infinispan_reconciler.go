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
	"fmt"

	api "github.com/kiegroup/kogito-operator/apis"
	infinispan "github.com/kiegroup/kogito-operator/core/infrastructure/infinispan/v1"

	"github.com/kiegroup/kogito-operator/core/infrastructure"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/builder"
)

const (
	// appPropInfinispanServerList application property for setting infinispan server
	appPropInfinispanServerList int = iota
	// appPropInfinispanUseAuth application property for enabling infinispan authentication
	appPropInfinispanUseAuth
	// appPropInfinispanSaslMechanism application property for setting infinispan SASL mechanism
	// InfinispanSaslMechanismType is the possible SASL Mechanism used during infinispan connection.
	// For more information, see https://en.wikipedia.org/wiki/Simple_Authentication_and_Security_Layer#SASL_mechanisms.
	appPropInfinispanSaslMechanism
	// appPropInfinispanAuthRealm application property for setting infinispan auth realm
	appPropInfinispanAuthRealm
	appPropInfinispanTrustStore
	appPropInfinispanTrustStoreType
	appPropInfinispanTrustStorePassword
	// envVarInfinispanUser environment variable for setting infinispan username
	envVarInfinispanUser
	// envVarInfinispanPassword environment variable for setting infinispan password
	envVarInfinispanPassword
)

var (
	//Infinispan variables for the KogitoInfra deployed infrastructure.
	//For Quarkus: https://quarkus.io/guides/infinispan-client#quarkus-infinispan-client_configuration
	//For Spring: https://github.com/infinispan/infinispan-spring-boot/blob/main/infinispan-spring-boot-starter-remote/src/test/resources/test-application.properties

	propertiesInfinispan = map[api.RuntimeType]map[int]string{
		api.QuarkusRuntimeType: {
			appPropInfinispanServerList:         "quarkus.infinispan-client.hosts",
			appPropInfinispanUseAuth:            "quarkus.infinispan-client.use-auth",
			appPropInfinispanSaslMechanism:      "quarkus.infinispan-client.sasl-mechanism",
			appPropInfinispanAuthRealm:          "quarkus.infinispan-client.auth-realm",
			appPropInfinispanTrustStore:         "quarkus.infinispan-client.trust-store",
			appPropInfinispanTrustStoreType:     "quarkus.infinispan-client.trust-store-type",
			appPropInfinispanTrustStorePassword: "quarkus.infinispan-client.trust-store-password",

			envVarInfinispanUser:     "QUARKUS_INFINISPAN_CLIENT_USERNAME",
			envVarInfinispanPassword: "QUARKUS_INFINISPAN_CLIENT_PASSWORD",
		},
		api.SpringBootRuntimeType: {
			appPropInfinispanServerList:         "infinispan.remote.server-list",
			appPropInfinispanUseAuth:            "infinispan.remote.use-auth",
			appPropInfinispanSaslMechanism:      "infinispan.remote.sasl-mechanism",
			appPropInfinispanAuthRealm:          "infinispan.remote.auth-realm",
			appPropInfinispanTrustStore:         "infinispan.remote.trust-store-file-name",
			appPropInfinispanTrustStoreType:     "infinispan.remote.trust-store-type",
			appPropInfinispanTrustStorePassword: "infinispan.remote.trust-store-password",

			envVarInfinispanUser:     "INFINISPAN_REMOTE_AUTH_USERNAME",
			envVarInfinispanPassword: "INFINISPAN_REMOTE_AUTH_PASSWORD",
		},
	}
)

type infinispanInfraReconciler struct {
	infraContext
}

func initInfinispanInfraReconciler(context infraContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "Infinispan")
	return &infinispanInfraReconciler{
		infraContext: context,
	}
}

// AppendInfinispanWatchedObjects ...
func AppendInfinispanWatchedObjects(b *builder.Builder) *builder.Builder {
	return b.Owns(&corev1.Secret{})
}

// Reconcile reconcile Kogito infra object
func (i *infinispanInfraReconciler) Reconcile() (resultErr error) {

	var infinispanInstance *infinispan.Infinispan

	infinispanHandler := infrastructure.NewInfinispanHandler(i.Context)
	if !infinispanHandler.IsInfinispanAvailable() {
		return errorForResourceAPINotFound(i.instance.GetSpec().GetResource().GetAPIVersion())
	}

	if len(i.instance.GetSpec().GetResource().GetName()) > 0 {
		i.Log.Debug("Custom infinispan instance reference is provided")

		namespace := i.instance.GetSpec().GetResource().GetNamespace()
		if len(namespace) == 0 {
			namespace = i.instance.GetNamespace()
			i.Log.Debug("Namespace is not provided for custom resource, taking", "Namespace", namespace)
		}
		infinispanInstance, resultErr = infinispanHandler.FetchInfinispanInstance(types.NamespacedName{Name: i.instance.GetSpec().GetResource().GetName(), Namespace: namespace})
		if resultErr != nil {
			return resultErr
		}
		if infinispanInstance == nil {
			return errorForResourceNotFound("Infinispan", i.instance.GetSpec().GetResource().GetName(), namespace)
		}
	} else {
		return errorForResourceConfigError(i.instance, "No Infinispan resource name given")
	}
	if !infinispanInstance.IsWellFormed() {
		return errorForResourceNotReadyError(fmt.Errorf("infinispan instance %s not ready", infinispanInstance.Name))
	}
	if resultErr = i.updateTrustStoreSecretReferenceInStatus(infinispanInstance); resultErr != nil {
		return nil
	}

	if resultErr = i.updateInfinispanRuntimePropsInStatus(infinispanInstance, api.QuarkusRuntimeType); resultErr != nil {
		return nil
	}
	if resultErr = i.updateInfinispanRuntimePropsInStatus(infinispanInstance, api.SpringBootRuntimeType); resultErr != nil {
		return nil
	}
	return resultErr
}

func (i *infinispanInfraReconciler) updateTrustStoreSecretReferenceInStatus(infinispanInstance *infinispan.Infinispan) error {
	infinispanTrustStoreReconciler := newInfinispanTrustStoreReconciler(i.infraContext, infinispanInstance)
	return infinispanTrustStoreReconciler.Reconcile()
}

func (i *infinispanInfraReconciler) updateInfinispanRuntimePropsInStatus(infinispanInstance *infinispan.Infinispan, runtime api.RuntimeType) error {
	i.Log.Debug("going to Update Infinispan runtime properties in kogito infra instance status", "runtime", runtime)
	infinispanConfigReconciler := newInfinispanConfigReconciler(i.infraContext, infinispanInstance, runtime)
	if err := infinispanConfigReconciler.Reconcile(); err != nil {
		return err
	}

	infinispanCredentialReconciler := newInfinispanCredentialReconciler(i.infraContext, infinispanInstance, runtime)
	return infinispanCredentialReconciler.Reconcile()
}
