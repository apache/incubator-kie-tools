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

package kogitosupportingservice

import (
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/kogitoservice"
	"github.com/kiegroup/kogito-operator/core/operator"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

// EndPointConfigMapReconciler ...
type EndPointConfigMapReconciler interface {
	Reconcile() error
}

type endPointConfigMapReconciler struct {
	operator.Context
	instance                 api.KogitoService
	serviceHTTPRouteEnv      string
	serviceWSRouteEnv        string
	configMapHandler         infrastructure.ConfigMapHandler
	deltaProcessor           infrastructure.DeltaProcessor
	kogitoServiceHandler     kogitoservice.ServiceHandler
	endPointConfigMapHandler infrastructure.EndPointConfigMapHandler
}

func newEndPointConfigMapReconciler(context operator.Context, instance api.KogitoService, serviceHTTPRouteEnv string, serviceWSRouteEnv string) EndPointConfigMapReconciler {
	return &endPointConfigMapReconciler{
		Context:                  context,
		instance:                 instance,
		serviceHTTPRouteEnv:      serviceHTTPRouteEnv,
		serviceWSRouteEnv:        serviceWSRouteEnv,
		configMapHandler:         infrastructure.NewConfigMapHandler(context),
		deltaProcessor:           infrastructure.NewDeltaProcessor(context),
		kogitoServiceHandler:     kogitoservice.NewKogitoServiceHandler(context),
		endPointConfigMapHandler: infrastructure.NewEndPointConfigMapHandler(context),
	}
}

func (i *endPointConfigMapReconciler) Reconcile() error {

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

	return nil
}

func (i *endPointConfigMapReconciler) createRequiredResources() (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	configMap, err := i.createEndPointConfigMap()
	if err != nil {
		return nil, err
	}
	if err := framework.SetOwner(i.instance, i.Scheme, configMap); err != nil {
		return nil, err
	}
	resources[reflect.TypeOf(v1.ConfigMap{})] = []client.Object{configMap}
	return resources, nil
}

func (i *endPointConfigMapReconciler) getDeployedResources() (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	configMap, err := i.configMapHandler.FetchConfigMap(types.NamespacedName{Name: i.endPointConfigMapHandler.GetEndPointConfigMapName(i.instance.GetName()), Namespace: i.instance.GetNamespace()})
	if err != nil {
		return nil, err
	}
	if configMap != nil {
		resources[reflect.TypeOf(v1.ConfigMap{})] = []client.Object{configMap}
	}
	return resources, nil
}

func (i *endPointConfigMapReconciler) processDelta(requestedResources map[reflect.Type][]client.Object, deployedResources map[reflect.Type][]client.Object) (err error) {
	comparator := i.configMapHandler.GetComparator()
	_, err = i.deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	return
}

func (i *endPointConfigMapReconciler) createEndPointConfigMap() (*v1.ConfigMap, error) {

	serviceEndpoints, err := i.kogitoServiceHandler.GetKogitoServiceEndpoints(i.instance, i.serviceHTTPRouteEnv, i.serviceWSRouteEnv)
	if err != nil {
		return nil, err
	}
	data := make(map[string]string)
	data[serviceEndpoints.HTTPRouteEnv] = serviceEndpoints.HTTPRouteURI

	if len(serviceEndpoints.WSRouteEnv) > 0 {
		data[serviceEndpoints.WSRouteEnv] = serviceEndpoints.WSRouteURI
	}

	configMapName := i.endPointConfigMapHandler.GetEndPointConfigMapName(i.instance.GetName())
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
	return configMap, nil
}
