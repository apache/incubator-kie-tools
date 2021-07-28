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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"k8s.io/api/apps/v1"
	"k8s.io/apimachinery/pkg/types"
)

// KogitoRuntimeManager ...
type KogitoRuntimeManager interface {
	FetchKogitoRuntimeDeployments(namespace string) ([]v1.Deployment, error)
}

// KogitoRuntimeHandler ...
type KogitoRuntimeHandler interface {
	FetchKogitoRuntimeInstance(key types.NamespacedName) (api.KogitoRuntimeInterface, error)
	FetchAllKogitoRuntimeInstances(namespace string) (api.KogitoRuntimeListInterface, error)
}

type kogitoRuntimeManager struct {
	operator.Context
	runtimeHandler KogitoRuntimeHandler
}

// NewKogitoRuntimeManager ...
func NewKogitoRuntimeManager(context operator.Context, runtimeHandler KogitoRuntimeHandler) KogitoRuntimeManager {
	return &kogitoRuntimeManager{
		Context:        context,
		runtimeHandler: runtimeHandler,
	}
}

// FetchKogitoRuntimeDeployments gets all dcs owned by KogitoRuntime services within the given namespace
func (k *kogitoRuntimeManager) FetchKogitoRuntimeDeployments(namespace string) ([]v1.Deployment, error) {
	var kdcs []v1.Deployment
	kogitoRuntimeServices, err := k.runtimeHandler.FetchAllKogitoRuntimeInstances(namespace)
	if err != nil {
		return nil, err
	} else if len(kogitoRuntimeServices.GetItems()) == 0 {
		return kdcs, nil
	}

	deploymentHandler := infrastructure.NewDeploymentHandler(k.Context)
	deps, err := deploymentHandler.FetchDeploymentList(namespace)
	if err != nil {
		return nil, err
	}
	k.Log.Debug("Looking for Deployments owned by KogitoRuntime")
	for _, dep := range deps.Items {
		for _, owner := range dep.OwnerReferences {
			for _, app := range kogitoRuntimeServices.GetItems() {
				if owner.UID == app.GetUID() {
					kdcs = append(kdcs, dep)
					break
				}
			}
		}
	}
	return kdcs, nil
}
