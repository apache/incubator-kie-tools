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

package shared

import (
	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/kogitoservice"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	v1 "k8s.io/api/core/v1"
	"reflect"
)

// ProtoBufConfigMapReconciler ...
type ProtoBufConfigMapReconciler interface {
	Reconcile() error
}

type protoBufConfigMapReconciler struct {
	operator.Context
	instance                 api.KogitoSupportingServiceInterface
	serviceDefinition        *kogitoservice.ServiceDefinition
	runtimeHandler           manager.KogitoRuntimeHandler
	configMapHandler         infrastructure.ConfigMapHandler
	protobufConfigMapHandler ProtoBufConfigMapHandler
	deltaProcessor           infrastructure.DeltaProcessor
}

// NewProtoBufConfigMapReconciler ...
func NewProtoBufConfigMapReconciler(context operator.Context, instance api.KogitoSupportingServiceInterface, serviceDefinition *kogitoservice.ServiceDefinition, runtimeHandler manager.KogitoRuntimeHandler) ProtoBufConfigMapReconciler {
	return &protoBufConfigMapReconciler{
		Context:                  context,
		instance:                 instance,
		serviceDefinition:        serviceDefinition,
		runtimeHandler:           runtimeHandler,
		configMapHandler:         infrastructure.NewConfigMapHandler(context),
		protobufConfigMapHandler: NewProtoBufConfigMapHandler(context),
		deltaProcessor:           infrastructure.NewDeltaProcessor(context),
	}
}

func (p *protoBufConfigMapReconciler) Reconcile() error {

	runtimeInstances, err := p.runtimeHandler.FetchAllKogitoRuntimeInstances(p.instance.GetNamespace())
	if err != nil {
		return err
	}
	for _, runtimeInstance := range runtimeInstances.GetItems() {
		// Create Required resource
		requestedResources, err := p.createRequiredResources(runtimeInstance)
		if err != nil {
			return err
		}

		// Get Deployed resource
		deployedResources, err := p.getDeployedResources(runtimeInstance)
		if err != nil {
			return err
		}

		// Process Delta
		if err = p.processDelta(requestedResources, deployedResources); err != nil {
			return err
		}

		volumeReference := p.protobufConfigMapHandler.CreateProtoBufConfigMapReference(runtimeInstance)
		p.serviceDefinition.ConfigMapVolumeReferences = append(p.serviceDefinition.ConfigMapVolumeReferences, volumeReference)
	}
	return nil
}

func (p *protoBufConfigMapReconciler) createRequiredResources(runtimeInstance api.KogitoRuntimeInterface) (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	protoBufConfigMap, err := p.protobufConfigMapHandler.CreateProtoBufConfigMap(runtimeInstance)
	if err != nil {
		return nil, err
	}
	if err := framework.SetOwner(p.instance, p.Scheme, protoBufConfigMap); err != nil {
		return nil, err
	}
	resources[reflect.TypeOf(v1.ConfigMap{})] = []resource.KubernetesResource{protoBufConfigMap}
	return resources, nil
}

func (p *protoBufConfigMapReconciler) getDeployedResources(runtimeInstance api.KogitoRuntimeInterface) (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	labels := map[string]string{
		framework.LabelAppKey:            runtimeInstance.GetName(),
		ConfigMapProtoBufEnabledLabelKey: "true",
	}
	configMapList, err := p.configMapHandler.FetchConfigMapsForLabel(p.instance.GetNamespace(), labels)
	if err != nil {
		return nil, err
	}
	if len(configMapList.Items) > 0 {
		kubernetesResources := make([]resource.KubernetesResource, len(configMapList.Items))
		for i, configMap := range configMapList.Items {
			item := configMap
			kubernetesResources[i] = &item
		}
		resources[reflect.TypeOf(v1.ConfigMap{})] = kubernetesResources
	}
	return resources, nil
}

func (p *protoBufConfigMapReconciler) processDelta(requestedResources map[reflect.Type][]resource.KubernetesResource, deployedResources map[reflect.Type][]resource.KubernetesResource) (err error) {
	comparator := p.configMapHandler.GetComparator()
	_, err = p.deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	return
}
