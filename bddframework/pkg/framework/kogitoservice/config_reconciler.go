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

package kogitoservice

import (
	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
)

const (
	appPropConfigMapSuffix = "-properties"
)

// ConfigReconciler ...
type ConfigReconciler interface {
	Reconcile() error
}

type configReconciler struct {
	operator.Context
	instance          api.KogitoService
	serviceDefinition *ServiceDefinition
	configMapHandler  infrastructure.ConfigMapHandler
	deltaProcessor    infrastructure.DeltaProcessor
}

func newConfigReconciler(context operator.Context, instance api.KogitoService, serviceDefinition *ServiceDefinition) ConfigReconciler {
	context.Log = context.Log.WithValues("resource", "InfraProperties")
	return &configReconciler{
		Context:           context,
		instance:          instance,
		serviceDefinition: serviceDefinition,
		configMapHandler:  infrastructure.NewConfigMapHandler(context),
		deltaProcessor:    infrastructure.NewDeltaProcessor(context),
	}
}

func (i *configReconciler) Reconcile() error {

	// Create Required resource
	requestedResources, err := i.createRequiredResources()
	if err != nil {
		return err
	}

	// Get Deployed resource
	deployedResources, err := i.getDeployedResources()
	if err != nil {
		return err
	}

	// Process Delta
	if err = i.processDelta(requestedResources, deployedResources); err != nil {
		return err
	}

	i.updateConfigMapReferenceInStatus()
	return nil
}

func (i *configReconciler) createRequiredResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	configMap := i.createInfraPropertiesConfigMap(i.instance.GetSpec().GetConfig())
	if err := framework.SetOwner(i.instance, i.Scheme, configMap); err != nil {
		return nil, err
	}
	resources[reflect.TypeOf(v1.ConfigMap{})] = []resource.KubernetesResource{configMap}
	return resources, nil
}

func (i *configReconciler) getDeployedResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	configMap, err := i.configMapHandler.FetchConfigMap(types.NamespacedName{Name: i.getInfraPropertiesConfigMapName(), Namespace: i.instance.GetNamespace()})
	if err != nil {
		return nil, err
	}
	if configMap != nil {
		resources[reflect.TypeOf(v1.ConfigMap{})] = []resource.KubernetesResource{configMap}
	}
	return resources, nil
}

func (i *configReconciler) processDelta(requestedResources map[reflect.Type][]resource.KubernetesResource, deployedResources map[reflect.Type][]resource.KubernetesResource) (err error) {
	comparator := i.configMapHandler.GetComparator()
	_, err = i.deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	return
}

func (i *configReconciler) createInfraPropertiesConfigMap(appProps map[string]string) *v1.ConfigMap {
	var data map[string]string = nil
	if len(appProps) > 0 {
		data = appProps
	}
	configMapName := i.getInfraPropertiesConfigMapName()
	configMap := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      configMapName,
			Namespace: i.instance.GetNamespace(),
			Labels: map[string]string{
				framework.LabelAppKey: i.instance.GetName(),
			},
		},
		Data: data,
	}
	return configMap
}

func (i *configReconciler) getInfraPropertiesConfigMapName() string {
	return i.instance.GetName() + appPropConfigMapSuffix
}

func (i *configReconciler) updateConfigMapReferenceInStatus() {
	i.serviceDefinition.ConfigMapEnvFromReferences = append(i.serviceDefinition.ConfigMapEnvFromReferences, i.getInfraPropertiesConfigMapName())
}
