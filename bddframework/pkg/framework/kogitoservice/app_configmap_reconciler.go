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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/framework/util"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
)

// AppConfigMapReconciler ...
type AppConfigMapReconciler interface {
	Reconcile() error
}

type appConfigMapReconciler struct {
	operator.Context
	instance            api.KogitoService
	configMapHandler    infrastructure.ConfigMapHandler
	deltaProcessor      infrastructure.DeltaProcessor
	infraManager        manager.KogitoInfraManager
	appConfigMapHandler AppConfigMapHandler
}

// NewAppConfigMapReconciler ...
func NewAppConfigMapReconciler(context operator.Context, instance api.KogitoService, infraHandler manager.KogitoInfraHandler) AppConfigMapReconciler {
	return &appConfigMapReconciler{
		Context:             context,
		instance:            instance,
		configMapHandler:    infrastructure.NewConfigMapHandler(context),
		deltaProcessor:      infrastructure.NewDeltaProcessor(context),
		infraManager:        manager.NewKogitoInfraManager(context, infraHandler),
		appConfigMapHandler: NewAppConfigMapHandler(context),
	}
}

func (c *appConfigMapReconciler) Reconcile() (err error) {

	// Create Required resource
	requestedResources, err := c.createRequiredResources()
	if err != nil {
		return
	}

	// Get Deployed resource
	deployedResources, err := c.getDeployedResources()
	if err != nil {
		return
	}

	// Process Delta
	return c.processDelta(requestedResources, deployedResources)
}

func (c *appConfigMapReconciler) createRequiredResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	appProps := map[string]string{}

	if len(c.instance.GetSpec().GetInfra()) > 0 {
		c.Log.Debug("Infra references are provided")
		infraAppProps, _, _, err := c.infraManager.FetchKogitoInfraProperties(c.instance.GetSpec().GetRuntime(), c.instance.GetNamespace(), c.instance.GetSpec().GetInfra()...)
		if err != nil {
			return resources, err
		}
		util.AppendToStringMap(infraAppProps, appProps)
	}

	if len(c.instance.GetSpec().GetConfig()) > 0 {
		c.Log.Debug("custom app properties are provided in spec")
		util.AppendToStringMap(c.instance.GetSpec().GetConfig(), appProps)
	}

	configMap := c.appConfigMapHandler.CreateAppConfigMap(c.instance, appProps)
	if err := framework.SetOwner(c.instance, c.Scheme, configMap); err != nil {
		return nil, err
	}
	resources[reflect.TypeOf(v1.ConfigMap{})] = []resource.KubernetesResource{configMap}

	return resources, nil
}

func (c *appConfigMapReconciler) getDeployedResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	configMap, err := c.configMapHandler.FetchConfigMap(types.NamespacedName{Name: c.appConfigMapHandler.GetAppConfigMapName(c.instance), Namespace: c.instance.GetNamespace()})
	if err != nil {
		return nil, err
	}
	if configMap != nil {
		resources[reflect.TypeOf(v1.ConfigMap{})] = []resource.KubernetesResource{configMap}
	}
	return resources, nil
}

func (c *appConfigMapReconciler) processDelta(requestedResources map[reflect.Type][]resource.KubernetesResource, deployedResources map[reflect.Type][]resource.KubernetesResource) (err error) {
	comparator := c.configMapHandler.GetComparator()
	_, err = c.deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	return
}
