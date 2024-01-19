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

package discovery

import (
	"context"
	"fmt"

	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/openshift"
	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
	"github.com/apache/incubator-kie-kogito-serverless-operator/utils"
	appsv1 "github.com/openshift/client-go/apps/clientset/versioned/typed/apps/v1"
	routev1 "github.com/openshift/client-go/route/clientset/versioned/typed/route/v1"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/rest"
	"k8s.io/klog/v2"
)

const (
	openShiftRoutes            = "routes"
	openShiftDeploymentConfigs = "deploymentconfigs"
)

type openShiftServiceCatalog struct {
	dc *OpenShiftDiscoveryClient
}

type OpenShiftDiscoveryClient struct {
	Client      client.Client
	RouteClient routev1.RouteV1Interface
	AppsClient  appsv1.AppsV1Interface
}

func newOpenShiftServiceCatalog(discoveryClient *OpenShiftDiscoveryClient) openShiftServiceCatalog {
	return openShiftServiceCatalog{
		dc: discoveryClient,
	}
}
func newOpenShiftServiceCatalogForClientAndConfig(cli client.Client, cfg *rest.Config) openShiftServiceCatalog {
	return openShiftServiceCatalog{
		dc: newOpenShiftDiscoveryClientForClientAndConfig(cli, cfg),
	}
}

func newOpenShiftDiscoveryClientForClientAndConfig(cli client.Client, cfg *rest.Config) *OpenShiftDiscoveryClient {
	var routeClient routev1.RouteV1Interface
	var appsClient appsv1.AppsV1Interface
	var err error
	if utils.IsOpenShift() {
		if routeClient, err = openshift.GetRouteClient(cfg); err != nil {
			klog.V(log.E).ErrorS(err, "Unable to get the openshift route client")
			return nil
		}
		if appsClient, err = openshift.GetAppsClient(cfg); err != nil {
			klog.V(log.E).ErrorS(err, "Unable to get the openshift apps client")
			return nil
		}
		return newOpenShiftDiscoveryClient(cli, routeClient, appsClient)
	}
	return nil
}

func newOpenShiftDiscoveryClient(cli client.Client, routeClient routev1.RouteV1Interface, appsClient appsv1.AppsV1Interface) *OpenShiftDiscoveryClient {
	return &OpenShiftDiscoveryClient{
		Client:      cli,
		RouteClient: routeClient,
		AppsClient:  appsClient,
	}
}

func (c openShiftServiceCatalog) Query(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	if c.dc == nil {
		return "", fmt.Errorf("OpenShiftDiscoveryClient was not provided, maybe current operator is not running in OpenShift")
	}
	switch uri.GVK.Kind {
	case openShiftRoutes:
		return c.resolveOpenShiftRouteQuery(ctx, uri)
	case openShiftDeploymentConfigs:
		return c.resolveOpenShiftDeploymentConfigQuery(ctx, uri, outputFormat)
	default:
		return "", fmt.Errorf("resolution of openshift kind: %s is not implemented", uri.GVK.Kind)
	}
}

func (c openShiftServiceCatalog) resolveOpenShiftRouteQuery(ctx context.Context, uri ResourceUri) (string, error) {
	if route, err := c.dc.RouteClient.Routes(uri.Namespace).Get(ctx, uri.Name, metav1.GetOptions{}); err != nil {
		return "", err
	} else {
		scheme := httpProtocol
		port := defaultHttpPort
		if route.Spec.TLS != nil {
			scheme = httpsProtocol
			port = defaultHttpsPort
		}
		return buildURI(scheme, route.Spec.Host, port), nil
	}
}

func (c openShiftServiceCatalog) resolveOpenShiftDeploymentConfigQuery(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	if deploymentConfig, err := c.dc.AppsClient.DeploymentConfigs(uri.Namespace).Get(ctx, uri.Name, metav1.GetOptions{}); err != nil {
		return "", err
	} else {
		if serviceList, err := findServicesBySelectorTarget(ctx, c.dc.Client, uri.Namespace, deploymentConfig.Spec.Selector); err != nil {
			return "", err
		} else if len(serviceList.Items) == 0 {
			return "", fmt.Errorf("no service was found for the deploymentConfig: %s in namespace: %s", uri.Name, uri.Namespace)
		} else {
			referenceService := selectBestSuitedServiceByCustomLabels(serviceList, uri.GetCustomLabels())
			return resolveServiceUri(referenceService, uri.GetPort(), outputFormat)
		}
	}
}
