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

package manager

import (
	"fmt"
	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/core/framework"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"k8s.io/apimachinery/pkg/types"
)

// KogitoInfraManager ...
type KogitoInfraManager interface {
	MustFetchKogitoInfraInstance(key types.NamespacedName) (api.KogitoInfraInterface, error)
	TakeKogitoInfraOwnership(key types.NamespacedName, owner resource.KubernetesResource) error
	RemoveKogitoInfraOwnership(key types.NamespacedName, owner resource.KubernetesResource) error
	IsKogitoInfraReady(key types.NamespacedName) (bool, error)
	GetKogitoInfraConditionReason(key types.NamespacedName) (api.KogitoInfraConditionReason, error)
}

// KogitoInfraHandler ...
type KogitoInfraHandler interface {
	FetchKogitoInfraInstance(key types.NamespacedName) (api.KogitoInfraInterface, error)
}

type kogitoInfraManager struct {
	*operator.Context
	infraHandler KogitoInfraHandler
}

// NewKogitoInfraManager ...
func NewKogitoInfraManager(context *operator.Context, infraHandler KogitoInfraHandler) KogitoInfraManager {
	return &kogitoInfraManager{
		Context:      context,
		infraHandler: infraHandler,
	}
}

// MustFetchKogitoInfraInstance loads a given infra instance by name and namespace.
// If the KogitoInfra resource is not present, an error is raised.
func (k *kogitoInfraManager) MustFetchKogitoInfraInstance(key types.NamespacedName) (api.KogitoInfraInterface, error) {
	k.Log.Debug("going to must fetch deployed kogito infra instance")
	if instance, resultErr := k.infraHandler.FetchKogitoInfraInstance(key); resultErr != nil {
		k.Log.Error(resultErr, "Error occurs while fetching deployed kogito infra instance")
		return nil, resultErr
	} else if instance == nil {
		return nil, fmt.Errorf("kogito Infra resource with name %s not found in namespace %s", key.Name, key.Namespace)
	} else {
		k.Log.Debug("Successfully fetch deployed kogito infra reference")
		return instance, nil
	}
}

func (k *kogitoInfraManager) TakeKogitoInfraOwnership(key types.NamespacedName, owner resource.KubernetesResource) (err error) {
	kogitoInfra, err := k.MustFetchKogitoInfraInstance(key)
	if err != nil {
		return
	}
	if framework.IsOwner(kogitoInfra, owner) {
		return
	}
	if err = framework.AddOwnerReference(owner, k.Scheme, kogitoInfra); err != nil {
		return
	}
	if err = kubernetes.ResourceC(k.Client).Update(kogitoInfra); err != nil {
		return
	}
	return
}

func (k *kogitoInfraManager) RemoveKogitoInfraOwnership(key types.NamespacedName, owner resource.KubernetesResource) (err error) {
	k.Log.Info("Removing kogito infra ownership", "infra name", key.Name, "owner", owner.GetName())
	kogitoInfra, err := k.MustFetchKogitoInfraInstance(key)
	if err != nil {
		return
	}
	framework.RemoveOwnerReference(owner, kogitoInfra)
	if err = kubernetes.ResourceC(k.Client).Update(kogitoInfra); err != nil {
		return err
	}
	k.Log.Debug("Successfully removed KogitoInfra ownership", "infra name", kogitoInfra.GetName(), "owner", owner.GetName())
	return
}

func (k *kogitoInfraManager) IsKogitoInfraReady(key types.NamespacedName) (bool, error) {
	infra, err := k.MustFetchKogitoInfraInstance(key)
	if err != nil {
		return false, err
	}
	if infra.GetStatus().GetCondition().GetType() == api.FailureInfraConditionType {
		return false, nil
	}
	return true, nil
}

func (k *kogitoInfraManager) GetKogitoInfraConditionReason(key types.NamespacedName) (api.KogitoInfraConditionReason, error) {
	infra, err := k.MustFetchKogitoInfraInstance(key)
	if err != nil {
		return "", err
	}

	return infra.GetStatus().GetCondition().GetReason(), nil
}
