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

package kogitoinfra

import (
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	infraPropConfigMapSuffix = "-properties"
)

type infraPropertiesReconciler struct {
	infraContext
	configMapHandler infrastructure.ConfigMapHandler
	deltaProcessor   infrastructure.DeltaProcessor
}

// initInfraPropertiesReconciler ...
func initInfraPropertiesReconciler(infraContext infraContext) Reconciler {
	infraContext.Log = infraContext.Log.WithValues("resource", "InfraProperties")
	return &infraPropertiesReconciler{
		infraContext:     infraContext,
		configMapHandler: infrastructure.NewConfigMapHandler(infraContext.Context),
		deltaProcessor:   infrastructure.NewDeltaProcessor(infraContext.Context),
	}
}

func (i *infraPropertiesReconciler) Reconcile() error {
	if len(i.instance.GetSpec().GetInfraProperties()) == 0 {
		return nil
	}

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

	i.instance.GetStatus().AddConfigMapEnvFromReferences(i.getInfraPropertiesConfigMapName())
	return nil
}

func (i *infraPropertiesReconciler) createRequiredResources() (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)

	configMap := i.createInfraPropertiesConfigMap(i.instance.GetSpec().GetInfraProperties())
	if err := framework.SetOwner(i.instance, i.Scheme, configMap); err != nil {
		return nil, err
	}
	resources[reflect.TypeOf(v1.ConfigMap{})] = []client.Object{configMap}
	return resources, nil
}

func (i *infraPropertiesReconciler) getDeployedResources() (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	configMap, err := i.configMapHandler.FetchConfigMap(types.NamespacedName{Name: i.getInfraPropertiesConfigMapName(), Namespace: i.instance.GetNamespace()})
	if err != nil {
		return nil, err
	}
	if configMap != nil {
		resources[reflect.TypeOf(v1.ConfigMap{})] = []client.Object{configMap}
	}
	return resources, nil
}

func (i *infraPropertiesReconciler) processDelta(requestedResources map[reflect.Type][]client.Object, deployedResources map[reflect.Type][]client.Object) (err error) {
	comparator := i.configMapHandler.GetComparator()
	_, err = i.deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	return
}

func (i *infraPropertiesReconciler) createInfraPropertiesConfigMap(appProps map[string]string) *v1.ConfigMap {
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

func (i *infraPropertiesReconciler) getInfraPropertiesConfigMapName() string {
	return i.instance.GetName() + infraPropConfigMapSuffix
}
