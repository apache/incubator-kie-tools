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
	"fmt"
	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/infinispan/infinispan-operator/pkg/apis/infinispan/v1"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	v12 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
)

const (
	infinispanConfigMapName           = "kogito-infinispan-%s-config"
	infinispanEnablePersistenceEnvKey = "ENABLE_PERSISTENCE"
)

type infinispanConfigReconciler struct {
	infraContext
	infinispanInstance *v1.Infinispan
	runtime            api.RuntimeType
	configMapHandler   infrastructure.ConfigMapHandler
}

func newInfinispanConfigReconciler(context infraContext, infinispanInstance *v1.Infinispan, runtime api.RuntimeType) Reconciler {
	return &infinispanConfigReconciler{
		infraContext:       context,
		infinispanInstance: infinispanInstance,
		runtime:            runtime,
		configMapHandler:   infrastructure.NewConfigMapHandler(context.Context),
	}
}

func (i *infinispanConfigReconciler) Reconcile() (err error) {

	// Create Required resource
	requestedResources, err := i.createRequiredResources()
	if err != nil {
		return
	}

	// Get Deployed resource
	deployedResources, err := i.getDeployedResources()
	if err != nil {
		return
	}

	// Process Delta
	if err = i.processDelta(requestedResources, deployedResources); err != nil {
		return err
	}

	i.instance.GetStatus().AddConfigMapEnvFromReferences(i.getInfinispanConfigMapName())
	return nil
}

func (i *infinispanConfigReconciler) createRequiredResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	appProps, err := i.getInfinispanAppProps()
	if err != nil {
		return nil, err
	}
	configMap := i.createInfinispanConfigMap(appProps)
	if err := framework.SetOwner(i.instance, i.Scheme, configMap); err != nil {
		return resources, err
	}
	resources[reflect.TypeOf(v12.ConfigMap{})] = []resource.KubernetesResource{configMap}
	return resources, nil
}

func (i *infinispanConfigReconciler) getDeployedResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	// fetch owned image stream
	deployedConfigMap, err := i.configMapHandler.FetchConfigMap(types.NamespacedName{Name: i.getInfinispanConfigMapName(), Namespace: i.instance.GetNamespace()})
	if err != nil {
		return nil, err
	}
	if deployedConfigMap != nil {
		resources[reflect.TypeOf(v12.ConfigMap{})] = []resource.KubernetesResource{deployedConfigMap}
	}
	return resources, nil
}

func (i *infinispanConfigReconciler) processDelta(requestedResources map[reflect.Type][]resource.KubernetesResource, deployedResources map[reflect.Type][]resource.KubernetesResource) (err error) {
	comparator := i.configMapHandler.GetComparator()
	deltaProcessor := infrastructure.NewDeltaProcessor(i.Context)
	_, err = deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	return err
}

func (i *infinispanConfigReconciler) getInfinispanAppProps() (map[string]string, error) {
	appProps := map[string]string{}

	infinispanHandler := infrastructure.NewInfinispanHandler(i.Context)
	infinispanURI, resultErr := infinispanHandler.FetchInfinispanInstanceURI(types.NamespacedName{Name: i.infinispanInstance.Name, Namespace: i.infinispanInstance.Namespace})
	if resultErr != nil {
		return nil, resultErr
	}

	appProps[infinispanEnablePersistenceEnvKey] = "true"
	appProps[propertiesInfinispan[i.runtime][appPropInfinispanUseAuth]] = "true"
	if len(infinispanURI) > 0 {
		appProps[propertiesInfinispan[i.runtime][appPropInfinispanServerList]] = infinispanURI
	}
	return appProps, nil
}

func (i *infinispanConfigReconciler) createInfinispanConfigMap(appProps map[string]string) *v12.ConfigMap {
	var data map[string]string = nil
	if len(appProps) > 0 {
		data = appProps
	}
	configMap := &v12.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      i.getInfinispanConfigMapName(),
			Namespace: i.instance.GetNamespace(),
			Labels: map[string]string{
				framework.LabelAppKey: i.instance.GetName(),
			},
		},
		Data: data,
	}
	return configMap
}

func (i *infinispanConfigReconciler) getInfinispanConfigMapName() string {
	return fmt.Sprintf(infinispanConfigMapName, i.runtime)
}
