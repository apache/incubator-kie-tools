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
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/kogitoservice"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	appsv1 "k8s.io/api/apps/v1"
	"k8s.io/apimachinery/pkg/types"
)

const (
	// JobsServicesHTTPRouteEnv Job service HTTP URL env
	JobsServicesHTTPRouteEnv = "KOGITO_JOBS_SERVICE_URL"
	// TrustyHTTPRouteEnv Trusty HTTP URL env
	TrustyHTTPRouteEnv = "KOGITO_TRUSTY_ENDPOINT"
	// TrustyWSRouteEnv Trusty WS URL env
	TrustyWSRouteEnv = "KOGITO_TRUSTY_WS_URL"
	// DataIndexHTTPRouteEnv Data index HTTP URL env
	DataIndexHTTPRouteEnv = "KOGITO_DATAINDEX_HTTP_URL"
	// DataIndexWSRouteEnv Data index WS URL env
	DataIndexWSRouteEnv = "KOGITO_DATAINDEX_WS_URL"
)

// URLHandler ...
type URLHandler interface {
	InjectDataIndexURLIntoSupportingService(key types.NamespacedName, serviceTypes ...api.ServiceType) error
	InjectDataIndexEndPointOnKogitoRuntimeServices(key types.NamespacedName) error
	InjectJobsServicesEndPointOnKogitoRuntimeServices(key types.NamespacedName) error
	InjectTrustyEndpointOnKogitoRuntimeServices(key types.NamespacedName) error
	InjectDataIndexEndpointOnDeployment(deployment *appsv1.Deployment) error
	InjectJobsServiceEndpointOnDeployment(deployment *appsv1.Deployment) error
	InjectTrustyEndpointOnDeployment(deployment *appsv1.Deployment) error
}

type urlHandler struct {
	operator.Context
	runtimeHandler           manager.KogitoRuntimeHandler
	supportingServiceHandler manager.KogitoSupportingServiceHandler
	supportingServiceManager manager.KogitoSupportingServiceManager
	kogitoServiceHandler     kogitoservice.ServiceHandler
	endPointConfigMapHandler infrastructure.EndPointConfigMapHandler
	configMapHandler         infrastructure.ConfigMapHandler
}

// NewURLHandler ...
func NewURLHandler(context operator.Context, runtimeHandler manager.KogitoRuntimeHandler, supportingServiceHandler manager.KogitoSupportingServiceHandler) URLHandler {
	return &urlHandler{
		Context:                  context,
		runtimeHandler:           runtimeHandler,
		supportingServiceHandler: supportingServiceHandler,
		supportingServiceManager: manager.NewKogitoSupportingServiceManager(context, supportingServiceHandler),
		kogitoServiceHandler:     kogitoservice.NewKogitoServiceHandler(context),
		endPointConfigMapHandler: infrastructure.NewEndPointConfigMapHandler(context),
		configMapHandler:         infrastructure.NewConfigMapHandler(context),
	}
}

// InjectDataIndexURLIntoSupportingService will query for Supporting service deployment in the given namespace to inject the Data Index route to each one
// Won't trigger an update if the SupportingService already has the route set to avoid unnecessary reconciliation triggers
func (u *urlHandler) InjectDataIndexURLIntoSupportingService(dataIndexKey types.NamespacedName, serviceTypes ...api.ServiceType) error {

	// Load data-index endpoints
	endPointConfigMap, err := u.endPointConfigMapHandler.FetchEndPointConfigMap(dataIndexKey)
	if err != nil {
		return err
	}
	if endPointConfigMap == nil {
		u.Log.Debug("Data-index EndPoint configmap not found.")
		return nil
	}
	u.Log.Debug("data-index endPointConfigMap", "data", endPointConfigMap.Data)

	for _, serviceType := range serviceTypes {

		// fetching deployment of other supporting services
		u.Log.Debug("Injecting Data-Index endpoint", "service", serviceType)
		deployment, err := u.supportingServiceManager.FetchKogitoSupportingServiceDeployment(dataIndexKey.Namespace, serviceType)
		if err != nil {
			return err
		}
		if deployment == nil {
			u.Log.Debug("No deployment found for service, skipping to inject DataIndex URL", "service", serviceType)
			return nil
		}

		// Mount data-index endpoint on others supporting service deployment
		u.configMapHandler.MountAsEnvFrom(deployment, endPointConfigMap.Name)
		if err := kubernetes.ResourceC(u.Client).Update(deployment); err != nil {
			return err
		}
	}
	return nil
}

// InjectDataIndexEndPointOnKogitoRuntimeServices will query for every KogitoRuntime in the given namespace to inject the Data Index route to each one
// Won't trigger an update if the KogitoRuntime already has the route set to avoid unnecessary reconciliation triggers
func (u *urlHandler) InjectDataIndexEndPointOnKogitoRuntimeServices(key types.NamespacedName) error {
	u.Log.Debug("Injecting Data-Index Route in kogito Runtime")
	return u.injectSupportingServiceURLIntoKogitoRuntime(key)
}

// InjectJobsServicesEndPointOnKogitoRuntimeServices will query for every KogitoRuntime in the given namespace to inject the Jobs Services route to each one
// Won't trigger an update if the KogitoRuntime already has the route set to avoid unnecessary reconciliation triggers
func (u *urlHandler) InjectJobsServicesEndPointOnKogitoRuntimeServices(key types.NamespacedName) error {
	u.Log.Debug("Injecting Jobs Service Route in kogito Runtime instances")
	return u.injectSupportingServiceURLIntoKogitoRuntime(key)
}

// InjectTrustyEndpointOnKogitoRuntimeServices will query for every KogitoRuntime in the given namespace to inject the Trusty route to each one
// Won't trigger an update if the KogitoRuntime already has the route set to avoid unnecessary reconciliation triggers
func (u *urlHandler) InjectTrustyEndpointOnKogitoRuntimeServices(key types.NamespacedName) error {
	u.Log.Debug("Injecting Trusty AI URL Route in kogito runtime")
	return u.injectSupportingServiceURLIntoKogitoRuntime(key)
}

// InjectDataIndexEndpointOnDeployment will inject data-index route URL in to kogito runtime deployment env var
func (u *urlHandler) InjectDataIndexEndpointOnDeployment(deployment *appsv1.Deployment) error {
	u.Log.Debug("Injecting Data-Index URL in kogito Runtime deployment")
	return u.injectSupportingServiceURLIntoDeployment(api.DataIndex, deployment)
}

// InjectJobsServiceEndpointOnDeployment will inject jobs-service route URL in to kogito runtime deployment env var
func (u *urlHandler) InjectJobsServiceEndpointOnDeployment(deployment *appsv1.Deployment) error {
	u.Log.Debug("Injecting Jobs Service URL in kogito Runtime deployment")
	return u.injectSupportingServiceURLIntoDeployment(api.JobsService, deployment)
}

// InjectTrustyEndpointOnDeployment will inject Trusty route URL in to kogito runtime deployment env var
func (u *urlHandler) InjectTrustyEndpointOnDeployment(deployment *appsv1.Deployment) error {
	u.Log.Debug("Injecting Trusty AI URL in kogito Runtime deployment")
	return u.injectSupportingServiceURLIntoDeployment(api.TrustyAI, deployment)
}

// injectSupportingServiceURLIntoKogitoRuntime will query for every KogitoApp in the given namespace to inject the Supporting service route to each one
// Won't trigger an update if the KogitoApp already has the route set to avoid unnecessary reconciliation triggers
// it will call when supporting service reconcile
func (u *urlHandler) injectSupportingServiceURLIntoKogitoRuntime(key types.NamespacedName) error {
	u.Log.Debug("Querying KogitoRuntime instances to inject a service endpoint")
	runtimeManager := manager.NewKogitoRuntimeManager(u.Context, u.runtimeHandler)
	deployments, err := runtimeManager.FetchKogitoRuntimeDeployments(key.Namespace)
	if err != nil {
		return err
	}
	u.Log.Debug("", "Found KogitoRuntime instances", len(deployments), "namespace", key.Namespace)
	if len(deployments) == 0 {
		u.Log.Debug("No deployment found for KogitoRuntime, skipping to inject request resource type URL into KogitoRuntime")
		return nil
	}

	endPointConfigMap, err := u.endPointConfigMapHandler.FetchEndPointConfigMap(key)
	if err != nil {
		return err
	}
	if endPointConfigMap == nil {
		u.Log.Debug("EndPoint configmap not found.", "service", key.Name)
		return nil
	}
	u.Log.Debug("endPointConfigMap", "data", endPointConfigMap.Data)

	for _, dep := range deployments {
		u.configMapHandler.MountAsEnvFrom(&dep, endPointConfigMap.Name)
		if err := kubernetes.ResourceC(u.Client).Update(&dep); err != nil {
			return err
		}
	}
	return nil
}

// injectSupportingServiceURLIntoDeployment will inject Supporting service route URL in to kogito runtime deployment env var
// It will call when Kogito runtime reconcile
func (u *urlHandler) injectSupportingServiceURLIntoDeployment(resourceType api.ServiceType, deployment *appsv1.Deployment) error {

	// load supporting service custom resource instance
	supportingServiceInstance, err := u.supportingServiceManager.FetchKogitoSupportingServiceForServiceType(deployment.Namespace, resourceType)
	if err != nil {
		return err
	}
	if supportingServiceInstance == nil {
		u.Log.Debug("Supporting service not found.", "resourceType", resourceType)
		return nil
	}

	// load endpoint configmap for supporting service
	endPointConfigMap, err := u.endPointConfigMapHandler.FetchEndPointConfigMap(types.NamespacedName{Name: supportingServiceInstance.GetName(), Namespace: supportingServiceInstance.GetNamespace()})
	if err != nil {
		return err
	}
	if endPointConfigMap == nil {
		u.Log.Debug("EndPoint configmap not found.", "service", supportingServiceInstance.GetName())
		return nil
	}
	u.Log.Debug("endPointConfigMap", "data", endPointConfigMap.Data)

	// mount endpoint configmap over runtime deployment
	u.configMapHandler.MountAsEnvFrom(deployment, endPointConfigMap.Name)

	return nil
}
