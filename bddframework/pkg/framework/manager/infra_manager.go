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
	"github.com/kiegroup/kogito-operator/core/framework/util"
	corev1 "k8s.io/api/core/v1"

	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	"k8s.io/apimachinery/pkg/api/meta"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

// KogitoInfraManager ...
type KogitoInfraManager interface {
	MustFetchKogitoInfraInstance(key types.NamespacedName) (api.KogitoInfraInterface, error)
	TakeKogitoInfraOwnership(key types.NamespacedName, owner resource.KubernetesResource) error
	RemoveKogitoInfraOwnership(key types.NamespacedName, owner resource.KubernetesResource) error
	IsKogitoInfraReady(key types.NamespacedName) (bool, error)
	GetKogitoInfraFailureConditionReason(key types.NamespacedName) (string, error)
	FetchKogitoInfraProperties(runtimeType api.RuntimeType, namespace string, kogitoInfraReferences ...string) (map[string]string, []corev1.EnvVar, []api.KogitoInfraVolumeInterface, error)
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
	kogitoInfra, err := k.infraHandler.FetchKogitoInfraInstance(key)
	if err != nil || kogitoInfra == nil {
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

func (k *kogitoInfraManager) FetchKogitoInfraProperties(runtimeType api.RuntimeType, namespace string, kogitoInfraReferences ...string) (map[string]string, []corev1.EnvVar, []api.KogitoInfraVolumeInterface, error) {
	k.Log.Debug("Going to fetch kogito infra properties", "infra", kogitoInfraReferences)
	consolidateAppProperties := map[string]string{}
	var consolidateEnvProperties []corev1.EnvVar
	var volumes []api.KogitoInfraVolumeInterface
	for _, kogitoInfraName := range kogitoInfraReferences {
		// load infra resource
		kogitoInfraInstance, err := k.MustFetchKogitoInfraInstance(types.NamespacedName{Name: kogitoInfraName, Namespace: namespace})
		if err != nil {
			return nil, nil, nil, err
		}

		// fetch app properties from Kogito infra instance
		runtimeProperties := kogitoInfraInstance.GetStatus().GetRuntimeProperties()[runtimeType]
		if runtimeProperties != nil {
			appProp := runtimeProperties.GetAppProps()
			util.AppendToStringMap(appProp, consolidateAppProperties)

			// fetch env properties from Kogito infra instance
			envProp := runtimeProperties.GetEnv()
			consolidateEnvProperties = append(consolidateEnvProperties, envProp...)
		}
		// fetch volume from Kogito infra instance
		volumes = append(volumes, kogitoInfraInstance.GetStatus().GetVolumes()...)
	}
	return consolidateAppProperties, consolidateEnvProperties, volumes, nil
}
