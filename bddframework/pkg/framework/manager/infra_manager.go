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
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/operator"
	"k8s.io/apimachinery/pkg/api/meta"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

// KogitoInfraManager ...
type KogitoInfraManager interface {
	MustFetchKogitoInfraInstance(key types.NamespacedName) (api.KogitoInfraInterface, error)
	IsKogitoInfraReady(key types.NamespacedName) (bool, error)
	GetKogitoInfraFailureConditionReason(key types.NamespacedName) (string, error)
}

// KogitoInfraHandler ...
type KogitoInfraHandler interface {
	FetchKogitoInfraInstance(key types.NamespacedName) (api.KogitoInfraInterface, error)
}

type kogitoInfraManager struct {
	operator.Context
	infraHandler KogitoInfraHandler
}

// NewKogitoInfraManager ...
func NewKogitoInfraManager(context operator.Context, infraHandler KogitoInfraHandler) KogitoInfraManager {
	if infraHandler == nil {
		panic("InfraHandler can't be nil when creating a new KogitoInfraManager")
	}
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

func (k *kogitoInfraManager) IsKogitoInfraReady(key types.NamespacedName) (bool, error) {
	infra, err := k.MustFetchKogitoInfraInstance(key)
	if err != nil {
		return false, err
	}
	conditions := infra.GetStatus().GetConditions()
	if conditions == nil {
		return false, nil
	}
	return meta.IsStatusConditionTrue(*conditions, string(api.KogitoInfraConfigured)), nil
}

func (k *kogitoInfraManager) GetKogitoInfraFailureConditionReason(key types.NamespacedName) (string, error) {
	infra, err := k.MustFetchKogitoInfraInstance(key)
	if err != nil {
		return "", err
	}
	conditions := infra.GetStatus().GetConditions()
	if conditions == nil {
		return "", nil
	}
	failureCondition := meta.FindStatusCondition(*conditions, string(api.KogitoInfraConfigured))
	if failureCondition != nil && failureCondition.Status == v1.ConditionFalse {
		return failureCondition.Reason, nil
	}
	return "", nil
}
