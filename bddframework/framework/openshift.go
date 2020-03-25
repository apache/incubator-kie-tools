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

package framework

import (
	"fmt"
	"time"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	ocapps "github.com/openshift/api/apps/v1"
	buildv1 "github.com/openshift/api/build/v1"
	routev1 "github.com/openshift/api/route/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"

	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/openshift"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
)

// WaitForBuildComplete waits for a build to be completed
func WaitForBuildComplete(namespace, buildName string, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Build %s complete", buildName), timeoutInMin,
		func() (bool, error) {
			bc := buildv1.BuildConfig{
				ObjectMeta: metav1.ObjectMeta{
					Name:      buildName,
					Namespace: namespace,
				},
			}
			builds, err := openshift.BuildConfigC(kubeClient).GetBuildsStatus(&bc, fmt.Sprintf("%s=%s", "buildconfig", buildName))

			if err != nil {
				return false, fmt.Errorf("Error while fetching buildconfig %s: %v", buildName, err)
			} else if builds == nil || len(builds.Complete) < 1 {
				return false, nil
			}

			return true, nil
		})
}

// WaitForDeploymentConfigRunning waits for a deployment config to be running, with a specific number of pod
func WaitForDeploymentConfigRunning(namespace, dcName string, podNb int, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("DeploymentConfig %s running", dcName), timeoutInMin,
		func() (bool, error) {
			if dc, err := GetDeploymentConfig(namespace, dcName); err != nil {
				return false, err
			} else if dc == nil {
				return false, nil
			} else {
				GetLogger(namespace).Debugf("Deployment config has %d available replicas\n", dc.Status.AvailableReplicas)
				return dc.Status.AvailableReplicas == int32(podNb), nil
			}
		})
}

// GetDeploymentConfig retrieves a deployment config
func GetDeploymentConfig(namespace, dcName string) (*ocapps.DeploymentConfig, error) {
	dc := &ocapps.DeploymentConfig{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: dcName, Namespace: namespace}, dc); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for DeploymentConfig %s: %v ", dcName, err)
	} else if errors.IsNotFound(err) || !exists {
		return nil, nil
	}
	return dc, nil
}

// WaitForRoute waits for a route to be available
func WaitForRoute(namespace, routeName string, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Route %s available", routeName), timeoutInMin,
		func() (bool, error) {
			route, err := GetRoute(namespace, routeName)
			if err != nil || route == nil {
				return false, err
			}

			return true, nil
		})
}

// GetRoute retrieves a route
func GetRoute(namespace, routeName string) (*routev1.Route, error) {
	route := &routev1.Route{}
	if exists, err :=
		kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: routeName, Namespace: namespace}, route); err != nil {
		return nil, err
	} else if !exists {
		return nil, nil
	} else {
		return route, nil
	}
}

func createHTTPRoute(namespace, serviceName string) error {
	GetLogger(namespace).Infof("Creating HTTP route for service %s.", serviceName)

	route := &routev1.Route{
		ObjectMeta: metav1.ObjectMeta{
			Name:      serviceName,
			Namespace: namespace,
		},
		Spec: routev1.RouteSpec{
			To: routev1.RouteTargetReference{
				Kind: "Service",
				Name: serviceName,
			},
		},
	}
	if err := kubernetes.ResourceC(kubeClient).Create(route); err != nil {
		return err
	}
	return nil
}

// GetRouteURI retrieves a route URI
func GetRouteURI(namespace, routeName string) (string, error) {
	route, err := GetRoute(namespace, routeName)
	if err != nil || route == nil {
		return "", err
	}
	host := route.Spec.Host

	protocol := "http"
	port := "80"
	if route.Spec.TLS != nil {
		protocol = "https"
		port = "443"
	}

	uri := protocol + "://" + host + ":" + port
	return uri, nil
}

// WaitAndRetrieveRouteURI waits for a route and returns its URI
func WaitAndRetrieveRouteURI(namespace, serviceName string) (string, error) {
	if err := WaitForRoute(namespace, serviceName, 2); err != nil {
		return "", fmt.Errorf("Route %s does not exist in namespace %s: %v", serviceName, namespace, err)
	}
	routeURI, err := GetRouteURI(namespace, serviceName)
	if err != nil {
		return "", fmt.Errorf("Error retrieving URI for route %s in namespace %s: %v", serviceName, namespace, err)
	} else if len(routeURI) <= 0 {
		return "", fmt.Errorf("No URI found for route name %s in namespace %s: %v", serviceName, namespace, err)
	}
	GetLogger(namespace).Debugf("Got route %s\n", routeURI)
	return routeURI, nil
}

// WaitForOnOpenshift is a specific method
func WaitForOnOpenshift(namespace, display string, timeoutInMin int, condition func() (bool, error)) error {
	return WaitFor(namespace, display, GetOpenshiftDurationFromTimeInMin(timeoutInMin), condition)
}

// GetOpenshiftDurationFromTimeInMin will calculate the time depending on the configured cluster load factor
func GetOpenshiftDurationFromTimeInMin(timeoutInMin int) time.Duration {
	return time.Duration(timeoutInMin*config.GetLoadFactor()) * time.Minute
}
