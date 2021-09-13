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
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
	"strings"
	"time"
)

const reconciliationStandardInterval = time.Second * 30

// Reconciler Interface to represent type of supported kogito infra reconciliation algorithm for resources like Infinispan, kafka & keycloak
type Reconciler interface {
	Reconcile() error
}

type infraContext struct {
	operator.Context
	instance api.KogitoInfraInterface
}

// ReconcilerHandler ...
type ReconcilerHandler interface {
	GetInfraReconciler(instance api.KogitoInfraInterface) (Reconciler, error)
	GetConfigMapReferenceReconciler(instance api.KogitoInfraInterface) Reconciler
	GetSecretReferenceReconciler(instance api.KogitoInfraInterface) Reconciler
	GetInfraPropertiesReconciler(instance api.KogitoInfraInterface) Reconciler
	GetReconcileResultFor(err error, requeue bool) (reconcile.Result, error)
}

type reconcilerHandler struct {
	operator.Context
}

// NewReconcilerHandler ...
func NewReconcilerHandler(context operator.Context) ReconcilerHandler {
	return &reconcilerHandler{
		context,
	}
}

// getKogitoInfraReconciler identify and return request kogito infra reconciliation logic on bases of information provided in kogitoInfra value
func (k *reconcilerHandler) GetInfraReconciler(instance api.KogitoInfraInterface) (Reconciler, error) {
	k.Log.Debug("going to fetch related kogito infra resource")
	context := infraContext{
		Context:  k.Context,
		instance: instance,
	}
	if infraRes, ok := getSupportedInfraResources(context)[resourceClassForInstance(instance.GetSpec().GetResource())]; ok {
		return infraRes, nil
	}
	return nil, errorForUnsupportedAPI(context)
}

// GetConfigMapReferenceReconciler identify and return request kogito infra reconciliation logic on bases of information provided in kogitoInfra value
func (k *reconcilerHandler) GetConfigMapReferenceReconciler(instance api.KogitoInfraInterface) Reconciler {
	k.Log.Debug("going to fetch related kogito infra resource")
	context := infraContext{
		Context:  k.Context,
		instance: instance,
	}
	return initConfigMapReferenceReconciler(context)
}

// GetSecretReferenceReconciler identify and return request kogito infra reconciliation logic on bases of information provided in kogitoInfra value
func (k *reconcilerHandler) GetSecretReferenceReconciler(instance api.KogitoInfraInterface) Reconciler {
	k.Log.Debug("going to fetch related kogito infra resource")
	context := infraContext{
		Context:  k.Context,
		instance: instance,
	}
	return initSecretReferenceReconciler(context)
}

// GetAppConfigMapReconciler identify and return request kogito infra reconciliation logic on bases of information provided in kogitoInfra value
func (k *reconcilerHandler) GetInfraPropertiesReconciler(instance api.KogitoInfraInterface) Reconciler {
	k.Log.Debug("going to fetch related kogito infra resource")
	context := infraContext{
		Context:  k.Context,
		instance: instance,
	}
	return initInfraPropertiesReconciler(context)
}

func resourceClassForInstance(resource api.ResourceInterface) string {
	return getResourceClass(resource.GetKind(), resource.GetAPIVersion())
}

func getResourceClass(kind, APIVersion string) string {
	return strings.ToLower(fmt.Sprintf("%s.%s", kind, APIVersion))
}

func getSupportedInfraResources(context infraContext) map[string]Reconciler {
	return map[string]Reconciler{
		getResourceClass(infrastructure.InfinispanKind, infrastructure.InfinispanAPIVersion):                 initInfinispanInfraReconciler(context),
		getResourceClass(infrastructure.KafkaKind, infrastructure.KafkaAPIVersion):                           initKafkaInfraReconciler(context),
		getResourceClass(infrastructure.KeycloakKind, infrastructure.KeycloakAPIVersion):                     initkeycloakInfraReconciler(context),
		getResourceClass(infrastructure.KnativeEventingBrokerKind, infrastructure.KnativeEventingAPIVersion): initknativeInfraReconciler(context),
		getResourceClass(infrastructure.MongoDBKind, infrastructure.MongoDBAPIVersion):                       initMongoDBInfraReconciler(context),
	}
}

func (k *reconcilerHandler) GetReconcileResultFor(err error, requeue bool) (reconcile.Result, error) {
	switch reasonForError(err) {
	case api.ReconciliationFailure:
		k.Log.Warn("Error while reconciling KogitoInfra", "error", err.Error())
		return reconcile.Result{RequeueAfter: 0, Requeue: false}, err
	case api.ResourceMissingResourceConfig, api.ResourceConfigError:
		k.Log.Error(err, "KogitoInfra configuration error")
		return reconcile.Result{RequeueAfter: 0, Requeue: false}, nil
	}

	// no requeue, no errors, stop reconciliation
	if !requeue && err == nil {
		k.Log.Debug("No need reconciliation for KogitoInfra")
		return reconcile.Result{RequeueAfter: 0, Requeue: false}, nil
	}
	// caller is asking for a reconciliation
	if err == nil {
		k.Log.Info("Waiting for all resources to be created, scheduling reconciliation.", "reconciliation interval", reconciliationStandardInterval.String())
	} else { // reconciliation duo to a problem in the env (CRDs missing), infra deployments not ready, operators not installed.. etc. See reconciliation_error.go
		k.Log.Info("Err", err.Error(), "Scheduling reconciliation", "reconciliation interval", reconciliationStandardInterval.String())
	}
	return reconcile.Result{RequeueAfter: reconciliationStandardInterval}, nil
}
