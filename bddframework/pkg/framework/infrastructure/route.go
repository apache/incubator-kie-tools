// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package infrastructure

import (
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	api "github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/client/openshift"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	routev1 "github.com/openshift/api/route/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/apimachinery/pkg/util/intstr"
	"reflect"
)

// RouteHandler ...
type RouteHandler interface {
	FetchRoute(key types.NamespacedName) (*routev1.Route, error)
	GetHostFromRoute(routeKey types.NamespacedName) (string, error)
	CreateRoute(instance api.KogitoService) *routev1.Route
	GetComparator() compare.MapComparator
}

type routeHandler struct {
	operator.Context
}

// NewRouteHandler ...
func NewRouteHandler(context operator.Context) RouteHandler {
	return &routeHandler{
		context,
	}
}

func (r *routeHandler) FetchRoute(key types.NamespacedName) (*routev1.Route, error) {
	route := &routev1.Route{}
	exists, err := kubernetes.ResourceC(r.Client).FetchWithKey(key, route)
	if err != nil {
		return nil, err
	} else if !exists {
		return nil, nil
	}
	return route, nil
}

func (r *routeHandler) GetHostFromRoute(routeKey types.NamespacedName) (string, error) {
	route, err := r.FetchRoute(routeKey)
	if err != nil || route == nil {
		return "", err
	}
	return route.Spec.Host, nil
}

// createRequiredRoute creates a new Route resource based on the given Service
func (r *routeHandler) CreateRoute(instance api.KogitoService) *routev1.Route {
	route := &routev1.Route{
		ObjectMeta: v1.ObjectMeta{
			Name:      instance.GetName(),
			Namespace: instance.GetNamespace(),
			Labels:    map[string]string{framework.LabelAppKey: instance.GetName()},
		},
		Spec: routev1.RouteSpec{
			Port: &routev1.RoutePort{
				TargetPort: intstr.FromString(framework.DefaultPortName),
			},
			To: routev1.RouteTargetReference{
				Kind: openshift.KindService.Name,
				Name: instance.GetName(),
			},
		},
	}
	route.ResourceVersion = ""
	return route
}

func (r *routeHandler) GetComparator() compare.MapComparator {
	resourceComparator := compare.DefaultComparator()
	resourceComparator.SetComparator(
		framework.NewComparatorBuilder().
			WithType(reflect.TypeOf(routev1.Route{})).
			WithCustomComparator(framework.CreateRouteComparator()).
			Build())
	return compare.MapComparator{Comparator: resourceComparator}
}
