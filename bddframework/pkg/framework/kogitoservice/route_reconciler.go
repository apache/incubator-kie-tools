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
	"reflect"

	api "github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	v1 "github.com/openshift/api/route/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

// RouteReconciler ...
type RouteReconciler interface {
	Reconcile() error
}

type routeReconciler struct {
	operator.Context
	instance       api.KogitoService
	routeHandler   infrastructure.RouteHandler
	deltaProcessor infrastructure.DeltaProcessor
}

func newRouteReconciler(context operator.Context, instance api.KogitoService) RouteReconciler {
	return &routeReconciler{
		Context:        context,
		instance:       instance,
		routeHandler:   infrastructure.NewRouteHandler(context),
		deltaProcessor: infrastructure.NewDeltaProcessor(context),
	}
}

func (i *routeReconciler) Reconcile() error {

	if !i.Client.IsOpenshift() {
		i.Log.Debug("Skipping route creation. Routes are only created in Openshift env.")
		return nil
	}

	if i.instance.GetSpec().IsRouteDisabled() {
		i.Log.Debug("Skipping route creation. Routes are not enabled.")
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
	return i.processDelta(requestedResources, deployedResources)
}

func (i *routeReconciler) createRequiredResources() (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	route := i.routeHandler.CreateRoute(i.instance)
	if err := framework.SetOwner(i.instance, i.Scheme, route); err != nil {
		return nil, err
	}
	resources[reflect.TypeOf(v1.Route{})] = []client.Object{route}
	return resources, nil
}

func (i *routeReconciler) getDeployedResources() (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	route, err := i.routeHandler.FetchRoute(types.NamespacedName{Name: i.instance.GetName(), Namespace: i.instance.GetNamespace()})
	if err != nil {
		return nil, err
	}
	if route != nil {
		resources[reflect.TypeOf(v1.Route{})] = []client.Object{route}
	}
	return resources, nil
}

func (i *routeReconciler) processDelta(requestedResources map[reflect.Type][]client.Object, deployedResources map[reflect.Type][]client.Object) (err error) {
	comparator := i.routeHandler.GetComparator()
	_, err = i.deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	if err != nil {
		return err
	}
	return
}
