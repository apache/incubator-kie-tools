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
)

const (
	serviceKind     = "services"
	podKind         = "pods"
	deploymentKind  = "deployments"
	statefulSetKind = "statefulsets"
	ingressKind     = "ingresses"
)

type k8sServiceCatalog struct {
	Client client.Client
}

func newK8SServiceCatalog(cli client.Client) k8sServiceCatalog {
	return k8sServiceCatalog{
		Client: cli,
	}
}

func (c k8sServiceCatalog) Query(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	switch uri.GVK.Kind {
	case serviceKind:
		return c.resolveServiceQuery(ctx, uri, outputFormat)
	case podKind:
		return c.resolvePodQuery(ctx, uri, outputFormat)
	case deploymentKind:
		return c.resolveDeploymentQuery(ctx, uri, outputFormat)
	case statefulSetKind:
		return c.resolveStatefulSetQuery(ctx, uri, outputFormat)
	case ingressKind:
		return c.resolveIngressQuery(ctx, uri)
	default:
		return "", fmt.Errorf("resolution of kubernetes kind: %s is not implemented", uri.GVK.Kind)
	}
}

func (c k8sServiceCatalog) resolveServiceQuery(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	if service, err := findService(ctx, c.Client, uri.Namespace, uri.Name); err != nil {
		return "", err
	} else if serviceUri, err := resolveServiceUri(service, uri.GetPort(), outputFormat); err != nil {
		return "", err
	} else {
		return serviceUri, nil
	}
}

func (c k8sServiceCatalog) resolvePodQuery(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	if pod, serviceList, err := findPodAndReferenceServices(ctx, c.Client, uri.Namespace, uri.Name); err != nil {
		return "", err
	} else {
		if serviceList != nil && len(serviceList.Items) > 0 {
			referenceService := selectBestSuitedServiceByCustomLabels(serviceList, uri.GetCustomLabels())
			return resolveServiceUri(referenceService, uri.GetPort(), outputFormat)
		} else {
			return resolvePodUri(pod, "", uri.GetPort(), outputFormat)
		}
	}
}

func (c k8sServiceCatalog) resolveDeploymentQuery(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	if deployment, err := findDeployment(ctx, c.Client, uri.Namespace, uri.Name); err != nil {
		return "", err
	} else {
		if serviceList, err := findServicesBySelectorTarget(ctx, c.Client, uri.Namespace, deployment.Spec.Selector.MatchLabels); err != nil {
			return "", err
		} else if len(serviceList.Items) == 0 {
			return "", fmt.Errorf("no service was found for the deployment: %s in namespace: %s", uri.Name, uri.Namespace)
		} else {
			referenceService := selectBestSuitedServiceByCustomLabels(serviceList, uri.GetCustomLabels())
			return resolveServiceUri(referenceService, uri.GetPort(), outputFormat)
		}
	}
}

func (c k8sServiceCatalog) resolveStatefulSetQuery(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	if statefulSet, err := findStatefulSet(ctx, c.Client, uri.Namespace, uri.Name); err != nil {
		return "", err
	} else {
		if serviceList, err := findServicesBySelectorTarget(ctx, c.Client, uri.Namespace, statefulSet.Spec.Selector.MatchLabels); err != nil {
			return "", err
		} else if len(serviceList.Items) == 0 {
			return "", fmt.Errorf("no service was found for the statefulset: %s in namespace: %s", uri.Name, uri.Namespace)
		} else {
			referenceService := selectBestSuitedServiceByCustomLabels(serviceList, uri.GetCustomLabels())
			return resolveServiceUri(referenceService, uri.GetPort(), outputFormat)
		}
	}
}

func (c k8sServiceCatalog) resolveIngressQuery(ctx context.Context, uri ResourceUri) (string, error) {
	if ingress, err := findIngress(ctx, c.Client, uri.Namespace, uri.Name); err != nil {
		return "", err
	} else {
		// for now stick with the first ip or hostname.
		loadBalancer := ingress.Status.LoadBalancer.Ingress[0]
		var scheme = httpProtocol
		var host string
		var port = defaultHttpPort
		if len(loadBalancer.Hostname) > 0 {
			host = loadBalancer.Hostname
		} else {
			host = loadBalancer.IP
		}
		// An Ingress does not expose arbitrary ports or protocols other than HTTP and HTTPS
		if len(ingress.Spec.TLS) >= 1 {
			scheme = httpsProtocol
			port = defaultHttpsPort
		}
		return buildURI(scheme, host, port), nil
	}
}
