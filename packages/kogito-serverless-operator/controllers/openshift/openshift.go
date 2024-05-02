/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package openshift

import (
	appsv1 "github.com/openshift/client-go/apps/clientset/versioned/typed/apps/v1"
	buildv1 "github.com/openshift/client-go/build/clientset/versioned/typed/build/v1"
	routev1 "github.com/openshift/client-go/route/clientset/versioned/typed/route/v1"
	"k8s.io/client-go/rest"
)

var routeClient routev1.RouteV1Interface
var appsClient appsv1.AppsV1Interface

func GetRouteClient(cfg *rest.Config) (routev1.RouteV1Interface, error) {
	if routeClient == nil {
		if osRouteClient, err := NewOpenShiftRouteClient(cfg); err != nil {
			return nil, err
		} else {
			routeClient = osRouteClient
		}
	}
	return routeClient, nil
}

func GetAppsClient(cfg *rest.Config) (appsv1.AppsV1Interface, error) {
	if appsClient == nil {
		if osAppsClient, err := NewOpenShiftAppsClientClient(cfg); err != nil {
			return nil, err
		} else {
			appsClient = osAppsClient
		}
	}
	return appsClient, nil
}

func NewOpenShiftRouteClient(cfg *rest.Config) (*routev1.RouteV1Client, error) {
	return routev1.NewForConfig(cfg)
}

func NewOpenShiftAppsClientClient(cfg *rest.Config) (*appsv1.AppsV1Client, error) {
	return appsv1.NewForConfig(cfg)
}

func NewOpenShiftBuildClient(cfg *rest.Config) (*buildv1.BuildV1Client, error) {
	return buildv1.NewForConfig(cfg)
}
