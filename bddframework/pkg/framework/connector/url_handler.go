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

package connector

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	appsv1 "k8s.io/api/apps/v1"
	"net/url"
)

const (
	// Data index HTTP URL env
	dataIndexHTTPRouteEnv = "KOGITO_DATAINDEX_HTTP_URL"
	// Data index WS URL env
	dataIndexWSRouteEnv = "KOGITO_DATAINDEX_WS_URL"
	// Job service HTTP URL env
	jobsServicesHTTPRouteEnv = "KOGITO_JOBS_SERVICE_URL"
	// Trusty HTTP URL env
	trustyHTTPRouteEnv = "KOGITO_TRUSTY_ENDPOINT"
	// Trusty WS URL env
	trustyWSRouteEnv = "KOGITO_TRUSTY_WS_URL"
)
const (
	webSocketScheme       = "ws"
	webSocketSecureScheme = "wss"
	httpScheme            = "http"
)

// URLHandler ...
type URLHandler interface {
	InjectDataIndexURLIntoKogitoRuntimeServices(namespace string) error
	InjectDataIndexURLIntoDeployment(namespace string, deployment *appsv1.Deployment) error
	InjectDataIndexURLIntoSupportingService(namespace string, serviceTypes ...api.ServiceType) error
	InjectJobsServicesURLIntoKogitoRuntimeServices(namespace string) error
	InjectJobsServiceURLIntoKogitoRuntimeDeployment(namespace string, deployment *appsv1.Deployment) error
	InjectTrustyURLIntoKogitoRuntimeServices(namespace string) error
	InjectTrustyURLIntoDeployment(namespace string, deployment *appsv1.Deployment) error
}

type urlHandler struct {
	operator.Context
	runtimeHandler           manager.KogitoRuntimeHandler
	supportingServiceHandler manager.KogitoSupportingServiceHandler
}

// NewURLHandler ...
func NewURLHandler(context operator.Context, runtimeHandler manager.KogitoRuntimeHandler, supportingServiceHandler manager.KogitoSupportingServiceHandler) URLHandler {
	return &urlHandler{
		Context:                  context,
		runtimeHandler:           runtimeHandler,
		supportingServiceHandler: supportingServiceHandler,
	}
}

// InjectDataIndexURLIntoKogitoRuntimeServices will query for every KogitoRuntime in the given namespace to inject the Data Index route to each one
// Won't trigger an update if the KogitoRuntime already has the route set to avoid unnecessary reconciliation triggers
func (u *urlHandler) InjectDataIndexURLIntoKogitoRuntimeServices(namespace string) error {
	u.Log.Debug("Injecting Data-Index Route in kogito Runtime")
	return u.injectSupportingServiceURLIntoKogitoRuntime(namespace, dataIndexHTTPRouteEnv, dataIndexWSRouteEnv, api.DataIndex)
}

// InjectDataIndexURLIntoDeployment will inject data-index route URL in to kogito runtime deployment env var
func (u *urlHandler) InjectDataIndexURLIntoDeployment(namespace string, deployment *appsv1.Deployment) error {
	u.Log.Debug("Injecting Data-Index URL in kogito Runtime deployment")
	return u.injectSupportingServiceURLIntoDeployment(namespace, dataIndexHTTPRouteEnv, dataIndexWSRouteEnv, deployment, api.DataIndex)
}

// InjectDataIndexURLIntoSupportingService will query for Supporting service deployment in the given namespace to inject the Data Index route to each one
// Won't trigger an update if the SupportingService already has the route set to avoid unnecessary reconciliation triggers
func (u *urlHandler) InjectDataIndexURLIntoSupportingService(namespace string, serviceTypes ...api.ServiceType) error {
	for _, serviceType := range serviceTypes {
		u.Log.Debug("Injecting Data-Index Route", "service", serviceType)
		supportingServiceManager := manager.NewKogitoSupportingServiceManager(u.Context, u.supportingServiceHandler)
		deployment, err := supportingServiceManager.FetchKogitoSupportingServiceDeployment(namespace, serviceType)
		if err != nil {
			return err
		}
		if deployment == nil {
			u.Log.Debug("No deployment found for service, skipping to inject DataIndex URL", "service", serviceType)
			return nil
		}

		u.Log.Debug("Querying DataIndex route to inject into service", "service", serviceType)
		serviceEndpoints, err := u.getSupportingServiceEndpoints(namespace, dataIndexHTTPRouteEnv, dataIndexWSRouteEnv, api.DataIndex)
		if err != nil {
			return err
		}
		if serviceEndpoints != nil {
			u.Log.Debug("", "DataIndex route", serviceEndpoints.HTTPRouteURI)

			updateHTTP, updateWS := u.updateServiceEndpointIntoDeploymentEnv(deployment, serviceEndpoints)
			// update only once
			if updateWS || updateHTTP {
				if err := kubernetes.ResourceC(u.Client).Update(deployment); err != nil {
					return err
				}
			}
		}
	}
	return nil
}

// InjectJobsServicesURLIntoKogitoRuntimeServices will query for every KogitoRuntime in the given namespace to inject the Jobs Services route to each one
// Won't trigger an update if the KogitoRuntime already has the route set to avoid unnecessary reconciliation triggers
func (u *urlHandler) InjectJobsServicesURLIntoKogitoRuntimeServices(namespace string) error {
	u.Log.Debug("Injecting Jobs Service Route in kogito Runtime instances")
	return u.injectSupportingServiceURLIntoKogitoRuntime(namespace, jobsServicesHTTPRouteEnv, "", api.JobsService)
}

// InjectJobsServiceURLIntoKogitoRuntimeDeployment will inject jobs-service route URL in to kogito runtime deployment env var
func (u *urlHandler) InjectJobsServiceURLIntoKogitoRuntimeDeployment(namespace string, deployment *appsv1.Deployment) error {
	u.Log.Debug("Injecting Jobs Service URL in kogito Runtime deployment")
	return u.injectSupportingServiceURLIntoDeployment(namespace, jobsServicesHTTPRouteEnv, "", deployment, api.JobsService)
}

// InjectTrustyURLIntoKogitoRuntimeServices will query for every KogitoRuntime in the given namespace to inject the Trusty route to each one
// Won't trigger an update if the KogitoRuntime already has the route set to avoid unnecessary reconciliation triggers
func (u *urlHandler) InjectTrustyURLIntoKogitoRuntimeServices(namespace string) error {
	u.Log.Debug("Injecting Trusty AI URL Route in kogito runtime")
	return u.injectSupportingServiceURLIntoKogitoRuntime(namespace, trustyHTTPRouteEnv, trustyWSRouteEnv, api.TrustyAI)
}

// InjectTrustyURLIntoDeployment will inject Trusty route URL in to kogito runtime deployment env var
func (u *urlHandler) InjectTrustyURLIntoDeployment(namespace string, deployment *appsv1.Deployment) error {
	u.Log.Debug("Injecting Trusty AI URL in kogito Runtime deployment")
	return u.injectSupportingServiceURLIntoDeployment(namespace, trustyHTTPRouteEnv, trustyWSRouteEnv, deployment, api.TrustyAI)
}

// injectSupportingServiceURLIntoKogitoRuntime will query for every KogitoApp in the given namespace to inject the Supporting service route to each one
// Won't trigger an update if the KogitoApp already has the route set to avoid unnecessary reconciliation triggers
// it will call when supporting service reconcile
func (u *urlHandler) injectSupportingServiceURLIntoKogitoRuntime(namespace string, serviceHTTPRouteEnv string, serviceWSRouteEnv string, resourceType api.ServiceType) error {
	serviceEndpoints, err := u.getSupportingServiceEndpoints(namespace, serviceHTTPRouteEnv, serviceWSRouteEnv, resourceType)
	if err != nil {
		return err
	}
	if serviceEndpoints != nil {

		u.Log.Debug("", "resourceType", resourceType, "route", serviceEndpoints.HTTPRouteURI)

		u.Log.Debug("Querying KogitoRuntime instances to inject a route ", "namespace", namespace)
		runtimeManager := manager.NewKogitoRuntimeManager(u.Context, u.runtimeHandler)
		deployments, err := runtimeManager.FetchKogitoRuntimeDeployments(namespace)
		if err != nil {
			return err
		}
		u.Log.Debug("", "Found KogitoRuntime instances", len(deployments), "namespace", namespace)
		if len(deployments) == 0 {
			u.Log.Debug("No deployment found for KogitoRuntime, skipping to inject request resource type URL into KogitoRuntime", "request resource type", resourceType)
			return nil
		}
		u.Log.Debug("Querying resource route to inject into KogitoRuntimes", "resource", resourceType)

		for _, dep := range deployments {
			updateHTTP, updateWS := u.updateServiceEndpointIntoDeploymentEnv(&dep, serviceEndpoints)
			// update only once
			if updateWS || updateHTTP {
				if err := kubernetes.ResourceC(u.Client).Update(&dep); err != nil {
					return err
				}
			}
		}
	}
	u.Log.Debug("Service Endpoint is nil")
	return nil
}

// InjectDataIndexURLIntoDeployment will inject Supporting service route URL in to kogito runtime deployment env var
// It will call when Kogito runtime reconcile
func (u *urlHandler) injectSupportingServiceURLIntoDeployment(namespace string, serviceHTTPRouteEnv string, serviceWSRouteEnv string, deployment *appsv1.Deployment, resourceType api.ServiceType) error {
	u.Log.Debug("Querying supporting service route to inject into Kogito runtime", "supporting service", resourceType)
	dataIndexEndpoints, err := u.getSupportingServiceEndpoints(namespace, serviceHTTPRouteEnv, serviceWSRouteEnv, resourceType)
	if err != nil {
		return err
	}
	if dataIndexEndpoints != nil {
		u.Log.Debug("", "resourceType", resourceType, "route", dataIndexEndpoints.HTTPRouteURI)
		u.updateServiceEndpointIntoDeploymentEnv(deployment, dataIndexEndpoints)
	}
	return nil
}

func (u *urlHandler) getSupportingServiceEndpoints(namespace string, serviceHTTPRouteEnv string, serviceWSRouteEnv string, resourceType api.ServiceType) (endpoints *ServiceEndpoints, err error) {
	route := ""
	supportingServiceManager := manager.NewKogitoSupportingServiceManager(u.Context, u.supportingServiceHandler)
	route, err = supportingServiceManager.FetchKogitoSupportingServiceRoute(namespace, resourceType)
	if err != nil {
		return
	}
	if len(route) > 0 {
		endpoints = &ServiceEndpoints{
			HTTPRouteEnv: serviceHTTPRouteEnv,
			WSRouteEnv:   serviceWSRouteEnv,
		}
		var routeURL *url.URL
		routeURL, err = url.Parse(route)
		if err != nil {
			u.Log.Error(err, "Failed to parse route url, set to empty", "route url", route)
			return
		}
		endpoints.HTTPRouteURI = routeURL.String()
		if httpScheme == routeURL.Scheme {
			endpoints.WSRouteURI = fmt.Sprintf("%s://%s", webSocketScheme, routeURL.Host)
		} else {
			endpoints.WSRouteURI = fmt.Sprintf("%s://%s", webSocketSecureScheme, routeURL.Host)
		}
		return
	}
	return nil, nil
}

func (u *urlHandler) updateServiceEndpointIntoDeploymentEnv(deployment *appsv1.Deployment, serviceEndpoints *ServiceEndpoints) (updateHTTP bool, updateWS bool) {
	// here we compare the current value to avoid updating the app every time
	if len(deployment.Spec.Template.Spec.Containers) > 0 && serviceEndpoints != nil {
		if len(serviceEndpoints.HTTPRouteEnv) > 0 {
			updateHTTP = framework.GetEnvVarFromContainer(serviceEndpoints.HTTPRouteEnv, &deployment.Spec.Template.Spec.Containers[0]) != serviceEndpoints.HTTPRouteURI
		}
		if len(serviceEndpoints.WSRouteEnv) > 0 {
			updateWS = framework.GetEnvVarFromContainer(serviceEndpoints.WSRouteEnv, &deployment.Spec.Template.Spec.Containers[0]) != serviceEndpoints.WSRouteURI
		}
		if updateHTTP {
			u.Log.Debug("Updating dc to inject route", "dc", deployment.GetName(), "route", serviceEndpoints.HTTPRouteURI)
			framework.SetEnvVar(serviceEndpoints.HTTPRouteEnv, serviceEndpoints.HTTPRouteURI, &deployment.Spec.Template.Spec.Containers[0])
		}
		if updateWS {
			u.Log.Debug("Updating dc to inject route ", "dc", deployment.GetName(), "route", serviceEndpoints.WSRouteURI)
			framework.SetEnvVar(serviceEndpoints.WSRouteEnv, serviceEndpoints.WSRouteURI, &deployment.Spec.Template.Spec.Containers[0])
		}
	}
	return
}
