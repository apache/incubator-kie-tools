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
	api "github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	v1 "k8s.io/api/core/v1"
	"reflect"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

// ProtoBufConfigMapReconciler ...
type ProtoBufConfigMapReconciler interface {
	Reconcile() error
}

type protoBufConfigMapReconciler struct {
	operator.Context
	runtimeInstance          api.KogitoRuntimeInterface
	configMapHandler         infrastructure.ConfigMapHandler
	protobufConfigMapHandler ProtoBufConfigMapHandler
	deltaProcessor           infrastructure.DeltaProcessor
}

// NewProtoBufConfigMapReconciler ...
func NewProtoBufConfigMapReconciler(context operator.Context, instance api.KogitoRuntimeInterface) ProtoBufConfigMapReconciler {
	return &protoBufConfigMapReconciler{
		Context:                  context,
		runtimeInstance:          instance,
		configMapHandler:         infrastructure.NewConfigMapHandler(context),
		protobufConfigMapHandler: NewProtoBufConfigMapHandler(context),
		deltaProcessor:           infrastructure.NewDeltaProcessor(context),
	}
}

func (p *protoBufConfigMapReconciler) Reconcile() error {

	// Create Required resource
	requestedResources, err := p.createRequiredResources(p.runtimeInstance)
	if err != nil {
		return err
	}

	// Get Deployed resource
	deployedResources, err := p.getDeployedResources(p.runtimeInstance)
	if err != nil {
		return err
	}

	// Process Delta
	return p.processDelta(requestedResources, deployedResources)
}

func (p *protoBufConfigMapReconciler) createRequiredResources(runtimeInstance api.KogitoRuntimeInterface) (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	protoBufConfigMap, err := p.protobufConfigMapHandler.CreateProtoBufConfigMap(runtimeInstance)
	if err != nil {
		return nil, err
	}
	if protoBufConfigMap != nil {

		if err := framework.SetOwner(runtimeInstance, p.Scheme, protoBufConfigMap); err != nil {
			return nil, err
		}
		resources[reflect.TypeOf(v1.ConfigMap{})] = []client.Object{protoBufConfigMap}
	}
	return resources, nil
}

func (p *protoBufConfigMapReconciler) getDeployedResources(runtimeInstance api.KogitoRuntimeInterface) (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	labels := map[string]string{
		framework.LabelAppKey:            runtimeInstance.GetName(),
		ConfigMapProtoBufEnabledLabelKey: "true",
	}
	configMapList, err := p.configMapHandler.FetchConfigMapsForLabel(runtimeInstance.GetNamespace(), labels)
	if err != nil {
		return nil, err
	}
	if len(configMapList.Items) > 0 {
		kubernetesResources := make([]client.Object, len(configMapList.Items))
		for i, configMap := range configMapList.Items {
			item := configMap
			kubernetesResources[i] = &item
		}
		resources[reflect.TypeOf(v1.ConfigMap{})] = kubernetesResources
	}
	return resources, nil
}

func (p *protoBufConfigMapReconciler) processDelta(requestedResources map[reflect.Type][]client.Object, deployedResources map[reflect.Type][]client.Object) (err error) {
	comparator := p.configMapHandler.GetComparator()
	isDeltaProcessed, err := p.deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	if err != nil {
		return err
	}
	if isDeltaProcessed {
		return infrastructure.ErrorForProcessingProtoBufConfigMapDelta()
	}
	return
}
