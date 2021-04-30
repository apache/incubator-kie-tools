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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"k8s.io/api/apps/v1"
	"k8s.io/apimachinery/pkg/types"
)

// KogitoSupportingServiceManager ...
type KogitoSupportingServiceManager interface {
	EnsureSingletonService(namespace string, resourceType api.ServiceType) error
	FetchKogitoSupportingServiceRoute(namespace string, serviceType api.ServiceType) (route string, err error)
	FetchKogitoSupportingServiceDeployment(namespace string, serviceType api.ServiceType) (*v1.Deployment, error)
}

// KogitoSupportingServiceHandler ...
type KogitoSupportingServiceHandler interface {
	FetchKogitoSupportingService(key types.NamespacedName) (api.KogitoSupportingServiceInterface, error)
	FetchKogitoSupportingServiceList(namespace string) (api.KogitoSupportingServiceListInterface, error)
}

type kogitoSupportingServiceManager struct {
	operator.Context
	supportingServiceHandler KogitoSupportingServiceHandler
}

// NewKogitoSupportingServiceManager ...
func NewKogitoSupportingServiceManager(context operator.Context, supportingServiceHandler KogitoSupportingServiceHandler) KogitoSupportingServiceManager {
	return &kogitoSupportingServiceManager{
		Context:                  context,
		supportingServiceHandler: supportingServiceHandler,
	}
}

func (k kogitoSupportingServiceManager) EnsureSingletonService(namespace string, resourceType api.ServiceType) error {
	k.Log.Info("Ensuring only single instance of supporting service exists")
	supportingServiceList, err := k.supportingServiceHandler.FetchKogitoSupportingServiceList(namespace)
	if err != nil {
		return err
	}

	var kogitoSupportingService []api.KogitoSupportingServiceInterface
	for _, service := range supportingServiceList.GetItems() {
		if service.GetSupportingServiceSpec().GetServiceType() == resourceType {
			kogitoSupportingService = append(kogitoSupportingService, service)
		}
	}

	if len(kogitoSupportingService) > 1 {
		return fmt.Errorf("kogito Supporting Service(%s) already exists, please delete the duplicate before proceeding", resourceType)
	}
	return nil
}

// getKogitoSupportingServiceRoute gets the route from a kogito service that's unique in the given namespace
func (k kogitoSupportingServiceManager) FetchKogitoSupportingServiceRoute(namespace string, serviceType api.ServiceType) (route string, err error) {
	supportingService, err := k.getKogitoSupportingService(namespace, serviceType)
	if err != nil {
		return
	}
	if supportingService != nil {
		return supportingService.GetStatus().GetExternalURI(), nil
	}
	return
}

// getSupportingServiceDeployment gets deployment owned by supporting service within the given namespace
func (k kogitoSupportingServiceManager) FetchKogitoSupportingServiceDeployment(namespace string, serviceType api.ServiceType) (*v1.Deployment, error) {
	supportingService, err := k.getKogitoSupportingService(namespace, serviceType)
	if err != nil {
		return nil, err
	} else if supportingService == nil {
		k.Log.Debug("KogitoSupportingService objects not found", "service type", serviceType, "namespace", namespace)
		return nil, nil
	}
	k.Log.Debug("KogitoSupportingService objects found", "services", serviceType, "namespace", namespace)

	deploymentHandler := infrastructure.NewDeploymentHandler(k.Context)
	dcs, err := deploymentHandler.FetchDeploymentList(namespace)
	if err != nil {
		return nil, err
	}
	k.Log.Debug("Looking for owned Deployments", "service type", serviceType)
	for _, dc := range dcs.Items {
		for _, owner := range dc.OwnerReferences {
			if owner.UID == supportingService.GetUID() {
				return &dc, nil
			}
		}
	}
	return nil, nil
}

func (k kogitoSupportingServiceManager) getKogitoSupportingService(namespace string, serviceType api.ServiceType) (api.KogitoSupportingServiceInterface, error) {
	supportingServiceList, err := k.supportingServiceHandler.FetchKogitoSupportingServiceList(namespace)
	if err != nil {
		return nil, err
	}
	for _, service := range supportingServiceList.GetItems() {
		if service.GetSupportingServiceSpec().GetServiceType() == serviceType {
			return service, nil
		}
	}
	return nil, nil
}
